import { useEffect, useState } from "react";

import { Eye, Pencil, Plus, Search, X } from "lucide-react";

import {
  getBooks,
  getBookByIsbn,
  createBook,
  updateBook,
  deleteBook,
} from "../api/bookApi";

import BookFormModal from "../components/BookFormModal";
import BookDetailsModal from "../components/BookDetailsModal";

import { getErrorMessage } from "../src/utils/errorHandler";

function Books() {
  const [books, setBooks] = useState([]);

  const [open, setOpen] = useState(false);

  const [selected, setSelected] = useState(null);

  const [selectedDetails, setSelectedDetails] = useState(null);

  const [error, setError] = useState("");

  const [searchIsbn, setSearchIsbn] = useState("");

  const [searchError, setSearchError] = useState("");

  const [searching, setSearching] = useState(false);

  const loadBooks = async () => {
    try {
      setError("");

      const res = await getBooks();

      setBooks(res.data);
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  useEffect(() => {
    loadBooks();
  }, []);

  const searchBookByIsbn = async () => {
    const isbn = searchIsbn.trim();

    if (!isbn) {
      setSearchError("Enter an ISBN.");
      return;
    }

    setSearching(true);
    setSearchError("");

    try {
      const response = await getBookByIsbn(isbn);

      setSelectedDetails(response.data);
    } catch (err) {
      setSelectedDetails(null);

      setSearchError(
        err.response?.data?.message ||
          err.response?.data?.error ||
          "Book not found.",
      );
    } finally {
      setSearching(false);
    }
  };

  const clearSearch = () => {
    setSearchIsbn("");
    setSearchError("");
    setSelectedDetails(null);
    loadBooks();
  };

  const saveBook = async (data) => {
    try {
      if (selected) {
        await updateBook(selected.id, data);
      } else {
        await createBook(data);
      }

      await loadBooks();

      setOpen(false);

      setSelected(null);
    } catch (err) {
      throw err;
    }
  };

  const viewBook = (book) => {
    setSelectedDetails(book);
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
          items-center
          gap-2
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
        mb-6
        "
      >
        <div className="flex items-center gap-2 mb-4">
          <Search size={19} />

          <h2 className="font-semibold">Find Book</h2>
        </div>

        <div className="flex gap-2">
          <input
            value={searchIsbn}
            onChange={(e) => {
              setSearchIsbn(e.target.value);
              setSearchError("");
            }}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                searchBookByIsbn();
              }
            }}
            placeholder="Enter ISBN"
            className="
            w-full
            border
            rounded-lg
            px-3
            py-2
            "
          />

          <button
            type="button"
            onClick={searchBookByIsbn}
            disabled={searching}
            className="
            bg-blue-600
            text-white
            px-4
            py-2
            rounded-lg
            disabled:opacity-50
            "
          >
            {searching ? "Searching..." : "Search"}
          </button>
        </div>

        {searchError && (
          <div className="mt-3 text-sm text-red-600">{searchError}</div>
        )}

        {searchIsbn && (
          <button
            type="button"
            onClick={clearSearch}
            className="
            mt-3
            flex
            items-center
            gap-1
            text-sm
            text-gray-600
            hover:text-gray-900
            "
          >
            <X size={15} />
            Clear search
          </button>
        )}
      </div>

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
            <tr className="border-b text-left">
              <th className="p-3">Title</th>

              <th>ISBN</th>

              <th>Category</th>

              <th>Copies</th>

              <th>Status</th>

              <th>Action</th>
            </tr>
          </thead>

          <tbody>
            {books.map((book) => (
              <tr key={book.id} className="border-b">
                <td className="p-3">{book.title}</td>

                <td>{book.isbn}</td>

                <td>{book.category}</td>

                <td>
                  {book.availableCopies}/{book.totalCopies}
                </td>

                <td>{book.status}</td>

                <td>
                  <div className="flex gap-3">
                    <button
                      type="button"
                      onClick={() => viewBook(book)}
                      className="
                      flex
                      items-center
                      gap-1
                      text-blue-600
                      hover:text-blue-800
                      "
                    >
                      <Eye size={17} />
                      View
                    </button>

                    <button
                      type="button"
                      onClick={() => {
                        setSelected(book);
                        setOpen(true);
                      }}
                      className="
                      text-green-600
                      hover:text-green-800
                      "
                    >
                      <Pencil size={18} />
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
        book={selected}
        onClose={() => {
          setOpen(false);
          setSelected(null);
        }}
        onSubmit={saveBook}
      />

      <BookDetailsModal
        book={selectedDetails}
        onClose={() => {
          setSelectedDetails(null);
        }}
      />
    </div>
  );
}

export default Books;
