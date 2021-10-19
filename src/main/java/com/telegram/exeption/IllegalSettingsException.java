package com.telegram.exeption;

public class IllegalSettingsException extends IllegalArgumentException {

    public IllegalSettingsException(String s) {
        super(s);
    }
}