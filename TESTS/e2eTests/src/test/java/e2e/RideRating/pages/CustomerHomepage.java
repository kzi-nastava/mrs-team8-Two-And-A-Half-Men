package e2e.RideRating.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CustomerHomepage extends BasePage {

    private final By mapComponent = By.cssSelector("app-map");
    private final By heroSection = By.cssSelector(".hero");

    public CustomerHomepage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        try {
            waitForElement(mapComponent);
            waitForElement(heroSection);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public RideHistoryPage navigateToHistory() {
        findNavbarButtonByText("Ride History").click();
        waitForUrlContains("/history");
        return new RideHistoryPage(driver);
    }
}
