export default function Receipt({ scene }) {
  const printedAt = new Date().toLocaleString('en-IN');

  const rows = [
    ['Scene No.', scene.sceneNumber],
    ['Date', scene.shootDate || 'TBD'],
    ['Time slot', scene.timeSlot || 'TBD'],
    ['Location', scene.locationName || 'TBD'],
    ['Created by', scene.createdBy],
  ];

  return (
    <div id="receipt" className="bg-white font-mono text-sm w-full max-w-sm mx-auto p-6 shadow-md border border-ink/10">
      <div className="text-center">
        <div className="font-bold tracking-[0.25em]">FILM-MAX</div>
        <div className="text-xs mt-1">SCENE CALL RECEIPT</div>
      </div>

      <div className="border-t border-dashed border-ink/40 my-4" />

      <div className="text-center text-base font-bold uppercase">{scene.title}</div>

      <div className="border-t border-dashed border-ink/40 my-4" />

      <div className="space-y-2">
        {rows.map(([label, value]) => (
          <div key={label} className="flex justify-between gap-4">
            <span className="text-ink/60">{label}</span>
            <span className="text-right">{String(value)}</span>
          </div>
        ))}
      </div>

      <div className="border-t border-dashed border-ink/40 my-4" />

      <div className="text-xs text-ink/70 leading-relaxed">
        <div className="font-bold mb-1">Description</div>
        <p>{scene.description || 'No description provided.'}</p>
      </div>

      <div className="border-t border-dashed border-ink/40 my-4" />

      <div className="text-center text-xs text-ink/60">
        <div>Printed {printedAt}</div>
        <div className="mt-2">*** END OF RECEIPT ***</div>
      </div>
    </div>
  );
}