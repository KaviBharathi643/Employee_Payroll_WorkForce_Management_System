import { useEffect, useState } from 'react';
import Alert from '../common/Alert';
import DetailGrid from '../common/DetailGrid';
import Modal from '../common/Modal';
import payslipService from '../../services/payslipService';
import { getErrorMessage } from '../../utils/authErrors';
import { formatCurrency, formatDateTime, formatMonthYear } from '../../utils/formatters';

export default function PayslipDetailModal({ payslipId, onClose }) {
  const [payslip, setPayslip] = useState(null);
  const [loading, setLoading] = useState(true);
  const [downloading, setDownloading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;
    (async () => {
      setLoading(true);
      setError('');
      try {
        const data = await payslipService.getDetails(payslipId);
        if (active) {
          setPayslip(data);
        }
      } catch (err) {
        if (active) {
          setError(getErrorMessage(err, 'Failed to load payslip'));
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    })();
    return () => {
      active = false;
    };
  }, [payslipId]);

  const handleDownload = async () => {
    setDownloading(true);
    setError('');
    try {
      const filename = payslip?.payslipNumber ? `${payslip.payslipNumber}.pdf` : 'payslip.pdf';
      await payslipService.download(payslipId, filename);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to download payslip'));
    } finally {
      setDownloading(false);
    }
  };

  const items = payslip
    ? [
        { label: 'Payslip number', value: payslip.payslipNumber },
        { label: 'Employee', value: `${payslip.employeeName} (${payslip.employeeCode})` },
        { label: 'Period', value: formatMonthYear(payslip.payrollYear, payslip.payrollMonth) },
        { label: 'Department', value: payslip.department },
        { label: 'Designation', value: payslip.designation },
        { label: 'Basic salary', value: formatCurrency(payslip.basicSalary) },
        { label: 'Bonus', value: formatCurrency(payslip.bonus) },
        { label: 'PF amount', value: formatCurrency(payslip.pfAmount) },
        { label: 'Unpaid leave days', value: payslip.unpaidLeaveCount },
        { label: 'Unpaid deduction', value: formatCurrency(payslip.unpaidLeaveDeduction) },
        { label: 'Final salary', value: formatCurrency(payslip.finalSalary) },
        { label: 'Bank', value: payslip.bankName },
        { label: 'Account', value: payslip.maskedAccountNumber },
        { label: 'IFSC', value: payslip.ifscCode },
        { label: 'Generated', value: formatDateTime(payslip.generatedDate) },
      ]
    : [];

  return (
    <Modal title="Payslip details" onClose={onClose} wide>
      {error && (
        <div className="mb-4">
          <Alert>{error}</Alert>
        </div>
      )}
      {loading ? (
        <p className="text-sm text-slate-600">Loading payslip...</p>
      ) : payslip ? (
        <>
          <DetailGrid items={items} />
          <div className="mt-6">
            <button
              type="button"
              disabled={downloading || !payslip.pdfGenerated}
              onClick={handleDownload}
              className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {downloading ? 'Downloading...' : 'Download PDF'}
            </button>
            {!payslip.pdfGenerated && (
              <p className="mt-2 text-xs text-slate-500">PDF is not available for this payslip yet.</p>
            )}
          </div>
        </>
      ) : (
        <p className="text-sm text-slate-600">Payslip not found.</p>
      )}
    </Modal>
  );
}
