package edu.utsa.cs3443.fall2026demoproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import model.ProfileManager;

public class ProfileController {

    private ProfileManager profileManager;
    private String currentUsername;

    @FXML
    private Label usernameIsTaken;

    @FXML
    private TextField studentEmailTextField;

    @FXML
    private TextField studentIDTextField;

    @FXML
    private TextField studentPassword;

    @FXML
    private TextField usernameTextField;

    /**
     * Call this method from your login or navigation controller right before
     * switching to the profile scene to pass along who is currently logged in.
     * Pass null or an empty string if it's a brand new user registration.
     */
    public void initData(String username) {
        this.currentUsername = username;

        if (username == null || username.isEmpty()) {
            // New user workflow
            profileManager = new ProfileManager(true);
        } else {
            // Existing user workflow: load via ProfileManager facade
            profileManager = new ProfileManager(currentUsername);

            usernameTextField.setText(profileManager.getUsername());
            studentIDTextField.setText(profileManager.getStudentID());
            studentEmailTextField.setText(profileManager.getStudentEmail());
            studentPassword.setText(profileManager.getPassword());
        }
    }

    @FXML
    private void initialize() {
        // Fallback initialization if loaded directly without initData()
        if (currentUsername == null) {
            initData("");
        }
    }

    @FXML
    void onConfirmChangesButtonClick(ActionEvent event) {
        usernameIsTaken.setText("");

        String newEmail = studentEmailTextField.getText();
        String newID = studentIDTextField.getText();
        String newPassword = studentPassword.getText();
        String newUsername = usernameTextField.getText();

        // Check if the username is taken only if they changed it
        if (!profileManager.isUsernameTaken(newUsername, currentUsername)) {
            profileManager.updateUser(currentUsername, newUsername, newPassword, newID, newEmail);
            // Update local tracker in case they changed their username successfully
            currentUsername = newUsername;
        } else {
            usernameIsTaken.setText("Username is already taken");
        }
    }
}