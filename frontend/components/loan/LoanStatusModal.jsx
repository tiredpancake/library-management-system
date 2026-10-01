import { useEffect, useState } from "react";

import FormInput from "../FormInput";

function LoanStatusModal({ open, onClose, onSubmit }) {
  const [trackingCode, setTrackingCode] = useState("");

  const [errors, setErrors] = useState({});

  const [result, setResult] = useState(null);

  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!open) {
      setTrackingCode("");
      setErrors({});
      setResult(null);
      setLoading(false);
    }
  }, [open]);

  if (!open) {
    return null;
  }

  const change = (e) => {
    setTrackingCode(e.target.value);

    setErrors({
      ...errors,
      trackingCode: "",
    });

    setResult(null);
  };

  const validate = () => {
    const e = {};

    if (!trackingCode.trim()) {
      e.trackingCode = "Tracking code is required";
    }

    setErrors(e);

    return Object.keys(e).length === 0;
  };

  const submit = async (e) => {
    e.preventDefault();

    if (!validate()) {
      return;
    }

    try {
      setLoading(true);

      const response = await onSubmit(trackingCode.trim());

      setResult(response.data);
    } catch (err) {
      const response = err.response?.data;

      setResult(null);

      setErrors({
        trackingCode: response?.message || "Loan not found",
      });
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (value) => {
    if (!value) {
      return "-";
    }

    return value.replace("T", " ");
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
            Check Loan Status
          </h2>
        </div>

        {/* Scrollable Content */}

        <div
          className="
          overflow-y-auto
          p-6
          "
        >
          <form
            onSubmit={submit}
            className="
            space-y-4
            "
          >
            <FormInput
              label="Tracking Code"
              name="trackingCode"
              value={trackingCode}
              onChange={change}
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
                disabled={loading}
                className="
                bg-blue-600
                text-white
                px-5
                py-2
                rounded-lg
                disabled:opacity-50
                "
              >
                {loading ? "Checking..." : "Check Status"}
              </button>
            </div>
          </form>

          {/* Result */}

          {result && (
            <div
              className="
              mt-6
              border
              rounded-lg
              p-4
              space-y-3
              "
            >
              <h3
                className="
                font-bold
                text-lg
                mb-3
                "
              >
                Transaction Status
              </h3>

              <Detail label="Tracking Code" value={result.trackingCode} />

              <Detail
                label="Membership Number"
                value={result.membershipNumber}
              />

              <Detail label="Book Code" value={result.bookCode} />

              <Detail label="Transaction Type" value={result.type} />

              <Detail label="Status" value={result.status} />

              <Detail
                label="Request Date"
                value={formatDate(result.requestDate)}
              />

              <Detail label="Due Date" value={formatDate(result.dueDate)} />

              <Detail
                label="Return Date"
                value={formatDate(result.returnDate)}
              />

              <Detail label="Renew Count" value={result.renewCount} />
            </div>
          )}
        </div>

        {/* Footer */}

        <div
          className="
          p-6
          border-t
          flex
          justify-end
          "
        >
          <button
            onClick={onClose}
            className="
            bg-blue-600
            text-white
            px-5
            py-2
            rounded-lg
            "
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
}

function Detail({ label, value }) {
  return (
    <div
      className="
      border-b
      pb-2
      "
    >
      <p
        className="
        text-sm
        text-gray-500
        "
      >
        {label}
      </p>

      <p
        className="
        font-medium
        break-words
        "
      >
        {value ?? "-"}
      </p>
    </div>
  );
}

export default LoanStatusModal;
