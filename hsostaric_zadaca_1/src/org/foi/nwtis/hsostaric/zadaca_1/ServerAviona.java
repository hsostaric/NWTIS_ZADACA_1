/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.zadaca_1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.hsostaric.konfiguracije.*;

/**
 * Klasa koja predstavlja poslužiteljsku stranu na koji klijent šalje svoje
 * naredbe. Pokreće dretvu zaduženu za serijalizaciju kolekcije zrakoplova
 * (aviona) te je zadužena za prijem korisnikovih naredbi. Ovisno o naredbi
 * izvršava i određenu radnju.
 *
 * @author Hrvoje Šoštarić
 */
public class ServerAviona {

    public Konfiguracija Konfiguracija;
    public String UlazniParametri = "";
    public String RegEx = "";
    public ServerSocket serverSocket;
    public int PortAvioni = 0;
    public static List<Korisnik> Korisnici = null;
    public static List<Aerodrom> Aerodoromi = null;
    public ThreadGroup GrupaDretvi;
    public ThreadGroup DretveZahtjeva;
    public Thread ServisnaDretva;
    public static List<Avion> ListaAviona = Collections.
            synchronizedList(new ArrayList<>());
    public static List<ZahtjevAviona> ListaKlijentskihDretvi;
    public static int brojDretvi = 0;
    public ZahtjevAviona zahtjevKorisnika;
    protected InputStreamReader isr;
    protected OutputStreamWriter osw;
    protected Iterator<ZahtjevAviona> iterator;
    public Socket veza;
    public static boolean kraj = false;
    private ReentrantLock lock = new ReentrantLock(true);
    public static int brojacVeze = 0;

    /**
     * Main metoda klase u kojoj kreiramo instancu ServerAviona i pozivamo
     * metodu Kreni koja pokreće aktivnosti poslužitelja.
     *
     * @param args ulazni parametri koji se pokreću kod pokretanja - datoteka
     * konfiguracije i najveći broj aktivnih korisnika istovremeno
     */
    public static void main(String[] args) {
        try {
            ServerAviona serverAviona = new ServerAviona();
            serverAviona.Kreni(args);
        } catch (NemaKonfiguracije
                | NeispravnaKonfiguracija
                | IOException ex) {
            System.out.println("Pogreska u radu: " + ex);
        }
    }

    /**
     * Metoda koja sadrži pozive ostalih metoda koje izvršavaju aktivnosti
     * poslužitelja te se poziva preka objekta serverAvoina u main metodi.
     *
     * @param args ulazni parametri proslijeđeni iz main metode
     * @throws org.foi.nwtis.hsostaric.konfiguracije.NemaKonfiguracije iznimka
     * koja se baca u slučaju da ne postoji konfiguracijska datoteka.
     * @throws org.foi.nwtis.hsostaric.konfiguracije.NeispravnaKonfiguracija
     * iznimka koja se baca u slučaju da je konfiguracijski zapis u neispravnome
     * obliku, formatu, vrijednosti i sl.
     * @throws java.io.IOException iznimka koja se baca u slučaju zauzetosti
     * porta mrežne utičnice
     */
    public void Kreni(String[] args) throws NemaKonfiguracije, NeispravnaKonfiguracija, IOException {
        Kraj();
        UlazniParametri = pretvoriUlazneParametreUString(args);
        RegEx = "^[\\w]+(.xml|.txt|.bin|.json) --brojDretvi ([1-9])+$";
        provijeriRegex(UlazniParametri, RegEx);
        ProvijeriPostojanjeDatoteke(args[0]);
        Konfiguracija = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);
        ProvjeriPostojanostKonfiguracijeUPostavkama(Konfiguracija, "port.avioni");
        PostaviPortAvioni();
        if (this.ZauzetostPorta(PortAvioni) == false) {
            throw new IOException("Mrežna utičnica vrijednosti port.avioni je zauzeta");
        }
        ProvjeraDatoteka(Konfiguracija);
        KreirajiNapuniKolekcije();
        ProvjeriSerijaliziranuDatoteku(Konfiguracija);
        PocetakRadaServisnihPoslova();
        brojDretvi = Integer.parseInt(args[2]);
        DretveZaObraduZahtjeva();

    }

    /**
     * Metoda koja ulazne podatke spaja u jedan string zapis.
     *
     * @param args polje ulaznih parametara
     * @return formatirani podaci u string formatu
     */
    public String pretvoriUlazneParametreUString(String[] args) {
        StringBuilder ulaz = new StringBuilder();
        for (String zapis : args) {
            ulaz.append(zapis)
                    .append(" ");
        }
        return ulaz.
                toString()
                .trim();
    }

    /**
     * Metoda koja provjerava ispravnost korisnikog unosa pomoću definiranog
     * regularnog izraza.U slučaju ispravnog unosa nastavlja se sa radom
     * programa, dok se u protivnome prekida rad programa.
     *
     * @param ulaz vrijednost korisnikovog unosa pretvorenog u zapis tipa String
     * @param regExIzraz uzorak regularnog izraza prema kojem se ispravlja
     * korisnikov unos.
     */
    public void provijeriRegex(String ulaz, String regExIzraz) {
        Pattern pattern = Pattern.compile(regExIzraz);
        Matcher m = pattern.matcher(ulaz);
        if (!m.find()) {
            System.err.println("Greška sa ulaznim parametrima. ");
            System.exit(0);
        }
    }

    
    public boolean ProvijeriPostojanjeDatoteke(String datoteka) throws NemaKonfiguracije {
        File file = new File(datoteka);
        if (!file.exists()) {
            throw new NemaKonfiguracije("Ne postoji unešena datoteka");
        }
        return true;
    }

    /**
     * Metoda koja provjerava postojanost i vrijednost parametra iz
     * konfiguracijske datoteke.
     *
     * @param konfiguracija objekt klase Konfiguracija pomoću koje pristupamo
     * vrijednostima postavki.
     * @param kljucPostavke ključ pomoću kojeg dohvaćamo vrijednost postavke.
     * @return istinitost postojanja vrijednosti ključa u postavkama
     * @throws org.foi.nwtis.hsostaric.konfiguracije.NeispravnaKonfiguracija
     * iznimka koja se baca u slučaju neispravne konfiguracije
     */
    public boolean ProvjeriPostojanostKonfiguracijeUPostavkama(Konfiguracija konfiguracija, String kljucPostavke) throws NeispravnaKonfiguracija {
        switch (kljucPostavke) {
            case "port.avioni":
                return ProvjeriIspravnostPostavkePorta(kljucPostavke);
            case "datoteka.korisnika":
                return ProvjeriPostojanostDatotekeKorisnika(kljucPostavke);
            case "datoteka.aerodromi":
                return ProvjeriPostojanostDatotekeAerodroma(kljucPostavke);
            default:
                System.err.println("Pogrešan ključ konfiguracije.");
                return false;
        }

    }

    /**
     * Metoda koja provjerava je li vrijednost postavke port.avioni u valjanom
     * intervalu te je li u ispravnom intervalu.
     *
     * @param naziv naziv ključa za port servera
     * @return ispravnost porta
     * @throws org.foi.nwtis.hsostaric.konfiguracije.NeispravnaKonfiguracija
     * pogreška koja se baca u slučaju neispravnog intervala ili vrijednosti
     * porta
     */
    public boolean ProvjeriIspravnostPostavkePorta(String naziv) throws NeispravnaKonfiguracija {
        boolean stanje = true;
        int vrijednostPorta;
        if (!Konfiguracija.postojiPostavka(naziv)) {
            stanje = false;
            throw new NeispravnaKonfiguracija("Postavka sa navedenim ključem nije pronađena");
        } else if (Konfiguracija.dajPostavku(naziv).equals("")) {
            stanje = false;
            throw new NeispravnaKonfiguracija("Nije definirana vrijednost konfiguracije u datotečnom sustavu");
        } else if (!Konfiguracija.dajPostavku(naziv).equals("")) {
            vrijednostPorta = Integer.parseInt(Konfiguracija.dajPostavku(naziv));
            if (naziv.equals("port.avioni")) {
                if (vrijednostPorta < 9000
                        || vrijednostPorta > 9999) {
                    stanje = false;
                    throw new NeispravnaKonfiguracija("Port nije u pravilnom rasponu !");
                }
            }
        }
        return stanje;
    }

    public void PostaviPortAvioni() {

        PortAvioni = Integer.parseInt(
                Konfiguracija.dajPostavku("port.avioni"));

    }

    /**
     * Metoda koja provjerava je li port za poslužitelja zauzet ili nije
     *
     * @param port vrijednost porta za koji se provjerava zauzetost
     * @return istinitost zauzetosti porta.
     */
    public boolean ZauzetostPorta(int port) {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.close();
        } catch (IOException exception) {
            return false;
        }
        return true;
    }

    public boolean ProvjeriPostojanostDatotekeKorisnika(String kljucPostavke) throws NeispravnaKonfiguracija {
        PronađiGreškuPostavkeDatoteke(kljucPostavke);
        return true;
    }

    public boolean ProvjeriPostojanostDatotekeAerodroma(String kljucPostavke) throws NeispravnaKonfiguracija {
        PronađiGreškuPostavkeDatoteke(kljucPostavke);
        return true;
    }

    /**
     * Metoda koja provjerava postoji li datoteka za korisnike, aerodorme.
     *
     * @param kljucPostavke ključ postavke za određenu datoteku
     * (datoteka.aerodromi, datoteka.korisnika i sl.)
     * @throws org.foi.nwtis.hsostaric.konfiguracije.NeispravnaKonfiguracija
     * iznimka koja se baca u slučaju neispravne vrijednosti konfiguracije
     */
    public void PronađiGreškuPostavkeDatoteke(String kljucPostavke) throws NeispravnaKonfiguracija {
        if (!Konfiguracija.postojiPostavka(kljucPostavke)) {
            throw new NeispravnaKonfiguracija("Postavka ne postoji u datotečnom sustavu");
        }
        if (Konfiguracija.dajPostavku(kljucPostavke)
                .equals("")) {
            throw new NeispravnaKonfiguracija("Konfiguracija nije definirana, tj. vraćen je prazan string.");
        } else {
            String imeDatoteke = Konfiguracija.dajPostavku(kljucPostavke);
            File f = new File(Konfiguracija.dajPostavku(kljucPostavke));
            String nastavak = (kljucPostavke.equals("datoteka.korisnika"))
                    ? ".xml" : ".csv";
            if (!f.exists()
                    || (imeDatoteke.toLowerCase()
                            .indexOf(nastavak) < 0)) {
                throw new NeispravnaKonfiguracija("Datoteka korisnika ili aerodroma ne postoji je pogrešnome formatu!");
            }
        }
    }

    public void ProvjeraDatoteka(Konfiguracija konfiguracija) throws NeispravnaKonfiguracija {
        ProvjeriPostojanostKonfiguracijeUPostavkama(konfiguracija, "datoteka.korisnika");
        ProvjeriPostojanostKonfiguracijeUPostavkama(konfiguracija, "datoteka.aerodromi");

    }

    /**
     * Metoda koja provjerava postoji li datoteka koja sadrži serijalizirane
     * avione(zrakoplove) te ukoliko postoji deserijalizira podatke te ih
     * ispiše.Ukoliko datoteka ne postoji ispisuje se poruka i nastavlja se sa
     * radom.
     *
     * @param konfiguracija parametar konfiguracije
     */
    public void ProvjeriSerijaliziranuDatoteku(Konfiguracija konfiguracija) {
        if (!Konfiguracija.postojiPostavka("datoteka.avioni")) {
            System.out.println("Postavka ne postoji u datotečnom sustavu.");
        }
        if (Konfiguracija.dajPostavku("datoteka.avioni")
                .equals("")) {
            System.out.println("Konfiguracija nije definirana, tj. vraćen je prazan string.");
        } else {
            String imeDatoteke = Konfiguracija.dajPostavku("datoteka.avioni");
            File f = new File(Konfiguracija.dajPostavku("datoteka.avioni"));
            if (!f.exists()
                    || (imeDatoteke.toLowerCase()
                            .indexOf(".bin") < 0)) {
                System.out.println("Datoteka s avionima ne postoji ili je u pogrešnome formatu!");
            } else {
                DeserijalizirajPodatke(f);
            }
        }
    }

    /**
     * Metoda u kojoj se kreiraju kolekcije za pohranu aerodroma i korisnika.
     *
     * @throws java.io.IOException iznimka koja se baca u slučaju neispravnosti
     * rada s datotekama
     */
    public void KreirajiNapuniKolekcije() throws IOException {
        Korisnici = new ArrayList<>(NapuniListuKorisnicima(
                Konfiguracija.dajPostavku("datoteka.korisnika")));
        Aerodoromi = Collections.synchronizedList(
                new ArrayList<>(NapuniKolekcijuAerodromima(
                        Konfiguracija.dajPostavku("datoteka.aerodromi"))));

    }

    /**
     * Metoda koja služi za deserijalizaciju datoteke sa zrakoplovima i njen
     * ispis.
     *
     * @param f naziv datoteke sa zrakoplovima (avionima)
     */
    public void DeserijalizirajPodatke(File f) {
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ListaAviona = (List<Avion>) ois.readObject();
            System.out.println("Ispis aviona:\n----------------\n----------------");
            ListaAviona.forEach((avion) -> {
                System.out.println(avion.toString());
            });
            System.out.println("----------------\n----------------");
        } catch (FileNotFoundException ex) {
            System.out.println("Nije pronađena datoteka za deserijalizaciju: " + ex);
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println(ex);
        }

    }

    /**
     * Metoda koja učitava datoteku sa korisnicima i obrađuje zapise te ih
     * sprema u kolekciju.
     *
     * @param nazivDatoteke naziv datoteke u kojoj su pohranjeni korisnici
     * @return kolekcija sa korisnicima
     * @throws java.io.IOException iznimka koja se baca ukoliko je došlo do
     * problema s datotekom
     */
    public ArrayList<Korisnik> NapuniListuKorisnicima(String nazivDatoteke) throws IOException {
        Korisnik korisnik;
        ArrayList<Korisnik> lista = new ArrayList<>();
        File file = new File(nazivDatoteke);
        Reader fileReader = new FileReader(file);
        BufferedReader bufReader = new BufferedReader(fileReader);
        String line;
        while ((line = bufReader.readLine()) != null) {
            if (line.contains("key")) {
                korisnik = new Korisnik(
                        UhvatiKorisnickoIme(line),
                        UhvatiLozinku(line));
                lista.add(korisnik);
            }
        }
        return lista;
    }

    /**
     * Metoda koja dohvaća korisničko ime iz pročitanog reda datoteke.
     *
     * @param red pročitani red datoetke
     * @return vrijendost korisničkog imena
     */
    public String UhvatiKorisnickoIme(String red) {
        return red.substring(
                red.indexOf("key=") + 5,
                red.indexOf(">") - 1).trim();
    }

    /**
     * Metoda koja dohvaća lozinku iz pročitanog reda datoteke.
     *
     * @param red vrijendost pročitanog reda datoteke
     * @return vrijednost lozinke
     */
    public String UhvatiLozinku(String red) {
        return red.substring(red.indexOf("\">") + 2,
                red.lastIndexOf("</"));
    }

    /**
     * Metoda koja učitava podatke iz datoteke sa aerodromima te ih nakon obrade
     * pohranjuje u kolekciju.
     *
     * @param nazivDatoteke naziv datoteke u kojoj su pohranjeni aerodromi
     * @return kolekcija sa aerodromima
     * @throws java.io.FileNotFoundException iznimka koja s ebaca u slučaju da
     * nije pronađena datoteka
     */
    public ArrayList<Aerodrom> NapuniKolekcijuAerodromima(String nazivDatoteke) throws FileNotFoundException, IOException {
        Aerodrom aerodrom;
        ArrayList<Aerodrom> lista = new ArrayList<>();
        FileReader fileReader = new FileReader(
                new File(nazivDatoteke));
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String red = bufferedReader.readLine();
        while ((red = bufferedReader.readLine()) != null) {
            aerodrom = kreirajAerodrom(red);
            lista.add(aerodrom);
        }
        fileReader.close();
        bufferedReader.close();
        return lista;
    }

    /**
     * Metoda u kojoj se obrađuju pročitani redovi datoteke sa aerodromima te se
     * obrađuju i spremaju u objekt koji se dodaje u kolekciju.
     *
     * @param red pročitani red datoteke
     * @return objekt klase Aerodrom koji se sprema u kolekciju sa aerodromima
     */
    public Aerodrom kreirajAerodrom(String red) {
        String oznaka;
        String[] kordinata;
        Koordinata koordinata;
        int pocetak = red.indexOf("\"");
        int zavrsetak = red.indexOf("\",");
        Aerodrom aerodrom = new Aerodrom();
        aerodrom.setIcao(
                red.substring(pocetak + 1, zavrsetak));
        int pocetak2 = red.indexOf(",\"", zavrsetak);
        int zavrsetak2 = red.indexOf("\",", pocetak2);
        aerodrom.setNaziv(
                red.substring(pocetak2 + 2, zavrsetak2));
        int pocetak3 = red.indexOf("\"", zavrsetak2);
        int zavrsetak3 = red.indexOf(",\"", pocetak3 + 2);
        oznaka = red.substring(pocetak3 + 2, zavrsetak3).replaceAll("\"", "");
        aerodrom.setDrzava(oznaka);
        String koordinate = red.substring(zavrsetak3 + 1).
                replaceAll("\"", "");
        kordinata = koordinate.split(",");
        koordinata = new Koordinata();
        koordinata.geoSirina = KonvertirajString(kordinata[0]);
        koordinata.geoDuzina = KonvertirajString(kordinata[1]);
        aerodrom.setKoridinate(koordinata);
        return aerodrom;
    }

    public double KonvertirajString(String value) {
        return Double.parseDouble(value.
                trim());
    }

    public String dajImeDretve(ThreadGroup tg) {
        String ime = "";
        ime = tg.getName()
                .substring(tg
                        .getName()
                        .indexOf("]_") + 1, tg
                        .getName()
                        .length());
        return ime;
    }

    public String dajImeKlijentske(ThreadGroup tg) {
        String ime = "";
        ime = tg.getName() + "_" + brojacVeze;
        return ime;
    }

    /**
     * Metoda u kojoj se provjerava ispravnost postavki za rad dretve sa
     * periodačnim izvršavanjem serijalizacije podataka aviona.Ukoliko su svi
     * podaci ispravni, kreira se grupa dretvi kojoj pripada dretva za
     * obavlajnje serijalizacije te se i ona sama pokreće.
     *
     * @throws org.foi.nwtis.hsostaric.konfiguracije.NeispravnaKonfiguracija
     * iznimka koja s ebaca u slučaju neispravnih vrijendosti konfiguracije
     */
    public void PocetakRadaServisnihPoslova() throws NeispravnaKonfiguracija {
        GrupaDretvi = new ThreadGroup("hsostaric_SD");
        String ime;
        ime = dajImeDretve(GrupaDretvi);
        if (!Konfiguracija.postojiPostavka("interval.pohrane.aviona")
                || Konfiguracija.dajPostavku("interval.pohrane.aviona")
                        .equals("")) {
            throw new NeispravnaKonfiguracija("Nije pronadjena konfiguracija za interval pohrane aviona");
        } else if (Integer.parseInt(
                Konfiguracija.dajPostavku("interval.pohrane.aviona")) < 1
                || Integer.parseInt(
                        Konfiguracija.dajPostavku("interval.pohrane.aviona")) > 999) {
            throw new NeispravnaKonfiguracija("Vrijednost intervala nije između 1 i 999.");
        }
        ServisnaDretva = new ServisAviona(Long.parseLong(Konfiguracija.dajPostavku("interval.pohrane.aviona")),
                Konfiguracija.dajPostavku("datoteka.avioni"),
                GrupaDretvi, ime + "_1");
        ServisnaDretva.start();
    }

    /**
     * Metoda koja je zadužena za obradu i kreiranja svih parametara potrebnih
     * za rad klijentskih zahtjeva.
     *
     * @throws org.foi.nwtis.hsostaric.konfiguracije.NemaKonfiguracije iznimka
     * koja se baca u slučaju nepostojanja konfiguracije za rad
     * @throws org.foi.nwtis.hsostaric.konfiguracije.NeispravnaKonfiguracija
     * iznimka koja se baca u slučaju neispravnih vrijednosti konfiguracijskih
     * vrijednosti
     */
    public void DretveZaObraduZahtjeva() throws NemaKonfiguracije, NeispravnaKonfiguracija {
        ListaKlijentskihDretvi = Collections.synchronizedList(new ArrayList<>());
        ProvjeriCekace("maks.cekaca");
        int maksCekaca = Integer.parseInt(
                Konfiguracija.dajPostavku("maks.cekaca"));

        ProvijeriOdnosCekacaiMaxDretvi(maksCekaca);
        try {
            serverSocket = new ServerSocket(PortAvioni, maksCekaca);
            DretveZahtjeva = new ThreadGroup("hsostaric_KD");
            while (!kraj) {

                ObradaKorisnika();
            }
        } catch (IOException ex) {
            System.out.println("Greška: " + ex.getMessage());
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
    }

    public void ProvijeriOdnosCekacaiMaxDretvi(int cekaci) throws NeispravnaKonfiguracija {
        if (cekaci < brojDretvi) {
            throw new NeispravnaKonfiguracija("Vrijednost ulaznih dretvi je veća od čekača!!!");
        }

    }

    /**
     * Metoda koja služi za obradu korisničkih zahtjeva, te prilikom toga
     * uspostavlja vezu sa korisnikom preko mrežne utičnice, dodaje dretve u
     * kolekciju te ih uklanja iz iste.
     *
     * @throws java.io.IOException iznimka koja se baca prilikom greške u
     * uspostavi veze
     * @throws java.lang.InterruptedException iznimka koja se baca prilikom
     * greške u prekidu rada dretve
     */
    public void ObradaKorisnika() throws IOException, InterruptedException {
        if (kraj == true
                && brojacVeze == 0) {
            System.exit(0);
        }
        veza = serverSocket.accept();
        brojacVeze++;
        if (kraj == false) {
            if (brojacVeze <= brojDretvi) {
                System.out.println(brojacVeze);
                DodajZahtjevUListu(veza);
                if (ListaKlijentskihDretvi.size() > 0) {
                    synchronized (ListaKlijentskihDretvi) {
                        for (iterator = ListaKlijentskihDretvi.iterator(); iterator.hasNext();) {
                            ZahtjevAviona dretva = iterator.next();
                            OdradiZahtjevKlijenta(dretva, iterator);
                        }
                    }
                }
            } else {
                PosaljiKlijentuGresku();
            }
        } else {
            PosaljiKlijentuGresku();
        }
    }

    /**
     * Metoda koja provjerava ispravnost vrijednosti čekača kao i njihovo
     * postojanje.
     *
     * @param kljuc naziv ključa za psotavku maksimalnih čekača
     * @throws org.foi.nwtis.hsostaric.konfiguracije.NemaKonfiguracije iznimka
     * koja se baca u slučaju nepostojanja konfiguracije za čekače
     * @throws org.foi.nwtis.hsostaric.konfiguracije.NeispravnaKonfiguracija
     * iznimka koja se baca u slučaju ako je vrijednost čekača prazan zapis
     */
    public void ProvjeriCekace(String kljuc) throws NemaKonfiguracije, NeispravnaKonfiguracija {
        if (!Konfiguracija.postojiPostavka(kljuc)) {
            throw new NemaKonfiguracije("Nije definirana postavka maks.cekaca");
        } else if (Konfiguracija.dajPostavku(kljuc).equals("")) {
            throw new NeispravnaKonfiguracija("Postavka maks.cekaca je prazan string");
        }
    }

    /**
     * Metoda koja pokreće klijentsku dretvu te je uklanja iz kolekcije.
     *
     * @param dretva dretva koja se pokreće
     * @param iterator kolekcija u kojoj se dretva nalazi
     * @throws java.lang.InterruptedException iznimka koja se baca ukoliko je
     * došlo do greške prilikom prekida dretve.
     */
    public void OdradiZahtjevKlijenta(ZahtjevAviona dretva, Iterator<ZahtjevAviona> iterator) throws InterruptedException {
        dretva.start();
        iterator.remove();
    }

    /**
     * Metoda u kojoj se kreira nova dretva te se dodaje u prikladnu kolekciju.
     *
     * @param veza mrežna utičnica koja se proslijeđuje dretvi putem
     * konstruktora
     */
    public void DodajZahtjevUListu(Socket veza) {
        String ime = dajImeKlijentske(DretveZahtjeva);
        zahtjevKorisnika = new ZahtjevAviona(veza, Konfiguracija, DretveZahtjeva, ime);
        ListaKlijentskihDretvi.add(zahtjevKorisnika);

    }

    /**
     * Metoda koja klijentu šalje grešku ukoliko više nema slobodnih dretvi ili
     * ako je server pred gašenjem.
     */
    public void PosaljiKlijentuGresku() {
        try {
            OutputStream outputStream = veza.getOutputStream();
            outputStream.write("ERROR 01; Nema vise slobodnih dretvi ili je server pred gašenjem;".getBytes());
            outputStream.flush();
            veza.shutdownOutput();
        } catch (IOException ex) {
            System.out.println("Greska: " + ex);
        }
    }
/**
 *Metoda koja serijalizira podatke nakon pritiska CTRL+C te prekida rad programa.
 */
    public void Kraj() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    System.out.println("Kraj programa!");
                    SerijalizirajZaKraj();
                } catch (InterruptedException ex) {
                    System.out.println("Greska: " + ex.getMessage());
                }
            }
        });
    }
    /**
     *Metoda koja serijalizira podatke ukoliko dolazi do prekida.
     */
    public synchronized void SerijalizirajZaKraj() {
        if (!ListaAviona.isEmpty()) {
            lock.lock();

            try {
                try (ObjectOutputStream oos = new ObjectOutputStream(
                        new FileOutputStream(new File(Konfiguracija.
                                dajPostavku("datoteka.avioni"))))) {
                    oos.writeObject(ListaAviona);
                }
            } catch (IOException ex) {
                kraj = true;
                ServisAviona.kraj = true;
            } finally {
                lock.unlock();
            }

        }
    }
}
