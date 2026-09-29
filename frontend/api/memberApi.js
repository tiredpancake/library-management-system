import api from "./axios";

export function getMembers() {
  return api.get("/members");
}

export function createMember(data) {
  return api.post("/members", data);
}

export function updateMember(id, data) {
  return api.put(`/members/${id}`, data);
}
