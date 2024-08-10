package com.piggyplugins.pcrewards;


import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import javax.inject.Inject;

import static com.piggyplugins.pcrewards.PcMath.*;

import net.runelite.api.Skill;

public class PcRewardsDrawWidget {

    private static final int PC_REWARDS_BAR_WIDGET = 15925253;

    public static final int font_ID = 496;
    public static final int font_Color = 16750623; // ff981f



    @Inject
    private PcRewardsConfig config;

    @Inject
    private Client client;


    private Widget parent_Widget = null;


    private Widget drawn_Widget = null;

    private int saved_points = -1;


    public void draw_Widget(int pest_points) {

        saved_points = pest_points;


        parent_Widget = client.getWidget(PC_REWARDS_BAR_WIDGET);

        if (parent_Widget == null) {return;}

        drawn_Widget = parent_Widget.createChild(0, 4);

        //Setup the child widget parameters
        drawn_Widget.setFontId(font_ID);
        drawn_Widget.setOpacity(0);

        if (config.getCalcStyle() == PcRewardExpOptions.HIDE) {
            drawn_Widget.setHidden(true);
        } else {
            drawn_Widget.setHidden(false);
        }

        drawn_Widget.setTextColor(font_Color);
        drawn_Widget.setTextShadowed(true);

        String points_String = config.getCalcStyle().toString() + " Exp: <col=ffffff>" + String.format("%,d",calc_Reward_EXP(get_Level(config.getCalcStyle()),pest_points, config.getCalcStyle(), config.getExpMult())) + "</col";
        drawn_Widget.setText(points_String);

        drawn_Widget.setOriginalHeight(40);
        drawn_Widget.setOriginalWidth(140);

        drawn_Widget.setOriginalX(320);
        drawn_Widget.setOriginalY(-3);

        drawn_Widget.setLineHeight(15);
        drawn_Widget.setXTextAlignment(1);
        drawn_Widget.setYTextAlignment(1);

        drawn_Widget.revalidate();
    }

    public void update_Calculation(){

        if (drawn_Widget == null) {return;}

        if (config.getCalcStyle() == PcRewardExpOptions.HIDE){
            drawn_Widget.setHidden(true);
        } else {
            drawn_Widget.setHidden(false);
        }

        String points_String = config.getCalcStyle().toString() + " Exp: <col=ffffff>" + String.format("%,d",calc_Reward_EXP(get_Level(config.getCalcStyle()),saved_points, config.getCalcStyle(), config.getExpMult())) + "</col";
        drawn_Widget.setText(points_String);


    }

    public void delete_Widget(){

        if (parent_Widget == null) { return; }

        parent_Widget.deleteAllChildren();

    }

    private int get_Level(PcRewardExpOptions pcRewardExpOptions){

        int level = 0;

        switch (pcRewardExpOptions){
            case ATTACK:
                level = client.getRealSkillLevel(Skill.ATTACK);
                break;
            case STRENGTH:
                level = client.getRealSkillLevel(Skill.STRENGTH);
                break;
            case DEFENCE:
                level = client.getRealSkillLevel(Skill.DEFENCE);
                break;
            case HITPOINTS:
                level = client.getRealSkillLevel(Skill.HITPOINTS);
                break;
            case MAGIC:
                level = client.getRealSkillLevel(Skill.MAGIC);
                break;
            case RANGE:
                level = client.getRealSkillLevel(Skill.RANGED);
                break;
            case PRAYER:
                level = client.getRealSkillLevel(Skill.PRAYER);
                break;
            case HIDE:
                level = 0;
                break;
        }


        return level;
    }
}
