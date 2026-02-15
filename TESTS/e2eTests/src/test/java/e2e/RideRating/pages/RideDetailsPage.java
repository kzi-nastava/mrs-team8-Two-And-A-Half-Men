package e2e.RideRating.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class RideDetailsPage extends BasePage {

    private final By detailsContainer = By.cssSelector(".ride-details-container");
    private final By detailsContent = By.cssSelector(".details-content");

    private final By statusBadge = By.cssSelector(".status-badge");

    private final By rateRideBtn = By.cssSelector(".btn-rate");

    private final By backBtn = By.cssSelector("button[appButton]");

    private final By loadingSpinner = By.cssSelector(".loading-spinner");

    public RideDetailsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        try {
            waitForElement(detailsContainer);

            // waiting until loading disappear
            wait.until(driver -> !isElementPresent(loadingSpinner));
            waitForElement(detailsContent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getRideStatus() {
        try {
            return waitForElement(statusBadge).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isRateButtonVisible() {
        return isElementPresent(rateRideBtn);
    }

    public RatingPopup clickRateRide() {
        clickElement(rateRideBtn);
        return new RatingPopup(driver);
    }

    public void goBack() {
        clickElement(backBtn);
    }
}
