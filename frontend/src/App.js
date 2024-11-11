import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import UserList from './components/UserList';
// Import other components as needed

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/users" element={<UserList />} />
        {/* Define other routes as needed */}
      </Routes>
    </Router>
  );
}

export default App;
