function LoanDetailsModal({ open, loan, onClose }) {
  if (!open || !loan) {
    return null;
  }

  const formatDate = (date) => {
    if (!date) {
      return "-";
    }

    return new Date(date).toLocaleString();
  };

  return (
    <div
      className="
      fixed
      inset-0
      bg-black/40
      z-50
      flex
      items-center
      justify-center
      p-4
      "
    >
      <div
        className="
        bg-white
        rounded-xl
        shadow-xl
        w-full
        max-w-lg
        max-h-[85vh]
        flex
        flex-col
        "
      >
        <div
          className="
          p-6
          border-b
          "
        >
          <h2
            className="
            text-xl
            font-bold
            "
          >
            Loan Details
          </h2>
        </div>

        <div
          className="
          p-6
          overflow-y-auto
          space-y-4
          "
        >
          <Detail label="Tracking Code" value={loan.trackingCode} />

          <Detail label="Membership Number" value={loan.membershipNumber} />

          <Detail label="Book Code" value={loan.bookCode} />

          <Detail label="Transaction Type" value={loan.type} />

          <Detail label="Status" value={loan.status} />

          <Detail label="Request Date" value={formatDate(loan.requestDate)} />

          <Detail label="Due Date" value={formatDate(loan.dueDate)} />

          <Detail label="Return Date" value={formatDate(loan.returnDate)} />

          <Detail label="Renew Count" value={loan.renewCount} />
        </div>

        <div
          className="
          p-6
          border-t
          flex
          justify-end
          "
        >
          <button
            type="button"
            onClick={onClose}
            className="
            bg-blue-600
            hover:bg-blue-700
            text-white
            px-5
            py-2
            rounded-lg
            "
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
    <div
      className="
      border-b
      pb-3
      "
    >
      <p
        className="
        text-sm
        text-gray-500
        mb-1
        "
      >
        {label}
      </p>

      <p
        className="
        font-medium
        break-words
        "
      >
        {value !== null && value !== undefined && value !== "" ? value : "-"}
      </p>
    </div>
  );
}

export default LoanDetailsModal;
