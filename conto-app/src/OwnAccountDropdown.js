import React, { Component } from "react";
import { connect } from "react-redux";
import DropdownItem from "react-bootstrap/DropdownItem";
import DropdownButton from "react-bootstrap/DropdownButton";

class AccountDropdown extends Component {
  constructor(props) {
    super(props);
    this.state = { selectedAccount: props.selectedAccount };
  }

  selectAccount(a) {
    this.props.onAccountSelected(a);
    this.setState({ selectedAccount: findAccount(this.props.accounts, a) });
  }

  render() {
    let selectedAccount = this.state.selectedAccount || this.props.accounts[0];
    if (!selectedAccount) {
      return null;
    }
    const title = selectedAccount.accountID;
    return (
      <DropdownButton
        style={{ width: "1000px" }}
        title={title}
        id="accountDropdown"
        rootCloseEvent="click"
      >
        {this.props.accounts.map((a) => (
          <DropdownItem
            key={a.accountID}
            eventKey={a.accountID}
            onSelect={() => this.selectAccount(a.accountID)}
          >
            {a.description} ({a.accountID}) - {a.balance}
          </DropdownItem>
        ))}
      </DropdownButton>
    );
  }
}

function findAccount(accounts, accountID) {
  return accounts && accounts.find((a) => a.accountID === accountID);
}

const mapStateToProps = (state) => {
  const accounts = state.accounts.accounts;
  const username = state.user.username;
  const ownAccounts = accounts && accounts.filter((a) => a.owner === username);
  return {
    accounts: ownAccounts,
    selectedAccount: findAccount(ownAccounts, state.flow.selectedAccount),
  };
};

export default connect(mapStateToProps)(AccountDropdown);
