import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { api } from '../lib/api';
import Receipt from '../components/Receipt';

const WRITE_ROLES = ['DIRECTOR', 'LINE_PRODUCER', 'SCRIPTWRITER'];
const SLOTS = ['MORNING', 'AFTERNOON', 'EVENING', 'NIGHT'];
const EMPTY = {
  sceneNumber: '',
  title: '',
  description: '',
  locationName: '',
  shootDate: '',
  timeSlot: 'MORNING',
};

export default function SceneManagement() {
  const { user, token } = useAuth();
  const canWrite = WRITE_ROLES.includes(user.role);

  const [scenes, setScenes] = useState([]);
  const [selected, setSelected] = useState(null);
  const [form, setForm] = useState(EMPTY);
  const [error, setError] = useState('');

  async function load() {
    try {
      setScenes(await api('/scenes', { token }));
    } catch (e) {
      setError(e.message);
    }
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  function pick(scene) {
    setSelected(scene);
    setError('');
    setForm({
      sceneNumber: scene.sceneNumber,
      title: scene.title,
      description: scene.description ?? '',
      locationName: scene.locationName ?? '',
      shootDate: scene.shootDate ?? '',
      timeSlot: scene.timeSlot ?? 'MORNING',
    });
  }

  function newScene() {
    setSelected(null);
    setError('');
    setForm({ ...EMPTY, sceneNumber: (scenes.at(-1)?.sceneNumber ?? 0) + 1 });
  }

  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value });

  async function save(e) {
    e.preventDefault();
    setError('');
    const body = {
      ...form,
      sceneNumber: Number(form.sceneNumber),
      shootDate: form.shootDate || null,
    };
    try {
      const saved = selected
        ? await api(`/scenes/${selected.id}`, { method: 'PUT', body, token })
        : await api('/scenes', { method: 'POST', body, token });
      await load();
      setSelected(saved);
    } catch (err) {
      setError(err.message);
    }
  }

  async function remove() {
    if (!selected || !confirm(`Delete scene ${selected.sceneNumber}?`)) return;
    try {
      await api(`/scenes/${selected.id}`, { method: 'DELETE', token });
      setSelected(null);
      setForm(EMPTY);
      await load();
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
      {/* Scene list */}
      <aside className="lg:col-span-3 no-print">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl">Scenes</h2>
          {canWrite && (
            <button className="btn text-xs" onClick={newScene}>+ New</button>
          )}
        </div>
        <ul className="divide-y divide-ink/15 border-y border-ink/15">
          {scenes.map((s) => (
            <li key={s.id}>
              <button
                onClick={() => pick(s)}
                className={`w-full text-left py-3 px-2 hover:bg-white/60 ${
                  selected?.id === s.id ? 'bg-white/80 text-accent' : ''
                }`}
              >
                <div className="text-xs text-ink/60">Scene {s.sceneNumber}</div>
                <div>{s.title}</div>
              </button>
            </li>
          ))}
          {scenes.length === 0 && <li className="py-3 text-sm text-ink/60">No scenes yet.</li>}
        </ul>
      </aside>

      {/* Editor */}
      <section className="lg:col-span-5 no-print">
        <h2 className="text-xl mb-4">
          {selected ? `Edit scene ${selected.sceneNumber}` : 'New scene'}
        </h2>

        {!canWrite && (
          <p className="text-sm text-ink/60 mb-4">
            Your role ({user.role.replace('_', ' ').toLowerCase()}) can view scenes but not change them.
          </p>
        )}

        <form onSubmit={save} className="card space-y-5">
          <fieldset disabled={!canWrite} className="space-y-5">
            <div className="grid grid-cols-2 gap-6">
              <div>
                <label className="label">Scene number</label>
                <input type="number" min="1" className="input" value={form.sceneNumber} onChange={set('sceneNumber')} required />
              </div>
              <div>
                <label className="label">Time slot</label>
                <select className="input bg-paper" value={form.timeSlot} onChange={set('timeSlot')}>
                  {SLOTS.map((s) => <option key={s}>{s}</option>)}
                </select>
              </div>
            </div>
            <div>
              <label className="label">Title</label>
              <input className="input" value={form.title} onChange={set('title')} required />
            </div>
            <div className="grid grid-cols-2 gap-6">
              <div>
                <label className="label">Location</label>
                <input className="input" value={form.locationName} onChange={set('locationName')} />
              </div>
              <div>
                <label className="label">Shoot date</label>
                <input type="date" className="input" value={form.shootDate} onChange={set('shootDate')} />
              </div>
            </div>
            <div>
              <label className="label">Description</label>
              <textarea rows="4" className="input resize-none border border-ink/30 p-2" value={form.description} onChange={set('description')} />
            </div>

            {error && <p className="text-sm text-accent">{error}</p>}

            {canWrite && (
              <div className="flex gap-3 pt-2">
                <button className="btn btn-accent">{selected ? 'Save changes' : 'Create scene'}</button>
                {selected && (
                  <button type="button" className="btn" onClick={remove}>Delete</button>
                )}
              </div>
            )}
          </fieldset>
        </form>
      </section>

      {/* Receipt */}
      <section className="lg:col-span-4">
        <div className="flex items-center justify-between mb-4 no-print">
          <h2 className="text-xl">Call receipt</h2>
          {selected && (
            <button className="btn text-xs" onClick={() => window.print()}>Print</button>
          )}
        </div>
        {selected ? (
          <Receipt scene={selected} />
        ) : (
          <p className="text-sm text-ink/60 no-print">Select a scene to see its receipt.</p>
        )}
      </section>
    </div>
  );
}