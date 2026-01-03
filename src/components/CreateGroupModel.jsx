import { useState } from "react";

export default function CreateGroupModal({ onClose, onCreate }) {
  const [groupName, setGroupName] = useState("");
  const [description, setDescription] = useState("");
  const [currency, setCurrency] = useState("PLN");

  const knownUsers = {
    "tosia@gmail.com": "Tosia",
    "kasia@gmail.com": "Kasia",
    "test@gmail.com": "Test User",
  };

  const [participants, setParticipants] = useState([
    { id: 1, name: "You", email: "you@email.com" },
  ]);

  const [pEmail, setPEmail] = useState("");
  const [pName, setPName] = useState("");

  function handleEmailChange(e) {
    const email = e.target.value;
    setPEmail(email);

    const lower = email.trim().toLowerCase();
    if (knownUsers[lower]) {
      setPName(knownUsers[lower]);
    }
  }

  function isEmailValid(email) {
    return email.includes("@") && email.includes(".");
  }

  function addParticipant() {
    const email = pEmail.trim().toLowerCase();
    const name = pName.trim();

    if (!name) {
      alert("Enter participant name.");
      return;
    }

    if (!isEmailValid(email)) {
      alert("Enter a valid email.");
      return;
    }

    const alreadyAdded = participants.some((p) => p.email.toLowerCase() === email);
    if (alreadyAdded) {
      alert("This email is already added.");
      return;
    }

    setParticipants([...participants, { id: Date.now(), name, email }]);
    setPEmail("");
    setPName("");
  }

  function removeParticipant(id) {
    setParticipants(participants.filter((p) => p.id !== id));
  }

  function handleSubmit(e) {
    e.preventDefault();

    if (!groupName.trim()) {
      alert("Enter group name.");
      return;
    }

    onCreate({
      groupName: groupName.trim(),
      description: description.trim(),
      currency,
      participants,
    });

    onClose();
  }

  return (
    <div className="modalOverlay" onMouseDown={onClose}>
      <div className="modalCard" onMouseDown={(e) => e.stopPropagation()}>
        <div className="modalHeader">
          <div className="modalTitle">Create New Group</div>
          <button className="iconBtn" type="button" onClick={onClose}>
            ✕
          </button>
        </div>

        <form className="modalBody" onSubmit={handleSubmit}>
          <div className="field">
            <label>Group name *</label>
            <input
              value={groupName}
              onChange={(e) => setGroupName(e.target.value)}
              placeholder="e.g. Weekend in the mountains"
              required
            />
          </div>

          <div className="field">
            <label>Description (optional)</label>
            <textarea
              className="textarea"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="e.g. Gas and accommodation expenses"
              rows={3}
            />
          </div>

          <div className="field">
            <label>Currency *</label>
            <select value={currency} onChange={(e) => setCurrency(e.target.value)} required>
              <option value="PLN">PLN (złoty)</option>
              <option value="EUR">EUR (euro)</option>
              <option value="USD">USD (dollar)</option>
            </select>
          </div>

          <div className="field">
            <label>Participants *</label>

            <div className="participantsBox">
              {participants.map((p) => (
                <div key={p.id} className="participantRow">
                  <div>
                    <div className="pName">{p.name}</div>
                    <div className="pEmail">{p.email}</div>
                  </div>

                  {p.name !== "You" && (
                    <button
                      type="button"
                      className="trashBtn"
                      onClick={() => removeParticipant(p.id)}
                    >
                      🗑
                    </button>
                  )}
                </div>
              ))}

              <div className="addBox">
                <input
                  placeholder="Email"
                  value={pEmail}
                  onChange={handleEmailChange}
                />
                <input
                  placeholder="Participant name"
                  value={pName}
                  onChange={(e) => setPName(e.target.value)}
                />

                <button type="button" className="addLink" onClick={addParticipant}>
                  + Add participant
                </button>
              </div>
            </div>
          </div>

          <div className="modalFooter">
            <button className="ghostBtn" type="button" onClick={onClose}>
              Cancel
            </button>
            <button className="primarySmall" type="submit">
              Create group
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
