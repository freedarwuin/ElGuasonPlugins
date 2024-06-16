package ${PACKAGE_NAME}.${PLUGIN_NAME};

import com.example.EthanApiPlugin.Collections.TileObjects;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Optional;

public class ${PLUGIN_NAME}Overlay extends OverlayPanel {
    private final Client client;
    private final ${PLUGIN_NAME}Plugin plugin;

    @Inject
    private ${PLUGIN_NAME}Overlay(Client client, ${PLUGIN_NAME}Plugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.setPreferredSize(new Dimension(240, 360));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("${PLUGIN_NAME}")
                .color(new Color(214, 143, 49))
                .build());

        return super.render(graphics);
    }
}