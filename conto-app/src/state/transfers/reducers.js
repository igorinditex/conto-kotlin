import {FETCH_TRANSFERS, FETCH_TRANSFERS_FAILURE, FETCH_TRANSFERS_SUCCES} from "./actions";
import {LOGOUT} from "../user/actions";

const initialState = {
  fetching: 0,
  transfers: []
};

export default function accounts(state = initialState, { type, payload }) {
  switch (type) {
    case FETCH_TRANSFERS:
      return {
        ...state,
        fetching: state.fetching + 1
      };
    case FETCH_TRANSFERS_FAILURE:
      return { ...state, fetching: Math.max(0, state.fetching - 1) };
    case FETCH_TRANSFERS_SUCCES:
      return {
        ...state,
        fetching: Math.max(0, state.fetching - 1),
        transfers: payload.transfers
      };
    case LOGOUT:
      return initialState;

    default:
      return state;
  }
}
