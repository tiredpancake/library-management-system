import { BarChart3, Download, Filter } from "lucide-react";
import { useEffect, useState } from "react";

import { getLoanHistory } from "../api/loanApi";

function Reports() {
  const [history, setHistory] = useState([]);
  const [filters, setFilters] = useState({
    membershipNumber: "",
    bookCode: "",
    type: "",
    status: "",
    state: "",
    from: "",
    to: "",
  });
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [error, setError] = useState("");

  const loadHistory = async (currentPage = 0, activeFilters = filters) => {
    try {
      setError("");
      const res = await getLoanHistory({
        ...activeFilters,
        page: currentPage,
        size: 10,
      });
      setHistory(res.data.content);
      setTotalPages(res.data.totalPages);
    } catch (err) {
      setError(err?.response?.data?.message || "Failed to load loan history");
    }
  };

  useEffect(() => {
    loadHistory(page);
  }, [page]);

  const changeFilter = (e) => {
    setFilters((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const search = () => {
    setPage(0);
    loadHistory(0, filters);
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold">Reports</h1>
        </div>

        <button
          type="button"
          className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg"
        >
          <Download size={18} />
          Export
        </button>
      </div>

      <div className="bg-white rounded-xl shadow p-5 mb-6">
        <div className="flex items-center gap-3 mb-4">
          <Filter size={20} />
          <h2 className="font-semibold">Filters</h2>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <input
            name="membershipNumber"
            value={filters.membershipNumber}
            onChange={changeFilter}
            placeholder="Membership Number"
            className="border rounded-lg p-3"
          />

          <input
            name="bookCode"
            value={filters.bookCode}
            onChange={changeFilter}
            placeholder="Book Code"
            className="border rounded-lg p-3"
          />

          <select
            name="type"
            value={filters.type}
            onChange={changeFilter}
            className="border rounded-lg p-3"
          >
            <option value="">All Types</option>
            <option value="BORROW">Borrow</option>
            <option value="RETURN">Return</option>
            <option value="RENEW">Renew</option>
          </select>

          <select
            name="status"
            value={filters.status}
            onChange={changeFilter}
            className="border rounded-lg p-3"
          >
            <option value="">All Statuses</option>
            <option value="SUCCESS">Success</option>
            <option value="PENDING">Pending</option>
            <option value="FAILED">Failed</option>
          </select>

          <select
            name="state"
            value={filters.state}
            onChange={changeFilter}
            className="border rounded-lg p-3"
          >
            <option value="">All States</option>
            <option value="RETURNED">Returned</option>
            <option value="NOT_RETURNED">Not Returned</option>
            <option value="OVERDUE">Overdue</option>
          </select>

          <input
            type="datetime-local"
            name="from"
            value={filters.from}
            onChange={changeFilter}
            className="border rounded-lg p-3"
          />

          <input
            type="datetime-local"
            name="to"
            value={filters.to}
            onChange={changeFilter}
            className="border rounded-lg p-3"
          />

          <button
            type="button"
            onClick={search}
            className="bg-green-600 text-white rounded-lg px-5"
          >
            Search
          </button>
        </div>

        {error && <p className="text-red-600 text-sm mt-4">{error}</p>}
      </div>

      <div className="bg-white rounded-xl shadow p-5">
        <div className="flex gap-2 items-center mb-5">
          <BarChart3 />
          <h2 className="font-semibold">Loan History</h2>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full min-w-[900px]">
            <thead>
              <tr className="border-b text-left">
                <th className="p-3">Membership</th>
                <th>Book Code</th>
                <th>Type</th>
                <th>Request Date</th>
                <th>Status</th>
              </tr>
            </thead>

            <tbody>
              {history.map((item) => (
                <tr key={item.id} className="border-b">
                  <td className="p-3">{item.membershipNumber}</td>
                  <td>{item.bookCode}</td>
                  <td>{item.type}</td>
                  <td>{new Date(item.requestDate).toLocaleString()}</td>
                  <td>
                    <span className="bg-green-100 text-green-700 px-3 py-1 rounded-full text-sm">
                      {item.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="flex justify-center gap-4 mt-5 items-center">
          <button
            type="button"
            disabled={page === 0}
            onClick={() => setPage(page - 1)}
            className="px-4 py-2 border rounded disabled:opacity-50"
          >
            Previous
          </button>

          <span>
            {totalPages === 0 ? 0 : page + 1} / {totalPages}
          </span>

          <button
            type="button"
            disabled={page + 1 >= totalPages}
            onClick={() => setPage(page + 1)}
            className="px-4 py-2 border rounded disabled:opacity-50"
          >
            Next
          </button>
        </div>
      </div>
    </div>
  );
}

export default Reports;
