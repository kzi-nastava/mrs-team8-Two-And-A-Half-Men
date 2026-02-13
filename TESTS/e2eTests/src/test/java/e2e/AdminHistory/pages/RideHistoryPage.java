package e2e.AdminHistory.pages;

import e2e.AdminHistory.models.History;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RideHistoryPage {
    @FindBy(css = ".ride-history-container")
    private WebElement rideHistoryContainer;
    private WebDriver driver;

    @FindBy(css = ".page-size-select")
    private WebElement pageSizeSelect;
    @FindBy(css = ".ride-card")
    private List<WebElement> rideCards;
    @FindBy(css = ".sort-field-select")
    private WebElement sortFieldSelect;
    @FindBy(css = ".btn-sort")
    private WebElement sortButton;
    @FindBy(css = ".btn-filter")
    private WebElement filterButton;
    @FindBy(id = "startDate")
    private WebElement startDateInput;
    @FindBy(id = "endDate")
    private WebElement endDateInput;
    @FindBy(id = "driverId")
    private WebElement driverIdInput;
    @FindBy(id = "customerId")
    private WebElement customerIdInput;
    @FindBy(css = ".btn-apply")
    private WebElement applyFilterButton;
    @FindBy(css = ".btn-clear")
    private WebElement clearFilterButton;
    public RideHistoryPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(this.driver, this);
    }

    public boolean isOnRideHistoryPage() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOf(rideHistoryContainer));
        return rideHistoryContainer.isDisplayed();
    }

    public void selectPageSize(String pageSize) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(pageSizeSelect));
        Select select = new Select(pageSizeSelect);
        select.selectByVisibleText(pageSize);
    }

    public List<History> getRidesOnPage() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfAllElements(rideCards));
        List<History> ridesOnPage = new ArrayList<History>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
        for (WebElement rideCard : rideCards) {
            String status = rideCard.findElement(By.cssSelector(".status-badge")).getText().trim();

            WebElement rideInfo = rideCard.findElement(By.cssSelector(".ride-info"));

            String startLocation = getInfoValue(rideInfo, "Start point:");
            String endLocation = getInfoValue(rideInfo, "Destination:");
            String startTimeStr = getInfoValue(rideInfo, "Start time:");
            String endTimeStr = getInfoValue(rideInfo, "End time:");
            String costStr = getInfoValue(rideInfo, "Total cost:");
            String driverName = getInfoValue(rideInfo, "Driver:");
            String customerOwner = getInfoValue(rideInfo, "Ride owner:");
            String sheculedTime = getInfoValue(rideInfo, "Scheduled time:");

            if(startTimeStr.isEmpty()) {
                startTimeStr = "01.01.1970. 00:00";
            }

            if(endTimeStr.isEmpty()) {
                endTimeStr = "01.01.1970. 00:00";
            }
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr.trim(), formatter);
            LocalDateTime endTime = LocalDateTime.parse(endTimeStr.trim(), formatter);

            Long cost = Long.parseLong(costStr.replace("RSD", "").trim());

            History ride = new History(startTime, endTime, cost, status, driverName, customerOwner, startLocation, endLocation,sheculedTime);
            ridesOnPage.add(ride);
        }
        return ridesOnPage;
    }


    private String getInfoValue(WebElement rideInfo, String labelText) {
        try {
            String lowerLabel = labelText.toLowerCase();
            String xpath = String.format(
                    ".//span[@class='label' and translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='%s']/following-sibling::span[contains(@class, 'value')]",
                    lowerLabel
            );
            return rideInfo.findElement(By.xpath(xpath)).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public void clickSortButton(boolean assending)
    {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(this.sortButton));
        String sortText = sortButton.getText();
        if(assending && !sortText.equalsIgnoreCase(" ↑ Ascending "));
        {
            sortButton.click();
        }
        if(!assending && !sortText.equalsIgnoreCase(" ↓ Descending "))
        {
            sortButton.click();
        }
    }
    public void selectSortField(String field)
    {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(this.sortFieldSelect));
        Select select = new Select(this.sortFieldSelect);
        select.selectByVisibleText(field);
    }

    public void clickFilterButton() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(this.filterButton));
        try {
            startDateInput.click();
        }catch (Exception e){
            filterButton.click();
        }
    }
    private void SetStartDate(String startDate) {
        if(startDate == null) {
            return;
        }

        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOf(this.startDateInput));
        this.startDateInput.sendKeys(Keys.ARROW_LEFT);
        this.startDateInput.sendKeys(Keys.ARROW_LEFT);
        this.startDateInput.sendKeys(startDate);
    }
    private void SetEndDate(String endDate) {
        if (endDate == null) {
            return;
        }
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOf(this.endDateInput));
        this.endDateInput.sendKeys(Keys.ARROW_LEFT);
        this.endDateInput.sendKeys(Keys.ARROW_LEFT);
        this.endDateInput.sendKeys(endDate);
    }
    private void SetDriverId(String driverId) {
        if(driverId == null) {
           return;
        }
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOf(this.driverIdInput));
        this.driverIdInput.sendKeys(driverId);
    }
    private void SetCustomerId(String customerId) {
        if(customerId == null) {
            return;
        }
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOf(this.customerIdInput));
        this.customerIdInput.sendKeys(customerId);
    }
    public void clearFilters() {
         pressClearFilterButton();
    }
    public void setFilter(String startDate, String endDate, String driverId, String customerId) {
        SetStartDate(startDate);
        SetEndDate(endDate);
        SetDriverId(driverId);
        SetCustomerId(customerId);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(this.applyFilterButton));
        applyFilterButton.click();
    }
    private void pressClearFilterButton() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(this.clearFilterButton));
        clearFilterButton.click();
    }

}
