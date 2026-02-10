package com.project.mobile.DTO.Auth;

public class LoginTransfer {


        private boolean success;
        private String role;         // only set if login succeeds
        private String errorMessage; // only set if login fails

        public LoginTransfer(boolean success, String role, String errorMessage) {
            this.success = success;
            this.role = role;
            this.errorMessage = errorMessage;
        }

        // getters
        public boolean isSuccess() { return success; }
        public String getRole() { return role; }
        public String getErrorMessage() { return errorMessage; }

}
