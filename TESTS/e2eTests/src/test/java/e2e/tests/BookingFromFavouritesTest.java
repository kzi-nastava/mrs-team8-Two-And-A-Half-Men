package e2e.tests;

import e2e.pages.CustomerHomepage;
import e2e.pages.LoginPage;
import e2e.pages.RideDetailsPage;
import e2e.pages.UnregisteredHomepage;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

public class BookingFromFavouritesTest extends TestSetup {

    protected static final String TEST_USERNAME = "customer@test.com";
    protected static final String TEST_PASSWORD = "password";

    // =====================================================================
    // HAPPY PATH
    // =====================================================================

    @Test
    void bookFromFavourites_happyPath_fullFlow() {
        // Unregistered Home Page
        UnregisteredHomepage homePage = new UnregisteredHomepage(driver);
        assertTrue(homePage.isLoaded(), "Unregistered home page is not presented");

        // Login
        LoginPage loginPage = homePage.clickLogin();
        assertTrue(loginPage.isLoaded(), "Login page is not presented");

        // Customer Home Page
        CustomerHomepage customerHomePage = loginPage.login(TEST_USERNAME, TEST_PASSWORD);
        assertTrue(customerHomePage.isLoaded(), "Customer home page is not presented");

        // Favourites tab
        customerHomePage.switchToTab("favorites");
        assertTrue(customerHomePage.isTabActive("favorites"), "Favourites tab is not active");

        var routes = customerHomePage.getFavouriteRoutes();
        assertFalse(routes.isEmpty(), "No favourite routes are presented");

        var selectedRoute = routes.stream().max(Comparator.comparingInt(r -> r.getStops().size())).orElseThrow();

        customerHomePage.selectFavouriteRoute(selectedRoute);
        assertTrue(customerHomePage.isTabActive("route"));

        var routeStops = customerHomePage.getRouteStops();

        assertEquals(routeStops.size(), selectedRoute.getStops().size(),
                "Number of stops in route details doesn't match with number of stops in selected favourite route");

        for (int i = 0; i < routeStops.size(); i++) {
            assertEquals(routeStops.get(i), selectedRoute.getStops().get(i),
                "Stop " + (i + 1) + " in route details doesn't match with stop " + (i + 1) + " in selected favourite route");
        }

        customerHomePage.clickMoreOptions().switchToTab("time");
        assertTrue(customerHomePage.isTabActive("time"), "Time tab is not active");
        customerHomePage
                .selectSpecificTime()
                .setTime(LocalDateTime.now().plusMinutes(20))
                .book();

        var swalSuccessPopup = customerHomePage
                .expectSwalSuccess();

        // SweetAlert2 success modal
        assertTrue(swalSuccessPopup.isLoaded(), "SweetAlert2 success popup is not presented");
        assertTrue(swalSuccessPopup.hasSuccessIcon(), "SweetAlert2 has not success icon");

        RideDetailsPage detailsAfterRating = swalSuccessPopup.clickOk();
        assertTrue(detailsAfterRating.isLoaded(), "Ride Details page is not reloaded");

        assertEquals("Pending", detailsAfterRating.getRideStatus(),
                "Ride status is not 'Pending' after booking from favourites with specific time");

    }
}
