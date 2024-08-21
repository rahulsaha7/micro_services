package com.rahul.user_service.Utils;

import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class PatternUtils {

    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public static final String PHONE_PATTERN = "^\\+?[1-9]\\d{1,14}$";

    public static final String PASSWORD_PATTERN =
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$";

    public static final String BASE64_PATTERN =
        "^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$";

    public static final String NAME_PATTERN = "^[A-Za-z ]+$";

    public static boolean isValidEmail(String email) {
        return StringUtils.isNotBlank(email) && email.matches(EMAIL_PATTERN);
    }
}
