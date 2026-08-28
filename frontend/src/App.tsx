import './App.css'
import LoginPage from './pages/LoginPage/LoginPage';
import CreateAccountPage from './pages/CreateAccountPage/CreateAccountPage';
import HomePage from './pages/HomePage/HomePage';
import { Routes, Route } from "react-router-dom";

function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<CreateAccountPage />} />
      <Route path="/" element={<HomePage />} />
    </Routes>
  );
}

export default App