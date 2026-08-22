import { useState } from "react";
import "./LoginPage.css"
const API_URL = import.meta.env.VITE_API_URL;

type LoginResponse = {
  token: string;
  userId: number;
  username: string;
};

function LoginPage() {
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
    } catch {
      setError("The server could not be reached");
    } finally {
      setIsLoading(false);
    }
  };

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;

    setCredentials({
      ...credentials,
      [name]: value,
    });
  };

  return (
    <main className="login-page">
      <h1>Welcome To MediaMatch</h1>

      <form onSubmit={handleSubmit}>
        <label htmlFor="email">Email</label>
        <input
          id="email"
          name="email"
          type="email"
          value={credentials.email}
          onChange={handleChange}
          required
        />

        <label htmlFor="password">Password</label>
        <input
          id="password"
          name="password"
          type={showPassword ? "text" : "password"}
          value={credentials.password}
          onChange={handleChange}
          required
        />
        {error && <p role="alert">{error}</p>}

        <button
          type="button" 
          onClick={() => setShowPassword(!showPassword)}
          aria-label={showPassword ? "Hide password" : "Show password"}
          >
            {showPassword ? "Hide" : "Show"}
        </button>

        <a href="/forgot-password">Forgot Password?</a>

        <button 
          type="submit" disabled={isLoading}>
          {isLoading ? "Logging in..." : "Login"}
        </button>
      </form>

      <a href="/register">Create New Account</a>
    </main>
  );
}

export default LoginPage;
