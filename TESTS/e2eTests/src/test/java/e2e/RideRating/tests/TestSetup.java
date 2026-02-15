package e2e.RideRating.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class TestSetup {
    protected WebDriver driver;

    protected static final String BASE_URL = "http://localhost:4200";

    protected static final String TEST_USERNAME = "ana@gmail.com";
    protected static final String TEST_PASSWORD = "password";
    protected static final int DRIVER_RATING = 5;
    protected static final int VEHICLE_RATING = 4;
    protected static final String COMMENT = "Everything was excellent.";

    @BeforeEach
    public void initializeWebDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
        driver.get(BASE_URL);
    }

    @AfterEach
    public void quitDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}
