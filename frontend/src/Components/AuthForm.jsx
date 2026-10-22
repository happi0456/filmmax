import { useState } from 'react';
import { useAuth } from '../context/AuthContext';

export default function AuthForm() {
  const { login, register } = useAuth();
  const [mode, setMode] = useState('login');
  const [form, setForm] = useState({ username: '', email: '', password: '', fullName: '' });
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value });

  async function submit(e) {
    e.preventDefault();
    setBusy(true);
    setError('');
    try {
      if (mode === 'login') await login(form.username, form.password);
      else await register(form);
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center px-6">
      <div className="w-full max-w-sm">
        <div className="text-center mb-10">
          <div className="text-2xl font-bold tracking-[0.3em]">FILM-MAX</div>
          <div className="text-sm text-ink/60 mt-2">Production management</div>
        </div>

        <form onSubmit={submit} className="card space-y-6">
          <div>
            <label className="label">Username</label>
            <input className="input" value={form.username} onChange={set('username')} required />
          </div>

          {mode === 'register' && (
            <>
              <div>
                <label className="label">Full name</label>
                <input className="input" value={form.fullName} onChange={set('fullName')} required />
              </div>
              <div>
                <label className="label">Email</label>
                <input type="email" className="input" value={form.email} onChange={set('email')} required />
              </div>
            </>
          )}

          <div>
            <label className="label">Password</label>
            <input type="password" className="input" value={form.password} onChange={set('password')} required />
          </div>

          {error && <p className="text-sm text-accent">{error}</p>}

          <button className="btn btn-accent w-full" disabled={busy}>
            {mode === 'login' ? 'Log in' : 'Create account'}
          </button>

          <button
            type="button"
            className="w-full text-sm text-ink/60 hover:text-accent"
            onClick={() => {
              setError('');
              setMode(mode === 'login' ? 'register' : 'login');
            }}
          >
            {mode === 'login' ? 'New here? Create an account' : 'Already have an account? Log in'}
          </button>
        </form>
      </div>
    </div>
  );
}