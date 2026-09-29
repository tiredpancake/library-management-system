import { BarChart3, Download, Filter } from "lucide-react";

const reports = [
  {
    title: "Total Books",
    value: 540,
  },
  {
    title: "Active Members",
    value: 120,
  },
  {
    title: "Current Loans",
    value: 35,
  },
  {
    title: "Unpaid Fines",
    value: 12,
  },
];

const loanHistory = [
  {
    member: "Ali Ahmadi",
    book: "Database Systems",
    type: "BORROW",
    date: "2026-09-20",
    status: "SUCCESS",
  },
  {
    member: "Sara Mohammadi",
    book: "Marine Engineering",
    type: "RETURN",
    date: "2026-09-25",
    status: "SUCCESS",
  },
  {
    member: "Reza Karimi",
    book: "Physics",
    type: "RENEW",
    date: "2026-09-27",
    status: "SUCCESS",
  },
];

function Reports() {
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
        <div>
          <h1 className="text-2xl font-bold">Reports</h1>

          <p className="text-slate-500">Analyze library activity</p>
        </div>

        <button
          className="
                    flex
                    items-center
                    gap-2
                    bg-blue-600
                    text-white
                    px-4
                    py-2
                    rounded-lg
                    "
        >
          <Download size={18} />
          Export
        </button>
      </div>

      {/* Summary Cards */}

      <div
        className="
                grid
                grid-cols-1
                sm:grid-cols-2
                lg:grid-cols-4
                gap-6
                mb-6
                "
      >
        {reports.map((item) => (
          <div
            key={item.title}
            className="
                            bg-white
                            rounded-xl
                            shadow
                            p-6
                            "
          >
            <p
              className="
                                text-slate-500
                                text-sm
                                "
            >
              {item.title}
            </p>

            <h2
              className="
                                text-3xl
                                font-bold
                                mt-2
                                "
            >
              {item.value}
            </h2>
          </div>
        ))}
      </div>

      {/* Filter */}

      <div
        className="
                bg-white
                rounded-xl
                shadow
                p-5
                mb-6
                "
      >
        <div
          className="
                    flex
                    items-center
                    gap-3
                    mb-4
                    "
        >
          <Filter size={20} />

          <h2 className="font-semibold">Filters</h2>
        </div>

        <div
          className="
                    grid
                    grid-cols-1
                    md:grid-cols-3
                    gap-4
                    "
        >
          <input
            type="date"
            className="
                        border
                        rounded-lg
                        p-3
                        "
          />

          <input
            type="date"
            className="
                        border
                        rounded-lg
                        p-3
                        "
          />

          <select
            className="
                        border
                        rounded-lg
                        p-3
                        "
          >
            <option>All Types</option>

            <option>Borrow</option>

            <option>Return</option>

            <option>Renew</option>
          </select>
        </div>
      </div>

      {/* History Table */}

      <div
        className="
                bg-white
                rounded-xl
                shadow
                p-5
                "
      >
        <div className="flex gap-2 items-center mb-5">
          <BarChart3 />

          <h2 className="font-semibold">Loan History</h2>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr
                className="
                        border-b
                        text-left
                        "
              >
                <th className="p-3">Member</th>

                <th>Book</th>

                <th>Type</th>

                <th>Date</th>

                <th>Status</th>
              </tr>
            </thead>

            <tbody>
              {loanHistory.map((item, index) => (
                <tr
                  key={index}
                  className="
                                border-b
                                "
                >
                  <td className="p-3">{item.member}</td>

                  <td>{item.book}</td>

                  <td>{item.type}</td>

                  <td>{item.date}</td>

                  <td>
                    <span
                      className="
                                        bg-green-100
                                        text-green-700
                                        px-3
                                        py-1
                                        rounded-full
                                        text-sm
                                        "
                    >
                      {item.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

export default Reports;
