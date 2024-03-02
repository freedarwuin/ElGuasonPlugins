package com.polyplugins.AutoCombat.util;

public enum PotionType {
    COMBAT("Combat"),
    EAT("Eat"),
    RANGING("Ranging"),
    SUPER_ATTACK("Super Attack"),
    SUPER_DEFENCE("Super Defence"),
    SUPER_STRENGTH("Super Strength"),
    ANTIPOISON("antipoison"),
    PRAYER("Prayer");

    private final String name;

    PotionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

