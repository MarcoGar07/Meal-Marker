package model;

/**
 * Budget handles financial balance calculations and expense management over a weekly cycle.
 * Represents the budget model in the Meal Marker application.
 */
public class Budget {
    private double weeklyLimit;
    private double spentAmount;

    /**
     * Default constructor initializing with standard weekly budget limit of $75.00.
     */
    public Budget() {
        this(75.0, 0.0);
    }

    /**
     * Initializes a budget with a specified weekly limit.
     * Initial spent amount defaults to 0.0.
     *
     * @param weeklyLimit the maximum weekly spending limit
     */
    public Budget(double weeklyLimit) {
        this(weeklyLimit, 0.0);
    }

    /**
     * Overloaded constructor initializing limit and existing expenditure.
     *
     * @param weeklyLimit the maximum weekly spending limit
     * @param spentAmount current cumulative spending for the week
     */
    public Budget(double weeklyLimit, double spentAmount) {
        this.weeklyLimit = weeklyLimit;
        this.spentAmount = spentAmount;
    }

    /**
     * Increments total spent expenditure.
     *
     * @param amount the expense amount to add
     */
    public void addExpense(double amount) {
        this.spentAmount += amount;
    }

    /**
     * Decrements expenditure when a purchase is removed or canceled.
     *
     * @param amount the expense amount to deduct
     */
    public void removeExpense(double amount) {
        this.spentAmount -= amount;
        if (this.spentAmount < 0) {
            this.spentAmount = 0.0;
        }
    }

    /**
     * Resets the spent amount back to 0.0.
     */
    public void reset() {
        this.spentAmount = 0.0;
    }

    /**
     * Checks if current expenditures exceed the weekly limit.
     *
     * @return true if spentAmount is strictly greater than weeklyLimit
     */
    public boolean isOverBudget() {
        return spentAmount > weeklyLimit;
    }

    // Getters and Setters

    /**
     * Returns the maximum weekly spending limit.
     *
     * @return the weekly limit
     */
    public double getWeeklyLimit() {
        return weeklyLimit;
    }

    /**
     * Updates the weekly limit.
     *
     * @param weeklyLimit the new weekly limit
     */
    public void setWeeklyLimit(double weeklyLimit) {
        this.weeklyLimit = weeklyLimit;
    }

    /**
     * Returns the accumulated expenses.
     *
     * @return the spent amount
     */
    public double getSpentAmount() {
        return spentAmount;
    }

    /**
     * Sets the spent amount directly.
     *
     * @param spentAmount the spent amount to set
     */
    public void setSpentAmount(double spentAmount) {
        this.spentAmount = spentAmount;
    }

    /**
     * Calculates remaining balance (weeklyLimit - spentAmount).
     *
     * @return remaining budget balance
     */
    public double getRemainingBudget() {
        return weeklyLimit - spentAmount;
    }

    @Override
    public String toString() {
        return String.format("Budget [Weekly Limit: $%.2f, Spent: $%.2f, Remaining: $%.2f]",
                weeklyLimit, spentAmount, getRemainingBudget());
    }
}
