package e2e.models;

import java.text.Format;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HistorySorter {
    public static List<History> sortHistory(List<History> historyList, HistorySortField field, boolean ascending) {
        List<History> sortedList = new ArrayList<>(historyList);

        Comparator<History> comparator = null;

        switch(field) {
            case START_TIME:
                comparator = Comparator.comparing(History::getStartTime);
                break;
            case END_TIME:
                comparator = Comparator.comparing(History::getEndTime);
                break;
            case COST:
                comparator = Comparator.comparing(History::getCost);
                break;
            case STATUS:
                comparator = Comparator.comparing(History::getStatus);
                break;
            case DRIVER_NAME:
                comparator = Comparator.comparing(History::getDriverName);
                break;
            case CUSTOMER_OWNER:
                comparator = Comparator.comparing(History::getCustomerOwner);
                break;
            case START_LOCATION:
                comparator = Comparator.comparing(History::getStartLocation);
                break;
            case END_LOCATION:
                comparator = Comparator.comparing(History::getEndLocation);
                break;
            case SHECULED_TIME:
                comparator = Comparator.comparing(History::getScheculedTime);
                break;
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        sortedList.sort(comparator);
        return sortedList;
    }
    public static List<History> filterHistoryByRange(List<History> historyList, HistorySortField field, Object fromValue, Object toValue) {
        List<History> filteredList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd.yyyy");
        for (History history : historyList) {
            boolean matches = true;

            switch(field) {
                case START_TIME:
                    LocalDateTime startTime = history.getStartTime();
                    LocalDateTime fromValueDate = fromValue != null ? LocalDate.parse((String) fromValue, formatter).atStartOfDay() : null;
                    if (fromValueDate != null && startTime.isBefore((LocalDateTime) fromValueDate)) {
                        matches = false;
                    }
                    LocalDateTime toValueDate = toValue != null ? LocalDate.parse((String) toValue, formatter).atStartOfDay().plusDays(1) : null;
                    if (toValueDate != null && startTime.isAfter((LocalDateTime) toValueDate)) {
                        matches = false;
                    }
                    break;

                case END_TIME:
                    LocalDateTime endTime = history.getEndTime();
                    LocalDateTime fromValueDateEnd = fromValue != null ? LocalDate.parse((String) fromValue, formatter).atStartOfDay() : null;
                    LocalDateTime toValueDateEnd = toValue != null ? LocalDate.parse((String) toValue, formatter).atStartOfDay() : null;
                    if (fromValueDateEnd != null && endTime.isBefore((LocalDateTime) fromValueDateEnd)) {
                        matches = false;
                    }
                    if (toValueDateEnd != null && endTime.isAfter((LocalDateTime) toValueDateEnd)) {
                        matches = false;
                    }
                    break;

                case COST:
                    Long cost = history.getCost();
                    if (fromValue != null && cost < (Long) fromValue) {
                        matches = false;
                    }
                    if (toValue != null && cost > (Long) toValue) {
                        matches = false;
                    }
                    break;

                case STATUS:
                    String status = history.getStatus();
                    if (fromValue != null && status.compareTo((String) fromValue) < 0) {
                        matches = false;
                    }
                    if (toValue != null && status.compareTo((String) toValue) > 0) {
                        matches = false;
                    }
                    break;

                case DRIVER_NAME:
                    String driverName = history.getDriverName();
                    if (fromValue != null && driverName.equalsIgnoreCase((String) fromValue)) {
                        matches = false;
                    }
                    break;

                case CUSTOMER_OWNER:
                    String customerOwner = history.getCustomerOwner();
                    if (fromValue != null && customerOwner.compareTo((String) fromValue) < 0) {
                        matches = false;
                    }
                    if (toValue != null && customerOwner.compareTo((String) toValue) > 0) {
                        matches = false;
                    }
                    break;

                case START_LOCATION:
                    String startLocation = history.getStartLocation();
                    if (fromValue != null && startLocation.compareTo((String) fromValue) < 0) {
                        matches = false;
                    }
                    if (toValue != null && startLocation.compareTo((String) toValue) > 0) {
                        matches = false;
                    }
                    break;

                case END_LOCATION:
                    String endLocation = history.getEndLocation();
                    if (fromValue != null && endLocation.compareTo((String) fromValue) < 0) {
                        matches = false;
                    }
                    if (toValue != null && endLocation.compareTo((String) toValue) > 0) {
                        matches = false;
                    }
                    break;
            }

            if (matches) {
                filteredList.add(history);
            }
        }

        return filteredList;
    }

    public static HistorySortField getHistoryEnum(String field) {
        switch (field) {
            case "Scheduled Time":
                return HistorySortField.SHECULED_TIME;
            case "Start Time":
                return HistorySortField.START_TIME;
            case "End Time":
                return HistorySortField.END_TIME;
            case "Total Cost":
                return HistorySortField.COST;
            case "Status":
                return HistorySortField.STATUS;
            case "Driver Name":
                return HistorySortField.DRIVER_NAME;
            case "Customer Owner":
                return HistorySortField.CUSTOMER_OWNER;
            case "Start Location":
                return HistorySortField.START_LOCATION;
            case "End Location":
                return HistorySortField.END_LOCATION;
        }
        throw new IllegalArgumentException("Invalid field: " + field);
    }
}
