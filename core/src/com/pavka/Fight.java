package com.pavka;

import com.badlogic.gdx.utils.Array;

import java.util.HashMap;


public class Fight {
    public static final int FIRE_ON_UNIT = 40;
    public static final double CASUALITY_INTO_MORALE = 3.3;
    public static final int CHARGE_ON_ENEMY = 30;
    public static final int PURSUIT_CHARGE = 45;
    public static final int MIN_SOLDIERS = 6;
    public static final double MIN_MORALE = 0.2;
    public static final double MORALE_PENALTY = -0.03;
    public static final double MORALE_BONUS = 0.03;
    public static final double SMALL_MORALE_BONUS = 0.02;
    public static final double VICTORY_BONUS = 0.5;
    public static final double SMALL_VICTORY_BONUS = 0.2;
    public static final double LONG_DISTANCE_FIRE = 0.65;

    private Hex hex;

    /* Section for the update comments, new and revised methods,etc.

    Battle abilities:
    For retreatLevel = 0.7 (the dependency doesn't look so crucial):
    13 - 15 battalions are about to be equal to 20 batteries, but infantry + artillery get a great bonus,
    as a result 10 batteries with 30 battalions can stop 45 battalions
    1 battalion is about to be equal to 3 squadrons
    battle result of artillery vs cavalry depends on too many factors, generally small cavalry in attack
    is more effective (1 to 1 ore even better) than big cavalry in defence (3 to one or worse)
    Generally in terms of fighting
    Squadron - 1 pt.
    Battery - 3 pt.
    Battalion - 3 pt.

    On march:
    Battalion length is about 200 m
    Squadron length is about 160 m
    Battery length is about 100 m

    Regiment - 600 m or a bit more
    Brigade - 1200 m or a bit more
    Division - up to 4 km
    Corps of 2 infantry and 1 cavalry divisions without wagons starts from 8 km

    Possible wagon train for a division - ?

    Ammo:
    Battalion - 600 Kgs per 4 hours
    Squadron - 100 Kgs per 4 hours
    Battery - 1600 Kgs per 4 hours
    Division 16 hour stock - 55200 Kgs

    Food:
    Battalion - 1800 Kgs per day
    Squadron - 1800 Kgs per day
    Battery - 600 Kgs per day
    Division 5 days stock - 138000Kgs

    2 horse wagon - 1000 Kgs
    100 wagons length - 650 m

    or maybe 65 wagon train of 400 m length

     */
    /*
    Changes in the project:

    Unit and sub-classes - double maxPower and MAX_POWER added;
    Nation color added
    whiteForces and blackForces in Hex, method locate(force) and method eliminate(force) added
    class Control: in Constructor forces are new Array addAll whites and blacks instead of this.forces = hex.forces
    methods hex.locate and hex.eliminate are used in Force class instead of hex.forces.add and hex.forces.removeValue
    class Hex, method containsEnemy added
    class Force setRetreatDirection from many enemies(?), method surrender added
    local variable Fight fight and method startFight are added to Hex class
     */


    HashMap<Force, Integer> white;
    HashMap<Force, Integer> black;
    Array<Unit> whiteUnits;
    Array<Unit> blackUnits;

    int whiteInitStrength;
    int blackInitStrength;
    double whiteInitPower;
    double blackInitPower;
    int stage;

    public Fight(Hex h) {
        hex = h;
        white = new HashMap<Force, Integer>();
        black = new HashMap<Force, Integer>();
        for (Force w : hex.whiteForces) {
            whiteInitStrength += w.strength;
            white.put(w, w.strength);
            if (w.isUnit) {
                Unit u = (Unit) w;
                whiteInitPower = u.maxPower * u.strength / u.maxStrength;

            } else {
                for (Unit u : w.battalions) {
                    whiteInitPower += u.maxPower * u.strength / u.maxStrength;
                }
                for (Unit u : w.squadrons) {
                    whiteInitPower += u.maxPower * u.strength / u.maxStrength;
                }
                for (Unit u : w.batteries) {
                    whiteInitPower += u.maxPower * u.strength / u.maxStrength;
                }
            }
        }

        for (Force b : hex.blackForces) {
            blackInitStrength += b.strength;
            black.put(b, b.strength);
            if (b.isUnit) {
                Unit u = (Unit) b;
                blackInitPower = u.maxPower * u.strength / u.maxStrength;

            } else {
                for (Unit u : b.battalions) {
                    blackInitPower += u.maxPower * u.strength / u.maxStrength;
                }
                for (Unit u : b.squadrons) {
                    blackInitPower += u.maxPower * u.strength / u.maxStrength;
                }
                for (Unit u : b.batteries) {
                    blackInitPower += u.maxPower * u.strength / u.maxStrength;
                }
            }


        }
    }


}


