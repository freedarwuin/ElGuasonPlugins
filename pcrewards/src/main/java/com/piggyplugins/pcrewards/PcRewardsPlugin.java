/*
 * Copyright (c) 2023, Marvinb16
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
package com.piggyplugins.pcrewards;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.api.events.MenuOptionClicked;

import com.piggyplugins.pcrewards.PcRewardsDrawWidget;

@Slf4j
@PluginDescriptor(
		name = "PC Rewards",
		description = "Customize the PC Rewards screen to prevent unwanted spending",
		tags = {"pest control", "pc", "minigame", "hide", "points"}
)
public class PcRewardsPlugin extends Plugin
{
	public static final int PC_SHOP_WIDGET_ID = 15925251;
	public static final int PC_SHOP_GROUP_ID = 243;

	public static final int PC_POINTS_WIDGET_ID = 15925256;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private PcRewardsConfig config;

	@Inject
	private PcRewardsDrawWidget drawPcWidget;



	@Override
	protected void startUp() throws Exception {




		Widget points_Screen = client.getWidget(PC_SHOP_WIDGET_ID);

		if (points_Screen == null) { return; }
		else {
			clientThread.invokeAtTickEnd(() ->
		{
			drawPcWidget.draw_Widget(get_Pest_Points());
			hideOptions();

		});
		}


	}

	@Override
	protected void shutDown() throws Exception {
		//Restores all interfaces to normal
		unhide_ALL();
		drawPcWidget.delete_Widget();

	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event) {

		hideOptions();
		drawPcWidget.update_Calculation();

	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked) {

		Widget pc_shop_interface = menuOptionClicked.getWidget();
		if (pc_shop_interface == null) {
			return;
		}
		if (pc_shop_interface.getId() == PC_SHOP_WIDGET_ID){
			clientThread.invokeAtTickEnd(() ->
			{
				hideOptions();

			});
		}


	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded) {

		if (widgetLoaded.getGroupId() == PC_SHOP_GROUP_ID) {

			clientThread.invokeAtTickEnd(() ->
			{
				drawPcWidget.draw_Widget(get_Pest_Points());
				hideOptions();

			});


		}
	}


	private void hideOptions(){

		Widget points_Screen = client.getWidget(PC_SHOP_WIDGET_ID);

		if (points_Screen == null) { return; }

		Widget rewards[] = points_Screen.getDynamicChildren();

		int total_points = get_Pest_Points();

		switch (config.getAtkOp()) {
			case ALL:
				rewards[2].setHidden(false); // 1pt
				rewards[3].setHidden(false); // 10 pt
				rewards[4].setHidden(false); // 100 pt
				break;
			case NONE:
				rewards[2].setHidden(true); // 1pt
				rewards[3].setHidden(true); // 10 pt
				rewards[4].setHidden(true); // 100 pt
				//rewards[0].setHidden(true); // icon
				//rewards[1].setHidden(true); // text
				break;
			case ONLY_100:
				rewards[2].setHidden(true); // 1pt
				rewards[3].setHidden(true); // 10 pt
				rewards[4].setHidden(false); // 100 pt
				break;
			case GREATER_THAN_100:
				if (total_points < 100) {
					rewards[2].setHidden(false); // 1pt
					rewards[3].setHidden(false); // 10 pt
					rewards[4].setHidden(false); // 100 pt
				} else {
					rewards[2].setHidden(true); // 1pt
					rewards[3].setHidden(true); // 10 pt
					rewards[4].setHidden(false); // 100 pt
				}
				break;
		}

		switch (config.getStrOp()) {
			case ALL:
				rewards[7].setHidden(false); // 1pt
				rewards[8].setHidden(false); // 10 pt
				rewards[9].setHidden(false); // 100 pt
				break;
			case NONE:
				rewards[7].setHidden(true); // 1pt
				rewards[8].setHidden(true); // 10 pt
				rewards[9].setHidden(true); // 100 pt
				break;
			case ONLY_100:
				rewards[7].setHidden(true); // 1pt
				rewards[8].setHidden(true); // 10 pt
				rewards[9].setHidden(false); // 100 pt
				break;
			case GREATER_THAN_100:
				if (total_points < 100) {
					rewards[7].setHidden(false); // 1pt
					rewards[8].setHidden(false); // 10 pt
					rewards[9].setHidden(false); // 100 pt
				} else {
					rewards[7].setHidden(true); // 1pt
					rewards[8].setHidden(true); // 10 pt
					rewards[9].setHidden(false); // 100 pt
				}
				break;
		}

		switch (config.getDefOp()) {
			case ALL:
				rewards[12].setHidden(false); // 1pt
				rewards[13].setHidden(false); // 10 pt
				rewards[14].setHidden(false); // 100 pt
				break;
			case NONE:
				rewards[12].setHidden(true); // 1pt
				rewards[13].setHidden(true); // 10 pt
				rewards[14].setHidden(true); // 100 pt
				break;
			case ONLY_100:
				rewards[12].setHidden(true); // 1pt
				rewards[13].setHidden(true); // 10 pt
				rewards[14].setHidden(false); // 100 pt
				break;
			case GREATER_THAN_100:
				if (total_points < 100) {
					rewards[12].setHidden(false); // 1pt
					rewards[13].setHidden(false); // 10 pt
					rewards[14].setHidden(false); // 100 pt
				} else {
					rewards[12].setHidden(true); // 1pt
					rewards[13].setHidden(true); // 10 pt
					rewards[14].setHidden(false); // 100 pt
				}
				break;
		}

		switch (config.getRngOp()) {
			case ALL:
				rewards[17].setHidden(false); // 1pt
				rewards[18].setHidden(false); // 10 pt
				rewards[19].setHidden(false); // 100 pt
				break;
			case NONE:
				rewards[17].setHidden(true); // 1pt
				rewards[18].setHidden(true); // 10 pt
				rewards[19].setHidden(true); // 100 pt
				break;
			case ONLY_100:
				rewards[17].setHidden(true); // 1pt
				rewards[18].setHidden(true); // 10 pt
				rewards[19].setHidden(false); // 100 pt
				break;
			case GREATER_THAN_100:
				if (total_points < 100) {
					rewards[17].setHidden(false); // 1pt
					rewards[18].setHidden(false); // 10 pt
					rewards[19].setHidden(false); // 100 pt
				} else {
					rewards[17].setHidden(true); // 1pt
					rewards[18].setHidden(true); // 10 pt
					rewards[19].setHidden(false); // 100 pt
				}
				break;
		}

		switch (config.getMagicOp()) {
			case ALL:
				rewards[22].setHidden(false); // 1pt
				rewards[23].setHidden(false); // 10 pt
				rewards[24].setHidden(false); // 100 pt
				break;
			case NONE:
				rewards[22].setHidden(true); // 1pt
				rewards[23].setHidden(true); // 10 pt
				rewards[24].setHidden(true); // 100 pt
				break;
			case ONLY_100:
				rewards[22].setHidden(true); // 1pt
				rewards[23].setHidden(true); // 10 pt
				rewards[24].setHidden(false); // 100 pt
				break;
			case GREATER_THAN_100:
				if (total_points < 100) {
					rewards[22].setHidden(false); // 1pt
					rewards[23].setHidden(false); // 10 pt
					rewards[24].setHidden(false); // 100 pt
				} else {
					rewards[22].setHidden(true); // 1pt
					rewards[23].setHidden(true); // 10 pt
					rewards[24].setHidden(false); // 100 pt
				}
				break;
		}

		switch (config.getHpOp()) {
			case ALL:
				rewards[27].setHidden(false); // 1pt
				rewards[28].setHidden(false); // 10 pt
				rewards[29].setHidden(false); // 100 pt
				break;
			case NONE:
				rewards[27].setHidden(true); // 1pt
				rewards[28].setHidden(true); // 10 pt
				rewards[29].setHidden(true); // 100 pt
				break;
			case ONLY_100:
				rewards[27].setHidden(true); // 1pt
				rewards[28].setHidden(true); // 10 pt
				rewards[29].setHidden(false); // 100 pt
				break;
			case GREATER_THAN_100:
				if (total_points < 100) {
					rewards[27].setHidden(false); // 1pt
					rewards[28].setHidden(false); // 10 pt
					rewards[29].setHidden(false); // 100 pt
				} else {
					rewards[27].setHidden(true); // 1pt
					rewards[28].setHidden(true); // 10 pt
					rewards[29].setHidden(false); // 100 pt
				}
				break;
		}

		switch (config.getPrayOp()) {
			case ALL:
				rewards[32].setHidden(false); // 1pt
				rewards[33].setHidden(false); // 10 pt
				rewards[34].setHidden(false); // 100 pt
				break;
			case NONE:
				rewards[32].setHidden(true); // 1pt
				rewards[33].setHidden(true); // 10 pt
				rewards[34].setHidden(true); // 100 pt
				break;
			case ONLY_100:
				rewards[32].setHidden(true); // 1pt
				rewards[33].setHidden(true); // 10 pt
				rewards[34].setHidden(false); // 100 pt
				break;
			case GREATER_THAN_100:
				if (total_points < 100) {
					rewards[32].setHidden(false); // 1pt
					rewards[33].setHidden(false); // 10 pt
					rewards[34].setHidden(false); // 100 pt
				} else {
					rewards[32].setHidden(true); // 1pt
					rewards[33].setHidden(true); // 10 pt
					rewards[34].setHidden(false); // 100 pt
				}
				break;
		}

		// HERB OPTION
		rewards[37].setHidden(config.getHerb());
		rewards[40].setHidden(config.getHerb());

		// MINERAL OPTION
		rewards[41].setHidden(config.getMine());
		rewards[44].setHidden(config.getMine());

		// SEED OPTION
		rewards[45].setHidden(config.getSeed());
		rewards[48].setHidden(config.getSeed());

		// MACE
		rewards[51].setHidden(config.getMace());
		rewards[54].setHidden(config.getMace());

		// Robe Top
		rewards[55].setHidden(config.getRTop());
		rewards[58].setHidden(config.getRTop());

		// robe bottoms
		rewards[59].setHidden(config.getRBtm());
		rewards[62].setHidden(config.getRBtm());

		// gloves
		rewards[63].setHidden(config.getGlove());
		rewards[66].setHidden(config.getGlove());

		// mage helm
		rewards[67].setHidden(config.getMageHelm());
		rewards[70].setHidden(config.getMageHelm());

		// range helm
		rewards[71].setHidden(config.getRhelm());
		rewards[74].setHidden(config.getRhelm());

		// melee helm
		rewards[75].setHidden(config.getMelHelm());
		rewards[78].setHidden(config.getMelHelm());

		// seal
		rewards[79].setHidden(config.getSeal());
		rewards[82].setHidden(config.getSeal());


	}

	private void unhide_ALL() {

		Widget points_Screen = client.getWidget(PC_SHOP_WIDGET_ID);

		if (points_Screen == null) { return; }

		Widget rewards[] = points_Screen.getDynamicChildren();

		for (int i = 0; i < rewards.length; i++){
			if (rewards[i].isSelfHidden()){
				rewards[i].setHidden(false);
			}
		}

	}

	private int get_Pest_Points() {

		Widget points_loc = client.getWidget(PC_POINTS_WIDGET_ID);

		String points_text = points_loc.getText();

		String value_points = points_text.substring(points_text.indexOf(">") + 1, points_text.lastIndexOf("<"));
		// Fixed crash when points is >= 1000, the "," caused a crash.
		String fixed_points = value_points.replace(",", "");

		return Integer.parseInt(fixed_points);
	}



	@Provides
	PcRewardsConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(PcRewardsConfig.class);
	}
}
