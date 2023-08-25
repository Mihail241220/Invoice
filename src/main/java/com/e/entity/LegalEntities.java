package com.e.entity;

enum LegalEntities {
    IP("ИП ", "ИП "),
    IP_FULL("Индивидуальный предприниматель ", "ИП "),
    OOO("ООО ", "ООО "),
    OOO_FULL("Общество с ограниченной ответственностью ", "ООО "),
    AO("АО ", "АО "),
    AO_FULL("акционерное общество", "АО "),
    ZAO("ЗАО ", "ЗАО "),
    ZAO_FULL("Закрытое акционерное общество ", "ЗАО ");

    private String name;
    private String subName;

    LegalEntities(String name, String subName) {
        this.name = name;
        this.subName = subName;
    }

    static String equalsForEach(String text) {

        for (LegalEntities i : LegalEntities.values()) {
            if (text.contains(i.getName())) {
                return i.getName();
            }
        }
        return null;
    }

    public static String getSubName(String text) {
        for (LegalEntities i : LegalEntities.values()) {
            if (text.contains(i.getName())) {
                return i.getSubName();
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getSubName() {
        return subName;
    }
}
