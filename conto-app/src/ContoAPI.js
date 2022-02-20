import store from "./Store";
import { authenticationFailed, tokenReceived } from "./state/user/actions";

const TOKEN_HEADER = "X-AUTH-TOKEN";
const tokenFromStorage = localStorage.getItem(TOKEN_HEADER);

if (tokenFromStorage) {
  store.dispatch(tokenReceived(tokenFromStorage, true));
}

function _fetch(resource, init, isJson = true) {
  const userState = store.getState().user;
  const storedToken = userState.token;

  const newInit = init || {};
  if (storedToken) {
    newInit.headers = newInit.headers || new Headers();
    newInit.headers.set(TOKEN_HEADER, storedToken);
  }

  if (isJson) {
    newInit.headers.set("Content-Type", "application/json");
  }

  return fetch(resource, newInit).then((response) => {
    if (response.status === 401) {
      // Authentication error, so make sure to logout the user
      localStorage.removeItem(TOKEN_HEADER);
      store.dispatch(authenticationFailed());
    } else {
      const receivedToken = response.headers.get(TOKEN_HEADER);
      if (receivedToken) {
        if (userState.rememberMe) {
          localStorage.setItem(TOKEN_HEADER, receivedToken);
        }
        store.dispatch(tokenReceived(receivedToken));
      }
    }
    return response;
  });
}

export function login(username, password, rememberMe) {
  if (!rememberMe) {
    localStorage.removeItem(TOKEN_HEADER);
  }

  const form = new FormData();
  form.append("username", username);
  form.append("password", password);
  return _fetch(
    "http://localhost:8080/api/login",
    {
      method: "POST",
      body: form,
    },
    false
  ).then((response) => response.status === 200);
}

export function findAccounts() {
  return _fetch("http://localhost:8080/api/account").then((response) =>
    response.json()
  );
}

export function findTransfers(accountID) {
  return _fetch(
    "http://localhost:8080/api/transfer/" + accountID
  ).then((response) => response.json());
}

export function transfer(transfer) {
  const tx = {
    debitAccountID: transfer.from,
    creditAccountID: transfer.to,
    amount: transfer.amount,
    description: transfer.description,
  };
  return _fetch("http://localhost:8080/api/transfer", {
    method: "POST",
    body: JSON.stringify(tx),
  }).then((response) => response.status === 200);
}
