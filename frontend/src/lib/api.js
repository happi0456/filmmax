const BASE = '/api';

const STATUS_MESSAGES = {
  400: 'Please check the form fields.',
  401: 'Your session has expired. Log out and log in again.',
  403: "You don't have permission for this action.",
  404: 'Not found.',
  409: 'That username or email already exists.',
};

export async function api(path, { method = 'GET', body, token } = {}) {
  const headers = {};
  if (body) headers['Content-Type'] = 'application/json';
  if (token) headers.Authorization = `Bearer ${token}`;

  const res = await fetch(BASE + path, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  if (res.status === 204) return null;

  if (!res.ok) {
    let serverMessage = '';
    try {
      const data = await res.json();
      serverMessage = data.message || '';
    } catch {
      // Body was empty or not JSON
    }
    throw new Error(serverMessage || STATUS_MESSAGES[res.status] || 'Something went wrong.');
  }

  return res.json();
}