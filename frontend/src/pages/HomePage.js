import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import axios from 'axios';

const HomePage = () => {
  const { user } = useAuth();
  const [track, setTrack] = useState('');
  const [results, setResults] = useState([]);

  const searchTracks = async () => {
    try {
      const response = await axios.get(`/api/music/search`, {
        params: { track },
      });
      setResults(response.data.results.trackmatches.track || []);
    } catch (error) {
      console.error('Error searching tracks:', error);
    }
  };

  if (!user) {
    return <div>Loading...</div>;
  }

  return (
    <div className="max-w-2xl mx-auto bg-white rounded-lg shadow-lg p-6">
      <h2 className="text-2xl font-bold mb-6">Welcome, {user.username}!</h2>

      <div className="mb-6">
        <h3 className="text-xl font-semibold mb-4">Search for Tracks</h3>
        <div className="flex items-center space-x-2 mb-4">
          <input
            type="text"
            value={track}
            onChange={(e) => setTrack(e.target.value)}
            placeholder="Enter track name"
            className="flex-grow p-2 border rounded"
          />
          <button
            onClick={searchTracks}
            className="px-4 py-2 bg-blue-600 text-white rounded"
          >
            Search
          </button>
        </div>
        {results.length > 0 && (
          <ul className="space-y-2">
            {results.map((track, index) => (
              <li key={index} className="p-2 bg-gray-100 rounded">
                <strong>{track.name}</strong> by {track.artist}
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
};

export default HomePage;
