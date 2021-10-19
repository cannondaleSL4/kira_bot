package com.telegram.commands;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Users settings
 */
@Getter
@EqualsAndHashCode
public class Settings {

    private int min;

    private int max;

    private int listCount;

    @EqualsAndHashCode.Exclude
    private int plusMinusUniqueTaskCount;

    @EqualsAndHashCode.Exclude
    private int multiplicationUniqueTaskCount;

    @EqualsAndHashCode.Exclude
    private int divisionUniqueTaskCount;

    public Settings(int min, int max, int listCount) {
        this.min = SettingsAssistant.calculateMin(min, max);
        this.max = SettingsAssistant.calculateMax(min, max);
        this.listCount = SettingsAssistant.calculateListCount(listCount);
        this.plusMinusUniqueTaskCount = SettingsAssistant.calculatePlusMinusUniqueTaskCount(this.min, this.max);
        this.multiplicationUniqueTaskCount = SettingsAssistant.calculateMultiplicationUniqueTaskCount(this.min, this.max);
        this.divisionUniqueTaskCount = SettingsAssistant.calculateDivisionUniqueTaskCount(this.min, this.max);
    }
}