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

    @BeforeEach
    public void initializeWebDriver() {
        WebDriverManager.chromedriver().browserVersion("144").setup();
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
