import { useEffect, useState } from "react";
import FormInput from "../FormInput";

function CheckStatusModal({ open, onClose, onSubmit, loading, result, error }) {
  const [code, setCode] = useState("");
  const [validationError, setValidationError] = useState("");

  useEffect(() => {
    if (!open) {
      setCode("");
      setValidationError("");
    }
  }, [open]);

  if (!open) return null;

  const close = () => {
    setCode("");
    setValidationError("");
    onClose();
  };

  const submit = (e) => {
    e.preventDefault();
    const trimmed = code.trim();

    if (!trimmed) {
      setValidationError("Tracking code is required");
      return;
    }

    setValidationError("");
    onSubmit(trimmed);
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white rounded-xl p-6 w-full max-w-lg">
        <h2 className="text-xl font-bold mb-5">Check Loan Status</h2>

        <form onSubmit={submit} className="space-y-4">
          <FormInput
            label="Tracking Code"
            name="trackingCode"
            value={code}
            onChange={(e) => setCode(e.target.value)}
            required
            error={validationError}
          />

          {error && (
            <div className="bg-red-100 text-red-700 p-3 rounded-lg">
              {error}
            </div>
          )}

          {result && (
            <div className="border rounded-lg p-4 space-y-2 text-sm">
              <div><strong>Transaction:</strong> {result.id}</div>
              <div><strong>Tracking Code:</strong> {result.trackingCode}</div>
              <div><strong>Member:</strong> {result.membershipNumber}</div>
              <div><strong>Book:</strong> {result.bookCode}</div>
              <div><strong>Type:</strong> {result.type}</div>
              <div><strong>Status:</strong> {result.status}</div>
              <div><strong>Request Date:</strong> {formatDate(result.requestDate)}</div>
              <div><strong>Due Date:</strong> {formatDate(result.dueDate)}</div>
              <div><strong>Return Date:</strong> {formatDate(result.returnDate)}</div>
              <div><strong>Renew Count:</strong> {result.renewCount}</div>
            </div>
          )}

          <div className="flex justify-end gap-3 mt-5">
            <button type="button" onClick={close}>
              Close
            </button>

            <button
              type="submit"
              disabled={loading}
              className="bg-blue-600 text-white px-5 py-2 rounded-lg disabled:opacity-50"
            >
              {loading ? "Checking..." : "Check Status"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

function formatDate(value) {
  if (!value) return "-";

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;

  return date.toLocaleString();
}

export default CheckStatusModal;
