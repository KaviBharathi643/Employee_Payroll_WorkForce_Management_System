/**
 * Unwrap standard ApiResponse envelope from backend.
 * @template T
 * @param {import('axios').AxiosResponse} response
 * @returns {T}
 */
export function unwrap(response) {
  const body = response.data;
  if (!body?.success) {
    throw new Error(body?.message || 'Request failed');
  }
  return body.data;
}

/**
 * @param {import('axios').AxiosResponse} response
 * @returns {{ message: string, data: unknown }}
 */
export function unwrapWithMessage(response) {
  const body = response.data;
  if (!body?.success) {
    throw new Error(body?.message || 'Request failed');
  }
  return { message: body.message, data: body.data };
}

/**
 * @param {import('axios').AxiosResponse<Blob>} response
 * @param {string} fallbackName
 */
export function downloadBlob(response, fallbackName) {
  const disposition = response.headers['content-disposition'] || '';
  const match = disposition.match(/filename="?([^"]+)"?/);
  const filename = match?.[1] || fallbackName;
  const url = URL.createObjectURL(response.data);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  link.click();
  URL.revokeObjectURL(url);
}
