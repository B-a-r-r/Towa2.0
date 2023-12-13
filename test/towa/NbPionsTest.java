package towa;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tetst unitaires de la classe NbPions.
 */
public class NbPionsTest {
    
    @Test
    public void testNbPions() {
        // constructeur par défaut
        NbPions pions0 = new NbPions();
        assertEquals(0, pions0.nbPionsNoirs);
        assertEquals(0, pions0.nbPionsBlancs);
        // constructeur paramétré
        NbPions pions1 = new NbPions(17, 48);
        assertEquals(17, pions1.nbPionsNoirs);
        assertEquals(48, pions1.nbPionsBlancs);
        // constructeur par copie
        NbPions pions2 = new NbPions(pions1);
        assertEquals(17, pions2.nbPionsNoirs);
        assertEquals(48, pions2.nbPionsBlancs);
    }
}
