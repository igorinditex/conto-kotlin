import {applyMiddleware, combineReducers, compose, createStore} from "redux";
import thunk from "redux-thunk";

import accounts from "./state/accounts/reducers";
import user from "./state/user/reducers";
import transfers from "./state/transfers/reducers";
import flow from "./state/flow/reducers";

const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

const store = createStore(
  combineReducers({ accounts, user, transfers, flow }),
  composeEnhancers(applyMiddleware(thunk))
);

export default store;
