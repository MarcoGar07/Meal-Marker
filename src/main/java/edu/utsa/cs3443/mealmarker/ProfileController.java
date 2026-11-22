package edu.utsa.cs3443.mealmarker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ProfileController {

    private User user;
    private String currentUsername;
    private ArrayList<User> allUsers = new ArrayList<User>();
    private ArrayList<String> selectedPrefernces = new ArrayList<>();

    @FXML
    private Button confirmChangesButton;

    @FXML
    private CheckBox glutenFreeCheck;
    @FXML
    private CheckBox vegetarianCheck;
    @FXML
    private CheckBox veganCheck;
    @FXML
    private CheckBox pescatarianCheck;
    @FXML
    private CheckBox highProteinCheck;
    @FXML
    private CheckBox eggsCheck;
    @FXML
    private CheckBox peanutsCheck;
    @FXML
    private CheckBox soyCheck;
    @FXML
    private CheckBox wheatCheck;
    @FXML
    private CheckBox fishCheck;
    @FXML
    private CheckBox shellfishCheck;

    @FXML
    private Label usernameIsTaken;

    @FXML
    private TextField studentEmailTextField;

    @FXML
    private TextField studentIDTextField;

    @FXML
    private PasswordField studentPassword;

    @FXML
    private TextField usernameTextField;

    /**
     * Call this method from your login or navigation controller right before
     * switching to the profile scene to pass along who is currently logged in.
     * Pass null or an empty string if it's a brand new user registration.
     */
    public void initData(User user) {
        this.user = user;
        usernameTextField.setText(user.getUsername());
        studentIDTextField.setText(user.getStudentID());
        studentEmailTextField.setText(user.getStudentEmail());
        studentPassword.setText(user.getPassword());
        loadAllUsers();
        selectedPrefernces = user.getPreferences();
        currentUsername = user.getUsername();
        setPreferencesDropdown();
    }

    @FXML
    private void initialize() {
        if (usernameTextField == null) {
            initData(null);
        }
    }

    @FXML
    void onConfirmChangesButtonClick(ActionEvent event) {
        usernameIsTaken.setText("");

        String newEmail = studentEmailTextField.getText();
        String newID = studentIDTextField.getText();
        String newPassword = studentPassword.getText();
        String newUsername = usernameTextField.getText();

        if (!isUsernameTaken(newUsername, currentUsername)) {
            updateUser(currentUsername, newUsername, newPassword, newID, newEmail);
            currentUsername = newUsername;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/utsa/cs3443/mealmarker/userguide.fxml"));
                Scene scene = new Scene(loader.load());

                Stage stage = (Stage) confirmChangesButton.getScene().getWindow();
                stage.setScene(scene);
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
        }else{
            usernameIsTaken.setText("Username is already taken");
        }
    }

    public void updateUser(String currentUsername, String newUsername, String newPassword, String newID, String newEmail) {
        if (this.user != null) {
            this.user.setUsername(newUsername);
            this.user.setPassword(newPassword);
            this.user.setStudentID(newID);
            this.user.setStudentEmail(newEmail);
            this.user.setUserPreferences(selectedPrefernces);
        }
        User existingUser = findUser(currentUsername);
        if (existingUser != null && existingUser != this.user) {
            existingUser.setUsername(newUsername);
            existingUser.setPassword(newPassword);
            existingUser.setStudentID(newID);
            existingUser.setStudentEmail(newEmail);
            existingUser.setUserPreferences(selectedPrefernces);
        }
        saveAllUsers();
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

    public User findUser(String username) {
        for (User user : allUsers) {
            if (user.getUsername() != null && user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public boolean isUsernameTaken(String newUsername, String currentUsername) {
        if (newUsername == null || newUsername.isBlank()) {
            return true;
        }
        if (!newUsername.equals(currentUsername) && findUser(newUsername) != null) {
            return true; // Another user already has this username
        }
        return false;
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

    public void setPreferencesDropdown(){
        if(user.getPreferences().contains("Gluten-free")){
            glutenFreeCheck.setSelected(true);
        }
        if(user.getPreferences().contains("Vegetarian")){
            vegetarianCheck.setSelected(true);
        }
        if(user.getPreferences().contains("Vegan")){
            veganCheck.setSelected(true);
        }
        if(user.getPreferences().contains("Pescatarian")){
            pescatarianCheck.setSelected(true);
        }
        if(user.getPreferences().contains("High protein")){
            highProteinCheck.setSelected(true);
        }
        if(user.getPreferences().contains("Eggs")){
            eggsCheck.setSelected(true);
        }
        if(user.getPreferences().contains("Peanuts")){
            peanutsCheck.setSelected(true);
        }
        if(user.getPreferences().contains("Soy")){
            soyCheck.setSelected(true);
        }
        if(user.getPreferences().contains("Wheat")){
            wheatCheck.setSelected(true);
        }
        if(user.getPreferences().contains("Fish")){
            fishCheck.setSelected(true);
        }
        if(user.getPreferences().contains("Shellfish")){
            shellfishCheck.setSelected(true);
        }
    }

    public void onCheckBoxButtonClicked(ActionEvent event) {
        CheckBox clicked = (CheckBox) event.getSource();
        String pref = clicked.getText();
        if (clicked.isSelected()) {
            if (!selectedPrefernces.contains(pref)) {
                selectedPrefernces.add(pref);
            }
        } else {
            selectedPrefernces.remove(pref);
        }
    }
}
