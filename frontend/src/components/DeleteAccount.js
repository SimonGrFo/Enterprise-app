import axios from 'axios';

const deleteAccount = async () => {
    const token = localStorage.getItem('token');

    try {
        const response = await axios.delete('http://localhost:8080/api/auth/delete', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        alert(response.data);
    } catch (error) {
        console.error(error.response.data);
        alert(error.response.data);
    }
};
