package com.piggyplugins.ItemCombiner;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;

@ConfigGroup("ItemCombiner")
public interface ItemCombinerConfig extends Config {
    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = 1
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigSection(
            name = "Configuration",
            description = "Config for item combiner",
            position = 0
    )
    String configuration = "Configuration";

    @ConfigItem(
            keyName = "itemOneName",
            name = "Item One (Tool/Vial)",
            description = "Name of the first item",
            position = 2,
            section = configuration
    )
    default String itemOneName() {
        return "";
    }

    @ConfigItem(
            keyName = "itemOneAmt",
            name = "Item One Amount",
            description = "Amount of the first item",
            position = 3,
            section = configuration
    )
    default int itemOneAmt() {
        return 14;
    }

    @ConfigItem(
            keyName = "itemTwoName",
            name = "Item Two (Herb/Second/Gem/Etc.)",
            description = "Name of the second item",
            position = 4,
            section = configuration
    )
    default String itemTwoName() {
        return "";
    }

    @ConfigItem(
            keyName = "itemTwoAmt",
            name = "Item Two Amount",
            description = "Amount of the second item",
            position = 4,
            section = configuration
    )
    default int itemTwoAmt() {
        return 14;
    }

    @ConfigItem(
            keyName = "amuletOfChemistry",
            name = "Use amulet of chemistry",
            description = "Tick if you want to use amulet of chemistry",
            position = 5,
            section = configuration
    )
    default boolean amuletOfChemistry() {return false;
    }
}
