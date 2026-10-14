
import { useState } from 'react';
import type { FormEvent } from 'react';
import { login } from '../../services/authApi';
import type { LoginResponse } from '../../services/authApi';

type LoginFormProps = {
  onLoginSuccess: (result: LoginResponse) => void;
};

export default function LoginForm({
  onLoginSuccess,
}: LoginFormProps) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const result = await login({ email, password });
      onLoginSuccess(result);
    } catch (err) {
      setError(
        err instanceof Error ? err.message : 'Unable to log in.',
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="login-page">
      <section className="login-card">
        <div className="login-brand">
          <h1>IMS</h1>
          <p>Inventory Management System</p>
        </div>

        <h2>Welcome back</h2>
        <p className="login-description">
          Sign in to manage your inventory.
        </p>

        <form onSubmit={handleSubmit} className="login-form">
          <label>
            Email
            <input
              type="email"
              autoComplete="username"
              required
              value={email}
              onChange={(event) => setEmail(event.target.value)}
            />
          </label>

          <label>
            Password
            <input
              type="password"
              autoComplete="current-password"
              required
              value={password}
              onChange={(event) => setPassword(event.target.value)}
            />
          </label>

          {error && (
            <p className="form-error" role="alert">
              {error}
            </p>
          )}

          <button
            type="submit"
            className="primary-button"
            disabled={loading}
          >
            {loading ? 'Signing in...' : 'Sign in'}
          </button>
        </form>
      </section>
    </main>
  );
}
