package towa;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests unitaires pour la classe Utils.
 */
public class UtilsTest {

    /**
     * Test de carLigneVersNum pour les valeurs admises.
     */
    @Test
    public void testCarLigneVersNum() {
        assertEquals(0, Coordonnees.carLigneVersNum('a'));
        assertEquals(1, Coordonnees.carLigneVersNum('b'));
        assertEquals(15, Coordonnees.carLigneVersNum('p'));
    }

    /**
     * Test de carLigneVersNum pour un argument trop petit.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCarLigneVersNumException1() {
        final char c = 'a' - 1;
        Coordonnees.carLigneVersNum(c);
    }

    /**
     * Test de carLigneVersNum pour un argument trop grand.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCarLigneVersNumException2() {
        final char c = 'a' + 16;
        Coordonnees.carLigneVersNum(c);
    }

    /**
     * Test de carColonneVersNum pour les valeurs admises.
     */
    @Test
    public void testCarColonneVersNum() {
        assertEquals(0, Coordonnees.carColonneVersNum('A'));
        assertEquals(1, Coordonnees.carColonneVersNum('B'));
        assertEquals(15, Coordonnees.carColonneVersNum('P'));
    }

    /**
     * Test de carColonneVersNum pour un argument trop petit.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCarColonneVersNumException1() {
        final char c = 'A' - 1;
        Coordonnees.carColonneVersNum(c);
    }

    /**
     * Test de carColonneVersNum pour un argument trop grand.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCarColonneVersNumException2() {
        final char c = 'A' + 16;
        Coordonnees.carColonneVersNum(c);
    }

    /**
     * Test de la fonction caseDepuisCodage.
     */
    @Test
    public void testCaseDepuisCodage() {
        Case laCase;
        // case vide
        laCase = Utils.caseDepuisCodage("---", "   ");
        assertEquals(Case.CAR_TERRE, laCase.nature);
        assertEquals(0, laCase.hauteur);
        assertEquals(Case.CAR_VIDE, laCase.couleur);
        assertEquals(0, laCase.altitude);
        /*
        // un pommier possédé par les rouges
        laCase = Utils.caseDepuisCodage("---", "PR4");
        assertEquals(Utils.CAR_TERRE, laCase.nature);
        assertEquals(Utils.CAR_POMMIER, laCase.espece);
        assertEquals(Utils.CAR_ROUGE, laCase.couleur);
        assertEquals(4, laCase.vitalite);
        // un pommier possédé par les bleus
        laCase = Utils.caseDepuisCodage("---", "PB9");
        assertEquals(Utils.CAR_TERRE, laCase.nature);
        assertEquals(Utils.CAR_POMMIER, laCase.espece);
        assertEquals(Utils.CAR_BLEU, laCase.couleur);
        assertEquals(9, laCase.vitalite);
*/
    }
}
