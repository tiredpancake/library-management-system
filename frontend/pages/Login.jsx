import { useState } from "react";
import { useNavigate } from "react-router-dom";

import api from "../api/axios";
import { getErrorMessage } from "../src/utils/errorHandler";

function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");

    try {
      sessionStorage.removeItem("basicAuth");
      await api.post("/auth/login", { username, password });

      const credentials = window.btoa(`${username}:${password}`);
      sessionStorage.setItem("basicAuth", credentials);

      navigate("/dashboard");
    } catch (err) {
      sessionStorage.removeItem("basicAuth");
      setError(getErrorMessage(err));
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-100">
      <div className="bg-white rounded-xl shadow-lg p-8 w-full max-w-md">
        <h1 className="text-3xl font-bold text-center mb-6">
          Library Management
        </h1>

        <form onSubmit={handleLogin} className="space-y-5">
          <div>
            <label>
              Username <span className="text-red-500">*</span>
            </label>
            <input
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>

          <div>
            <label>
              Password <span className="text-red-500">*</span>
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full border rounded-lg px-3 py-2"
            />
            {error && <p className="text-red-500 text-sm mt-2">{error}</p>}
          </div>

          <button className="w-full bg-blue-600 text-white py-3 rounded-lg">
            Login
          </button>
        </form>
      </div>
    </div>
  );
}

export default Login;
