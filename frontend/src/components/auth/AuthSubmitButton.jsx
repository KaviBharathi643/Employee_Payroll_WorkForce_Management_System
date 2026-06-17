export default function AuthSubmitButton({ children, loading, loadingText, disabled }) {
  return (
    <button
      type="submit"
      disabled={disabled || loading}
      className="w-full rounded-lg bg-slate-900 px-4 py-2.5 text-sm font-medium text-white hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-60"
    >
      {loading ? loadingText || 'Please wait...' : children}
    </button>
  );
}
