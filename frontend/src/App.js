import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import PrivateRoute from './components/PrivateRoute';
import AuthPage from './pages/AuthPage';
import HomePage from './pages/HomePage';
import AccountSettings from './components/AccountSettings'; // Import AccountSettings
import Header from './components/Header';

function App() {
    return (
        <AuthProvider>
            <Router>
                <div className="min-h-screen flex flex-col bg-gray-50">
                    <Header />
                    <main className="flex-grow container mx-auto p-4 pt-16">
                        <Routes>
                            <Route path="/login" element={<AuthPage />} />
                            <Route
                                path="/home"
                                element={
                                    <PrivateRoute>
                                        <HomePage />
                                    </PrivateRoute>
                                }
                            />
                            <Route
                                path="/account-settings"
                                element={
                                    <PrivateRoute>
                                        <AccountSettings />
                                    </PrivateRoute>
                                }
                            />
                            <Route path="/" element={<Navigate to="/home" replace />} />
                        </Routes>
                    </main>
                </div>
            </Router>
        </AuthProvider>
    );
}

export default App;
