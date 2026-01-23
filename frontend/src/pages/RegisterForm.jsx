import "../styles/auth.css";
import logo from "../assets/logo.png";
import {useState } from "react";

export default function Register({ goToLogin }) {
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirm, setConfirm] = useState("");

    async function handleSubmit(e) {
        e.preventDefault();

        try {
            const response = await fetch("http://localhost:8080/api/auth/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    name: name,
                    email: email,
                    password: password,
                }),
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem("token", data.token);
                goToLogin();
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
        <div className="auth register">
            <div className="page">
                <div className="card">
                    <div className="logo">
                        <img src={logo} alt="Divi logo" />
                    </div>

                    <h1 className="title">DIVI</h1>
                    <p className="subtitle">Welcome to your Expense Sharing Hub!</p>

                    <div className="tabs">
                        <button type="button" className="tab" onClick={goToLogin}>
                            Login
                        </button>

                        <button type="button" className="tab active">
                            Register
                        </button>
                    </div>

                    <form className="form" onSubmit={handleSubmit}>
                        <div className="field">
                            <label>Name</label>
                            <input
                                placeholder="Your name"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                required

                            />
                        </div>

                        <div className="field">
                            <label>Email address</label>
                            <input
                                type="email"
                                placeholder="your@email.com"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                        </div>

                        <div className="field">
                            <label>Password</label>
                            <input
                                type="password"
                                placeholder="******"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>

                        <div className="field">
                            <label>Confirm password</label>
                            <input
                                type="password"
                                placeholder="******"
                                value={confirm}
                                onChange={(e) => setConfirm(e.target.value)}
                                required
                            />
                        </div>

                        <button className="primary" type="submit">
                            Register
                        </button>
                    </form>
                </div>
            </div>
        </div>
    )
}

