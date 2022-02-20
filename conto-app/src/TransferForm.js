import React, { Component } from "react";
import Form from "react-bootstrap/Form";
import FormControl from "react-bootstrap/FormControl";
import ButtonToolbar from "react-bootstrap/ButtonToolbar";
import Button from "react-bootstrap/Button";
import { connect } from "react-redux";

import {
  cancelTransfer,
  transferStart,
  transferSuccess,
  transferFailed,
} from "./state/transfers/actions";

import OwnAccountDropdown from "./OwnAccountDropdown";
import OtherAccountSelector from "./OtherAccountSelector";
import { transfer } from "./ContoAPI";

class TransferForm extends Component {
  constructor(props) {
    super(props);
    this.state = {
      from: props.accounts.find((a) => a.owner === this.props.username)
        .accountID,
      to: "",
      description: "",
      amount: 0,
    };
  }

  handleInputChange(event) {
    const target = event.target;
    const value = target.type === "checkbox" ? target.checked : target.value;
    const name = target.name;

    this.setState({
      [name]: value,
    });
  }

  setTo(a) {
    this.setState({ to: a.accountID });
  }

  render() {
    return (
      <span>
        <h4>New transfer</h4>
        <OwnAccountDropdown
          onAccountSelected={(a) => this.setState({ from: a })}
        />
        <hr />
        <form
          onSubmit={(e) => {
            e.preventDefault();
            this.props.onSubmit(this.state);
          }}
        >
          <Form.Group controlId="to">
            <Form.Label>To:</Form.Label>
            <OtherAccountSelector
              fromAccount={this.state.from}
              onChange={(a) => this.setTo(a)}
            />
            <FormControl.Feedback />
          </Form.Group>

          <Form.Group controlId="amount">
            <Form.Label>Amount:</Form.Label>
            <FormControl
              type="text"
              value={this.state.amount}
              name="amount"
              onChange={(e) => this.handleInputChange(e)}
            />
            <FormControl.Feedback />
          </Form.Group>
          <Form.Group controlId="description">
            <Form.Label>Description:</Form.Label>
            <FormControl
              type="text"
              value={this.state.description}
              name="description"
              onChange={(e) => this.handleInputChange(e)}
            />
            <FormControl.Feedback />
          </Form.Group>
          <ButtonToolbar>
            <Button bsStyle="primary" type="submit">
              Transfer
            </Button>{" "}
            &nbsp;
            <Button
              bsStyle="default"
              type="reset"
              onClick={() => this.props.onCancel()}
            >
              Cancel
            </Button>
          </ButtonToolbar>
        </form>
      </span>
    );
  }
}

const mapStateToProps = (state) => {
  return {
    from: state.flow.selectedAccount,
    accounts: state.accounts.accounts,
    username: state.user.username,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    onSubmit(tx) {
      dispatch(transferStart(tx));
      transfer(tx)
        .then((success) => {
          if (success) {
            dispatch(transferSuccess(tx));
          } else {
            dispatch(transferFailed(tx));
          }
        })
        .catch((e) => {
          console.log(e);
          dispatch(transferFailed(tx));
        });
    },
    onCancel() {
      dispatch(cancelTransfer());
    },
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(TransferForm);
