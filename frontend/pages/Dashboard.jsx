import { useEffect, useState } from "react";
import api from "../api/axios";

function Dashboard() {
  const [stats, setStats] = useState({
    books: 0,
    members: 0,
    activeLoans: 0,
    unpaidFines: 0,
  });

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  useEffect(() => {
    const loadDashboard = async () => {
      try {
        setLoading(true);

        setError("");

        const res = await api.get("/dashboard/summary");

        setStats(res.data);
      } catch (err) {
        console.error("Dashboard loading failed:", err);

        setError("Failed to load dashboard data");
      } finally {
        setLoading(false);
      }
    };

    loadDashboard();
  }, []);

  const cards = [
    {
      title: "Books",
      value: stats.books,
    },

    {
      title: "Members",
      value: stats.members,
    },

    {
      title: "Active Loans",
      value: stats.activeLoans,
    },

    {
      title: "Unpaid Fines",
      value: stats.unpaidFines,
    },
  ];

  return (
    <div>
      <h1
        className="
        text-3xl
        font-bold
        mb-6
        "
      >
        Dashboard
      </h1>

      {loading && (
        <div
          className="
            bg-white
            rounded-xl
            shadow
            p-6
            "
        >
          Loading dashboard...
        </div>
      )}

      {error && (
        <div
          className="
            bg-red-100
            text-red-700
            rounded-xl
            p-4
            mb-5
            "
        >
          {error}
        </div>
      )}

      {!loading && !error && (
        <div
          className="
            grid
            grid-cols-1
            sm:grid-cols-2
            xl:grid-cols-4
            gap-5
            "
        >
          {cards.map((card) => (
            <div
              key={card.title}
              className="
                  bg-white
                  rounded-xl
                  shadow
                  p-6
                  "
            >
              <h2
                className="
                    text-gray-500
                    "
              >
                {card.title}
              </h2>

              <p
                className="
                    text-4xl
                    font-bold
                    mt-4
                    "
              >
                {card.value}
              </p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default Dashboard;
