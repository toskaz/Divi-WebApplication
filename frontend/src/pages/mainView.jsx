import { useState, useEffect } from "react";
import logo from "../assets/logo.png";
import "../styles/mainView.css";
import CreateGroupModal from "../components/CreateGroupModal";

export default function MainView({ onLogout, onSelectGroup }) {
    const [showCreateGroup, setShowCreateGroup] = useState(false);
    const [groups, setGroups] = useState([]);
    const [loading, setLoading] = useState(true);

    async function handleCreateGroup(formData) {
        const token = localStorage.getItem("token");
        console.log("Creating group with data:", formData);
        try {
            const response = await fetch("http://localhost:8080/api/groups", {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(formData)
            });

            if (response.ok) {
                const newGroupSummary = await response.json();
                setShowCreateGroup(false);

                setGroups(prev => [...prev, newGroupSummary]);
            } else {
                // const errorData = await response.json();
                // alert(errorData.error || "Failed to create group");
            }
        } catch /*(error)*/ {
            // console.error("Connection error:", error);
        }
    }

    useEffect(() => {
        const fetchGroups = async () => {
            const token = localStorage.getItem("token");
            try {
                const response = await fetch("http://localhost:8080/api/groups/me", {
                    method: "GET",
                    headers: {
                        "Authorization": `Bearer ${token}`,
                        "Content-Type": "application/json"
                    }
                });

                if (response.ok) {
                    const data = await response.json();
                    setGroups(data);
                } else if (response.status === 401 || response.status === 403) {
                    // onLogout();
                } else {
                    // console.error("Failed to fetch groups");
                }
            } catch /*(error)*/ {
                // console.error("Network error:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchGroups();
    }, [onLogout]);

    return (
        <>
            <header className="main-header">
                <nav>
                    <div className="logo">
                        <img src={logo} alt="Divi logo" />
                    </div>
                    <button className="logout-btn" onClick={onLogout}>Logout</button>
                </nav>
            </header>

            <main>
                <div className="content-header">
                    <div className="title-section">
                        <h1>My Groups</h1>
                        <p>Manage your shared expenses</p>
                    </div>
                    <button className="create-group-btn" onClick={() => setShowCreateGroup(true)}>
                        + New Group
                    </button>
                </div>

                <div className="groups-container">
                    {loading ? (
                        <p>Loading your groups...</p>
                    ) : groups.length > 0 ? (
                        groups.map((group) => (
                            <article
                                key={group.id}
                                className="group-card"
                                onClick={() => onSelectGroup(group.id)}
                            >
                                <h2>{group.groupName}</h2>
                                <p>...</p>
                                <div className="group-stats">
                                    <span>👤 {group.memberCount || 0}</span>
                                    <span>📅 {group.lastPaymentDate} days ago</span>
                                </div>
                            </article>
                        ))
                    ) : (
                        <p>You haven't joined any groups yet.</p>
                    )}
                </div>
            </main>

            {showCreateGroup && (
                <CreateGroupModal
                    onClose={() => setShowCreateGroup(false)}
                    onCreate={handleCreateGroup}
                />
            )}
        </>
    );
}