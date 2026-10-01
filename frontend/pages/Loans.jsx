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
import LoanDetailsModal from "../components/loan/LoanDetailsModal";

function Loans() {
  const [loans, setLoans] = useState([]);

  const [borrowOpen, setBorrowOpen] = useState(false);
  const [returnOpen, setReturnOpen] = useState(false);
  const [renewOpen, setRenewOpen] = useState(false);
  const [detailsOpen, setDetailsOpen] = useState(false);

  const [selectedLoan, setSelectedLoan] = useState(null);

  const [error, setError] = useState("");

  const load = async () => {
    try {
      setError("");

      const res = await getLoanHistory();

      setLoans(res.data.content || []);
    } catch (err) {
      setError(err.response?.data?.message || "Failed to load loans");
    }
  };

  useEffect(() => {
    load();
  }, []);

  const openBorrow = () => {
    setError("");
    setSelectedLoan(null);
    setBorrowOpen(true);
  };

  const closeBorrow = () => {
    setBorrowOpen(false);
    setSelectedLoan(null);
    setError("");
  };

  const openDetails = (loan) => {
    setError("");
    setSelectedLoan(loan);
    setDetailsOpen(true);
  };

  const closeDetails = () => {
    setDetailsOpen(false);
    setSelectedLoan(null);
  };

  const openReturn = (loan) => {
    setError("");
    setSelectedLoan(loan);
    setReturnOpen(true);
  };

  const closeReturn = () => {
    setReturnOpen(false);
    setSelectedLoan(null);
    setError("");
  };

  const openRenew = (loan) => {
    setError("");
    setSelectedLoan(loan);
    setRenewOpen(true);
  };

  const closeRenew = () => {
    setRenewOpen(false);
    setSelectedLoan(null);
    setError("");
  };

  const handleBorrow = async (data) => {
    try {
      setError("");

      await borrowBook(data);

      await load();

      closeBorrow();
    } catch (err) {
      throw err;
    }
  };

  const handleReturn = async (data) => {
    try {
      setError("");

      await returnBook(data);

      await load();

      closeReturn();
    } catch (err) {
      throw err;
    }
  };

  const handleRenew = async (data) => {
    try {
      setError("");

      await renewLoan(data);

      await load();

      closeRenew();
    } catch (err) {
      throw err;
    }
  };

  /*
   * فقط تراکنش موفق و جاری باید Return / Renew داشته باشد.
   *
   * تراکنش RETURN شده:
   *    returnDate != null
   *
   * تراکنش ناموفق:
   *    status != SUCCESS
   *
   * تراکنش RETURN:
   *    type == RETURN
   */
  const isActiveLoan = (loan) => {
    return (
      loan.status === "SUCCESS" && loan.type !== "RETURN" && !loan.returnDate
    );
  };

  return (
    <div>
      {/* Header */}

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
          onClick={openBorrow}
          className="
          bg-blue-600
          hover:bg-blue-700
          text-white
          px-4
          py-2
          rounded-lg
          "
        >
          Borrow Book
        </button>
      </div>

      {/* Error */}

      {error && (
        <div
          className="
          bg-red-100
          text-red-700
          border
          border-red-300
          p-3
          rounded-lg
          mb-4
          "
        >
          {error}
        </div>
      )}

      {/* Loans Table */}

      <div
        className="
        bg-white
        rounded-xl
        shadow
        p-5
        overflow-x-auto
        "
      >
        <table
          className="
          w-full
          min-w-[850px]
          "
        >
          <thead>
            <tr
              className="
              border-b
              text-left
              "
            >
              <th
                className="
                p-4
                whitespace-nowrap
                "
              >
                Tracking
              </th>

              <th
                className="
                p-4
                whitespace-nowrap
                "
              >
                Member
              </th>

              <th
                className="
                p-4
                whitespace-nowrap
                "
              >
                Book
              </th>

              <th
                className="
                p-4
                whitespace-nowrap
                "
              >
                Type
              </th>

              <th
                className="
                p-4
                whitespace-nowrap
                "
              >
                Status
              </th>

              <th
                className="
                p-4
                whitespace-nowrap
                "
              >
                Action
              </th>
            </tr>
          </thead>

          <tbody>
            {loans.map((loan) => {
              const active = isActiveLoan(loan);

              return (
                <tr
                  key={loan.id}
                  className="
                  border-b
                  last:border-b-0
                  "
                >
                  {/* Tracking */}

                  <td
                    className="
                    p-4
                    max-w-[280px]
                    "
                  >
                    <span
                      className="
                      block
                      truncate
                      "
                      title={loan.trackingCode}
                    >
                      {loan.trackingCode || "-"}
                    </span>
                  </td>

                  {/* Member */}

                  <td
                    className="
                    p-4
                    whitespace-nowrap
                    "
                  >
                    {loan.membershipNumber || "-"}
                  </td>

                  {/* Book */}

                  <td
                    className="
                    p-4
                    whitespace-nowrap
                    "
                  >
                    {loan.bookCode || "-"}
                  </td>

                  {/* Type */}

                  <td
                    className="
                    p-4
                    whitespace-nowrap
                    "
                  >
                    {loan.type || "-"}
                  </td>

                  {/* Status */}

                  <td
                    className="
                    p-4
                    whitespace-nowrap
                    "
                  >
                    {loan.status || "-"}
                  </td>

                  {/* Action */}

                  <td
                    className="
                    p-4
                    "
                  >
                    <div
                      className="
                      flex
                      items-center
                      gap-4
                      whitespace-nowrap
                      "
                    >
                      {/* View */}

                      <button
                        onClick={() => openDetails(loan)}
                        className="
                        text-blue-600
                        hover:text-blue-800
                        "
                      >
                        View
                      </button>

                      {/* Return / Renew فقط برای Loan جاری */}

                      {active && (
                        <>
                          <button
                            onClick={() => openReturn(loan)}
                            className="
                            text-green-600
                            hover:text-green-800
                            "
                          >
                            Return
                          </button>

                          <button
                            onClick={() => openRenew(loan)}
                            className="
                            text-orange-500
                            hover:text-orange-700
                            "
                          >
                            Renew
                          </button>
                        </>
                      )}
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      {/* Borrow Modal */}

      <BorrowModal
        open={borrowOpen}
        onClose={closeBorrow}
        onSubmit={handleBorrow}
      />

      {/* Return Modal */}

      <ReturnModal
        open={returnOpen}
        trackingCode={selectedLoan?.trackingCode || ""}
        onClose={closeReturn}
        onSubmit={handleReturn}
      />

      {/* Renew Modal */}

      <RenewModal
        open={renewOpen}
        trackingCode={selectedLoan?.trackingCode || ""}
        onClose={closeRenew}
        onSubmit={handleRenew}
      />

      {/* Details Modal */}

      <LoanDetailsModal
        open={detailsOpen}
        loan={selectedLoan}
        onClose={closeDetails}
      />
    </div>
  );
}

export default Loans;
