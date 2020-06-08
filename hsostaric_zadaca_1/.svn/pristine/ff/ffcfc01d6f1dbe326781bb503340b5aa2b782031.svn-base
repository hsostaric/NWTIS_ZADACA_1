/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.zadaca_1;

import org.foi.nwtis.hsostaric.konfiguracije.Konfiguracija;
import org.foi.nwtis.hsostaric.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.hsostaric.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.hsostaric.konfiguracije.NemaKonfiguracije;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author Hrvoje Šoštarić
 */
public class ServerSimulatoraLetaTest {

    String datoteka = "hsostaric.txt";
    Konfiguracija k = null;

    public ServerSimulatoraLetaTest() {
    }

    @BeforeClass
    public static void setUpClass() {
       

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws NemaKonfiguracije, NeispravnaKonfiguracija {
    k = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datoteka);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class ServerSimulatoraLeta.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = {"hsostaric.txt"};
        ServerSimulatoraLeta.main(args);

    }

    /**
     * Test of Kreni method, of class ServerSimulatoraLeta.
     */
    @Test
    public void testKreni() throws Exception {
        String[] args = {"hsostaric.txt"};
        ServerSimulatoraLeta instance = new ServerSimulatoraLeta();
        instance.Kreni(args);
        // TODO review the generated test code and remove the default call to fail.
      
    }

    /**
     * Test of ProvjeriUlazneParametre method, of class ServerSimulatoraLeta.
     */
    @Test
    public void testProvjeriUlazneParametre() {
        System.out.println("ProvjeriUlazneParametre");
        String args = "korisnik.nastavak";
        String regex = "^([\\w-]+)(.xml|.txt|.bin|.json)$";
        ServerSimulatoraLeta instance = new ServerSimulatoraLeta();
        boolean expResult = true;
        boolean result = instance.ProvjeriUlazneParametre(args, regex);
        assertEquals(expResult, result);
    }

    /**
     * Test of ProvijeriPostojanjeDatoteke method, of class
     * ServerSimulatoraLeta.
     */
    @Test
    public void testProvijeriPostojanjeDatoteke() throws Exception {
        System.out.println("ProvijeriPostojanjeDatoteke");
        ServerSimulatoraLeta instance = new ServerSimulatoraLeta();
        boolean expResult = true;
        boolean result = instance.ProvijeriPostojanjeDatoteke(datoteka);
        assertEquals(expResult, result);
    }

    /**
     * Test of ProvjeriPort method, of class ServerSimulatoraLeta.
     * @throws java.lang.Exception
     */
    @Test
    public void testProvjeriPort() throws Exception {
        System.out.println("ProvjeriPort");
        ServerSimulatoraLeta instance = new ServerSimulatoraLeta();
        instance.ProvjeriPort(k);
       
    }

    /**
     * Test of ZauzetostPorta method, of class ServerSimulatoraLeta.
     */
    @Test
    public void testZauzetostPorta() {
        System.out.println("ZauzetostPorta");
        int port = Integer.parseInt(k.dajPostavku("port.simulator"));
        ServerSimulatoraLeta instance = new ServerSimulatoraLeta();
        boolean expResult = true;
        boolean result = instance.ZauzetostPorta(port);
        assertEquals(expResult, result);
        
     
    }

    /**
     * Test of IspitajCekace method, of class ServerSimulatoraLeta.
     * @throws java.lang.Exception
     */
    @Test
    public void testIspitajCekace() throws Exception {
        System.out.println("IspitajCekace");
        String kljuc = k.dajPostavku("dsf");
        ServerSimulatoraLeta instance = new ServerSimulatoraLeta();
        instance.IspitajCekace(kljuc);
      
    }

    /**
     * Test of ObradiKorisnika method, of class ServerSimulatoraLeta.
     * @throws java.lang.Exception
     */   
    
    @Ignore
    public void testObradiKorisnika() throws Exception {
        System.out.println("ObradiKorisnika");
        int port = Integer.parseInt(k.dajPostavku("maks"));
        int cekaci = 0;
        ServerSimulatoraLeta instance = new ServerSimulatoraLeta();
        instance.ObradiKorisnika(port, cekaci);
      
    }

}
