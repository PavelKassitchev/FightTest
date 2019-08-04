package com.pavka;

import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class Fighting {
    public static final int FIRE_ON_UNIT = 40;
    public static final double CASUALITY_INTO_MORALE = 3.3;
    public static final int CHARGE_ON_ENEMY = 30;
    public static final int PURSUIT_CHARGE = 45;
    public static final int PURSUIT_ON_RETREATER = 25;
    public static final double PURSUIT_ARTILLERY_FACTOR = 1.5;
    public static final double PURSUIT_CAVALRY_FACTOR = 0.5;
    public static final int MIN_SOLDIERS = 6;
    public static final double MIN_MORALE = 0.2;
    public static final double MORALE_PENALTY = -0.03;
    public static final double MORALE_BONUS = 0.03;
    public static final double SMALL_MORALE_BONUS = 0.02;
    public static final double VICTORY_BONUS = 0.5;
    public static final double SMALL_VICTORY_BONUS = 0.2;
    public static final double LONG_DISTANCE_FIRE = 0.65;
    public static final double NEXT_BONUS = 0.05;
    public static final double NEXT_NEXT_BONUS = 0.1;
    public static final double BACK_BONUS = 0.2;

    public static final double FIRE_ON_ARTILLERY = 0.6;
    public static final double CHARGE_ON_ARTILLERY = 1.5;
    public static final double CHARGE_ON_CAVALRY = 0.5;

    private Hex hex;
    HashMap<Force, Integer> white;
    HashMap<Force, Integer> black;
    Array<Unit> whiteUnits;
    Array<Unit> blackUnits;

    int whiteInitStrength;
    int blackInitStrength;
    double whiteInitPower;
    double blackInitPower;
    int whiteStrength;
    int blackStrength;
    int whiteCasualties;
    int blackCasualties;
    int whiteImprisoned;
    int blackImprisoned;
    int whiteDisordered;
    int blackDisordered;
    double whiteFire;
    double whiteCharge;
    double blackFire;
    double blackCharge;
    HashSet<Direction> whiteFronts;
    HashSet<Direction> blackFronts;
    HashSet<Unit> whiteRouted;
    HashSet<Unit> blackRouted;
    double whiteDirectionBonus;
    double blackDirectionBonus;
    int whiteBattalions;
    int whiteSquadrons;
    int whiteBatteries;
    int whiteWagoms;
    int blackBattalions;
    int blackSquadrons;
    int blackBatteries;
    int blackWagons;
    double scale;
    int stage;
    int winner;
    Random random;
    boolean isOver;


}
