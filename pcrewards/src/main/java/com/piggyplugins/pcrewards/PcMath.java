package com.piggyplugins.pcrewards;

public class PcMath {


    public static int calc_Reward_EXP(int level, int points , PcRewardExpOptions exp_type, int league_mult){
        // Floor(level^2/600) * n where n = 35,32,or 18;

        if (level < 25) {return 0;}

        int n_mod = 0;

        switch (exp_type) {
            case ATTACK:
            case STRENGTH:
            case DEFENCE:
            case HITPOINTS:
                n_mod = 35;
                break;
            case MAGIC:
            case RANGE:
                n_mod = 32;
                break;
            case PRAYER:
                n_mod = 18;
                break;
        }

        double exp_per_point = Math.floor((Math.pow(level,2) / 600)) * n_mod;

        if (league_mult > 0 && league_mult <= 16) {
            exp_per_point = exp_per_point * league_mult;
        }

        int bonus_point_sets = (points / 100) * 100;
        int remaining_points = points % 100;

        int bonus_Exp = (int) (bonus_point_sets * exp_per_point * 1.1);
        int remain_Exp = (int) (remaining_points * exp_per_point);


        return bonus_Exp + remain_Exp;
    }


}
