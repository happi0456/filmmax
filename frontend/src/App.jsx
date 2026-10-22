import { useState } from 'react';
import { useAuth } from './context/AuthContext';
import AuthForm from './components/AuthForm';
import Home from './pages/Home';
import SceneManagement from './pages/SceneManagement';
import Production from './pages/Production';
import GeneralManagement from './pages/GeneralManagement';

const TABS = [
  { id: 'home', label: 'Home', Page: Home },
  { id: 'scenes', label: 'Scene Management', Page: SceneManagement },
  { id: 'production', label: 'Production', Page: Production },
  { id: 'general', label: 'General Management', Page: GeneralManagement },
];

export default function App() {
  const { user, logout } = useAuth();
  const [tab, setTab] = useState('home');

  if (!user) return <AuthForm />;

  const Page = TABS.find((t) => t.id === tab).Page;

  return (
    <div className="min-h-screen">
      <header className="no-print border-b border-ink/20 px-6 py-4 flex flex-wrap items-center justify-between gap-4">
        <div>
          <div className="text-lg font-bold tracking-[0.3em]">FILM-MAX</div>
          <div className="text-xs text-ink/60">
            {user.username} · {user.role.replace('_', ' ')}
          </div>
        </div>

        <nav className="flex flex-wrap gap-6 text-sm">
          {TABS.map((t) => (
            <button
              key={t.id}
              onClick={() => setTab(t.id)}
              className={`pb-1 border-b-2 transition ${
                tab === t.id ? 'border-accent text-accent' : 'border-transparent hover:text-accent'
              }`}
            >
              {t.label}
            </button>
          ))}
        </nav>

        <button className="btn" onClick={logout}>Log out</button>
      </header>

      <main className="max-w-6xl mx-auto px-6 py-10">
        <Page />
      </main>
    </div>
  );
}