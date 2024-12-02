import React, { useState } from "react";
import axios from "axios";

const TrackSearch = () => {
  const [track, setTrack] = useState("");
  const [results, setResults] = useState([]);

  const searchTracks = async () => {
    try {
      console.log("Sending request to backend...");
      const response = await axios.get('/api/music/search', {
        params: { track: 'believer' },
      });
      console.log('Track search results:', response.data);
    } catch (error) {
      console.error('Error searching tracks:', error);
      alert("Error communicating with the backend.");
    }
  };


  return (
    <div>
      <h1>Track Search</h1>
      <input
        type="text"
        value={track}
        onChange={(e) => setTrack(e.target.value)}
        placeholder="Enter track name"
      />
      <button onClick={searchTracks}>Search</button>
      <ul>
        {results.map((track, index) => (
          <li key={index}>
            {track.name} by {track.artist}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default TrackSearch;
