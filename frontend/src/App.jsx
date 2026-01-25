import { useState } from 'react';
import Login from './pages/LoginForm';
import Register from "./pages/RegisterForm";
import MainView from "./pages/mainView";
import GroupView from "./pages/groupView";

function App() {
  const [view, setView] = useState(() => {
    const savedToken = localStorage.getItem("token");
    return savedToken ? "main" : "login";
  });
  const [selectedGroupId, setSelectedGroupId] = useState(null);

  const handleLogout = () => {
    localStorage.removeItem("token");
    window.location.reload();
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
          onSelectGroup={(id) => {
            setSelectedGroupId(id);
            setView("group");
          }}
        />
      )}

      {view === "group" && (
        <GroupView
          key="view-group"
          groupId={selectedGroupId}
          onBack={() => setView("main")}
        />
      )}
    </div>
  );
}

export default App;