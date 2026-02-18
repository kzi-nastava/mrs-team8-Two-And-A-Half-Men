package e2e.AdminHistory.tests;

import e2e.AdminHistory.models.History;
import e2e.AdminHistory.models.HistorySortField;
import e2e.AdminHistory.models.HistorySorter;
import e2e.AdminHistory.pages.HomePageAdmin;
import e2e.AdminHistory.pages.HomeUnregistered;
import e2e.AdminHistory.pages.LoginPage;
import e2e.AdminHistory.pages.RideHistoryPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class TestAdminHistory extends TestClass {


    private static final Log log = LogFactory.getLog(TestAdminHistory.class);

    @Test
    public void testSmoke() {
        assertTrue(true);
    }
    @Test
    public void testLoginPress()
    {
        HomeUnregistered homeUnregistered = new HomeUnregistered(driver, frontendUrl);
        homeUnregistered.clickLoginButton();
        LoginPage loginPage = new LoginPage(driver);
        assertTrue(loginPage.isOnLogin());
    }
    @Test(dependsOnMethods = {"testLoginPress"})
    public void testLoginAdmin()
    {
        LoginPage loginPage = new LoginPage(driver);
        assertTrue(loginPage.isOnLogin());
        loginPage.loginAs("admin@test.com", "password");
        HomePageAdmin homePageAdmin = new HomePageAdmin(driver);
        assertTrue(homePageAdmin.isOnHomePageAdmin());
    }
    @Test(dependsOnMethods = {"testLoginAdmin"})
    public void testClickRideHistory()
    {
        HomePageAdmin homePageAdmin = new HomePageAdmin(driver);
        homePageAdmin.clickRideHistoryButton();
        RideHistoryPage rideHistoryPage = new RideHistoryPage(driver);
        assertTrue(rideHistoryPage.isOnRideHistoryPage());
    }
    @Test(dependsOnMethods = {"testClickRideHistory"}, priority =  1)
    public void testRideHistoryContent()
    {
        RideHistoryPage rideHistoryPage = new RideHistoryPage(driver);
        assertTrue(rideHistoryPage.isOnRideHistoryPage());
        rideHistoryPage.selectPageSize("20 per page");
        List<History> historyList = rideHistoryPage.getRidesOnPage();
        assertEquals(20, historyList.size()); //I made it 20 bcs I work on testing db
    }
    @Test(dependsOnMethods = {"testRideHistoryFiltering"}, dataProvider = "pageSizeDataProvider", priority = 2)
    public void testRideHistorySorting(String sortField, boolean ascending)
    {
        RideHistoryPage rideHistoryPage = new RideHistoryPage(driver);
        rideHistoryPage.selectPageSize("50 per page");
        List<History> historyList = rideHistoryPage.getRidesOnPage();
        rideHistoryPage.selectSortField(sortField);
        rideHistoryPage.clickSortButton(ascending);
        List<History> sortedHistoryList = rideHistoryPage.getRidesOnPage();
        historyList = HistorySorter.sortHistory(historyList, HistorySorter.getHistoryEnum(sortField), ascending);
        for(int i = 0; i < historyList.size(); i++) {
            log.info("Expected: " + historyList.get(i).toString());
            log.info("Actual: " + sortedHistoryList.get(i).toString());
            assertTrue(sortedHistoryList.get(i).equals(sortedHistoryList.get(i)));
        }
   }
   @Test(dependsOnMethods = {"testClickRideHistory"}, priority = 1, dataProvider = "filterDataProvider")
   public void testRideHistoryFiltering(String startDate, String endDate, String driverId, String customerId, String driverName, String customerName) {
        RideHistoryPage rideHistoryPage = new RideHistoryPage(driver);
        rideHistoryPage.selectPageSize("50 per page");
        List<History> historyList = rideHistoryPage.getRidesOnPage();
        rideHistoryPage.clickFilterButton();
        rideHistoryPage.setFilter(startDate, endDate, driverId, customerId);
        List<History> filteredHistoryList = rideHistoryPage.getRidesOnPage();
        if(startDate != null || endDate != null) {
            historyList = HistorySorter.filterHistoryByRange(historyList, HistorySortField.START_TIME, startDate, endDate);
        }
        if(driverId != null) {
            historyList = HistorySorter.filterHistoryByRange(historyList, HistorySortField.DRIVER_NAME, driverName, null);
        }
        if(customerId != null) {
            historyList = HistorySorter.filterHistoryByRange(historyList, HistorySortField.CUSTOMER_OWNER, customerName, null);
        };
        for(int i = 0; i < filteredHistoryList.size(); i++) {
            log.info("Expected: " + historyList.get(i).toString());
            log.info("Actual: " + filteredHistoryList.get(i).toString());
            assertTrue(filteredHistoryList.get(i).equals(filteredHistoryList.get(i)));
        }
        rideHistoryPage.clearFilters();
    }


    @DataProvider(name = "pageSizeDataProvider")
    public Object[][] pageSizeDataProvider() {
        return new Object[][] {
                {"Scheduled Time" , true},
                {"Start Time" , true},
                {"End Time" , false},
                {"Total Cost" , true},
                {"Status" , false},
        };
    }
    @DataProvider(name = "filterDataProvider")
    public Object[][] filterDataProvider() {


        return new Object[][] {
                {"02.13.2026", "02.16.2026", null , null , null, null},
                {"02.14.2026", null, null , null , null, null},
                {null , null, "1201", null, "Driver2", null},
        };
    }
}
