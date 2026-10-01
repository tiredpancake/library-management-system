import { useEffect, useState } from "react";

import FormInput from "./FormInput";

function BookFormModal({ open, onClose, onSubmit, book }) {
  const emptyForm = {
    isbn: "",
    title: "",
    author: "",
    category: "",
    publisher: "",
    publishYear: "",
    totalCopies: "",
    price: "",
    status: "ACTIVE",
  };

  const [form, setForm] = useState(emptyForm);

  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (book) {
      setForm({
        isbn: book.isbn || "",

        title: book.title || "",

        author: book.author || "",

        category: book.category || "",

        publisher: book.publisher || "",

        publishYear: book.publishYear || "",

        totalCopies: book.totalCopies || "",

        price: book.price || "",

        status: book.status || "ACTIVE",
      });
    } else {
      setForm(emptyForm);

      setErrors({});
    }
  }, [book, open]);

  if (!open) return null;

  const change = (e) => {
    setForm({
      ...form,

      [e.target.name]: e.target.value,
    });

    setErrors({
      ...errors,

      [e.target.name]: "",
    });
  };

  const validate = () => {
    let e = {};

    if (!book && !form.isbn) {
      e.isbn = "ISBN is required";
    }

    if (!form.title) {
      e.title = "Title is required";
    }

    if (!form.author) {
      e.author = "Author is required";
    }

    if (!form.category) {
      e.category = "Category is required";
    }

    if (!form.publisher) {
      e.publisher = "Publisher is required";
    }

    if (!form.publishYear) {
      e.publishYear = "Publish year is required";
    }

    if (!form.totalCopies) {
      e.totalCopies = "Total copies is required";
    }

    if (Number(form.totalCopies) < 0) {
      e.totalCopies = "Total copies cannot be negative";
    }

    setErrors(e);

    return Object.keys(e).length === 0;
  };

  const submit = async (e) => {
    e.preventDefault();

    if (validate()) {
      try {
        await onSubmit(form);
      } catch (err) {
        const response = err.response?.data;

        if (response?.field) {
          setErrors({
            [response.field]: response.message,
          });
        } else {
          setErrors({
            server: response?.message || "Something went wrong",
          });
        }
      }
    }
  };

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
max-h-[90vh]
flex
flex-col
"
      >
        {/* Header */}

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
            {book ? "Edit Book" : "Add Book"}
          </h2>

          {errors.server && (
            <p
              className="
text-red-600
text-sm
mt-3
"
            >
              {errors.server}
            </p>
          )}
        </div>

        {/* Body */}

        <div
          className="
overflow-y-auto
p-6
"
        >
          <form
            onSubmit={submit}
            className="
space-y-3
"
          >
            {!book && (
              <FormInput
                label="ISBN"
                name="isbn"
                value={form.isbn}
                onChange={change}
                required
                error={errors.isbn}
              />
            )}

            <FormInput
              label="Title"
              name="title"
              value={form.title}
              onChange={change}
              required
              error={errors.title}
            />

            <FormInput
              label="Author"
              name="author"
              value={form.author}
              onChange={change}
              required
              error={errors.author}
            />

            <FormInput
              label="Category"
              name="category"
              value={form.category}
              onChange={change}
              required
              error={errors.category}
            />

            <FormInput
              label="Publisher"
              name="publisher"
              value={form.publisher}
              onChange={change}
              required
              error={errors.publisher}
            />

            <FormInput
              label="Publish Year"
              name="publishYear"
              type="number"
              value={form.publishYear}
              onChange={change}
              required
              error={errors.publishYear}
            />

            <FormInput
              label="Total Copies"
              name="totalCopies"
              type="number"
              value={form.totalCopies}
              onChange={change}
              required
              error={errors.totalCopies}
            />

            <FormInput
              label="Price"
              name="price"
              type="number"
              value={form.price}
              onChange={change}
              error={errors.price}
            />

            <div>
              <label>
                Status
                <span className="text-red-500 ml-1">*</span>
              </label>

              <select
                name="status"
                value={form.status}
                onChange={change}
                className="
w-full
border
rounded-lg
px-3
py-2
"
              >
                <option value="ACTIVE">ACTIVE</option>

                <option value="INACTIVE">INACTIVE</option>

                <option value="DELETED">DELETED</option>
              </select>
            </div>
          </form>
        </div>

        {/* Footer */}

        <div
          className="
p-6
border-t
flex
justify-end
gap-3
"
        >
          <button
            type="button"
            onClick={onClose}
            className="
border
px-4
py-2
rounded-lg
"
          >
            Cancel
          </button>

          <button
            onClick={submit}
            className="
bg-blue-600
text-white
px-5
py-2
rounded-lg
"
          >
            Save
          </button>
        </div>
      </div>
    </div>
  );
}

export default BookFormModal;
