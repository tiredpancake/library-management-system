import api from "./axios";

export function checkCurrentUser() {
  return api.get("/auth/current-user");
}
