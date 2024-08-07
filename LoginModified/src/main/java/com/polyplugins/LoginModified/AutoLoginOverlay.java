package com.polyplugins.LoginModified;

import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class AutoLoginOverlay extends Overlay {
    private final Client client;
    private final AutoLoginPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private AutoLoginOverlay(Client client, AutoLoginPlugin plugin, AutoLoginConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.setPriority(OverlayPriority.HIGHEST);
        this.setPosition(OverlayPosition.BOTTOM_LEFT);
        this.getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "AutoLogin Overlay"));
    }

    public Dimension render(Graphics2D graphics) {
        if (this.plugin == null) {
            return null;
        } else if (!this.plugin.started) {
            return null;
        } else {
            this.panelComponent.getChildren().clear();
            this.panelComponent.getChildren().add(TitleComponent.builder().text("AutoLogin").color(Color.decode("#fa5555")).build());
            this.panelComponent.getChildren().add(TitleComponent.builder().text("Status: " + this.plugin.status).color(Color.ORANGE).build());
            this.panelComponent.setPreferredSize(new Dimension(175, 100));
            this.panelComponent.setBackgroundColor(Color.BLACK);
            return this.panelComponent.render(graphics);
        }
    }
}

