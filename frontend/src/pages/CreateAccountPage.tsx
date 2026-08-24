import {useState} from "react";
import "./CreateAccountPage.css";

function CreateAccountPage() {
    const [credentials, setCredentials] = useState({
        userName: "",
        email: "",
        password: "",
        confirmPassword: "",
    });

    const [showPassword, setShowPassword] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement) => {
        event.preventDefault();
        setError(null);
        setIsLoading(true);

        //continue here with fetch requrest to users
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
                <h1 id="title">Create New Account</h1>

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="username">UserName</label>
                        <input 
                            className="form-input"
                            id="username"
                            name="username"
                            type="username"
                            value={credentials.userName}
                            onChange={handleChange}
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
                                required
                            />
                        </div>
                    </div>

                    <div className="form-group">
                        <label htmlFor="confirmPassword">Confirm Password</label>

                        <div className="password-input-row">
                            <input 
                                className="form-input"
                                id="confirmPassword"
                                name="confirmPassword"
                                type={showPassword ? "text" : "confirmPassword"}
                                onChange={handleChange}
                                required
                            />

                            <button
                                type="button" 
                                onClick={() => setShowPassword(!showPassword)}
                                aria-label={showPassword ? "Hide password" : "Show password"}
                                >
                                {showPassword ? "Hide" : "Show"}
                            </button>
                        </div>

                        {error && <p role="alert">{error}</p>}
                    </div>
                </form>
            </section>
        </main>
    )
}

export default CreateAccountPage;