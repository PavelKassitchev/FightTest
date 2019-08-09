package com.pavka;

import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static com.pavka.Nation.BLACK;
import static com.pavka.Nation.WHITE;
import static com.pavka.Unit.*;
import static com.pavka.Unit.ARTILLERY;

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
    HashSet<Unit> whiteUnits;
    HashSet<Unit> blackUnits;
    HashSet<Direction> whiteFronts;
    HashSet<Direction> blackFronts;
    HashSet<Unit> whiteRouted;
    HashSet<Unit> blackRouted;

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
    double whiteDirectionBonus;
    double blackDirectionBonus;
    int whiteBattalions;
    int whiteSquadrons;
    int whiteBatteries;
    int whiteWagons;
    int blackBattalions;
    int blackSquadrons;
    int blackBatteries;
    int blackWagons;
    double scale;
    int stage;
    int winner;
    Random random;
    boolean isOver;

    public Fighting(Hex h) {
        hex = h;
        random = random;

        white = new HashMap<Force, Integer>();
        black = new HashMap<Force, Integer>();
        whiteUnits = new HashSet<Unit>();
        blackUnits = new HashSet<Unit>();
        whiteRouted = new HashSet<Unit>();
        blackRouted = new HashSet<Unit>();
        whiteFronts = new HashSet<Direction>();
        blackFronts = new HashSet<Direction>();

        for (Force force: hex.whiteForces) {
            whiteInitStrength += force.strength;
        }
        for (Force force: hex.blackForces) {
            blackInitStrength += force.strength;
        }

        whiteStrength = whiteInitStrength;
        blackStrength = blackInitStrength;
    }

    private void clear() {
        whiteFire = 0;
        whiteCharge = 0;
        blackFire = 0;
        blackCharge = 0;
        whiteStrength = 0;
        blackStrength = 0;
        whiteDirectionBonus = 0;
        blackDirectionBonus  = 0;
        whiteBattalions = 0;
        whiteSquadrons = 0;
        whiteBatteries = 0;
        whiteWagons = 0;
        blackBattalions = 0;
        blackSquadrons = 0;
        blackBatteries = 0;
        blackWagons = 0;
        whiteFronts = new HashSet<Direction>();
        blackFronts = new HashSet<Direction>();
        whiteUnits = new HashSet<Unit>();
        blackUnits = new HashSet<Unit>();
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

    public void init() {
        scale = 1;

        boolean whiteAdvantage = whiteStrength > blackStrength;
        boolean blackAdvantage = whiteStrength < blackStrength;

        clear();

        Array<Force> whiteBroken = new Array<Force>();
        Array<Force> blackBroken = new Array<Force>();

        Direction whiteInitDirection = null;
        Direction blackInitDirection = null;

        for (Force f: hex.whiteForces) {
            if (blackAdvantage && f.morale < 0) {
                whiteBroken.add(f);
            }
            else {
                if (!white.containsKey(f)) white.put(f, f.strength);
                //TODO check back hex property
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
                blackBroken.add(f);
            }
            else {
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

        if (blackAdvantage) {
            for (Force force: whiteBroken) whiteImprisoned += force.surrender();
        }
        if (whiteAdvantage) {
            for (Force force: blackBroken) blackImprisoned += force.surrender();
        }

        if (white.isEmpty()) {
            winner = -1;
            isOver = true;
        }
        if (black.isEmpty()) {
            winner = 1;
            isOver = true;
        }

    }

    public void fight() {
        if (!isOver) {
            double circlingFactor = whiteDirectionBonus - blackDirectionBonus;

            double fireOnBlack = FIRE_ON_UNIT * whiteFire / blackStrength;
            //System.out.println("Fire on black " + fireOnBlack);
            double fireOnWhite = FIRE_ON_UNIT * blackFire / whiteStrength;
            //System.out.println("Fire on white " + fireOnWhite);
            double chargeOnBlack = -(CHARGE_ON_ENEMY * whiteCharge / blackStrength);
            //System.out.println("Charge on black " + chargeOnBlack);
            double chargeOnWhite = -(CHARGE_ON_ENEMY * blackCharge / whiteStrength);

            for (Unit u: whiteUnits) {
                u.fire(1/scale);
                double randomFactor = 0.7 + 0.6 * random.nextDouble();
                int casualties = hitUnit(u, randomFactor * fireOnWhite * hex.getFireDefenseFactor(u) / scale,
                        randomFactor * chargeOnWhite * hex.getChargeDefenseFactor(u) * (1 - circlingFactor) / scale);
                whiteCasualties += casualties;
                if (u != null && (u.morale < MIN_MORALE) || u.strength <= MIN_SOLDIERS) {
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
                u.fire(1 / scale);
                double randomFactor = 0.7 + 0.6 * random.nextDouble();
                int casualties = hitUnit(u, randomFactor * fireOnBlack * hex.getFireDefenseFactor(u) / scale,
                        randomFactor * chargeOnBlack * hex.getChargeDefenseFactor(u) * (1 + circlingFactor) / scale);
                blackCasualties += casualties;
                if (u != null && u.morale < MIN_MORALE || u.strength <= MIN_SOLDIERS) {
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

            for (Unit unit : whiteRouted) {
                if (unit.isDisordered) {
                    if (!unit.isSub) white.remove(unit);
                    whiteUnits.remove(unit);
                    int imprisoned  = pursuit((unit));
                    whiteImprisoned += imprisoned;
                    whiteDisordered -= imprisoned;
                    if (unit.strength <= MIN_SOLDIERS) {
                        int s = unit.surrender();
                        whiteImprisoned += s;
                        whiteDisordered -= s;

                    }
                    if (unit != null) {
                        unit.isDisordered = false;
                        unit.route();
                    }
                }
            }

            for (Unit unit : blackRouted) {
                if (unit.isDisordered) {
                    if (!unit.isSub) black.remove(unit);
                    blackUnits.remove(unit);
                    int imprisoned  = pursuit((unit));
                    blackImprisoned += imprisoned;
                    blackDisordered -= imprisoned;
                    if (unit.strength <= MIN_SOLDIERS) {
                        int s = unit.surrender();
                        blackImprisoned += s;
                        blackDisordered -= s;
                    }
                    if (unit != null) {
                        unit.isDisordered = false;
                        unit.route();
                    }
                }
            }

        }

    }

    private int pursuit(Unit unit) {
        int prisoners = 0;
        int catching = 0;
        double circlingFactor = whiteDirectionBonus - blackDirectionBonus;
        if (unit.nation.color == WHITE) {
            catching = (int) (PURSUIT_CHARGE * blackCharge * (1 - circlingFactor) * unit.strength / whiteStrength);
            if (unit.type == ARTILLERY) catching *= PURSUIT_ARTILLERY_FACTOR;
            if (unit.type == CAVALRY) catching *= PURSUIT_CAVALRY_FACTOR;
            if (catching >= unit.strength) {
                prisoners += unit.surrender();
            } else {
                double ratio = catching / unit.strength;
                prisoners += unit.bearLoss(ratio);
            }
        }
        if (unit.nation.color == BLACK) {
            catching = (int) (PURSUIT_CHARGE * whiteCharge * (1 + circlingFactor) * unit.strength / blackStrength);
            if (unit.type == ARTILLERY) catching *= PURSUIT_ARTILLERY_FACTOR;
            if (unit.type == CAVALRY) catching *= PURSUIT_CAVALRY_FACTOR;
            if (catching >= unit.strength) {
                prisoners += unit.surrender();
            } else {
                double ratio = catching / unit.strength;
                prisoners += unit.bearLoss(ratio);
            }
        }
        return prisoners;
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
    private boolean onlyWhiteBatteries() {
        return (whiteBatteries > 0 && whiteBattalions == 0 && whiteSquadrons == 0 && (blackBattalions > 0 || blackSquadrons > 0));
    }
    private boolean onlyBlackBatteries() {
        return (blackBatteries > 0 && blackBattalions == 0 && blackSquadrons == 0 && (whiteBattalions > 0 || whiteSquadrons > 0));
    }
    private boolean onlyBatteries() {
        return (whiteBatteries > 0 && whiteBattalions == 0 && whiteSquadrons == 0 && blackBatteries > 0 && blackBattalions == 0 && blackSquadrons == 0);
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
        unit.changeMorale(-(CASUALITY_INTO_MORALE) * f);

        return casualties;
    }
    private void charging(Unit unit, double charge) {
        double c = charge;
        if (unit.type == ARTILLERY) c *= CHARGE_ON_ARTILLERY;
        if (unit.type == CAVALRY) c *= CHARGE_ON_CAVALRY;
        unit.changeMorale(c);
    }

    private int hitUnit(Unit unit, double fire, double charge) {

        charging(unit, charge);
        return firing(unit, fire);

    }

}
