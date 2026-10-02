import { useEffect, useState } from "react";

import { getFines, payFine } from "../api/fineApi";

import PayFineModal from "../components/PayFineModal";

function formatAmount(value) {
  const amount = Number(value ?? 0);

  return amount.toLocaleString();
}

function Fines() {
  const [fines, setFines] = useState([]);

  const [open, setOpen] = useState(false);

  const load = () => {
    getFines().then((res) => {
      setFines(res.data);
    });
  };

  useEffect(() => {
    load();
  }, []);

  return (
    <div>
      <div
        className="
flex
justify-between
mb-6
"
      >
        <h1
          className="
text-2xl
font-bold
"
        >
          Fines
        </h1>

        <button
          onClick={() => setOpen(true)}
          className="
bg-green-600
text-white
px-4
py-2
rounded-lg
"
        >
          Pay Fine
        </button>
      </div>

      <div
        className="
bg-white
rounded-xl
shadow
p-5
overflow-x-auto
"
      >
        <table className="w-full min-w-[1100px]">
          <thead>
            <tr className="border-b text-left">
              <th className="p-3">Fine ID</th>

              <th>Member ID</th>

              <th>Membership Number</th>

              <th>Loan ID</th>

              <th>Amount</th>

              <th>Paid</th>

              <th>Remaining</th>

              <th>Status</th>

              <th>Created</th>
            </tr>
          </thead>

          <tbody>
            {fines.map((fine) => (
              <tr key={fine.id} className="border-b">
                <td className="p-3">{fine.id}</td>

                <td>{fine.memberId ?? "—"}</td>

                <td>{fine.membershipNumber ?? "—"}</td>

                <td>{fine.loanTransactionId}</td>

                <td>{formatAmount(fine.amount)}</td>

                <td>{formatAmount(fine.paidAmount)}</td>

                <td>{formatAmount(fine.remainingAmount)}</td>

                <td>{fine.status}</td>

                <td>{fine.createdAt}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <PayFineModal
        open={open}
        onClose={() => setOpen(false)}
        onSubmit={async (data) => {
          await payFine(data);
          await load();
          setOpen(false);
        }}
      />
    </div>
  );
}

export default Fines;
