import Sidebar from "../components/layout/Sidebar";

function MainLayout({ children }) {
  return (
    <div
      className="
flex
min-h-screen
bg-slate-100
"
    >
      <Sidebar />

      <main
        className="
min-w-0
flex-1
p-8
overflow-x-hidden
"
      >
        {children}
      </main>
    </div>
  );
}

export default MainLayout;
