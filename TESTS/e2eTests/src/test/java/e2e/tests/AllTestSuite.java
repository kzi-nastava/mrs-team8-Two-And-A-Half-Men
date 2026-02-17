package e2e.tests;

import org.junit.platform.suite.api.*;

@Suite
@SelectClasses({ BookingFromFavouritesTest.class, RideRatingTest.class })
public class AllTestSuite {
}
