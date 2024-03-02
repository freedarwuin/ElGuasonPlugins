package com.polyplugins.AutoCombat;


import net.runelite.client.config.*;

@ConfigGroup("AutoCombatConfig")
public interface AutoCombatConfig extends Config {
    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = -100
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }


    @ConfigSection(
            name = "Auto Combat Configuration",
            description = "Configure how to handles game tick delays, 1 game tick equates to roughly 600ms",
            position = 1,
            closedByDefault = false
    )
    String autoCombatConfig = "autoCombatConfig";

    @ConfigItem(
            keyName = "targetName",
            name = "Target names",
            description = "",
            position = -99,
            section = autoCombatConfig
    )
    default String targetNames() {
        return "Chicken,Goblin";
    }

    @ConfigItem(
            keyName = "useCombatPotion",
            name = "Combat potions?",
            description = "Uses regular or super combat potions",
            position = -10,
            section = autoCombatConfig
    )
    default boolean useCombatPotion() {
        return true;
    }

    @Range(
            min = 1,
            max = 99
    )
    @ConfigItem(
            keyName = "useCombatAt",
            name = "Use at",
            description = "Use combat at skill level or super set drops below a certain percentage",
            position = -9,
            section = autoCombatConfig
    )

    default int useCombatPotAt() {
        return 80;
    }

    @ConfigItem(
            keyName = "useRangingPotion",
            name = "Ranging potions?",
            description = "Uses ranging potions",
            position = -8,
            section = autoCombatConfig
    )
    default boolean useRangingPotion() {
        return false;
    }

    @Range(
            min = 1,
            max = 99
    )
    @ConfigItem(
            keyName = "useRangingPotAt",
            name = "Use at",
            description = "What level to use ranging potions at",
            position = -7,
            section = autoCombatConfig
    )

    default int useRangingPotAt() {
        return 80;
    }

    @ConfigItem(
            keyName = "usePrayerPotion",
            name = "Prayer potions?",
            description = "Uses prayer potions",
            position = 4,
            section = autoCombatConfig
    )
    default boolean usePrayerPotion() {
        return true;
    }

    @Range(
            min = 1,
            max = 99
    )
    @ConfigItem(
            keyName = "usePrayerAt",
            name = "Use at",
            description = "What level to use prayer potions at, prayer or super restore",
            position = 5,
            section = autoCombatConfig
    )

    default int usePrayerPotAt() {
        return 20;
    }

    @Range(
            min = 2,
            max = 90
    )
    @ConfigItem(keyName = "eatAt",
            name = "Eat at",
            description = "What HP to eat at",
            position = 6,
            section = autoCombatConfig)
    default int eatAt() {
        return 50;
    }


    @ConfigItem(keyName = "buryBones",
            name = "Bury bones/ashes",
            description = "Will bury ANY bone/ash in your inventory",
            position = 10,
            section = autoCombatConfig)
    default boolean buryBones() {
        return false;
    }

    @ConfigItem(keyName = "useAntiPoison",
            name = "Use anti-poison",
            description = "Use antipoison if poisoned",
            position = 11,
            section = autoCombatConfig)
    default boolean useAntiPoison() {
        return false;
    }


    @ConfigSection(
            name = "Looting Configuration",
            description = "Configure how to handle looting",
            position = 2,
            closedByDefault = false
    )
    String lootingConfig = "lootingConfig";

    @ConfigItem(
            keyName = "lootEnabled",
            name = "Loot?",
            description = "Loots items",
            position = 1,
            section = lootingConfig
    )
    default boolean lootEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "lootNames",
            name = "Loot names",
            description = "",
            position = 3,
            section = lootingConfig
    )
    default String lootNames() {
        return "Feather";
    }

    @ConfigItem(
            keyName = "lootIds",
            name = "Item IDs to loot",
            description = "",
            position = 4,
            section = lootingConfig
    )
    default String lootIds() {
        return "995";
    }

    @ConfigItem(
            keyName = "minLootWealth",
            name = "Loot items with value of",
            description = "Loot items that possess a minimum wealth threshold.",
            position = 5,
            section = lootingConfig
    )
    default int minLootWealth() {
        return 10000;
    }

    @Range(
            min = 1,
            max = 10
    )
    @ConfigItem(
            keyName = "minLootDelay",
            name = "Min Loot delay",
            description = "Loot delay for death Animation. 1 game tick equates to roughly 600ms",
            position = 6,
            section = lootingConfig
    )
    default int minLootDelay() {
        return 2;
    }

    @Range(
            min = 1,
            max = 10
    )
    @ConfigItem(
            keyName = "maxLootDelay",
            name = "Max Loot delay",
            description = "Loot delay for death Animation. 1 game tick equates to roughly 600ms",
            position = 7,
            section = lootingConfig
    )
    default int maxLootDelay() {
        return 4;
    }


    @ConfigSection(
            name = "Slayer Section",
            description = "",
            position = 20
    )
    String slayerSection = "slayerTitle";

    @ConfigItem(
            keyName = "useGuthan",
            name = "Use Guthans?",
            description = "",
            position = 21,
            section = slayerSection
    )
    default boolean useGuthan() {
        return false;
    }


    @Range(
            min = 2,
            max = 90
    )
    @ConfigItem(
            keyName = "hitpointsThreshold",
            name = "HP Threshold in %",
            description = "",
            position = 22,
            section = slayerSection
    )
    default int hitpointsThreshold() {
        return 40;
    }

    @Range(
            min = 2,
            max = 90
    )
    @ConfigItem(
            keyName = "minHitpointsThreshold",
            name = "Min HP% for Default",
            description = "Min HP% for Default Gear",
            position = 23,
            section = slayerSection
    )
    default int minHitpointsThreshold() {
        return 80;
    }

    @ConfigItem(
            keyName = "defaultGear",
            name = "Default Gear to equip",
            description = "",
            position = 24,
            section = slayerSection
    )
    default String defaultGear() {
        return "4151,12954,1127,1079,11865";
    }

    @ConfigItem(
            keyName = "gearPerTick",
            name = "Gear To Equip per tick",
            description = "Gear To Equip per tick",
            position = 25,
            section = slayerSection
    )
    default int gearPerTick() {
        return 2;
    }

    @ConfigItem(keyName = "shutdownOnTaskDone",
            name = "Stop when task done?",
            description = "Teleports away and stops. Untick if you have no task or want to stay",
            position = 34,
            section = slayerSection)
    default boolean shutdownOnTaskDone() {
        return false;
    }

    @ConfigItem(keyName = "breakTab",
            name = "Gloves / Tab on Task Done?",
            description = "Use Karamja Gloves 3 or Break any teleport tablet when task completes",
            position = 45,
            section = slayerSection)
    default boolean breakTab() {
        return false;
    }


    @ConfigItem(
            keyName = "enableDebug",
            name = "Enable Debug?",
            description = "",
            position = 100
    )
    default boolean enableDebug() {
        return false;
    }
}

