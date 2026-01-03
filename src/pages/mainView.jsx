export default function MainView({ onLogout, onSelectGroup }) {
    return(
        <>
            <header className="main-header">
                <nav>
                <div className="logo">Divi</div>
                <button className="logout-btn" onClick={onLogout}>Logout</button>
                </nav>
            </header>

            <main>
                <div className="content-header">
                    <div className="title-section">
                    <h1>My Groups</h1>
                    <p>Manage your shared expenses</p>
                    </div>
                    <button className="create-group-btn">+ New Group</button>
                </div>
                <div className="groups-container">
                    <article className="group-card" onClick={onSelectGroup}>
                        <h2>Trip to Spain</h2>
                        <p>Hotel and restaurant expenses</p>
                        <div className="group-stats">
                            <span>👤 5</span>
                            <span>📅 2 days ago</span>
                        </div>
                        <div className="balance-overview">
                            <span className="balance-label">Your Balance:</span>
                            <span className="balance-amount positive">+$45.00</span>
                        </div>
                    </article>

                    <article className="group-card" onClick={onSelectGroup}>
                        <h2>Weekend Getaway</h2>
                        <p>Cabin and groceries</p>
                        <div className="group-stats">
                            <span>👤 3</span>
                            <span>📅 5 days ago</span>
                        </div>
                        <div className="balance-overview">
                            <span className="balance-label">Your Balance:</span>
                            <span className="balance-amount negative">-$20.00</span>
                        </div>
                    </article>

                    <article className="group-card" onClick={onSelectGroup}>
                        <h2>Office Party</h2>
                        <p>Decorations and food</p>
                        <div className="group-stats">
                            <span>👤 7</span>
                            <span>📅 1 week ago</span>
                        </div>
                        <div className="balance-overview">
                            <span className="balance-label">Your Balance:</span>
                            <span className="balance-amount positive">+$15.00</span>
                        </div>
                    </article>
                </div>
            </main>
        </>
    );


}