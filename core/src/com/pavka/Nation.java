package com.pavka;

public enum Nation {
    AUSTRIA,
    BRITAIN,
    FRANCE,
    PRUSSIA,
    RUSSIA;

    public static final int BLACK = -1;
    public static final int WHITE = 1;
    private float nationalMorale;
    public int color;
    static
    {
        FRANCE.nationalMorale = 1.4f;
        FRANCE.color = WHITE;
        AUSTRIA.nationalMorale = 1.0f;
        AUSTRIA.color = BLACK;
    }

    public float getNationalMorale() {
        return nationalMorale;
    }

    public void setNationalMorale(float nationalMorale) {
        this.nationalMorale = nationalMorale;
    }
}
