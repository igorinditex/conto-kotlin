import {CREATE_TRANSFER, SELECT_ACCOUNT} from "./actions";
import {CANCEL_TRANSFER, TRANSFER_SUCCESS} from "../transfers/actions";
const initialState = {
  selectedAccount: null,
  currentView: "Home"
};

export default function(state = initialState, { type, payload }) {
  switch (type) {
    case SELECT_ACCOUNT:
      return { ...state, selectedAccount: payload.accountID };
    case CREATE_TRANSFER:
      return { ...state, currentView: "TransferForm" };
    case TRANSFER_SUCCESS:
    case CANCEL_TRANSFER:
      return { ...state, currentView: "Home" };
    default:
      return state;
  }
}
