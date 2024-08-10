package com.piggyplugins.pcrewards;

public enum PcRewardExpOptions {
    ATTACK("Attack"),
    STRENGTH("Strength"),
    DEFENCE("Defence"),
    RANGE("Ranged"),
    MAGIC("Magic"),
    HITPOINTS("Hitpoints"),
    PRAYER("Prayer"),
    HIDE("Hide");

    private final String abbrev;

    PcRewardExpOptions(String abbrev) {
        this.abbrev = abbrev;
    }

    @Override
    public String toString() {
        return abbrev;
    }

}
