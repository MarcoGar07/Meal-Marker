package edu.utsa.cs3443.mealmarker;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.User;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class LoginController {

    @FXML
    private Button loginButton;

    @FXML
    private Button createAccountButton;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private void login() {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        // Check for empty fields
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Missing Information",
                    "Please enter both your username and password.");
            return;
        }

        User user = User.lookForUser(username, password);

        if (user != null) {

            showAlert("Login Successful",
                    "Welcome to Meal Marker," + username + "!");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/utsa/cs3443/mealmarker/sidebar-view.fxml"));
                Scene scene = new Scene(loader.load(), 900, 600);

                SideBarController sideBarController = loader.getController();
                sideBarController.initData(user);

                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(scene);
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
        } else {
            showAlert("Login Failed",
                    "Incorrect username or password.");
        }
    }


    @FXML
    private void createAccount() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/utsa/cs3443/mealmarker/create-account-view.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);

            Stage stage = (Stage) createAccountButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error",
                    "Could not open the Create Account Screen.");
        }
    }

    @FXML
    private void forgotPassword() {
        System.out.println("Forgot Password clicked!");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
