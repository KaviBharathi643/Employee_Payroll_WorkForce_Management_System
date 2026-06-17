export default function AuthTextField({
  id,
  label,
  type = 'text',
  value,
  onChange,
  required = true,
  autoComplete,
  placeholder,
  maxLength,
  pattern,
  inputMode,
  hint,
}) {
  return (
    <div>
      <label htmlFor={id} className="mb-1 block text-sm font-medium text-slate-700">
        {label}
      </label>
      <input
        id={id}
        type={type}
        required={required}
        value={value}
        onChange={onChange}
        autoComplete={autoComplete}
        placeholder={placeholder}
        maxLength={maxLength}
        pattern={pattern}
        inputMode={inputMode}
        className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-500 focus:ring-1 focus:ring-slate-500"
      />
      {hint && <p className="mt-1 text-xs text-slate-500">{hint}</p>}
    </div>
  );
}
