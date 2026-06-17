import { useEffect, useState } from 'react';
import profileService from '../../services/profileService';

function initials(name) {
  if (!name) {
    return '?';
  }
  return name
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0].toUpperCase())
    .join('');
}

export default function ProfileAvatar({ userId, fullName, profilePhotoUrl, size = 'lg' }) {
  const [src, setSrc] = useState(null);

  const sizeClass =
    size === 'sm' ? 'h-12 w-12 text-sm' : 'h-24 w-24 text-2xl';

  useEffect(() => {
    let active = true;
    let objectUrl = null;

    if (!userId || !profilePhotoUrl) {
      setSrc(null);
      return undefined;
    }

    (async () => {
      try {
        const response = await profileService.fetchPhotoBlob(userId);
        objectUrl = URL.createObjectURL(response.data);
        if (active) {
          setSrc(objectUrl);
        }
      } catch {
        if (active) {
          setSrc(null);
        }
      }
    })();

    return () => {
      active = false;
      if (objectUrl) {
        URL.revokeObjectURL(objectUrl);
      }
    };
  }, [userId, profilePhotoUrl]);

  if (src) {
    return (
      <img
        src={src}
        alt={fullName ? `${fullName} profile` : 'Profile'}
        className={`${sizeClass} rounded-full object-cover ring-2 ring-slate-200`}
      />
    );
  }

  return (
    <div
      className={`${sizeClass} flex items-center justify-center rounded-full bg-slate-200 font-semibold text-slate-600 ring-2 ring-slate-200`}
    >
      {initials(fullName)}
    </div>
  );
}
