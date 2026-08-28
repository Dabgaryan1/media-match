import { Link } from "react-router-dom";
import "./HomePage.css";

function HomePage() {
    

    return (
        <>
            <header className="header">
                <nav aria-label="main-navigation">
                    <p id="page-title">MediaMatch</p>
                    <ul>
                        <li><Link to="/register">Create Account</Link></li>
                        <li><Link to="/login">Login</Link></li>
                    </ul>
                </nav>
            </header>

            <main className="home-page">
                <section className="hero">
                    <div className="hero-content">
                        <h1>All your favorite media in one place</h1>
                        <p>
                        Track the movies, shows, books, and games you love—and discover
                        what to enjoy next.
                        </p>

                        <div className="hero-actions">
                            <Link to="/register">Create Account</Link>
                            <Link to="/login">Login</Link>
                        </div>
                    </div>
                </section>

                <section className="features" aria-labelledby="features-heading">
                        <h2 id="features-heading">Everything you love, organized</h2>
                        <div className="feature-lists">
                            <article className="feature-card">
                                <h3>Track</h3>
                                <p>Keep track of the movies, shows, books, and games you enjoy!</p>
                            </article>
                            <article className="feature-card">
                                <h3>Organize</h3>
                                <p>Organize your favorite media in lists!</p>
                            </article>
                            <article className="feature-card">
                                <h3>Discover</h3>
                                <p>Find new media with similarities to all of your favorites!</p>
                            </article>
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

export default HomePage;