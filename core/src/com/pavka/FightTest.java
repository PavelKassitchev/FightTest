package com.pavka;

import static com.pavka.Nation.*;
import static com.pavka.Direction.*;

public class FightTest {
    static Hex hex = new Hex();

    private static Force createForce(Hex hex, Nation nation, int i, int c, int a) {
        int b = 1;
        int s = 1;
        int y = 1;

        Force force = new Force(nation, hex);
        force.name = "Corps " + nation;
        for (int count = 0; count < i; count++) {
            Unit u = new Battalion(nation, hex);
            u.name = b++ +". Battalion";

            force.attach(u);
        }
        for (int count = 0; count < c; count++) {
            Unit u =  new Squadron(nation, hex);
            u.name = s++ + ". Squadron";

            force.attach(u);
        }
        for (int count = 0; count < a; count++) {
            Unit u = new Battery(nation, hex);
            u.name = y++ + ". Battery";
            force.attach(u);
        }

        return force;
    }


    private static Force createForce(Nation nation, int i, int c, int a) {
        int b = 1;
        int s = 1;
        int y = 1;
        //Hex hex = new Hex();
        Force force = new Force(nation, hex);
        force.name = "Corps " + nation;
        for (int count = 0; count < i; count++) {
            Unit u = new Battalion(nation, hex);
            u.name = b++ +". Battalion";

            force.attach(u);
        }
        for (int count = 0; count < c; count++) {
            Unit u =  new Squadron(nation, hex);
            u.name = s++ + ". Squadron";

            force.attach(u);
        }
        for (int count = 0; count < a; count++) {
            Unit u = new Battery(nation, hex);
            u.name = y++ + ". Battery";
            force.attach(u);
        }

        return force;
    }
    public static void getStat(int fi, int fc, int fa, int ai, int ac, int aa) {
        int a = 0;
        int d = 0;
        int n = 0;
        int whiteKilled = 0;
        int blackKilled = 0;

        for (int i = 0; i < 100; i++) {
            Hex hex = new Hex();
            Force att = createForce(hex, FRANCE, fi, fc, fa);
            att.order = new Order(true, 0.7, 0);
            //hex.whiteForces.add(att);
            Force def = createForce(hex, AUSTRIA, ai, ac, aa);
            def.order = new Order(true, 0.7, 0);
            //hex.blackForces.add(def);


            //Fight fight = hex.startFight();
            Fighting fight = hex.startFighting();

            //Battle battle = new Battle(force1, force2);
            fight.resolve();

            /*if (att.morale > def.morale) a++;
            else if (att.morale < def.morale) d++;

            else n++;*/
            if(fight.winner == 1) a++;
            else if (fight.winner == -1) d++;
            else n++;
            whiteKilled += fight.whiteImprisoned;
            blackKilled += fight.blackImprisoned;
        }
        System.out.println("White wins = " + a + " Black wins = " + d + " Vague " + n);
        System.out.println("white killed: " + whiteKilled + " black killed: " + blackKilled);
    }

    public static void getStat(Force attacker, Force defender) {
        int a = 0;
        int d = 0;
        int n = 0;

        for (int i = 0; i < 100; i++) {
            Hex hex = new Hex();
            Force att = createForce(attacker.nation, attacker.battalions.size(), attacker.squadrons.size(), attacker.batteries.size());
            att.order = new Order(attacker.order.seekBattle, attacker.order.retreatLevel, 0);
            hex.whiteForces.add(att);
            Force def = createForce(defender.nation, defender.battalions.size(), defender.squadrons.size(), defender.batteries.size());
            def.order = new Order(defender.order.seekBattle, defender.order.retreatLevel, 0);
            hex.blackForces.add(def);


            Fight fight = hex.startFight();

            //Battle battle = new Battle(force1, force2);
            fight.resolve();

            /*if (att.morale > def.morale) a++;
            else
            if (att.morale < def.morale) d++;

            else n++;*/
            if(fight.winner == 1) a++;
            if (fight.winner == -1) d++;
            //fight = null;
        }

        System.out.println("White wins = " + a + " Black wins = " + d + " Vague " + n);
    }

    public static void main(String[] args) {
        /*Force france = new Battalion(FRANCE, hex);
        Force france1 = new Battalion(FRANCE, hex);
        Force france2 = new Battalion(FRANCE, hex);
        Force austria = new Force(AUSTRIA, hex);
        austria.attach(new Battalion(AUSTRIA, hex));
        austria.attach(new Battalion(AUSTRIA, hex));
        austria.attach(new Battalion(AUSTRIA, hex));
        Fighting fight = hex.startFighting();
        fight.resolve();*/
        /*Force austria = new Battery(AUSTRIA, hex);
        Force f = new Battalion(FRANCE, hex);
        Force a = new Squadron(AUSTRIA, hex);
        france.attach(f);
        austria.attach(a);
        herac(3);*/
        //multipleHerac();
        //Force france = createForce(FRANCE, 0, 3, 1);
        //Force austria = createForce(AUSTRIA, 1, 0, 1);
        getStat(2, 0, 0, 0, 2, 1);

        //Corps and divisions
        /*Force f = createDivision(FRANCE);
        Force a = createDivision(AUSTRIA);
        /*Force f1 = createDivision(FRANCE);
        Force f2 = createDivision(FRANCE);
        Force a1 = createDivision(AUSTRIA);
        Force a2 = createDivision(AUSTRIA);
        Force fCorps = new Force(f, f1, f2);
        Force aCorps = new Force(a, a1, a2);

        /*fCorps.order.frontDirection = EAST;
        aCorps.order.frontDirection = WEST;
        f2.order.frontDirection = WEST;
        /*Force a = createForce(AUSTRIA, 11, 3, 4);
        f.order.frontDirection = NORTHEAST;
        a.order.frontDirection = SOUTHWEST;
        Force a1 = new Battalion(AUSTRIA, hex);
        a1.order.frontDirection = SOUTHEAST;
        Force a2 = new Squadron(AUSTRIA, hex);
        a2.order.frontDirection = NORTHEAST;*/
        //Fighting fight = hex.startFighting();
        //fight.resolve();

    }

    private static Force createDivision (Nation nation) {
        Unit battalion1 = new Battalion(nation, hex);
        Unit battalion2 = new Battalion(nation, hex);
        Unit battalion3 = new Battalion(nation, hex);
        Unit battalion4 = new Battalion(nation, hex);
        Unit battalion5 = new Battalion(nation, hex);
        Unit battalion6 = new Battalion(nation, hex);
        Unit battalion7 = new Battalion(nation, hex);
        Unit battalion8 = new Battalion(nation, hex);
        Unit battalion9 = new Battalion(nation, hex);
        Unit battalion10 = new Battalion(nation, hex);
        Unit battalion11 = new Battalion(nation, hex);
        Unit battalion12 = new Battalion(nation, hex);
        Force regiment1 = new Force(battalion1, battalion2, battalion3);
        Force regiment2 = new Force(battalion4, battalion5, battalion6);
        Force regiment3 = new Force(battalion7, battalion8, battalion9);
        Force regiment4 = new Force(battalion10, battalion11, battalion12);
        Force brigade1 = new Force(regiment1, regiment2);
        Force brigade2 = new Force(regiment3, regiment4);
        Unit squadron1 = new Squadron(nation, hex);
        Unit squadron2 = new Squadron(nation, hex);
        Unit squadron3 = new Squadron(nation, hex);
        Unit squadron4 = new Squadron(nation, hex);
        Unit battery1 = new Battery(nation, hex);
        Unit battery2 = new Battery(nation, hex);
        Unit battery3 = new Battery(nation, hex);
        Unit battery4 = new Battery(nation, hex);
        Force artDivision = new Force(battery1, battery2, battery3, battery4);
        Force division = new Force(brigade1, brigade2, artDivision, squadron1, squadron2, squadron3, squadron4);

        return division;

    }

    private static void herac(int i) {
        Fight fight = new Fight(hex);
        //Fight fight = hex.startFight();
        fight.init();
        for (int k = 0; k < i; k++) {
            fight.resolveStage();
        }
    }
    private static void multipleHerac() {
        int w = 0;
        int b = 0;
        for (int i = 0; i < 100; i++) {
            Hex hex = new Hex();
            Force f = new Force(new Squadron(FRANCE, hex));
            f.order.frontDirection = NORTHEAST;
            f.attach(new Squadron(FRANCE, hex));
            f.attach(new Squadron(FRANCE, hex));

            //Force f1 = new Squadron(FRANCE, hex);
            //f1.order.frontDirection = SOUTHWEST;
            //f1.strength = 10;
            //f1.morale = -1;
            Force f3 = new Battery(FRANCE, hex);
            f.attach(f3);

            Force a = new Force(new Battalion(AUSTRIA, hex));
            Force a1 = new Battery(AUSTRIA, hex);
            a.attach(a1);
            //Fight fight = new Fight(h);

            Fight fight = hex.startFight();
            //fight.init();
            fight.resolveStage();
            //fight.resolveStage();
            //System.out.println(fight.whiteDirectionBonus + " " + fight.blackDirectionBonus);
            //fight.resolveStage();
            if(f.morale > a.morale) w++;
            else if (f.morale < a.morale) b++;
            /*f.disappear();
            //f1.disappear();
            a.disappear();
            f3.disappear();
            a1.disappear();*/

        }
        System.out.println("Whites - " + w + " Blacks - " + b);
        System.out.println();
    }
}
