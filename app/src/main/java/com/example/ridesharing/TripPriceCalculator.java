package com.example.ridesharing;

public class TripPriceCalculator {

    private static final double BASE_FARE = 5.0;  // Base fare in your currency
    private static final double DISTANCE_RATE = 0.6;  // Distance rate per kilometer or mile
    private static final double DURATION_RATE = 0.2;  // Duration rate per minute


    private static double calculateTripPrice(double distance, int duration) {
        double distanceFare = distance * DISTANCE_RATE;
        double durationFare = duration * DURATION_RATE;
        double totalFare = BASE_FARE + distanceFare + durationFare ;

        // Round the fare to 2 decimal places
        totalFare = Math.round(totalFare * 100.0) / 100.0;

        return totalFare;
    }
}
