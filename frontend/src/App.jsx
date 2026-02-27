import React from "react";
import "./App.css";
import { BrowserRouter, Route, Routes, useLocation } from "react-router-dom";
import LandingPage from "./components/LandingPage";
import AboutPage from "./components/AboutPage";
import NavBar from "./components/NavBar";
import Footer from "./components/Footer";
import RegisterPage from "./components/RegisterPage";
import { Toaster } from "react-hot-toast";
import LoginPage from "./components/LoginPage";
import DashboardLayout from "./components/dashboard/DashboardLayout";
import ShortUrlPage from "./components/ShortUrlPage";
import PrivateRoute from "./PrivateRoute";
import ErrorPage from "./components/ErrorPage";

// Child component inside BrowserRouter to use useLocation
function AppContent() {
  const location = useLocation();

  const mainRoutes = [
    "/",
    "/about",
    "/register",
    "/login",
    "/dashboard",
    "/error",
  ];

  const hideLayout = !mainRoutes.includes(location.pathname);

  return (
    <>
      {!hideLayout && <NavBar />}
      <Toaster position="bottom-center" />
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/about" element={<AboutPage />} />
        <Route
          path="/register"
          element={
            <PrivateRoute publicPage={true}>
              <RegisterPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/login"
          element={
            <PrivateRoute publicPage={true}>
              <LoginPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/dashboard"
          element={
            <PrivateRoute publicPage={false}>
              <DashboardLayout />
            </PrivateRoute>
          }
        />
        <Route path="/:url" element={<ShortUrlPage />} />
        <Route
          path="/error"
          element={<ErrorPage message="Page not found!" />}
        />
        <Route path="*" element={<ErrorPage message="Page not found!" />} />
      </Routes>
      {!hideLayout && <Footer />}
    </>
  );
}

function App() {
  return (
    <BrowserRouter>
      <AppContent />
    </BrowserRouter>
  );
}

export default App;
