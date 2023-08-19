package com.piggyplugins.Karambwans;

import com.piggyplugins.Karambwans.Overlay.TableAlignment;
import com.piggyplugins.Karambwans.Overlay.TableComponent;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

@Slf4j
@Singleton
class KarambwansOverlay extends OverlayPanel {
    private final KarambwansPlugin plugin;
    private final KarambwansConfiguration config;


    String timeFormat;
    public String infoStatus = "Starting...";

    @Inject
    private KarambwansOverlay(final Client client, final KarambwansPlugin plugin, final KarambwansConfiguration config) {
        super(plugin);
        setPosition(OverlayPosition.BOTTOM_LEFT);
        this.plugin = plugin;
        this.config = config;
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Auto Karambwans overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        if (plugin.timer == null || !config.enableUI()) {
            log.debug("Overlay conditions not met, not starting overlay");
            return null;
        }

        if (!plugin.enablePlugin) {
            infoStatus = "Plugin disabled";
        }
        TableComponent tableComponent = new TableComponent();
        tableComponent.setColumnAlignments(TableAlignment.LEFT, TableAlignment.RIGHT);
        Duration duration = Duration.between(plugin.timer, Instant.now());
        timeFormat = (duration.toHours() < 1) ? "mm:ss" : "HH:mm:ss";
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Auto Karambwans " + config.version)
                .color(ColorUtil.fromHex("#FFE247"))
                .build());

        if (plugin.enablePlugin) {
            panelComponent.setPreferredSize(new Dimension(200, 250));
            panelComponent.setBorder(new Rectangle(5, 5, 5, 5));

            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Plugin Enabled")
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Status:")
                    .leftColor(Color.WHITE)
                    .right(infoStatus)
                    .rightColor(Color.WHITE)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Time running:")
                    .leftColor(Color.WHITE)
                    .right(formatDuration(duration.toMillis(), timeFormat))
                    .rightColor(Color.WHITE)
                    .build());

        } else {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Plugin Disabled")
                    .color(Color.RED)
                    .build());
        }

        return super.render(graphics);
    }
}
