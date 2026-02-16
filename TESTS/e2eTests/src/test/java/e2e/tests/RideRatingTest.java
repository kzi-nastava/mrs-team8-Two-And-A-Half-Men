package e2e.tests;

import e2e.pages.*;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

public class RideRatingTest extends TestSetup {

    protected static final String TEST_USERNAME = "customer@test.com";
    protected static final String TEST_PASSWORD = "password";
    protected static final int DRIVER_RATING = 5;
    protected static final int VEHICLE_RATING = 4;
    protected static final String COMMENT = "Everything was excellent.";

    // =====================================================================
    // HAPPY PATH
    // =====================================================================

    @Test
    void rateRide_happyPath_fullFlow() {
        // Unregistered Home Page
        UnregisteredHomepage homePage = new UnregisteredHomepage(driver);
        assertTrue(homePage.isLoaded(), "Unregistered home page is not presented");

        // Login
        LoginPage loginPage = homePage.clickLogin();
        assertTrue(loginPage.isLoaded(), "Login page is not presented");

        // Customer Home Page
        CustomerHomepage customerHomePage = loginPage.login(TEST_USERNAME, TEST_PASSWORD);
        assertTrue(customerHomePage.isLoaded(), "Customer home page is not presented");

        // Ride History
        RideHistoryPage historyPage = customerHomePage.navigateToHistory();
        assertTrue(historyPage.isLoaded(), "Ride History page is not presented");
        assertTrue(historyPage.hasRateableRide(),
                "There is no FINISHED/PANICKED/INTERRUPTED ride in history");

        // Ride Details
        RideDetailsPage detailsPage = historyPage.clickFirstRateableRide();
        assertTrue(detailsPage.isLoaded(), "Ride Details page is not presented");

        String status = detailsPage.getRideStatus();
        assertTrue(
                status.equals("Finished") || status.equals("Panicked") || status.equals("Interrupted"),
                "Opened ride has not appropriate status for rating, status: " + status
        );

        assertTrue(detailsPage.isRateButtonVisible(), "Rate ride button is not presented");

        // Rating Popup
        RatingPopup ratingPopup = detailsPage.clickRateRide();
        assertTrue(ratingPopup.isLoaded(), "Rating popup is not presented");
        assertEquals("Leave a rate", ratingPopup.getHeaderText(),
                "Rating popup title is not ok");

        SwalSuccessPopup swal = ratingPopup
                .rateDriver(DRIVER_RATING)
                .rateVehicle(VEHICLE_RATING)
                .enterComment(COMMENT)
                .confirmAndExpectSuccess();

        // SweetAlert2 success modal
        assertTrue(swal.isLoaded(), "SweetAlert2 success popup is not presented");
        assertTrue(swal.hasSuccessIcon(), "SweetAlert2 has not success icon");


        RideDetailsPage detailsAfterRating = swal.clickOk();
        assertTrue(detailsAfterRating.isLoaded(), "Ride Details page is not reloaded");
        assertFalse(ratingPopup.isLoaded(), "Rating popup should be closed");

        assertTrue(detailsAfterRating.isPassengerReviewsSectionVisible(),
                "Passenger Reviews section with entered rate is not visible");

        WebElement myReview = detailsAfterRating.findReviewByEmail(TEST_USERNAME);

        String reviewEmail = detailsAfterRating.getEmailFromReview(myReview);
        assertEquals(TEST_USERNAME, reviewEmail,
                "Review card with rating doesn't match with users email");

        int displayedDriverRating = detailsAfterRating.getDriverRatingFromReview(myReview);
        assertEquals(DRIVER_RATING, displayedDriverRating,
                "Driver rating doesn't match with value sent");

        int displayedVehicleRating = detailsAfterRating.getVehicleRatingFromReview(myReview);
        assertEquals(VEHICLE_RATING, displayedVehicleRating,
                "Driver rating doesn't match with value sent");

        String displayedComment = detailsAfterRating.getCommentFromReview(myReview);
        assertEquals(COMMENT, displayedComment,
                "Comment doesn't match with value sent");
    }
}
