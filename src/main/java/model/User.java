package model;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class User{
    private String username;
    private String studentID;
    private String studentEmail;
    private String password;
    private double budget;
    private ArrayList<String> userPreferences;

    public User(String username, String studentID, String studentEmail, String password,  double budget, ArrayList<String> userPreferences) {
        this.username = username;
        this.studentID = studentID;
        this.studentEmail = studentEmail;
        this.password = password;
        this.budget = budget;
        this.userPreferences = userPreferences;
    }
    public User(){
        this.username = "";
        this.studentID = "";
        this.studentEmail = "";
        this.password = "";
        this.budget = 0;
        this.userPreferences = new ArrayList<>();
    }

    public String getUsername(){
        return this.username;
    }
    public String getStudentID(){
        return this.studentID;
    }
    public String getStudentEmail(){
        return this.studentEmail;
    }
    public String getPassword(){
        return this.password;
    }
    public double getBudget() {
        return this.budget;
    }
    public ArrayList<String> getPreferences(){
        return this.userPreferences;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public void setStudentID(String studentID){this.studentID = studentID; }
    public void setStudentEmail(String studentEmail){
        this.studentEmail = studentEmail;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setUserPreferences(ArrayList<String> preferences){
        this.userPreferences = preferences;
    }
    public void setBudget(double budget){
        this.budget = budget;
    }

    public static User lookForUser(String username, String password) {
        try {
            Scanner scanner = new Scanner(new File("data/users.csv"));
            if (scanner.hasNextLine()) {
                scanner.nextLine(); // Skip the header line!
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] fields = line.split(",");
                if (fields.length > 3 && fields[0].equals(username) && fields[3].equals(password)) {
                    ArrayList<String> preferencesTemp = new ArrayList<>();
                    for (int i = 5; i < fields.length; i++) {
                        preferencesTemp.add(fields[i]);
                    }
                    return new User(fields[0], fields[1], fields[2], fields[3], Double.parseDouble(fields[4]), preferencesTemp);
                }
            }
        } catch (Exception e) {
            System.out.println("No user file found or error reading: " + e.getMessage());
        }
        return null;
    }

    public String toString() {
        return String.format("%s,%s,%s,%s,%f,%s", this.username, this.studentID, this.studentEmail, this.password, this.budget,  this.userPreferences);
    }
}
