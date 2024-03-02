package com.polyplugins.AutoCombat.helper;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.query.ItemQuery;
import com.google.inject.Inject;
import com.polyplugins.AutoCombat.AutoCombatConfig;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class LootHelper {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private AutoCombatConfig config;

    @Inject
    public ItemManager itemManager;

    /**
     * Name, Price
     */
    public HashMap<String, Integer> lootCache = new HashMap<>();

    @Setter
    private List<String> lootNames = null;
    @Setter
    private List<Integer> lootIds = null;


    /**
     * Looks for the item by name and returns if there are any noted or stackable in the inventory
     *
     * @param
     * @return
     */
    public boolean hasStackableLoot(ItemComposition comp) {
        String name = comp.getName();
        ItemQuery itemQry = Inventory.search().withName(name);
        if (itemQry.first().isEmpty()) {
            return false;
        }
        return itemQry.onlyNoted().first().isPresent() || itemQry.quantityGreaterThan(1).first().isPresent();
    }

    /**
     * Takes the loot names and returns as a list with trimmed names
     *
     * @return
     */
    public List<String> getLootNames() {
//        if (lootNames == null)
            lootNames = Arrays.stream(config.lootNames().split(",")).map(String::trim).collect(Collectors.toList());
        return lootNames;
    }

    /**
     * Takes the loot ids and returns as a list with trimmed names
     *
     * @return
     */
    public List<Integer> getLootIds() {
        lootIds = Arrays.stream(config.lootIds().split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        return lootIds;
    }


    /**
     * Gets the cached price or wiki price if not yet cached
     *
     * @param name Exact name of item
     * @return
     */
    public int getPrice(String name) {
        return lootCache.computeIfAbsent(name, key -> itemManager.search(key).get(0).getWikiPrice());
    }

}
