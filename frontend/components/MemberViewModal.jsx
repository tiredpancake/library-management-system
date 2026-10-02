import { useCallback, useEffect, useState } from "react";

import { getLoanHistory, returnBook, renewLoan } from "../api/loanApi";
import ReturnModal from "./loan/ReturnModal";
import RenewModal from "./loan/RenewModal";
import LoanDetailsModal from "./loan/LoanDetailsModal";

function formatDate(value) {
  if (!value) return "—";

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;

  return date.toLocaleString();
}

function formatBirthDate(value) {
  if (!value) return "—";

  const date = new Date(`${value}T00:00:00`);
  if (Number.isNaN(date.getTime())) return value;

  return date.toLocaleDateString();
}

function isActiveLoan(loan) {
  return (
    loan?.status === "SUCCESS" && loan?.type !== "RETURN" && !loan?.returnDate
  );
}

function isCurrentActiveLoan(loan, allLoans) {
  if (!isActiveLoan(loan)) return false;

  return !allLoans.some(
    (candidate) =>
      candidate?.parentTransactionId != null &&
      String(candidate.parentTransactionId) === String(loan.id),
  );
}

function MemberViewModal({ open, onClose, member }) {
  const [loans, setLoans] = useState([]);
  const [loanLoading, setLoanLoading] = useState(false);
  const [loanError, setLoanError] = useState("");
  const [selectedLoan, setSelectedLoan] = useState(null);
  const [returnOpen, setReturnOpen] = useState(false);
  const [renewOpen, setRenewOpen] = useState(false);

  const loadLoans = useCallback(async () => {
    if (!member?.membershipNumber) {
      setLoans([]);
      return;
    }

    try {
      setLoanLoading(true);
      setLoanError("");

      const response = await getLoanHistory({
        membershipNumber: member.membershipNumber,
        page: 0,
        size: 100,
        sort: "requestDate,desc",
      });

      setLoans(response.data?.content || []);
    } catch (error) {
      setLoans([]);
      setLoanError(
        error?.response?.data?.message || "Failed to load loan history",
      );
    } finally {
      setLoanLoading(false);
    }
  }, [member?.membershipNumber]);

  useEffect(() => {
    if (open && member) {
      loadLoans();
    } else {
      setLoans([]);
      setLoanError("");
      setSelectedLoan(null);
      setReturnOpen(false);
      setRenewOpen(false);
    }
  }, [open, member?.membershipNumber, loadLoans]);

  if (!open || !member) return null;

  const openReturn = (loan) => {
    setSelectedLoan(loan);
    setReturnOpen(true);
    setLoanError("");
  };

  const closeReturn = () => {
    setReturnOpen(false);
    setSelectedLoan(null);
  };

  const openRenew = (loan) => {
    setSelectedLoan(loan);
    setRenewOpen(true);
    setLoanError("");
  };

  const closeRenew = () => {
    setRenewOpen(false);
    setSelectedLoan(null);
  };

  const handleReturn = async (data) => {
    try {
      await returnBook(data);
      await loadLoans();
      closeReturn();
    } catch (error) {
      setLoanError(
        error?.response?.data?.message || "Failed to return the book",
      );
    }
  };

  const handleRenew = async (data) => {
    try {
      await renewLoan(data);
      await loadLoans();
      closeRenew();
    } catch (error) {
      setLoanError(
        error?.response?.data?.message || "Failed to renew the loan",
      );
    }
  };

  return (
    <>
      <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
        <div className="bg-white rounded-xl shadow-xl w-full max-w-6xl max-h-[92vh] overflow-y-auto">
          <div className="p-6 border-b flex justify-between items-start">
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

          <div className="p-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Detail label="Full Name" value={member.fullName} />
              <Detail label="National Code" value={member.nationalCode} />
              <Detail
                label="Birth Date"
                value={formatBirthDate(member.birthDate)}
              />
              <Detail label="Membership Type" value={member.membershipType} />
              <Detail label="Phone" value={member.phone} />
              <Detail label="Postal Code" value={member.postalCode} />
              <Detail label="Status" value={member.status} />
              <Detail
                label="Membership Number"
                value={member.membershipNumber}
              />
              <Detail label="Created At" value={formatDate(member.createdAt)} />
              <Detail
                label="Last Updated"
                value={formatDate(member.updatedAt)}
              />
              <div className="md:col-span-2">
                <Detail label="Address" value={member.address} />
              </div>
            </div>

            <section className="mt-8">
              <div className="flex items-center justify-between mb-4">
                <div>
                  <h3 className="text-lg font-semibold">Loan History</h3>
                  <p className="text-sm text-gray-500 mt-1">
                    All loan transactions for this member.
                  </p>
                </div>

                <span className="text-sm text-gray-500">
                  {loans.length} transaction{loans.length === 1 ? "" : "s"}
                </span>
              </div>

              {loanError && (
                <div className="bg-red-100 text-red-700 border border-red-300 p-3 rounded-lg mb-4">
                  {loanError}
                </div>
              )}

              {loanLoading ? (
                <div className="border rounded-lg p-6 text-center text-gray-500">
                  Loading loan history...
                </div>
              ) : loans.length === 0 ? (
                <div className="border rounded-lg p-6 text-center text-gray-500">
                  No loan transactions found for this member.
                </div>
              ) : (
                <div className="border rounded-lg overflow-x-auto">
                  <table className="w-full min-w-[900px]">
                    <thead>
                      <tr className="border-b bg-gray-50 text-left">
                        <th className="p-3">Tracking</th>
                        <th className="p-3">Book</th>
                        <th className="p-3">Type</th>
                        <th className="p-3">Status</th>
                        <th className="p-3">Request Date</th>
                        <th className="p-3">Due Date</th>
                        <th className="p-3">Return Date</th>
                        <th className="p-3">Renew Count</th>
                        <th className="p-3">Action</th>
                      </tr>
                    </thead>

                    <tbody>
                      {loans.map((loan) => {
                        const active = isCurrentActiveLoan(loan, loans);

                        return (
                          <tr
                            key={loan.id}
                            className="border-b last:border-b-0"
                          >
                            <td className="p-3 max-w-[220px] break-all">
                              {loan.trackingCode || "—"}
                            </td>
                            <td className="p-3 whitespace-nowrap">
                              {loan.bookCode || "—"}
                            </td>
                            <td className="p-3 whitespace-nowrap">
                              {loan.type || "—"}
                            </td>
                            <td className="p-3 whitespace-nowrap">
                              {loan.status || "—"}
                            </td>
                            <td className="p-3 whitespace-nowrap">
                              {formatDate(loan.requestDate)}
                            </td>
                            <td className="p-3 whitespace-nowrap">
                              {formatDate(loan.dueDate)}
                            </td>
                            <td className="p-3 whitespace-nowrap">
                              {formatDate(loan.returnDate)}
                            </td>
                            <td className="p-3 whitespace-nowrap">
                              {loan.renewCount ?? 0}
                            </td>
                            <td className="p-3 whitespace-nowrap">
                              <div className="flex items-center gap-3">
                                <button
                                  type="button"
                                  onClick={() => setSelectedLoan(loan)}
                                  className="text-blue-600 hover:text-blue-800"
                                >
                                  View
                                </button>

                                {active && (
                                  <>
                                    <button
                                      type="button"
                                      onClick={() => openReturn(loan)}
                                      className="text-green-600 hover:text-green-800"
                                    >
                                      Return
                                    </button>

                                    <button
                                      type="button"
                                      onClick={() => openRenew(loan)}
                                      className="text-orange-500 hover:text-orange-700"
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
              )}
            </section>
          </div>

          <div className="p-6 border-t flex justify-end">
            <button
              type="button"
              onClick={onClose}
              className="bg-gray-200 hover:bg-gray-300 px-5 py-2 rounded-lg"
            >
              Close
            </button>
          </div>
        </div>
      </div>

      <LoanDetailsModal
        open={Boolean(selectedLoan) && !returnOpen && !renewOpen}
        loan={selectedLoan}
        onClose={() => setSelectedLoan(null)}
      />

      <ReturnModal
        open={returnOpen}
        trackingCode={selectedLoan?.trackingCode || ""}
        membershipNumber={selectedLoan?.membershipNumber || ""}
        bookCode={selectedLoan?.bookCode || ""}
        onClose={closeReturn}
        onSubmit={handleReturn}
      />

      <RenewModal
        open={renewOpen}
        trackingCode={selectedLoan?.trackingCode || ""}
        onClose={closeRenew}
        onSubmit={handleRenew}
      />
    </>
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
