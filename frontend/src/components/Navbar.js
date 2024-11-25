import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="bg-white shadow-md border-b border-gray-200 fixed top-0 left-0 w-full z-50">
      <div className="container mx-auto px-6 py-6"> {/* Increased padding-y to 6 */}
        <div className="flex justify-between items-center">
          {/* Logo */}
          <div className="flex items-center space-x-2">
            <Link
              to="/"
              className="text-2xl font-bold text-blue-600 hover:text-blue-700 transition-colors"
            >
              My App
            </Link>
          </div>

          {/* Links or User Actions */}
          <div className="flex items-center space-x-6">
            {user ? (
              <div className="flex items-center space-x-4">
                <span className="text-gray-700 font-medium">
                  Welcome, <span className="text-blue-600">{user.username}</span>
                </span>
                <button
                  onClick={handleLogout}
                  className="bg-red-500 text-white px-5 py-2 rounded-lg hover:bg-red-600 transition-colors duration-200 font-medium shadow-sm hover:shadow-md"
                >
                  Logout
                </button>
              </div>
            ) : (
              <div className="flex items-center space-x-4">
                <Link
                  to="/login"
                  className="text-gray-600 hover:text-blue-600 transition-colors font-medium"
                >
                  Login
                </Link>
                <Link
                  to="/register"
                  className="bg-blue-500 text-white px-5 py-2 rounded-lg hover:bg-blue-600 transition-colors duration-200 font-medium shadow-sm hover:shadow-md"
                >
                  Register
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
