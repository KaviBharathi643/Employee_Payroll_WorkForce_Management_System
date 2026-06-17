export default function FormField({
  id,
  label,
  type = 'text',
  value,
  onChange,
  required = true,
  placeholder,
  hint,
  min,
  step,
  as = 'input',
  rows = 3,
}) {
  const className =
    'w-full rounded-lg border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-500 focus:ring-1 focus:ring-slate-500';

  return (
    <div>
      <label htmlFor={id} className="mb-1 block text-sm font-medium text-slate-700">
        {label}
      </label>
      {as === 'textarea' ? (
        <textarea
          id={id}
          required={required}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          rows={rows}
          className={className}
        />
      ) : (
        <input
          id={id}
          type={type}
          required={required}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          min={min}
          step={step}
          className={className}
        />
      )}
      {hint && <p className="mt-1 text-xs text-slate-500">{hint}</p>}
    </div>
  );
}
