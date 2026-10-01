function formatDate(value) {
  if (!value) return "Not edited yet";

  return value.replace("T", " ");
}

function formatBirthDate(value) {
  if (!value) return "—";

  const date = new Date(`${value}T00:00:00`);
  if (Number.isNaN(date.getTime())) return value;

  return date.toLocaleDateString();
}

function MemberViewModal({ open, onClose, member }) {
  if (!open || !member) return null;

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-xl p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto">
        <div className="flex justify-between items-start mb-5">
          <div>
            <h2 className="text-xl font-bold">Member Details</h2>
            <p className="text-sm text-gray-500 mt-1">
              Membership No.: {member.membershipNumber}
            </p>
          </div>

          <button
            type="button"
            onClick={onClose}
            className="text-gray-500 hover:text-gray-800 text-xl"
            aria-label="Close"
          >
            ×
          </button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Detail label="Full Name" value={member.fullName} />
          <Detail label="National Code" value={member.nationalCode} />
          <Detail label="Birth Date" value={formatBirthDate(member.birthDate)} />
          <Detail label="Membership Type" value={member.membershipType} />
          <Detail label="Phone" value={member.phone} />
          <Detail label="Postal Code" value={member.postalCode} />
          <Detail label="Status" value={member.status} />
          <Detail label="Membership Number" value={member.membershipNumber} />
          <Detail label="Created At" value={formatDate(member.createdAt)} />
          <Detail label="Last Updated" value={formatDate(member.updatedAt)} />
          <div className="md:col-span-2">
            <Detail label="Address" value={member.address} />
          </div>
        </div>

        <div className="flex justify-end mt-6">
          <button
            type="button"
            onClick={onClose}
            className="bg-gray-200 px-5 py-2 rounded-lg"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
}

function Detail({ label, value }) {
  return (
    <div className="border rounded-lg p-3">
      <div className="text-xs text-gray-500 mb-1">{label}</div>
      <div className="font-medium break-words">{value || "—"}</div>
    </div>
  );
}

export default MemberViewModal;
