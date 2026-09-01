import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./LoginPage.css";

const API_URL = import.meta.env.VITE_API_URL;

type LoginResponse = {
  token: string;
  userId: number;
  username: string;
};

function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();

  const successMessage = location.state?.message;
  const [credentials, setCredentials] = useState({
    email: "",
    password: "",
  });

  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);
    setIsLoading(true);

    try {
      const response = await fetch(`${API_URL}/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(credentials),
      });

      if (!response.ok) {
        setError("Invalid email or password");
        return;
      }

      const loginResponse: LoginResponse = await response.json();
      localStorage.setItem("token", loginResponse.token);
      localStorage.setItem("userId", String(loginResponse.userId));
      localStorage.setItem("username", loginResponse.username);
      navigate("/dashboard");
    } catch {
      setError("The server could not be reached");
    } finally {
      setIsLoading(false);
    }
  }

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const {name, value} = event.target;

    setCredentials({
      ...credentials,
      [name]: value,
    });
  };

  return (
    <main className="login-page">
      <section className="login-card">
        <h1 className="title">Welcome to MediaMatch</h1>

        {successMessage && (
          <p className="success-message" role="status">
            {successMessage}
          </p>
        )}
        <form onSubmit={handleSubmit}>
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

          <a className="page-text" href="/forgot-password">Forgot Password?</a>
          
        <a className="page-text" href="/register">Create New Account</a>

          <div id="submit-button">
            <button 
              type="submit" disabled={isLoading}>
              {isLoading ? "Logging in..." : "Login"}
            </button>
          </div>
        </form>
      </section>
    </main>
  );
}

export default LoginPage
