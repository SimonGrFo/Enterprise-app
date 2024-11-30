import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { deleteAccount } from '../services/authService';
import { useNavigate } from 'react-router-dom';

const ProfilePage = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const [modalVisible, setModalVisible] = useState(false);
  const [usernameInput, setUsernameInput] = useState('');
  const [message, setMessage] = useState({ type: '', text: '' });

  const handleDelete = async () => {
    if (usernameInput !== user.username) {
      setMessage({ type: 'error', text: 'Username does not match.' });
      return;
    }

    try {
      const token = localStorage.getItem('token');
      await deleteAccount(token);

      setMessage({ type: 'success', text: 'Account deleted successfully.' });
      setTimeout(() => {
        logout();
        navigate('/signup');
      }, 2000);
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data || 'An error occurred while deleting the account.',
      });
    } finally {
      setModalVisible(false);
      setUsernameInput('');
    }
  };

  const resetModal = () => {
    setModalVisible(false);
    setUsernameInput('');
    setMessage({ type: '', text: '' });
  };

  return (
    <div className="max-w-2xl mx-auto bg-white rounded-lg shadow-lg p-6 space-y-6">
      <h2 className="text-2xl font-bold mb-6">Profile</h2>

      {/* Change Username Section */}
      <div className="space-y-4">
        <h3 className="text-lg font-semibold">Change Username</h3>
        <p className="text-gray-700">Current username: <strong>{user.username}</strong></p>
        <div className="flex space-x-2">
          <input
            type="text"
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 focus:outline-none"
            placeholder="Enter new username"
            disabled
          />
          <button
            className="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition-colors"
            disabled
          >
            Confirm
          </button>
        </div>
      </div>

      {/* Change Email Address Section */}
      <div className="space-y-4">
        <h3 className="text-lg font-semibold">Change Email Address</h3>
        <p className="text-gray-700">Current email: <strong>{user.email || 'Not provided'}</strong></p>
        <div className="flex space-x-2">
          <input
            type="text"
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 focus:outline-none"
            placeholder="Enter new email"
            disabled
          />
          <button
            className="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition-colors"
            disabled
          >
            Confirm
          </button>
        </div>
      </div>

      {/* Change Password Section */}
      <div className="space-y-4">
        <h3 className="text-lg font-semibold">Change Password</h3>
        <div className="space-y-2">
          <input
            type="password"
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 focus:outline-none"
            placeholder="Current password"
            disabled
          />
          <input
            type="password"
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 focus:outline-none"
            placeholder="New password"
            disabled
          />
        </div>
        <button
          className="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition-colors"
          disabled
        >
          Confirm
        </button>
      </div>

      {/* Manage Your Data Section */}
      <div className="space-y-4">
        <h3 className="text-lg font-semibold">Manage Your Data</h3>
        <button
          onClick={() => setModalVisible(true)}
          className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition-colors"
        >
          Delete Account
        </button>
        <p className="text-gray-600 text-sm">
          <strong>Note:</strong> This will delete your account and everything associated with it.
        </p>
      </div>

      {message.text && (
        <div
          className={`mt-4 px-4 py-3 rounded ${
            message.type === 'success' ? 'bg-green-100 border-green-400 text-green-700' : 'bg-red-100 border-red-400 text-red-700'
          }`}
        >
          {message.text}
        </div>
      )}

      {/* Delete Account Modal */}
      {modalVisible && (
        <div className="fixed inset-0 bg-gray-900 bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-lg p-6 w-96">
            <h3 className="text-xl font-bold mb-4 text-center">Confirm Account Deletion</h3>
            <p className="text-gray-700 mb-4 text-center">
              Please type your username (<strong>{user.username}</strong>) to confirm.
            </p>
            <input
              type="text"
              value={usernameInput}
              onChange={(e) => {
                setUsernameInput(e.target.value);
                setMessage({ type: '', text: '' });
              }}
              className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 focus:outline-none mb-4"
              placeholder="Enter your username"
            />
            <div className="flex justify-end space-x-4">
              <button
                onClick={resetModal}
                className="bg-gray-300 text-gray-800 px-4 py-2 rounded-lg hover:bg-gray-400 transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={handleDelete}
                className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition-colors"
              >
                Confirm
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProfilePage;
