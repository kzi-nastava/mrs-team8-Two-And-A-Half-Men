package e2e.tests;

import e2e.DatabaseResetUtility;
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
