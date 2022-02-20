import jwtDecode from "jwt-decode";

import {AUTHENTICATION_FAILED, LOGIN, LOGIN_FAILURE, LOGIN_SUCCESS, LOGOUT, TOKEN_RECEIVED} from "./actions";

const initialState = {
  token: "",
  username: "",
  rememberMe: false,
  loggedIn: false,
  fetching: 0
};

export default function user(state = initialState, action) {
  switch (action.type) {
    case LOGIN:
      return {
        ...state,
        username: action.payload.username,
        rememberMe: action.payload.rememberMe,
        fetching: state.fetching + 1,
        loggedIn: false,
        token: ""
      };
    case LOGIN_FAILURE:
      return {
        ...state,
        loggedIn: false,
        fetching: state.fetching - 1
      };
    case LOGIN_SUCCESS:
      return {
        ...state,
        loggedIn: true,
        fetching: state.fetching - 1
      };
    case LOGOUT:
      return { ...state, loggedIn: false };
    case TOKEN_RECEIVED:
      try {
        const claims = jwtDecode(action.payload.token);
        const username = claims.sub;
        return {
          ...state,
          username,
          rememberMe: state.rememberMe || action.payload.fromStorage,
          loggedIn: true,
          token: action.payload.token
        };
      } catch (e) {
        console.log(e);
        return state;
      }
    case AUTHENTICATION_FAILED:
      return {
        ...state,
        token: "",
        username: "",
        loggedIn: false
      };
    default:
      return state;
  }
}
