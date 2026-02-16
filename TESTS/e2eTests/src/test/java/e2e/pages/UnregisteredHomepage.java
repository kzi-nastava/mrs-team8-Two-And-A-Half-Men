package e2e.pages;

import e2e.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class UnregisteredHomepage extends BasePage {

    private final By estimateForm = By.cssSelector("app-estimate-form");
    private final By mapComponent = By.cssSelector("app-map");

    public UnregisteredHomepage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        try {
            waitForElement(estimateForm);
            waitForElement(mapComponent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public LoginPage clickLogin() {
        findNavbarButtonByText("Login").click();
        waitForUrlContains("/login");
        return new LoginPage(driver);
    }
}
