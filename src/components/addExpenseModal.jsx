import { useEffect, useRef, useState } from "react";

export default function AddExpenseModal({ isOpen, onClose, onSave, participants }) {
    const [amount, setAmount] = useState("");
    const [date, setDate] = useState("2025-11-22");
    const [description, setDescription] = useState("");
    const [payer, setPayer] = useState("You (You)");
    const firstRef = useRef(null);
    useEffect(() => {
    if (isOpen) firstRef.current?.focus();
    }, [isOpen]);
    
    const [splitType, setSplitType] = useState('equally'); 
    const [showExchangeRate, setShowExchangeRate] = useState(false);

    if (!isOpen) return null;
    
    const handleSubmit = (e) => {
        e.preventDefault(); 
        onSave({
            amount,
            date,
            description,
            payer,
            splitType
        }); 
        // Czyścimy pola
        setAmount("");
        setDescription("");
    };

    return (
        <div className="modal-overlay"  onClick={onClose}>
            <div className="modal-container" onClick={(e) => e.stopPropagation()}>
                <header className="modal-header">
                    <h2>Add Expense</h2>
                    <button className="close-x" onClick={onClose} type="button">&times;</button>
                </header>

                <form className="expense-form" onSubmit={handleSubmit}>
                    <div className="form-row">
                        <div className="form-group">
                            <label>Amount <span className="required">*</span></label>
                            <div className="amount-input-wrapper">
                                <input 
                                    ref={firstRef}
                                    type="number" 
                                    placeholder="500" 
                                    value={amount}
                                    onChange={(e) => setAmount(e.target.value)}
                                    required
                                />
                                <select className="currency-select">
                                    <option>GBP</option>
                                    <option>PLN</option>
                                    <option>EUR</option>
                                </select>
                            </div>
                        </div>

                        <div className="form-group">
                            <label>Date</label>
                            <input 
                                type="date" 
                                value={date} 
                                onChange={(e) => setDate(e.target.value)} // Naprawia pisanie
                            />
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
                        <div className="total-converted">Total: {(parseFloat(amount || 0) * 4.822).toFixed(2)} PLN</div>
                    </div>

                    <div className="form-group">
                        <label>Description <span className="required">*</span></label>
                        <input 
                            type="text" 
                            placeholder="Grocery Shopping" 
                            value={description}
                            onChange={(e) => setDescription(e.target.value)} // Naprawia pisanie
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Who paid? <span className="required">*</span></label>
                        <select value={payer} onChange={(e) => setPayer(e.target.value)}>
                            <option value="You (You)">You (You)</option>
                            {participants?.map(p => (
                                <option key={p.id} value={p.name}>{p.name}</option>
                            ))}
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
                                    You (You)
                                </label>
                                <div className="split-amount">
                                    <input type="number" defaultValue="320" />
                                    <span>PLN</span>
                                </div>
                            </div>
                            <div className="split-total-footer">
                                <span>Split total:</span>
                                <span className="valid">PLN zapłacone</span>
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