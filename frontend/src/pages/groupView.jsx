import { useState } from "react";
import AddExpenseModal from "../components/addExpenseModal";
import "../styles/groupView.css";
export default function GroupView({ onBack }) {
    const [activeTab, setActiveTab] = useState('history');
    const [isModalOpen, setIsModalOpen] = useState(false);

    const handleSaveExpense = () => {
        setIsModalOpen(false);
        setActiveTab('history');
    };

    return(
        <>
            <header className="group-header">
                <div className="back-btn" onClick={onBack}>← Back to groups</div>

                <div className="title-bar">
                    <h1>Trip to Spain</h1>
                    <p>Hotel and restaurant expenses</p>
                </div>
                <div className="action-btn">
                    <button className="btn-delete">🗑️ Delete Group</button>
                    <button className="btn-add" onClick={() => setIsModalOpen(true)}>+ Add Expense</button>

                    <AddExpenseModal 
                        isOpen={isModalOpen} 
                        onClose={() => setIsModalOpen(false)} 
                        onSave={handleSaveExpense}
                        participants={[{id: 1, name: 'Kasia'}]} />
                </div>

                <div className="stats-row">
                    <span>👤 3 participants</span>
                    <span>🧾 2 expenses</span>
                    <span>💰 Total: $300.00</span>
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
                        <h3 className="date-label">August 15, 2024</h3>

                        <article className="expense-card">
                            <div className="expense-info">
                                <div className="expense-icon">🧾</div>
                                <div className="expense-details">
                                    <h4>Dinner at La Casa</h4>
                                    <span>👤 Paid by: Anna</span>
                                    <p>Split between 3 people</p>
                                </div>
                            </div>
                            <div className="expnese-amounts">
                                <span className="total">$100.00</span>
                                <span className="your-share">$33.33</span>
                            </div>                        
                        </article>

                        <article className="expense-card">
                            <div className="expense-info">
                                <div className="expense-icon">🧾</div>
                                <div className="expense-details">
                                    <h4>Hotel Room</h4>
                                    <span>👤 Paid by: Ben</span>
                                    <p>Split between 3 people</p>
                                </div>
                            </div>
                            <div className="expnese-amounts">
                                <span className="total">$200.00</span>
                                <span className="your-share">$66.67</span>
                            </div>                        
                        </article>
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