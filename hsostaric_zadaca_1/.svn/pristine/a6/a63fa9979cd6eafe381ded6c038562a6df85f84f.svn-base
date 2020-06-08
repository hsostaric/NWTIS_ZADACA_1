
package org.foi.nwtis.hsostaric.zadaca_1;

import java.io.Serializable;

/**
 *Klasa sa podacima avoina koja služi za 
 * serijalizaciju koju vrši dretva SerisAviona
 * @author Hrvoje Šoštarić
 */
public class Avion implements Serializable {

    private String nazivAviona;
    private String nazivAeorodromaPolijetanja;
    private String nazivAeorodromaSlijetanja;
    private String vrijemePolijetanja;
    private String vrijemeSlijetanja;

    /**
     * Konstruktor klase Avion sa parametrima:
     * @param nazivAviona   kod(naziv) avoina kojeg korisnik  
     * @param nazivAeorodromaPolijetanja    naziv aerodroma s kojeg avion polijeće 
     * @param nazivAeorodromaSlijeanja      naziv aerodroma na koji avion slijeće
     * @param vrijemePolijetanja            vrijeme polijetanja avoina s definiranog polazišta
     * @param vrijemeSlijetanja             vrijeme slijetanja aviona na definirano odredište
     */
    public Avion(String nazivAviona, String nazivAeorodromaPolijetanja, String nazivAeorodromaSlijeanja, String vrijemePolijetanja, String vrijemeSlijetanja) {
        this.nazivAviona = nazivAviona;
        this.nazivAeorodromaPolijetanja = nazivAeorodromaPolijetanja;
        this.nazivAeorodromaSlijetanja = nazivAeorodromaSlijeanja;
        this.vrijemePolijetanja = vrijemePolijetanja;
        this.vrijemeSlijetanja = vrijemeSlijetanja;
    }

    public String getNazivAviona() {
        return nazivAviona;
    }

    public void setNazivAviona(String nazivAviona) {
        this.nazivAviona = nazivAviona;
    }

    public String getNazivAeorodromaPolijetanja() {
        return nazivAeorodromaPolijetanja;
    }

    public void setNazivAeorodromaPolijetanja(String nazivAeorodromaPolijetanja) {
        this.nazivAeorodromaPolijetanja = nazivAeorodromaPolijetanja;
    }

    public String getNazivAeorodromaSlijetanja() {
        return nazivAeorodromaSlijetanja;
    }

    public void setNazivAeorodromaSlijetanja(String nazivAeorodromaSlijetanja) {
        this.nazivAeorodromaSlijetanja = nazivAeorodromaSlijetanja;
    }

    public String getVrijemePolijetanja() {
        return vrijemePolijetanja;
    }

    public void setVrijemePolijetanja(String vrijemePolijetanja) {
        this.vrijemePolijetanja = vrijemePolijetanja;
    }

    public String getVrijemeSlijetanja() {
        return vrijemeSlijetanja;
    }

    public void setVrijemeSlijetanja(String vrijemeSlijetanja) {
        this.vrijemeSlijetanja = vrijemeSlijetanja;
    }
    
    @Override
    public String toString(){
        return "Oznaka aviona: "+this.nazivAviona+"\nAerodrom polijetanja: "+this.nazivAeorodromaPolijetanja+
                "\nAerodrom slijetanja: "+this.nazivAeorodromaSlijetanja+"\nVrijeme polijetanja: "+this.vrijemePolijetanja+
                "\n Vrijeme slijetanja: "+this.vrijemeSlijetanja;
    }
   

  

}
