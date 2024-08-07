package com.polyplugins.LoginModified;

import com.example.EthanApiPlugin.Collections.Widgets;
import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.event.KeyEvent;
import java.util.Optional;

@Singleton
@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[GS]</font> Auto Login + Pin</html>",
        enabledByDefault = false,
        description = "Auto Login, el auto login funciona cuando haces logout, q se desconecta a las 6 horas",
        tags = {"ElGuason"}
)
public class AutoLoginPlugin extends Plugin {
    public int timeout = 0;
    public int idleTicks = 0;
    public boolean started = false;
    @Inject
    AutoLoginConfig config;
    @Inject
    Client client;
    @Inject
    private KeyManager keyManager;
    public String status = "ACTIVE...";

    public AutoLoginPlugin() {
    }

    @Provides
    public AutoLoginConfig getConfig(ConfigManager configManager) {
        return (AutoLoginConfig)configManager.getConfig(AutoLoginConfig.class);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (this.config.useAutoLogin()) {
            if (this.client.getGameState() == GameState.LOGIN_SCREEN) {
                this.client.setUsername(this.config.email());
                this.client.setPassword(this.config.password());
                this.client.setGameState(GameState.LOGGING_IN);
            }
        }
    }

    @Subscribe
    private void onGameTick(GameTick gameTick) {
        Optional<Widget> widget = Widgets.search().withId(13959169).first();
        Optional<Widget> widget2 = Widgets.search().withId(13959178).first();
        String number = this.config.bankPin();
        char[] digits = number.toCharArray();
        if (this.config.autoBankPin()) {
            char fourthDigit;
            int fourthDigitInt;
            if (widget.isPresent() && widget2.isPresent() && ((Widget)widget2.get()).getText().contains("First click the FIRST digit.")) {
                fourthDigit = digits[0];
                fourthDigitInt = Character.getNumericValue(fourthDigit);
                if (fourthDigitInt == 0) {
                    this.sendKey(48);
                } else if (fourthDigitInt == 1) {
                    this.sendKey(49);
                } else if (fourthDigitInt == 2) {
                    this.sendKey(50);
                } else if (fourthDigitInt == 3) {
                    this.sendKey(51);
                } else if (fourthDigitInt == 4) {
                    this.sendKey(52);
                } else if (fourthDigitInt == 5) {
                    this.sendKey(53);
                } else if (fourthDigitInt == 6) {
                    this.sendKey(54);
                } else if (fourthDigitInt == 7) {
                    this.sendKey(55);
                } else if (fourthDigitInt == 8) {
                    this.sendKey(56);
                } else if (fourthDigitInt == 9) {
                    this.sendKey(57);
                }
            } else if (widget.isPresent() && widget2.isPresent() && ((Widget)widget2.get()).getText().contains("Now click the SECOND digit.")) {
                fourthDigit = digits[1];
                fourthDigitInt = Character.getNumericValue(fourthDigit);
                if (fourthDigitInt == 0) {
                    this.sendKey(48);
                } else if (fourthDigitInt == 1) {
                    this.sendKey(49);
                } else if (fourthDigitInt == 2) {
                    this.sendKey(50);
                } else if (fourthDigitInt == 3) {
                    this.sendKey(51);
                } else if (fourthDigitInt == 4) {
                    this.sendKey(52);
                } else if (fourthDigitInt == 5) {
                    this.sendKey(53);
                } else if (fourthDigitInt == 6) {
                    this.sendKey(54);
                } else if (fourthDigitInt == 7) {
                    this.sendKey(55);
                } else if (fourthDigitInt == 8) {
                    this.sendKey(56);
                } else if (fourthDigitInt == 9) {
                    this.sendKey(57);
                }
            } else if (widget.isPresent() && widget2.isPresent() && ((Widget)widget2.get()).getText().contains("Time for the THIRD digit.")) {
                fourthDigit = digits[2];
                fourthDigitInt = Character.getNumericValue(fourthDigit);
                if (fourthDigitInt == 0) {
                    this.sendKey(48);
                } else if (fourthDigitInt == 1) {
                    this.sendKey(49);
                } else if (fourthDigitInt == 2) {
                    this.sendKey(50);
                } else if (fourthDigitInt == 3) {
                    this.sendKey(51);
                } else if (fourthDigitInt == 4) {
                    this.sendKey(52);
                } else if (fourthDigitInt == 5) {
                    this.sendKey(53);
                } else if (fourthDigitInt == 6) {
                    this.sendKey(54);
                } else if (fourthDigitInt == 7) {
                    this.sendKey(55);
                } else if (fourthDigitInt == 8) {
                    this.sendKey(56);
                } else if (fourthDigitInt == 9) {
                    this.sendKey(57);
                }
            } else if (widget.isPresent() && widget2.isPresent() && ((Widget)widget2.get()).getText().contains("Finally, the FOURTH digit.")) {
                fourthDigit = digits[3];
                fourthDigitInt = Character.getNumericValue(fourthDigit);
                if (fourthDigitInt == 0) {
                    this.sendKey(48);
                } else if (fourthDigitInt == 1) {
                    this.sendKey(49);
                } else if (fourthDigitInt == 2) {
                    this.sendKey(50);
                } else if (fourthDigitInt == 3) {
                    this.sendKey(51);
                } else if (fourthDigitInt == 4) {
                    this.sendKey(52);
                } else if (fourthDigitInt == 5) {
                    this.sendKey(53);
                } else if (fourthDigitInt == 6) {
                    this.sendKey(54);
                } else if (fourthDigitInt == 7) {
                    this.sendKey(55);
                } else if (fourthDigitInt == 8) {
                    this.sendKey(56);
                } else if (fourthDigitInt == 9) {
                    this.sendKey(57);
                }
            }
        }

    }

    private void sendKey(int charCode) {
        KeyEvent kvPressed = new KeyEvent(this.client.getCanvas(), 401, System.currentTimeMillis(), 0, charCode, '\uffff');
        KeyEvent kvTyped = new KeyEvent(this.client.getCanvas(), 400, System.currentTimeMillis(), 0, 0, (char)charCode);
        KeyEvent kvReleased = new KeyEvent(this.client.getCanvas(), 402, System.currentTimeMillis(), 0, charCode, '\uffff');
        this.client.getCanvas().dispatchEvent(kvPressed);
        this.client.getCanvas().dispatchEvent(kvTyped);
        this.client.getCanvas().dispatchEvent(kvReleased);
    }
}

