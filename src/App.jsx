import { useState } from 'react'

import Login  from './pages/LoginForm';
import Register from "./pages/RegisterForm";
import MainView from "./pages/mainView";  
import GroupView from "./pages/groupView"; 

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
          onLogout={() => setView("login")}
          onSelectGroup={() => setView("group")}
        />
      )}

      {view === "group" && (
        <GroupView 
          onBack={() => setView("main")}
          onLogout={() => setView("login")}
        />
      )}
    </>
  )
}

export default App
