package e2e.pages;

import e2e.models.FavouriteRoute;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerHomepage extends BasePage {

    private final By mapComponent = By.cssSelector("app-map");
    private final By heroSection = By.cssSelector(".hero");
    private DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

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

    public CustomerHomepage switchToTab(String tabName) {
        waitForElement(By.cssSelector("[data-testId=tab-button-"+ tabName +"]")).click();
        return this;
    }

    public List<FavouriteRoute> getFavouriteRoutes() {
        return waitForElements(By.cssSelector("[data-favourite-route]"))
                .stream()
                .map(element -> {
                    List<String> stops = new ArrayList<>(element.findElements(
                                    By.cssSelector("[data-favourite-route-stop]")
                            )
                            .stream()
                            .map(WebElement::getText)
                            .toList());
                    String start = element.findElement(By.cssSelector("[data-favourite-route-start]")).getText();
                    String end = element.findElement(By.cssSelector("[data-favourite-route-end]")).getText();
                    stops.add(0, start);
                    stops.add(end);
                    FavouriteRoute route = new FavouriteRoute();
                    route.setElement(element);
                    route.setStops(stops);
                    return route;
                })
                .toList();
    }


    public CustomerHomepage selectFavouriteRoute(FavouriteRoute selectedRoute) {
        selectedRoute.getElement().click();
        return this;
    }

    public boolean isTabActive(String tabName) {
        var tabButton = waitForElement(By.cssSelector("[data-testId=tab-button-"+ tabName +"]"));
        return tabButton.getAttribute("class").contains("active");
    }

    public List<String> getRouteStops() {
        return waitForElements(By.cssSelector("[data-route-stop]"))
                .stream()
                .map(element -> Optional.ofNullable(element.getAttribute("value")).orElse(""))
                .toList();
    }

    public CustomerHomepage clickMoreOptions() {
        waitForElement(By.cssSelector("[data-testId=more-options-button]")).click();
        return this;
    }

    public CustomerHomepage selectSpecificTime() {
        waitForElement(By.cssSelector("[data-testId=schedule-specific-option]")).click();
        return this;
    }

    public CustomerHomepage setTime(LocalDateTime dateTime) {
        String formatted = dateTime.format(formatter);
        System.out.println("Setting time to: " + dateTime);
        System.out.println("Formatted date time: " + formatted);
        var input = waitForElement(By.cssSelector("[data-testId=schedule-datetime-input]"));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                input,
                formatted
        );
        return this;
    }

    public CustomerHomepage book() {
        waitForElement(By.cssSelector("[data-testId=book-button]")).click();
        return this;
    }

    public SwalSuccessPopup expectSwalSuccess() {
        return new SwalSuccessPopup(driver);
    }
}
