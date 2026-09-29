import { useState } from "react";
import FormInput from "./FormInput";
import { mapBackendError } from "../utils/errorHandler";
function BookFormModal({ open, onClose, onSubmit, book }) {
  const [form, setForm] = useState({
    isbn: book?.isbn || "",
    title: book?.title || "",
    author: book?.author || "",
    category: book?.category || "",
    publisher: book?.publisher || "",
    publishYear: book?.publishYear || "",
    totalCopies: book?.totalCopies || "",
    price: book?.price || "",
  });

  const [errors, setErrors] = useState({});

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

    setErrors(e);

    return Object.keys(e).length === 0;
  };

  const submit = async (e) => {
    e.preventDefault();

    if (validate()) {
      try {
        await onSubmit(form);
      } catch (err) {
        setErrors({
          ...mapBackendError(err),
        });
      }
    }
  };

  return (
    <div
      className="
fixed inset-0
bg-black/40
flex
items-center
justify-center
z-50
"
    >
      <div
        className="
bg-white
rounded-xl
p-6
w-full
max-w-lg
"
      >
        <h2
          className="
text-xl
font-bold
mb-5
"
        >
          {book ? "Edit Book" : "Add Book"}
        </h2>

        <form onSubmit={submit} className="space-y-3">
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
          />

          <div
            className="
flex
justify-end
gap-3
mt-5
"
          >
            <button type="button" onClick={onClose}>
              Cancel
            </button>

            <button
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
        </form>
      </div>
    </div>
  );
}

export default BookFormModal;
