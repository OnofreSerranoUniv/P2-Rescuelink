const BASE_URL = 'http://localhost:8081/api/auth'; // URL correcta del backend

function registerUser(username, password, callback) {
  sendRequest("POST", `${BASE_URL}/register`, { username, password }, callback);
}

function loginUser(username, password, callback) {
  sendRequest("POST", `${BASE_URL}/login`, { username, password }, callback);
}

function sendRequest(method, url, data = null, callback) {
  const xhr = new XMLHttpRequest();
  xhr.open(method, url, true);
  xhr.setRequestHeader("Content-Type", "application/json");

  xhr.onreadystatechange = function () {
    if (xhr.readyState === 4) {
      callback(xhr.status, xhr.responseText);
    }
  };

  if (data) {
    xhr.send(JSON.stringify(data));
  } else {
    xhr.send();
  }
}

// front/src/api.js

export async function registerVolunteer({ fullName, phoneNumber, location, skills }) {
  const token = localStorage.getItem('jwt'); // o como guardes el JWT
  const resp = await fetch('/api/volunteers/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ fullName, phoneNumber, location, skills })
  });
  if (!resp.ok) throw new Error('Error al registrar voluntario');
  return resp.json();
}

