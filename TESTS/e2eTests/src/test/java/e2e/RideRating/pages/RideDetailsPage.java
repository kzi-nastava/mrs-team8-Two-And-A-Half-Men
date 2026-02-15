package e2e.RideRating.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class RideDetailsPage extends BasePage {

    private final By detailsContainer = By.cssSelector(".ride-details-container");
    private final By detailsContent = By.cssSelector(".details-content");
    private final By statusBadge = By.cssSelector(".status-badge");
    private final By rateRideBtn = By.cssSelector(".btn-rate");
    private final By loadingSpinner = By.cssSelector(".loading-spinner");
    private final By passengerReviewsHeading = By.xpath("//h3[normalize-space()='Passenger Reviews']");
    private final By allPassengerReviews = By.cssSelector(".passenger-review");

    private final By reviewerEmail = By.cssSelector(".passenger-email-review");
    private final By driverRatingValue = By.cssSelector(".ratings-row .rating-item:nth-of-type(1) .rating-value");
    private final By vehicleRatingValue = By.cssSelector(".ratings-row .rating-item:nth-of-type(2) .rating-value");
    private final By commentText = By.cssSelector(".comment-text");

    public RideDetailsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        try {
            waitForElement(detailsContainer);

            // waiting until loading disappear
            wait.until(driver -> !isElementPresent(loadingSpinner));
            waitForElement(detailsContent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getRideStatus() {
        try {
            return waitForElement(statusBadge).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isRateButtonVisible() {
        return isElementPresent(rateRideBtn);
    }

    public RatingPopup clickRateRide() {
        clickElement(rateRideBtn);
        return new RatingPopup(driver);
    }


    public boolean isPassengerReviewsSectionVisible() {
        try {
            waitForElement(passengerReviewsHeading);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getEmailFromReview(WebElement reviewCard) {
        return reviewCard.findElement(reviewerEmail).getText().trim();
    }

    public WebElement findReviewByEmail(String email) {
        List<WebElement> reviews = waitForElements(allPassengerReviews);
        for (WebElement review : reviews) {
            try {
                String foundEmail = review.findElement(reviewerEmail).getText().trim();
                if (foundEmail.equalsIgnoreCase(email)) {
                    return review;
                }
            } catch (Exception ignored) {}
        }
        throw new org.openqa.selenium.NoSuchElementException(
                "There is no rate for email: " + email
        );
    }

    public int getDriverRatingFromReview(WebElement reviewCard) {
        try {
            String text = reviewCard.findElement(driverRatingValue).getText().trim();
            return parseRatingFromText(text);
        } catch (Exception e) {
            return -1;
        }
    }

    public int getVehicleRatingFromReview(WebElement reviewCard) {
        try {
            String text = reviewCard.findElement(vehicleRatingValue).getText().trim();
            return parseRatingFromText(text);
        } catch (Exception e) {
            return -1;
        }
    }

    public String getCommentFromReview(WebElement reviewCard) {
        try {
            return reviewCard.findElement(commentText).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    private int parseRatingFromText(String text) {
        String clean = text.replaceAll("[^0-9/]", "").trim(); // for example 4/5
        System.out.println(clean);
        if (clean.contains("/")) {
            return Integer.parseInt(clean.split("/")[0]);
        }
        return Integer.parseInt(clean);
    }
}
