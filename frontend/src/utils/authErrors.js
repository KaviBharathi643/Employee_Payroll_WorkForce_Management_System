/**
 * Extract a user-facing message from an Axios error or generic Error.
 * @param {unknown} error
 * @param {string} [fallback]
 */
export function getErrorMessage(error, fallback = 'Something went wrong') {
  if (error && typeof error === 'object' && 'response' in error) {
    const data = error.response?.data;
    if (data?.message) {
      return data.message;
    }
  }
  if (error instanceof Error && error.message) {
    return error.message;
  }
  return fallback;
}
