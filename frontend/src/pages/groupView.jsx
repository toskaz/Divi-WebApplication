import { useState, useEffect } from "react";
import AddExpenseModal from "../components/addExpenseModal";
import "../styles/groupView.css";
export default function GroupView({ groupId, onBack }) {
    const [activeTab, setActiveTab] = useState('history');
    const [isModalOpen, setIsModalOpen] = useState(false);

    const [groupDetails, setGroupDetails] = useState(null);
    const [expenses, setExpenses] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem("token");

        const fetchData = async () => {
            try {
                console.log(`Sending request: http://localhost:8080/api/groups/details/${groupId}`)
                const detailRes = await fetch(`http://localhost:8080/api/groups/details/${groupId}`, {
                    headers: { "Authorization": `Bearer ${token}` }
                });
                const detailData = await detailRes.json();
                setGroupDetails(detailData);

                const expenseRes = await fetch(`http://localhost:8080/api/expenses/group/${groupId}`, {
                    headers: { "Authorization": `Bearer ${token}` }
                });
                const expenseData = await expenseRes.json();
                setExpenses(expenseData);
                console.log("Fetched expenses:", expenseData);

            } catch /*(error)*/ {
                // console.error("Network error:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [groupId]);

    const handleSaveExpense = () => {
        setIsModalOpen(false);
        setActiveTab('history');
        // TODO: Refresh expenses list?
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
                    className={`tab-btn ${activeTab === 'settled' ? 'active' : ''}`}
                    onClick={() => setActiveTab('settled')}  > How to settle?
                </button>
            </nav>

            <main className="tab-content">
                {activeTab === 'history' && (
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

                {activeTab === 'balances' && (
                    <section className="balances-list">
                        <div className="info-box">
                            <h2>How to read balances?</h2>
                            <p>Positive balance (green) mean others owe you money. Negative balance (red) mean you owe others.</p>
                        </div>
                        <article className="balance-card positive">
                            <div className="status-icon">📈</div>
                            <div className="user-details">
                                <strong>You</strong>
                                <span className="status-label">is owed</span>
                            </div>
                            <span className="balance-amount">+$45.00</span>
                        </article>

                        <article className="balance-card negative">
                            <div className="status-icon">📉</div>
                            <div className="user-details">
                                <strong>Ben</strong>
                                <span className="status-label">owes you</span>
                            </div>
                            <span className="balance-amount">-$60.00</span>
                        </article>

                        <article className="balance-card positive">
                            <div className="status-icon">📈</div>
                            <div className="user-details">
                                <strong>Anna</strong>
                                <span className="status-label">is owed</span>
                            </div>
                            <span className="balance-amount">+$15.00</span>
                        </article>
                    </section>
                )}  

                {activeTab === 'settled' && (
                    <section className="settlement-guide">
                        <div className="info-box blue">
                            <h2>How does it work?</h2>
                            <p>Below is an optimized list of transfers that will settle all debts in the group with the minimum number of transactions.</p>
                            <p>Number of transactions: 2</p>
                        </div>

                        <div className="success-box">
                            <div className="success-icon">✅</div>
                            <div className="success-text">
                                <strong>Payment Recorded</strong>
                                <p>Ben sent $60.00 to You</p>
                            </div>
                        </div>

                        <article className="transfer-card">
                            <div className="transfer-details">
                                <div className="user-from"><strong>You</strong></div>
                                <div className="transfer-arrow">
                                    <span className="amount-bubble">$15.00</span>
                                    <span>→</span>
                                </div>
                                <div className="user-to"><strong>Anna</strong></div>
                            </div>
                            <button className="mark-btn">Mark as completed</button>
                        </article>

                        <div className="success-box secondary">
                        <div className="success-icon">✔️</div>
                            <div className="success-text">
                                <strong>After completing settlements</strong>
                                <p>After completing all the above transfers, all participants will be settled and balances will be $0.00</p>
                            </div>
                        </div>
                    </section>
                )}
            </main>
        </>
    );
}