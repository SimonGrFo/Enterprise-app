// src/components/UserList.js
import React, { useEffect, useState } from 'react';
import UserService from '../services/UserService';

function UserList() {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    UserService.getAllUsers()
      .then(response => setUsers(response.data))
      .catch(error => console.error("Error fetching users:", error));
  }, []);

  return (
    <div>
      <h2>User List</h2>
      <ul>
        {users.map(user => (
          <li key={user.username}>
            {user.username} - {user.email} - {user.active ? 'Active' : 'Inactive'}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default UserList;
