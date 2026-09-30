import { NavLink, useNavigate } from "react-router-dom";

function Sidebar() {
  const navigate = useNavigate();

  const logout = () => {
    localStorage.removeItem("token");

    navigate("/login");
  };

  const links = [
    {
      name: "Dashboard",
      path: "/dashboard",
    },

    {
      name: "Books",
      path: "/books",
    },

    {
      name: "Members",
      path: "/members",
    },

    {
      name: "Loans",
      path: "/loans",
    },

    {
      name: "Fines",
      path: "/fines",
    },

    {
      name: "Reports",
      path: "/reports",
    },
  ];

  return (
    <div
      className="
      w-64
      min-h-screen
      bg-slate-900
      text-white
      p-5
      flex
      flex-col
      "
    >
      <h1
        className="
        text-xl
        font-bold
        mb-8
        "
      >
        Library System
      </h1>

      <nav
        className="
        space-y-2
        flex-1
        "
      >
        {links.map((link) => (
          <NavLink
            key={link.path}
            to={link.path}
            className={({ isActive }) =>
              `
                block
                px-4
                py-3
                rounded-lg
                transition

                ${isActive ? "bg-blue-600" : "hover:bg-slate-700"}

                `
            }
          >
            {link.name}
          </NavLink>
        ))}
      </nav>

      <button
        onClick={logout}
        className="
        bg-red-600
        hover:bg-red-700
        py-2
        rounded-lg
        "
      >
        Logout
      </button>
    </div>
  );
}

export default Sidebar;
