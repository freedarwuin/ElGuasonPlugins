package com.piggyplugins.toa.Het;

import com.piggyplugins.toa.ToaConfig;
import com.piggyplugins.toa.ToaPlugin;
import com.piggyplugins.toa.Room;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import com.piggyplugins.toa.reflectMeth;

public class Het extends Room {
    @Inject
    private Client client;

    @Inject
    private HetOverlay hetOverlay;

    @Inject
    protected Het(ToaPlugin plugin, ToaConfig config)
	{
		super(plugin, config);
	}

    @Override
    public void init(){
    }

    @Getter(AccessLevel.PACKAGE)
    private final List<GameObject> objects = new ArrayList<>();

    @Override
    public void load()
    {
        overlayManager.add(hetOverlay);
    }

    @Override
    public void unload()
    {
        overlayManager.remove(hetOverlay);
    }


}
