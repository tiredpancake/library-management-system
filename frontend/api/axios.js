import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080/api",
});

api.interceptors.request.use(
  (config) => {
    const basicAuth = sessionStorage.getItem("basicAuth");

    if (basicAuth) {
      config.headers.Authorization = `Basic ${basicAuth}`;
    }

    return config;
  },
  (error) => Promise.reject(error),
);

export default api;
