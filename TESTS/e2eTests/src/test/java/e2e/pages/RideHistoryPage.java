package e2e.pages;
import e2e.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class RideHistoryPage extends BasePage {

    private final By historyContainer = By.cssSelector(".ride-history-container");
    private final By historyHeader = By.cssSelector(".history-header h2");

    // app-rides-list -> ride-list.component.html
    private final By rideCards = By.cssSelector(".ride-card");

    private final By finishedStatusBadge = By.cssSelector(".status-badge.status-finished");
    private final By panickedStatusBadge = By.cssSelector(".status-badge.status-panicked");
    private final By interruptedStatusBadge = By.cssSelector(".status-badge.status-interrupted");

    // Selector for rateable statuses
    private final By rateableRideCard = By.cssSelector(
            ".ride-card:has(.status-badge.status-finished), " +
                    ".ride-card:has(.status-badge.status-panicked), " +
                    ".ride-card:has(.status-badge.status-interrupted)"
    );

    public RideHistoryPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        try {
            waitForElement(historyContainer);
            waitForElement(historyHeader);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getAllRideCards() {
        try {
            return waitForElements(rideCards);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public boolean hasRateableRide() {
        return isElementPresent(finishedStatusBadge)
                || isElementPresent(panickedStatusBadge)
                || isElementPresent(interruptedStatusBadge);
    }

    public RideDetailsPage clickFirstRateableRide() {
        List<WebElement> cards = getAllRideCards();

        for (WebElement card : cards) {
            if (isRateableCard(card)) {
                card.click();
                waitForUrlContains("/rides/");
                return new RideDetailsPage(driver);
            }
        }
        throw new RuntimeException("There is no rateable rides (FINISHED/PANICKED/INTERRUPTED) in history");
    }

    private boolean isRateableCard(WebElement card) {
        try {
            return card.findElement(By.cssSelector(
                    ".status-badge.status-finished, " +
                            ".status-badge.status-panicked, " +
                            ".status-badge.status-interrupted"
            )) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
