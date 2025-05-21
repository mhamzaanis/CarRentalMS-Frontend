package com.Buildex.user;

import javax.swing.text.DefaultFormatter;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateLabelFormatter extends DefaultFormatter {
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Object stringToValue(String text) throws ParseException {
        try {
            return LocalDate.parse(text, dateFormatter);
        } catch (Exception e) {
            throw new ParseException("Invalid date format", 0);
        }
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value == null) {
            return "";
        }
        if (value instanceof LocalDate) {
            return dateFormatter.format((LocalDate) value);
        }
        throw new ParseException("Unsupported value type", 0);
    }
}