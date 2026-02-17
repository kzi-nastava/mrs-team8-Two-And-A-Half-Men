package e2e.tests;

import e2e.DatabaseResetUtility;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@Tag("JUnitTests")
public class TestSetup {
    protected WebDriver driver;

    protected static final String BASE_URL = System.getenv("BASE_URL");

    @BeforeEach
    public void initializeWebDriver() {
        if (BASE_URL == null || BASE_URL.isEmpty()) {
            throw new IllegalStateException("BASE_URL environment variable is not set.");
        }
        WebDriverManager.chromedriver().browserVersion("143").setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
        driver.get(BASE_URL);
        try {
            DatabaseResetUtility.resetDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @AfterEach
    public void quitDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}
