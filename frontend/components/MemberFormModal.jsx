import { useEffect, useState } from "react";
import FormInput from "./FormInput";

function MemberFormModal({ open, onClose, onSubmit, member }) {
  const emptyForm = {
    fullName: "",
    nationalCode: "",
    birthDate: "",
    membershipType: "INDIVIDUAL",
    phone: "",
    address: "",
    postalCode: "",
    status: "ACTIVE",
  };

  const [form, setForm] = useState(emptyForm);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (member) {
      setForm({
        fullName: member.fullName || "",
        nationalCode: member.nationalCode || "",
        birthDate: member.birthDate || "",
        membershipType: member.membershipType || "INDIVIDUAL",
        phone: member.phone || "",
        address: member.address || "",
        postalCode: member.postalCode || "",
        status: member.status || "ACTIVE",
      });
    } else {
      setForm(emptyForm);
    }

    setErrors({});
  }, [member, open]);

  if (!open) return null;

  const change = (e) => {
    const { name, value } = e.target;

    setForm((current) => ({
      ...current,
      [name]: value,
    }));

    setErrors((current) => ({
      ...current,
      [name]: "",
      server: "",
    }));
  };

  const validate = () => {
    const e = {};

    if (!form.fullName.trim()) e.fullName = "Full name is required";
    if (!form.nationalCode.trim()) e.nationalCode = "National code is required";
    if (!form.birthDate) e.birthDate = "Birth date is required";
    if (!form.phone.trim()) e.phone = "Phone is required";
    if (!form.address.trim()) e.address = "Address is required";
    if (!form.postalCode.trim()) e.postalCode = "Postal code is required";

    setErrors(e);

    return Object.keys(e).length === 0;
  };

  const submit = async (e) => {
    e.preventDefault();

    if (!validate()) return;

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
        <div className="p-6 border-b">
          <h2 className="text-xl font-bold">
            {member ? "Edit Member" : "Add Member"}
          </h2>

          {errors.server && (
            <p className="text-red-600 text-sm mt-3">{errors.server}</p>
          )}
        </div>

        <form onSubmit={submit} className="flex flex-col min-h-0">
          <div className="overflow-y-auto p-6 space-y-3">
            <FormInput
              label="Full Name"
              name="fullName"
              value={form.fullName}
              onChange={change}
              error={errors.fullName}
              required
            />

            <FormInput
              label="National Code"
              name="nationalCode"
              value={form.nationalCode}
              onChange={change}
              error={errors.nationalCode}
              required
            />

            <FormInput
              label="Birth Date"
              name="birthDate"
              type="date"
              value={form.birthDate}
              onChange={change}
              error={errors.birthDate}
              required
            />

            <div>
              <label className="block text-sm font-medium mb-1">
                Membership Type
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
                <option value="INDIVIDUAL">INDIVIDUAL</option>
                <option value="ORGANIZATIONAL">ORGANIZATIONAL</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">Status</label>

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
                <option value="BLOCKED">BLOCKED</option>
              </select>
            </div>

            <FormInput
              label="Phone"
              name="phone"
              value={form.phone}
              onChange={change}
              error={errors.phone}
              required
            />

            <FormInput
              label="Address"
              name="address"
              value={form.address}
              onChange={change}
              error={errors.address}
              required
            />

            <FormInput
              label="Postal Code"
              name="postalCode"
              value={form.postalCode}
              onChange={change}
              error={errors.postalCode}
              required
            />
          </div>

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
              type="submit"
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
