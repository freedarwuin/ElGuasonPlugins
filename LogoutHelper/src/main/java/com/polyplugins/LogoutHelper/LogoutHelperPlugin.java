package com.polyplugins.LogoutHelper;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import java.util.Iterator;
import java.util.Optional;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependencies;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(
        name = "<html><font color=\"#ff4d00\">[GS]</font> Logout Helper </html>",
        enabledByDefault = false,
        tags = {"ElGuason"}
)
@PluginDependencies({@PluginDependency(EthanApiPlugin.class), @PluginDependency(PacketUtilsPlugin.class)})
public class LogoutHelperPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(LogoutHelperPlugin.class);
    @Inject
    private Client client;
    @Inject
    private LogoutHelperConfig config;
    private int timeout;
    private int previousLevel = -1 >>> (char)15424 << (880640 >>> 6054);

    public LogoutHelperPlugin() {
    }

    @Provides
    public LogoutHelperConfig getConfig(ConfigManager configManager) {
        return (LogoutHelperConfig)configManager.getConfig(LogoutHelperConfig.class);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (EthanApiPlugin.loggedIn()) {
            this.handleTimeout();
            int level = this.getWildernessLevel();
            if (level > -1 << (char)11456 >>> (49 << 10760)) {
                this.updatePreviousLevel(level);
            }

            Iterator var3 = this.client.getPlayers().iterator();

            while(var3.hasNext()) {
                Player player = (Player)var3.next();
                if (this.shouldLogout(player, level)) {
                    this.performLogout();
                }
            }

        }
    }

    private void handleTimeout() {
        if (this.timeout > 0) {
            --this.timeout;
        }

    }

    private int getWildernessLevel() {
        Widget wildernessLevel = this.client.getWidget(WidgetInfo.PVP_WILDERNESS_LEVEL);
        if (wildernessLevel != null && !wildernessLevel.getText().equals("")) {
            try {
                if (wildernessLevel.getText().contains("<br>")) {
                    String text = wildernessLevel.getText().split("<br>")[0];
                    return Integer.parseInt(text.replaceAll("Level: ", ""));
                }

                return Integer.parseInt(wildernessLevel.getText().replaceAll("Level: ", ""));
            } catch (NumberFormatException var3) {
            }
        }

        return -1 >>> 15936 << (479232 >>> 11366);
    }

    private void updatePreviousLevel(int level) {
        if (this.previousLevel == -1 << 13920 >>> -112 - -7952) {
            this.previousLevel = level;
        }

    }

    private boolean shouldLogout(Player player, int level) {
        int lowRange = this.client.getLocalPlayer().getCombatLevel() - level;
        int highRange = this.client.getLocalPlayer().getCombatLevel() + level;
        return !player.equals(this.client.getLocalPlayer()) && (player.getCombatLevel() >= lowRange && player.getCombatLevel() <= highRange || !this.config.combatrange());
    }

    private void performLogout() {
        log.info("Trying to logout");
        Optional<Widget> widget = Widgets.search().withId(("\udb40\udc21".hashCode() ^ -1977918751) >>> 5186 >>> (4455 << (char)10528)).first();
        if (widget.isPresent()) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, (1676498401 ^ 1097685345) >>> ("114@".hashCode() ^ -1516454 ^ -7151), -1 >>> 7136 << -9544 - -21608, -1 >>> (char)7392 >>> 23160 - (char)7416);
        } else {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 11927560 << Integer.parseInt("7mb", 29) >>> (5508 << (char)11521), -1 << 9760 << 28296 + -22344, -1 << 4608 >>> (3848 << 13762));
        }

    }
}
