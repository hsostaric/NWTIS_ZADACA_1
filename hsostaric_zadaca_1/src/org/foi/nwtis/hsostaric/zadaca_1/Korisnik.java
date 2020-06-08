/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.zadaca_1;

/**
 *Klasa koja sadrži atribute za učitavanje podataka iz određene datoteke, te pomoću čijih se 
 * podataka autentificiramo u klasi ServerAviona
 * @author Hrvoje Šoštarić
 */
public class Korisnik {
    private String korisnickoIme;
    private String lozinka;

    /**
    *Konstruktor klase Korisnik koji prima sljedeće parametre:
     * @param korisnickoIme korisničko ime korisnika koji se pokušava autentificirati
     * @param lozinka       lozinka korisnika koji se pokušava autentificirati
    */
    public Korisnik(String korisnickoIme, String lozinka) {
        this.korisnickoIme = korisnickoIme;
        this.lozinka = lozinka;
    }

    
    
    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }
    
    
}
