package edu.utsa.cs3443.mealmarker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import model.Budget;
import model.Meal;
import model.User;

/**
 * BudgetController coordinates weekly expense tracking, meal selection, dietary
 * filtering,
 * and macronutrient summaries.
 */
public class BudgetController {
    private User user;
    private Budget budget;

    private int totalCalories;
    private double totalProtein;
    private double totalCarbs;
    private double totalFat;
    private double totalSugar;

    private List<Meal> allMeals = new ArrayList<>();
    private ArrayList<Meal> selectableMeals = new ArrayList<>();
    private ArrayList<Meal> selectedMeals = new ArrayList<>();

    // FXML UI references
    @FXML
    private Label weeklyLimitLabel;

    @FXML
    private Label spentAmountLabel;

    @FXML
    private Label remainingBudgetLabel;

    @FXML
    private Label budgetStatusLabel;

    @FXML
    private Label totalCaloriesLabel;

    @FXML
    private Label totalProteinLabel;

    @FXML
    private Label totalCarbsLabel;

    @FXML
    private Label totalFatLabel;

    @FXML
    private Label totalSugarLabel;

    @FXML
    private ListView<Meal> selectableMealsListView;

    @FXML
    private ListView<Meal> selectedMealsListView;

    /**
     * Initializes the controller, loads user data and meals from data/meals.csv,
     * and sets up initial budget and nutrition metrics.
     */
    @FXML
    private void initialize() {
        // 1. Initialize user with baseline preferences and budget if not provided
        if (user == null) {
            ArrayList<String> initialPreferences = new ArrayList<>();
            user = new User("Runner", "abc1234", "runner@my.utsa.edu", "password", 75.0, initialPreferences);
        }

        // 2. Initialize Budget model
        double initialLimit = user.getBudget() > 0 ? user.getBudget() : 75.0;
        budget = new Budget(initialLimit);

        // 3. Setup readable cell format for meal list views
        setupListViewCellFactory(selectableMealsListView);
        setupListViewCellFactory(selectedMealsListView);

        // 4. Load meal dataset from data folder
        File mealsFile = new File("data/meals.csv");
        ArrayList<Meal> loadedMeals = new ArrayList<>();
        try {
            if (mealsFile.exists()) {
                List<Meal> meals = Meal.loadMeals(mealsFile);
                if (meals != null) {
                    for (Meal m : meals) {
                        // Ensure prices and nutrition are not null
                        if (m.getPrice() == null) {
                            if (m.getHistoricalPrice() != null) {
                                m.setPrice(m.getHistoricalPrice());
                            } else {
                                m.setPrice(5.99);
                            }
                        }
                        if (m.getCalories() == null)
                            m.setCalories(0);
                        if (m.getProtein() == null)
                            m.setProtein(0.0);
                        if (m.getCarbs() == null)
                            m.setCarbs(0.0);
                        if (m.getFat() == null)
                            m.setFat(0.0);
                        if (m.getSugar() == null)
                            m.setSugar(0.0);
                        loadedMeals.add(m);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Unable to load data/meals.csv - " + e.getMessage());
        }

        // If dataset file not found or empty, fallback to representative campus sample
        // items
        if (loadedMeals.isEmpty()) {
            loadedMeals.add(createSampleMeal("1", "Chick-fil-A Chicken Sandwich", "Chick-fil-A", 5.49, 420, 29.0, 41.0,
                    16.0, 6.0));
            loadedMeals.add(createSampleMeal("2", "Smoothie King Chicken Wrap", "Smoothie King", 6.49, 480, 32.0, 38.0,
                    18.0, 4.0));
            loadedMeals.add(
                    createSampleMeal("3", "Panda Express Bowl", "Panda Express", 7.80, 510, 24.0, 62.0, 19.0, 8.0));
            loadedMeals
                    .add(createSampleMeal("4", "Subway 6\" Turkey Breast", "Subway", 6.99, 280, 18.0, 40.0, 3.5, 5.0));
            loadedMeals.add(createSampleMeal("5", "Roadrunner Cafe Lunch Pass", "Roadrunner Cafe", 10.50, 650, 35.0,
                    70.0, 22.0, 12.0));
            loadedMeals.add(
                    createSampleMeal("6", "Chick-fil-A Grilled Wrap", "Chick-fil-A", 6.99, 660, 43.0, 32.0, 45.0, 3.0));
        }

        // 5. Populate selectable meals and apply user preference filtering
        this.allMeals = loadedMeals;
        this.selectableMeals = filterSelectableMeals(user.getPreferences());

        // 6. Populate ListViews
        refreshListViews();

        // 7. Compute initial daily nutrition and budget display
        calculateDailyNutrition();
        updateBudgetDisplay();
    }

    /**
     * Initializes the controller conforming to UML naming (- Initialize(): void).
     */
    public void Initialize() {
        initialize();
    }

    /**
     * Configures a custom ListCell renderer for meal ListView items.
     */
    private void setupListViewCellFactory(ListView<Meal> listView) {
        if (listView == null)
            return;
        listView.setCellFactory(lv -> new ListCell<Meal>() {
            @Override
            protected void updateItem(Meal meal, boolean empty) {
                super.updateItem(meal, empty);
                if (empty || meal == null) {
                    setText(null);
                } else {
                    String rest = (meal.getRestaurantName() != null && !meal.getRestaurantName().isBlank())
                            ? meal.getRestaurantName() + " - "
                            : "";
                    double price = meal.getPrice() != null ? meal.getPrice()
                            : (meal.getHistoricalPrice() != null ? meal.getHistoricalPrice() : 0.0);
                    int calories = meal.getCalories() != null ? meal.getCalories() : 0;
                    double protein = meal.getProtein() != null ? meal.getProtein() : 0.0;
                    setText(String.format("%s%s  |  $%.2f  (%d cal, %.1fg protein)",
                            rest, meal.getName() != null ? meal.getName() : "Meal", price, calories, protein));
                }
            }
        });
    }

    /**
     * Helper to construct a Meal object using default constructor and setters.
     */
    private Meal createSampleMeal(String id, String name, String restaurant, double price, int calories, double protein,
            double carbs, double fat, double sugar) {
        Meal meal = new Meal();
        meal.setMealId(id);
        meal.setName(name);
        meal.setRestaurantName(restaurant);
        meal.setPrice(price);
        meal.setCalories(calories);
        meal.setProtein(protein);
        meal.setCarbs(carbs);
        meal.setFat(fat);
        meal.setSugar(sugar);
        return meal;
    }

    /**
     * Handles adding a selected meal from the selectable list to the user's
     * selected meals.
     * Updates budget and daily nutrition.
     */
    @FXML
    public void onFoodListSelection(ActionEvent event) {
        if (selectableMealsListView != null) {
            Meal selected = selectableMealsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedMeals.add(selected);
                double price = selected.getPrice() != null ? selected.getPrice()
                        : (selected.getHistoricalPrice() != null ? selected.getHistoricalPrice() : 0.0);
                budget.addExpense(price);
                refreshListViews();
                calculateDailyNutrition();
                updateBudgetDisplay();
            }
        }
    }

    /**
     * Handles removing a meal from the user's selected meals.
     * Deducts cost from spent amount and recalculates daily nutrition.
     */
    @FXML
    public void onFoodListDeselection(ActionEvent event) {
        if (selectedMealsListView != null) {
            Meal selected = selectedMealsListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedMeals.remove(selected);
                double price = selected.getPrice() != null ? selected.getPrice()
                        : (selected.getHistoricalPrice() != null ? selected.getHistoricalPrice() : 0.0);
                budget.removeExpense(price);
                refreshListViews();
                calculateDailyNutrition();
                updateBudgetDisplay();
            }
        }
    }

    /**
     * Filters available meals against dietary preferences and restrictions from the
     * User model.
     *
     * @param preferences list of dietary preferences/allergens
     * @return filtered list of selectable meals
     */
    public ArrayList<Meal> filterSelectableMeals(ArrayList<String> preferences) {
        if (preferences == null || preferences.isEmpty()) {
            return new ArrayList<>(this.allMeals);
        }

        ArrayList<Meal> filtered = new ArrayList<>();
        for (Meal meal : this.allMeals) {
            boolean allowed = true;
            String name = meal.getName() != null ? meal.getName().toLowerCase() : "";
            String allergens = meal.getAllergens() != null ? meal.getAllergens().toLowerCase() : "";
            String tags = meal.getDietaryTags() != null ? meal.getDietaryTags().toLowerCase() : "";

            for (String pref : preferences) {
                if (pref == null || pref.isBlank())
                    continue;
                String lowerPref = pref.toLowerCase().trim();

                // Check allergen matches
                if (!allergens.isEmpty() && allergens.contains(lowerPref)) {
                    allowed = false;
                    break;
                }
                // Check name matches for allergens (e.g. peanuts, egg, soy, fish)
                if (name.contains(lowerPref) && (lowerPref.equals("egg") || lowerPref.equals("eggs")
                        || lowerPref.equals("peanuts") || lowerPref.equals("soy") || lowerPref.equals("fish")
                        || lowerPref.equals("shellfish") || lowerPref.equals("wheat"))) {
                    allowed = false;
                    break;
                }

                // Check dietary preferences
                if (lowerPref.equals("gluten-free") && (allergens.contains("wheat") || allergens.contains("gluten"))) {
                    allowed = false;
                    break;
                }
                if (lowerPref.equals("vegetarian") && (name.contains("chicken") || name.contains("beef")
                        || name.contains("pork") || name.contains("bacon") || name.contains("turkey")
                        || name.contains("sausage") || name.contains("steak"))) {
                    allowed = false;
                    break;
                }
                if (lowerPref.equals("vegan") && (name.contains("chicken") || name.contains("beef")
                        || name.contains("pork") || name.contains("bacon") || name.contains("turkey")
                        || name.contains("cheese") || allergens.contains("milk") || allergens.contains("egg"))) {
                    allowed = false;
                    break;
                }
            }
            if (allowed) {
                filtered.add(meal);
            }
        }
        return filtered;
    }

    /**
     * Computes total calories, protein, carbs, fat, and sugar across all currently
     * selected meals.
     */
    public void calculateDailyNutrition() {
        totalCalories = 0;
        totalProtein = 0.0;
        totalCarbs = 0.0;
        totalFat = 0.0;
        totalSugar = 0.0;

        for (Meal meal : selectedMeals) {
            if (meal.getCalories() != null)
                totalCalories += meal.getCalories();
            if (meal.getProtein() != null)
                totalProtein += meal.getProtein();
            if (meal.getCarbs() != null)
                totalCarbs += meal.getCarbs();
            if (meal.getFat() != null)
                totalFat += meal.getFat();
            if (meal.getSugar() != null)
                totalSugar += meal.getSugar();
        }

        if (totalCaloriesLabel != null) {
            totalCaloriesLabel.setText(totalCalories + " kcal");
        }
        if (totalProteinLabel != null) {
            totalProteinLabel.setText(String.format("%.1f g", totalProtein));
        }
        if (totalCarbsLabel != null) {
            totalCarbsLabel.setText(String.format("%.1f g", totalCarbs));
        }
        if (totalFatLabel != null) {
            totalFatLabel.setText(String.format("%.1f g", totalFat));
        }
        if (totalSugarLabel != null) {
            totalSugarLabel.setText(String.format("%.1f g", totalSugar));
        }
    }

    /**
     * Updates the budget display UI with current weekly limit, spent amount, and
     * remaining budget.
     * Changes remaining balance text to red if the user is over budget.
     */
    public void updateBudgetDisplay() {
        if (budget == null) {
            return;
        }

        if (weeklyLimitLabel != null) {
            weeklyLimitLabel.setText(String.format("$%.2f", budget.getWeeklyLimit()));
        }
        if (spentAmountLabel != null) {
            spentAmountLabel.setText(String.format("$%.2f", budget.getSpentAmount()));
        }

        double remaining = budget.getRemainingBudget();
        if (remainingBudgetLabel != null) {
            remainingBudgetLabel.setText(String.format("$%.2f", remaining));

            // Visual feedback: turns text red when over budget
            if (remaining < 0) {
                remainingBudgetLabel.setStyle("-fx-text-fill: #e53935; -fx-font-weight: bold;");
                if (budgetStatusLabel != null) {
                    budgetStatusLabel.setText(String.format("Over Budget by $%.2f", Math.abs(remaining)));
                    budgetStatusLabel.setStyle("-fx-text-fill: #e53935; -fx-font-weight: bold;");
                }
            } else {
                remainingBudgetLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
                if (budgetStatusLabel != null) {
                    budgetStatusLabel.setText("Within Weekly Budget");
                    budgetStatusLabel.setStyle("-fx-text-fill: #2e7d32;");
                }
            }
        }
    }

    /**
     * Synchronizes ListView controls with the internal ArrayList items.
     */
    private void refreshListViews() {
        if (selectableMealsListView != null) {
            selectableMealsListView.setItems(FXCollections.observableArrayList(selectableMeals));
        }
        if (selectedMealsListView != null) {
            selectedMealsListView.setItems(FXCollections.observableArrayList(selectedMeals));
        }
    }

    // Sidebar Navigation Event Handlers

    @FXML
    public void onHomeButtonClick(ActionEvent event) {
        navigateTo(event, "/edu/utsa/cs3443/mealmarker/userguide.fxml");
    }

    @FXML
    public void onCreateUpdateProfileButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/utsa/cs3443/mealmarker/profile-view.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);
            ProfileController profileController = loader.getController();
            if (profileController != null && user != null) {
                profileController.initData(user);
            }
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Error navigating to profile view: " + e.getMessage());
        }
    }

    @FXML
    public void onBudgetButtonClick(ActionEvent event) {
        updateBudgetDisplay();
    }

    @FXML
    public void onUserGuideButtonClick(ActionEvent event) {
        navigateTo(event, "/edu/utsa/cs3443/mealmarker/userguide.fxml");
    }

    @FXML
    public void onLogoutButtonClick(ActionEvent event) {
        navigateTo(event, "/edu/utsa/cs3443/mealmarker/login-view.fxml");
    }

    private void navigateTo(ActionEvent event, String resourcePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Error navigating to " + resourcePath + ": " + e.getMessage());
        }
    }

    // Public API / Getters and Setters

    /**
     * Pass along the logged-in user when switching scenes to this controller.
     */
    public void initData(User user) {
        setUser(user);
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
        if (user != null && budget != null) {
            user.setBudget(budget.getWeeklyLimit());
        }
        updateBudgetDisplay();
    }

    public ArrayList<Meal> getSelectedMeals() {
        return selectedMeals;
    }

    public ArrayList<Meal> getSelectableMeals() {
        return selectableMeals;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            double limit = user.getBudget() > 0 ? user.getBudget() : 75.0;
            this.budget = new Budget(limit);
            this.selectableMeals = filterSelectableMeals(user.getPreferences());
            refreshListViews();
            updateBudgetDisplay();
        }
    }

    public int getTotalCalories() {
        return totalCalories;
    }

    public double getTotalProtein() {
        return totalProtein;
    }

    public double getTotalCarbs() {
        return totalCarbs;
    }

    public double getTotalFat() {
        return totalFat;
    }

    public double getTotalSugar() {
        return totalSugar;
    }
}
