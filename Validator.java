package com.studentdbms.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for validating and sanitizing user inputs.
 */
public class Validator {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** Returns true if the email matches a basic pattern. */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    /** Returns true if phone contains only digits, spaces, +, - (7–15 chars). */
    public static boolean isValidPhone(String phone) {
        return phone == null || phone.isEmpty() ||
               phone.matches("^[+\\d][\\d\\s\\-]{6,14}$");
    }

    /** Parses a date string (yyyy-MM-dd) and returns LocalDate, or null. */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return LocalDate.parse(dateStr.trim(), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** Returns true if the string is not null and not blank. */
    public static boolean isNotBlank(String s) {
        return s != null && !s.isBlank();
    }

    /** Returns true if GPA is in range [0.00 – 10.00]. */
    public static boolean isValidGpa(double gpa) {
        return gpa >= 0.0 && gpa <= 10.0;
    }

    /** Returns true if the year is sensible (1990–current+1). */
    public static boolean isValidYear(int year) {
        int currentYear = LocalDate.now().getYear();
        return year >= 1990 && year <= (currentYear + 1);
    }

    /** Trims and capitalizes first letter of a name. */
    public static String formatName(String name) {
        if (name == null || name.isBlank()) return "";
        name = name.trim();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
    }
}
