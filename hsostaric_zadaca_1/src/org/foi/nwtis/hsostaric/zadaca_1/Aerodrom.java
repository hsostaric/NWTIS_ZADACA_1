/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.zadaca_1;

/**
 * Klasa koja predstavlja entitet jednog učitanog redka iz datoteke koja sadrži
 * podatke o aerodromima.
 *
 * @author Hrvoje Šoštarić
 */
public class Aerodrom {

    private String icao;
    private String naziv;
    private String drzava;
    private Koordinata koridinate;

    /*
        Prazan konstruktor klase Aerodrom
     */
    public Aerodrom() {
    }

    /**
     * Konstruktor klase Aerodrom s parametrima:
     *
     * @param icao       četveroznamenkasti kod aerodroma 
     * @param naziv      naziv samog aerodroma
     * @param drzava     naziv države u kojoj se aerodrom nalazi
     * @param koridinate  geografske kordinate na karti gdje se aerodrom nalazi (geografska širina i dužina)
     */
    public Aerodrom(String icao, String naziv, String drzava, Koordinata koridinate) {
        this.icao = icao;
        this.naziv = naziv;
        this.drzava = drzava;
        this.koridinate = koridinate;
    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getDrzava() {
        return drzava;
    }

    public void setDrzava(String drzava) {
        this.drzava = drzava;
    }

    public Koordinata getKoridinate() {
        return koridinate;
    }

    public void setKoridinate(Koordinata koridinate) {
        this.koridinate = koridinate;
    }

    @Override
    public String toString() {
        return "Oznaka: " + this.icao + "\nNaziv aerodroma: " + this.naziv + "\n Država aerodroma: " + this.drzava;
    }

}
