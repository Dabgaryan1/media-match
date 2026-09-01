import "./DashboardPage.css";
import { Link, useNavigate } from "react-router-dom";
import { FaUser, FaSearch } from "react-icons/fa";
import { useState } from "react";

function DashboardPage() {
    const username = localStorage.getItem("username");
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState("");
    
    function handleSearch(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault();

        const trimmedQuery = searchQuery.trim();

        if (!trimmedQuery) {
            return;
        }

        navigate(`/search?query=${encodeURIComponent(trimmedQuery)}`);
    }

    return (
        <>
            <header className="header">
                <nav aria-label="main-navigation">
                    <p id="page-title">MediaMatch</p>

                    <div className="search-container">
                        <form className="search-bar" onSubmit={handleSearch}>
                            <input
                                type="search"
                                value={searchQuery}
                                onChange={(event) => setSearchQuery(event.target.value)}
                                placeholder="Search media lists..."
                                aria-label="Search media lists"
                            />

                            <button type="submit" aria-label="Submit search">
                                <FaSearch aria-hidden="true" />
                            </button>
                        </form>
                    </div>
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
                        View your lists or search for other lists and recommendations!
                        </p>

                        <div className="hero-actions">
                            <Link to="/lists">My Lists</Link>
                            <Link to="/recommendations">My Recommendations</Link>
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
