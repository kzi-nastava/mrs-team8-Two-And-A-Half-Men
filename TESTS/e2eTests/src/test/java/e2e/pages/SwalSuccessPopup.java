package e2e.pages;

import e2e.pages.BasePage;
import e2e.pages.RideDetailsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SwalSuccessPopup extends BasePage {

    private final By popupOverlay = By.cssSelector(".popup-overlay");

    private final By swalPopupLocator = By.cssSelector(".swal2-popup");
    private final By successIcon = By.cssSelector(".swal2-icon.swal2-success");
    private final By titleLocator = By.cssSelector(".swal2-title");
    private final By okBtn = By.cssSelector(".swal2-confirm");

    public SwalSuccessPopup(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        try {
            waitForElement(swalPopupLocator);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasSuccessIcon() {
        return isElementPresent(successIcon);
    }

    public String getTitle() {
        return waitForElement(titleLocator).getText().trim();
    }

    public RideDetailsPage clickOk() {
        clickElement(okBtn);
        wait.until(driver -> !isElementPresent(swalPopupLocator));
        wait.until(driver -> !isElementPresent(popupOverlay));
        return new RideDetailsPage(driver);
    }
}
