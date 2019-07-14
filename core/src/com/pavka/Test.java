package com.pavka;


import static com.pavka.Nation.*;



public class Test {

    static Hex hex = new Hex();


    public static void main(String[] args) {

        //MAIN SECTION
        /*Force france = createForce(FRANCE,3, 1, 1);
        Force austria = createForce(AUSTRIA, 6, 1, 1);

        Force f = createForce(FRANCE, 4, 2, 1);
        Force a = createForce(AUSTRIA, 0, 1, 1);

        //france.attach(f);
        austria.attach(a);

        france.order = new Order(true, 0.7, 0);
        austria.order = new Order(true, 0.7, 0);


        Battle battle = new Battle(france, austria);


        battle.longDistanceBombing();


        //battle.resolve();
        battle.resolveStage();
        if (battle.winner != 0) System.out.println("Victory of " + battle.winner);
        france.attach(f);
        battle.resolveStage();
        if (battle.winner != 0) System.out.println("Victory of " + battle.winner);
        battle.resolveStage();
        if (battle.winner != 0) System.out.println("Victory of " + battle.winner);
        battle.resolveStage();
        if (battle.winner != 0) System.out.println("Victory of " + battle.winner);
        //battle.resolveStage();
        //battle.resolveStage();
        System.out.println("After the battle");
        System.out.println();
        list(france);
        list(austria);*/
        //END OF MAIN SECTION
        Unit france = new Battalion(FRANCE, hex);
        //Force france = createForce(FRANCE, 1, 0 , 0 );
        Force austria = new Force(new Battalion(AUSTRIA, hex));
        //Force austria = createForce(AUSTRIA, 1, 0, 0);
        Battle battle = new Battle(france, austria);
        france.order = new Order(true, 0.7, 0);
        austria.order = new Order(true, 0.7, 0);
        battle.resolve();
        //getStat(france, austria);
    }

    public static void getStat(Force attacker, Force defender) {
        int a = 0;
        int d = 0;
        int n = 0;
        for (int i = 0; i < 100; i++) {
            Force att = createForce(attacker.nation, attacker.battalions.size(), attacker.squadrons.size(), attacker.batteries.size(), attacker.morale);
            att.order = new Order(attacker.order.seekBattle, attacker.order.retreatLevel, 0);
            Force def = createForce(defender.nation, defender.battalions.size(), defender.squadrons.size(), defender.batteries.size(), defender.morale);
            def.order = new Order(defender.order.seekBattle, defender.order.retreatLevel, 0);


            Battle battle = new Battle(att, def);

            //Battle battle = new Battle(force1, force2);
            int r = battle.resolve();

            if (r == 1) a++;
            else
                if (r == -1) d++;

            else n++;
        }

        System.out.println("Attacker wins = " + a + " Defender wins = " + d + " Without battle " + n);
    }

    static void list(Force force) {
        System.out.println(force.name);
        System.out.println("Totally soldiers: " + force.strength + ", Morale level: " + force.morale + " speed: " + force.speed +
                " AMMO: " + force.ammoStock + " FOOD: " + force.foodStock +" foodNeed: "+ force.foodNeed + " foodLimit " + force.foodLimit + " fire : " + force.fire + " charge: " +
                force.charge);
        System.out.println("Including: ");
        System.out.println();
        for (Force f: force.forces) {
            if (f.isUnit) {
                System.out.println("    " + f.name + ": " + f.strength + " soldiers, Morale level: " + f.morale + " speed: " + f.speed +
                        " AMMO: " + f.ammoStock + " FOOD: " + f.foodStock + " Food Need " + f.foodNeed + " foodlimit " + f.foodLimit + " fire: " + f.fire + " charge: " + f.charge);
            }
            else {
                list(f);
            }
        }
        System.out.println();
    }

    private static Force createForce(Nation nation, int i, int c, int a) {
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
    private static Force createForce(Nation nation, int i, int c, int a, double m) {
        int b = 1;
        int s = 1;
        int y = 1;
        Force force = new Force(nation, hex);
        force.name = "Corps " + nation;
        for (int count = 0; count < i; count++) {
            Unit u = new Battalion(nation, hex);
            u.name = b++ +". Battalion";
            u.morale = m;
            force.attach(u);
        }
        for (int count = 0; count < c; count++) {
            Unit u =  new Squadron(nation, hex);
            u.name = s++ + ". Squadron";
            u.morale = m;
            force.attach(u);
        }
        for (int count = 0; count < a; count++) {
            Unit u = new Battery(nation, hex);
            u.name = y++ + ". Battery";
            u.morale = m;
            force.attach(u);
        }


        return force;
    }
}
