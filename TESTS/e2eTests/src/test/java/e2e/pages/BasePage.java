package e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 15);
    }

    protected WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickableElement(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected List<WebElement> waitForElements(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected WebElement waitForPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    protected void clickElement(By locator) {
        waitForClickableElement(locator).click();
    }

    protected void waitForUrlContains(String urlPart) {
        wait.until(ExpectedConditions.urlContains(urlPart));
    }

    protected boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected WebElement findNavbarButtonByText(String label) {
        waitForElement(By.cssSelector("app-navbar, nav, header"));

        List<WebElement> anchors = driver.findElements(By.tagName("a"));
        for (WebElement el : anchors) {
            if (el.getText().trim().equalsIgnoreCase(label) && el.isDisplayed()) {
                return el;
            }
        }

        List<WebElement> buttons = driver.findElements(By.tagName("button"));
        for (WebElement el : buttons) {
            if (el.getText().trim().equalsIgnoreCase(label) && el.isDisplayed()) {
                return el;
            }
        }

        throw new org.openqa.selenium.NoSuchElementException(
                "There is no navbar element with text: '" + label + "'"
        );
    }
}
