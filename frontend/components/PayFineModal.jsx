import { useState } from "react";

import FormInput from "./FormInput";

function PayFineModal({ open, onClose, onSubmit }) {
  const [form, setForm] = useState({
    membershipNumber: "",
    amount: "",
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

    if (!form.amount) {
      e.amount = "Amount is required";
    } else if (Number(form.amount) <= 0) {
      e.amount = "Amount must be greater than zero";
    }

    setErrors(e);

    return Object.keys(e).length === 0;
  };

  const submit = (e) => {
    e.preventDefault();

    if (validate()) {
      onSubmit({
        ...form,

        amount: Number(form.amount),
      });
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
          Pay Fine
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
            label="Amount"
            name="amount"
            type="number"
            value={form.amount}
            onChange={change}
            required
            error={errors.amount}
          />

          <div
            className="
flex
justify-end
gap-3
"
          >
            <button type="button" onClick={onClose}>
              Cancel
            </button>

            <button
              className="
bg-green-600
text-white
px-5
py-2
rounded-lg
"
            >
              Pay
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default PayFineModal;
