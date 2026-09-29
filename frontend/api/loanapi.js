import api from "./axios";

export function getLoanHistory(params) {
  return api.get("/loans/history", {
    params,
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
