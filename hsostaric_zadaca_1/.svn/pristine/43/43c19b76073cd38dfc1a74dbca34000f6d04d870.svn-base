/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.zadaca_1;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.hsostaric.konfiguracije.Konfiguracija;

/**
 * Klasa koja je ujedno i dretva koja obrađuje klijentski zahtjev te ga šalje na
 * poslužitelj ServerSimulatoraLeta te ujedno i njezin zahtjev proslijeđuje
 * nazad korisniku.
 *
 * @author Hrvoje Šoštarić
 */
public class ZahtjevAviona extends Thread {

    private final Socket veza;
    private InputStream inputStream;
    private OutputStream outputStream;
    private StringBuilder stringBuilder;
    private InputStream streamReader;
    private OutputStreamWriter streamWriter;
    private String regexUlaznogZahtjeva;
    private InputStreamReader inputStreamReader;
    private final ReentrantLock reentrantLock;
    private Konfiguracija konfiguracija;
    private Socket vezaPremaLetu;
    private OutputStreamWriter osw;
    private OutputStream os;
    private InputStream ulaz;
    private InputStreamReader isr;
    private String trenutnoVrijeme;
    private String konacnoVrijeme;
    private int intervalZaDodavanje;

    /**
     * Konstruktor klase koja prima parametre za primanje i proslijeđivanje
     * zahtjeva prema poslužitelju i klijentu.
     *
     * @param veza mrežna utičnica veze iz koje preuzimamo ulazne i izlazne
     * tokove
     * @param konfiguracija parametar konfiguracije sustava
     * @param grupa parametar grupe dretvi
     * @param name ime dretve
     */
    public ZahtjevAviona(Socket veza, Konfiguracija konfiguracija, ThreadGroup grupa, String name) {
        super(grupa, name);
        this.veza = veza;
        this.konfiguracija = konfiguracija;
        reentrantLock = new ReentrantLock(true);
    }

    /**
     * Metoda koja sadrži radnju koja se izvodi nakon pokretanja dretve i poziva
     * metodu za obradu klijenta i zatvaranja veze.
     */
    @Override
    public void run() {
        try {
            ObradaKlijenta();
        } catch (IOException ex) {
            System.out.println("Greška: " + ex.getMessage());
        } catch (ParseException ex) {
            System.out.println("Došlo je do greške prilikom parsanja zapisa.");
        } finally {
            try {
                ZatvoriKlijenta();
            } catch (IOException ex) {
                System.out.println("Došlo je do greške prilikom zatvaranja klijenta.");
            }
        }
    }

    /**
     * Metoda u kojoj se obrađuju zahtjevi klijenta te ovisno o naredbi šalju se
     * upiti prema poslužitelju ServerSimulatoraLeta i prema klijentu.
     *
     * @throws java.io.IOException iznimka koja se poziva ukoliko dođe do greške
     * sa datotekama
     * @throws java.text.ParseException iznimka koja se poziva ukoliko dođe do
     * greške sa prilagođavanjem datuma
     */
    public synchronized void ObradaKlijenta() throws IOException, ParseException {
        int bajt;
        String zahtjev;
        String odgovor;
        boolean uvjet;
        stringBuilder = new StringBuilder();
        streamReader = veza.getInputStream();
        inputStreamReader = new InputStreamReader(streamReader, "UTF-8");
        streamWriter = new OutputStreamWriter(veza.getOutputStream(),
                "UTF-8");
        while ((bajt = inputStreamReader.read()) != -1) {
            stringBuilder.append((char) bajt);
        }
        zahtjev = stringBuilder.toString();
        System.out.println("Dobiveno od klijenta: " + zahtjev);
        uvjet = IspitajSintaksuZahtjeva(zahtjev);
        if (uvjet == true) {
            if (AutentificirajKorisnika(zahtjev) == true) {
                odgovor = IzvrsiOdredjenuRadnju(zahtjev);
                VratiKorisnikuPoruku(odgovor);

            } else {
                VratiKorisnikuPoruku("ERROR 03; Korisničko ime ili lozinka ne odgovaraju;");
            }
        } else {
            VratiKorisnikuPoruku("ERROR 02; Kriva sintaksa zahtjeva;");
        }
    }

    /**
     * Metoda koja ispituje ispravnost sintakse zaprimljenog zahtjeva.
     *
     * @param zahtjev naredba dobivena od klijenta.
     * @return istinitost ispravnosti sintakse dobivenog zahtjeva
     */
    public synchronized boolean IspitajSintaksuZahtjeva(String zahtjev) {
        regexUlaznogZahtjeva = "^(KORISNIK [\\w\\-]{3,10};) (LOZINKA ([\\-\\!\\#a-zA-Z0-9]){3,10};)(( KRAJ;)|( DODAJ ([1-9]|[1][0-9]|20);)"
                + "|( ISPIS [\\w\\-]+;)|( UZLETIO [\\w\\-]+; POLAZIŠTE [\\w\\-]+; ODREDIŠTE [\\w\\-]+; TRAJANJE [1-9](\\d+);))$";
        Pattern pattern = Pattern.
                compile(regexUlaznogZahtjeva);
        Matcher m = pattern.
                matcher(zahtjev);
        return m.find();
    }

    /**
     * Metoda koja zatvara vezu prema klijentu i smanjuje brojač aktivne veze.
     *
     * @throws java.io.IOException iznimka koja se baca prilikom greške kod
     * zatvaranja veze
     */
    public synchronized void ZatvoriKlijenta() throws IOException {
        veza.shutdownInput();
        veza.shutdownOutput();
        veza.close();
        ServerAviona.brojacVeze--;
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Metoda koja provjerava postoje li korisničko ime i lozinka iz dobivenog
     * zahtjeva te ovisno o tome vraća istinitost.
     *
     * @param zahtjev vrijednost zahtjeva dobivenog od strane korisnika
     * @return vraća istinitost o postojanju korisničkog imena i lozinke u
     * kolekciji
     */
    public boolean AutentificirajKorisnika(String zahtjev) {
        String korisnickoIme;
        String polje[];
        String lozinka;
        boolean uvjet = false;
        int pocetakLozinke;
        int pocetak;
        polje = zahtjev.split(";");
        pocetak = polje[0].indexOf("KORISNIK");
        korisnickoIme = polje[0].trim()
                .substring(pocetak + 9,
                        polje[0].length())
                .trim();
        pocetakLozinke = zahtjev.indexOf("LOZINKA");
        lozinka = polje[1].substring(pocetak + 9,
                polje[1].length())
                .trim();
        for (Korisnik k : ServerAviona.Korisnici) {
            if (k.getKorisnickoIme().equals(korisnickoIme)
                    && k.getLozinka().equals(lozinka)) {
                uvjet = true;
                break;
            }
        }
        return uvjet;
    }

    public synchronized void VratiKorisnikuPoruku(String poruka) throws IOException {
        streamWriter.write(poruka);
        streamWriter.flush();
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Metoda koja provjerava i utvrđuje o kojoj se naredbi dobivene od klijenta
     * radi te prema tome poziva određene metode.
     *
     * @param zahtjev vrijednost klijentskog zahtjeva
     * @return odgovor koji se šalje klijentu
     * @throws java.text.ParseException iznimka koja se baca nakongreške
     * prilikom prilagodbe datuma
     */
    public synchronized String IzvrsiOdredjenuRadnju(String zahtjev) throws ParseException {
        if (zahtjev.contains("KRAJ")) {
            return IzvrsiKraj();
        } else if (zahtjev.contains("DODAJ")) {
            return DodajBrojDretvi(zahtjev);
        } else if (zahtjev.contains("UZLETIO")
                && zahtjev.contains("POLAZIŠTE")
                && zahtjev.contains("ODREDIŠTE")
                && zahtjev.contains("TRAJANJE")) {
            return IzvrsiUzletioNaredbu(zahtjev);
        } else {
            return IzvrsiNaredbuPozicija(zahtjev);
        }

    }

    /**
     * Metoda koja obrađuje zahtjev ukoliko zahtjev dobiven od klijenta sadrži
     * DODAJ.
     *
     * @param zahtjev vrijednost zahtjeva dobivenog od korisnika
     * @return odgovor koji se šalje korisniku ovisno o uspjehu
     */
    public synchronized String DodajBrojDretvi(String zahtjev) {
        String maksCekaca = konfiguracija.dajPostavku("maks.cekaca");
        int maxCekaca = Integer.parseInt(maksCekaca);
        String[] polje;
        int dodajN;
        int noviBrojDretvi;
        String element;
        String dodaj;
        try {
            polje = zahtjev.split(";");
            element = polje[2].trim();
            dodaj = element.substring(6,
                    element.length());
            dodajN = Integer.parseInt(dodaj);

            noviBrojDretvi = dodajN + ServerAviona.brojDretvi;
            if (noviBrojDretvi > maxCekaca) {
                return "ERROR 12; Novi broj dretvi nemože biti veći od broja max čekača";
            } else {
                ServerAviona.brojDretvi = noviBrojDretvi;
                return "OK;";
            }
        } catch (NumberFormatException ex) {
            return "ERROR 12; " + ex;
        }
    }

    /**
     * Metoda koja seriajlizira kolekciju aviona nakon dobivenog zahtjeva za
     * KRAJ
     *
     * @return poruka za kllijenta ovisno o uspjehu
     */
    public synchronized String IzvrsiKraj() {

        if (!ServerAviona.ListaAviona.
                isEmpty()) {
            reentrantLock.lock();
            try {
                FileOutputStream out = new FileOutputStream(ServisAviona.nazivDatoteke);
                try (ObjectOutputStream s = new ObjectOutputStream(out)) {
                    s.writeObject(ServerAviona.ListaAviona);
                }
            } catch (IOException ex) {
                return "ERROR 11; Problem sa serijalizacijom";
            } finally {
                reentrantLock.unlock();
            }
        }
        ServerAviona.kraj = true;
        ServisAviona.kraj = true;
        return "OK;";
    }

    /**
     * Metoda koja se izvršava ukoliko zahtjev sadrži ISPIS te šalje upit
     * POZICIJA na poslužitelj ServerSimulatoraLeta.
     *
     * @param zahtjev vrijendost korisnikovog zahtjeva koji u sebi sadrži riješ
     * ISPIS
     * @return odgovor za korisnika dobiven od poslužitelja ili grešku ukoliko
     * zrakoplov nije pronađen u kolekciji
     */
    public synchronized String IzvrsiNaredbuPozicija(String zahtjev) {
        String[] polje;
        String oznakaAviona;
        String necistaOznaka;
        polje = zahtjev.split(";");
        necistaOznaka = polje[2].trim();
        oznakaAviona = necistaOznaka.substring(6,
                necistaOznaka.length());
        for (Avion avion : ServerAviona.ListaAviona) {
            if (avion.getNazivAviona()
                    .equals(oznakaAviona)) {
                return KomunicirajSaServerom(avion, 0);
            }
        }
        return "ERROR 16; Traženi avion nije pronađen u listi !!!;";
    }

    /**
     * Metoda koja priprema naredbu za poslužitelja ukoliko dobiven zahtjev u
     * sebi sadrži UZLETIO.
     *
     * @param zahtjev vrijednost korisničkog zahtjeva koji u sebi sadrži
     * vrijednost UZLETO i SLETIO
     * @return poruka za klijenta koja ovisi o uspjehu
     * @throws java.text.ParseException iznimka koja se izbacuje kada dolazi do
     * greške prilikom prilagodbe datuma.
     */
    public synchronized String IzvrsiUzletioNaredbu(String zahtjev) throws ParseException {
        String polazisteOcisceno;
        String uzletisteOcisceno;
        String polaziste;
        String uzletiste;
        String nazivAviona;
        String trajanje;
        int trajanjeKonacno;
        boolean postojanostUzletista;
        boolean postojanostOdredista;
        String[] polje;
        polje = zahtjev.split(";");
        polazisteOcisceno = polje[3].trim();
        uzletisteOcisceno = polje[4].trim();
        polaziste = polazisteOcisceno.substring(10,
                polazisteOcisceno.length());
        uzletiste = uzletisteOcisceno.substring(10, uzletisteOcisceno
                .length());
        nazivAviona = polje[2].trim().
                substring(8, polje[2]
                        .length() - 1);
        trajanje = polje[5].trim()
                .substring(9, polje[5]
                        .length() - 1);
        postojanostOdredista = ProvjeriPostojanostAerodroma(polaziste);
        postojanostUzletista = ProvjeriPostojanostAerodroma(uzletiste);
        if (postojanostOdredista == false
                || postojanostUzletista == false) {
            return "ERROR 13; Aerodromi ne odgovaraju zapisima u listi;";
        }
        return radSPodacimaAvoina(nazivAviona, trajanje, polaziste, uzletiste);
    }

    /**
     * Metoda koja provjerava ispravnost aerodroma polazišta i odredišta aviona
     * iz dobivenog zahtjeva.
     *
     * @param naziv oznaka aerodroma
     * @return istinitost o ekvivalentnosti aeorodroma
     */
    public synchronized boolean ProvjeriPostojanostAerodroma(String naziv) {
        for (Aerodrom aerodrom : ServerAviona.Aerodoromi) {
            if (aerodrom.getIcao()
                    .equals(naziv)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Metoda koja manipulira sa kolekcijom aviona, evidentira njihovo
     * postojanje, relevantnost te ih dodaje u kolekciju i šalje upit na
     * poslužitelj.
     *
     * @param oznaka vrijednost naziva aviona
     * @param interval vrijednost koja predstavlja trajanje leta aviona
     * @param aerodromPolijetanja vrijednost oznake aerodroma polazišta
     * @param aerodromSlijetanja vrijednost oznake aerodorma odredišta
     * @return vrijednost koja se šalje na server
     * @throws java.text.ParseException iznimka koja se izbacuje prilikom greške
     * u formatiranju datuma.
     */
    public synchronized String radSPodacimaAvoina(String oznaka, String interval,
            String aerodromPolijetanja, String aerodromSlijetanja) throws ParseException {
        intervalZaDodavanje = Integer.parseInt(interval);
        Avion av;
        int mod = 1;
        String aerodromPol = dajNazivAerodroma(aerodromPolijetanja);
        String aerodromSli = dajNazivAerodroma(aerodromSlijetanja);
        if (!ServerAviona.ListaAviona
                .isEmpty()) {
            for (Avion avion : ServerAviona.ListaAviona) {
                if (avion.getNazivAviona()
                        .equals(oznaka)) {
                    if (!avion.getNazivAeorodromaPolijetanja()
                            .equals(aerodromPol)
                            || !avion.getNazivAeorodromaSlijetanja()
                                    .equals(aerodromSli)) {
                        return "ERROR 14; Avion postoji, ali aerodorom polijetanja i slijetanja nisu isti;";
                    }
                    av = avion;
                    return KomunicirajSaServerom(av, mod);
                }
            }
            return DodajAvionUKolekciju(oznaka, interval, aerodromPol, aerodromSli, mod);
        } else if (ServerAviona.ListaAviona
                .isEmpty()) {
            return DodajAvionUKolekciju(oznaka, interval, aerodromPol, aerodromSli, mod);
        }
        return "ERROR";
    }

    /**
     * Metoda koja dodaje avione u odgovarajuću kolekciju te to obavlja
     * međusobno isključivo u odnosu na dretvu koja je zadužena za
     * serijalizaciju.
     *
     * @param oznaka vrijednost naziva aviona
     * @param interval vrijednost koja predstavlja trajanje leta aviona
     * @param aerodromPolijetanja vrijednost oznake aerodroma polazišta
     * @param aerodormSlijetanja vrijednost oznake aerodorma odredišta
     * @param mod način na koji se obavlja radnja
     * @return odgovor dobiven os servera
     * @throws java.text.ParseException iznimka koja se izbacuje prilikom greške
     * u formatiranju datuma.
     */
    public synchronized String DodajAvionUKolekciju(String oznaka, String interval, String aerodromPolijetanja,
            String aerodormSlijetanja, int mod) throws ParseException {

        Avion avion;
        avion = SloziAvion(oznaka, interval, aerodromPolijetanja, aerodormSlijetanja);
        synchronized (ServerAviona.ListaAviona) {
            ServerAviona.ListaAviona
                    .add(avion);
        }
        return KomunicirajSaServerom(avion, mod);
    }

    /**
     * Metoda koja je zadužena za kreiranje novog zrakoplova koji se dodaje u
     * kolekciju.
     *
     * @param oznaka vrijednost naziva aviona
     * @param interval vrijednost koja predstavlja trajanje leta aviona
     * @param aerodromPolijetanja vrijednost oznake aerodroma polazišta
     * @param aerodromSlijetanja vrijednost oznake aerodorma odredišta
     * @return instanca novog aviona
     * @throws java.text.ParseException iznimka koja se izbacuje prilikom greške
     */
    public synchronized Avion SloziAvion(String oznaka, String interval,
            String aerodromPolijetanja, String aerodromSlijetanja) throws ParseException {
        Avion avion = null;
        String polijetanje = FormatiranoTrenutnoVrijeme();
        Date trenutniDatum = formatirajStringuDate(polijetanje);
        int seconds = Integer.parseInt(interval);
        String slijetanje = dodajSekundeDatumu(trenutniDatum, seconds);
        avion = new Avion(oznaka, aerodromPolijetanja, aerodromSlijetanja, polijetanje, slijetanje);
        return avion;
    }

    /**
     * Metoda koja trenutno vrijeme pretvara u oblik vremena prema uzorku
     * "yyyy.MM.dd HH:mm:ss".
     *
     * @return trenutno vrijeme
     */
    public synchronized String FormatiranoTrenutnoVrijeme() {
        SimpleDateFormat formatDatuma = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date date = new Date();
        return formatDatuma.format(date);

    }

    /**
     * Metoda koja pretvara String oblik zapisa u zapis tipa Date.
     *
     * @param zapis vrijednost datuma u stringu
     * @return datum u obliku Date
     * @throws java.text.ParseException greška nastala prilikom konvertiranja
     * datuma
     */
    public synchronized Date formatirajStringuDate(String zapis) throws ParseException {
        SimpleDateFormat formatDatuma = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        return formatDatuma.parse(zapis);
    }

    /**
     * Metoda koja dodaje interval vremenu polijetanja te tako odredi vrijeme
     * slijetanja.
     *
     * @param datum datum kojemu se dodaje interval u sekundama
     * @param sekunde interval koji se dodaje vremenu
     * @return konačna vrijednost datuma nakon dodanog intervala
     */
    public synchronized String dodajSekundeDatumu(Date datum, int sekunde) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datum);
        calendar.add(Calendar.SECOND, sekunde);
        SimpleDateFormat formatDatuma = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date dat = calendar.getTime();
        return formatDatuma.format(dat);
    }

    public synchronized String dajNazivAerodroma(String oznaka) {
        String naziv = "";
        for (Aerodrom aerodrom : ServerAviona.Aerodoromi) {
            if (aerodrom.getIcao()
                    .equals(oznaka)) {
                naziv = aerodrom.getNaziv();
            }
        }
        return naziv;
    }

    /**
     * Metoda zadužena za komunikaciju sa poslužiteljem ServerSimulatoraLeta.
     *
     * @param avion oznaka aviona koji se šalje na poslužitelja
     * @param mod način koji određuje koji se zahtjev na poslužitelja šalje
     * @return odgovor dobiven od poslužitelja koji se šalje klijentu
     */
    public synchronized String KomunicirajSaServerom(Avion avion, int mod) {
        String upit;
        String adresa = veza.getLocalAddress()
                .toString()
                .replace("/", "")
                .trim();
        int port = dajPortZaLetove();
        try {
            vezaPremaLetu = new Socket(adresa, port);
            ulaz = vezaPremaLetu.getInputStream();
            os = vezaPremaLetu.getOutputStream();
            osw = new OutputStreamWriter(os, "UTF-8");
            upit = urediKomanduZaServerLetova(avion, mod);
            osw.write(upit);
            osw.flush();
            vezaPremaLetu.shutdownOutput();
            return VracenOdgovorOdLeta(mod, avion);
        } catch (IOException ex) {
            System.out.println("Greška: " + ex);
        } finally {
            try {
                ZatvoriVezuPremaLetovima();
            } catch (IOException ex) {
            }
        }
        return "ERROR 15; Pogreška kod komunikacije sa serverom";
    }

    /**
     * Metoda koja uređuje naredbu za slanje na poslužitelja
     * ServerSimulatoraLeta.
     *
     * @param avion oznaka aviona
     * @param mod način koji određuje koja se naredba šalje
     * @return naredba koja se šalje na poslužitelja
     */
    public synchronized String urediKomanduZaServerLetova(Avion avion, int mod) {
        if (mod == 1) {
            trenutnoVrijeme = FormatiranoTrenutnoVrijeme();
            try {
                konacnoVrijeme = dodajSekundeDatumu(
                        formatirajStringuDate(trenutnoVrijeme), intervalZaDodavanje);
            } catch (ParseException ex) {

            }
            return "LET " + avion.getNazivAviona() + "; "
                    + "POLIJETANJE " + trenutnoVrijeme + "; "
                    + "SLIJETANJE " + konacnoVrijeme + ";";
        }
        return "POZICIJA: " + avion.getNazivAviona() + ";";
    }

    public synchronized void ZatvoriVezuPremaLetovima() throws IOException {

        vezaPremaLetu.close();

    }

    /**
     * Naredba koja čita dobivenu poruku od poslužitelja te kreira prikladan
     * odgovor za klijenta.
     *
     * @param mod način koji određuje koji će se odgovor poslati klijentu
     * @param avion avion kojemu se ažuriraju vrijeme polijetaja i slijetanja
     * nakon uspješnog odgovora
     * @return odgovor za klijenta
     */
    public synchronized String VracenOdgovorOdLeta(int mod, Avion avion) {
        try {
            String odgovor;

            StringBuilder sb = new StringBuilder();
            int red;
            isr = new InputStreamReader(ulaz, "UTF-8");
            while ((red = isr.read()) != -1) {
                sb.append((char) red);
            }
            odgovor = sb.toString();
            vezaPremaLetu.shutdownInput();
            System.out.println("Odgovor: " + odgovor);
            if (mod == 1) {
                return odgovorZaLet(odgovor, avion);
            } else {
                return OdgovorZaIspis(odgovor);
            }
        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return "Greska";
    }

    /**
     * Metoda koja kreira odgovor za korisnika nakon odgovora OK koji je dobiven
     * od poslužitelja ServerSimulatoraLeta.
     *
     * @param odgovor odgovor dobiven od poslužitelja
     * @param avion avion kojemu se ažuriraju vrijeme polijetanja i slijetanja
     * @return
     */
    public synchronized String odgovorZaLet(String odgovor, Avion avion) {
        if (odgovor.startsWith("ERROR")) {
            return "ERROR 15; Server simulatora leta je vratio grešku;";
        } else {
            avion.setVrijemePolijetanja(trenutnoVrijeme);
            avion.setVrijemeSlijetanja(konacnoVrijeme);
            Koordinata polaziste = vratiKordinatuAerodroma(avion
                    .getNazivAeorodromaPolijetanja());
            Koordinata odrediste = vratiKordinatuAerodroma(avion.
                    getNazivAeorodromaSlijetanja());
            int d = IzracunajUdaljenost(polaziste, odrediste);
            return "OK; UDALJENOST " + d + ";";
        }
    }

    /**
     * Metoda koja dohvaća kordinate za dobiveni aerodrom.
     *
     * @param aerodrom naziv aerodorma
     * @return Koordinata aerodroma
     */
    public synchronized Koordinata vratiKordinatuAerodroma(String aerodrom) {

        for (Aerodrom a : ServerAviona.Aerodoromi) {
            if (a.getNaziv().
                    equals(aerodrom)) {
                return a.getKoridinate();
            }
        }
        return null;
    }

    /**
     * Metoda zadužena za izračunavanje udaljenosti dvaju aerodroma, tj.za
     * izračun duljine leta.
     *
     * @param polijetanje koordinata aerodroma polijetanja
     * @param slijetanje koordinata aerodorma slijetanja
     * @return cjelobrojna udaljenost dvaju aerodorma
     */
    public synchronized int IzracunajUdaljenost(Koordinata polijetanje, Koordinata slijetanje) {
        int udaljenost = Koordinata.
                izracunajUdaljenost(polijetanje, slijetanje);
        return udaljenost;
    }

    /**
     * Metoda koja modificira odgovor dobivenog od poslužitelja
     * ServerSimulatoraLeta.
     *
     * @param odgovor vrijednost odgovora dobivenog od poslužitelja
     * @return konačan odgovor za klijenta
     */
    public synchronized String OdgovorZaIspis(String odgovor) {
        if (odgovor.startsWith("OK;")) {
            return odgovor;
        }
        return "ERROR 16; Traženog leta nema u listi ili je greška kod formatiranja datuma;";
    }

    /**
     * Metoda koja daje vrijednost za postavku porta simulatora.
     *
     * @return vrijednost postacke port.simulator
     */
    public synchronized int dajPortZaLetove() {
        String port;
        port = konfiguracija.dajPostavku("port.simulator");
        return Integer.parseInt(port);
    }
}
