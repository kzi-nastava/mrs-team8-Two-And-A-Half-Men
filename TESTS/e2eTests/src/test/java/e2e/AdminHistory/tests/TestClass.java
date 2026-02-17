package e2e.AdminHistory.tests;

import e2e.DatabaseResetUtility;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;


public class TestClass {

    public String frontendUrl;
    public WebDriver driver;
    @BeforeClass
    @Parameters({"frontendUrl"})
    public void setup(String frontendUrl) {
        WebDriverManager.chromedriver().browserVersion("144").setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        this.driver = new ChromeDriver(options);
        this.frontendUrl = frontendUrl;
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
