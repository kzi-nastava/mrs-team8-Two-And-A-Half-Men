package e2e.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.channels.Selector;

public class HomePageAdmin {
    private WebDriver driver;
    @FindBy(css = ".ride-active-container")
    private WebElement activeRidesContainer;
    @FindBy(css = "button[aria-label=\"Ride History\"]")
    private WebElement rideHistoryButton;




    public HomePageAdmin(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(this.driver, this);
    }
    public boolean isOnHomePageAdmin() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOf(activeRidesContainer));
        return activeRidesContainer.isDisplayed();
    }
    public void clickRideHistoryButton() {
            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.until(ExpectedConditions.elementToBeClickable(rideHistoryButton));
            rideHistoryButton.click();
        }

}
