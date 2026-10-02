function BookDetailsModal({ book, onClose }) {
  if (!book) return null;

  const borrowedCopies =
    book.totalCopies != null && book.availableCopies != null
      ? book.totalCopies - book.availableCopies
      : null;

  return (
    <div
      className="
      fixed
      inset-0
      bg-black/40
      z-50
      flex
      items-center
      justify-center
      p-4
      "
    >
      <div
        className="
        bg-white
        rounded-xl
        shadow-xl
        w-full
        max-w-lg
        max-h-[85vh]
        flex
        flex-col
        "
      >
        <div
          className="
          p-6
          border-b
          "
        >
          <h2
            className="
            text-xl
            font-bold
            "
          >
            Book Details
          </h2>
        </div>

        <div
          className="
          p-6
          overflow-y-auto
          space-y-4
          "
        >
          <Detail label="Book Code" value={book.bookCode} />

          <Detail label="ISBN" value={book.isbn} />

          <Detail label="Title" value={book.title} />

          <Detail label="Author" value={book.author} />

          <Detail label="Category" value={book.category} />

          <Detail label="Publisher" value={book.publisher} />

          <Detail label="Publish Year" value={book.publishYear} />

          <Detail label="Total Copies" value={book.totalCopies} />

          <Detail label="Available Copies" value={book.availableCopies} />

          <Detail label="Borrowed Copies" value={borrowedCopies} />

          <Detail label="Price" value={book.price} />

          <Detail label="Status" value={book.status} />

          <Detail
            label="Created At"
            value={
              book.createdAt ? new Date(book.createdAt).toLocaleString() : null
            }
          />

          <Detail
            label="Last Updated"
            value={
              book.updatedAt ? new Date(book.updatedAt).toLocaleString() : null
            }
          />
        </div>

        <div
          className="
          p-6
          border-t
          flex
          justify-end
          "
        >
          <button
            type="button"
            onClick={onClose}
            className="
            bg-blue-600
            text-white
            px-5
            py-2
            rounded-lg
            "
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
}

function Detail({ label, value }) {
  const displayValue =
    value === null || value === undefined || value === "" ? "-" : value;

  return (
    <div
      className="
      border-b
      pb-2
      "
    >
      <p
        className="
        text-sm
        text-gray-500
        "
      >
        {label}
      </p>

      <p
        className="
        font-medium
        break-words
        "
      >
        {displayValue}
      </p>
    </div>
  );
}

export default BookDetailsModal;
