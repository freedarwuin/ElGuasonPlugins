package com.piggyplugins.toa.Warden;

import lombok.extern.slf4j.Slf4j;
import com.piggyplugins.toa.RaidRoom;
import com.piggyplugins.toa.RaidStateTracker;
import com.piggyplugins.toa.ToaConfig;
import com.piggyplugins.toa.RoomOverlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

@Slf4j
public class WardenOverlayBox extends RoomOverlay {
    private int[] poisonIds = new int[]{45570, 45571, 45572, 45573, 45574, 45575, 45576};

    private final Warden plugin;

    @Inject RaidStateTracker tracker;

    @Inject
    protected WardenOverlayBox(ToaConfig config, ModelOutlineRenderer outliner, Warden plugin) {
        super(config, outliner);
        this.plugin = plugin;
		this.setLayer(OverlayLayer.ABOVE_WIDGETS);
		this.setPriority(OverlayPriority.HIGH);
		this.setPosition(OverlayPosition.BOTTOM_RIGHT);
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        if(plugin.getWardenp3() != null && tracker.getCurrentState().getCurrentRoom() != RaidRoom.TOMB)
		{//TODO add config option
			this.panelComponent.getChildren().clear();
			this.panelComponent.getChildren().add(LineComponent.builder().left("Safe Side:").right(plugin.getCurrentSide().getText()).build());
			return this.panelComponent.render(graphics);
		}
        return null;
    }

    }

