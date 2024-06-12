/**
 * @author agge3
 * @file AutoLootConfig.java
 * Derived in large part from AutoCombat.
 */

package com.polyplugins.AutoLoot;


import net.runelite.client.config.*;

@ConfigGroup("AutoLootConfig")
public interface AutoLootConfig extends Config {
    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = 0
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigSection(
            name = "Auto Loot Configuration",
            description = "Undefined behavior when ran with other looting interactions",
            position = 1,
            closedByDefault = false
    )
    String autoLootConfig = "autoLootConfig";

    @ConfigItem(
            keyName = "lootNames",
            name = "Loot names",
            description = "Items to loot",
            position = 2,
            section = autoLootConfig
    )
    default String lootNames() {
        return "Coins,Bones";
    }

    @ConfigItem(
            keyName = "waitTicks",
            name = "Wait for...",
            description = "How many ticks?",
            position = 3,
            section = autoLootConfig
    )
    default int waitFor() {
        return 0;
    }

    // @todo Implement picking up self vs. other's.
    //@ConfigItem(
    //        keyName = "myPickupNames",
    //        name = "My pickup names",
    //        description = "Items to pickup (my items)",
    //        position = -10,
    //        section = autoLootConfig
    //)
    //default String myPickupNames() {
    //    return "Bronze arrows,Iron arrows";
    //}

    //@ConfigItem(
    //        keyName = "othersPickupNames",
    //        name = "Other's pickup names",
    //        description = "Items to pickup (other's items)",
    //        position = -20,
    //        section = autoLootConfig
    //)
    //default String othersPickupNames() {
    //    return "Coins,Bones";
    //}

    @ConfigItem(
            keyName = "buryBones",
            name = "Bury bones/ashes",
            description = "Will bury ANY bone/ash in your inventory",
            position = 3,
            section = autoLootConfig
    )
    default boolean buryBones() {
        return false;
    }
}
