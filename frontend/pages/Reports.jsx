import {
  BarChart3,
  ChevronLeft,
  ChevronRight,
  Filter,
  RotateCcw,
  Search,
} from "lucide-react";
import { useEffect, useState } from "react";

import { getLoanHistory } from "../api/loanApi";

const EMPTY_FILTERS = {
  membershipNumber: "",
  bookCode: "",
  type: "",
  status: "",
  from: "",
  to: "",
};

function formatDate(value) {
  if (!value) return "—";

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return date.toLocaleString();
}

function Reports() {
  const [history, setHistory] = useState([]);

  const [filters, setFilters] = useState(EMPTY_FILTERS);
  const [appliedFilters, setAppliedFilters] = useState(EMPTY_FILTERS);

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const loadHistory = async (
    currentPage = 0,
    currentFilters = appliedFilters,
  ) => {
    setLoading(true);
    setError("");

    try {
      const res = await getLoanHistory({
        membershipNumber: currentFilters.membershipNumber || undefined,
        bookCode: currentFilters.bookCode || undefined,
        type: currentFilters.type || undefined,

        state: currentFilters.status || undefined,
        from: currentFilters.from
          ? new Date(currentFilters.from).toISOString()
          : undefined,

        to: currentFilters.to
          ? new Date(currentFilters.to).toISOString()
          : undefined,

        page: currentPage,
        size: 10,
        sort: "requestDate,desc",
      });

      const data = res.data;

      setHistory(data?.content || []);
      setTotalPages(data?.totalPages || 0);
      setTotalElements(data?.totalElements || 0);
    } catch (err) {
      console.error("Failed to load loan history", err);

      setHistory([]);
      setTotalPages(0);
      setTotalElements(0);

      setError(
        err?.response?.data?.message ||
          err?.response?.data?.error ||
          "Failed to load loan history.",
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadHistory(page, appliedFilters);
  }, [page, appliedFilters]);

  const changeFilter = (event) => {
    const { name, value } = event.target;

    setFilters((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const applyFilters = () => {
    setPage(0);
    setAppliedFilters({ ...filters });
  };

  const clearFilters = () => {
    setFilters({ ...EMPTY_FILTERS });
    setAppliedFilters({ ...EMPTY_FILTERS });
    setPage(0);
  };

  const hasFilters = Object.values(filters).some((value) => value !== "");

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div>
          <div className="flex items-center gap-3">
            <div className="rounded-lg bg-blue-100 p-2 text-blue-600">
              <BarChart3 size={22} />
            </div>

            <h1 className="text-3xl font-bold text-slate-900">Reports</h1>
          </div>
        </div>
      </div>

      <div className="rounded-xl bg-white p-6 shadow">
        <div className="mb-5 flex items-center justify-between">
          {hasFilters && (
            <button
              type="button"
              onClick={clearFilters}
              className="flex items-center gap-2 text-sm text-slate-500 hover:text-slate-900"
            >
              <RotateCcw size={16} />
              Clear filters
            </button>
          )}
        </div>

        <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">
              Membership Number
            </label>

            <input
              type="text"
              name="membershipNumber"
              value={filters.membershipNumber}
              onChange={changeFilter}
              placeholder="Enter membership number"
              className="w-full rounded-lg border border-slate-300 px-3 py-2.5 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
            />
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">
              Book Code
            </label>

            <input
              type="text"
              name="bookCode"
              value={filters.bookCode}
              onChange={changeFilter}
              placeholder="Enter book code"
              className="w-full rounded-lg border border-slate-300 px-3 py-2.5 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
            />
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">
              Transaction Type
            </label>

            <select
              name="type"
              value={filters.type}
              onChange={changeFilter}
              className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
            >
              <option value="">All Types</option>
              <option value="BORROW">Borrow</option>
              <option value="RENEW">Renew</option>
              <option value="RETURN">Return</option>
            </select>
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">
              Status
            </label>

            <select
              name="status"
              value={filters.status}
              onChange={changeFilter}
              className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
            >
              <option value="">All Statuses</option>
              <option value="RETURNED">Returned</option>
              <option value="NOT_RETURNED">Not Returned</option>
              <option value="OVERDUE">Overdue</option>
            </select>
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">
              From Date
            </label>

            <input
              type="datetime-local"
              name="from"
              value={filters.from}
              onChange={changeFilter}
              className="w-full rounded-lg border border-slate-300 px-3 py-2.5 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
            />
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">
              To Date
            </label>

            <input
              type="datetime-local"
              name="to"
              value={filters.to}
              onChange={changeFilter}
              className="w-full rounded-lg border border-slate-300 px-3 py-2.5 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
            />
          </div>
        </div>

        <div className="mt-5 flex justify-end">
          <button
            type="button"
            onClick={applyFilters}
            disabled={loading}
            className="flex items-center gap-2 rounded-lg bg-blue-600 px-5 py-2.5 font-medium text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50"
          >
            <Search size={18} />

            {loading ? "Loading..." : "Apply Filters"}
          </button>
        </div>
      </div>

      {error && (
        <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      )}

      <div className="overflow-hidden rounded-xl bg-white shadow">
        <div className="flex flex-col gap-2 border-b px-6 py-5 md:flex-row md:items-center md:justify-between">
          <div>
            <div className="flex items-center gap-2">
              <BarChart3 size={20} className="text-slate-600" />

              <h2 className="text-lg font-semibold text-slate-900">
                Loan History
              </h2>
            </div>
          </div>

          <div className="text-sm text-slate-500">10 records per page</div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full min-w-[800px]">
            <thead>
              <tr className="border-b bg-slate-50 text-left text-sm text-slate-600">
                <th className="px-6 py-4 font-semibold">Membership</th>

                <th className="px-4 py-4 font-semibold">Book Code</th>

                <th className="px-4 py-4 font-semibold">Type</th>

                <th className="px-4 py-4 font-semibold">Request Date</th>

                <th className="px-4 py-4 font-semibold">Due Date</th>

                <th className="px-4 py-4 font-semibold">Return Date</th>

                <th className="px-4 py-4 font-semibold">Status</th>
              </tr>
            </thead>

            <tbody>
              {loading ? (
                <tr>
                  <td
                    colSpan="7"
                    className="px-6 py-12 text-center text-slate-500"
                  >
                    Loading loan history...
                  </td>
                </tr>
              ) : history.length === 0 ? (
                <tr>
                  <td
                    colSpan="7"
                    className="px-6 py-12 text-center text-slate-500"
                  >
                    No loan transactions found.
                  </td>
                </tr>
              ) : (
                history.map((item) => (
                  <tr
                    key={item.id}
                    className="border-b last:border-b-0 hover:bg-slate-50"
                  >
                    <td className="px-6 py-4 font-medium text-slate-800">
                      {item.membershipNumber}
                    </td>

                    <td className="px-4 py-4 text-slate-700">
                      {item.bookCode}
                    </td>

                    <td className="px-4 py-4">
                      <span className="rounded-full bg-blue-100 px-3 py-1 text-xs font-semibold text-blue-700">
                        {item.type}
                      </span>
                    </td>

                    <td className="px-4 py-4 text-sm text-slate-600">
                      {formatDate(item.requestDate)}
                    </td>

                    <td className="px-4 py-4 text-sm text-slate-600">
                      {formatDate(item.dueDate)}
                    </td>

                    <td className="px-4 py-4 text-sm text-slate-600">
                      {formatDate(item.returnDate)}
                    </td>

                    <td className="px-4 py-4">
                      <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-slate-700">
                        {item.status}
                      </span>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {totalPages > 0 && (
          <div className="flex items-center justify-between border-t px-6 py-4">
            <button
              type="button"
              disabled={page === 0 || loading}
              onClick={() => setPage((previous) => previous - 1)}
              className="flex items-center gap-1 rounded-lg border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
            >
              <ChevronLeft size={17} />
              Previous
            </button>

            <span className="text-sm font-medium text-slate-600">
              Page {page + 1} of {totalPages}
            </span>

            <button
              type="button"
              disabled={page + 1 >= totalPages || loading}
              onClick={() => setPage((previous) => previous + 1)}
              className="flex items-center gap-1 rounded-lg border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
            >
              Next
              <ChevronRight size={17} />
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

export default Reports;
