package com.saadahmedev.accountservice.util;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;

public class RequestResolver {

    @Nonnull
    public static String getHeader(HttpServletRequest httpServletRequest, HeaderType headerType) {
        switch (headerType) {
            case ID -> {
                return httpServletRequest.getHeader("X-USER-ID");
            }
            case ROLE -> {
                return httpServletRequest.getHeader("X-USER-ROLE");
            }
            case EMAIL -> {
                return httpServletRequest.getHeader("X-USER-EMAIL");
            }
            case USERNAME -> {
                return httpServletRequest.getHeader("X-USER-USERNAME");
            }
            case PHONE -> {
                return httpServletRequest.getHeader("X-USER-PHONE");
            }
            case FIRST_NAME -> {
                return httpServletRequest.getHeader("X-USER-FIRST_NAME");
            }
            case LAST_NAME -> {
                return httpServletRequest.getHeader("X-USER-LAST_NAME");
            }
            case FULL_NAME -> {
                return httpServletRequest.getHeader("X-USER-FULL_NAME");
            }
            case DATE_OF_BIRTH -> {
                return httpServletRequest.getHeader("X-USER-DATE_OF_BIRTH");
            }
            case SECRET_KEY -> {
                return httpServletRequest.getHeader("X-ADMIN-SECRET") == null ? httpServletRequest.getHeader("X-EMPLOYEE-SECRET") : httpServletRequest.getHeader("X-ADMIN-SECRET");
            }
        }

        return "";
    }
}
