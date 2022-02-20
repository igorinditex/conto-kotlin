import React, {Component} from "react";
import "./vendor/html5-device-mockups/device-mockups2.css";

export default class Phone extends Component {
  render() {
    return (
      <div
        className="device-container"
        style={{ maxWidth: "360px", marginLeft: "25px", marginTop: "25px" }}
        data-size-port="360px"
      >
        <div
          className="device-mockup"
          data-device="galaxy_s5"
          data-orientation="portrait"
          data-color="black"
        >
          <div className="device">
            <div className="screen">
              {this.props.children}
            </div>
          </div>
          <div className="button" />
        </div>
      </div>
    );
  }
}
