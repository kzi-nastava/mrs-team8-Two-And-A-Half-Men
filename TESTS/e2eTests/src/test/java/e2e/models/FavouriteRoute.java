package e2e.models;

import lombok.Data;
import org.openqa.selenium.WebElement;

import java.util.List;

@Data
public class FavouriteRoute {
    private WebElement element;
    private List<String> stops;
}
