import { findTransfers } from "../../ContoAPI";

export const FETCH_TRANSFERS = "FETCH_TRANSFERS";
export const FETCH_TRANSFERS_SUCCES = "FETCH_TRANSFERS_SUCCES";
export const FETCH_TRANSFERS_FAILURE = "FETCH_TRANSFERS_FAILURE";
export const CANCEL_TRANSFER = "CANCEL_TRANSFER";
export const TRANSFER_START = "TRANSFER_START";
export const TRANSFER_SUCCESS = "TRANSFER_SUCCESS";
export const TRANSFER_FAILED = "TRANSFER_FAILED";

export function cancelTransfer() {
  return {
    type: CANCEL_TRANSFER,
  };
}

export function transferStart(transfer) {
  return {
    type: TRANSFER_START,
    payload: { transfer },
  };
}

export function transferSuccess(transfer) {
  return {
    type: TRANSFER_SUCCESS,
    payload: { transfer },
  };
}

export function transferFailed(transfer) {
  return {
    type: TRANSFER_FAILED,
    payload: { transfer },
  };
}

export function fetchTransfers(accountID) {
  return function (dispatch) {
    dispatch(fetchTransfersStart(accountID));
    findTransfers(accountID)
      .then((transfers) => {
        dispatch(fetchTransfersSuccess(transfers));
      })
      .catch((e) => {
        console.log(e);
        dispatch(fetchTransfersFailure());
      });
  };
}

function fetchTransfersStart(accountID) {
  return {
    type: FETCH_TRANSFERS,
    payload: { accountID },
  };
}

function fetchTransfersSuccess(transfers) {
  return {
    type: FETCH_TRANSFERS_SUCCES,
    payload: { transfers },
  };
}

function fetchTransfersFailure() {
  return {
    type: FETCH_TRANSFERS_FAILURE,
  };
}
