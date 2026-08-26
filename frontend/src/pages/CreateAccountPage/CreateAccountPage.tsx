import {useState} from "react";
import "./CreateAccountPage.css";
import {useNavigate} from "react-router-dom";
const API_URL = import.meta.env.VITE_API_URL;

function CreateAccountPage() {
    const [credentials, setCredentials] = useState({
        userName: "",
        email: "",
        password: "",
        confirmPassword: "",
    });

    const navigate = useNavigate();

    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setError(null);
        setIsLoading(true);

        try {
            if (credentials.password !== credentials.confirmPassword) {
                setError("Passwords do not match");
                return;
            }
            const response = await fetch(`${API_URL}/users`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    userName: credentials.userName,
                    email: credentials.email,
                    password: credentials.password,
                }),
            });

            if (response.status === 409) {
                setError("Username or email already in use");
                return;
            }

            if (response.status === 400) {
                setError("Invalid fields");
                return;
            }

            if (!response.ok) {
                setError("Account could not be created");
                return;
            }
            navigate("/login", {
                state: {
                    message: "Account created successfully. Please log in.",
                },
            });
            
        } catch {
            setError("The server could not be reached");
        }   finally {
            setIsLoading(false);
        }
    }

    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = event.target;

        setCredentials({
            ...credentials,
            [name]: value,
        });
    }

    return (
        <main className="create-account-page">
            <section className="create-account-card">
                <h1 className="title">Create New Account</h1>

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="userName">Username</label>
                        <input 
                            className="form-input"
                            id="userName"
                            name="userName"
                            type="text"
                            value={credentials.userName}
                            onChange={handleChange}
                            minLength={3}
                            maxLength={30}
                            required
                        />
                    </div>


                    <div className="form-group">
                        <label htmlFor="email">Email</label>
                        <input
                            className="form-input"
                            id="email"
                            name="email"
                            type="email"
                            value={credentials.email}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">Password</label>

                        <div className="password-input-row">
                            <input
                                className="form-input"
                                id="password"
                                name="password"
                                type={showPassword ? "text" : "password"}
                                value={credentials.password}
                                onChange={handleChange}
                                minLength={8}
                                required
                            />

                            <button
                                className="password-visibility-button"
                                type="button" 
                                onClick={() => setShowPassword(!showPassword)}
                                aria-label={showPassword ? "Hide password" : "Show password"}
                                >
                                {showPassword ? "Hide" : "Show"}
                            </button>
                        </div>
                    </div>

                    <div className="form-group">
                        <label htmlFor="confirmPassword">Confirm Password</label>

                        <div className="password-input-row">
                            <input 
                                className="form-input"
                                id="confirmPassword"
                                name="confirmPassword"
                                type={showConfirmPassword ? "text" : "password"}
                                value={credentials.confirmPassword}
                                onChange={handleChange}
                                minLength={8}
                                required
                            />

                            <button
                                className="password-visibility-button"
                                type="button"
                                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                aria-label={showConfirmPassword ? "Hide confirm password" : "Show confirm password"}
                                >
                                {showConfirmPassword ? "Hide" : "Show"}
                            </button>
                        </div>

                        {error && <p role="alert">{error}</p>}
                    </div>

                    <a className="page-text" href="/login">Already have an account? Log in</a>

                    <div className="submit-button">
                        <button type="submit" disabled={isLoading}>
                        {isLoading ? "Creating Account..." : "Create Account"}
                        </button>
                    </div>
                </form>
            </section>
        </main>
    )
}

export default CreateAccountPage;
