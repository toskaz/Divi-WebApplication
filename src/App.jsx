import { useState } from 'react'

import Login  from './pages/LoginForm';
import Register from "./pages/RegisterForm";
import "./auth.css";

function App() {
  const [view, setView] = useState("login");

  return (
    <>
    {view === "login" && (
      <Login goToRegister={() => setView("register")} />
    )}

    {view === "register" && (
      <Register goToLogin={() => setView("login")} />
    )}
    </>
  )
}

export default App
