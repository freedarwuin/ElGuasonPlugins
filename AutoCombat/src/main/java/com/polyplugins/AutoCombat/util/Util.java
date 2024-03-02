package com.polyplugins.AutoCombat.util;

import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.EquipmentItemWidget;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.query.NPCQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InventoryInteraction;
import com.example.Packets.MovementPackets;
import com.google.inject.Inject;
import com.polyplugins.AutoCombat.AutoCombatConfig;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.game.ItemManager;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
public class Util {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private AutoCombatConfig config;

    @Inject
    ChatMessageManager chatMessageManager;

    /**
     * Finds and returns the nearest npc
     *
     * @param name Name of npc (uses contains) and ignores matching case
     * @return The nearest npc, or null if none are found
     */
    public NPC findNpc(String name) {
        List<String> targetNames = targetNames();
        NPCQuery npcQuery = NPCs.search()
                .alive()
                .walkable()
                .filter(foundNpc -> foundNpc.getName() != null && targetNames.stream().anyMatch(targetName -> foundNpc.getName().toLowerCase().contains(targetName.toLowerCase())))
                .withAction("Attack")
                .filter(n -> !n.isInteracting() || (n.isInteracting() && n.getInteracting() instanceof Player && n.getInteracting().equals(client.getLocalPlayer())));
        return npcQuery.nearestToPlayer().orElse(null);
    }

    public List<String> targetNames() {
        return Arrays.asList(config.targetNames().split(","));
    }

    public boolean inMulti() {
        return client.getVarbitValue(Varbits.MULTICOMBAT_AREA) == 1;
    }


    public NPC getBeingInteracted() {
        Optional<NPC> npcOp = NPCs.search().interactingWithLocal().first();
        if (npcOp.isEmpty()) {
            log.info("getBeingInteracted NULL");
            return null;
        }
        log.info("NPC: " + npcOp.get().getName());
        NPC npc = npcOp.get();
        log.info("LOS: " + client.getLocalPlayer().getWorldArea().hasLineOfSightTo(client, npc.getWorldLocation()));
        return npcOp.orElse(null);
    }


    /**
     * Sends a debug message into the game chat with the specified log level.
     * The message will only be sent if the client is logged in and debugging is enabled.
     *
     * @param message  The message to be sent.
     * @param logLevel The log level of the message.
     */
    public void sendDebugMessageIntoGameChat(String message, LogLevel logLevel) {
        if (client.getGameState() != GameState.LOGGED_IN || !config.enableDebug()) {
            return;
        }

        Map<LogLevel, Color> colorMap = Map.of(
                LogLevel.DEBUG, Color.blue,
                LogLevel.INFO, Color.GREEN,
                LogLevel.WARN, Color.orange,
                LogLevel.ERROR, Color.red
        );
        Color color = colorMap.getOrDefault(logLevel, Color.black);

        String chatMessage = new ChatMessageBuilder()
                .append(color, message)
                .build();

        chatMessageManager.queue(QueuedMessage.builder()
                .type(ChatMessageType.CONSOLE)
                .runeLiteFormattedMessage(chatMessage)
                .build());
    }


    /**
     * Sends a debug message into the game chat with the default log level INFO.
     * The message will only be sent if the client is logged in and debugging is enabled.
     *
     * @param message The message to be sent.
     */
    public void sendDebugMessageIntoGameChat(String message) {
        sendDebugMessageIntoGameChat(message, LogLevel.INFO);
    }


    public void equipDefaultEquipment() {
        List<EquipmentItemWidget> currentEquipment = Optional.ofNullable(Equipment.search().result()).orElse(Collections.emptyList());
        List<Widget> gearToEquip = new ArrayList<>();
        Arrays.stream(Optional.ofNullable(config.defaultGear()).orElse("").split(","))
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    try {
                        return Integer.parseInt(s.trim());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(item -> Inventory.search().withId(item).first())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(gearToEquip::add);

        // remove items that are already equipped to prevent looping
        // check by ID and name as some items like barrows don't have unique IDs
        gearToEquip = gearToEquip.stream()
                .filter(item -> {
                    boolean equippedById = currentEquipment.stream().anyMatch(ce -> ce.getItemId() == item.getId());
                    boolean equippedByName = currentEquipment.stream().anyMatch(ce -> ce.getName().equalsIgnoreCase(item.getName()));
                    boolean notEquipped = !equippedById && !equippedByName;
                    return notEquipped;
                })
                .collect(Collectors.toList());

        int gearPerTick = Math.max(config.gearPerTick(), 1);
        for (int i = 0; i < gearPerTick && !gearToEquip.isEmpty(); i++) {
            Widget gear = gearToEquip.remove(0);
            sendDebugMessageIntoGameChat("Equipping " + gear.getName() + "...");
            InventoryInteraction.useItem(gear, "Equip", "Wield", "Wear");
        }

        gearToEquip.clear();
    }


    public int getNumberOfEquipedGuthanPieces() {
        return Equipment.search()
                .filter(item -> item.getName().toLowerCase().contains("guthan"))
                .result().size();
    }


    public int getHpPercentValue(float ratio, float scale) {
        return Math.round((ratio / scale) * 100f);
    }


    public void equipGuthans() {
        if (getNumberOfEquipedGuthanPieces() == 4) {
            return;
        }

        List<String> equippedItemNames = Equipment.search().result().stream()
                .map(Widget::getName)
                .collect(Collectors.toList());

        // prevent looping if user have multiple Guthan sets in the inventory.
        // this is also covered by the getNumberOfEquipedGuthanPieces() check
        List<Widget> guthanItemsToEquip = Inventory.search()
                .filter(item -> item.getName().toLowerCase().contains("guthan") &&
                        equippedItemNames.stream().noneMatch(
                                equippedName -> equippedName.equalsIgnoreCase(item.getName())))
                .result();

        int gearPerTick = Math.max(config.gearPerTick(), 1);
        for (int i = 0; i < gearPerTick && !guthanItemsToEquip.isEmpty(); i++) {
            Widget guthanPiece = guthanItemsToEquip.remove(0);
            InventoryInteraction.useItem(guthanPiece, "Equip", "Wield", "Wear");
        }
    }


    public boolean hasItem(SuppliesUtil supplies, Supplier<Widget> itemSupplier) {
        return itemSupplier.get() != null;
    }


    public double calculateBoostedSkillLevel(double realSkillLevel) {
        return realSkillLevel * 1.15 + 5;
    }

    public boolean defaultGearNotEquipped() {
        List<Integer> listOfGear = Arrays.stream(config.defaultGear().split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        List<Integer> currentEquipmentIds = Equipment.search()
                .result()
                .stream()
                .map(Widget::getId)
                .collect(Collectors.toList());

        // Check if any gear IDs from default gear are missing in current equipment
        return listOfGear.stream().anyMatch(id -> !currentEquipmentIds.contains(id));
    }

    public boolean defaultGearIsEquipped() {
        return Equipment.search()
                .result()
                .stream()
                .map(Widget::getId)
                .collect(Collectors.toList())
                .containsAll(Arrays.stream(config.defaultGear().split(","))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .collect(Collectors.toList()));
    }


    public boolean isNameMatch(List<String> lootNames, String itemName) {
        return lootNames != null && !lootNames.isEmpty() &&
                lootNames.stream().anyMatch(lootName -> itemName.contains(lootName.toLowerCase()));
    }

    public boolean isIdMatch(List<Integer> lootIds, int itemId) {
        return lootIds != null && !lootIds.isEmpty() &&
                lootIds.contains(itemId);
    }




}
