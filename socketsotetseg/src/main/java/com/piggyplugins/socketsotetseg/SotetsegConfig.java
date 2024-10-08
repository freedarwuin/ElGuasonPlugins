/*
 * Copyright (c) 2020, Charles Xu <github.com/kthisiscvpv>
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

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Socket Sotetseg")
public interface SotetsegConfig extends Config
{
	@ConfigItem(
		position = 1,
		keyName = "getTileColor",
		name = "Tile Color",
		description = "The color of the tiles."
	)
	default Color getTileColor()
	{
		return new Color(0, 0, 0);
	}

	@ConfigItem(
		position = 2,
		keyName = "getTileTransparency",
		name = "Tile Transparency",
		description = "The color transparency of the tiles. Ranges from 0 to 255, inclusive."
	)
	default int getTileTransparency()
	{
		return 50;
	}

	@ConfigItem(
		position = 3,
		keyName = "getTileOutline",
		name = "Tile Outline Color",
		description = "The color of the outline of the tiles."
	)
	default Color getTileOutline()
	{
		return Color.GREEN;
	}

	@ConfigItem(
		position = 4,
		keyName = "getTileOutlineSize",
		name = "Tile Outline Size",
		description = "The size of the outline of the tiles."
	)
	default int getTileOutlineSize()
	{
		return 1;
	}

	@ConfigItem(
		position = 5,
		keyName = "flashScreenOnChosen",
		name = "Flash Screen on Chosen",
		description = "Flash your screen when you're chosen to run the maze"
	)
	default boolean flashScreen()
	{
		return false;
	}

	@ConfigItem(
		position = 6,
		keyName = "hideScreenFlash",
		name = "Hide Screen Flash",
		description = "Remove the white flash when you're teleported at the start of the maze phase"
	)
	default boolean hideScreenFlash()
	{
		return false;
	}

	@ConfigItem(
		position = 7,
		keyName = "solveMaze",
		name = "Solve the Maze",
		description = "Adds an overlay for the best way to run sotetseg maze"
	)
	default boolean solveMaze()
	{
		return true;
	}

	@ConfigItem(
		position = 8,
		keyName = "numbersOn",
		name = "Add Numbers",
		description = "Adds number overlay to tiles you need to click"
	)
	default boolean numbersOn()
	{
		return true;
	}

	@ConfigItem(
		position = 9,
		keyName = "getFontSize",
		name = "Font Size",
		description = "Size of font for numbers"
	)
	default int getFontSize()
	{
		return 10;
	}

	@ConfigItem(
		position = 10,
		keyName = "highlightTiles",
		name = "Add Outline",
		description = "Adds tile highlight to tiles you need to click"
	)
	default boolean highlightTiles()
	{
		return true;
	}

	@ConfigItem(
		position = 11,
		keyName = "getHighlightTileOutline",
		name = "Tile Outline Color",
		description = "The color of the outline of the highlighted tiles"
	)
	default Color getHighlightTileOutline()
	{
		return Color.GREEN;
	}

	@ConfigItem(
		position = 12,
		keyName = "showDPSSplits",
		name = "Show Between Maze Splits",
		description = "Shows time between mazes"
	)
	default boolean showBetweenSplits()
	{
		return false;
	}

	@ConfigItem(
		position = 13,
		keyName = "showMazeSplits",
		name = "Show Maze Splits",
		description = "Shows maze splits"
	)
	default boolean showMazeSplits()
	{
		return false;
	}

	@ConfigItem(
		position = 14,
		keyName = "showDetailedSplits",
		name = "Show Detailed Splits",
		description = "Adds extra information to splits"
	)
	default boolean showDetailedSplits()
	{
		return false;
	}

	@ConfigItem(
		position = 15,
		keyName = "splitMessageColor",
		name = "Time Splits Message Color",
		description = "Color of splits in chat box"
	)
	default Color getSplitsMessageColor()
	{
		return Color.RED;
	}

	@ConfigItem(
		position = 16,
		keyName = "showSotetsegInstanceTimer",
		name = "Sotetseg Instance Timer",
		description = "Show when Sote can be attacked after the maze"
	)
	default boolean showSotetsegInstanceTimer()
	{
		return true;
	}

	@ConfigItem(
		position = 16,
		keyName = "showSotetsegInstanceTimerPlayer",
		name = "Instance Timer On Player",
		description = "Show when Sote can be attacked after the maze"
	)
	default boolean showSotetsegInstanceTimerPlayer()
	{
		return true;
	}

	@ConfigItem(
		position = 16,
		keyName = "getFontSizeInstanceTimer",
		name = "Font Size Instance Timer",
		description = "Size of font for Instance Timer"
	)
	default int getFontSizeInstanceTimer()
	{
		return 14;
	}

	@ConfigItem(
		position = 17,
		keyName = "isChosenText",
		name = "Add Chosen Text",
		description = "Adds the text from the config below onto the screen when you've been chosen to run the maze"
	)
	default boolean isChosenText()
	{
		return true;
	}

	@ConfigItem(
		position = 18,
		keyName = "customChosenText",
		name = "Custom Chosen Text",
		description = "Adds the text from this config onto the screen when you've been chosen to run the maze"
	)
	default String customChosenText()
	{
		return "You have been chosen.";
	}

	@ConfigItem(
		position = 19,
		keyName = "chosenTextDuration",
		name = "Chosen Text Duration",
		description = "How long the chosen text should stay on the screen"
	)
	default int chosenTextDuration()
	{
		return 200;
	}
}
