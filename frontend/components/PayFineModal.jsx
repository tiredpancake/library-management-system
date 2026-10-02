import { useEffect, useState } from "react";

import FormInput from "./FormInput";

function PayFineModal({ open, onClose, onSubmit }) {
  const [form, setForm] = useState({
    membershipNumber: "",
    amount: "",
  });

  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (open) {
      setForm({
        membershipNumber: "",
        amount: "",
      });

      setErrors({});
      setSubmitting(false);
    }
  }, [open]);

  if (!open) return null;

  const change = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });

    setErrors((current) => ({
      ...current,
      [e.target.name]: "",
      server: "",
    }));
  };

  const validate = () => {
    const e = {};

    if (!form.membershipNumber.trim()) {
      e.membershipNumber = "Membership number is required";
    }

    if (!form.amount) {
      e.amount = "Amount is required";
    } else if (Number(form.amount) <= 0) {
      e.amount = "Amount must be greater than zero";
    }

    setErrors(e);

    return Object.keys(e).length === 0;
  };

  const submit = async (e) => {
    e.preventDefault();

    if (!validate()) return;

    try {
      setSubmitting(true);
      setErrors({});

      await onSubmit({
        membershipNumber: form.membershipNumber.trim(),
        amount: Number(form.amount),
      });
    } catch (err) {
      const response = err?.response?.data;

      setErrors({
        server: response?.message || response?.error || "Payment failed",
      });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-xl p-6 w-full max-w-md shadow-xl">
        <h2 className="text-xl font-bold mb-5">Pay Fine</h2>

        {errors.server && (
          <div className="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {errors.server}
          </div>
        )}

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
            label="Payment Amount"
            name="amount"
            type="number"
            min="0.01"
            step="0.01"
            value={form.amount}
            onChange={change}
            required
            error={errors.amount}
          />

          <div className="flex justify-end gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              disabled={submitting}
              className="px-4 py-2 rounded-lg border border-slate-200 text-slate-700 hover:bg-slate-50 disabled:opacity-50"
            >
              Cancel
            </button>

            <button
              type="submit"
              disabled={submitting}
              className="bg-green-600 text-white px-5 py-2 rounded-lg hover:bg-green-700 disabled:opacity-50"
            >
              {submitting ? "Processing..." : "Pay"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default PayFineModal;
