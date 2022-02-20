import React, { Component } from "react";
import { Col, Container, Row } from "react-bootstrap";
import { connect } from "react-redux";

import Phone from "./Phone";
import LoginForm from "./LoginForm";
import Home from "./Home";
import TransferForm from "./TransferForm";

import "./App.css";

const components = {
  Home,
  TransferForm,
};

class App extends Component {
  render() {
    let view;

    if (!this.props.loggedIn) {
      view = <LoginForm />;
    } else {
      view = React.createElement(components[this.props.currentView]);
    }

    return (
      <Phone>
        <div className="App">
          <Container>
            <Row>
              <Col xs={12}>{view}</Col>
            </Row>
          </Container>
        </div>
      </Phone>
    );
  }
}

const mapStateToProps = (state) => {
  return {
    loggedIn: state.user.loggedIn,
    currentView: state.flow.currentView,
  };
};

export default connect(mapStateToProps)(App);
