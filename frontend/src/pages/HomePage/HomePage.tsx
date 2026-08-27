import { Link } from "react-router-dom";
import "./HomePage.css";

function HomePage() {
    

    return (
        <>
            <header className="header">
                <nav aria-label="Main navigation">
                    <ul>
                        <li><Link to="/register">Create Account</Link></li>
                        <li><Link to="/login">Login</Link></li>
                    </ul>
                </nav>
            </header>

            <main className="home-page">
                <h1>MediaMatch</h1>
            </main>
        </>
    );
}

export default HomePage;