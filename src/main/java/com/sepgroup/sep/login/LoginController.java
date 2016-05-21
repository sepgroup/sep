package com.sepgroup.sep.login;

import com.sepgroup.sep.AbstractController;
import com.sepgroup.sep.Main;
import com.sepgroup.sep.project.ProjectController;
import javafx.fxml.FXML;

import java.io.IOException;

/**
 * Created by jeremybrown on 2016-05-17.
 */
public class LoginController extends AbstractController {

    public LoginController() {
        setFxmlPath("/login/login.fxml");
    }

    @FXML
    public void onLoginButtonClicked() {
        boolean validLogin = validateLogin();

        if (validLogin) {
            try {
                Main.setPrimaryScene(new ProjectController());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean validateLogin() {
        return true;
    }

    @Override
    public void update() {

    }
}
