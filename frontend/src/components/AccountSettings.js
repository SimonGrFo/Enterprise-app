import React from 'react';
import api from '../api';

const AccountSettings = () => {
    const handleDelete = async () => {
        const confirmDelete = window.confirm(
            'Are you sure you want to delete your account? This action cannot be undone.'
        );
        if (!confirmDelete) return;

        try {
            const response = await api.delete('/auth/delete');
            alert(response.data);
            localStorage.removeItem('token');
            window.location.href = '/signup';
        } catch (error) {
            console.error(error.response?.data || 'Error deleting account');
            alert(error.response?.data || 'An error occurred');
        }
    };

    return (
        <div className="account-settings">
            <h2>Account Settings</h2>
            <button onClick={handleDelete} style={{ color: 'red', marginTop: '20px' }}>
                Delete Account
            </button>
        </div>
    );
};

export default AccountSettings;
