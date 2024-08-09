package com.piggyplugins.toa.Warden;

import com.piggyplugins.toa.ToaConfig;
import com.piggyplugins.toa.Prayer.NextAttack;
import com.piggyplugins.toa.Prayer.PrayerOverlay;
import net.runelite.api.Client;

import javax.inject.Inject;
import java.util.Queue;

public class WardenPrayerOverlay extends PrayerOverlay
{
    private final Warden plugin;

    @Inject
    protected WardenPrayerOverlay(Client client, ToaConfig config, Warden plugin)
    {
        super(client, config);
        this.plugin = plugin;
    }

    @Override
    protected Queue<NextAttack> getAttackQueue()
    {
        return plugin.getNextAttackQueue();
    }

    @Override
    protected long getLastTick()
    {
        return plugin.getLastTick();
    }

    @Override
    protected boolean isEnabled()
    {
        return getConfig().zebakPrayerHelper();
    }
}
