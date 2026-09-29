import { useState } from "react";
import FormInput from "./FormInput";

function MemberFormModal({ open, onClose, onSubmit }) {
  const [form, setForm] = useState({
    fullName: "",
    nationalCode: "",
    birthDate: "",
    membershipType: "NORMAL",
    phone: "",
    address: "",
    postalCode: "",
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

    [
      "fullName",
      "nationalCode",
      "birthDate",
      "phone",
      "address",
      "postalCode",
    ].forEach((field) => {
      if (!form[field]) {
        e[field] = `${field} is required`;
      }
    });

    if (!form.membershipType) {
      e.membershipType = "Membership type is required";
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
          Add Member
        </h2>

        <form onSubmit={submit} className="space-y-3">
          <FormInput
            label="Full Name"
            name="fullName"
            value={form.fullName}
            onChange={change}
            required
            error={errors.fullName}
          />

          <FormInput
            label="National Code"
            name="nationalCode"
            value={form.nationalCode}
            onChange={change}
            required
            error={errors.nationalCode}
          />

          <FormInput
            label="Birth Date"
            name="birthDate"
            type="date"
            value={form.birthDate}
            onChange={change}
            required
            error={errors.birthDate}
          />

          <div>
            <label>
              Membership Type
              <span className="text-red-500">*</span>
            </label>

            <select
              name="membershipType"
              value={form.membershipType}
              onChange={change}
              className="
w-full
border
rounded-lg
px-3
py-2
"
            >
              <option value="NORMAL">NORMAL</option>

              <option value="VIP">VIP</option>
            </select>
          </div>

          <FormInput
            label="Phone"
            name="phone"
            value={form.phone}
            onChange={change}
            required
            error={errors.phone}
          />

          <FormInput
            label="Address"
            name="address"
            value={form.address}
            onChange={change}
            required
            error={errors.address}
          />

          <FormInput
            label="Postal Code"
            name="postalCode"
            value={form.postalCode}
            onChange={change}
            required
            error={errors.postalCode}
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

export default MemberFormModal;
