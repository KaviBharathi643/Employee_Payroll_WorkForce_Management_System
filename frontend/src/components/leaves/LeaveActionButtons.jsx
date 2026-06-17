import { useState } from 'react';
import leaveService from '../../services/leaveService';
import { getErrorMessage } from '../../utils/authErrors';

export default function LeaveActionButtons({
  leave,
  mode = 'own',
  onUpdated,
  onError,
}) {
  const [busy, setBusy] = useState(false);

  const runAction = async (action, confirmMessage) => {
    if (confirmMessage && !window.confirm(confirmMessage)) {
      return;
    }
    setBusy(true);
    onError?.('');
    try {
      const { message } = await action();
      onUpdated?.(message);
    } catch (err) {
      onError?.(getErrorMessage(err, 'Action failed'));
    } finally {
      setBusy(false);
    }
  };

  if (mode === 'own') {
    if (leave.status !== 'PENDING') {
      return null;
    }
    return (
      <button
        type="button"
        disabled={busy}
        onClick={() =>
          runAction(
            () => leaveService.cancelLeave(leave.leaveId),
            'Cancel this leave request?',
          )
        }
        className="text-sm font-medium text-red-700 hover:underline disabled:opacity-60"
      >
        {busy ? 'Cancelling...' : 'Cancel'}
      </button>
    );
  }

  const canApproveReject = leave.status === 'PENDING';
  const canConvert =
    leave.status === 'APPROVED' && leave.leaveType !== 'UNPAID';

  if (!canApproveReject && !canConvert) {
    return null;
  }

  return (
    <div className="flex flex-wrap gap-2">
      {canApproveReject && (
        <>
          <button
            type="button"
            disabled={busy}
            onClick={() =>
              runAction(
                () => leaveService.approveLeave(leave.leaveId),
                'Approve this leave request?',
              )
            }
            className="rounded-lg bg-green-700 px-2.5 py-1 text-xs font-medium text-white hover:bg-green-800 disabled:opacity-60"
          >
            Approve
          </button>
          <button
            type="button"
            disabled={busy}
            onClick={() =>
              runAction(
                () => leaveService.rejectLeave(leave.leaveId),
                'Reject this leave request?',
              )
            }
            className="rounded-lg bg-red-700 px-2.5 py-1 text-xs font-medium text-white hover:bg-red-800 disabled:opacity-60"
          >
            Reject
          </button>
        </>
      )}
      {canConvert && (
        <button
          type="button"
          disabled={busy}
          onClick={() =>
            runAction(
              () => leaveService.convertToUnpaid(leave.leaveId),
              'Convert this approved leave to unpaid?',
            )
          }
          className="rounded-lg border border-slate-300 px-2.5 py-1 text-xs font-medium text-slate-700 hover:bg-slate-100 disabled:opacity-60"
        >
          Convert to unpaid
        </button>
      )}
    </div>
  );
}
