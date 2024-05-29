package com.piggyplugins.ItemCombiner;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.BankInteraction;
import com.example.InteractionApi.NPCInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.util.Optional;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Item Combiner</html>",
        description = "Automatically banks & combines items for you",
        enabledByDefault = false,
        tags = {"ethan", "piggy"}
)
@Slf4j
public class ItemCombinerPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ItemCombinerConfig config;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ItemCombinerOverlay overlay;
    @Inject
    private ReflectBreakHandler breakHandler;
    @Getter
    private boolean started;
    private int afkTicks;
    private boolean isMaking;
    private boolean consumedAmulet;

    @Provides
    private ItemCombinerConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(ItemCombinerConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        isMaking = false;
        keyManager.registerKeyListener(toggle);
        overlayManager.add(overlay);
        breakHandler.registerPlugin(this);
    }

    @Override
    protected void shutDown() throws Exception {
        isMaking = false;
        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);
        breakHandler.unregisterPlugin(this);
    }

    @Subscribe
    private void onChatMessage(ChatMessage event) {
        if (event == null || event.getMessage() == null || event.getType() != ChatMessageType.GAMEMESSAGE) {
            return;
        }

        if (event.getMessage().contains("It then crumbles to dust") && config.amuletOfChemistry()) {
            consumedAmulet = true;
            isMaking = false;
        }
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN
                || !started
                || EthanApiPlugin.isMoving()
                || breakHandler.isBreakActive(this)) {
            return;
        }

        if (!hasItems(Bank.isOpen())) {
            isMaking = false;
        }

        if (isMaking) {
            handleTimeout();
            return;
        }

        if (breakHandler.shouldBreak(this)) {
            breakHandler.startBreak(this);
            return;
        }

        Widget potionWidget = client.getWidget(17694734);
        if (potionWidget != null && !potionWidget.isHidden()) {
            int itemOneQuantity = Inventory.search().withName(config.itemOneName()).result().size();
            int itemTwoQuantity = Inventory.search().withName(config.itemTwoName()).result().size();
            int quantity = (itemOneQuantity < itemTwoQuantity) ? itemOneQuantity : itemTwoQuantity;
            MousePackets.queueClickPacket();
            WidgetPackets.queueResumePause(17694734, quantity);
            isMaking = true;
            return;
        }

        if (hasItems(Bank.isOpen()) && !consumedAmulet) {
            useItems();
        } else {
            doBanking();
        }

    }

    private void handleTimeout() {
        if (client.getLocalPlayer().getAnimation() == -1) {
            afkTicks++;
        } else {
            afkTicks = 0;
        }
        if (afkTicks >= 2) {
            isMaking = false;
        }
    }

    private void findBank() {
        Optional<TileObject> chest1 = TileObjects.search().withName("Bank chest").nearestToPlayer();
        Optional<TileObject> chest2 = TileObjects.search().withName("Bank Chest").nearestToPlayer();
        Optional<TileObject> chest3 = TileObjects.search().withName("Bank Chest-wreck").nearestToPlayer();
        Optional<NPC> banker = NPCs.search().withAction("Bank").nearestToPlayer();
        Optional<TileObject> booth = TileObjects.search().withAction("Bank").nearestToPlayer();
        if (chest1.isPresent()){
            TileObjectInteraction.interact(chest1.get(), "Use");
            return;
        }
        if (chest2.isPresent()){
            TileObjectInteraction.interact(chest2.get(), "Use");
            return;
        }
        if (chest3.isPresent()){
            TileObjectInteraction.interact(chest3.get(), "Use");
            return;
        }
        if (booth.isPresent()){
            TileObjectInteraction.interact(booth.get(), "Bank");
            return;
        }
        if (banker.isPresent()){
            NPCInteraction.interact(banker.get(), "Bank");
            return;
        }
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "couldn't find bank or banker", null);
        EthanApiPlugin.stopPlugin(this);
    }

    private boolean hasItems(boolean bank) {
        return bank
                ? !BankInventory.search().withName(config.itemOneName()).empty() && !BankInventory.search().withName(config.itemTwoName()).empty()
                : Inventory.search().withName(config.itemOneName()).first().isPresent() && Inventory.search().withName(config.itemTwoName()).first().isPresent();
    }

    private void doBanking() {
        Widget depositInventory = client.getWidget(WidgetInfo.BANK_DEPOSIT_INVENTORY);

        if (!Bank.isOpen()) {
            findBank();
            return;
        }

        if (depositInventory != null) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(depositInventory, "Deposit inventory");
        }

        if (config.amuletOfChemistry() && !hasAmulet()) {
            consumedAmulet = true;
        }

        if (config.amuletOfChemistry() && consumedAmulet) {
            handleAmulet();
        } else {
            Bank.search().withName(config.itemOneName()).first().ifPresentOrElse(item -> {
                BankInteraction.withdrawX(item, config.itemOneAmt());
            }, () -> {
                EthanApiPlugin.stopPlugin(this);
                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=ff0000>ItemCombiner Plugin:</col> Out of " + config.itemOneName(), null);
            });

            Bank.search().withName(config.itemTwoName()).first().ifPresentOrElse(item -> {
                BankInteraction.withdrawX(item, config.itemTwoAmt());
            }, () -> {
                EthanApiPlugin.stopPlugin(this);
                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=ff0000>ItemCombiner Plugin:</col> Out of " + config.itemTwoName(), null);
            });
        }
    }

    private boolean hasAmulet() {return Equipment.search().withId(ItemID.AMULET_OF_CHEMISTRY).first().isPresent();}

    private void handleAmulet() {
        Optional<Widget> amulet = Bank.search().withId(ItemID.AMULET_OF_CHEMISTRY).first();
        if (amulet.isPresent()) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(amulet.get(), "Withdraw-1");
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(9, WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getPackedId(), ItemID.AMULET_OF_CHEMISTRY, 0);
            consumedAmulet = false;
        } else {
            EthanApiPlugin.stopPlugin(this);
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=ff0000>ItemCombiner Plugin:</col> Out of amulets", null);
        }
    }

    private void useItems() {
        Widget itemOne = Inventory.search().filter(item -> item.getName().contains(config.itemOneName())).first().get();
        Widget itemTwo = Inventory.search().filter(item -> item.getName().contains(config.itemTwoName())).first().get();

        MousePackets.queueClickPacket();
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetOnWidget(itemOne, itemTwo);
    }

    private final HotkeyListener toggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            toggle();
        }
    };

    public void toggle() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        started = !started;

        if (started) {
            isMaking = false;
            breakHandler.startPlugin(this);
        } else {
            breakHandler.stopPlugin(this);
        }
    }
}
