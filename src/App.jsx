import { useEffect, useState } from 'react';
import Login from './pages/LoginForm';
import Register from "./pages/RegisterForm";
import MainView from "./pages/mainView";  
import GroupView from "./pages/groupView"; 

function App() {
  const [view, setView] = useState("login");
  useEffect(() => {
    window.focus();
  }, [view]);

  const handleLogout = () => {
    setView("login");
  };

  return (
    <div className="app-container">
      {view === "login" && (
        <Login 
          key="view-login"
          goToRegister={() => setView("register")} 
          onLogin={() => setView("main")} 
        />
      )}

      {view === "register" && (
        <Register 
          key="view-register"
          goToLogin={() => setView("login")} 
        />
      )}

      {view === "main" && (
        <MainView 
          key="view-main"
          onLogout={handleLogout}
          onSelectGroup={() => setView("group")}
        />
      )}

      {view === "group" && (
        <GroupView 
          key="view-group"
          onBack={() => setView("main")}
          onLogout={handleLogout}
        />
      )}
    </div>
  );
}

export default App;