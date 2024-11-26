import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Header = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="bg-white shadow-md border-b border-gray-200 fixed top-0 left-0 w-full z-50">
      <div className="container mx-auto px-6 py-4 flex justify-between items-center">
        {/* Branding */}
        <div className="text-2xl font-bold text-blue-600">
        </div>

        {/* User Actions */}
        <div>
          {user ? (
            <div className="flex items-center space-x-4">
              <span className="text-gray-700 font-medium">
                <span className="text-blue-600">{user.username}</span>
              </span>
              <button
                onClick={handleLogout}
                className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition-colors duration-200 font-medium shadow-sm hover:shadow-md"
              >
                Logout
              </button>
            </div>
          ) : null}
        </div>
      </div>
    </header>
  );
};

export default Header;
