package e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class RatingPopup extends BasePage {

    private final By popupOverlay = By.cssSelector(".popup-overlay");
    private final By modalContainer = By.cssSelector(".modal-container");
    private final By modalHeader = By.cssSelector(".modal-header span");
    private final By closeBtn = By.cssSelector(".close-btn");

    private final By driverStarButtons = By.cssSelector(
            ".rating-section:nth-of-type(1) .star-group button"
    );

    private final By vehicleStarButtons = By.cssSelector(
            ".rating-section:nth-of-type(2) .star-group button"
    );

    private final By commentTextarea = By.cssSelector("textarea[formControlName='comment']");

    private final By confirmBtn = By.cssSelector("button.confirm-btn");

    private final By swalContainer = By.cssSelector(".swal2-container");
    private final By swalPopup = By.cssSelector(".swal2-popup");
    private final By swalSuccessIcon = By.cssSelector(".swal2-icon.swal2-success");
    private final By swalTitle = By.cssSelector(".swal2-title");
    private final By swalConfirmBtn = By.cssSelector(".swal2-confirm");

    public RatingPopup(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        try {
            waitForElement(popupOverlay);
            waitForElement(modalContainer);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getHeaderText() {
        return waitForElement(modalHeader).getText().trim();
    }

    public RatingPopup rateDriver(int stars) {
        validateStarCount(stars);
        List<WebElement> starBtns = waitForElements(driverStarButtons);
        starBtns.get(stars - 1).click();
        return this;
    }

    public RatingPopup rateVehicle(int stars) {
        validateStarCount(stars);
        List<WebElement> starBtns = waitForElements(vehicleStarButtons);
        starBtns.get(stars - 1).click();
        return this;
    }

    public RatingPopup enterComment(String comment) {
        WebElement textarea = waitForElement(commentTextarea);
        textarea.clear();
        textarea.sendKeys(comment);
        return this;
    }

    public SwalSuccessPopup confirmAndExpectSuccess() {
        clickElement(confirmBtn);
        waitForElement(swalContainer);
        waitForElement(swalPopup);
        return new SwalSuccessPopup(driver);
    }

    public void confirm_expectFormInvalid() {
        clickElement(confirmBtn);
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    public RideDetailsPage close() {
        clickElement(closeBtn);
        wait.until(driver -> !isElementPresent(popupOverlay));
        return new RideDetailsPage(driver);
    }

    public RideDetailsPage closeByClickingOverlay() {
        waitForElement(popupOverlay).click();
        wait.until(driver -> !isElementPresent(popupOverlay));
        return new RideDetailsPage(driver);
    }

    private void validateStarCount(int stars) {
        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }
}
