import axios from 'axios';

export const deleteAccount = async (token) => {
  return axios.delete('http://localhost:8080/api/auth/delete', {
    headers: { Authorization: `Bearer ${token}` },
  });
};
