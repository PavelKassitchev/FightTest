package com.pavka;

import static com.pavka.Nation.*;

public class FightTest {
    static Hex hex = new Hex();
    public static void main(String[] args) {
        /*Force france = new Battalion(FRANCE, hex);
        Force austria = new Battery(AUSTRIA, hex);
        Force f = new Battalion(FRANCE, hex);
        Force a = new Squadron(AUSTRIA, hex);
        france.attach(f);
        austria.attach(a);
        herac(3);*/
        multipleHerac();
    }

    private static void herac(int i) {
        Fight fight = new Fight(hex);
        for (int k = 0; k < i; k++) {
            fight.resolveStage();
        }
    }
    private static void multipleHerac() {
        int w = 0;
        int b = 0;
        for (int i = 0; i < 100; i++) {
            Hex h = new Hex();
            Force f = new Squadron(FRANCE, h);
            f.attach(new Squadron(FRANCE, h));
            f.attach(new Squadron(FRANCE, h));

            Force a = new Battalion(AUSTRIA, h);
            Fight fight = new Fight(h);
            fight.resolveStage();
            fight.resolveStage();
            //fight.resolveStage();
            if(f.morale > a.morale) w++;
            else b++;
        }
        System.out.println("Whites - " + w + " Blacks - " + b);
    }
}