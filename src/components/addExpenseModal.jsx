import { useState } from "react";

export default function AddExpenseModal({ isOpen, onClose,onSave, participants }) {
    const [splitType, setSplitType] = useState('equally'); // 'equally' lub 'custom'
    const [showExchangeRate, setShowExchangeRate] = useState(false);

    if (!isOpen) return null;
    const handleSubmit = (e) => {
        e.preventDefault(); 
        onSave(); 
    };


    return (
        <div className="modal-overlay">
            <div className="modal-container">
                <header className="modal-header">
                    <h2>Add Expense</h2>
                    <button className="close-x" onClick={onClose}>&times;</button>
                </header>

                <form className="expense-form" onSubmit={handleSubmit}>
                    <div className="form-row">
                        <div className="form-group">
                            <label>Amount <span className="required">*</span></label>
                            <div className="amount-input-wrapper">
                                <input type="number" placeholder="500" />
                                <select className="currency-select">
                                    <option>GBP</option>
                                    <option>PLN</option>
                                    <option>EUR</option>
                                </select>
                            </div>
                        </div>

                        <div className="form-group">
                            <label>Date</label>
                            <input type="date" defaultValue="2025-11-22" />
                        </div>
                    </div>

                    <div className="exchange-rate-section">
                        <div className="rate-info">
                            <span>Rate: 1 GBP ≈ 4.8220 PLN</span>
                            <button type="button" className="btn-edit-rate" onClick={() => setShowExchangeRate(!showExchangeRate)}>
                                ✎ Edit
                            </button>
                        </div>
                        {showExchangeRate && (
                            <div className="rate-edit-box">
                                <label>Exchange Rate</label>
                                <div className="rate-input">
                                    <span>1 GBP =</span>
                                    <input type="number" step="0.0001" defaultValue="4.8220" />
                                    <span>PLN</span>
                                </div>
                            </div>
                        )}
                        <div className="total-converted">Total: 2411.00 PLN</div>
                    </div>

                    <div className="form-group">
                        <label>Description <span className="required">*</span></label>
                        <input type="text" placeholder="Grocery Shopping" />
                    </div>

                    <div className="form-group">
                        <label>Who paid? <span className="required">*</span></label>
                        <select>
                            <option>You (You)</option>
                            {participants?.map(p => <option key={p.id}>{p.name}</option>)}
                        </select>
                    </div>

                    <div className="form-group">
                        <label>For whom? <span className="required">*</span></label>
                        <div className="split-toggle">
                            <button 
                                type="button"
                                className={splitType === 'equally' ? 'active' : ''} 
                                onClick={() => setSplitType('equally')}
                            >Split equally</button>
                            <button 
                                type="button"
                                className={splitType === 'custom' ? 'active' : ''} 
                                onClick={() => setSplitType('custom')}
                            >Custom split</button>
                        </div>
                    </div>

                    {splitType === 'custom' && (
                        <div className="custom-split-list">
                            <p className="split-hint">Splits shown in PLN</p>
                            <div className="participant-row">
                                <label className="checkbox-container">
                                    <input type="checkbox" defaultChecked />
                                    <span className="checkmark"></span>
                                    You (You)
                                </label>
                                <div className="split-amount">
                                    <input type="number" defaultValue="320" />
                                    <span>PLN</span>
                                </div>
                            </div>
                            <div className="participant-row">
                                <label className="checkbox-container">
                                    <input type="checkbox" defaultChecked />
                                    <span className="checkmark"></span>
                                    Kasia
                                </label>
                                <div className="split-amount">
                                    <input type="number" defaultValue="2091" />
                                    <span>PLN</span>
                                </div>
                            </div>
                            <div className="split-total-footer">
                                <span>Split total:</span>
                                <span className="valid">2411.00 / 2411.00 PLN</span>
                            </div>
                        </div>
                    )}

                    <footer className="modal-footer">
                        <button type="button" className="btn-cancel" onClick={onClose}>Cancel</button>
                        <button type="submit" className="btn-save">Save expense</button>
                    </footer>
                </form>
            </div>
        </div>
    );
}