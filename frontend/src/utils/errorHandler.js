export function getErrorMessage(error) {
  if (error.response?.data?.message) {
    return error.response.data.message;
  }

  return "Something went wrong";
}

export function mapBackendError(error) {
  const data = error.response?.data;

  const errors = {};

  if (data?.field && data?.message) {
    errors[data.field] = data.message;
  }

  return errors;
}
