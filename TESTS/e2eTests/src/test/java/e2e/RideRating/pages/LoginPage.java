package e2e.RideRating.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage extends BasePage {

    private final By usernameInput = By.id("username");
    private final By passwordInput = By.id("password");
    private final By rememberMeCheckbox = By.id("rememberMe");
    private final By submitBtn = By.cssSelector("button[type='submit']");

    private final By usernameError = By.cssSelector(".form-group small");
    private final By loginContainer = By.cssSelector(".login-container");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        try {
            waitForElement(loginContainer);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public LoginPage enterUsername(String username) {
        WebElement input = waitForElement(usernameInput);
        input.clear();
        input.sendKeys(username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        WebElement input = waitForElement(passwordInput);
        input.clear();
        input.sendKeys(password);
        return this;
    }

    public LoginPage checkRememberMe() {
        WebElement checkbox = waitForElement(rememberMeCheckbox);
        if (!checkbox.isSelected()) {
            checkbox.click();
        }
        return this;
    }

    public CustomerHomepage submitAndExpectSuccess() {
        clickElement(submitBtn);
        waitForUrlContains("/");
        return new CustomerHomepage(driver);
    }

    public CustomerHomepage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return submitAndExpectSuccess();
    }
}