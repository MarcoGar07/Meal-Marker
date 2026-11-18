package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ProfileManager {
    private User user;
    private ArrayList<User> allUsers = new ArrayList<>();

    public ProfileManager(boolean newUser) {
        loadAllUsers();
        if (newUser) {
            this.user = new User();
            allUsers.add(this.user);
        }
    }

    public ProfileManager(String username) {
        loadAllUsers();
        this.user = findUser(username);
    }

    public String getUsername() {
        return (user != null) ? user.getUsername() : "";
    }

    public String getStudentID() {
        return (user != null) ? user.getStudentID() : "";
    }

    public String getStudentEmail() {
        return (user != null) ? user.getStudentEmail() : "";
    }

    public String getPassword() {
        return (user != null) ? user.getPassword() : "";
    }

    public ArrayList<String> getPreferences(){
        return (user != null) ? user.getPreferences() : new ArrayList<>();
    }

    public void updateUser(String currentUsername, String newUsername, String newPassword, String newID, String newEmail) {
        if (this.user != null) {
            this.user.setUsername(newUsername);
            this.user.setPassword(newPassword);
            this.user.setStudentID(newID);
            this.user.setStudentEmail(newEmail);
        }
        User existingUser = findUser(currentUsername);
        if (existingUser != null && existingUser != this.user) {
            existingUser.setUsername(newUsername);
            existingUser.setPassword(newPassword);
            existingUser.setStudentID(newID);
            existingUser.setStudentEmail(newEmail);
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

    public static User authenticateUser(String username, String password) {
        return User.lookForUser(username, password);
    }

    public boolean isUsernameTaken(String newUsername, String currentUsername) {
        if (newUsername.equals(currentUsername)) {
            return false;
        }
        return findUser(newUsername) != null;
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
}