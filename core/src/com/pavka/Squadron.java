package com.pavka;



public class Squadron extends Unit {
    public static final int MAX_STRENGTH = 180;
    public final static double SPEED = 44;
    public final static double FOOD_NEED = 1.1;
    public final static double AMMO_NEED = 0.1;
    public final static double FOOD_LIMIT = 7;
    public final static double AMMO_LIMIT = 2.4;
    public final static double FIRE = 0.1;
    public final static double CHARGE = 2.4;
    public final static double MAX_POWER = 1;

    public Squadron(Nation nation, Hex hex) {
        this(nation, hex, MAX_STRENGTH);
    }
    public Squadron(Play play, Nation nation, Hex hex) {
        this(nation, hex);
        this.play = play;
    }

    public Squadron(Nation nation, Hex hex, int strength) {
        super(nation, hex);
        //isUnit = true;
        type = CAVALRY;
        imprisoned = 0.7;
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
