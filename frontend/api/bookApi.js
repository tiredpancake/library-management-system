import api from "./axios";

export function getBooks() {
  return api.get("/books");
}

export function createBook(data) {
  return api.post("/books", data);
}

export function updateBook(id, data) {
  return api.put(`/books/${id}`, data);
}

export function deleteBook(id) {
  return api.delete(`/books/${id}`);
}
