import { useState } from "react";
const API_URL = import.meta.env.VITE_API_URL;

function LoginPage() {
  const [credentials, setCredentials] = useState({
    email: "",
    password: "",
  });

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    try {
      const response = await fetch(`${API_URL}/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(credentials),
      });

      if (!response.ok) {
        console.log("Invalid email or password");
        return;
      }

      const loginResponse = await response.json();
      console.log(loginResponse);
    } catch {
      console.log("The server could not be reached");
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
    <main>
      <h1>Welcome To MediaMatch</h1>

      <form onSubmit={handleSubmit}>
        <label htmlFor="email">Email</label>
        <input
          id="email"
          name="email"
          type="email"
          value={credentials.email}
          onChange={handleChange}
        />

        <label htmlFor="password">Password</label>
        <input
          id="password"
          name="password"
          type="password"
          value={credentials.password}
          onChange={handleChange}
        />

        <button type="submit">Login</button>
      </form>
    </main>
  );
}

export default LoginPage;
