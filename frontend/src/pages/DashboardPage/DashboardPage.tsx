import "./DashboardPage.css";
import { Link } from "react-router-dom";
import { FaUser } from "react-icons/fa";

function DashboardPage() {
    const username = localStorage.getItem("username");
    
    return (
        <>
            <header className="header">
                <nav aria-label="main-navigation">
                    <p id="page-title">MediaMatch</p>

                    <div className="profile-menu">
                        <Link
                            to="/account"
                            className="profile-button"
                            aria-label="View account"
                        >
                            <FaUser aria-hidden="true" />
                        </Link>
                    </div>
                </nav>
            </header>

            <main className="home-page">
                <section className="hero">
                    <div className="hero-content">
                        <h1>Welcome, {username}!</h1>
                        <p>
                        Track the movies, shows, books, and games you love—and discover
                        what to enjoy next.
                        </p>

                        <div className="hero-actions">
                            <Link to="/lists">My Lists</Link>
                        </div>
                    </div>
                </section>
            </main>

            <footer className="footer">
                <nav aria-label="footer-navigation">
                    <p>Created by: Daniel Abgaryan</p>
                    <ul>
                        <li>
                            <a
                                href="https://github.com/Dabgaryan1/media-match"
                                target="_blank"
                                rel="noopener noreferrer"
                            >GitHub</a>
                        </li>
                        <li>
                            <a
                                href="https://www.linkedin.com/in/daniel-abgaryan/"
                                target="_blank"
                                rel="noopener noreferrer"
                            >LinkedIn</a>
                        </li>
                    </ul>
                </nav>
            </footer>
        </>
    );
}

export default DashboardPage