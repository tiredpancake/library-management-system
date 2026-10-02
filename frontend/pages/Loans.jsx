import { useEffect, useMemo, useState } from "react";
import {
  BookOpen,
  CheckCircle2,
  Clipboard,
  Eye,
  History,
  RefreshCw,
  RotateCcw,
  Search,
  SlidersHorizontal,
  X,
  XCircle,
} from "lucide-react";

import {
  getLoanHistory,
  borrowBook,
  returnBook,
  renewLoan,
  getLoanStatus,
} from "../api/loanApi";

import BorrowModal from "../components/loan/BorrowModal";
import ReturnModal from "../components/loan/ReturnModal";
import RenewModal from "../components/loan/RenewModal";
import CheckStatusModal from "../components/loan/CheckStatusModal";
import LoanDetailsModal from "../components/loan/LoanDetailsModal";

function formatDate(value) {
  if (!value) return "—";

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;

  return date.toLocaleString();
}

function shortTracking(value) {
  if (!value) return "—";
  if (value.length <= 18) return value;
  return `${value.slice(0, 8)}…${value.slice(-6)}`;
}

function getErrorMessage(error, fallback) {
  return (
    error?.response?.data?.message || error?.response?.data?.error || fallback
  );
}

function TypeBadge({ type }) {
  const styles = {
    BORROW: "bg-blue-50 text-blue-700 border-blue-200",
    RENEW: "bg-amber-50 text-amber-700 border-amber-200",
    RETURN: "bg-emerald-50 text-emerald-700 border-emerald-200",
  };

  return (
    <span
      className={`inline-flex items-center rounded-full border px-2.5 py-1 text-xs font-semibold ${
        styles[type] || "bg-slate-50 text-slate-600 border-slate-200"
      }`}
    >
      {type || "—"}
    </span>
  );
}

function StatusBadge({ status, current }) {
  return (
    <div className="flex flex-wrap items-center gap-1.5">
      <span
        className={`inline-flex items-center rounded-full border px-2.5 py-1 text-xs font-semibold ${
          status === "SUCCESS"
            ? "bg-emerald-50 text-emerald-700 border-emerald-200"
            : "bg-red-50 text-red-700 border-red-200"
        }`}
      >
        {status || "—"}
      </span>
      {current && (
        <span className="inline-flex items-center rounded-full bg-blue-600 px-2.5 py-1 text-xs font-semibold text-white">
          Current
        </span>
      )}
    </div>
  );
}

function Loans() {
  const [loans, setLoans] = useState([]);

  const [borrowOpen, setBorrowOpen] = useState(false);
  const [returnOpen, setReturnOpen] = useState(false);
  const [renewOpen, setRenewOpen] = useState(false);
  const [statusOpen, setStatusOpen] = useState(false);
  const [detailsOpen, setDetailsOpen] = useState(false);

  const [selectedLoan, setSelectedLoan] = useState(null);
  const [error, setError] = useState("");
  const [borrowError, setBorrowError] = useState("");
  const [returnError, setReturnError] = useState("");
  const [renewError, setRenewError] = useState("");
  const [search, setSearch] = useState("");

  const [filters, setFilters] = useState({
    membershipNumber: "",
    bookCode: "",
    type: "",
    state: "",
    from: "",
    to: "",
  });
  const [filtersOpen, setFiltersOpen] = useState(false);
  const [filtersLoading, setFiltersLoading] = useState(false);

  const [statusError, setStatusError] = useState("");
  const [statusResult, setStatusResult] = useState(null);
  const [statusLoading, setStatusLoading] = useState(false);

  const [lastTransaction, setLastTransaction] = useState(null);
  const [copiedTracking, setCopiedTracking] = useState("");

  const load = async (nextFilters = filters) => {
    try {
      setError("");
      setFiltersLoading(true);

      const params = {
        page: 0,
        size: 100,
        sort: "requestDate,desc",
      };

      Object.entries(nextFilters).forEach(([key, value]) => {
        const trimmed = typeof value === "string" ? value.trim() : value;
        if (trimmed !== "") {
          params[key] = trimmed;
        }
      });

      const response = await getLoanHistory(params);
      setLoans(response.data?.content || []);
    } catch (err) {
      setError(getErrorMessage(err, "Failed to load loans"));
    } finally {
      setFiltersLoading(false);
    }
  };

  useEffect(() => {
    load({
      membershipNumber: "",
      bookCode: "",
      type: "",
      state: "",
      from: "",
      to: "",
    });
  }, []);

  const updateFilter = (field, value) => {
    setFilters((current) => ({ ...current, [field]: value }));
  };

  const applyFilters = async () => {
    if (filters.from && filters.to && filters.from > filters.to) {
      setError("From date cannot be later than To date.");
      return;
    }

    setError("");
    await load(filters);
  };

  const clearFilters = async () => {
    const emptyFilters = {
      membershipNumber: "",
      bookCode: "",
      type: "",
      state: "",
      from: "",
      to: "",
    };

    setFilters(emptyFilters);
    setSearch("");
    setError("");
    await load(emptyFilters);
  };

  const hasActiveFilters = Object.values(filters).some(
    (value) => String(value || "").trim() !== "",
  );

  const filteredLoans = useMemo(() => {
    const query = search.trim().toLowerCase();
    if (!query) return loans;

    return loans.filter((loan) =>
      [
        loan.trackingCode,
        loan.membershipNumber,
        loan.bookCode,
        loan.type,
        loan.status,
      ].some((value) =>
        String(value || "")
          .toLowerCase()
          .includes(query),
      ),
    );
  }, [loans, search]);

  const summary = useMemo(
    () => ({
      total: loans.length,
      current: loans.filter((loan) => loan.current).length,
      renewals: loans.filter((loan) => loan.type === "RENEW").length,
      returned: loans.filter((loan) => loan.type === "RETURN").length,
    }),
    [loans],
  );

  const openBorrow = () => {
    setError("");
    setBorrowError("");
    setLastTransaction(null);
    setBorrowOpen(true);
  };

  const closeBorrow = () => {
    setBorrowOpen(false);
    setBorrowError("");
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
    setReturnError("");
    setLastTransaction(null);
    setSelectedLoan(loan);
    setReturnOpen(true);
  };

  const closeReturn = () => {
    setReturnOpen(false);
    setSelectedLoan(null);
    setReturnError("");
  };

  const openRenew = (loan) => {
    setError("");
    setRenewError("");
    setLastTransaction(null);
    setSelectedLoan(loan);
    setRenewOpen(true);
  };

  const closeRenew = () => {
    setRenewOpen(false);
    setSelectedLoan(null);
    setRenewError("");
  };

  const openStatus = () => {
    setStatusError("");
    setStatusResult(null);
    setStatusOpen(true);
  };

  const closeStatus = () => {
    setStatusOpen(false);
    setStatusError("");
    setStatusResult(null);
  };

  const handleStatus = async (trackingCode) => {
    try {
      setStatusLoading(true);
      setStatusError("");
      setStatusResult(null);

      const response = await getLoanStatus(trackingCode);
      setStatusResult(response.data);
    } catch (err) {
      setStatusError(getErrorMessage(err, "Tracking code not found"));
    } finally {
      setStatusLoading(false);
    }
  };

  const handleBorrow = async (data) => {
    try {
      setBorrowError("");
      const response = await borrowBook(data);
      setLastTransaction(response.data);
      closeBorrow();

      try {
        await load();
      } catch (err) {
        setError(getErrorMessage(err, "Failed to load loans"));
      }
    } catch (err) {
      setBorrowError(getErrorMessage(err, "Borrow failed"));
    }
  };

  const handleReturn = async (data) => {
    try {
      setReturnError("");
      const response = await returnBook(data);
      setLastTransaction(response.data);
      closeReturn();

      try {
        await load();
      } catch (err) {
        setError(getErrorMessage(err, "Failed to load loans"));
      }
    } catch (err) {
      setReturnError(getErrorMessage(err, "Return failed"));
    }
  };

  const handleRenew = async (data) => {
    try {
      setRenewError("");
      const response = await renewLoan(data);
      setLastTransaction(response.data);
      closeRenew();

      try {
        await load();
      } catch (err) {
        setError(getErrorMessage(err, "Failed to load loans"));
      }
    } catch (err) {
      setRenewError(getErrorMessage(err, "Renew failed"));
    }
  };

  const copyTracking = async (trackingCode) => {
    if (!trackingCode) return;

    try {
      await navigator.clipboard.writeText(trackingCode);
      setCopiedTracking(trackingCode);
      window.setTimeout(() => setCopiedTracking(""), 1500);
    } catch {
      setCopiedTracking("");
    }
  };

  return (
    <div className="min-w-0 max-w-full space-y-6">
      <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <div className="flex items-center gap-2">
            <div className="rounded-xl bg-blue-100 p-2 text-blue-700">
              <BookOpen size={22} />
            </div>
            <h1 className="text-3xl font-bold tracking-tight text-slate-900">
              Loans
            </h1>
          </div>
        </div>

        <div className="flex flex-wrap gap-3">
          <button
            type="button"
            onClick={openStatus}
            className="inline-flex items-center gap-2 rounded-lg border border-blue-200 bg-white px-4 py-2.5 font-medium text-blue-700 shadow-sm transition hover:bg-blue-50"
          >
            <Search size={18} />
            Check Status
          </button>

          <button
            type="button"
            onClick={openBorrow}
            className="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2.5 font-medium text-white shadow-sm transition hover:bg-blue-700"
          >
            <BookOpen size={18} />
            Borrow Book
          </button>
        </div>
      </div>

      {error && (
        <div className="flex items-start gap-3 rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
          <span className="font-medium">Error:</span>
          <span>{error}</span>
        </div>
      )}

      {lastTransaction && (
        <div
          className={`rounded-xl border p-4 ${
            lastTransaction.status === "FAILED"
              ? "border-red-200 bg-red-50"
              : "border-emerald-200 bg-emerald-50"
          }`}
        >
          <div className="flex items-start gap-3">
            {lastTransaction.status === "FAILED" ? (
              <XCircle className="mt-0.5 text-red-600" size={21} />
            ) : (
              <CheckCircle2 className="mt-0.5 text-emerald-600" size={21} />
            )}

            <div className="min-w-0 flex-1">
              <p
                className={`font-semibold ${
                  lastTransaction.status === "FAILED"
                    ? "text-red-800"
                    : "text-emerald-800"
                }`}
              >
                {lastTransaction.status === "FAILED"
                  ? "Transaction failed."
                  : "Transaction completed successfully."}
              </p>

              {lastTransaction.status === "FAILED" &&
                lastTransaction.errorMessage && (
                  <p className="mt-2 text-sm text-red-700">
                    <span className="font-semibold">Reason:</span>{" "}
                    {lastTransaction.errorMessage}
                  </p>
                )}

              <div className="mt-3 grid gap-3 text-sm sm:grid-cols-3">
                <div>
                  <p
                    className={
                      lastTransaction.status === "FAILED"
                        ? "text-red-600"
                        : "text-emerald-600"
                    }
                  >
                    Transaction
                  </p>
                  <p
                    className={`font-medium ${
                      lastTransaction.status === "FAILED"
                        ? "text-red-900"
                        : "text-emerald-900"
                    }`}
                  >
                    #{lastTransaction.id}
                  </p>
                </div>

                <div className="min-w-0">
                  <p
                    className={
                      lastTransaction.status === "FAILED"
                        ? "text-red-600"
                        : "text-emerald-600"
                    }
                  >
                    Tracking Code
                  </p>

                  <p
                    className={`truncate font-medium ${
                      lastTransaction.status === "FAILED"
                        ? "text-red-900"
                        : "text-emerald-900"
                    }`}
                    title={lastTransaction.trackingCode}
                  >
                    {lastTransaction.trackingCode || "—"}
                  </p>
                </div>

                <div>
                  <p
                    className={
                      lastTransaction.status === "FAILED"
                        ? "text-red-600"
                        : "text-emerald-600"
                    }
                  >
                    Request Date
                  </p>

                  <p
                    className={`font-medium ${
                      lastTransaction.status === "FAILED"
                        ? "text-red-900"
                        : "text-emerald-900"
                    }`}
                  >
                    {formatDate(lastTransaction.requestDate)}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <SummaryCard
          icon={<History size={19} />}
          label="Total Transactions"
          value={summary.total}
        />
        <SummaryCard
          icon={<BookOpen size={19} />}
          label="Current Loans"
          value={summary.current}
        />
        <SummaryCard
          icon={<RefreshCw size={19} />}
          label="Renewals"
          value={summary.renewals}
        />
        <SummaryCard
          icon={<RotateCcw size={19} />}
          label="Returns"
          value={summary.returned}
        />
      </div>

      <section className="min-w-0 max-w-full overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
        <div className="border-b border-slate-200 p-5">
          <div className="flex flex-col gap-3 lg:flex-row lg:items-center lg:justify-between">
            <div>
              <h2 className="text-lg font-semibold text-slate-900">
                Transaction History
              </h2>
            </div>

            <div className="flex flex-col gap-2 sm:flex-row sm:items-center">
              <div className="relative w-full sm:w-72">
                <Search
                  size={17}
                  className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
                />
                <input
                  value={search}
                  onChange={(event) => setSearch(event.target.value)}
                  placeholder="Quick search..."
                  className="w-full rounded-lg border border-slate-300 bg-slate-50 py-2.5 pl-9 pr-3 text-sm outline-none transition focus:border-blue-500 focus:bg-white focus:ring-2 focus:ring-blue-100"
                />
              </div>

              <button
                type="button"
                onClick={() => setFiltersOpen((open) => !open)}
                className={`inline-flex items-center justify-center gap-2 rounded-lg border px-4 py-2.5 text-sm font-semibold transition ${
                  hasActiveFilters
                    ? "border-blue-300 bg-blue-50 text-blue-700"
                    : "border-slate-300 bg-white text-slate-700 hover:bg-slate-50"
                }`}
              >
                <SlidersHorizontal size={17} />
                Filters
                {hasActiveFilters && (
                  <span className="rounded-full bg-blue-600 px-1.5 py-0.5 text-[10px] text-white">
                    {
                      Object.values(filters).filter(
                        (value) => String(value || "").trim() !== "",
                      ).length
                    }
                  </span>
                )}
              </button>
            </div>
          </div>

          {filtersOpen && (
            <div className="mt-4 rounded-xl border border-slate-200 bg-slate-50 p-4">
              <div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-3">
                <FilterInput
                  label="Membership Number"
                  value={filters.membershipNumber}
                  onChange={(value) => updateFilter("membershipNumber", value)}
                  placeholder="e.g. 3735632761"
                />

                <FilterInput
                  label="Book Code"
                  value={filters.bookCode}
                  onChange={(value) => updateFilter("bookCode", value)}
                  placeholder="e.g. 93666827790000"
                />

                <FilterSelect
                  label="Transaction Type"
                  value={filters.type}
                  onChange={(value) => updateFilter("type", value)}
                >
                  <option value="">All types</option>
                  <option value="BORROW">Borrow</option>
                  <option value="RENEW">Renew</option>
                  <option value="RETURN">Return</option>
                </FilterSelect>

                <FilterSelect
                  label="Status"
                  value={filters.state}
                  onChange={(value) => updateFilter("state", value)}
                >
                  <option value="">All statuses</option>
                  <option value="RETURNED">Returned</option>
                  <option value="NOT_RETURNED">Not returned</option>
                  <option value="OVERDUE">Overdue</option>
                </FilterSelect>

                <FilterInput
                  label="From Date"
                  type="datetime-local"
                  value={filters.from}
                  onChange={(value) => updateFilter("from", value)}
                />

                <FilterInput
                  label="To Date"
                  type="datetime-local"
                  value={filters.to}
                  onChange={(value) => updateFilter("to", value)}
                />
              </div>

              <div className="mt-4 flex flex-col gap-2 border-t border-slate-200 pt-4 sm:flex-row sm:justify-end">
                <button
                  type="button"
                  onClick={clearFilters}
                  disabled={filtersLoading}
                  className="inline-flex items-center justify-center gap-2 rounded-lg border border-slate-300 bg-white px-4 py-2.5 text-sm font-semibold text-slate-700 transition hover:bg-slate-100 disabled:cursor-not-allowed disabled:opacity-60"
                >
                  <X size={16} />
                  Clear
                </button>
                <button
                  type="button"
                  onClick={applyFilters}
                  disabled={filtersLoading}
                  className="inline-flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-60"
                >
                  <Search size={16} />
                  {filtersLoading ? "Filtering..." : "Apply Filters"}
                </button>
              </div>
            </div>
          )}
        </div>

        <div className="w-full min-w-0 max-w-full overflow-x-auto overflow-y-hidden overscroll-x-contain">
          <table className="min-w-[1040px] table-fixed">
            <colgroup>
              <col className="w-[22%]" />
              <col className="w-[12%]" />
              <col className="w-[14%]" />
              <col className="w-[12%]" />
              <col className="w-[14%]" />
              <col className="w-[14%]" />
              <col className="w-[12%]" />
            </colgroup>
            <thead className="bg-slate-50">
              <tr className="border-b border-slate-200 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                <th className="px-5 py-4">Tracking Code</th>
                <th className="px-3 py-4">Member</th>
                <th className="px-3 py-4">Book</th>
                <th className="px-3 py-4">Type</th>
                <th className="px-3 py-4">Status</th>
                <th className="px-3 py-4">Request Date</th>
                <th className="px-3 py-4 text-right">Actions</th>
              </tr>
            </thead>

            <tbody className="divide-y divide-slate-100">
              {filteredLoans.length === 0 ? (
                <tr>
                  <td colSpan="7" className="px-6 py-14 text-center">
                    <div className="mx-auto flex max-w-sm flex-col items-center">
                      <div className="rounded-full bg-slate-100 p-4 text-slate-400">
                        <BookOpen size={24} />
                      </div>
                      <p className="mt-3 font-medium text-slate-700">
                        {search
                          ? "No matching transactions"
                          : "No loan transactions found"}
                      </p>
                      <p className="mt-1 text-sm text-slate-400">
                        {search
                          ? "Try another member, book, or tracking code."
                          : "Borrow a book to create the first transaction."}
                      </p>
                    </div>
                  </td>
                </tr>
              ) : (
                filteredLoans.map((loan) => {
                  const canReturn = loan?.canReturn === true;
                  const canRenew = loan?.canRenew === true;

                  return (
                    <tr
                      key={loan.id}
                      className={`transition hover:bg-slate-50 ${
                        loan.current ? "bg-blue-50/30" : ""
                      }`}
                    >
                      <td className="px-5 py-4">
                        <div className="flex min-w-0 items-center gap-2">
                          <div className="min-w-0 flex-1">
                            <p
                              className="truncate font-mono text-sm font-medium text-slate-800"
                              title={loan.trackingCode}
                            >
                              {shortTracking(loan.trackingCode)}
                            </p>
                            {loan.parentTransactionId && (
                              <p className="mt-1 text-xs text-slate-400">
                                Follow-up transaction
                              </p>
                            )}
                          </div>
                          {loan.trackingCode && (
                            <button
                              type="button"
                              onClick={() => copyTracking(loan.trackingCode)}
                              title="Copy tracking code"
                              className="shrink-0 rounded-md p-1.5 text-slate-400 transition hover:bg-slate-100 hover:text-blue-600"
                            >
                              <Clipboard size={15} />
                            </button>
                          )}
                          {copiedTracking === loan.trackingCode && (
                            <span className="shrink-0 text-xs font-medium text-emerald-600">
                              Copied
                            </span>
                          )}
                        </div>
                      </td>

                      <td className="px-3 py-4">
                        <span className="font-mono text-sm text-slate-700">
                          {loan.membershipNumber || "—"}
                        </span>
                      </td>

                      <td className="px-3 py-4">
                        <span className="font-mono text-sm text-slate-700">
                          {loan.bookCode || "—"}
                        </span>
                      </td>

                      <td className="px-3 py-4">
                        <TypeBadge type={loan.type} />
                      </td>

                      <td className="px-3 py-4">
                        <StatusBadge
                          status={loan.status}
                          current={loan.current}
                        />
                      </td>

                      <td className="px-3 py-4">
                        <div className="text-sm text-slate-700">
                          {formatDate(loan.requestDate)}
                        </div>
                        {loan.dueDate && loan.current && (
                          <div className="mt-1 text-xs text-slate-400">
                            Due: {formatDate(loan.dueDate)}
                          </div>
                        )}
                      </td>

                      <td className="px-3 py-4">
                        <div className="flex items-center justify-end gap-1.5">
                          <ActionButton
                            label="View"
                            icon={<Eye size={15} />}
                            onClick={() => openDetails(loan)}
                            variant="blue"
                          />
                          <ActionButton
                            label="Return"
                            icon={<RotateCcw size={15} />}
                            onClick={() => openReturn(loan)}
                            disabled={!canReturn}
                            variant="green"
                            title={
                              canReturn
                                ? "Return this current loan"
                                : "Only the current loan can be returned"
                            }
                          />
                          <ActionButton
                            label="Renew"
                            icon={<RefreshCw size={15} />}
                            onClick={() => openRenew(loan)}
                            disabled={!canRenew}
                            variant="amber"
                            title={
                              canRenew
                                ? "Renew this current loan"
                                : loan?.current
                                  ? "Maximum renew limit reached"
                                  : "Only the current loan can be renewed"
                            }
                          />
                        </div>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </section>

      <BorrowModal
        open={borrowOpen}
        onClose={closeBorrow}
        onSubmit={handleBorrow}
        error={borrowError}
      />

      <ReturnModal
        open={returnOpen}
        trackingCode={selectedLoan?.trackingCode || ""}
        membershipNumber={selectedLoan?.membershipNumber || ""}
        bookCode={selectedLoan?.bookCode || ""}
        onClose={closeReturn}
        onSubmit={handleReturn}
        error={returnError}
      />

      <RenewModal
        open={renewOpen}
        trackingCode={selectedLoan?.trackingCode || ""}
        onClose={closeRenew}
        onSubmit={handleRenew}
        error={renewError}
      />

      <CheckStatusModal
        open={statusOpen}
        onClose={closeStatus}
        onSubmit={handleStatus}
        loading={statusLoading}
        result={statusResult}
        error={statusError}
      />

      <LoanDetailsModal
        open={detailsOpen}
        loan={selectedLoan}
        onClose={closeDetails}
      />
    </div>
  );
}

function FilterInput({ label, value, onChange, placeholder, type = "text" }) {
  return (
    <label className="block">
      <span className="mb-1.5 block text-xs font-semibold text-slate-600">
        {label}
      </span>
      <input
        type={type}
        value={value}
        onChange={(event) => onChange(event.target.value)}
        placeholder={placeholder}
        className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-800 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
      />
    </label>
  );
}

function FilterSelect({ label, value, onChange, children }) {
  return (
    <label className="block">
      <span className="mb-1.5 block text-xs font-semibold text-slate-600">
        {label}
      </span>
      <select
        value={value}
        onChange={(event) => onChange(event.target.value)}
        className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-800 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
      >
        {children}
      </select>
    </label>
  );
}

function SummaryCard({ icon, label, value }) {
  return (
    <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
      <div className="flex items-center justify-between">
        <div className="rounded-lg bg-slate-100 p-2 text-slate-600">{icon}</div>
        <span className="text-2xl font-bold text-slate-900">{value}</span>
      </div>
      <p className="mt-3 text-sm font-medium text-slate-500">{label}</p>
    </div>
  );
}

function ActionButton({ label, icon, onClick, disabled, variant, title }) {
  const variants = {
    blue: "border-blue-200 bg-blue-50 text-blue-700 hover:bg-blue-100",
    green:
      "border-emerald-200 bg-emerald-50 text-emerald-700 hover:bg-emerald-100",
    amber: "border-amber-200 bg-amber-50 text-amber-700 hover:bg-amber-100",
  };

  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled}
      title={title}
      className={`inline-flex items-center gap-1.5 rounded-md border px-2.5 py-1.5 text-xs font-semibold transition disabled:cursor-not-allowed disabled:border-slate-200 disabled:bg-slate-50 disabled:text-slate-300 disabled:hover:bg-slate-50 ${variants[variant]}`}
    >
      {icon}
      <span>{label}</span>
    </button>
  );
}

export default Loans;
