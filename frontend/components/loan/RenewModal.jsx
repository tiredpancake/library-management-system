import { useState, useEffect } from "react";
import FormInput from "../FormInput";

function RenewModal({ open, onClose, onSubmit, trackingCode = "" }) {
  const [code, setCode] = useState("");

  const [errors, setErrors] = useState({});

  useEffect(() => {
    setCode(trackingCode);
  }, [trackingCode]);

  if (!open) return null;

  const validate = () => {
    let e = {};

    if (!code) {
      e.trackingCode = "Tracking code is required";
    }

    setErrors(e);

    return Object.keys(e).length === 0;
  };

  const submit = (e) => {
    e.preventDefault();

    if (validate()) {
      onSubmit({
        trackingCode: code,
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
          Renew Loan
        </h2>

        <form onSubmit={submit} className="space-y-4">
          <FormInput
            label="Tracking Code"
            name="trackingCode"
            value={code}
            onChange={(e) => setCode(e.target.value)}
            required
            error={errors.trackingCode}
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
bg-orange-500
text-white
px-5
py-2
rounded-lg
"
            >
              Renew
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default RenewModal;
