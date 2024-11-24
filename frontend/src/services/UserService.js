// src/services/UserService.js
import axios from 'axios';

const API_URL = "/api/users";

export const UserService = {
  getAllUsers: () => axios.get(API_URL),
  getUserByUsername: (username) => axios.get(`${API_URL}/${username}`),
  updateUser: (username, updateData) => axios.put(`${API_URL}/${username}`, updateData),
  deleteUser: (username) => axios.delete(`${API_URL}/${username}`),
  toggleUserStatus: (username) => axios.put(`${API_URL}/${username}/toggle-status`),
  changePassword: (username, passwordData) => axios.post(`${API_URL}/${username}/change-password`, passwordData),
};