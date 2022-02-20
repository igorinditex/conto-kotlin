export const FETCH_ACCOUNTS = "FETCH_ACCOUNTS";
export const FETCH_ACCOUNTS_SUCCES = "FETCH_ACCOUNTS_SUCCES";
export const FETCH_ACCOUNTS_FAILURE = "FETCH_ACCOUNTS_FAILURE";

export function fetchAccountsStart() {
  return {
    type: FETCH_ACCOUNTS,
  };
}

export function fetchAccountsSucces(accounts) {
  return {
    type: FETCH_ACCOUNTS_SUCCES,
    payload: { accounts },
  };
}

export function fetchAccountsFailure() {
  return {
    type: FETCH_ACCOUNTS_FAILURE,
  };
}
