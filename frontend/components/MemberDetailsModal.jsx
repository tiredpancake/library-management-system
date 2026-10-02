function MemberDetailsModal({ member, onClose }) {
  if (!member) {
    return null;
  }

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
            Member Details
          </h2>
        </div>

        <div
          className="
                    p-6
                    overflow-y-auto
                    space-y-4
                    "
        >
          <Detail label="Name" value={member.fullName} />

          <Detail label="Membership Number" value={member.membershipNumber} />

          <Detail label="National Code" value={member.nationalCode} />

          <Detail label="Birth Date" value={member.birthDate} />

          <Detail label="Membership Type" value={member.membershipType} />

          <Detail label="Phone" value={member.phone} />

          <Detail label="Address" value={member.address} />

          <Detail label="Postal Code" value={member.postalCode} />

          <Detail label="Status" value={member.status} />

          <Detail
            label="Created At"
            value={
              member.createdAt
                ? new Date(member.createdAt).toLocaleString()
                : "-"
            }
          />
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
            pb-2
            "
    >
      <p
        className="
                text-sm
                text-gray-500
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
        {value || "-"}
      </p>
    </div>
  );
}

export default MemberDetailsModal;
