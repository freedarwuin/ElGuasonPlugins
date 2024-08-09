package com.piggyplugins.toa.Zebak;

import com.piggyplugins.toa.ToaConfig;
import com.piggyplugins.toa.Prayer.NextAttack;
import com.piggyplugins.toa.Prayer.PrayerOverlay;
import net.runelite.api.Client;

import javax.inject.Inject;
import java.util.Queue;

public class ZebakPrayerOverlay extends PrayerOverlay
{
    private final Zebak plugin;

    @Inject
    protected ZebakPrayerOverlay(Client client, ToaConfig config, Zebak plugin)
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
