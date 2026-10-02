import { useEffect, useState } from "react";
import FormInput from "../FormInput";

function ReturnModal({
  open,
  onClose,
  onSubmit,
  trackingCode = "",
  membershipNumber = "",
  bookCode = "",
}) {
  const [form, setForm] = useState({
    membershipNumber: "",
    bookCode: "",
    trackingCode: "",
  });
  const [error, setError] = useState("");

  useEffect(() => {
    if (open) {
      setForm({
        membershipNumber,
        bookCode,
        trackingCode,
      });
    } else {
      setForm({ membershipNumber: "", bookCode: "", trackingCode: "" });
      setError("");
    }
  }, [open, trackingCode, membershipNumber, bookCode]);

  if (!open) return null;

  const change = (e) => {
    setForm((current) => ({
      ...current,
      [e.target.name]: e.target.value,
    }));
    setError("");
  };

  const submit = (e) => {
    e.preventDefault();

    const membership = form.membershipNumber.trim();
    const book = form.bookCode.trim();
    const tracking = form.trackingCode.trim();

    if (!tracking && (!membership || !book)) {
      setError(
        "Enter a tracking code, or enter both membership number and book code.",
      );
      return;
    }

    setError("");
    onSubmit({
      membershipNumber: membership || null,
      bookCode: book || null,
      trackingCode: tracking || null,
    });
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-[60] p-4">
      <div className="bg-white rounded-xl p-6 w-full max-w-md shadow-xl">
        <h2 className="text-xl font-bold mb-5">Return Book</h2>

        <form onSubmit={submit} className="space-y-4">
          <FormInput
            label="Tracking Code"
            name="trackingCode"
            value={form.trackingCode}
            onChange={change}
          />

          <div className="text-xs text-gray-500 -mt-2">
            Or use the member and book identifiers below.
          </div>

          <FormInput
            label="Membership Number"
            name="membershipNumber"
            value={form.membershipNumber}
            onChange={change}
          />

          <FormInput
            label="Book Code"
            name="bookCode"
            value={form.bookCode}
            onChange={change}
          />

          {error && (
            <div className="bg-red-100 text-red-700 border border-red-300 p-3 rounded-lg text-sm">
              {error}
            </div>
          )}

          <div className="flex justify-end gap-3">
            <button type="button" onClick={onClose}>
              Cancel
            </button>

            <button
              type="submit"
              className="bg-green-600 text-white px-5 py-2 rounded-lg"
            >
              Return
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default ReturnModal;
