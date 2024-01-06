package com.ozplugins.AutoMLM;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.BankInventoryInteraction;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.ozplugins.AutoMLM.util.Utils;
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.time.Instant;
import java.util.Optional;

import static com.ozplugins.AutoMLM.AutoMLMState.*;

@PluginDependency(EthanApiPlugin.class)
@PluginDependency(PacketUtilsPlugin.class)
@PluginDescriptor(
        name = "<html><font color=\"#0394FC\">[OZ]</font> Auto MLM</font></html>",
        enabledByDefault = false,
        description = "Mines shit in the motherlode mine for you..",
        tags = {"oz", "piggy"}
)
@Slf4j
public class AutoMLMPlugin extends Plugin {
    Instant botTimer;
    boolean enablePlugin;
    @Inject
    private ReflectBreakHandler breakHandler;
    @Inject
    Client client;
    @Inject
    AutoMLMConfiguration config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoMLMOverlay overlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ClientThread clientThread;
    @Inject
    public Utils util;
    AutoMLMState state;
    int timeout = 0;
    int pouches = 0;
    int idleCount = 0;
    boolean depositOres = false;
    UISettings uiSetting;
    WorldPoint MinePoint;


    @Provides
    AutoMLMConfiguration provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoMLMConfiguration.class);
    }

    @Override
    protected void startUp() {
        breakHandler.registerPlugin(this);
        timeout = 0;
        pouches = 0;
        idleCount = 0;
        enablePlugin = false;
        depositOres = false;
        botTimer = Instant.now();
        state = null;
        uiSetting = config.UISettings();
        keyManager.registerKeyListener(pluginToggle);
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() {
        keyManager.unregisterKeyListener(pluginToggle);
        resetVals();
    }

    private void resetVals() {
        state = TIMEOUT;
        timeout = 10;
        pouches = 0;
        idleCount = 0;
        enablePlugin = false;
        depositOres = false;
        breakHandler.unregisterPlugin(this);
    }

    public AutoMLMState getState() {
        Player player = client.getLocalPlayer();

        if (player == null) {
            return UNHANDLED_STATE;
        }
        if (breakHandler.shouldBreak(this)) {
            return HANDLE_BREAK;
        }
        if (timeout > 0) {
            return TIMEOUT;
        }
        if (EthanApiPlugin.isMoving()) {
            return MOVING;
        }
        if (isBankPinOpen()) {
            overlay.infoStatus = "Bank Pin";
            return BANK_PIN;
        }
        if (client.getLocalPlayer().getAnimation() == 6752) {
            overlay.infoStatus = "Mining";
            return ANIMATING;
        }
        if (idleCount < 3) {
            return IDLE;
        }
        if (needsToDepositOres()) {
            depositOres = true;
        }
        if (depositOres) {
            return DEPOSIT_BANK;
        }
        if (!Inventory.full()) {
            return MINE;
        }
        if (Inventory.full()) {
            return DEPOSIT_HOPPER;
        }
        return UNHANDLED_STATE;
    }

    @Subscribe
    private void onGameTick(GameTick tick) {
        if (!enablePlugin || breakHandler.isBreakActive(this)) {
            return;
        }
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        uiSetting = config.UISettings();
        idleCount = (client.getLocalPlayer().getAnimation() == -1) ? idleCount + 1 : 0;
        MinePoint = config.MineArea().getMinePoint();

        state = getState();
        switch (state) {
            case TIMEOUT:
                timeout--;
                break;

            case FIND_BANK:
                openNearestBank();
                break;

            case MINE:
                handleGem();
                handleMineOre();
                break;

            case DEPOSIT_HOPPER:
                handleHopper();
                break;

            case DEPOSIT_BANK:
                handleDepositOres();
                break;

            case HANDLE_BREAK:
                breakHandler.startBreak(this);
                timeout = 10;
                break;

            case UNHANDLED_STATE:
                overlay.infoStatus = "Ded";
                break;

            case MOVING:
            case ANIMATING:
            case BANK_PIN:
            case IDLE:
                timeout = util.tickDelay();
                break;
        }
    }

    public void handleDepositOres() {
        if (isOnUpperFloor()) {
            handleLadder();
            return;
        }
        if (isBankOpen()) {
            overlay.infoStatus = "Banking ores";
            //TODO simplify this with a list of item ids and add deposit box support
            BankInventory.search().result().stream()
                    .filter(x -> x.getItemId() != ItemID.HAMMER && !x.getName().contains("pickaxe") && x.getItemId() != ItemID.PAYDIRT)
                    .forEach(x -> BankInventoryInteraction.useItem(x, "Deposit-All"));

            if (client.getVarbitValue(Varbits.SACK_NUMBER) > 0) {
                handleSack();
            } else {
                depositOres = false;
                return;
            }
        }

        if (Inventory.getItemAmount(12011) > 1) {
            handleHopper();
            return;
        }
        if (Inventory.search().filter(x -> x.getName().contains("ore") || x.getName().contains("Coal")).first().isPresent()) {
            openNearestBank();
            return;
        }
        if (!isBankOpen() && Inventory.search().filter(x -> x.getName().contains("ore") || x.getName().contains("Coal")).first().isEmpty()
                && client.getVarbitValue(Varbits.SACK_NUMBER) > 0) {
            handleSack();
        }
    }

    public void handleSack() {
        Optional<TileObject> sack = TileObjects.search().withName("Sack").withAction("Search").nearestToPlayer();
        if (sack.isPresent() && !Inventory.full()) {
            overlay.infoStatus = "Searching sack";
            TileObjectInteraction.interact(sack.get(), "Search");
        }
    }

    public void handleGem() {
        overlay.infoStatus = "Dropping gem";
        Inventory.search().filter(gem -> gem.getName().contains("Uncut")).first().ifPresent(x -> InventoryInteraction.useItem(x, "Drop"));
    }

    public void handleMineOre() {
        overlay.infoStatus = "Mine ore";
        if (config.useSpec() && hasSpec()) {
            useSpec();
        }
        if (((config.MineArea() == MineArea.UPPER_1 || config.MineArea() == MineArea.UPPER_2) && !isOnUpperFloor())
                || ((config.MineArea() == MineArea.LOWER_1 || config.MineArea() == MineArea.LOWER_2) && isOnUpperFloor())) {
            handleLadder();
            return;
        }
        TileObjects.search().withName("Ore vein").withAction("Mine").nearestToPoint(MinePoint).ifPresent(x -> TileObjectInteraction.interact(x, "Mine"));
    }

    private void useSpec() { //thanks polyj
        if (!Equipment.search().matchesWildCardNoCase("*Dragon pickaxe*").empty() || !Equipment.search().matchesWildCardNoCase("*infernal pickaxe*").empty()) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 38862884, -1, -1);
        }
    }

    private boolean hasSpec() {
        return client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) == 1000;
    }

    public void handleHopper() {
        if (isOnUpperFloor()) {
            handleLadder();
            return;
        }
        Optional<TileObject> brokenWheel = TileObjects.search().withAction("Hammer").nearestToPlayer();
        if (brokenWheel.isPresent() && config.fixWheels() && Inventory.search().withId(ItemID.HAMMER).first().isPresent()) {
            overlay.infoStatus = "Fixing wheel";
            TileObjectInteraction.interact(brokenWheel.get(), "Hammer");
            return;
        }
        overlay.infoStatus = "Deposit hopper";
        TileObjects.search().withName("Hopper").withAction("Deposit").nearestToPlayer().ifPresent(x -> TileObjectInteraction.interact(x, "Deposit"));
    }

    public void handleLadder() {
        overlay.infoStatus = "Climb ladder";
        TileObjects.search().withName("Ladder").withAction("Climb").nearestToPlayer().ifPresent(x -> TileObjectInteraction.interact(x, "Climb"));
    }

    public boolean needsToDepositOres() {
        return config.Sack().getSize() - (client.getVarbitValue(Varbits.SACK_NUMBER) + Inventory.getItemAmount(12011)) <= 0
                || Inventory.search().filter(x -> x.getName().contains("ore") || x.getName().contains("Coal")).first().isPresent();
    }

    public boolean isOnUpperFloor() {
        return (client.getVarbitValue(2086) == 1);
    }

    private final HotkeyListener pluginToggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            togglePlugin();
        }
    };

    public void togglePlugin() {
        enablePlugin = !enablePlugin;
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        if (!enablePlugin) {
            clientThread.invokeLater(() -> {
                EthanApiPlugin.sendClientMessage("Auto MLM disabled.");
            });
            breakHandler.stopPlugin(this);
            resetVals();
        } else {
            clientThread.invokeLater(() -> {
                EthanApiPlugin.sendClientMessage("Auto MLM enabled.");
            });
            botTimer = Instant.now();
            uiSetting = config.UISettings();
            keyManager.registerKeyListener(pluginToggle);
            breakHandler.registerPlugin(this);
            breakHandler.startPlugin(this);
        }
    }

    public boolean isBankOpen() {
        return (client.getWidget(WidgetInfo.BANK_CONTAINER) != null);
    }

    public boolean isBankPinOpen() {
        return (client.getWidget(WidgetInfo.BANK_PIN_CONTAINER) != null);
    }

    public void openNearestBank() {
        if (!isBankOpen()) {
            TileObjects.search().withName("Bank chest").nearestToPlayer().ifPresentOrElse(x -> {
                overlay.infoStatus = "Banking";
                TileObjectInteraction.interact(x, "Use");
            }, () -> {
                overlay.infoStatus = "Bank not found";
            });
        }
        timeout = util.tickDelay();
    }
}
