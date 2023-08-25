package com.e.entity.form;

public enum Month {
    January("января"),
    February("февраля"),
    March("марта"),
    April("апреля"),
    May("мая"),
    June("июня"),
    July("июля"),
    August("августа"),
    September("сентября"),
    October("октября"),
    November("ноября"),
    December("декабря");
    private String name;

    Month(String name) {
        this.name = name;
    }

    public static String get(int index) {
        Month[] n = Month.values();
        return n[index].name;
    }

}
