import api from "./axios";

export function getFines() {
  return api.get("/fines");
}

export function payFine(data) {
  return api.put("/fines/pay", data);
}
