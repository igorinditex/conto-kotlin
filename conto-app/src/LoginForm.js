import React, { Component } from "react";
import Button from "react-bootstrap/Button";
import Form from "react-bootstrap/Form";
import { connect } from "react-redux";

import { login, loginFailure, loginSuccess } from "./state/user/actions";
import { login as _login } from "./ContoAPI";

class LoginForm extends Component {
  constructor(props) {
    super(props);
    this.state = {
      username: this.props.username,
      password: "",
      rememberMe: this.props.rememberMe,
    };
  }

  handleInputChange(event) {
    const target = event.target;
    const value = target.type === "Form.Check" ? target.checked : target.value;
    const name = target.name;

    this.setState({
      [name]: value,
    });
  }

  render() {
    return (
      <span>
        <h1>CONTO</h1>
        <form
          onSubmit={(e) => {
            e.preventDefault();
            this.props.submit(
              this.state.username,
              this.state.password,
              this.state.rememberMe
            );
          }}
        >
          <Form.Group controlId="userName">
            <Form.Label>Username:</Form.Label>
            <Form.Control
              type="text"
              value={this.state.username}
              name="username"
              placeholder="Enter username"
              onChange={(e) => this.handleInputChange(e)}
            />
            <Form.Control.Feedback />
          </Form.Group>
          <Form.Group controlId="password">
            <Form.Label>Password:</Form.Label>
            <Form.Control
              type="password"
              value={this.state.password}
              name="password"
              placeholder="Enter password"
              onChange={(e) => this.handleInputChange(e)}
            />
            <Form.Control.Feedback />
          </Form.Group>
          <Form.Group controlId="rememberme">
            <Form.Check
              type="checkbox"
              label="Remember me"
              checked={this.state.rememberMe}
              name="rememberMe"
              onChange={(e) => this.handleInputChange(e)}
            />
          </Form.Group>
          <Button bsStyle="primary" type="submit">
            Login
          </Button>
        </form>
      </span>
    );
  }
}

const mapStateToProps = (state) => {
  return {
    username: state.user.username,
    rememberMe: state.user.rememberMe,
    loginPending: state.user.fetching > 0,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    submit(username, password, rememberMe) {
      dispatch(login(username, password, rememberMe));
      _login(username, password, rememberMe)
        .then((success) => {
          if (success) {
            dispatch(loginSuccess());
          } else {
            dispatch(loginFailure());
          }
        })
        .catch((error) => {
          console.log(error);
          dispatch(loginFailure());
        });
    },
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(LoginForm);
