import React, {Component} from "react";
import {ListGroup, ListGroupItem} from "react-bootstrap";

class TranferListItem extends Component {
  render() {
    const t = this.props.transfer;
    const type = t.debitAccountID === this.props.ownAccount
      ? "DEBIT"
      : "CREDIT";
    let amount, account;
    if (type === "DEBIT") {
      amount = <span>-{t.amount}</span>;
      account = <span>To: {t.creditAccountID}</span>;
    } else {
      amount = <span style={{ color: "green" }}>+{t.amount}</span>;
      account = <span>From: {t.debitAccountID}</span>;
    }
    return (
      <ListGroupItem>
        <div style={{ display: "inline-block" }}>
          <span style={{ fontStyle: "italic" }}>{t.description}</span>
          <br />
          <span className="small text-muted">{account}</span>
        </div>
        <div style={{ display: "inline-block" }} className="pull-right">
          {amount}
        </div>
      </ListGroupItem>
    );
  }
}

export default class TransferList extends Component {
  render() {
    const shouldRender =
      this.props.selectedAccount && this.props.transfers.length > 0;
    if (!shouldRender) {
      return null;
    }

    return (
      <ListGroup>
        {this.props.transfers.map(t => (
          <TranferListItem
            key={t.transferID}
            transfer={t}
            ownAccount={this.props.selectedAccount.accountID}
          />
        ))}
      </ListGroup>
    );
  }
}
