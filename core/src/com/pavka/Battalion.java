package com.pavka;


public class Battalion extends Unit {
    public static final int MAX_STRENGTH = 900;
    public final static double SPEED = 28;
    public final static double FOOD_NEED = 2;
    public final static double AMMO_NEED = 0.5;
    public final static double FOOD_LIMIT = 12;
    public final static double AMMO_LIMIT = 4;
    public final static double FIRE = 1;
    public final static double CHARGE = 1;
    public final static double MAX_POWER = 3;

    public Battalion (Nation nation, Hex hex) {
        this(nation, hex, MAX_STRENGTH);
    }
    public Battalion(Play play, Nation nation, Hex hex) {
        this(nation, hex);
        this.play = play;
    }

    public Battalion (Nation nation, Hex hex, int strength) {
        super(nation, hex);
        //isUnit = true;
        type = INFANTRY;
        imprisoned = 1;
        maxStrength = MAX_STRENGTH;
        maxFire = FIRE;
        maxCharge = CHARGE;
        maxPower = MAX_POWER;
        this.strength = strength;
        speed = SPEED;
        foodNeed = FOOD_NEED * strength / maxStrength;
        ammoNeed = AMMO_NEED * strength / maxStrength;
        foodLimit = FOOD_LIMIT * strength / maxStrength;
        ammoLimit = AMMO_LIMIT * strength / maxStrength;
        foodStock = FOOD_LIMIT * strength / maxStrength;
        ammoStock = AMMO_LIMIT * strength / maxStrength;
        fire = FIRE  * strength / maxStrength;
        charge = CHARGE * strength / maxStrength;
        xp = 0;
        fatigue = 0;
        morale = nation.getNationalMorale();
    }
}
