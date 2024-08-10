package com.piggyplugins.pcrewards;

public enum PcRewardsConfigOptions {


    ALL("Show"),
    NONE("Hide"),
    ONLY_100("100 Only"),
    GREATER_THAN_100("Dynamic 100");


    private final String abbrev;

    PcRewardsConfigOptions(String abbrev) {
        this.abbrev = abbrev;
    }

    @Override
    public String toString() {
        return abbrev;
    }
}
