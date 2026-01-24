import { useEffect, useRef, useState } from "react";

export default function CreateGroupModal({ onClose, onCreate }) {
  const [groupName, setGroupName] = useState("");
  const [currencyCode, setCurrencyCode] = useState("PLN");
  const firstRef = useRef(null);

  const [pEmail, setPEmail] = useState("");
  const [members, setMembers] = useState([]);

  useEffect(() => {
    firstRef.current?.focus();
  }, []);

  function isEmailValid(email) {
    return email.includes("@") && email.includes(".");
  }

  function addParticipant() {
    const email = pEmail.trim().toLowerCase();

    if (!isEmailValid(email)) {
      console.log("Enter a valid email.");
      return;
    }

    const alreadyAdded = members.some((p) => p.email.toLowerCase() === email);
    if (alreadyAdded) {
      console.log("This email is already added.");
      return;
    }

    setMembers([...members, { id: Date.now(), email }]);
    setPEmail("");
  }

  function removeParticipant(id) {
    setMembers(members.filter((p) => p.id !== id));
  }

  function handleSubmit(e) {
    e.preventDefault();

    if (!groupName.trim()) {
      console.log("Enter group name.");
      return;
    }

    onCreate({
      groupName: groupName.trim(),
      currencyCode,
      members,
    });

    onClose();
  }

  return (
    <div className="modalOverlay" onClick={onClose}>
      <div className="modalCard" onClick={(e) => e.stopPropagation()}>
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
              ref={firstRef}
              autoFocus
              value={groupName}
              onChange={(e) => setGroupName(e.target.value)}
              placeholder="e.g. Weekend in the mountains"
              required
            />
          </div>

          <div className="field">
            <label>Currency *</label>
            <select value={currencyCode} onChange={(e) => setCurrencyCode(e.target.value)} required>
              <option value="PLN">PLN (złoty)</option>
              <option value="EUR">EUR (euro)</option>
              <option value="USD">USD (dollar)</option>
            </select>
          </div>

          <div className="field">
            <label>Participants *</label>

            <div className="participantsBox">
              {members.map((p) => (
                <div key={p.id} className="participantRow">
                  <div>
                    <div className="pEmail"><strong>{p.email}</strong></div>
                  </div>

                  <button
                    type="button"
                    className="trashBtn"
                    onClick={() => removeParticipant(p.id)}
                  >
                    🗑
                  </button>
                </div>
              ))}

              <div className="addBox">
                <input
                  placeholder="Email"
                  value={pEmail}
                  onChange={(e) => setPEmail(e.target.value)}
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
