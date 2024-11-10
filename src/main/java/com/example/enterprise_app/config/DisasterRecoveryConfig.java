package com.example.enterprise_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableScheduling
public class DisasterRecoveryConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${backup.retention.days:7}")
    private int backupRetentionDays;

    @Value("${backup.directory:/var/backups/database}")
    private String backupDirectory;

    @Value("${backup.secondary.host:secondary-db-host}")
    private String secondaryHost;

    @Component
    public class DatabaseBackupService {
        private static final Logger log = LoggerFactory.getLogger(DatabaseBackupService.class);
        private static final String BACKUP_SCRIPT = "pg_dump";
        private static final long BACKUP_TIMEOUT_MINUTES = 30;
        private final SecureRandom secureRandom = new SecureRandom();

        @Scheduled(cron = "${backup.schedule:0 0 0 * * *}")
        public void performBackup() {
            String tempBackupFile = null;
            try {
                createBackupDirectoryIfNeeded();
                validateBackupPrerequisites();

                tempBackupFile = createTempBackupFile();
                String finalBackupFile = createFinalBackupPath();

                executeBackup(tempBackupFile);
                moveBackupToFinalLocation(tempBackupFile, finalBackupFile);
                rotateBackups();

                log.info("Database backup completed successfully: {}", finalBackupFile);
            } catch (Exception e) {
                log.error("Backup failed", e);
                alertAdministrators(e);
            } finally {
                cleanupTempFile(tempBackupFile);
            }
        }

        private void createBackupDirectoryIfNeeded() throws IOException {
            Path backupPath = Paths.get(backupDirectory);
            if (!Files.exists(backupPath)) {
                Files.createDirectories(backupPath);
                Files.setPosixFilePermissions(backupPath,
                        java.nio.file.attribute.PosixFilePermissions.fromString("rwx------"));
            }
        }

        private void validateBackupPrerequisites() {
            if (!new File(BACKUP_SCRIPT).exists() &&
                    !new File("/usr/bin/" + BACKUP_SCRIPT).exists() &&
                    !new File("/usr/local/bin/" + BACKUP_SCRIPT).exists()) {
                throw new IllegalStateException("pg_dump not found in system path");
            }

            if (!StringUtils.hasText(dbUrl) || !StringUtils.hasText(dbUsername) || !StringUtils.hasText(dbPassword)) {
                throw new IllegalStateException("Database credentials not properly configured");
            }
        }

        private String createTempBackupFile() {
            return backupDirectory + "/temp_" + System.currentTimeMillis() +
                    "_" + secureRandom.nextInt(1000000) + ".sql";
        }

        private String createFinalBackupPath() {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            return backupDirectory + "/backup_" + timestamp + ".sql";
        }

        private void executeBackup(String backupFile) throws IOException, InterruptedException {
            ProcessBuilder pb = new ProcessBuilder(
                    BACKUP_SCRIPT,
                    "-h", "localhost",
                    "-U", dbUsername,
                    "-F", "c",
                    "-b",
                    "-v",
                    "-f", backupFile,
                    getDatabaseName(dbUrl)
            );

            pb.environment().put("PGPASSWORD", dbPassword);
            pb.redirectErrorStream(true);

            Process p = pb.start();
            boolean completed = p.waitFor(BACKUP_TIMEOUT_MINUTES, TimeUnit.MINUTES);

            if (!completed) {
                p.destroyForcibly();
                throw new RuntimeException("Backup process timed out after " + BACKUP_TIMEOUT_MINUTES + " minutes");
            }

            if (p.exitValue() != 0) {
                throw new RuntimeException("Backup process failed with exit code: " + p.exitValue());
            }
        }

        private void moveBackupToFinalLocation(String tempFile, String finalFile) throws IOException {
            Path source = Paths.get(tempFile);
            Path target = Paths.get(finalFile);
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
            Files.setPosixFilePermissions(target,
                    java.nio.file.attribute.PosixFilePermissions.fromString("rw-------"));
        }

        private void rotateBackups() throws IOException {
            File[] backups = new File(backupDirectory)
                    .listFiles((dir, name) -> name.startsWith("backup_"));

            if (backups != null && backups.length > backupRetentionDays) {
                Arrays.sort(backups, Comparator.comparingLong(File::lastModified));
                for (int i = 0; i < backups.length - backupRetentionDays; i++) {
                    secureDelete(backups[i]);
                }
            }
        }

        private void secureDelete(File file) throws IOException {
            // Overwrite file with random data before deletion
            if (file.exists()) {
                try (var os = Files.newOutputStream(file.toPath())) {
                    byte[] random = new byte[4096];
                    secureRandom.nextBytes(random);
                    os.write(random);
                }
            }
            Files.delete(file.toPath());
        }

        private String getDatabaseName(String url) {
            return url.substring(url.lastIndexOf("/") + 1);
        }

        private void alertAdministrators(Exception e) {
            // TODO: Implement alerting mechanism (email, SMS, monitoring system, etc.)
            log.error("Critical backup failure. Administrator notification required.", e);
        }

        private void cleanupTempFile(String tempFile) {
            if (tempFile != null) {
                try {
                    secureDelete(new File(tempFile));
                } catch (IOException e) {
                    log.error("Failed to cleanup temporary backup file", e);
                }
            }
        }
    }

    @Bean
    public DataSource secondaryDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(dbUrl.replace("localhost", secondaryHost));
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }
}