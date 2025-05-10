package com.example.apkpencatatankeuangan.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class RegisterController {

    private Button ButtonSubmit;
    private ActionEvent event;

    public RegisterController(Button buttonSubmit) {
        ButtonSubmit = buttonSubmit;
    }

    @FXML
    void ClickButtonSubmit(ActionEvent event) {
        this.event = event;
    }

    public Button getButtonSubmit() {
        return ButtonSubmit;
    }

    public void setButtonSubmit(Button buttonSubmit) {
        ButtonSubmit = buttonSubmit;
    }

    public ActionEvent getEvent() {
        return event;
    }

    public void setEvent(ActionEvent event) {
        this.event = event;
    }
}
