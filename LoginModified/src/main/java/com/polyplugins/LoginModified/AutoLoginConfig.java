package com.polyplugins.LoginModified;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("AutoLogin + Pin")
public interface AutoLoginConfig extends Config {
    @ConfigSection(
            name = "Lee esto",
            description = "Importante.",
            position = -56
    )
    String instructionsConfig2 = "instructionsConfig2";
    @ConfigSection(
            name = "Config AutoLogin",
            description = "",
            position = 1,
            closedByDefault = true
    )
    String settingLogin = "settingLogin";
    @ConfigSection(
            name = "Config BankPin",
            description = "",
            position = 5,
            closedByDefault = true
    )
    String settingBankPin = "settingBankPin";

    @ConfigItem(
            keyName = "instructions5",
            name = "",
            description = "Instructions.",
            position = -56,
            section = "instructionsConfig2"
    )
    default String instructions5() {
        return "Importante";
    }

    @ConfigItem(
            keyName = "useAutoLogin",
            name = "Re Login",
            description = "",
            position = 2,
            section = "settingLogin"
    )
    default boolean useAutoLogin() {
        return false;
    }

    @ConfigItem(
            keyName = "email",
            name = "E-mail",
            description = "email",
            secret = true,
            position = 3,
            section = "settingLogin"
    )
    default String email() {
        return "";
    }

    @ConfigItem(
            keyName = "password",
            name = "Password",
            description = "password",
            position = 4,
            secret = true,
            section = "settingLogin"
    )
    default String password() {
        return "";
    }

    @ConfigItem(
            keyName = "useBankPin",
            name = "Auto Bank Pin",
            description = "",
            position = 6,
            section = "settingBankPin"
    )
    default boolean autoBankPin() {
        return false;
    }

    @ConfigItem(
            keyName = "bankPin",
            name = "Bank Pin",
            description = "bank pin",
            position = 7,
            secret = true,
            section = "settingBankPin"
    )
    default String bankPin() {
        return "";
    }
}

