package com.project.mobile.config;

public class HistoryConfig {
    public static final int[] PAGE_SIZE_OPTIONS = {5, 10, 20, 50};
    public static final int DEFAULT_PAGE_SIZE = 10;

    public enum SortField {
        SCHEDULED_TIME("scheduledTime", "Scheduled Time"),
        START_TIME("startTime", "Start Time"),
        END_TIME("endTime", "End Time"),
        TOTAL_COST("totalCost", "Total Cost"),
        STATUS("status", "Status");

        private final String apiValue;
        private final String displayName;

        SortField(String apiValue, String displayName) {
            this.apiValue = apiValue;
            this.displayName = displayName;
        }

        public String getApiValue() {
            return apiValue;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum SortDirection {
        ASC("asc", "Ascending ↑"),
        DESC("desc", "Descending ↓");

        private final String apiValue;
        private final String displayName;

        SortDirection(String apiValue, String displayName) {
            this.apiValue = apiValue;
            this.displayName = displayName;
        }

        public String getApiValue() {
            return apiValue;
        }

        public String getDisplayName() {
            return displayName;
        }

        public SortDirection toggle() {
            return this == ASC ? DESC : ASC;
        }
    }
}