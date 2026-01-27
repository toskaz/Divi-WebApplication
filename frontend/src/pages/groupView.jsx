import { useState, useEffect, useCallback } from "react";
import AddExpenseModal from "../components/addExpenseModal";
import "../styles/groupView.css";
export default function GroupView({ groupId, onBack }) {
    const [activeTab, setActiveTab] = useState('history');
    const [isModalOpen, setIsModalOpen] = useState(false);

    const [loading, setLoading] = useState(true);
    const [groupDetails, setGroupDetails] = useState(null);
    const [expenses, setExpenses] = useState([]);
    const [loadingExpenses, setLoadingExpenses] = useState(true);

    const [balances, setBalances] = useState([]);
    const [loadingBalances, setLoadingBalances] = useState(false);
    const [currentUserId, setCurrentUserId] = useState(null);

    const [settlements, setSettlements] = useState([]);
    const [loadingSettlements, setLoadingSettlements] = useState(false);

    const fetchData = useCallback(async () => {
        setLoading(true);
        const token = localStorage.getItem("token");
        try {
            const detailRes = await fetch(`http://localhost:8080/api/groups/details/${groupId}`, {
                headers: { "Authorization": `Bearer ${token}` }
            });
            const detailData = await detailRes.json();
            setGroupDetails(detailData);
            setCurrentUserId(detailData.currentUserId);
        } catch (error) {
            console.error("Failed to fetch group details: ", error);
        } finally {
            setLoading(false);
        }
    }, [groupId]);

    useEffect(() => {
        fetchData();
    }, [fetchData]);

    const fetchExpenses = useCallback(async () => {
        const token = localStorage.getItem("token");
        setLoadingExpenses(true);
        try {
            const res = await fetch(`http://localhost:8080/api/expenses/group/${groupId}`, {
                headers: { "Authorization": `Bearer ${token}` }
            });
            const data = await res.json();
            setExpenses(data);
            console.log("Fetched expenses: ", data);
        } catch (error) {
            console.error("Failed to fetch expenses: ", error);
        } finally {
            setLoadingExpenses(false);
        }
    }, [groupId]);

    useEffect(() => {
        if (activeTab === 'history') {
            fetchExpenses();
        }
    }, [activeTab, fetchExpenses]);

    const fetchBalances = useCallback(async () => {
        const token = localStorage.getItem("token");
        setLoadingBalances(true);
        try {
            const res = await fetch(`http://localhost:8080/api/groups/${groupId}/balances`, {
                headers: { "Authorization": `Bearer ${token}` }
            });
            const data = await res.json();
            setBalances(data);
            console.log("Fetched balances:", data);
        } catch (error) {
            console.error("Failed to fetch balances:", error);
        } finally {
            setLoadingBalances(false);
        }
    }, [groupId]);

    useEffect(() => {
        if (activeTab === 'balances') {
            fetchBalances();
        }
    }, [activeTab, fetchBalances]);

    const fetchSettlements = useCallback(async () => {
        const token = localStorage.getItem("token");
        setLoadingSettlements(true);
        try {
            const res = await fetch(`http://localhost:8080/api/groups/${groupId}/settlements`, {
                headers: { "Authorization": `Bearer ${token}` }
            });
            const data = await res.json();
            setSettlements(data);
            console.log("Fetched settlements: ", data);
        } catch (error) {
            console.error("Failed to fetch settlements: ", error);
        } finally {
            setLoadingSettlements(false);
        }
    }, [groupId]);

    useEffect(() => {
        if (activeTab === 'settling') {
            fetchSettlements();
        }
    }, [activeTab, fetchSettlements]);

    const handleMarkCompleted = async (settlement) => {
        const token = localStorage.getItem("token");
        setLoadingSettlements(true);
        try {
            const result = await fetch(`http://localhost:8080/api/groups/${groupId}/settle`, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(settlement)
            });
            if (result.ok) {
                fetchSettlements();
            }
        } catch (error) {
            console.error("Failed to fetch settlements: ", error);
        } finally {
            setLoadingSettlements(false);
        }
    };

    const handleSaveExpense = () => {
        setIsModalOpen(false);
        setActiveTab('history');
        fetchData();
        fetchExpenses();
    };

    const handleDeleteGroup = () => {
        if (!window.confirm("Are you sure you want to delete this group? This action cannot be undone.")) {
            return;
        }
        const token = localStorage.getItem("token");
        fetch(`http://localhost:8080/api/groups/${groupId}`, {
            method: "DELETE",
            headers: { "Authorization": `Bearer ${token}` }
        }).then(() => {
            onBack();
        });
    };

    if (loading) return <div className="loader">Loading group details...</div>;
    if (!groupDetails) return <div className="error">Group not found.</div>;

    return(
        <>
            <header className="group-header">
                <div className="back-btn" onClick={onBack}>← Back to groups</div>

                <div className="title-bar">
                    <h1>{groupDetails.groupName}</h1>
                </div>
                <div className="action-btn">
                    <button className="btn-delete" onClick={handleDeleteGroup}>🗑️ Delete Group</button>
                    <button className="btn-add" onClick={() => setIsModalOpen(true)}>+ Add Expense</button>

                    {isModalOpen && <AddExpenseModal
                        onClose={() => setIsModalOpen(false)}
                        onSave={handleSaveExpense}
                        groupId={groupId}
                        currentUserId={currentUserId}
                        />}
                </div>

                <div className="stats-row">
                    <span>👤 {groupDetails.memberCount} participants</span>
                    <span>🧾 {groupDetails.expenseCount} expenses</span>
                    <span>💰 Total: {groupDetails.totalExpenses} {groupDetails.currencySymbol}</span>
                </div>
            </header>

            <nav className="tabs-nav">
                <button 
                    className={`tab-btn ${activeTab === 'history' ? 'active' : ''}`}
                    onClick={() => setActiveTab('history')}  > Expense history
                </button>

                <button 
                    className={`tab-btn ${activeTab === 'balances' ? 'active' : ''}`}
                    onClick={() => setActiveTab('balances')}  > Balances
                </button>

                <button 
                    className={`tab-btn ${activeTab === 'settling' ? 'active' : ''}`}
                    onClick={() => setActiveTab('settling')}  > How to settle?
                </button>
            </nav>

            <main className="tab-content">
                {activeTab === 'history' && (
                    <>
                        {loadingExpenses ? (
                            <div className="mini-loader">fetching newest expenses...</div>
                        ) : (
                            <section className="history-list">
                                <div className="date-group">
                                    {expenses.length === 0 && (
                                        <p>No expenses recorded yet.</p>
                                    )}
                                    {expenses.map((exp) => (
                                        <article className="expense-card" key={exp.paymentId}>
                                            <div className="expense-info">
                                                <div className="expense-icon">🧾</div>
                                                <div className="expense-details">
                                                    <h4>{exp.description}</h4>
                                                    <span>👤 Paid by: {exp.payerName}</span>
                                                    <p>Split between {exp.involvedPeopleCount} people</p>
                                                    <p>{new Date(exp.date).toLocaleDateString()}</p>
                                                </div>
                                            </div>
                                            <div className="expense-amounts">
                                                <span className="total">{exp.amount} {exp.currencyCode}</span>
                                                <span className="your-share">{exp.yourShare > 0 ? `Your share: ${exp.yourShare} ${exp.currencySymbol}` : 'Not involved'}</span>
                                            </div>
                                        </article>
                                    ))}
                                </div>
                            </section>
                        )}
                    </>
                )}

                {activeTab === 'balances' && (
                    <section className="balances-list">
                        <div className="info-box">
                            <h2>How to read balances?</h2>
                            <p>Positive balance (green) mean others owe you money. Negative balance (red) mean you owe others.</p>
                        </div>

                        {loadingBalances ? (
                            <div className="mini-loader">Calculating balances...</div>
                        ) : (
                            balances.map((b) => {
                                const isPositive = b.balance >= 0;
                                let balanceCard;
                                let statusLabel;
                                let statusIcon;
                                if (b.balance > 0) {
                                    balanceCard = "positive";
                                    statusLabel = "is owed";
                                    statusIcon = "📈";
                                } else if (b.balance < 0) {
                                    balanceCard = "negative";
                                    statusLabel = "owes";
                                    statusIcon = "📉";
                                } else {
                                    balanceCard = "neutral";
                                    statusLabel = "is settled";
                                    statusIcon = "⚖️";
                                }
                                return (
                                    <article key={b.userId} className={`balance-card ${balanceCard}`} >
                                        <div className="status-icon">{statusIcon}</div>
                                        <div className="user-details">
                                            <strong>{b.fullName + (b.userId === currentUserId ? ' (You)' : '')}</strong>
                                            <span className="status-label">{statusLabel}</span>
                                        </div>
                                        <span className="balance-amount">
                                            {isPositive ? '+' : ''}{b.balance.toFixed(2)} {groupDetails.currencySymbol}
                                        </span>
                                    </article>
                                );
                            })
                        )}
                    </section>
                )}

                {activeTab === 'settling' && (
                    <section className="settlement-guide">
                        <div className="info-box blue">
                            <h2>How does it work?</h2>
                            <p>Below is an optimized list of transfers that will settle all debts in the group with the minimum number of transactions.</p>
                            <p>Number of transactions: {settlements.length}</p>
                        </div>

                        {loadingSettlements ? (
                            <div className="mini-loader">Calculating optimal settlements...</div>
                        ) : (
                            <>
                                {settlements.map((s, idx) => (
                                    <article className="transfer-card" key={`pend-${idx}`}>
                                        <div className="transfer-details">
                                            <div className="user-from"><strong>{s.fromUserName} {currentUserId === s.fromUserId ? " (You)" : ""}</strong></div>
                                            <div className="transfer-arrow">
                                                <span className="amount-bubble">{s.amount.toFixed(2)} {s.currencyCode}</span>
                                                <span>→</span>
                                            </div>
                                            <div className="user-to"><strong>{s.toUserName} {currentUserId === s.toUserId ? " (You)" : ""}</strong></div>
                                        </div>
                                        <button className="mark-btn" onClick={() => handleMarkCompleted(s)}>
                                            Mark as completed
                                        </button>
                                    </article>
                                ))}

                                <div className="success-box secondary">
                                <div className="success-icon">✔️</div>
                                    <div className="success-text">
                                        <strong>After completing settlements</strong>
                                        <p>After completing all the above transfers, all participants will be settled and balances will be $0.00</p>
                                    </div>
                                </div>
                            </>
                        )}
                    </section>
                )}
            </main>
        </>
    );
}