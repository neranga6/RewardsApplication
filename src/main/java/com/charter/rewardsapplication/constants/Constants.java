package com.charter.rewardsapplication.constants;


/**
 * Centralized constants for reward calculation logic.
 * This class holds all business rule thresholds and rates.
 */
public final class Constants {

    /**
     * Minimum amount to start earning points.
     */
    public static final int LOWER_THRESHOLD = 50;

    /**
     * Amount above which higher reward rate applies.
     */
    public static final int UPPER_THRESHOLD = 100;

    /**
     * Points per dollar between $50 and $100.
     */
    public static final int LOWER_RATE = 1;

    /**
     * Points per dollar above $100.
     */
    public static final int UPPER_RATE = 2;

    /**
     * Base points earned between $50 and $100.
     */
    public static final int BASE_POINTS =
            (UPPER_THRESHOLD - LOWER_THRESHOLD) * LOWER_RATE;

    /**
     * Private constructor to prevent instantiation.
     */
    private Constants() {
        throw new IllegalStateException("Utility class");
    }
}