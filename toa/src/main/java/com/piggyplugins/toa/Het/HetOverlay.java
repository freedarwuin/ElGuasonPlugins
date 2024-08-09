package com.piggyplugins.toa.Het;

import com.piggyplugins.toa.ToaConfig;
import com.piggyplugins.toa.RoomOverlay;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import javax.inject.Inject;
import java.awt.*;

public class HetOverlay extends RoomOverlay {

    @Inject
    protected HetOverlay(ToaConfig config, ModelOutlineRenderer outliner)
    {
        super(config, outliner);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (config.orbTrueTile()){
            renderOrbs(graphics);
        }

        return null;
    }

    private void renderOrbs(Graphics2D graphics){
        for (NPC npc : client.getNpcs())
        {
            NPCComposition npcComposition = npc.getTransformedComposition();

            if (npcComposition != null && npc.getId() == 11708){
                renderTrueTile(graphics, npc, npcComposition.getSize());
            }
        }
    }

}
