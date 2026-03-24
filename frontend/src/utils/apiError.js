const isNonEmptyString = (value) => typeof value === 'string' && value.trim().length > 0;

/**
 * Extracts the most useful API error message from axios/fetch-like errors.
 */
export const getApiErrorMessage = (error, fallbackMessage = 'Something went wrong. Please try again.') => {
  const serverMessage = error?.response?.data?.message;
  if (isNonEmptyString(serverMessage)) {
    return serverMessage;
  }

  if (isNonEmptyString(error?.message)) {
    return error.message;
  }

  return fallbackMessage;
};
