package dto.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public enum ApiResponseCodes {


    LOGIN_SUCCESS(2000, "Login Successful", "User logged in successfully.", ResponseCodeType.SUCCESS),
    LOGIN_FAILED_INVALID_CREDENTIALS(2001, "Login Failed", "Invalid username or password.", ResponseCodeType.ERROR),
    SIGNUP_SUCCESS(2100, "Signup Successful", "User signed up successfully.", ResponseCodeType.SUCCESS),
    SIGNUP_FAILED_DUPLICATE_EMAIL(2101, "Signup Failed", "User with this email already exists.", ResponseCodeType.ERROR),
    SIGNUP_FAILED_DUPLICATE_USERNAME(2103, "Signup Failed", "User with this username already exists.", ResponseCodeType.ERROR),
    SIGNUP_FAILED_INVALID_PARAMETER(2102, "Signup Failed", "Invalid input for field : ", ResponseCodeType.ERROR),
    UPDATE_SUCCESS(2200, "Update Successful", "User information updated successfully.", ResponseCodeType.SUCCESS),
    UPDATE_FAILED_NOT_FOUND(2201, "Update Failed", "User not found.", ResponseCodeType.ERROR),
    INVALID_PARAMS(2108, "Signup Failed", "Invalid input for field : ", ResponseCodeType.ERROR),
    UPDATE_FAILED_VALIDATION_ERROR(2202, "Update Failed", "Validation error occurred during update.", ResponseCodeType.ERROR),
    UNAUTHORIZED_ACCESS(4010, "Unauthorized Access", "You are not authorized to access this resource. Reason : ", ResponseCodeType.ERROR),
    UPDATE_FAILED_INVALID_PARAMETER(2104, "Signup Failed", "Invalid input for field : ", ResponseCodeType.ERROR),
    SUCCESS(2105,"Data fetched Successfully", "Success.", ResponseCodeType.SUCCESS),
    COIN_VIEW_INVALID_PARAMS(2106, "coin view failed", "Invalid input for field : ", ResponseCodeType.ERROR),
    ACCOUNT_BLOCKED(2107, "Account Blocked", "Account Blocked Due to multiple invalid attempts ", ResponseCodeType.ERROR),
    LOGOUT_SUCCESS(2010, "Logout Successful", "User logged out successfully.", ResponseCodeType.SUCCESS),
    SERVER_ERROR(2203, "Internal Server Error", "System Error! We are experiencing technical difficulties and are working to fix them. Please try again later.", ResponseCodeType.ERROR),
    USER_NOT_FOUND(2011, "User not found","No user found", ResponseCodeType.ERROR),
    LOGIN_FAILED_ACCOUNT_BLOCKED(2012,"Account Blocked", "Your account is blocked, please try again after 24 hour", ResponseCodeType.ERROR),
    DUPLICATE_EMAIL(2013,"Duplicate email", "User already exists with this email, please use different email", ResponseCodeType.ERROR),
    DUPLICATE_MOBILE_NUMBER(2014,"Duplicate mobile number", "User already exists with this mobile number, please use different mobile number", ResponseCodeType.ERROR);

    private final int code;
    private final String title;
    private final String message;
    private final ResponseCodeType responseCodeType;

    public static ApiResponseCodes getByCode(int code) {
        for (ApiResponseCodes responseCode : ApiResponseCodes.values()) {
            if (responseCode.getCode() == code) {
                return responseCode;
            }
        }
        return ApiResponseCodes.SERVER_ERROR;
    }


}
