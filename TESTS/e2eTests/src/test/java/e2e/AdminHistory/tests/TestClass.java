package e2e.AdminHistory.tests;

import e2e.DatabaseResetUtility;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;


public class TestClass {
    protected static final String BASE_URL = System.getenv("BASE_URL");
    private final String CHROME_VERSION = System.getenv("CHROME_VERSION");
    public String frontendUrl;
    public WebDriver driver;
    @BeforeClass
    @Parameters({"frontendUrl"})
    public void setup(String frontendUrl) {
        WebDriverManager.chromedriver().browserVersion(CHROME_VERSION).setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        this.driver = new ChromeDriver(options);
        this.frontendUrl = BASE_URL;
        try {
            System.out.println("Resetting database before tests...");
            DatabaseResetUtility.resetDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.close();
            driver.quit();
        }
    }
}
