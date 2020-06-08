/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.zadaca_1;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Klasa koja je ujedno i dretva koja periodično izvršava serijalizaciju aviona
 * iz prikladne kolekcije za definirani interval iz postavki.
 *
 * @author Hrvoje Šoštarić
 */
public class ServisAviona extends Thread {

    private long intervalPohrane;
    public static String nazivDatoteke;

    public static boolean kraj = false;

    /**
     * Konstruktor klase ServisAviona koji prima sljedeće parametre:
     *
     * @param intervalPohrane vrijednost vremena u sekundama za koji dretva
     * serijalizira podatke
     * @param nazivDatoteke vrijednost koja sadrži naziv datoteke u koji
     * datoteka serijalizira avione
     * @param group grupa dretvi kojoj pripada servisna dretva
     * @param name ime dretve
     */
    public ServisAviona(long intervalPohrane, String nazivDatoteke, ThreadGroup group, String name) {
        super(group, name);
        this.intervalPohrane = intervalPohrane;
        this.nazivDatoteke = nazivDatoteke;

    }

    /**
     * Metoda koja sadrži radnju koja se izvodi nakon pokretanja dretve i poziva
     * metodu za serijalizaciju.
     */
    @Override
    public void run() {
        while (!kraj) {
            try {
                Serijaliziraj();
            } catch (IOException | InterruptedException ex) {
                System.out.println("Greška kod serijalizacije : " + ex.getMessage());
            }
        }
    }

    /**
     * Metoda koja serijalizira kolekciju aviona u datoteku koja se proslijeđena
     * konstruktoru.Valja napomenuti da se serijalizacija obavlja međusobno
     * isključivo u odnosu na dretvu koja unosi podatke o avionima u kolekciju.
     *
     * @throws java.io.IOException iznimka koja se baca ukoliko je došlo do
     * greške prilikom rada s datotekom
     * @throws java.lang.InterruptedException iznimka koja se baca ukoliko je
     * došlo do greške prilikom prekida rada dretve
     */
    public synchronized void Serijaliziraj() throws IOException, InterruptedException {
        if (!ServerAviona.ListaAviona.isEmpty()) {
            try {

                long poc = System.currentTimeMillis();
                synchronized (ServerAviona.ListaAviona) {
                    FileOutputStream out = new FileOutputStream(this.nazivDatoteke);
                    try (ObjectOutputStream s = new ObjectOutputStream(out)) {
                        s.writeObject(ServerAviona.ListaAviona);
                    }
                }
                long trajanje = System.currentTimeMillis() - poc;
                long pocek = intervalPohrane * 1000;
                sleep(pocek - trajanje);
            } catch (IOException | InterruptedException ex) {
                System.out.println("Greška kod serijalizacije: " + ex.getMessage());
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    @Override
    public synchronized void start() {
        super.start();
    }

}
