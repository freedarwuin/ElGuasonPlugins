package com.polyplugins.LogoutHelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("LogoutHelper")
public interface LogoutHelperConfig extends Config {
    /*@ConfigSection(
            name = "Lee esto",
            description = "Importante.",
            position = -56
    )*/
    String instructionsConfig2 = "instructionsConfig2";

    @ConfigItem(
            keyName = "instructions5",
            name = "",
            description = "Instructions.",
            position = -56,
            section = "instructionsConfig2"
    )
    default String instructions5() {
        return "Lee esto. \n\nEres un idiota si pagaste esto por algun grupo de whatsapp o facebook por este plugin. \n\nEsto esta editado por ElKondo user de discord factord.crypto\n\nCanal de Youtube https://www.youtube.com/@ElKondo  \n\nCanal de Discord https://discord.com/invite/URXjtjambp";
    }

    @ConfigItem(
            keyName = "combatrange",
            name = "Cualquiera que pueda atacarte",
            description = "Cuando esté marcado, solo te teletransportarás."
    )
    default boolean combatrange() {
        return false;
    }
}
