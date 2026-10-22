import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { api } from '../lib/api';

const TYPES = [
  { path: 'actors', label: 'Actors', manager: 'CASTING_MANAGER' },
  { path: 'equipment', label: 'Equipment', manager: 'EQUIPMENT_MANAGER' },
  { path: 'locations', label: 'Locations', manager: 'LOCATION_MANAGER' },
];

const OVERSEERS = ['DIRECTOR', 'LINE_PRODUCER'];

export default function Production() {
  const { user, token } = useAuth();
  const [tab, setTab] = useState(TYPES[0]);
  const [items, setItems] = useState([]);
  const [scenes, setScenes] = useState([]);
  const [sceneId, setSceneId] = useState('');
  const [callSheet, setCallSheet] = useState(null);
  const [form, setForm] = useState({ name: '', detail: '' });
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');

  const canManage = OVERSEERS.includes(user.role) || user.role === tab.manager;

  async function loadItems() {
    try {
      setItems(await api(`/resources/${tab.path}`, { token }));
    } catch (e) {
      setError(e.message);
    }
  }

  async function loadCallSheet(id) {
    if (!id) {
      setCallSheet(null);
      return;
    }
    try {
      setCallSheet(await api(`/scenes/${id}/callsheet`, { token }));
    } catch (e) {
      setError(e.message);
    }
  }

  useEffect(() => {
    setError('');
    setNotice('');
    loadItems();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tab]);

  useEffect(() => {
    api('/scenes', { token }).then(setScenes).catch((e) => setError(e.message));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    loadCallSheet(sceneId);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [sceneId]);

  async function create(e) {
    e.preventDefault();
    setError('');
    try {
      await api(`/resources/${tab.path}`, { method: 'POST', body: form, token });
      setForm({ name: '', detail: '' });
      loadItems();
    } catch (err) {
      setError(err.message);
    }
  }

  async function removeItem(id) {
    if (!confirm('Delete this from the library? It will also be removed from scenes.')) return;
    try {
      await api(`/resources/${tab.path}/${id}`, { method: 'DELETE', token });
      loadItems();
    } catch (err) {
      setError(err.message);
    }
  }

  async function attach(item) {
    setError('');
    setNotice('');
    if (!sceneId) {
      setError('Pick a scene first.');
      return;
    }
    try {
      await api(`/scenes/${sceneId}/resources`, {
        method: 'POST',
        body: { type: tab.path, resourceId: item.id },
        token,
      });
      setNotice(`${item.name} booked for this scene.`);
      loadCallSheet(sceneId);
    } catch (err) {
      setError(err.message);
    }
  }

  async function detach(item) {
    try {
      await api(`/scenes/${sceneId}/resources/${tab.path}/${item.id}`, { method: 'DELETE', token });
      loadCallSheet(sceneId);
    } catch (err) {
      setError(err.message);
    }
  }

  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value });

  return (
    <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
      {/* Library */}
      <section className="lg:col-span-6">
        <h1 className="text-3xl mb-6">Production</h1>

        <div className="flex gap-6 mb-6 text-sm">
          {TYPES.map((t) => (
            <button
              key={t.path}
              onClick={() => setTab(t)}
              className={`pb-1 border-b-2 transition ${
                tab.path === t.path ? 'border-accent text-accent' : 'border-transparent hover:text-accent'
              }`}
            >
              {t.label}
            </button>
          ))}
        </div>

        {canManage && (
          <form onSubmit={create} className="card space-y-4 mb-6">
            <div className="grid grid-cols-2 gap-6">
              <div>
                <label className="label">Name</label>
                <input className="input" value={form.name} onChange={set('name')} required />
              </div>
              <div>
                <label className="label">Detail</label>
                <input className="input" value={form.detail} onChange={set('detail')} />
              </div>
            </div>
            <button className="btn btn-accent">Add {tab.label.toLowerCase().replace(/s$/, '')}</button>
          </form>
        )}

        {!canManage && (
          <p className="text-sm text-ink/60 mb-6">
            Your role can view {tab.label.toLowerCase()} but not change them.
          </p>
        )}

        {error && <p className="text-sm text-accent mb-4">{error}</p>}
        {notice && <p className="text-sm text-ink/70 mb-4">{notice}</p>}

        <ul className="divide-y divide-ink/15 border-y border-ink/15">
          {items.map((item) => (
            <li key={item.id} className="py-3 flex items-center justify-between gap-4">
              <div>
                <div>{item.name}</div>
                <div className="text-xs text-ink/60">{item.detail}</div>
              </div>
              <div className="flex gap-2">
                {sceneId && (
                  <button className="btn text-xs" onClick={() => attach(item)}>Book</button>
                )}
                {canManage && (
                  <button className="btn text-xs" onClick={() => removeItem(item.id)}>Remove</button>
                )}
              </div>
            </li>
          ))}
          {items.length === 0 && <li className="py-3 text-sm text-ink/60">Nothing here yet.</li>}
        </ul>
      </section>

      {/* Scene and call sheet */}
      <section className="lg:col-span-6">
        <h2 className="text-xl mb-4">Call sheet</h2>

        <label className="label">Scene</label>
        <select className="input bg-paper mb-8" value={sceneId} onChange={(e) => setSceneId(e.target.value)}>
          <option value="">Select a scene</option>
          {scenes.map((s) => (
            <option key={s.id} value={s.id}>
              Scene {s.sceneNumber}: {s.title}
            </option>
          ))}
        </select>

        {callSheet && (
          <div className="card space-y-6">
            <div>
              <div className="text-xs uppercase tracking-widest text-ink/60">Scene {callSheet.scene.sceneNumber}</div>
              <div className="text-2xl">{callSheet.scene.title}</div>
              <div className="text-sm text-ink/70 mt-1">
                {callSheet.scene.shootDate || 'No date'} · {callSheet.scene.timeSlot || 'No slot'} ·{' '}
                {callSheet.scene.locationName || 'No location'}
              </div>
            </div>

            {TYPES.map((t) => (
              <div key={t.path}>
                <div className="label">{t.label}</div>
                <ul className="text-sm space-y-1">
                  {callSheet[t.path].map((r) => (
                    <li key={r.id} className="flex justify-between items-center">
                      <span>
                        {r.name} <span className="text-ink/60">{r.detail}</span>
                      </span>
                      {canManage && (
                        <button className="text-xs text-accent hover:underline" onClick={() => detach(r)}>
                          remove
                        </button>
                      )}
                    </li>
                  ))}
                  {callSheet[t.path].length === 0 && <li className="text-ink/50">None booked</li>}
                </ul>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}