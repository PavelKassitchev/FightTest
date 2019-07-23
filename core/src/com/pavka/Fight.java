package com.pavka;

import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;


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
    class Hex, method containsEnemy added, empty methods getFireFactor, getChargeFactor, getFireDefenseFactor, getChargeDefenseFactor added
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
    int whiteStrength;
    int blackStrength;
    int whiteCasualties;
    int blackCasualties;
    int whiteImprisoned;
    int blackImprisoned;
    double whiteFire;
    double whiteCharge;
    double blackFire;
    double blackCharge;
    int stage;
    int winner;
    Random random;

    public Fight(Hex h) {
        random = new Random();
        hex = h;
        whiteUnits = new Array<Unit>();
        blackUnits = new Array<Unit>();
        white = new HashMap<Force, Integer>();
        black = new HashMap<Force, Integer>();
        for (Force w : hex.whiteForces) {
            whiteInitStrength += w.strength;
            white.put(w, w.strength);
            whiteStrength = whiteInitStrength;
            if (w.isUnit) {
                Unit u = (Unit) w;
                whiteInitPower = u.maxPower * u.strength / u.maxStrength;
                whiteFire += u.fire * hex.getFireFactor(u);
                whiteCharge += u.charge * hex.getChargeFactor(u);
                whiteUnits.add(u);

            } else {
                for (Unit u : w.battalions) {
                    whiteInitPower += u.maxPower * u.strength / u.maxStrength;
                    whiteFire += u.fire * hex.getFireFactor(u);
                    whiteCharge += u.charge * hex.getChargeFactor(u);
                    whiteUnits.add(u);
                }
                for (Unit u : w.squadrons) {
                    whiteInitPower += u.maxPower * u.strength / u.maxStrength;
                    whiteFire += u.fire * hex.getFireFactor(u);
                    whiteCharge += u.charge * hex.getChargeFactor(u);
                    whiteUnits.add(u);
                }
                for (Unit u : w.batteries) {
                    whiteInitPower += u.maxPower * u.strength / u.maxStrength;
                    whiteFire += u.fire * hex.getFireFactor(u);
                    whiteCharge += u.charge * hex.getChargeFactor(u);
                    whiteUnits.add(u);
                }
            }
        }

        for (Force b : hex.blackForces) {
            blackInitStrength += b.strength;
            black.put(b, b.strength);
            blackStrength = blackInitStrength;
            if (b.isUnit) {
                Unit u = (Unit) b;
                blackInitPower = u.maxPower * u.strength / u.maxStrength;
                blackFire += u.fire * hex.getFireFactor(u);
                blackCharge += u.charge * hex.getChargeFactor(u);
                blackUnits.add(u);

            } else {
                for (Unit u : b.battalions) {
                    blackInitPower += u.maxPower * u.strength / u.maxStrength;
                    blackFire += u.fire * hex.getFireFactor(u);
                    blackCharge += u.charge * hex.getChargeFactor(u);
                    blackUnits.add(u);
                }
                for (Unit u : b.squadrons) {
                    blackInitPower += u.maxPower * u.strength / u.maxStrength;
                    blackFire += u.fire * hex.getFireFactor(u);
                    blackCharge += u.charge * hex.getChargeFactor(u);
                    blackUnits.add(u);
                }
                for (Unit u : b.batteries) {
                    blackInitPower += u.maxPower * u.strength / u.maxStrength;
                    blackFire += u.fire * hex.getFireFactor(u);
                    blackCharge += u.charge * hex.getChargeFactor(u);
                    blackUnits.add(u);
                }
            }


        }
    }

    public void init() {
        if (blackInitStrength >= whiteInitStrength || blackInitPower >= whiteInitPower) {
            System.out.println("SURRENDER!");
            for (Force f: white.keySet()) {
                System.out.println("???");
                if (f.morale < 0) {
                    System.out.println("Yes, surrender");
                    if (f.isUnit) {
                        whiteUnits.removeValue((Unit)f, true);
                    }
                    else {
                        for (Unit u: f.battalions) {
                            whiteUnits.removeValue(u, true);
                        }
                        for (Unit u: f.squadrons) {
                            whiteUnits.removeValue(u, true);
                        }
                        for (Unit u: f.batteries){
                            whiteUnits.removeValue(u, true);
                        }
                    }
                    whiteStrength -= f.strength;
                    whiteImprisoned += f.surrender();
                }
            }
        }
        if (whiteInitStrength >= blackInitStrength || whiteInitPower >= blackInitPower) {
            for (Force f: black.keySet()) {
                if (f.morale < 0) {
                    if (f.isUnit) {
                        blackUnits.removeValue((Unit)f, true);
                    }
                    else {
                        for (Unit u: f.battalions) {
                            blackUnits.removeValue(u, true);
                        }
                        for (Unit u: f.squadrons) {
                            blackUnits.removeValue(u, true);
                        }
                        for (Unit u: f.batteries){
                            blackUnits.removeValue(u, true);
                        }
                    }
                    blackStrength -= f.strength;
                    blackImprisoned += f.surrender();
                }
            }
        }
    }

    public int resolveStage() {
        System.out.println("START STAGE " + ++stage);
        if (whiteStrength == 0 || blackStrength == 0) System.out.println("NO ENEMIES!");
        else {
            double fireOnBlack = FIRE_ON_UNIT * whiteFire / blackStrength;

            double fireOnWhite = FIRE_ON_UNIT * blackFire / whiteStrength;

            double chargeOnBlack = -(CASUALITY_INTO_MORALE * fireOnBlack + CHARGE_ON_ENEMY * whiteCharge / blackStrength);

            double chargeOnWhite = -(CASUALITY_INTO_MORALE * fireOnWhite + CHARGE_ON_ENEMY * blackCharge / whiteStrength);


            for (Unit u : whiteUnits) {
                u.fire(1);
                double ratio = u.strength / whiteStrength;
                double randomFactor = 0.7 + 0.6 * random.nextDouble();
                whiteCasualties += hitUnit(u, randomFactor * fireOnWhite, randomFactor * chargeOnWhite);
            }
            for (Unit u : blackUnits) {
                u.fire(1);
                double ratio = u.strength / blackStrength;
                double randomFactor = 0.7 + 0.6 * random.nextDouble();
                blackCasualties += hitUnit(u, randomFactor * fireOnBlack, randomFactor * chargeOnBlack);
            }
            whiteStrength = 0;
            double whiteAmmo = 0;
            for (Force w : hex.whiteForces) {
                whiteStrength += w.strength;
                whiteAmmo += w.ammoStock;
            }
            blackStrength = 0;
            double blackAmmo = 0;
            for (Force b : hex.blackForces) {
                blackStrength += b.strength;
                blackAmmo += b.ammoStock;
            }

            System.out.println("WHITE: strength - " + whiteStrength + " casualties - " + whiteCasualties +
                    " imprisoned - " + whiteImprisoned + " ammo stock - " + whiteAmmo);
            for (Force f : hex.whiteForces) System.out.println("Morale - " + f.morale);
            System.out.println("BLACK: strength - " + blackStrength + " casualties - " + blackCasualties +
                    " imprisoned - " + blackImprisoned + " ammo stock - " + blackAmmo);
            for (Force f : hex.blackForces) System.out.println("Morale - " + f.morale);

            if (winner != 0) System.out.println("WINNER = " + winner);
        }
        return winner;

    }

    public int hitUnit(Unit unit, double fire, double charge) {
        int in = unit.strength;

        unit.bearLoss(fire);
        int out = unit.strength;
        unit.changeMorale(charge);


        return in - out;

    }


}


