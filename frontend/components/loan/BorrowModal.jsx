import { useState } from "react";
import FormInput from "../FormInput";

function BorrowModal({ open, onClose, onSubmit }) {
  const [form, setForm] = useState({
    membershipNumber: "",
    bookCode: "",
  });

  const [errors, setErrors] = useState({});

  if (!open) return null;

  const change = (e) => {
    setForm({
      ...form,

      [e.target.name]: e.target.value,
    });
  };

  const validate = () => {
    let e = {};

    if (!form.membershipNumber) {
      e.membershipNumber = "Membership number is required";
    }

    if (!form.bookCode) {
      e.bookCode = "Book code is required";
    }

    setErrors(e);

    return Object.keys(e).length === 0;
  };

  const submit = (e) => {
    e.preventDefault();

    if (validate()) {
      onSubmit(form);
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
max-w-md
"
      >
        <h2
          className="
text-xl
font-bold
mb-5
"
        >
          Borrow Book
        </h2>

        <form onSubmit={submit} className="space-y-4">
          <FormInput
            label="Membership Number"
            name="membershipNumber"
            value={form.membershipNumber}
            onChange={change}
            required
            error={errors.membershipNumber}
          />

          <FormInput
            label="Book Code"
            name="bookCode"
            value={form.bookCode}
            onChange={change}
            required
            error={errors.bookCode}
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
              Borrow
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default BorrowModal;
