import React, { Component } from "react";
import { connect } from "react-redux";
import Button from "react-bootstrap/Button";
import { BsFillPlusSquareFill } from "react-icons/bs";
import { findAccounts } from "./ContoAPI";

import {
  fetchAccountsStart,
  fetchAccountsSucces,
  fetchAccountsFailure,
} from "./state/accounts/actions";
import { createTransfer, selectAccount } from "./state/flow/actions";
import { logout } from "./state/user/actions";

import OwnAccountDropdown from "./OwnAccountDropdown";
import TransferList from "./TransferList";

class Home extends Component {
  componentDidMount() {
    this.props.onMount(this.props.username);
  }

  render() {
    const accounts = this.props.requestPending ? (
      <div className="loader" />
    ) : (
      <OwnAccountDropdown
        onAccountSelected={(accountID) =>
          this.props.onAccountSelected(accountID)
        }
      />
    );
    const txList =
      this.props.transferRequestPending || this.props.requestPending ? (
        <div className="loader" />
      ) : (
        <TransferList
          transfers={this.props.transfers}
          fetching={this.props.transferRequestPending}
          selectedAccount={this.props.selectedAccount}
        />
      );

    return (
      <div style={{ height: "100%" }}>
        <h4>My accounts</h4>
        <div>{accounts}</div>
        <hr />
        <h4>
          Transfers <BsFillPlusSquareFill style={{ color: "rgb(51, 122, 183)", cursor: "pointer" }} onClick={() => this.props.createTransfer()}/> 
        </h4>
        <div style={{ height: "250px", overflow: "auto" }}>{txList}</div>
        <hr />
        <Button bsStyle="primary" onClick={() => this.props.logout()}>
          Logout
        </Button>
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  const accounts = state.accounts.accounts;
  return {
    accounts,
    transfers: state.transfers.transfers,
    requestPending: state.accounts.fetching > 0,
    transferRequestPending: state.transfers.fetching > 0,
    username: state.user.username,
    selectedAccount:
      accounts &&
      accounts.find((a) => a.accountID === state.flow.selectedAccount),
  };
};

const mapDispatchToProps = (dispatch, getState) => {
  return {
    onMount: (username) => {
      dispatch(fetchAccountsStart());
      findAccounts()
        .then((accounts) => {
          dispatch(fetchAccountsSucces(accounts));
          if (accounts && username) {
            const accountToSelect = accounts.find((a) => a.owner === username);
            if (accountToSelect) {
              dispatch(selectAccount(accountToSelect.accountID));
            }
          }
        })
        .catch((e) => {
          console.log(e);
          dispatch(fetchAccountsFailure());
        });
    },

    onAccountSelected: (accountID) => {
      dispatch(selectAccount(accountID));
    },

    logout: () => {
      dispatch(logout());
    },

    createTransfer: () => {
      dispatch(createTransfer());
    },
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Home);
