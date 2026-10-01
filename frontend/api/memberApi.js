import api from "./axios";

export function getMembers() {
  return api.get("/members");
}

export function getMemberByMembershipNumber(membershipNumber) {
  return api.get(`/members/membership/${encodeURIComponent(membershipNumber)}`);
}

export function getMemberByNationalCode(nationalCode) {
  return api.get(`/members/national-code/${encodeURIComponent(nationalCode)}`);
}

export function createMember(data) {
  return api.post("/members", data);
}

export function updateMember(id, data) {
  return api.put(`/members/${id}`, data);
}
