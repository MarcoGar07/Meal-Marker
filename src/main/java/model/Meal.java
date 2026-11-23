package model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


public class Meal {

    // Basic meal information
    private String mealId;
    private String restaurantId;
    private String restaurantName;
    private String name;
    private String category;
    private String servingSize;

    // Nutritional information
    private Integer calories;
    private Double protein;
    private Double carbs;
    private Double fat;
    private Double sugar;

    // Pricing information
    private Double price;
    private Double historicalPrice;

    // Verification and source information
    private String dataStatus;
    private boolean campusVerified;
    private boolean priceVerified;
    private String nutritionBasis;
    private String itemSourceUrl;
    private String nutritionSourceUrl;
    private String lastResearched;
    private String notes;

    // Dietary information
    private String allergens;
    private String dietaryTags;

    // Default constructor
    public Meal() {
    }

    // Loads meals from a CSV file
    public static List<Meal> loadMeals(File file) throws IOException {

        List<Meal> meals = new ArrayList<>();

        // Read the CSV file
        String csvContent = Files.readString(
                file.toPath(), StandardCharsets.UTF_8);

        List<List<String>> records = parseCsv(csvContent);

        if (records.isEmpty()) {
            return meals;
        }

        // First row contains the column names
        List<String> headers = records.get(0);

        // Remove UTF-8 BOM if present
        if (!headers.isEmpty()) {
            headers.set(0, headers.get(0).replace("\uFEFF", ""));
        }

        // Create a map to locate columns by their names
        Map<String, Integer> columnIndexes = new HashMap<>();

        for (int i = 0; i < headers.size(); i++) {
            columnIndexes.put(headers.get(i).trim(), i);
        }

        // Process the remaining rows
        for (int i = 1; i < records.size(); i++) {

            List<String> row = records.get(i);

            // Skip empty rows
            if (row.isEmpty() || row.stream().allMatch(String::isBlank)) {
                continue;
            }

            Meal meal = new Meal();

            meal.setMealId(getColumn(row, columnIndexes, "meal_id"));
            meal.setRestaurantId(getColumn(row, columnIndexes, "restaurant_id"));
            meal.setRestaurantName(getColumn(row, columnIndexes, "restaurant_name"));
            meal.setName(getColumn(row, columnIndexes, "item_name"));
            meal.setCategory(getColumn(row, columnIndexes, "category"));
            meal.setServingSize(getColumn(row, columnIndexes, "serving_size"));

            meal.setCalories(parseInteger(
                    getColumn(row, columnIndexes, "calories")));

            meal.setProtein(parseDouble(
                    getColumn(row, columnIndexes, "protein_g")));

            meal.setCarbs(parseDouble(
                    getColumn(row, columnIndexes, "carbs_g")));

            meal.setFat(parseDouble(
                    getColumn(row, columnIndexes, "fat_g")));

            meal.setSugar(parseDouble(
                    getColumn(row, columnIndexes, "sugar_g")));

            meal.setPrice(parseDouble(
                    getColumn(row, columnIndexes, "price_usd")));

            meal.setHistoricalPrice(parseDouble(
                    getColumn(row, columnIndexes, "historical_price_usd")));

            meal.setDataStatus(getColumn(row, columnIndexes, "data_status"));

            meal.setCampusVerified(
                    getColumn(row, columnIndexes, "campus_item_verified_2026"));

            meal.setPriceVerified(
                    getColumn(row, columnIndexes, "price_verified_2026"));

            meal.setNutritionBasis(
                    getColumn(row, columnIndexes, "nutrition_basis"));

            meal.setItemSourceUrl(
                    getColumn(row, columnIndexes, "item_source_url"));

            meal.setNutritionSourceUrl(
                    getColumn(row, columnIndexes, "nutrition_source_url"));

            meal.setLastResearched(
                    getColumn(row, columnIndexes, "last_researched"));

            meal.setNotes(getColumn(row, columnIndexes, "notes"));

            meal.setAllergens(getColumn(row, columnIndexes, "allergens"));

            meal.setDietaryTags(getColumn(row, columnIndexes, "dietary_tags"));

            meals.add(meal);
        }

        return meals;
    }

    // Retrieves a value using its column name
    private static String getColumn(
            List<String> row,
            Map<String, Integer> columnIndexes,
            String columnName) {

        Integer index = columnIndexes.get(columnName);

        if (index == null || index >= row.size()) {
            return "";
        }

        return row.get(index).trim();
    }

    // Basic CSV parser supporting quoted fields and escaped quotes
    private static List<List<String>> parseCsv(String content) {

        List<List<String>> records = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder field = new StringBuilder();

        boolean insideQuotes = false;

        for (int i = 0; i < content.length(); i++) {

            char current = content.charAt(i);

            if (insideQuotes) {

                if (current == '"') {

                    // Two quotation marks represent one literal quotation mark
                    if (i + 1 < content.length()
                            && content.charAt(i + 1) == '"') {

                        field.append('"');
                        i++;

                    } else {
                        insideQuotes = false;
                    }

                } else {
                    field.append(current);
                }

            } else {

                if (current == '"' && field.length() == 0) {
                    insideQuotes = true;

                } else if (current == ',') {
                    row.add(field.toString());
                    field.setLength(0);

                } else if (current == '\n' || current == '\r') {

                    row.add(field.toString());
                    field.setLength(0);

                    records.add(row);
                    row = new ArrayList<>();

                    // Handle Windows CRLF line endings
                    if (current == '\r'
                            && i + 1 < content.length()
                            && content.charAt(i + 1) == '\n') {
                        i++;
                    }

                } else {
                    field.append(current);
                }
            }
        }

        // Add the final field and row
        if (field.length() > 0 || !row.isEmpty()) {
            row.add(field.toString());
            records.add(row);
        }

        return records;
    }

    // Converts an empty numeric CSV value into null
    private static Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return Double.parseDouble(value.trim());
    }

    private static Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return Integer.parseInt(value.trim());
    }

    // Converts CSV Yes/No values into booleans
    private static boolean parseBoolean(String value) {
        return value != null && value.trim().equalsIgnoreCase("Yes");
    }

    // Getters and setters

    public String getMealId() {
        return mealId;
    }

    public void setMealId(String mealId) {
        this.mealId = mealId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = this.restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getServingSize() {
        return servingSize;
    }

    public void setServingSize(String servingSize) {
        this.servingSize = servingSize;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
    }

    public Double getCarbs() {
        return carbs;
    }

    public void setCarbs(Double carbs) {
        this.carbs = carbs;
    }

    public Double getFat() {
        return fat;
    }

    public void setFat(Double fat) {
        this.fat = fat;
    }

    public Double getSugar() {
        return sugar;
    }

    public void setSugar(Double sugar) {
        this.sugar = sugar;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getHistoricalPrice() {
        return historicalPrice;
    }

    public void setHistoricalPrice(Double historicalPrice) {
        this.historicalPrice = historicalPrice;
    }

    public String getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(String dataStatus) {
        this.dataStatus = dataStatus;
    }

    public boolean isCampusVerified() {
        return campusVerified;
    }

    public void setCampusVerified(String campusVerified) {
        this.campusVerified = parseBoolean(campusVerified);
    }

    public boolean isPriceVerified() {
        return priceVerified;
    }

    public void setPriceVerified(String priceVerified) {
        this.priceVerified = parseBoolean(priceVerified);
    }

    public String getNutritionBasis() {
        return nutritionBasis;
    }

    public void setNutritionBasis(String nutritionBasis) {
        this.nutritionBasis = nutritionBasis;
    }

    public String getItemSourceUrl() {
        return itemSourceUrl;
    }

    public void setItemSourceUrl(String itemSourceUrl) {
        this.itemSourceUrl = itemSourceUrl;
    }

    public String getNutritionSourceUrl() {
        return nutritionSourceUrl;
    }

    public void setNutritionSourceUrl(String nutritionSourceUrl) {
        this.nutritionSourceUrl = nutritionSourceUrl;
    }

    public String getLastResearched() {
        return lastResearched;
    }

    public void setLastResearched(String lastResearched) {
        this.lastResearched = lastResearched;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAllergens() {
        return allergens;
    }

    public void setAllergens(String allergens) {
        this.allergens = allergens;
    }

    public String getDietaryTags() {
        return dietaryTags;
    }

    public void setDietaryTags(String dietaryTags) {
        this.dietaryTags = dietaryTags;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "mealId='" + mealId + '\'' +
                ", name='" + name + '\'' +
                ", restaurantName='" + restaurantName + '\'' +
                ", price=" + price +
                ", calories=" + calories +
                '}';
    }
}
