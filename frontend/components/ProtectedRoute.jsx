import { Navigate } from "react-router-dom";

function ProtectedRoute({ children }) {
  const basicAuth = sessionStorage.getItem("basicAuth");

  if (!basicAuth) {
    return <Navigate to="/" replace />;
  }

  return children;
}

export default ProtectedRoute;
