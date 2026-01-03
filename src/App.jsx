import { useState } from 'react'

import Login  from './pages/LoginForm';
import Register from "./pages/RegisterForm";
import MainView from "./pages/mainView";  
import GroupView from "./pages/groupView"; 
import "./auth.css";
import "./mainView.css";
import "./groupView.css";

function App() {
  const [view, setView] = useState("login");

  return (
    <>
    {view === "login" && (
      <Login goToRegister={() => setView("register")} 
      onLogin={() => setView("main")} 
      />

    )}

    {view === "register" && (
      <Register goToLogin={() => setView("login")} />
    )}

    {view === "main" && (
        <MainView 
          onLogout={() => setView("login")}      // Przycisk Logout
          onSelectGroup={() => setView("group")} // Kliknięcie w kartę grupy
        />
      )}

      {view === "group" && (
        <GroupView 
          onBack={() => setView("main")}         // Przycisk "Back to groups"
          onLogout={() => setView("login")}      // Przycisk Logout w nagłówku
        />
      )}
    </>
  )
}

export default App
