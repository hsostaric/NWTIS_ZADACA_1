/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.zadaca_1;

/**
 * Klasa koja sadrži atribute i metode za rad sa geografskim koordinatama
 * @author Hrvoje Šoštarić
 */
public class Koordinata {

    public double geoSirina;
    public double geoDuzina;

    /**
     * Prazan konstruktor klase Koordinata
     * 
      */
    public Koordinata() {
    }

    /**
     * Statična metoda pomoću koje se izračunavaju udaljenost dvaju aerodroma
     * 
     * @param polaziste  predstavlja koordinatu aerodroma sa kojeg aerodrom polijeće
     * @param odrediste  predstavlja koordinatu aerodroma na koji aerodrom slijeće
     * @return vraća se cjelobrojna vrijednost koja predstavlja udaljenost dvaju aerodroma
     */
    public static int izracunajUdaljenost(Koordinata polaziste, Koordinata odrediste) {

        double d = Math.sqrt(Math.pow((odrediste.geoSirina - polaziste.geoSirina), 2)
                + Math.pow((odrediste.geoDuzina - polaziste.geoDuzina), 2));

        return (int) d;
    }

    /**
     *Konstruktor klase Koordinata sa parametrima:
     * 
     * @param geoSirina geografska širina aerodroma 
     * @param geoDuzina geografska dužina aerodroma
     */
    public Koordinata(double geoSirina, double geoDuzina) {
        this.geoDuzina = geoDuzina;
        this.geoSirina = geoSirina;
    }

    @Override
    public String toString() {

        return "Dužina: " + zaokruzi(this.geoDuzina) + ", Širina: " + zaokruzi(this.geoSirina);
    }

    @Override
    public boolean equals(Object obj) {
        Koordinata o = null;
        if (obj instanceof Koordinata) {
            o = (Koordinata) obj;
        } else {
            return false;
        }

        if (zaokruzi(this.geoDuzina) == zaokruzi(o.geoDuzina)
                && zaokruzi(this.geoSirina) == zaokruzi(o.geoSirina)) {
            return true;
        } else {
            return false;
        }
    }

    private double zaokruzi(double broj) {
        return Math.round(broj * 1000000) / 1000000d;
    }
}
