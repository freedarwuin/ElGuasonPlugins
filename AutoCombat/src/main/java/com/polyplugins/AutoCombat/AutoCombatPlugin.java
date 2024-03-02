package com.polyplugins.AutoCombat;


import com.example.EthanApiPlugin.Collections.ETileItem;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileItems;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InventoryInteraction;
import com.example.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.API.PlayerUtil;
import com.polyplugins.AutoCombat.helper.LootHelper;
import com.polyplugins.AutoCombat.helper.SlayerHelper;
import com.polyplugins.AutoCombat.util.LogLevel;
import com.polyplugins.AutoCombat.util.PotionType;
import com.polyplugins.AutoCombat.util.SuppliesUtil;
import com.polyplugins.AutoCombat.util.Util;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@PluginDescriptor(
        name = "<html><font color=\"#7ecbf2\">[PJ]</font>AutoCombat</html>",
        description = "Kills shit",
        enabledByDefault = false,
        tags = {"poly", "plugin"}
)
@Slf4j
public class AutoCombatPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    public AutoCombatConfig config;
    @Inject
    private AutoCombatOverlay overlay;
    @Inject
    private AutoCombatTileOverlay tileOverlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    public ItemManager itemManager;
    @Inject
    private ClientThread clientThread;
    public boolean started = false;
    public int timeout = 0;

    WorldPoint lootTile = null;

    @Provides
    private AutoCombatConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoCombatConfig.class);
    }

    @Inject
    public SuppliesUtil supplies;
    @Inject
    public Util util;
    @Inject
    public LootHelper lootHelper;
    @Inject
    public PlayerUtil playerUtil;
    @Inject
    public SlayerHelper slayerHelper;
    public Queue<ItemStack> lootQueue = new LinkedList<>();

    private boolean hasFood = false;
    private boolean hasPrayerPot = false;
    private boolean hasCombatPot = false;
    private boolean hasSuperAttackPot = false;
    private boolean hasSuperDefencePot = false;
    private boolean hasSuperStrengthPot = false;
    private boolean hasAntiPoisonPot = false;
    private boolean hasBones = false;
    public boolean isSlayerNpc = false;
    public SlayerNpc slayerInfo = null;
    public int idleTicks = 0;
    public NPC targetNpc = null;
    public Player player = null;
    private boolean looting = false;

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggle);
        timeout = 0;
    }


    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
        removeOverlay();
        resetEverything();
    }


    public void resetEverything() {
        timeout = 0;
        started = false;
        hasBones = false;
        hasCombatPot = false;
        hasPrayerPot = false;
        hasFood = false;
        idleTicks = 0;
        lootQueue.clear();
        targetNpc = null;
        player = null;
        slayerInfo = null;
        isSlayerNpc = false;
    }

    List<ETileItem> eItems = new ArrayList<>();

    @Subscribe
    private void onGameTick(GameTick event) {
        player = client.getLocalPlayer();
        isSlayerNpc = slayerHelper.isSlayerNPC(util.targetNames());

        if (isSlayerNpc) {
            slayerInfo = slayerHelper.getSlayerInfo(util.targetNames());
            playerUtil.getBeingInteracted(util.targetNames()).first().ifPresent(n -> {
                if (n.getHealthRatio() == -1) return;
                if (n.getHealthRatio() <= slayerInfo.getUseHp()) {
                    slayerHelper.useSlayerItem(slayerInfo.getItemName());
                    timeout = 3;
                }
            });
        }

        if (!playerUtil.isInteracting() || player.getAnimation() == -1) idleTicks++;
        else idleTicks = 0;
        if (timeout > 0) {
            timeout--;
            return;
        }
        if (client.getGameState() != GameState.LOGGED_IN || EthanApiPlugin.isMoving() || !started) {
            return;
        }

        if (client.getVarpValue(VarPlayer.POISON) > 0 && config.useAntiPoison()) {
            handleAntiPoison();
        }


        processBoneAction("Bury");

        processBoneAction("Scatter");

//        if (lootQueue.isEmpty()) looting = false;
        if (lootTile == null) looting = false;
        checkRunEnergy();
        setupPotionsAndFoodVariables();


//        if (lootTile != null) {
//            looting = true;
//          eItems = TileItems.search().filter(ti -> ti.getLocation().distanceTo(lootTile) == 0)..result();
//            if (eItems == null) return;
//            for (ETileItem eit : eItems) {
//                ItemComposition comp = itemManager.getItemComposition(eit.getTileItem().getId());
//                if (!lootHelper.getLootNames().contains(comp.getName())) {
//                    log.info("removing " + comp.getName() + " size - " + eItems.size());
//                    eItems.remove(eit);
////                    continue;
//                }
//            }
//            if (eItems.isEmpty()) {
//                log.info("empty loot, resetting");
//                lootTile = null;
//                looting = false;
//                return;
//            }
////            while (!eItems.isEmpty()) {
//            ETileItem eItem = eItems.get(0);
//            ItemComposition comp = itemManager.getItemComposition(eItem.getTileItem().getId());
//            log.info("r0");
//            if (!lootHelper.getLootNames().contains(comp.getName())) {
//                eItems.remove(eItem);
////                return;
////                continue;
//            }
//            log.info("r1");
////                if (EthanApiPlugin.isMoving()) return;
//            if (comp.isStackable() || comp.getNote() != -1) {
//                if (Inventory.full() && Inventory.getItemAmount(eItem.getTileItem().getId()) > 0) {
//                    eItem.interact(false);
//                } else if (!Inventory.full()) {
//                    EthanApiPlugin.sendClientMessage("Looting stackable: " + comp.getName() + " " + client.getTickCount());
//                    eItem.interact(false);
//                }
//            } else {
//                if (!Inventory.full()) {
//                    EthanApiPlugin.sendClientMessage("Looting: " + comp.getName() + " " + client.getTickCount());
//                    eItem.interact(false);
//                }
//            }
//            eItems.remove(eItem);
////            }
//            return;
//        }

        if (!lootQueue.isEmpty()) {
            looting = true;
            ItemStack itemStack = lootQueue.peek();
            WorldPoint stackLocation = WorldPoint.fromLocal(client, itemStack.getLocation());
            TileItems.search().withId(itemStack.getId()).withinDistanceToPoint(1, stackLocation).first().ifPresent(item -> {
                ItemComposition comp = itemManager.getItemComposition(item.getTileItem().getId());
                log.info("Looting: " + comp.getName());
                if (comp.isStackable() || comp.getNote() != -1) {
                    log.info("stackable loot " + comp.getName());
                    if (lootHelper.hasStackableLoot(comp)) {
                        log.info("Has stackable loot");
                        item.interact(false);
                    }
                }
                if (!Inventory.full()) {
                    item.interact(false);
                } else {
                    EthanApiPlugin.sendClientMessage("Inventory full, stopping. May handle in future update");
                    EthanApiPlugin.stopPlugin(this);
                }
            });
            timeout = 3;
            lootQueue.remove();
            return;
        }


        if (config.useGuthan()) {
            int currentHpRatio = util.getHpPercentValue(client.getLocalPlayer().getHealthRatio(), client.getLocalPlayer().getHealthScale());

            if (currentHpRatio <= config.hitpointsThreshold()) {
                util.equipGuthans();
            } else if ((currentHpRatio >= config.minHitpointsThreshold() && util.defaultGearNotEquipped()) ||
                    (currentHpRatio > config.hitpointsThreshold() && currentHpRatio < config.minHitpointsThreshold() &&
                            util.getNumberOfEquipedGuthanPieces() >= 1 && util.getNumberOfEquipedGuthanPieces() < 4)) {
                util.equipDefaultEquipment();
            }
        }


//        if (lootTile != null) lootTile = null;
        if (playerUtil.isInteracting() || looting) {
            timeout = 3;
            return;
        }
        targetNpc = util.findNpc(config.targetNames());
        if (targetNpc == null && isSlayerNpc && !slayerInfo.getDisturbAction().isEmpty()) {
            Optional<NPC> disturbNpc = NPCs.search().withName(slayerInfo.getUndisturbedName()).first();
            log.info("Disturbing " + slayerInfo.getUndisturbedName());
            disturbNpc.ifPresent(npc -> {
                MousePackets.queueClickPacket();
                NPCPackets.queueNPCAction(disturbNpc.get(), slayerInfo.getDisturbAction());
                timeout = 6;
                idleTicks = 0;
            });
        } else {
            if (targetNpc != null) {
                log.info("Should fight, found npc");
                MousePackets.queueClickPacket();
                NPCPackets.queueNPCAction(targetNpc, "Attack");
                timeout = 6;
                idleTicks = 0;
            }
        }
    }

    private void setupPotionsAndFoodVariables() {
        hasFood = util.hasItem(supplies, supplies::findFood);
        hasPrayerPot = util.hasItem(supplies, supplies::findPrayerPotion);
        hasCombatPot = util.hasItem(supplies, supplies::findCombatPotion);
        hasSuperAttackPot = util.hasItem(supplies, supplies::findSuperAttackPotion);
        hasSuperDefencePot = util.hasItem(supplies, supplies::findSuperDefencePotion);
        hasSuperStrengthPot = util.hasItem(supplies, supplies::findSuperStrengthPotion);
        hasAntiPoisonPot = util.hasItem(supplies, supplies::findAntiPoisonPotion);
        hasBones = util.hasItem(supplies, supplies::findBone);
    }

    private void handleFullInventory() {

    }

    private void handleRangingPot() {
        handlePotion(PotionType.RANGING);
    }

    private void handleCombatPot() {
        handlePotion(PotionType.COMBAT);
    }

    private void handleSuperAttack() {
        handlePotion(PotionType.SUPER_ATTACK);
    }

    private void handleSuperDefence() {
        handlePotion(PotionType.SUPER_DEFENCE);
    }

    private void handleSuperStrength() {
        handlePotion(PotionType.SUPER_STRENGTH);
    }

    private void handlePrayerPot() {
        handlePotion(PotionType.PRAYER);
    }

    public void handleAntiPoison() {
        handlePotion(PotionType.ANTIPOISON, 2);
    }

    private void handleEating() {
        handlePotion(PotionType.EAT, 1);
    }


    private void handlePotion(PotionType potionType) {
        handlePotion(potionType, 0);
    }

    private void handlePotion(PotionType potionType, int timeoutTime) {
        Supplier<Widget> potionSupplier = null;

        switch (potionType) {
            case COMBAT:
                if (hasCombatPot) potionSupplier = supplies::findCombatPotion;
                break;
            case SUPER_ATTACK:
                if (hasSuperAttackPot) potionSupplier = supplies::findSuperAttackPotion;
                break;
            case SUPER_DEFENCE:
                if (hasSuperDefencePot) potionSupplier = supplies::findSuperDefencePotion;
                break;
            case SUPER_STRENGTH:
                if (hasSuperStrengthPot) potionSupplier = supplies::findSuperStrengthPotion;
                break;
            case PRAYER:
                if (hasPrayerPot) potionSupplier = supplies::findPrayerPotion;
                break;
            case RANGING:
                if (hasCombatPot) potionSupplier = supplies::findRangingPotion;
                break;
            case ANTIPOISON:
                if (hasAntiPoisonPot) potionSupplier = supplies::findAntiPoisonPotion;
                break;
            case EAT:
                if (hasFood) potionSupplier = supplies::findFood;
                break;
        }

        if (potionSupplier != null) {
            InventoryInteraction.useItem(potionSupplier.get(), potionType == PotionType.EAT ? "Eat" : "Drink");
        }

        if (timeoutTime >= 1) {
            timeout = timeoutTime;
        }
    }


    private void checkAndHandleCombatSkillPotions(Skill skill, Runnable handler) {
        double realSkillLevel = client.getRealSkillLevel(skill);
        double boostedSkillLevel = util.calculateBoostedSkillLevel(realSkillLevel);
        double currentBoostedSkill = client.getBoostedSkillLevel(skill);
        double initialBoost = boostedSkillLevel - realSkillLevel;
        double currentBoostRemaining = currentBoostedSkill - realSkillLevel;
        double finalBoost = Math.round((currentBoostRemaining / initialBoost) * 100);


        if (config.enableDebug()) {
            System.out.println("==================START===========================");
            System.out.println("Debug: Skill: " + skill.getName() + " - Stats");
            System.out.println("Boosted Skill Level: " + boostedSkillLevel);
            System.out.println("Current Boosted Skill Level: " + currentBoostedSkill);
            System.out.println("Real Skill Level: " + realSkillLevel);
            System.out.println("Result (Final Boost): " + finalBoost);
            System.out.println("==================END============================");
        }


        if (currentBoostedSkill == realSkillLevel || finalBoost <= config.useCombatPotAt()) {
            handler.run();
        }
    }


    @Subscribe
    public void onNpcLootReceived(NpcLootReceived event) {
        if (!started || !config.lootEnabled()) return;

        timeout += ThreadLocalRandom.current().nextInt(config.minLootDelay(), config.maxLootDelay());
        Collection<ItemStack> items = event.getItems();
        List<String> lootNames = lootHelper.getLootNames();
        List<Integer> lootIds = lootHelper.getLootIds();

        items.stream().filter(item -> {
            ItemComposition comp = itemManager.getItemComposition(item.getId());
            String itemName = comp.getName().toLowerCase();
            boolean nameMatch = util.isNameMatch(lootNames, itemName);
            boolean idMatch = util.isIdMatch(lootIds, comp.getId());
            return (nameMatch || idMatch) || lootHelper.getPrice(itemName) >= config.minLootWealth();
        }).forEach(it -> {
            util.sendDebugMessageIntoGameChat("Adding to lootQueue: " + it.getId());
            lootQueue.add(it);
        });
    }


    @Subscribe
    public void onStatChanged(StatChanged event) {
        if (!started) return;
        if (client.getBoostedSkillLevel(Skill.HITPOINTS) <= config.eatAt()) {
            handleEating();
        }
        if (config.usePrayerPotion()) {
            if (client.getBoostedSkillLevel(Skill.PRAYER) <= config.usePrayerPotAt()) {
                handlePrayerPot();
            }
        }
        if (config.useCombatPotion() && hasCombatPot) {
            if (client.getBoostedSkillLevel(Skill.STRENGTH) <= config.useCombatPotAt()) {
                handleCombatPot();
            }
        } else if (config.useCombatPotion() && !hasCombatPot) {
            checkAndHandleCombatSkillPotions(Skill.ATTACK, this::handleSuperAttack);
            checkAndHandleCombatSkillPotions(Skill.STRENGTH, this::handleSuperStrength);
            checkAndHandleCombatSkillPotions(Skill.DEFENCE, this::handleSuperDefence);
        }
        if (config.useRangingPotion()) {
            if (client.getBoostedSkillLevel(Skill.RANGED) <= config.useRangingPotAt()) {
                handleRangingPot();
            }
        }
    }


    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (!started) return;
    }


    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (!started) return;
        int bid = event.getVarbitId();
        int pid = event.getVarpId();
        if (pid == VarPlayer.SLAYER_TASK_SIZE) {
            if (event.getValue() <= 0) {
                if (config.breakTab()) {
                    Optional<Widget> item = Inventory.search().withId(11140).first();
                    if (item.isPresent()) {
                        MousePackets.queueClickPacket();
                        InventoryInteraction.useItem(item.get(), "Gem Mine");
                    } else {
                        InventoryInteraction.useItem(supplies.findTeleport(), "Break");
                    }
                }
                if (config.shutdownOnTaskDone()) {
                    util.sendDebugMessageIntoGameChat("Task done, stopping", LogLevel.INFO);
                    resetEverything();
                }
            }
        }
//        } else if (pid == VarPlayer.CANNON_AMMO) {
//            if (event.getValue() <= ThreadLocalRandom.current().nextInt(4, 12)) {
//                reloadCannon();
//                timeout = 1;
//            }
//        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("AutoCombatConfig"))
            return;
        if (event.getKey().equals("lootNames")) {
            lootHelper.setLootNames(null);
            lootHelper.getLootNames();
        }

        if (event.getKey().equals("lootIds")) {
            lootHelper.setLootIds(null);
            lootHelper.getLootIds();
        }
    }


    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        GameState state = event.getGameState();
        if (state == GameState.HOPPING || state == GameState.LOGGED_IN) return;
        EthanApiPlugin.stopPlugin(this);
    }

    private void checkRunEnergy() {
        if (runIsOff() && playerUtil.runEnergy() >= 30) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
        }
    }

    private boolean runIsOff() {
        return EthanApiPlugin.getClient().getVarpValue(173) == 0;
    }

//    private void reloadCannon() {
//        Optional<Widget> cannonball = InventoryUtil.nameContainsNoCase("cannonball").first();
//
//        if (cannonball.isPresent()) {
//            Optional<TileObject> to = ObjectUtil.nameContainsNoCase("dwarf multicannon").nearestToPlayer();
//            if (to.isPresent()) {
//                MousePackets.queueClickPacket();
//                MousePackets.queueClickPacket();
//                ObjectPackets.queueWidgetOnTileObject(cannonball.get(), to.get());
//            }
//        }
//    }


    private void processBoneAction(String action) {
        Inventory.search()
                .onlyUnnoted()
                .withAction(action)
                .filter(b -> config.buryBones())
                .first()
                .ifPresent(bone -> {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetAction(bone, action);
                    timeout = 1;
                });
    }


    private final HotkeyListener toggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            toggle();
        }
    };

    public void toggle() {
        if (client.getGameState() == GameState.LOGGED_IN) {
            started = !started;
            if (started) {
                addOverlay();
            } else {
                removeOverlay();
            }
        }
    }

    private void addOverlay() {
        overlayManager.add(overlay);
        overlayManager.add(tileOverlay);
    }

    private void removeOverlay() {
        overlayManager.remove(overlay);
        overlayManager.remove(tileOverlay);
    }

}
