import { BookOpen, Users, ArrowLeftRight, BarChart3 } from "lucide-react";

import { useNavigate } from "react-router-dom";

const features = [
  {
    title: "Book Management",
    description:
      "Manage books, categories, availability and inventory efficiently.",
    icon: BookOpen,
  },
  {
    title: "Member Management",
    description: "Track members, profiles and activity history.",
    icon: Users,
  },
  {
    title: "Loan Management",
    description: "Handle borrowing, returning and renewals easily.",
    icon: ArrowLeftRight,
  },
  {
    title: "Reports & Analytics",
    description: "Monitor library performance with detailed reports.",
    icon: BarChart3,
  },
];

function Landing() {
  const navigate = useNavigate();

  return (
    <div
      className="
            min-h-screen
            bg-slate-50
            "
    >

      <nav
        className="
                flex
                justify-between
                items-center
                px-6
                md:px-16
                py-6
                bg-white
                shadow-sm
                "
      >
        <h1
          className="
                    text-2xl
                    font-bold
                    text-slate-800
                    "
        >
          📚 Library
        </h1>

        <button
          onClick={() => navigate("/login")}
          className="
                    bg-blue-600
                    text-white
                    px-5
                    py-2
                    rounded-lg
                    hover:bg-blue-700
                    transition
                    "
        >
          Login
        </button>
      </nav>


      <section
        className="
                px-6
                md:px-16
                py-20
                text-center
                "
      >
        <h2
          className="
                    text-4xl
                    md:text-6xl
                    font-bold
                    text-slate-900
                    leading-tight
                    "
        >
          Smart Library
          <br />
          Management System
        </h2>

        <p
          className="
                    mt-6
                    max-w-2xl
                    mx-auto
                    text-lg
                    text-slate-600
                    "
        >
          A modern platform to manage books, members, loans and library
          operations in one place.
        </p>

      </section>


      <section
        className="
                px-6
                md:px-16
                pb-20
                "
      >
        <div
          className="
                    grid
                    grid-cols-1
                    sm:grid-cols-2
                    lg:grid-cols-4
                    gap-6
                    "
        >
          {features.map((feature) => {
            const Icon = feature.icon;

            return (
              <div
                key={feature.title}
                className="
                                    bg-white
                                    rounded-2xl
                                    p-6
                                    shadow-sm
                                    hover:shadow-lg
                                    transition
                                    "
              >
                <div
                  className="
                                        bg-blue-100
                                        text-blue-600
                                        w-12
                                        h-12
                                        rounded-xl
                                        flex
                                        items-center
                                        justify-center
                                        mb-5
                                        "
                >
                  <Icon />
                </div>

                <h3
                  className="
                                        font-bold
                                        text-lg
                                        "
                >
                  {feature.title}
                </h3>

                <p
                  className="
                                        mt-3
                                        text-slate-500
                                        text-sm
                                        "
                >
                  {feature.description}
                </p>
              </div>
            );
          })}
        </div>
      </section>


      <footer
        className="
                text-center
                py-6
                text-slate-500
                text-sm
                "
      >
        © 2026 Library Management System
      </footer>
    </div>
  );
}

export default Landing;
