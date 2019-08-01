package com.pavka;

import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static com.pavka.Nation.BLACK;
import static com.pavka.Nation.WHITE;
import static com.pavka.Unit.*;


public class Fight {
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
    default constructor Order() - added retreatLevel 0,7;
    FIRE_ON_ARTILLERY, CHARGE_ON_ARTILLERY and CHARGE_ON_CAVALRY moved from Unit bearLoss and changeMorale to hitUnit in Fight;
    Unit.route() added;
    force.formerSuper = force.superForce; added to Force.detach()
    Hex.clean() added
    Unit.bearLosses returns int

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
    int stage;
    int winner;
    Random random;
    boolean isOver;

    public Fight(Hex h) {
        random = new Random();
        hex = h;
        whiteUnits = new Array<Unit>();
        blackUnits = new Array<Unit>();
        white = new HashMap<Force, Integer>();
        black = new HashMap<Force, Integer>();
        whiteRouted = new HashSet<Unit>();
        blackRouted = new HashSet<Unit>();
        whiteFronts = new HashSet<Direction>();
        blackFronts = new HashSet<Direction>();
        for (Force w : hex.whiteForces) {
            whiteInitStrength += w.strength;
            if (w.isUnit) {
                Unit u = (Unit) w;
                whiteInitPower += u.maxPower * u.strength / u.maxStrength;

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
        System.out.println();
        System.out.println("Battle begins! White: " + whiteInitStrength + " Black: " + blackInitStrength);
    }

    public void init() {
        boolean blackAdvantage = blackInitStrength > whiteInitStrength || blackInitPower >= whiteInitPower;
        Direction whiteInitDirection = null;
        Direction blackInitDirection = null;
        for (Force f : hex.whiteForces) {
            if (blackAdvantage && f.morale < 0) {
                whiteImprisoned += f.strength;
                f.surrender();
            } else {
                white.put(f, f.strength);
                if (f.order.frontDirection != null) {
                    Direction d = f.order.frontDirection;
                    if (whiteFronts.isEmpty()) {
                        whiteInitDirection = d;
                        whiteFronts.add(d);
                    } else if (whiteFronts.add(d)) {
                        if (d == whiteInitDirection.getLeftForward() || d == whiteInitDirection.getRightForward()) {
                            whiteDirectionBonus += NEXT_BONUS;
                        }
                        if (d == whiteInitDirection.getLeftBack() || d == whiteInitDirection.getRightBack()) {
                            whiteDirectionBonus += NEXT_NEXT_BONUS;
                        }
                        if (d == whiteInitDirection.getOpposite()) {
                            whiteDirectionBonus += BACK_BONUS;
                        }
                    }
                }
                whiteStrength += f.strength;
                if (f.isUnit) {
                    Unit u = (Unit) f;
                    addUnitToFight(u);
                } else {
                    for (Unit u : f.battalions) {
                        addUnitToFight(u);
                    }
                    for (Unit u : f.squadrons) {
                        addUnitToFight(u);
                    }
                    for (Unit u : f.batteries) {
                        addUnitToFight(u);
                    }
                }
            }
        }
        for (Force f : hex.blackForces) {
            if (!blackAdvantage && f.morale < 0) {
                blackImprisoned += f.strength;
                f.surrender();
            } else {
                black.put(f, f.strength);
                if (f.order.frontDirection != null) {
                    Direction d = f.order.frontDirection;
                    if (blackFronts.isEmpty()) {
                        blackInitDirection = d;
                        blackFronts.add(d);
                    } else if (blackFronts.add(d)) {
                        if (d == blackInitDirection.getLeftForward() || d == blackInitDirection.getRightForward()) {
                            blackDirectionBonus += NEXT_BONUS;
                        }
                        if (d == blackInitDirection.getLeftBack() || d == blackInitDirection.getRightBack()) {
                            whiteDirectionBonus += NEXT_NEXT_BONUS;
                        }
                        if (d == blackInitDirection.getOpposite()) {
                            blackDirectionBonus += BACK_BONUS;
                        }
                    }
                }
                blackStrength += f.strength;
                if (f.isUnit) {
                    Unit u = (Unit) f;
                    addUnitToFight(u);
                } else {
                    for (Unit u : f.battalions) {
                        addUnitToFight(u);
                    }
                    for (Unit u : f.squadrons) {
                        addUnitToFight(u);
                    }
                    for (Unit u : f.batteries) {
                        addUnitToFight(u);
                    }
                }
            }
        }
        if (hex.whiteForces.isEmpty()) {
            winner = -1;
            isOver = true;
        }
        if (hex.blackForces.isEmpty()) {
            winner = 1;
            isOver = true;
        }
    }

    private void addUnitToFight(Unit u) {
        if (u.nation.color == WHITE) {
            whiteFire += u.fire * hex.getFireFactor(u);
            whiteCharge += u.charge * hex.getChargeFactor(u);
            whiteUnits.add(u);
            switch(u.type) {
                case INFANTRY: whiteBattalions++;
                break;
                case CAVALRY: whiteSquadrons++;
                break;
                case ARTILLERY: whiteBatteries++;
                break;
            }
        } else {
            blackFire += u.fire * hex.getFireFactor(u);
            blackCharge += u.charge * hex.getChargeFactor(u);
            blackUnits.add(u);
            switch(u.type) {
                case INFANTRY: blackBattalions++;
                    break;
                case CAVALRY: blackSquadrons++;
                    break;
                case ARTILLERY: blackBatteries++;
                    break;
            }
        }
    }

    private Force getEnemyRandomForce(Force force) {
        Set<Force> enemy = null;
        enemy = force.nation.color == WHITE ? black.keySet() : white.keySet();
        if (!enemy.isEmpty()) {
            int num = (int) (Math.random() * enemy.size());
            for (Force f : enemy) {
                if (--num < 0) return f;
            }
        }
        return null;
    }

    private int pursuit(Unit unit) {
        int prisoners = 0;
        int catching = 0;
        double circlingFactor = whiteDirectionBonus - blackDirectionBonus;
        if (unit.nation.color == WHITE) {
            catching = (int) (PURSUIT_CHARGE * blackCharge * (1 - circlingFactor) * unit.strength / (whiteStrength + whiteDisordered));
            System.out.println("Pursuiting " + unit + " charge: " + blackCharge + " strength " + whiteStrength + " disordered " + whiteDisordered);
            if (unit.type == ARTILLERY) catching *= PURSUIT_ARTILLERY_FACTOR;
            if (unit.type == CAVALRY) catching *= PURSUIT_CAVALRY_FACTOR;
            if (catching >= unit.strength) {
                whiteImprisoned += unit.strength;
                System.out.println("IMPRISONED COMPLETELY!!!: catching - " + catching + " " + unit.nation + " " + unit.strength);
                unit.surrender();
            } else {
                double ratio = catching / unit.strength;
                whiteImprisoned += catching;
                System.out.println("Imprisoned: " + unit.nation + " " + catching);
                unit.bearLoss(ratio);
            }
        }
        if (unit.nation.color == BLACK) {
            catching = (int) (PURSUIT_CHARGE * whiteCharge * (1 + circlingFactor) * unit.strength / (blackStrength + blackDisordered));
            System.out.println("Pursuiting " + unit + " charge: " + whiteCharge + " strength " + blackStrength + " disordered " + blackDisordered);
            if (unit.type == ARTILLERY) catching *= PURSUIT_ARTILLERY_FACTOR;
            if (unit.type == CAVALRY) catching *= PURSUIT_CAVALRY_FACTOR;
            if (catching >= unit.strength) {
                blackImprisoned += unit.strength;
                System.out.println("IMPRISONED COMPLETELY!!!: catching - " + catching + " " + unit.nation + " " + unit.strength);
                unit.surrender();
            } else {
                double ratio = catching / unit.strength;
                blackImprisoned += catching;
                System.out.println("Imprisoned: " + unit.nation + " " + catching);
                unit.bearLoss(ratio);
            }
        }
        return prisoners;
    }

    private boolean onlyWhiteBatteries() {
        return (whiteBatteries > 0 && whiteBattalions == 0 && whiteSquadrons == 0 && (blackBattalions > 0 || blackSquadrons > 0));
    }
    private boolean onlyBlackBatteries() {
        return (blackBatteries > 0 && blackBattalions == 0 && blackSquadrons == 0 && (whiteBattalions > 0 || whiteSquadrons > 0));
    }
    private boolean onlyBatteries() {
        return (whiteBatteries > 0 && whiteBattalions == 0 && whiteSquadrons == 0 && blackBatteries > 0 && blackBattalions == 0 && blackSquadrons == 0);
    }
    //mistake

    public int pursuitRetreaters(Force force) {
        int imprisoned = 0;

        double pursuitCharge = 0;
        if (force.nation.color == WHITE) {
            pursuitCharge = blackCharge * force.strength / whiteStrength - force.charge;
            if(pursuitCharge > 0) {
                int prisoners = (int)(pursuitCharge * PURSUIT_ON_RETREATER);
                if (prisoners > force.strength) {
                    imprisoned = force.strength;
                    whiteDisordered += force.strength;
                    whiteStrength -= force.strength;
                    Array<Unit> units = new Array<Unit>();

                    for (Unit u: force.battalions) {
                        units.add(u);
                    }
                    for (Unit u: force.squadrons) {
                        units.add(u);
                    }
                    for (Unit u: force.batteries) {
                        units.add(u);
                    }
                    for(Unit u: units) {
                        whiteUnits.removeValue(u, true);
                        whiteImprisoned += u.surrender();
                    }
                }
                else {
                    //imprisoned = prisoners;
                    double ratio = (double)prisoners / force.strength;
                    for (Unit u: force.battalions) {
                        imprisoned += u.bearLoss(ratio);
                    }
                    for (Unit u: force.squadrons) {
                        imprisoned += u.bearLoss(ratio);
                    }
                    for (Unit u: force.batteries) {
                        imprisoned +=u.bearLoss(ratio);
                    }
                    whiteImprisoned += imprisoned;
                    whiteDisordered += imprisoned;
                    whiteStrength -= imprisoned;
                }
            }
        }
        if (force.nation.color == BLACK) {
            pursuitCharge = whiteCharge * force.strength / blackStrength - force.charge;
            if(pursuitCharge > 0) {
                int prisoners = (int)(pursuitCharge * PURSUIT_ON_RETREATER);
                if (prisoners > force.strength) {
                    imprisoned = force.strength;
                    blackDisordered += force.strength;
                    blackStrength -= force.strength;
                    Array<Unit> units = new Array<Unit>();
                    for (Unit u: force.battalions) {
                        units.add(u);
                    }
                    for (Unit u: force.squadrons) {
                        units.add(u);
                    }
                    for (Unit u: force.batteries) {
                        units.add(u);
                    }
                    for (Unit u: units) {
                        blackUnits.removeValue(u, true);
                        blackImprisoned += u.surrender();
                    }
                }
                else {
                    //imprisoned = prisoners;
                    double ratio = (double)prisoners / force.strength;
                    for (Unit u: force.battalions) {
                        imprisoned += u.bearLoss(ratio);
                    }
                    for (Unit u: force.squadrons) {
                        imprisoned += u.bearLoss(ratio);
                    }
                    for (Unit u: force.batteries) {
                        imprisoned += u.bearLoss(ratio);
                    }
                    blackImprisoned += imprisoned;
                    blackDisordered += imprisoned;
                    blackStrength -= imprisoned;
                }
            }
        }

        return imprisoned;
    }

    public int resolveStage() {
        System.out.println("START STAGE " + ++stage);
        init();
        System.out.println("White Units: " + whiteUnits);
        System.out.println("Black Units: " + blackUnits);
        System.out.println(whiteBattalions + " " + whiteSquadrons + " " + whiteBatteries
        + " " + blackBattalions + " " + blackSquadrons + " " + blackBatteries);

        if(onlyBatteries()) {
            for(Force force: white.keySet()) force.retreat();
            for(Force force: black.keySet()) force.retreat();
            return 0;
        }
        if(onlyWhiteBatteries()) {
            for(Force force: white.keySet()) {
                force.retreat();
                pursuitRetreaters(force);

                System.out.println("WHITE: " + whiteUnits + " strength - " + whiteStrength + " casualties - " + whiteCasualties +
                        " imprisoned - " + whiteImprisoned + " routed - " + (whiteDisordered - whiteImprisoned));
                for (Force f : hex.whiteForces) System.out.println(f + " Morale - " + f.morale);
                System.out.println("BLACK: " + blackUnits + " strength - " + blackStrength + " casualties - " + blackCasualties +
                        " imprisoned - " + blackImprisoned + " routed - " + (blackDisordered - blackImprisoned));
            }
            return -1;
        }
        if(onlyBlackBatteries()) {
            for(Force force: black.keySet()) {
                force.retreat();
                pursuitRetreaters(force);
                System.out.println("WHITE: " + whiteUnits + " strength - " + whiteStrength + " casualties - " + whiteCasualties +
                        " imprisoned - " + whiteImprisoned + " routed - " + (whiteDisordered - whiteImprisoned));
                for (Force f : hex.whiteForces) System.out.println(f + " Morale - " + f.morale);
                System.out.println("BLACK: " + blackUnits + " strength - " + blackStrength + " casualties - " + blackCasualties +
                        " imprisoned - " + blackImprisoned + " routed - " + (blackDisordered - blackImprisoned));
            }
            return 1;
        }
        if (whiteStrength == 0 || blackStrength == 0) System.out.println("NO ENEMIES!");

        else {
            double circlingFactor = whiteDirectionBonus - blackDirectionBonus;

            double fireOnBlack = FIRE_ON_UNIT * whiteFire / blackStrength;
            System.out.println("Fire on black " + fireOnBlack);
            double fireOnWhite = FIRE_ON_UNIT * blackFire / whiteStrength;
            System.out.println("Fire on white " + fireOnWhite);
            //double chargeOnBlack = -(CASUALITY_INTO_MORALE * fireOnBlack + CHARGE_ON_ENEMY * whiteCharge / blackStrength);
            double chargeOnBlack = -(CHARGE_ON_ENEMY * whiteCharge / blackStrength);
            System.out.println("Charge on black " + chargeOnBlack);

            //double chargeOnWhite = -(CASUALITY_INTO_MORALE * fireOnWhite + CHARGE_ON_ENEMY * blackCharge / whiteStrength);
            double chargeOnWhite = -(CHARGE_ON_ENEMY * blackCharge / whiteStrength);
            System.out.println("Charge on white " + chargeOnWhite);

            whiteFire = 0;
            whiteCharge = 0;
            blackFire = 0;
            blackCharge = 0;
            whiteStrength = 0;
            blackStrength = 0;

            for (Unit u : whiteUnits) {
                u.fire(1);
                double randomFactor = 0.7 + 0.6 * random.nextDouble();
                int casualties = hitUnit(u, randomFactor * fireOnWhite * hex.getFireDefenseFactor(u),
                        randomFactor * chargeOnWhite * hex.getChargeDefenseFactor(u) * (1 - circlingFactor));
                System.out.println("Unit: " + u.type + "  " + u.nation + " " + u + " White casualties: " + casualties + " unit morale: " + u.morale
                + " Random factor = " + randomFactor);
                whiteCasualties += casualties;
                if (u != null && u.morale < MIN_MORALE) {
                    whiteRouted.add(u);
                    u.isDisordered = true;
                    whiteDisordered += u.strength;
                    Unit enemy = getEnemyRandomForce(u).selectRandomUnit();
                    if (enemy != null) {
                        enemy.changeMorale(MORALE_BONUS);
                    }
                    if (u.isSub) {
                        for (Force force : u.superForce.forces) {
                            if (force.isUnit && force != u) {
                                ((Unit) force).changeMorale(MORALE_PENALTY);
                                if (force.morale <= MIN_MORALE) {
                                    whiteRouted.add((Unit) force);
                                    ((Unit) force).isDisordered = true;
                                    Unit e = getEnemyRandomForce(force).selectRandomUnit();
                                    if (e != null) {
                                        e.changeMorale(SMALL_MORALE_BONUS);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (Unit u : blackUnits) {
                u.fire(1);
                double randomFactor = 0.7 + 0.6 * random.nextDouble();
                int casualties = hitUnit(u, randomFactor * fireOnBlack * hex.getFireDefenseFactor(u),
                        randomFactor * chargeOnBlack * hex.getChargeDefenseFactor(u) * (1 + circlingFactor));
                System.out.println("Unit: " + u.type + " " + u.nation + " " + u + " Black casualties: " + casualties + " Unit morale: " + u.morale
                + " Random factor = " + randomFactor);
                blackCasualties += casualties;
                if (u != null && u.morale < MIN_MORALE) {
                    blackRouted.add(u);
                    u.isDisordered = true;
                    blackDisordered += u.strength;
                    Unit enemy = getEnemyRandomForce(u).selectRandomUnit();
                    if (enemy != null) {
                        enemy.changeMorale(MORALE_BONUS);
                    }
                    if (u.isSub) {
                        for (Force force : u.superForce.forces) {
                            if (force.isUnit && force != u) {
                                ((Unit) force).changeMorale(MORALE_PENALTY);
                                if (force.morale <= MIN_MORALE) {
                                    blackRouted.add((Unit) force);
                                    ((Unit) force).isDisordered = true;
                                    Unit e = getEnemyRandomForce(force).selectRandomUnit();
                                    if (e != null) {
                                        e.changeMorale(SMALL_MORALE_BONUS);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (Unit u: whiteUnits) {
                if (!u.isDisordered) {
                    whiteFire += u.fire * hex.getFireFactor(u);
                    whiteCharge += u.charge * hex.getChargeFactor(u);
                    whiteStrength += u.strength;
                }
            }
            for (Unit u: blackUnits) {
                if (!u.isDisordered) {
                    blackFire += u.fire * hex.getFireFactor(u);
                    blackCharge += u.charge * hex.getChargeFactor(u);
                    blackStrength += u.strength;
                }
            }
            for (Unit unit : whiteRouted) {
                if (unit.isDisordered) {
                    if (!unit.isSub) white.remove(unit);
                    whiteUnits.removeValue(unit, true);
                    unit.route();
                    pursuit((unit));
                    if (unit != null) {
                        unit.isDisordered = false;
                    }
                }
            }

            for (Unit unit : blackRouted) {
                if (unit.isDisordered) {
                    if (!unit.isSub) black.remove(unit);
                    blackUnits.removeValue(unit, true);
                    unit.route();
                    pursuit((unit));
                    if (unit != null) {
                        unit.isDisordered = false;
                    }
                }
            }

            //Retreating phase

        }
        return winner;

    }

    public void resolve() {
        resolveStage();
        double whiteAmmo = 0;
        for (Force w : hex.whiteForces) {
            whiteAmmo += w.ammoStock;
        }

        double blackAmmo = 0;
        for (Force b : hex.blackForces) {
            blackAmmo += b.ammoStock;
        }
        System.out.println("WHITE: " + whiteUnits + " strength - " + whiteStrength + " casualties - " + whiteCasualties +
                " imprisoned - " + whiteImprisoned + " routed - " + (whiteDisordered - whiteImprisoned) + " ammo stock - " + whiteAmmo);
        for (Force f : hex.whiteForces) System.out.println(f + " Morale - " + f.morale);
        System.out.println("BLACK: " + blackUnits + " strength - " + blackStrength + " casualties - " + blackCasualties +
                " imprisoned - " + blackImprisoned + " routed - " + (blackDisordered - blackImprisoned) + " ammo stock - " + blackAmmo);
        for (Force f : hex.blackForces) System.out.println(f + " Morale - " + f.morale);

        if (winner != 0) System.out.println("WINNER = " + winner);

    }

    private int firing(Unit unit, double fire) {
        int in = unit.strength;
        double f = fire;
        if (unit.type == ARTILLERY) {
            f *= FIRE_ON_ARTILLERY;
        }
        unit.bearLoss(f);
        int out = unit.strength;
        int casualties = in - out;
        if (unit.strength <= MIN_SOLDIERS) {
            unit.surrender();
        }
        else {
            unit.changeMorale(-(CASUALITY_INTO_MORALE) * f);
        }
        return casualties;
    }
    private void charging(Unit unit, double charge) {
        double c = charge;
        if (unit.type == ARTILLERY) c *= CHARGE_ON_ARTILLERY;
        if (unit.type == CAVALRY) c *= CHARGE_ON_CAVALRY;
        unit.changeMorale(c);
    }

    public int hitUnit(Unit unit, double fire, double charge) {
        /*int in = unit.strength;
        double f = fire;
        double c = charge;
        if (unit.type == ARTILLERY) {
            f *= FIRE_ON_ARTILLERY;
            c *= CHARGE_ON_ARTILLERY;
        }
        if (unit.type == CAVALRY) {
            c *= CHARGE_ON_CAVALRY;
        }
        System.out.println("Inside hitUnit: unit type - " + unit.type + " fire - " + f + " charge - " + c);
        unit.bearLoss(f);
        int out = unit.strength;
        unit.changeMorale(c);


        return in - out;*/
        charging(unit, charge);
        return firing(unit, fire);

    }


}


