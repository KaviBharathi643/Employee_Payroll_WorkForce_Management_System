export default function AuthAlert({ type = 'error', children }) {
  if (!children) {
    return null;
  }

  const styles =
    type === 'success'
      ? 'border-green-200 bg-green-50 text-green-800'
      : 'border-red-200 bg-red-50 text-red-800';

  return (
    <div className={`rounded-lg border px-3 py-2 text-sm ${styles}`} role="alert">
      {children}
    </div>
  );
}
