package com.telegram.commands;

import com.telegram.exeption.IllegalSettingsException;

/**
 * Validation
 */
class SettingsAssistant {

    static int calculateMin(int min, int max) {
        if (min == max) {
            throw new IllegalSettingsException("\uD83D\uDCA9 Совпадение минимального и максимального значений " +
                    "недопустимо");
        }
        if (min < max) {
            return min;
        }
        return max;
    }

    static int calculateMax(int min, int max) {
        if (min < max) {
            return max;
        }
        return min;
    }

    static int calculateListCount(int listCount) {
        if (listCount > 10) {
            return 10;
        }
        return listCount;
    }

    static int calculatePlusMinusUniqueTaskCount(int min, int max) {
        if (max - 2 * min + 1 >= 0) {
            return ((max - 2 * min + 2) * (max - 2 * min + 1)) / 2;
        }
        return 0;
    }

    static int calculateMultiplicationUniqueTaskCount(int min, int max) {
        if (max < 10) {
            return (((max - min + 1) * 10) * 2 - (max - min + 1)) / 2;
        } else {
            return (((max - min + 1) * max) * 2 - (max - min + 1)) / 2;
        }
    }

    static int calculateDivisionUniqueTaskCount(int min, int max) {
        if (max < 10) {
            return (((max - min + 1) * 10) - (max - min + 1)) / 2;
        } else {
            return (((max - min + 1) * max) - (max - min + 1)) / 2;
        }
    }
}