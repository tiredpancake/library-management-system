import { useEffect, useState } from "react";

import { Plus, Pencil, Trash } from "lucide-react";

import { getBooks, createBook, updateBook, deleteBook } from "../api/bookApi";

import BookFormModal from "../components/BookFormModal";

import { getErrorMessage } from "../utils/errorHandler";

function Books() {
  const [books, setBooks] = useState([]);

  const [open, setOpen] = useState(false);

  const [selected, setSelected] = useState(null);

  const [error, setError] = useState("");

  const loadBooks = async () => {
    try {
      const res = await getBooks();

      setBooks(res.data);
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  useEffect(() => {
    loadBooks();
  }, []);

  const saveBook = async (data) => {
    if (selected) {
      await updateBook(selected.id, data);
    } else {
      await createBook(data);
    }

    await loadBooks();

    setOpen(false);

    setSelected(null);
  };

  const removeBook = async (id) => {
    const confirmDelete = window.confirm("Delete book?");

    if (!confirmDelete) return;

    try {
      await deleteBook(id);

      loadBooks();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  return (
    <div>
      <div
        className="
        flex
        justify-between
        mb-6
        "
      >
        <h1
          className="
          text-2xl
          font-bold
          "
        >
          Books
        </h1>

        <button
          onClick={() => {
            setSelected(null);

            setOpen(true);
          }}
          className="
          bg-blue-600
          text-white
          px-4
          py-2
          rounded-lg
          flex
          gap-2
          items-center
          "
        >
          <Plus size={18} />
          Add Book
        </button>
      </div>

      {error && (
        <div
          className="
          bg-red-100
          text-red-700
          border
          border-red-300
          p-3
          rounded-lg
          mb-4
          "
        >
          {error}
        </div>
      )}

      <div
        className="
        bg-white
        rounded-xl
        shadow
        p-5
        overflow-x-auto
        "
      >
        <table className="w-full">
          <thead>
            <tr
              className="
              border-b
              text-left
              "
            >
              <th className="p-3">Title</th>

              <th>Author</th>

              <th>ISBN</th>

              <th>Copies</th>

              <th>Action</th>
            </tr>
          </thead>

          <tbody>
            {books.map((book) => (
              <tr
                key={book.id}
                className="
                  border-b
                  "
              >
                <td className="p-3">{book.title}</td>

                <td>{book.author}</td>

                <td>{book.isbn}</td>

                <td>
                  {book.availableCopies}/{book.totalCopies}
                </td>

                <td>
                  <div
                    className="
                      flex
                      gap-3
                      "
                  >
                    <button
                      onClick={() => {
                        setSelected(book);

                        setOpen(true);
                      }}
                    >
                      <Pencil size={18} />
                    </button>

                    <button onClick={() => removeBook(book.id)}>
                      <Trash size={18} />
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <BookFormModal
        open={open}
        onClose={() => {
          setOpen(false);

          setSelected(null);
        }}
        book={selected}
        onSubmit={saveBook}
      />
    </div>
  );
}

export default Books;
