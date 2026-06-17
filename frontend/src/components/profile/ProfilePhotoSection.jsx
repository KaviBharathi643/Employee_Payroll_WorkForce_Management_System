import { useRef, useState } from 'react';
import Alert from '../common/Alert';
import ProfileAvatar from './ProfileAvatar';
import profileService from '../../services/profileService';
import { getErrorMessage } from '../../utils/authErrors';

const MAX_BYTES = 2 * 1024 * 1024;
const ALLOWED_TYPES = ['image/jpeg', 'image/jpg', 'image/png'];

export default function ProfilePhotoSection({ profile, onUpdated }) {
  const inputRef = useRef(null);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [busy, setBusy] = useState(false);

  const validateFile = (file) => {
    if (!ALLOWED_TYPES.includes(file.type)) {
      return 'Only JPG, JPEG, and PNG files are allowed';
    }
    if (file.size > MAX_BYTES) {
      return 'File must be 2 MB or smaller';
    }
    return null;
  };

  const handleUpload = async (event) => {
    const file = event.target.files?.[0];
    event.target.value = '';
    if (!file) {
      return;
    }

    const validationError = validateFile(file);
    if (validationError) {
      setError(validationError);
      return;
    }

    setError('');
    setNotice('');
    setBusy(true);
    try {
      const { message, data } = await profileService.uploadPhoto(file);
      setNotice(message || 'Photo uploaded');
      onUpdated(data);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to upload photo'));
    } finally {
      setBusy(false);
    }
  };

  const handleDelete = async () => {
    if (!profile?.profilePhotoUrl) {
      return;
    }
    if (!window.confirm('Remove your profile photo?')) {
      return;
    }

    setError('');
    setNotice('');
    setBusy(true);
    try {
      const { message, data } = await profileService.deletePhoto();
      setNotice(message || 'Photo removed');
      onUpdated(data);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to remove photo'));
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="flex flex-col items-start gap-4 sm:flex-row sm:items-center">
      <ProfileAvatar
        userId={profile.userId}
        fullName={profile.fullName}
        profilePhotoUrl={profile.profilePhotoUrl}
      />
      <div className="space-y-2">
        <div className="flex flex-wrap gap-2">
          <button
            type="button"
            disabled={busy}
            onClick={() => inputRef.current?.click()}
            className="rounded-lg bg-slate-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-slate-800 disabled:opacity-60"
          >
            {busy ? 'Working...' : 'Upload photo'}
          </button>
          {profile.profilePhotoUrl && (
            <button
              type="button"
              disabled={busy}
              onClick={handleDelete}
              className="rounded-lg border border-slate-300 px-3 py-1.5 text-sm text-slate-700 hover:bg-slate-100 disabled:opacity-60"
            >
              Remove
            </button>
          )}
        </div>
        <p className="text-xs text-slate-500">JPG, JPEG, or PNG. Max 2 MB.</p>
        {notice && <Alert type="success">{notice}</Alert>}
        {error && <Alert>{error}</Alert>}
        <input
          ref={inputRef}
          type="file"
          accept=".jpg,.jpeg,.png,image/jpeg,image/png"
          className="hidden"
          onChange={handleUpload}
        />
      </div>
    </div>
  );
}
