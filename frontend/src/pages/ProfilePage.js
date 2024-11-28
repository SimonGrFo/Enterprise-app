import React, { useState } from 'react';
import api from '../api';

const ProfilePage = () => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    currentPassword: '',
    newPassword: '',
  });

  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');
  const [deletionError, setDeletionError] = useState('');
  const [deletionSuccess, setDeletionSuccess] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;

    // Perform validation for the current field
    const errorMessage = validateField(name, value);

    // If there's an error, display it and don't update the form data
    if (errorMessage) {
      setError(errorMessage);
      return;
    }

    // Update the form data if valid and clear any existing error messages
    setFormData({ ...formData, [name]: value });
    setError('');
  };

  const validateField = (name, value) => {
  if (name === 'username' && (value.length < 3 || value.length > 20)) {
    return 'Username must be between 3 and 20 characters.';
  }
  if (name === 'email' && !/\S+@\S+\.\S+/.test(value)) {
    return 'Please enter a valid email address.';
  }
  if (name === 'currentPassword' && value.trim() === '') {
    return 'Current password is required.';
  }
  if (name === 'newPassword' && value.length < 6) {
    return 'New password must be at least 6 characters.';
  }
  return ''; // No error
  };


  const updateField = async (field, data) => {
    setSuccess('');
    setError('');
    try {
      // Combine the data into the expected DTO structure
      const payload = { ...data, [field]: data[field] };
      const response = await api.put('/api/users/update', payload);
      setSuccess(`${field.charAt(0).toUpperCase() + field.slice(1)} updated successfully`);
    } catch (err) {
      setError(err.response?.data || `Failed to update ${field}`);
    }
  };



  const handleDeleteAccount = async () => {
    if (!window.confirm('Are you sure? This will delete your account and everything that goes with it.')) {
      return;
    }

    try {
      const payload = { password: formData.currentPassword };
      await api.delete('/api/users/delete', { data: payload });
      setDeletionSuccess('Account deleted successfully. Redirecting to login page...');
      setTimeout(() => {
        window.location.href = '/login';
      }, 2000);
    } catch (err) {
      setDeletionError(err.response?.data || 'Failed to delete account');
    }
  };



  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white shadow-lg rounded-lg p-8 max-w-md w-full">
        <h2 className="text-2xl font-bold text-center mb-6">Manage Your Profile</h2>

        {/* Success/Error messages */}
        {success && (
          <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
            {success}
          </div>
        )}
        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}
        {deletionSuccess && (
          <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
            {deletionSuccess}
          </div>
        )}
        {deletionError && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {deletionError}
          </div>
        )}

        {/* Change Username */}
        <div className="mb-6">
          <h3 className="text-lg font-semibold mb-2">Change Username</h3>
          <input
            type="text"
            name="username"
            placeholder="New username"
            value={formData.username}
            onChange={handleChange}
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-2"
          />
          <button
            onClick={() => updateField('username', { username: formData.username })}
            className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded w-full"
          >
            Update Username
          </button>
        </div>

        {/* Change Email Address */}
        <div className="mb-6">
          <h3 className="text-lg font-semibold mb-2">Change Email Address</h3>
          <input
            type="email"
            name="email"
            placeholder="New email"
            value={formData.email}
            onChange={handleChange}
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-2"
          />
          <button
            onClick={() => updateField('email', { email: formData.email })}
            className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded w-full"
          >
            Update Email
          </button>
        </div>

        {/* Change Password */}
        <div className="mb-6">
          <h3 className="text-lg font-semibold mb-2">Change Password</h3>
          <input
            type="password"
            name="currentPassword"
            placeholder="Current password"
            value={formData.currentPassword}
            onChange={handleChange}
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-2"
          />
          <input
            type="password"
            name="newPassword"
            placeholder="New password"
            value={formData.newPassword}
            onChange={handleChange}
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-2"
          />
          <button
            onClick={() =>
              updateField('password', {
                currentPassword: formData.currentPassword,
                newPassword: formData.newPassword,
              })
            }
            className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded w-full"
          >
            Update Password
          </button>
        </div>

        {/* Delete Account */}
        <div className="mt-6">
          <h3 className="text-lg font-semibold mb-2">Manage Your Data</h3>
          <p className="text-sm text-gray-700 mb-4">
            This will delete your account, and everything that goes with it.
          </p>
          <button
            onClick={handleDeleteAccount}
            className="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded w-full"
          >
            Delete Account
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
