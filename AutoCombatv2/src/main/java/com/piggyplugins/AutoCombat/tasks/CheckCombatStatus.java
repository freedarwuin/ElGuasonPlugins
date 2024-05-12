package com.piggyplugins.AutoCombat.tasks;

import com.piggyplugins.AutoCombat.AutoCombatConfig;
import com.piggyplugins.AutoCombat.AutoCombatPlugin;
import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckCombatStatus extends AbstractTask<AutoCombatPlugin, AutoCombatConfig> {
    public CheckCombatStatus(AutoCombatPlugin plugin, AutoCombatConfig config) {
        super(plugin, config);
    }

    @Override
    public boolean validate() {
        return true; // Always run to continuously check and update combat status
    }

    @Override
    public void execute() {
        // Check both if the player is interacting and if there is an interaction target
        boolean currentlyInCombat = plugin.getClient().getLocalPlayer().isInteracting() &&
                plugin.getClient().getLocalPlayer().getInteracting() != null;

        if (currentlyInCombat != plugin.inCombat) {
            plugin.inCombat = currentlyInCombat;
            log.info("Combat status updated: Currently in combat = {}", plugin.inCombat);
        }
    }
}