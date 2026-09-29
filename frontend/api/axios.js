import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
});

api.interceptors.request.use((config) => {
  const username = localStorage.getItem("username");

  const password = localStorage.getItem("password");

  if (username && password) {
    const token = btoa(`${username}:${password}`);

    config.headers.Authorization = `Basic ${token}`;
  }

  return config;
});

export default api;
