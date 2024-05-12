package com.piggyplugins.AutoCombat.tasks;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.InteractionApi.NPCInteraction;
import com.google.inject.Inject;
import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import com.piggyplugins.AutoCombat.AutoCombatConfig;
import com.piggyplugins.AutoCombat.AutoCombatPlugin;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.client.*;

@Getter
@Slf4j
public class attackNPC extends AbstractTask<AutoCombatPlugin, AutoCombatConfig> {
    public attackNPC(AutoCombatPlugin plugin, AutoCombatConfig config) {
        super(plugin, config);
    }

    @Override
    public boolean validate() {
        return !plugin.inCombat;
    }

    @Override
    public void execute() {
        if (plugin.getClient() == null) {
            log.error("Client is null");
            return;
        }
        if (plugin.getClient().getNpcs() == null) {
            log.error("NPC list is null");
            return;
        }
        NPC targetNPC = findNPC(config.npcTarget());
        if (targetNPC != null) {
            log.info("Attacking NPC: {}", config.npcTarget());
            NPCInteraction.interact(targetNPC, "Attack");
        } else {
            log.info("NPC not found: {}", config.npcTarget());
        }
    }

    public NPC findNPC(String npcName) {
        if (plugin.getClient() == null || plugin.getClient().getNpcs() == null) {
            log.warn("Client or NPC list not initialized");
            return null;
        }
        return plugin.getClient().getNpcs().stream()
                .filter(npc -> npc.getName() != null && npc.getName().contains(npcName))
                .findFirst()
                .orElse(null);
    }
}
