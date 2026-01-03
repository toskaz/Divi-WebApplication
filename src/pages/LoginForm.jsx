import "../styles/login.css";
import logo from "../assets/logo.png";
import { useState } from "react";

export default function Login({ goToRegister, onLogin }) {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const DEMO_EMAIL = "test@gmail.com";
    const DEMO_PASS = "test";

    function handleSubmit(e) {
        e.preventDefault();

        if (email === DEMO_EMAIL && password === DEMO_PASS) {
            alert("Login success");
            onLogin();
        } else {
            alert("Wrong email or password");
        }
    }

    return (
        <div className="auth login">
            <div className="page">
                <div className="card">
                    <div className="logo">
                        <img src={logo} alt="Divi logo" />
                    </div>

                    <h1 className="title">DIVI</h1>
                    <p className="subtitle">Welcome to your Expense Sharing Hub!</p>

                    <div className="tabs">
                        <button type="button" className="tab active">
                            Login
                        </button>

                        <button type="button" className="tab" onClick={goToRegister}>
                            Register
                        </button> 
                    </div>

                    <form className="form" onSubmit={handleSubmit}>
                        <div className="field">
                            <label>Email address</label>
                            <input 
                                type="email"
                                placeholder="test@divi.com"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                        </div>

                        <div className="field">
                            <label>Password</label>
                            <input
                                type="password"
                                placeholder="********"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>

                        <button className="primary" type="submit" >
                            Sign in
                        </button>
                    </form>
                </div>
            </div>
        </div>
    )
}