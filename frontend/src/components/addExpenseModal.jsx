import { useEffect, useRef, useState } from "react";

export default function AddExpenseModal({ onClose, onSave, groupId, currentUserId }) {
    const [amount, setAmount] = useState("");
    const [currencyCode, setCurrencyCode] = useState("PLN");
    const [date, setDate] = useState(new Date().toISOString().split("T")[0]);
    const [description, setDescription] = useState("");
    const [payerId, setPayerId] = useState("");
    const [exchangeRate, setExchangeRate] = useState(1);
    const [customExchangeRate, setCustomExchangeRate] = useState(false);
    const [participants, setParticipants] = useState([]);
    const [groupDefaultCurrencyCode, setGroupDefaultCurrencyCode] = useState("PLN");
    const [availableCurrencyCodes, setAvailableCurrencyCodes] = useState([]);
    const [splitType, setSplitType] = useState('equally');
    const [splitDetails, setSplitDetails] = useState({});

    const firstRef = useRef(null);
    useEffect(() => {
        firstRef.current?.focus();
    }, []);

    useEffect(() => {
        const token = localStorage.getItem("token");
        const fetchContext = async () => {
            const res = await fetch(`http://localhost:8080/api/groups/${groupId}/expense-context`, {
                headers: { "Authorization": `Bearer ${token}` }
            });
            const data = await res.json();

            setParticipants(data.participants);
            setGroupDefaultCurrencyCode(data.defaultCurrencyCode);
            setCurrencyCode(data.currentCurrencyCode);
            setExchangeRate(data.currentExchangeRate);
            setAvailableCurrencyCodes(data.availableCurrencyCodes);
            setPayerId(data.currentUserId);
        };
        fetchContext();
    }, [groupId]);

    const handleCurrencyChange = async (newCurrencyCode) => {
        setCurrencyCode(newCurrencyCode);

        if (newCurrencyCode === groupDefaultCurrencyCode) {
            setExchangeRate(1);
            return;
        }

        const token = localStorage.getItem("token");
        try {
            const res = await fetch(`http://localhost:8080/api/currencies/rate?from=${newCurrencyCode}&to=${groupDefaultCurrencyCode}`, {
                headers: { "Authorization": `Bearer ${token}` }
            });
            const data = await res.json();
            setExchangeRate(data.rate);
        } catch (err) {
            console.error(err);
        }
    };

    const handleSplitChange = (userId, value) => {
        setSplitDetails(prev => ({ ...prev, [userId]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        let finalSplitDetails = {};
        const numericAmount = parseFloat(amount);
        if (splitType === 'equally') {
            finalSplitDetails = null;
        } else {
            Object.keys(splitDetails).forEach(id => {
                finalSplitDetails[id] = parseFloat(splitDetails[id] || 0);
            });
        }

        const payload = {
            description,
            amount: numericAmount,
            currencyCode,
            exchangeRate: exchangeRate ? parseFloat(exchangeRate) : null,
            payerId: parseInt(payerId),
            groupId: parseInt(groupId),
            date: date,
            splitDetails: finalSplitDetails,
            isCustomRate: customExchangeRate
        }

        try {
            const response = await fetch("http://localhost:8080/api/expenses", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${localStorage.getItem("token")}`
                },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                onSave();
                resetForm();
            } else {
                // const err = await response.json();
                // alert("Error: " + (err.error || "Failed to add expense"));
            }
        } catch /*(error)*/ {
            // console.error("Network error:", error);
        }
    };

    const resetForm = () => {
        setAmount("");
        setDescription("");
        setExchangeRate(1);
        setSplitType('equally');
        onClose();
    }

    return (
        <div className="modal-overlay" onClick={onClose}>
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
                                    autoFocus
                                    type="number"
                                    placeholder="0.00"
                                    value={amount}
                                    onChange={(e) => setAmount(e.target.value)}
                                    required
                                />
                                <select className="currency-select" value={currencyCode} onChange={(e) => handleCurrencyChange(e.target.value)}>
                                    {availableCurrencyCodes.map(code => (
                                        <option key={code} value={code}>{code}</option>
                                    ))}
                                </select>
                            </div>
                        </div>

                        <div className="form-group">
                            <label>Date</label>
                            <input
                                type="date"
                                value={date}
                                onChange={(e) => setDate(e.target.value)}
                            />
                        </div>
                    </div>

                    <div className="exchange-rate-section">
                        <div className="rate-info">
                            {groupDefaultCurrencyCode !== currencyCode && (
                                <span>Rate: 1 {currencyCode} ≈ {exchangeRate} {groupDefaultCurrencyCode}</span>
                            )}
                            <button
                                type="button"
                                className="btn-edit-rate"
                                onClick={() => setCustomExchangeRate(!customExchangeRate)}
                                disabled={groupDefaultCurrencyCode === currencyCode}
                                >
                                {customExchangeRate ? "✖ Use real exchange rate" : "✎ Set Custom Exchange Rate"}
                            </button>
                        </div>
                        {customExchangeRate && (
                            <div className="rate-edit-box">
                                <label>Exchange Rate</label>
                                <div className="rate-input">
                                    <span>1 {currencyCode} =</span>
                                    <input type="number" step="0.0001" defaultValue={exchangeRate} onChange={(e) => setExchangeRate(e.target.value)} />
                                    <span>{groupDefaultCurrencyCode}</span>
                                </div>
                            </div>
                        )}
                        <div className="total-converted">Total: {(parseFloat(amount || 0) * (exchangeRate)).toFixed(2)} {groupDefaultCurrencyCode}</div>
                    </div>

                    <div className="form-group">
                        <label>Description <span className="required">*</span></label>
                        <input
                            type="text"
                            placeholder="e. g. Grocery Shopping"
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Who paid? <span className="required">*</span></label>
                        <select value={payerId} onChange={(e) => setPayerId(e.target.value)}>
                            {participants?.map(p => (
                                <option key={p.id} value={p.id}>{p.name + (p.id === currentUserId ? " (You)" : "")}</option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group">
                        <label>Split Type<span className="required">*</span></label>
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
                            {participants.map(p => (
                                <div key={p.id} className="participant-row">
                                    <label className="checkbox-container">
                                        <input type="checkbox" defaultChecked />
                                        {p.name + (p.id === currentUserId ? " (You)" : "")}
                                    </label>
                                    <div className="split-amount">
                                        <input
                                            type="number"
                                            value={splitDetails[p.id] || 0}
                                            onChange={(e) => handleSplitChange(p.id, e.target.value)}
                                            />
                                        <span>{groupDefaultCurrencyCode}</span>
                                    </div>
                                </div>
                            ))}
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