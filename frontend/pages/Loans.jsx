import { useEffect, useState } from "react";

import {
  getLoanHistory,
  borrowBook,
  returnBook,
  renewLoan,
} from "../api/loanApi";

import BorrowModal from "../components/loan/BorrowModal";
import ReturnModal from "../components/loan/ReturnModal";
import RenewModal from "../components/loan/RenewModal";

function Loans() {
  const [loans, setLoans] = useState([]);

  const [borrowOpen, setBorrowOpen] = useState(false);

  const [returnOpen, setReturnOpen] = useState(false);

  const [renewOpen, setRenewOpen] = useState(false);

  const [selectedLoan, setSelectedLoan] = useState(null);

  const [error, setError] = useState("");

  const load = () => {
    getLoanHistory()
      .then((res) => {
        setLoans(res.data.content);
      })

      .catch(() => {
        setError("Failed to load loans");
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
items-center
mb-6
"
      >
        <h1
          className="
text-2xl
font-bold
"
        >
          Loans
        </h1>

        <button
          onClick={() => setBorrowOpen(true)}
          className="
bg-blue-600
text-white
px-4
py-2
rounded-lg
"
        >
          Borrow Book
        </button>
      </div>

      {error && (
        <div
          className="
bg-red-100
text-red-700
p-3
rounded-lg
mb-4
"
        >
          {error}
        </div>
      )}

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
            <tr
              className="
border-b
text-left
"
            >
              <th className="p-3">Tracking</th>

              <th>Member</th>

              <th>Book</th>

              <th>Type</th>

              <th>Status</th>

              <th>Action</th>
            </tr>
          </thead>

          <tbody>
            {loans.map((loan) => (
              <tr
                key={loan.id}
                className="
border-b
"
              >
                <td className="p-3">{loan.trackingCode}</td>

                <td>{loan.membershipNumber}</td>

                <td>{loan.bookCode}</td>

                <td>{loan.type}</td>

                <td>{loan.status}</td>

                <td>
                  <div
                    className="
flex
gap-3
"
                  >
                    <button
                      onClick={() => {
                        setSelectedLoan(loan);

                        setReturnOpen(true);
                      }}
                      className="
text-green-600
"
                    >
                      Return
                    </button>

                    <button
                      onClick={() => {
                        setSelectedLoan(loan);

                        setRenewOpen(true);
                      }}
                      className="
text-orange-500
"
                    >
                      Renew
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <BorrowModal
        open={borrowOpen}
        onClose={() => setBorrowOpen(false)}
        onSubmit={(data) => {
          borrowBook(data)
            .then(() => {
              load();

              setBorrowOpen(false);
            })

            .catch((err) => {
              setError(err.response?.data?.message || "Borrow failed");
            });
        }}
      />

      <ReturnModal
        open={returnOpen}
        trackingCode={selectedLoan?.trackingCode}
        onClose={() => setReturnOpen(false)}
        onSubmit={(data) => {
          returnBook(data)
            .then(() => {
              load();

              setReturnOpen(false);

              setSelectedLoan(null);
            })

            .catch((err) => {
              setError(err.response?.data?.message || "Return failed");
            });
        }}
      />

      <RenewModal
        open={renewOpen}
        trackingCode={selectedLoan?.trackingCode}
        onClose={() => setRenewOpen(false)}
        onSubmit={(data) => {
          renewLoan(data)
            .then(() => {
              load();

              setRenewOpen(false);

              setSelectedLoan(null);
            })

            .catch((err) => {
              setError(err.response?.data?.message || "Renew failed");
            });
        }}
      />
    </div>
  );
}

export default Loans;
