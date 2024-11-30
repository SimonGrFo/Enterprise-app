import React from 'react';
import { useAuth } from '../context/AuthContext';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const ProfilePage = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleDelete = async () => {
    const confirmDelete = window.confirm(
      'Are you sure you want to delete your account? This action cannot be undone.'
    );
    if (!confirmDelete) return;

    try {
      const token = localStorage.getItem('token');
      const response = await axios.delete('http://localhost:8080/api/auth/delete', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      alert(response.data);
      logout(); // Log out the user after deletion
      navigate('/signup'); // Redirect to the signup page
    } catch (error) {
      console.error(error.response?.data || 'Error deleting account');
      alert(error.response?.data || 'An error occurred');
    }
  };

  if (!user) {
    return <div>Loading...</div>;
  }

  return (
    <div className="max-w-2xl mx-auto bg-white rounded-lg shadow-lg p-6">
      <h2 className="text-2xl font-bold mb-6">Profile</h2>
      <div className="space-y-4">
        <div>
          <label className="block text-gray-700 font-bold mb-2">Username:</label>
          <p className="text-gray-600">{user.username}</p>
        </div>
        <div>
          <button
            onClick={handleDelete}
            className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition-colors duration-200 font-medium shadow-sm hover:shadow-md"
            style={{ marginTop: '20px' }}
          >
            Delete Account
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;