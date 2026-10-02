import { useEffect, useState } from "react";
import FormInput from "../FormInput";

const EMPTY_FORM = {
  membershipNumber: "",
  bookCode: "",
};

function BorrowModal({ open, onClose, onSubmit, error = "" }) {
  const [form, setForm] = useState(EMPTY_FORM);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (!open) {
      setForm(EMPTY_FORM);
      setErrors({});
    }
  }, [open]);

  if (!open) return null;

  const change = (e) => {
    setForm((current) => ({
      ...current,
      [e.target.name]: e.target.value,
    }));

    setErrors((current) => ({
      ...current,
      [e.target.name]: "",
    }));
  };

  const close = () => {
    setForm(EMPTY_FORM);
    setErrors({});
    onClose();
  };

  const validate = () => {
    const e = {};

    if (!form.membershipNumber.trim()) {
      e.membershipNumber = "Membership number is required";
    }

    if (!form.bookCode.trim()) {
      e.bookCode = "Book code is required";
    }

    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const submit = (e) => {
    e.preventDefault();

    if (validate()) {
      onSubmit({
        membershipNumber: form.membershipNumber.trim(),
        bookCode: form.bookCode.trim(),
      });
    }
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white rounded-xl p-6 w-full max-w-md">
        <h2 className="text-xl font-bold mb-5">Borrow Book</h2>

        <form onSubmit={submit} className="space-y-4">
          {error && (
            <div className="bg-red-100 text-red-700 border border-red-300 p-3 rounded-lg text-sm">
              {error}
            </div>
          )}
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

          <div className="flex justify-end gap-3 mt-5">
            <button type="button" onClick={close}>
              Cancel
            </button>

            <button
              type="submit"
              className="bg-blue-600 text-white px-5 py-2 rounded-lg"
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
