/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.zadaca_1;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Klasa KorisnikAvoina predstavlja aplikaciju namjenjenu korisniku pomoću koje
 * šalje zahtjeve na server.
 *
 * @author Hrvoje Šoštarić
 */
public class KorisnikAviona {

    public Socket socket;
    public InputStreamReader isr;
    public OutputStreamWriter osw;
    private StringBuilder sb;

    /**
     * Glavna (main) metoda u kojoj kreiramo novi objekt "KorisnikAvoina" te
     * pozovemo metodu Pokreni koja pokreće sve aktivnosti klase
     *
     * @param args argumenti koje korisnik unosi za rad
     */
    public static void main(String[] args) {
        KorisnikAviona korisnikAviona = new KorisnikAviona();
        korisnikAviona.Pokreni(args);

    }

    /**
     * Metoda koju pozivamo u main metodi, u nju se pozivaju metode koje
     * omogućavaju izvršavanje sviju funkcionalnosti sustava.
     *
     * @param args ulazni parametri proslijeđeni iz main metode.
     */
    public void Pokreni(String[] args) {
        String naredba;
        try {
            String ulazniParametri = pretvoriUlazneParametreUString(args);
            provijeriRegex(ulazniParametri);

            if (ulazniParametri.contains(",")) {
                args = ulazniParametri.split(" ");
            }
            naredba = vratiKomanduZaServer(args, ulazniParametri);

            try {
                KomunicirajSaServerom(naredba, args[5].trim(), args[7].trim());
            } catch (IOException ex) {
                System.out.println("Došlo je do greške " + ex);

            }

        } catch (NumberFormatException ex) {
            System.out.println("Došlo je do greške: " + ex);
        }
    }

    /**
     * Metoda koja spaja korisnikov unos u jedan zapis (string)
     * @param args ulazni parametri koje unosi korisnik
     * @return 
     */
   public String pretvoriUlazneParametreUString(String[] args) {
        StringBuilder ulaz = new StringBuilder();
        for (String zapis : args) {
            ulaz.append(zapis)
                    .append(" ");
        }
        return ulaz.toString()
                .trim();
    }

    /**
     *Metoda koja provjerava ispravnost korisnikog unosa pomoću definiranog regularnog izraza.
     * U slučaju ispravnog unosa nastavlja se sa radom programa, dok se u protivnome prekida rad programa.
     * @param ulaz vrijednost korisnikovog unosa pretvorenog u zapis tipa String
     */
   public static void provijeriRegex(String ulaz) {
        String ulazniIzraz = "^(-k [\\w\\-]{3,10}) (-l ([\\-\\!\\#a-zA-Z0-9]){3,10}) (-s (((?:[0-9]{1,3}\\.){3}[0-9]{1,3})"
                + "|([\\w\\.]+))) (-p 9[0-9][0-9][0-9]) (--kraj|--dodajDretve ([1-9]|[1][0-9]|20)|(--uzletio AerodromPolazište:"
                + " ([\\w\\-]+), AerodromOdredište: ([\\w\\-]+), Avion: ([\\w\\-]+), trajanjeLeta: [1-9](\\d+))|(--ispis [\\w-]+))$";
        Pattern pattern = Pattern.compile(ulazniIzraz);
        Matcher m = pattern.matcher(ulaz);
        if (!m.find()) {
            System.err.println("Ulazni parametri ne odgovaraju zadanom izrazu ");
            System.exit(0);
        }
    }
    /**
     *Metoda koja ovisno o korisnikovom unosu kreira naredbu za poslužitelj.Svaka naredba pokreće različitu radnju na poslužitelju.
     *
     * @param ulaz polje koje sadrži argumente koje je korisnik unio kod pokretanja programa
     * @param ulazniPodaciUStringu  podaci koje je korisnik unio na početku pretvoreni u zapis tipa "String"
     * @return  uređena vrijednost(naredba) koja se šalje prema poslužitelju.
     */
  public  String vratiKomanduZaServer(String[] ulaz, String ulazniPodaciUStringu) {
        String komanda;
        StringBuilder dodatak = new StringBuilder();
        komanda = "KORISNIK " + ulaz[1] + "; LOZINKA " + ulaz[3] + ";";
        if (postojanostArgumenta(ulazniPodaciUStringu, "--kraj")) {
            dodatak.append(" KRAJ;");
        } else if (postojanostArgumenta(ulazniPodaciUStringu, "--dodajDretve")) {
            dodatak.append(" DODAJ ")
                    .append(ulaz[9])
                    .append(";");
        } else if (postojanostArgumenta(ulazniPodaciUStringu, "--uzletio")) {
            kreirajNaredbuUzletio(ulaz, dodatak);
        } else if (postojanostArgumenta(ulazniPodaciUStringu, "--ispis")) {
            dodatak.append(" ISPIS ")
                    .append(ulaz[9])
                    .append(";");
        } else {
            System.err.println("Došlo je do greške");
            System.exit(0);
        }
        return komanda + dodatak.toString();
    }
  /**
   *Metoda u kojoj se kreira komanda za poslužitelja u slučaju da se šalje avion.
   * 
     * @param ulaz polje koje sadrži argumente koje je korisnik unio kod pokretanja programa
     * @param sb  Stringbuilder u koji dodajemo vrijednosti.
   */
    public void kreirajNaredbuUzletio(String ulaz[], StringBuilder sb) {

        sb.append(" UZLETIO ").
                append(ulaz[14].
                        replace(",", ""))
                .append("; POLAZIŠTE ")
                .append(ulaz[10]
                        .replace(",", ""))
                .append("; ODREDIŠTE ")
                .append(ulaz[12]
                        .replace(",", ""))
                .append("; TRAJANJE ")
                .append(ulaz[16])
                .append(";");
    }

    boolean postojanostArgumenta(String tekst, String argument) {
        return (tekst.contains(argument));
    }

    /**
     * Metoda u kojoj se odvija protokol za komunikaciju prema poslužitelju ServerAviona u što podrazumijevamo slanje
     * i primanje poruke putem mrežne utičnice.
     * 
     * @param komanda naredba koja se šalje prema poslužitelju
     * @param adresa  adresa na koju se spajamo
     * @param port    port na koji se spajamo
     * @throws java.io.IOException pogriješka koja se događa prilikom grešaka (neispravni parametri, zauzetost porta i sl.)
     */
   public void KomunicirajSaServerom(String komanda, String adresa, String port) throws IOException {
        int red = 0;
        socket = new Socket(adresa, Integer.parseInt(port));
        try {
            sb = new StringBuilder();
            osw = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
            System.out.println("Na server poslano: " + komanda);
            osw.write(komanda);
            osw.flush();
            socket.shutdownOutput();
            InputStream inputStream = socket.getInputStream();
            isr = new InputStreamReader(inputStream, "UTF-8");
            while ((red = isr.read()) != -1) {
                sb.append((char) red);
            }
            System.out.println("Odgovor: " + sb.toString());
            socket.shutdownInput();
            socket.close();
        } catch (IOException ex) {
            System.out.println("Greška kod uspostave veze prema poslužitelju. " + ex);
        }
    }
}
