package e2e.AdminHistory.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomeUnregistered {

WebDriver driver;

@FindBy(css = ".navbar-right button:nth-child(1)")
private WebElement loginButton;


public HomeUnregistered(WebDriver driver , String url) {
    this.driver = driver;
    this.driver.navigate().to(url);
    PageFactory.initElements(this.driver, this);
}

public void clickLoginButton() {
    loginButton.click();
}

}
