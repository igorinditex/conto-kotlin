import React, {Component} from "react";
import {connect} from "react-redux";
import {Typeahead} from "react-bootstrap-typeahead";

class OtherAccountSelector extends Component {
  handleInputChange(e) {
    console.log(e);
  }

  render() {
    const accounts = this.props.accounts.filter(
      a => a.accountID !== this.props.fromAccount
    );
    return (
      <Typeahead
        name={this.props.name || "to"}
        emptyLabel="No accounts found"
        onChange={selected => this.props.onChange(selected[0])}
        options={accounts}
        labelKey="accountID"
        filterBy={["accountID", "owner", "description"]}
        renderMenuItemChildren={(account, props) => {
          return (
            <span>
              <div style={{ display: "inline-block" }}>
                <span style={{ fontStyle: "italic" }}>
                  {account.accountID} ( {account.description})
                </span>
                <br />
                <span className="small text-muted">{account.owner}</span>
              </div>
            </span>
          );
        }}
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    accounts: state.accounts.accounts
  };
};

export default connect(mapStateToProps)(OtherAccountSelector);
