import PageHeader from '../../components/common/PageHeader';
import MyLeavePanel from './MyLeavePanel';

export default function EmployeeLeavePage() {
  return (
    <div>
      <PageHeader
        title="Leave"
        description="View your leave balance, apply for leave, and manage pending requests."
      />
      <MyLeavePanel canApply />
    </div>
  );
}
