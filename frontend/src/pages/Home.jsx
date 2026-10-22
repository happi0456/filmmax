import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { api } from '../lib/api';

export default function Home() {
  const { user, token } = useAuth();
  const [scenes, setScenes] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    api('/scenes', { token }).then(setScenes).catch((e) => setError(e.message));
  }, [token]);

  const today = new Date().toISOString().slice(0, 10);
  const upcoming = scenes
    .filter((s) => s.shootDate && s.shootDate >= today)
    .sort((a, b) => a.shootDate.localeCompare(b.shootDate));
  const locations = new Set(scenes.map((s) => s.locationName).filter(Boolean));

  return (
    <div className="space-y-12">
      <section>
        <p className="text-sm uppercase tracking-widest text-accent">Welcome back</p>
        <h1 className="text-4xl mt-2">Good to see you, {user.username}.</h1>
        <p className="mt-4 max-w-2xl text-ink/70 leading-relaxed">
          FILM-MAX keeps every department on the same page: scripts, casting, styling, equipment, and
          locations. Conflicts are caught before they reach the call sheet.
        </p>
      </section>

      <section className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="card">
          <div className="label">Scenes</div>
          <div className="text-4xl">{scenes.length}</div>
        </div>
        <div className="card">
          <div className="label">Locations</div>
          <div className="text-4xl">{locations.size}</div>
        </div>
        <div className="card">
          <div className="label">Next shoot</div>
          <div className="text-2xl">{upcoming[0]?.shootDate ?? 'None scheduled'}</div>
          {upcoming[0] && <div className="text-sm text-ink/60 mt-1">Scene {upcoming[0].sceneNumber}: {upcoming[0].title}</div>}
        </div>
      </section>

      {error && <p className="text-accent text-sm">{error}</p>}

      <section className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="card">
          <h2 className="text-xl mb-2">Scene Management</h2>
          <p className="text-sm text-ink/70 leading-relaxed">
            Write and schedule scenes, then print any scene as a call receipt for the crew.
          </p>
        </div>
        <div className="card">
          <h2 className="text-xl mb-2">General Management</h2>
          <p className="text-sm text-ink/70 leading-relaxed">
            Review the team and assign roles. Directors can change access, and line producers can view the roster.
          </p>
        </div>
      </section>
    </div>
  );
}