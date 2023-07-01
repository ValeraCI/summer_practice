package com.ledivax.util;

public class Convertor {

    public static Integer stringToInteger(String num) {
        try {
            return Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    public static SongFindParameter stringToSongFindParameter(String parameter) {
        try {
            return SongFindParameter.valueOf(parameter);
        } catch (IllegalArgumentException e) {
            return SongFindParameter.BY_TITLE;
        }
    }
}
