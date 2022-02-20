export const LOGIN = "LOGIN";
export const LOGIN_SUCCESS = "LOGIN_SUCCES";
export const LOGIN_FAILURE = "LOGIN_FAILURE";
export const LOGOUT = "LOGOUT";
export const TOKEN_RECEIVED = "TOKEN_RECEIVED";
export const AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";

export function login(username, password, rememberMe) {
  return {
    type: LOGIN,
    payload: {
      username,
      password,
      rememberMe
    }
  };
}

export function loginSuccess(token) {
  return {
    type: LOGIN_SUCCESS,
    payload: {
      token
    }
  };
}

export function loginFailure() {
  return {
    type: LOGIN_FAILURE
  };
}

export function logout() {
  return {
    type: LOGOUT
  };
}

export function tokenReceived(token, fromStorage) {
  return {
    type: TOKEN_RECEIVED,
    payload: {
      token,
      fromStorage
    }
  };
}

export function authenticationFailed() {
  return {
    type: AUTHENTICATION_FAILED
  };
}
