package edu.utsa.cs3443.mealmarker;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class CreateAccountController {

    private ArrayList<User> allUsers;

    @FXML
    private Button createAccountButton;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField studentIDTextField;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label studentIDLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private TextField emailTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private PasswordField confirmPasswordTextField;


    @FXML public void initialize() {
        allUsers = new ArrayList<>();
        loadAllUsers();
        usernameTextField.textProperty().addListener((obs, oldVal, newVal) -> findUsername(newVal));
        studentIDTextField.textProperty().addListener((obs, oldVal, newVal) -> findStudentID(newVal));
        emailTextField.textProperty().addListener((obs, oldVal, newVal) -> findEmail(newVal));
    }
    @FXML
    private void createAccount() {
        String username = usernameTextField.getText().trim();
        String studentID = studentIDTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String password = passwordTextField.getText();
        String confirmPassword = confirmPasswordTextField.getText();

        // Check for empty fields
        if (username.isEmpty() || studentID.isEmpty()
        || email.isEmpty() || password.isEmpty()
        || confirmPassword.isEmpty()) {

            showAlert("Missing information",
                    "Please complete all fields.");
            return;
        }

        // Check email format
        if (!email.contains("@") || !email.contains(".")) {
            showAlert("Invalid Email",
                    "Please enter a valid email address.");
            return;
        }

        // Check password confirmation
        if (!password.equals(confirmPassword)) {
            showAlert("Password Mismatch",
                    "Your passwords do not match.");
            return;
        }

        boolean usernameTaken = findUsername(username);
        boolean idTaken = findStudentID(studentID);
        boolean emailTaken = findEmail(email);

        if (usernameTaken || idTaken || emailTaken) {
            return;
        }

        showAlert("Account Created",
                "Welcome to Meal Marker, " + username + "!");
        User user = new User(username, studentID, email, password, 0.0, new ArrayList<String>());
        allUsers.add(user);
        saveAllUsers();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/utsa/cs3443/mealmarker/profile-view.fxml"));
            Scene scene = new Scene(loader.load());

            ProfileController profileController = loader.getController();
            profileController.initData(user);

            Stage stage = (Stage) createAccountButton.getScene().getWindow();
            stage.setScene(scene);
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void backToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    CreateAccountController.class.getResource(
                            "/edu/utsa/cs3443/mealmarker/login-view.fxml"
                    )
            );

            Stage stage = (Stage) usernameTextField.getScene().getWindow();

            Scene scene = new Scene(loader.load(), 900, 600);

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error",
                    "Could not return to the Login Screen.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public User convertLineToUser(String line) {
        String[] content = line.split(",");
        String username = content.length > 0 ? content[0] : "";
        String studentID = content.length > 1 ? content[1] : "";
        String studentEmail = content.length > 2 ? content[2] : "";
        String password = content.length > 3 ? content[3] : "";
        double budget = content.length > 4 && !content[4].isEmpty() ? Double.parseDouble(content[4]) : 0.0;

        ArrayList<String> preferencesTemp = new ArrayList<>();
        for (int i = 5; i < content.length; i++) {
            preferencesTemp.add(content[i]);
        }

        User user = new User();
        user.setUsername(username);
        user.setStudentID(studentID);
        user.setStudentEmail(studentEmail);
        user.setPassword(password);
        user.setBudget(budget);
        user.setUserPreferences(preferencesTemp);
        return user;
    }

    public void loadAllUsers() {
        allUsers.clear();
        File file = new File("data/users.csv");
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.trim().isEmpty()) {
                    User user = convertLineToUser(line);
                    allUsers.add(user);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    public void saveAllUsers() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("data/users.csv"));
            bw.write("username,studentID,studentEmail,password,budget,preferences\n");
            for (User user : allUsers) {
                bw.write(convertUserToLine(user) + "\n");
            }
            bw.close();
        } catch (Exception e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    public String convertUserToLine(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append(user.getUsername()).append(",") .append(user.getStudentID()).append(",").append(user.getStudentEmail()).append(",")
                .append(user.getPassword()).append(",").append(user.getBudget());
        if (user.getPreferences() != null) {
            for (String pref : user.getPreferences()) {
                sb.append(",").append(pref);
            }
        }
        return sb.toString();
    }

    public boolean findUsername(String username) {
        username = username.trim();
        for (User user : allUsers) {
            if (user.getUsername() != null && user.getUsername().equals(username)) {
                usernameLabel.setText("Username is taken");
                return true;
            }
        }
        usernameLabel.setText("");
        return false;
    }
    public boolean findStudentID(String studentID) {
        studentID = studentID.trim();
        for (User user : allUsers) {
            if (user.getStudentID() != null && user.getStudentID().equals(studentID)) {
                studentIDLabel.setText("Student ID is taken");
                return true;
            }
        }
        studentIDLabel.setText("");
        return false;
    }
    public boolean findEmail(String email) {
        email = email.trim();
        for (User user : allUsers) {
            if (user.getStudentEmail() != null && user.getStudentEmail().equals(email)) {
                emailLabel.setText("Email is taken");
                return true;
            }
        }
        emailLabel.setText("");
        return false;
    }


}
