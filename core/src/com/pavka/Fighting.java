package com.pavka;

import com.badlogic.gdx.utils.Array;

import java.util.*;

import static com.pavka.Nation.BLACK;
import static com.pavka.Nation.WHITE;
import static com.pavka.Unit.*;


public class Fighting {
    public static final int FIRE_ON_UNIT = 40;
    public static final double CASUALITY_INTO_MORALE = 3.3;
    public static final int CHARGE_ON_ENEMY = 30;
    public static final int PURSUIT_CHARGE = 50;
    public static final int PURSUIT_ON_RETREATER = 35;
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
    HashSet<Force> whiteRetreaters;
    HashSet<Force> blackRetreaters;

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
    boolean whiteLosing;
    boolean blackLosing;

    public Fighting(Hex h) {
        hex = h;
        random = new Random();

        white = new HashMap<Force, Integer>();
        black = new HashMap<Force, Integer>();
        whiteUnits = new HashSet<Unit>();
        blackUnits = new HashSet<Unit>();
        whiteRouted = new HashSet<Unit>();
        blackRouted = new HashSet<Unit>();
        whiteFronts = new HashSet<Direction>();
        blackFronts = new HashSet<Direction>();
        whiteRetreaters = new HashSet<Force>();
        blackRetreaters = new HashSet<Force>();

        for (Force force : hex.whiteForces) {
            whiteInitStrength += force.strength;
        }
        for (Force force : hex.blackForces) {
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
        blackDirectionBonus = 0;
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
            switch (u.type) {
                case INFANTRY:
                    whiteBattalions++;
                    break;
                case CAVALRY:
                    whiteSquadrons++;
                    break;
                case ARTILLERY:
                    whiteBatteries++;
                    break;
            }
        } else {
            blackFire += u.fire * hex.getFireFactor(u);
            blackCharge += u.charge * hex.getChargeFactor(u);
            blackUnits.add(u);
            switch (u.type) {
                case INFANTRY:
                    blackBattalions++;
                    break;
                case CAVALRY:
                    blackSquadrons++;
                    break;
                case ARTILLERY:
                    blackBatteries++;
                    break;
            }
        }
    }

    public void init() {
        scale = 1;

        //System.out.println("INIT. Stage " + stage);
        System.out.println();

        boolean whiteAdvantage = whiteStrength > blackStrength;
        boolean blackAdvantage = whiteStrength < blackStrength;

        clear();

        Array<Force> whiteBroken = new Array<Force>();
        Array<Force> blackBroken = new Array<Force>();

        Direction whiteInitDirection = null;
        Direction blackInitDirection = null;

        for (Force f : hex.whiteForces) {
            if (blackAdvantage && f.morale <= 0) {
                whiteBroken.add(f);
            } else {
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
            if (whiteAdvantage && f.morale <= 0) {
                blackBroken.add(f);
            } else {
                if (!black.containsKey(f)) black.put(f, f.strength);
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
            for (Force force : whiteBroken) whiteImprisoned += force.surrender();
        }
        if (whiteAdvantage) {
            for (Force force : blackBroken) blackImprisoned += force.surrender();
        }

        if (white.isEmpty()) {
            winner = -1;
            isOver = true;
        }
        if (black.isEmpty()) {
            winner = 1;
            isOver = true;
        }

        if (onlyBatteries()) {
            for (Force force : white.keySet()) whiteRetreaters.add(force);
            for (Force force : black.keySet()) blackRetreaters.add(force);
            isOver = true;
        }
        if (onlyWhiteBatteries()) {
            for (Force force : white.keySet()) whiteRetreaters.add(force);
            isOver = true;
            winner = -1;
        }
        if (onlyBlackBatteries()) {
            for (Force force : black.keySet()) blackRetreaters.add(force);
            isOver = true;
            winner = 1;
        }
        System.out.println("INIT. NUMBER OF STAGE: " + stage);
        System.out.println();
        System.out.println("WHITE: current strength - " + whiteStrength + " killed - " + whiteCasualties + " imprisoned - " + whiteImprisoned);
        System.out.println("White Units Length = " + whiteUnits.size());
        System.out.println("White Fire = " + whiteFire + " WhiteCharge = " + whiteCharge);

        System.out.println();
        System.out.println("BLACK: current strength - " + blackStrength + " killed - " + blackCasualties + " imprisoned - " + blackImprisoned);
        System.out.println("Black Units Length = " + blackUnits.size());
        System.out.println("Black Fire = " + blackFire + " Black Charge = " + blackCharge);

        System.out.println();

    }

    public void fight() {

        if (!isOver) {
            int w = 0;
            int b = 0;
            HashSet<Unit> whiteShaken = new HashSet<Unit>();
            HashSet<Unit> blackShaken = new HashSet<Unit>();


            double circlingFactor = whiteDirectionBonus - blackDirectionBonus;
            //System.out.println("Circling: " + circlingFactor + " Stage: " + stage);
            double fireOnBlack = FIRE_ON_UNIT * whiteFire / blackStrength;
            //System.out.println("Fire on black " + fireOnBlack);
            double fireOnWhite = FIRE_ON_UNIT * blackFire / whiteStrength;
            //System.out.println("Fire on white " + fireOnWhite);
            double chargeOnBlack = -(CHARGE_ON_ENEMY * whiteCharge / blackStrength);
            //System.out.println("Charge on black " + chargeOnBlack);
            double chargeOnWhite = -(CHARGE_ON_ENEMY * blackCharge / whiteStrength);
            //System.out.println("Charge on white " + chargeOnWhite);

            int min = Math.min(whiteUnits.size(), blackUnits.size());
            if (min == 0) min = 1;
            int whiteStep = whiteUnits.size() / min;
            int blackStep = blackUnits.size() / min;

            //for (Unit u : whiteUnits) {
            for (int i = 0; i < whiteStep; i++) {
                Unit u = getRandomForce(WHITE).selectRandomUnit();
                u.fire(1 / scale);
                double randomFactor = 0.65 + 0.7 * random.nextDouble();
                //double randomFactor = 1;
                //System.out.println(randomFactor + " random");
                int casualties = hitUnit(u, randomFactor * fireOnWhite * hex.getFireDefenseFactor(u) / scale,
                        randomFactor * chargeOnWhite * hex.getChargeDefenseFactor(u) * (1 - circlingFactor) / scale);
                //System.out.println("White casualties: " + casualties + " morale: " + u.morale);
                whiteCasualties += casualties;
                if (u.morale < MIN_MORALE || u.strength <= MIN_SOLDIERS) {
                    if (whiteShaken.add(u)) {
                        //whiteDisordered += u.strength;
                        w++;
                        u.isDisordered = true;
                        System.out.println("White unit BROKEN: " + u.strength);
                    }
                    /*Unit enemy = getEnemyRandomForce(u).selectRandomUnit();
                    if (enemy != null) {
                        enemy.changeMorale(MORALE_BONUS);
                    }*/
                    if (u.isSub) {
                        for (Force force : u.superForce.forces) {
                            if (force.isUnit && force != u) {
                                ((Unit) force).changeMorale(MORALE_PENALTY);
                                if (force.morale <= MIN_MORALE) {
                                    if (whiteShaken.add((Unit) force)) {
                                        //whiteDisordered += u.strength;
                                        w++;
                                        ((Unit) force).isDisordered = true;
                                        System.out.println("White Unit broken");
                                    }
                                    //whiteDisordered += force.strength;
                                    /*Unit e = getEnemyRandomForce(force).selectRandomUnit();
                                    if (e != null) {
                                        e.changeMorale(SMALL_MORALE_BONUS);
                                    }*/
                                }
                            }
                        }
                    }
                }
            }
            //for (Unit u : blackUnits) {
            for (int i = 0; i < blackStep; i++) {
                Unit u = getRandomForce(BLACK).selectRandomUnit();
                u.fire(1 / scale);
                double randomFactor = 0.65 + 0.7 * random.nextDouble();
                //double randomFactor = 1;
                //System.out.println(randomFactor + " random");
                int casualties = hitUnit(u, randomFactor * fireOnBlack * hex.getFireDefenseFactor(u) / scale,
                        randomFactor * chargeOnBlack * hex.getChargeDefenseFactor(u) * (1 + circlingFactor) / scale);
                //System.out.println("Black casualties: " + casualties + " morale: " + u.morale);
                blackCasualties += casualties;
                if (u.morale < MIN_MORALE || u.strength <= MIN_SOLDIERS) {
                    if (blackShaken.add(u)) {
                        //blackDisordered += u.strength;
                        b++;
                        u.isDisordered = true;
                        System.out.println("Black unit BROKEN: " + u.strength);
                    }

                    /*Unit enemy = getEnemyRandomForce(u).selectRandomUnit();
                    if (enemy != null) {
                        enemy.changeMorale(MORALE_BONUS);
                    }*/
                    if (u.isSub) {
                        for (Force force : u.superForce.forces) {
                            if (force.isUnit && force != u) {
                                ((Unit) force).changeMorale(MORALE_PENALTY);
                                if (force.morale <= MIN_MORALE) {
                                    if (blackShaken.add((Unit) force)) {
                                        //blackDisordered += u.strength;
                                        b++;
                                        ((Unit) force).isDisordered = true;
                                        System.out.println("Black Unit broken");
                                    }
                                    //blackDisordered += force.strength;
                                    /*Unit e = getEnemyRandomForce(force).selectRandomUnit();
                                    if (e != null) {
                                        e.changeMorale(SMALL_MORALE_BONUS);
                                    }*/
                                }
                            }
                        }
                    }
                }
            }
            for (int i = 0; i < w; i++) {
                Unit u = getRandomForce(BLACK).selectRandomUnit();
                if (u != null) u.changeMorale(MORALE_BONUS);
            }
            for (int i = 0; i < b; i++) {
                Unit u = getRandomForce(WHITE).selectRandomUnit();
                if (u != null) u.changeMorale(MORALE_BONUS);
            }


            for (Unit unit : whiteShaken) {
                if (unit.morale < MIN_MORALE) {
                    if (whiteUnits.size() > 1) {
                        if (!unit.isSub) white.remove(unit);
                        else unit.superForce.detach(unit);
                        whiteUnits.remove(unit);
                        int imprisoned = pursuit((unit));
                        whiteImprisoned += imprisoned;
                        whiteDisordered += unit.strength;
                        if (unit.strength <= MIN_SOLDIERS) {
                            int s = unit.surrender();
                            whiteImprisoned += s;
                            whiteDisordered -= s;

                        }
                        if (unit != null) {
                            unit.isDisordered = false;
                            whiteRouted.add(unit);
                            unit.route();
                        }

                    } else whiteLosing = true;
                }
            }
            for (Unit unit : blackShaken) {
                if (unit.morale < MIN_MORALE) {
                    if (blackUnits.size() > 1) {
                        if (!unit.isSub) black.remove(unit);
                        else unit.superForce.detach(unit);
                        blackUnits.remove(unit);
                        int imprisoned = pursuit((unit));
                        blackImprisoned += imprisoned;
                        blackDisordered += unit.strength;
                        if (unit.strength <= MIN_SOLDIERS) {
                            int s = unit.surrender();
                            blackImprisoned += s;
                            blackDisordered -= s;
                        }
                        if (unit != null) {
                            unit.isDisordered = false;
                            blackRouted.add(unit);
                            unit.route();
                        }
                    } else blackLosing = true;
                }
            }

            whiteStrength = 0;
            blackStrength = 0;

            for (Force force : hex.whiteForces) {
                if (force.strength == 0) white.remove(force);
                else whiteStrength += force.strength;
            }
            for (Force force : hex.blackForces) {
                if (force.strength == 0) black.remove(force);
                else blackStrength += force.strength;
            }

            if (whiteUnits.isEmpty() && !blackUnits.isEmpty()) {
                winner = -1;
                isOver = true;
            }
            if (!whiteUnits.isEmpty() && blackUnits.isEmpty()) {
                winner = 1;
                isOver = true;
            }
            if (whiteUnits.isEmpty() && blackUnits.isEmpty()) {

                isOver = true;
            }
        }
        System.out.println("FIGHT. STAGE " + stage);
        System.out.println("White Unit Length: " + whiteUnits.size() + " Units: " + whiteUnits);
        System.out.println("WHITE: initial strength - " + whiteInitStrength + " killed - " + whiteCasualties + " imprisoned - " + whiteImprisoned);
        System.out.println("White: " + white);
        System.out.println("Black Unit Length: " + blackUnits.size() + " Units: " + blackUnits);
        System.out.println("BLACK: initial strength - " + blackInitStrength + " killed - " + blackCasualties + " imprisoned - " + blackImprisoned);
        System.out.println("Black: " + black);
        System.out.println();

    }

    public void checkRetreat() {
        HashSet<Force> whiteToRetreat = new HashSet<Force>();
        HashSet<Force> blackToRetreat = new HashSet<Force>();

        if (!whiteLosing && !blackLosing) {

            for (Map.Entry<Force, Integer> set : white.entrySet()) {
                Force force = set.getKey();
                if (force.strength <= force.order.retreatLevel * set.getValue()) {
                    //whiteRetreaters.add(force);
                    whiteToRetreat.add(force);
                }
            }

            for (Map.Entry<Force, Integer> set : black.entrySet()) {
                Force force = set.getKey();
                if (force.strength <= force.order.retreatLevel * set.getValue()) {
                    //blackRetreaters.add(force);
                    blackToRetreat.add(force);
                }
            }

            if (whiteToRetreat.size() != white.size() && blackToRetreat.size() != black.size()) {

            }

            if (whiteToRetreat.size() == white.size() && blackToRetreat.size() != black.size()) {
                for (Force force : whiteToRetreat) {
                    whiteRetreaters.add(force);
                    white.remove(force);
                    whiteImprisoned += pursuitRetreaters(force);
                }
            }
            if (whiteToRetreat.size() != white.size() && blackToRetreat.size() == black.size()) {
                for (Force force : blackToRetreat) {
                    blackRetreaters.add(force);
                    black.remove(force);
                    blackImprisoned += pursuitRetreaters(force);
                }
            }
            if (whiteToRetreat.size() == white.size() && blackToRetreat.size() == black.size()) {
                int whites = 0;
                int blacks = 0;
                double whiteMorale = 0;
                double blackMorale = 0;
                for (Force force : whiteToRetreat) {
                    whiteMorale += force.morale * force.strength;
                    whites += force.strength;
                }
                for (Force force : blackToRetreat) {
                    blackMorale += force.morale * force.strength;
                    blacks += force.strength;
                }
                if (whiteMorale / whites > blackMorale / blacks) {
                    for (Force force : blackToRetreat) {
                        blackRetreaters.add(force);
                        black.remove(force);
                        blackImprisoned += pursuitRetreaters(force);
                    }
                }
                if (whiteMorale / whites < blackMorale / blacks) {
                    for (Force force : whiteToRetreat) {
                        whiteRetreaters.add(force);
                        white.remove(force);
                        whiteImprisoned += pursuitRetreaters(force);
                    }
                }
            }

            if (white.isEmpty() && !black.isEmpty()) {
                winner = -1;
                isOver = true;
            }
            if (black.isEmpty() && !white.isEmpty()) {
                winner = 1;
                isOver = true;
            }
            if (black.isEmpty() && white.isEmpty()) {
                isOver = true;
            }
        } else {
            if (whiteLosing && !blackLosing) {
                winner = -1;
                isOver = true;
                //for (Force force: white.keySet()) {
                //whiteRetreaters.add(force);
                //whiteImprisoned += pursuitRetreaters(force);
                for (Unit u : whiteUnits) {
                    u.isDisordered = false;
                    whiteRouted.add(u);
                    Force f = u.superForce;
                    u.route();
                    whiteImprisoned += pursuit(u);
                    whiteDisordered += u.strength;
                    f.surrender();
                    white.remove(f);

                }
            }
            if (!whiteLosing && blackLosing) {
                winner = 1;
                isOver = true;
                //for (Force force: black.keySet()) {
                //blackRetreaters.add(force);
                //blackImprisoned += pursuitRetreaters(force);
                for (Unit u : blackUnits) {
                    u.isDisordered = false;
                    blackRouted.add(u);
                    Force f = u.superForce;
                    u.route();
                    blackImprisoned += pursuit(u);
                    blackDisordered += u.strength;
                    f.surrender();
                    black.remove(f);
                }
            }
            if (whiteLosing && blackLosing) {
                isOver = true;
                double whiteMorale = 0;
                double blackMorale = 0;
                for (Unit w : whiteUnits) whiteMorale += w.morale;
                for (Unit b : blackUnits) blackMorale += b.morale;
                if (whiteMorale > blackMorale) {
                    winner = 1;

                    for (Force force : black.keySet()) {
                        blackRetreaters.add(force);
                        blackImprisoned += pursuitRetreaters(force);
                    }
                }
                if (whiteMorale < blackMorale) {
                    winner = -1;

                    for (Force force : white.keySet()) {
                        whiteRetreaters.add(force);
                        whiteImprisoned += pursuitRetreaters(force);
                    }
                }
            }

        }
        System.out.println("CHECK. STAGE " + stage);
        System.out.println("White Unit Length: " + whiteUnits.size() + " Units: " + whiteUnits);
        System.out.println("WHITE: initial strength - " + whiteInitStrength + " killed - " + whiteCasualties + " imprisoned - " + whiteImprisoned);
        System.out.println("White Broken = " + whiteDisordered + " Retreated = " + whiteToRetreat);
        System.out.println("White: " + white);
        System.out.println();
        System.out.println("Black Unit Length: " + blackUnits.size() + " Units: " + blackUnits);
        System.out.println("BLACK: initial strength - " + blackInitStrength + " killed - " + blackCasualties + " imprisoned - " + blackImprisoned);
        System.out.println("Black Broken = " + blackDisordered + " Retreated = " + blackToRetreat);
        System.out.println("Black: " + black);
        System.out.println();
    }

    public void obtainVictoryBonus() {
        if (winner == 1) {
            for (Unit unit : whiteUnits) unit.changeMorale(VICTORY_BONUS);
            for (Unit unit : whiteRouted) {
                unit.changeMorale(SMALL_VICTORY_BONUS);
                if (unit.morale >= 0) {
                    unit.moveTo(hex);
                    if (unit.formerSuper != null) {
                        unit.formerSuper.attach(unit);
                        whiteDisordered -= unit.strength;
                    }
                }
            }
        }
        if (winner == -1) {
            for (Unit unit : blackUnits) unit.changeMorale(VICTORY_BONUS);
            for (Unit unit : blackRouted) {
                unit.changeMorale(SMALL_VICTORY_BONUS);
                if (unit.morale >= 0) {
                    unit.moveTo(hex);
                    if (unit.formerSuper != null) {
                        unit.formerSuper.attach(unit);
                        blackDisordered -= unit.strength;
                    }
                }
            }
        }
    }

    public void resolve() {
        while (!isOver) {
            init();
            fight();
            checkRetreat();
            stage++;
        }
        obtainVictoryBonus();
        String victory = "VICTORY OF ";
        if (winner == 1) victory += "WHITE";
        if (winner == -1) victory += "BLACK";

        int whiteFinal = 0;
        int blackFinal = 0;
        int whiteRetreat = 0;
        int blackRetreat = 0;
        int whiteInHex = 0;
        int blackInHex = 0;
        for (Force f : white.keySet()) whiteFinal += f.strength;
        for (Force f : black.keySet()) blackFinal += f.strength;
        for (Force f : whiteRetreaters) whiteRetreat += f.strength;
        for (Force f : blackRetreaters) blackRetreat += f.strength;
        for (Force f : hex.whiteForces) whiteInHex += f.strength;
        for (Force f : hex.blackForces) blackInHex += f.strength;

        System.out.println(victory);
        System.out.println("NUMBER OF STAGES: " + stage);
        System.out.println();
        System.out.println("WHITE: initial strength - " + whiteInitStrength + " killed - " + whiteCasualties + " imprisoned - " + whiteImprisoned);
        System.out.println("WHITE: final strength - " + whiteFinal + " and " + whiteInHex + " retreated - " + whiteRetreat + " routed - " + whiteDisordered);
        System.out.println();
        System.out.println("BLACK: initial strength - " + blackInitStrength + " killed - " + blackCasualties + " imprisoned - " + blackImprisoned);
        System.out.println("BLACK: final strength - " + blackFinal + " and " + blackInHex + " retreated - " + blackRetreat + " routed - " + blackDisordered);
        System.out.println();
    }

    private int pursuitRetreaters(Force force) {
        int imprisoned = 0;
        double pursuitCharge = 0;
        if (force.nation.color == WHITE) {

            pursuitCharge = (blackCharge * force.strength / whiteStrength - force.charge) * (1 + blackDirectionBonus);
            if (pursuitCharge > 0) {
                int prisoners = (int) (pursuitCharge * PURSUIT_ON_RETREATER);
                if (prisoners > force.strength) {

                    Array<Unit> units = new Array<Unit>();

                    if (force.isUnit) {
                        units.add((Unit) force);
                    } else {
                        for (Unit u : force.battalions) {
                            units.add(u);
                        }
                        for (Unit u : force.squadrons) {
                            units.add(u);
                        }
                        for (Unit u : force.batteries) {
                            units.add(u);
                        }
                    }
                    for (Unit u : units) {
                        whiteUnits.remove(u);
                        imprisoned += u.surrender();
                    }
                } else {
                    //imprisoned = prisoners;
                    double ratio = (double) prisoners / force.strength;
                    if (force.isUnit) imprisoned += ((Unit) force).bearLoss(ratio);
                    else {
                        for (Unit u : force.battalions) {
                            imprisoned += u.bearLoss(ratio);
                        }
                        for (Unit u : force.squadrons) {
                            imprisoned += u.bearLoss(ratio);
                        }
                        for (Unit u : force.batteries) {
                            imprisoned += u.bearLoss(ratio);
                        }
                    }

                }
            }
        }
        if (force.nation.color == BLACK) {
            pursuitCharge = (whiteCharge * force.strength / blackStrength - force.charge) * (1 + whiteDirectionBonus);
            if (pursuitCharge > 0) {
                int prisoners = (int) (pursuitCharge * PURSUIT_ON_RETREATER);
                if (prisoners > force.strength) {

                    Array<Unit> units = new Array<Unit>();
                    if (force.isUnit) {
                        units.add((Unit) force);
                    } else {
                        for (Unit u : force.battalions) {
                            units.add(u);
                        }
                        for (Unit u : force.squadrons) {
                            units.add(u);
                        }
                        for (Unit u : force.batteries) {
                            units.add(u);
                        }
                    }
                    for (Unit u : units) {
                        blackUnits.remove(u);
                        imprisoned += u.surrender();
                    }
                } else {
                    //imprisoned = prisoners;
                    double ratio = (double) prisoners / force.strength;
                    if (force.isUnit) blackImprisoned += ((Unit) force).bearLoss(ratio);
                    for (Unit u : force.battalions) {
                        imprisoned += u.bearLoss(ratio);
                    }
                    for (Unit u : force.squadrons) {
                        imprisoned += u.bearLoss(ratio);
                    }
                    for (Unit u : force.batteries) {
                        imprisoned += u.bearLoss(ratio);
                    }

                }
            }
        }

        return imprisoned;
    }

    private int pursuit(Unit unit) {
        int prisoners = 0;
        int catching = 0;
        //double circlingFactor = whiteDirectionBonus - blackDirectionBonus;
        if (unit.nation.color == WHITE) {
            catching = (int) (PURSUIT_CHARGE * blackCharge * (1 + blackDirectionBonus) * unit.strength / whiteStrength);
            if (unit.type == ARTILLERY) catching *= PURSUIT_ARTILLERY_FACTOR;
            if (unit.type == CAVALRY) catching *= PURSUIT_CAVALRY_FACTOR;
            if (catching >= unit.strength) {
                prisoners += unit.surrender();
            } else {
                double ratio = (double) catching / unit.strength;
                prisoners += unit.bearLoss(ratio);
            }
        }
        if (unit.nation.color == BLACK) {
            catching = (int) (PURSUIT_CHARGE * whiteCharge * (1 + whiteDirectionBonus) * unit.strength / blackStrength);
            if (unit.type == ARTILLERY) catching *= PURSUIT_ARTILLERY_FACTOR;
            if (unit.type == CAVALRY) catching *= PURSUIT_CAVALRY_FACTOR;
            if (catching >= unit.strength) {
                prisoners += unit.surrender();
            } else {
                double ratio = (double) catching / unit.strength;
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

    private Force getRandomForce(int color) {
        Set<Force> forces = null;
        forces = color == WHITE ? white.keySet() : black.keySet();
        if (!forces.isEmpty()) {
            int num = (int) (Math.random() * forces.size());
            for (Force f : forces) {
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
