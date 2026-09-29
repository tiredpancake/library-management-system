function StatCard({ title, value, icon }) {
  return (
    <div
      className="
            bg-white
            rounded-xl
            shadow
            p-6
            flex
            justify-between
            items-center
            "
    >
      <div>
        <p className="text-gray-500 text-sm">{title}</p>

        <h3 className="text-3xl font-bold mt-2">{value}</h3>
      </div>

      <div
        className="
                bg-blue-100
                text-blue-600
                p-3
                rounded-xl
                "
      >
        {icon}
      </div>
    </div>
  );
}

export default StatCard;
