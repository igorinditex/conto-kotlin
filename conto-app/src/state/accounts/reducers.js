import {
  FETCH_ACCOUNTS,
  FETCH_ACCOUNTS_FAILURE,
  FETCH_ACCOUNTS_SUCCES,
} from "./actions";
import { LOGOUT } from "../user/actions";

const initialState = {
  fetching: 0,
  accounts: [],
};

export default function accounts(state = initialState, { type, payload }) {
  switch (type) {
    case FETCH_ACCOUNTS:
      return { ...state, fetching: state.fetching + 1 };
    case FETCH_ACCOUNTS_FAILURE:
      return {
        ...state,
        fetching: Math.max(0, state.fetching - 1),
      };
    case FETCH_ACCOUNTS_SUCCES:
      console.log(payload.accounts);
      return {
        ...state,
        fetching: Math.max(0, state.fetching - 1),
        accounts: payload.accounts,
      };
    case LOGOUT:
      return initialState;
    default:
      return state;
  }
}
