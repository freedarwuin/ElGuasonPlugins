/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.piggyplugins.socketsotetseg;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.tileindicators.TileIndicatorsConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import javax.inject.Inject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;

public class SotetsegTileIndicatorsOverlay extends Overlay
{
	private final Client client;
	private final SotetsegPlugin plugin;
	private final TileIndicatorsConfig config;

	@Inject
	private SotetsegTileIndicatorsOverlay(Client client, SotetsegPlugin plugin)
	{
		this.client = client;
		this.config = plugin.getTileIndicatorsConfig();
		this.plugin = plugin;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.HIGHEST);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (plugin.isMazeActive())
		{
			if (config.highlightHoveredTile())
			{
				// If we have tile "selected" render it
				if (client.getSelectedSceneTile() != null)
				{
					renderTile(graphics, client.getSelectedSceneTile().getLocalLocation(), config.highlightHoveredColor(), config.hoveredTileBorderWidth());
				}
			}

			if (config.highlightDestinationTile())
			{
				renderTile(graphics, client.getLocalDestinationLocation(), config.highlightDestinationColor(), config.destinationTileBorderWidth());
			}

			if (config.highlightCurrentTile())
			{
				final WorldPoint playerPos = client.getLocalPlayer().getWorldLocation();
				if (playerPos == null)
				{
					return null;
				}

				final LocalPoint playerPosLocal = LocalPoint.fromWorld(client, playerPos);
				if (playerPosLocal == null)
				{
					return null;
				}

				renderTile(graphics, playerPosLocal, config.highlightCurrentColor(), config.currentTileBorderWidth());
			}
		}

		return null;
	}

	private void renderTile(final Graphics2D graphics, final LocalPoint dest, final Color color, final double borderWidth)
	{
		if (dest == null)
		{
			return;
		}

		final Polygon poly = Perspective.getCanvasTilePoly(client, dest);

		if (poly == null)
		{
			return;
		}

		OverlayUtil.renderPolygon(graphics, poly, color, new BasicStroke((float) borderWidth));
	}
}
