import {fetchTransfers} from "../transfers/actions";

export const SELECT_ACCOUNT = "SELECT_ACCOUNT";
export const CREATE_TRANSFER = "CREATE_TRANSFER";

export function selectAccount(accountID) {
  return dispatch => {
    dispatch({
      type: SELECT_ACCOUNT,
      payload: {
        accountID
      }
    });
    dispatch(fetchTransfers(accountID));
  };
}

export function createTransfer() {
  return {
    type: CREATE_TRANSFER
  };
}
