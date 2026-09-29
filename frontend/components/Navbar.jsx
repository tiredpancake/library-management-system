import { Menu } from "lucide-react";

function Navbar({ setOpen }) {
  return (
    <header
      className="
            h-16
            bg-white
            shadow-sm
            flex items-center
            px-6
            justify-between
            "
    >
      <button className="md:hidden" onClick={() => setOpen(true)}>
        <Menu />
      </button>

      <h2 className="font-semibold text-lg">Library Dashboard</h2>

      <div>
        <span
          className="
                    bg-blue-100
                    text-blue-700
                    px-3 py-1
                    rounded-full
                    text-sm
                    "
        >
          Admin
        </span>
      </div>
    </header>
  );
}

export default Navbar;
