import { useEffect, useState } from "react";

import { getFines, payFine } from "../api/fineApi";

import PayFineModal from "../components/PayFineModal";

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
        <table className="w-full">
          <thead>
            <tr className="border-b text-left">
              <th className="p-3">ID</th>

              <th>Loan ID</th>

              <th>Amount</th>

              <th>Status</th>

              <th>Created</th>
            </tr>
          </thead>

          <tbody>
            {fines.map((fine) => (
              <tr key={fine.id} className="border-b">
                <td className="p-3">{fine.id}</td>

                <td>{fine.loanTransactionId}</td>

                <td>{fine.amount}</td>

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
        onSubmit={(data) => {
          payFine(data).then(() => {
            load();

            setOpen(false);
          });
        }}
      />
    </div>
  );
}

export default Fines;
