package com.pavka;

import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

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

    }
}
