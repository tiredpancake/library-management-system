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

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Landing />} />

        <Route path="/login" element={<Login />} />

        <Route
          path="/reports"
          element={
            <MainLayout>
              <Reports />
            </MainLayout>
          }
        />

        <Route
          path="/dashboard"
          element={
            <MainLayout>
              <Dashboard />
            </MainLayout>
          }
        />

        <Route
          path="/books"
          element={
            <MainLayout>
              <Books />
            </MainLayout>
          }
        />

        <Route
          path="/members"
          element={
            <MainLayout>
              <Members />
            </MainLayout>
          }
        />

        <Route
          path="/loans"
          element={
            <MainLayout>
              <Loans />
            </MainLayout>
          }
        />

        <Route
          path="/fines"
          element={
            <MainLayout>
              <Fines />
            </MainLayout>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
