package com.piggyplugins.toa.Warden;

import com.piggyplugins.toa.ToaConfig;
import com.piggyplugins.toa.Prayer.NextAttack;
import com.piggyplugins.toa.Prayer.PrayerBoxOverlay;
import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;

import javax.inject.Inject;
import java.util.Queue;
import com.piggyplugins.toa.Warden.Warden;

public class WardenPrayerBoxOverlay extends PrayerBoxOverlay
{
    private final Warden plugin;

    @Inject
    protected WardenPrayerBoxOverlay(Client client, ToaConfig config, Warden plugin, SpriteManager spriteManager)
    {
        super(client, config, spriteManager);
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
