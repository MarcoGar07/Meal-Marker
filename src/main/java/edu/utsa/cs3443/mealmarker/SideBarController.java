package edu.utsa.cs3443.mealmarker;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SideBarController {

    //private static final String HOME_VIEW = "/edu/utsa/cs3443/mealmarker/home-view.fxml";
    private static final String BUDGET_NUTRITION_VIEW = "/edu/utsa/cs3443/mealmarker/budget-view.fxml";
    private static final String PROFILE_VIEW = "/edu/utsa/cs3443/mealmarker/profile-view.fxml";
    private static final String USER_GUIDE_VIEW = "/edu/utsa/cs3443/mealmarker/userguide.fxml";

    private User user;

    @FXML
    private Label userNameLabel;

    @FXML
    private Button logoutButton;

    @FXML
    private StackPane contentPane;

    private final Map<String, Parent> viewCache = new HashMap<>();

    public void initData(User user) {
        this.user = user;
        userNameLabel .setText(user.getUsername());
        viewCache.clear();
        loadView(PROFILE_VIEW);
    }

//    @FXML
//    private void onHomeButtonClick() {
//        loadView(HOME_VIEW);
//    }

    @FXML
    private void onBudgetAndNutritionButtonClick() {
        loadView(BUDGET_NUTRITION_VIEW);
    }

    @FXML
    private void onProfileAndPreferencesButtonClick() {
        loadView(PROFILE_VIEW);
    }

    @FXML
    private void onUserGuideButtonClick() {
        loadView(USER_GUIDE_VIEW);
    }

    @FXML
    public void onLogoutButtonClick() {
        this.user = null;
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/utsa/cs3443/mealmarker/login-view.fxml"));
                Scene scene = new Scene(loader.load(), 900, 600);
                viewCache.clear();
                stage.setScene(scene);
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
    }

    private void loadView(String fxmlPath) {
        try {
            Parent view = viewCache.get(fxmlPath);
            if (view == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                if (loader.getLocation() == null) {
                    throw new IOException("FXML resource not found on classpath: " + fxmlPath);
                }

                view = loader.load();

                if (PROFILE_VIEW.equals(fxmlPath)) {
                    ProfileController profileController = loader.getController();
                    if (profileController != null) {
                        profileController.initData(user);
                    }
                }

                viewCache.put(fxmlPath, view);
            }
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Error loading view: " + fxmlPath);
            e.printStackTrace();
        }
    }
}