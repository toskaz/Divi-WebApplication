import "../styles/auth.css";
import logo from "../assets/logo.png";
import { useState } from "react";

export default function Login({ goToRegister, onLogin }) {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);

    async function handleSubmit(e) {
        e.preventDefault();

        try {
            const response = await fetch("http://localhost:8080/api/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    email: email,
                    password: password,
                }),
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem("token", data.token);
                onLogin();
            } else {
                // const errorText = await response.json();
                // alert("Wrong email or password");
            }
        } catch /* (error) */ {
            // console.error("Network error:", error);
            // alert("Could not connect to the server.");
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
                            <div className="password-input-wrapper" style={{ position: 'relative' }}>
                                <input
                                    type={showPassword ? "text" : "password"}
                                    placeholder="********"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                    style={{ width: '100%', paddingRight: '40px' }}
                                />
                                <button
                                    type="button"
                                    className="password-toggle-btn"
                                    onClick={() => setShowPassword(!showPassword)}
                                    style={{
                                        position: 'absolute',
                                        right: '10px',
                                        top: '50%',
                                        transform: 'translateY(-50%)',
                                        background: 'none',
                                        border: 'none',
                                        cursor: 'pointer'
                                    }}
                                >
                                    {showPassword ? '🫣' : '👁️'}
                                </button>
                            </div>
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