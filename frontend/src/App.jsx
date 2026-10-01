import { BrowserRouter, Routes, Route } from "react-router-dom";

import Landing from "../pages/Landing";
import Login from "../pages/Login";

import MainLayout from "../layout/MainLayout";

import Dashboard from "../pages/Dashboard";
import Books from "../pages/Books";
import Members from "../pages/Members";
import Loans from "../pages/Loans";
import Fines from "../pages/Fines";
import Reports from "../pages/Reports";

import ProtectedRoute from "../components/ProtectedRoute";

function PrivatePage({ children }) {
  return (
    <ProtectedRoute>
      <MainLayout>{children}</MainLayout>
    </ProtectedRoute>
  );
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Landing />} />

        <Route path="/login" element={<Login />} />

        <Route
          path="/dashboard"
          element={
            <PrivatePage>
              <Dashboard />
            </PrivatePage>
          }
        />

        <Route
          path="/books"
          element={
            <PrivatePage>
              <Books />
            </PrivatePage>
          }
        />

        <Route
          path="/members"
          element={
            <PrivatePage>
              <Members />
            </PrivatePage>
          }
        />

        <Route
          path="/loans"
          element={
            <PrivatePage>
              <Loans />
            </PrivatePage>
          }
        />

        <Route
          path="/fines"
          element={
            <PrivatePage>
              <Fines />
            </PrivatePage>
          }
        />

        <Route
          path="/reports"
          element={
            <PrivatePage>
              <Reports />
            </PrivatePage>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
