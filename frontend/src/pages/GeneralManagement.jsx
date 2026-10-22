import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { api } from '../lib/api';

const ROLES = [
  'DIRECTOR', 'LINE_PRODUCER', 'SCRIPTWRITER', 'CASTING_MANAGER',
  'STYLIST', 'EQUIPMENT_MANAGER', 'LOCATION_MANAGER',
];

export default function GeneralManagement() {
  const { user, token } = useAuth();
  const isDirector = user.role === 'DIRECTOR';

  const [users, setUsers] = useState([]);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');

  async function load() {
    try {
      setUsers(await api('/users', { token }));
    } catch (e) {
      setError(e.message);
    }
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function changeRole(id, role) {
    setError('');
    setNotice('');
    try {
      await api(`/users/${id}/role`, { method: 'PUT', body: { role }, token });
      setNotice('Role updated. The user must log out and log in again to apply it.');
      await load();
    } catch (e) {
      setError(e.message);
    }
  }

  const countByRole = ROLES.map((r) => ({
    role: r,
    count: users.filter((u) => u.role === r).length,
  }));

  return (
    <div className="space-y-10">
      <section>
        <h1 className="text-3xl">General Management</h1>
        <p className="text-sm text-ink/70 mt-2">
          {isDirector
            ? 'You can assign roles to everyone on the team.'
            : 'You can view the team. Only directors can change roles.'}
        </p>
      </section>

      <section className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-7 gap-4">
        {countByRole.map(({ role, count }) => (
          <div key={role} className="card p-4">
            <div className="text-[10px] uppercase tracking-widest text-ink/60">
              {role.replace('_', ' ')}
            </div>
            <div className="text-2xl mt-1">{count}</div>
          </div>
        ))}
      </section>

      {error && <p className="text-sm text-accent">{error}</p>}
      {notice && <p className="text-sm text-ink/70">{notice}</p>}

      <section className="overflow-x-auto">
        <table className="w-full text-sm border-collapse">
          <thead>
            <tr className="border-b border-ink/30 text-left text-xs uppercase tracking-widest text-ink/60">
              <th className="py-3 pr-4">Name</th>
              <th className="py-3 pr-4">Username</th>
              <th className="py-3 pr-4">Email</th>
              <th className="py-3 pr-4">Role</th>
            </tr>
          </thead>
          <tbody>
            {users.map((u) => (
              <tr key={u.id} className="border-b border-ink/10">
                <td className="py-3 pr-4">{u.fullName}</td>
                <td className="py-3 pr-4 text-ink/70">{u.username}</td>
                <td className="py-3 pr-4 text-ink/70">{u.email}</td>
                <td className="py-3 pr-4">
                  {isDirector ? (
                    <select
                      className="bg-paper border border-ink/30 px-2 py-1"
                      value={u.role}
                      onChange={(e) => changeRole(u.id, e.target.value)}
                    >
                      {ROLES.map((r) => <option key={r} value={r}>{r.replace('_', ' ')}</option>)}
                    </select>
                  ) : (
                    u.role.replace('_', ' ')
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
    </div>
  );
}