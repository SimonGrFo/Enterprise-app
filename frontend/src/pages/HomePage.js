import React from 'react';
import { useAuth } from '../context/AuthContext';

const HomePage = () => {
  const { user } = useAuth();

  if (!user) {
    return <div>Loading...</div>;
  }

  return (
    <div className="max-w-2xl mx-auto bg-white rounded-lg shadow-lg p-6">
      <h2 className="text-2xl font-bold mb-6">Account Information</h2>
      <div className="space-y-4">
        <div>
          <label className="block text-gray-700 font-bold mb-2">Username:</label>
          <p className="text-gray-600">{user.username}</p>
        </div>
      </div>
    </div>
  );
};

export default HomePage;
