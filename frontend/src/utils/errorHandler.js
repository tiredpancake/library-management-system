export function getErrorMessage(error) {
  if (error.response?.data?.message) {
    return error.response.data.message;
  }

  return "Something went wrong";
}

export function mapBackendError(error) {
  const message = error.response?.data?.message || "";

  const errors = {};

  if (message.toLowerCase().includes("isbn")) {
    errors.isbn = "ISBN already exists";
  }

  if (message.toLowerCase().includes("national")) {
    errors.nationalCode = "National code already exists";
  }

  if (message.toLowerCase().includes("username")) {
    errors.username = "Invalid username or password";
  }

  if (message.toLowerCase().includes("password")) {
    errors.password = "Invalid username or password";
  }

  return errors;
}
