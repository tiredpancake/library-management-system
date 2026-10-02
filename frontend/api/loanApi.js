import api from "./axios";

export function getLoanHistory(params = {}) {
  const cleanedParams = Object.fromEntries(
    Object.entries(params).filter(([, value]) => value !== "" && value !== null && value !== undefined),
  );

  return api.get("/loans/history", {
    params: cleanedParams,
  });
}

export function borrowBook(data) {
  return api.post("/loans/borrow", data);
}

export function returnBook(data) {
  return api.post("/loans/return", data);
}

export function renewLoan(data) {
  return api.post("/loans/renew", data);
}

export function getLoanStatus(trackingCode) {
  return api.get("/loans/status", {
    params: { trackingCode },
  });
}
