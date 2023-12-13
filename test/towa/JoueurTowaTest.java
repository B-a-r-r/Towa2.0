package towa;

import java.util.Arrays;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static towa.JoueurTowa.pionsFusionnesPerdus;
import static towa.JoueurTowa.premiereTourSurRouteCardinale;
import static towa.JoueurTowa.toursAdjascentes;
import static towa.JoueurTowa.toursAlliees;
import static towa.JoueurTowa.toursSurRoutesCardinales;

/**
 * Tests sur la classe JoueurTowa.
 */
public class JoueurTowaTest {

    /**
     * Test de la fonction actionsPossibles. Commentez les appels aux tests des
     * niveaux inférieurs, n'activez que le test du niveau à valider.
     */
    @Test
    public void testActionsPossibles() {
        testActionsPossibles_niveau9();
    }
    
    /**
     * Test de la fonction actionsPossibles, au niveau 9.
     */
    public void testActionsPossibles_niveau9() {
        JoueurTowa joueur = new JoueurTowa();
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU_NIVEAU2);
        // sur le plateau initial : 26 pions noirs et 20 pions blancs
        int niveau = 9;
        char couleur = Case.CAR_BLANC;
        // on lance actionsPossibles
        String[] actionsPossiblesDepuisPlateau = joueur.actionsPossibles(plateau, couleur, niveau);
        ActionsPossibles actionsPossibles
                = new ActionsPossibles(actionsPossiblesDepuisPlateau);
        actionsPossibles.afficher();
        // déploiement des kamikazes sur le bord nord
        assertTrue(actionsPossibles.contient("CN,18,9"));
        // déploiement des kamikazes sur le bord sud
        assertTrue(actionsPossibles.contient("CS,18,15"));
        // déploiement des kamikazes sur le bord est
        assertTrue(actionsPossibles.contient("CO,16,13"));
        // déploiement des kamikazes sur le bord ouest
        assertTrue(actionsPossibles.contient("CE,18,12"));
        
        // pose sur une case isolée : possible
        assertTrue(actionsPossibles.contient("PaP,26,21"));
        // double pose sur une case vide : possible
        assertTrue(actionsPossibles.contient("PbK,26,22"));
        // double pose sur une tour alliee : impossible
        assertFalse(actionsPossibles.contient("PcL,26,22"));
        // pose sur une tour de hauteur = 4 : impossible
        assertFalse(actionsPossibles.contient("PlF,26,21"));
        // pose sur une tour adverse : impossible
        assertFalse(actionsPossibles.contient("PbA,26,21"));
        // pose sur une case hors plateau : impossible
        assertFalse(actionsPossibles.contient("PzZ,26,21"));
        // double pose sur une case isolée : impossible 
        assertFalse(actionsPossibles.contient("PaP,26,22"));
        
        // activation cardinale limitée sur une case alliée : possible
        assertTrue(actionsPossibles.contient("AbH,24,20"));
        // activation cardinale limitée sur une case adverse : impossible
        assertFalse(actionsPossibles.contient("AiB,24,20"));
        // activation cardinale limitée sur une case vide : impossible
        assertFalse(actionsPossibles.contient("AoJ,24,20"));
        
        // fusion sur une case alliee : possible
        assertTrue(actionsPossibles.contient("FbL,26,20"));
        // fusion sur une case vide : possible
        assertTrue(actionsPossibles.contient("FkF,26,19"));
        // fusion sur une case adverse : impossible
        assertFalse(actionsPossibles.contient("FlE,26,20"));
        
    }
        
    @Test
    public void testNbPions() {
                
        // niveau 1 : le plateau doit être vierge     
        assertEquals(0, 
                JoueurTowa.nbPions(Utils.plateauDepuisTexte(
                                PLATEAU_NIVEAU1)).nbPionsNoirs);
        assertEquals(0, 
                JoueurTowa.nbPions(Utils.plateauDepuisTexte(
                        PLATEAU_NIVEAU1)).nbPionsBlancs);
        
        // niveau deux : le plateau contient 27 pions noirs et 20 pions blancs
        assertEquals(27, 
                JoueurTowa.nbPions(Utils.plateauDepuisTexte(
                        PLATEAU_NIVEAU2)).nbPionsNoirs);
        assertEquals(20, 
                JoueurTowa.nbPions(Utils.plateauDepuisTexte(
                        PLATEAU_NIVEAU2)).nbPionsBlancs);
    }
    
    @Test
    public void testPosePossible() {
        JoueurTowa joueur = new JoueurTowa();
        Coordonnees coord = new Coordonnees(0, 0);
        
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU_NIVEAU1);
        // pose pion blanc sur case vide : possible
        coord.ligne=0; coord.colonne=0;
        assertTrue(joueur.posePossible(plateau, coord, Case.CAR_BLANC));
        // pose pion noir sur case vide : possible
        assertTrue(joueur.posePossible(plateau, coord, Case.CAR_NOIR));
        // pose hors du plateau : impossible
        coord.ligne=-1; coord.colonne=-1;
        assertFalse(joueur.posePossible(plateau, coord, Case.CAR_NOIR));
        
        
        plateau = Utils.plateauDepuisTexte(PLATEAU_NIVEAU2);
        // on peut poser sur une case vide
        coord.ligne=0; coord.colonne=0;
        assertTrue(joueur.posePossible(plateau,coord,Case.CAR_BLANC));
        // on peut placer une tour s'il y en a déjà une alliée
        coord.ligne=1; coord.colonne=0;
        assertTrue(joueur.posePossible(plateau,coord,Case.CAR_NOIR));  
        // mais pas une tour adverse
        assertFalse(joueur.posePossible(plateau,coord,Case.CAR_BLANC));
        // pose sur tour alliée de hauteur >= 4 : impossible
        coord.ligne=2; coord.colonne=10;
        assertFalse(joueur.posePossible(plateau,coord,Case.CAR_NOIR));
        // une pose ne peut se faire hors du plateau
        coord.ligne=-1; coord.colonne=-1;
        assertFalse(joueur.posePossible(plateau, coord, Case.CAR_NOIR));
    }
    
    @Test
    public void testDoublePosePossible() {
        JoueurTowa joueur = new JoueurTowa();
        Coordonnees coord = new Coordonnees(0, 0);
        
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU_NIVEAU2);
        // double pose sur une case vide : possible
        assertTrue(joueur.poseDoublePossible(plateau,coord,Case.CAR_BLANC));
        // double pose sur case non vide : impossible
        coord.ligne=1; coord.colonne=0;
        assertFalse(joueur.poseDoublePossible(plateau,coord,Case.CAR_NOIR));  
        // mais pas une tour adverse
        assertFalse(joueur.posePossible(plateau,coord,Case.CAR_BLANC));
        // pose sur tour alliée de hauteur >= 4 : impossible
        coord.ligne=2; coord.colonne=10;
        assertFalse(joueur.posePossible(plateau,coord,Case.CAR_NOIR));
        // une pose ne peut se faire hors du plateau
        coord.ligne=-1; coord.colonne=-1;
        assertFalse(joueur.posePossible(plateau, coord, Case.CAR_NOIR));
    }
    
    @Test
    public void testActivationPossible() {
        JoueurTowa joueur = new JoueurTowa();
        Coordonnees coord = new Coordonnees(0, 6);
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU_NIVEAU2);
        
        // activer une tour de même couleur : possible
        assertTrue(joueur.activationPossible(plateau, coord, Case.CAR_BLANC));
        // activer une tour adverse : impossible 
        coord.ligne=1; coord.colonne=0;
        assertFalse(joueur.activationPossible(plateau, coord, Case.CAR_BLANC));
        // activation sur une case vide : impossible
        coord.ligne=0; coord.colonne=0;
        assertFalse(joueur.activationPossible(plateau, coord, Case.CAR_BLANC));
        // coordonnées hors plateau : impossible
        coord.ligne=-1; coord.colonne=-1;
        assertFalse(joueur.activationPossible(plateau, coord, Case.CAR_BLANC));
    }
    
    @Test
    public void testAjoutActionPose() {
        JoueurTowa joueur = new JoueurTowa();
        ActionsPossibles actions = new ActionsPossibles();
        NbPions nbPions = new NbPions(0, 0);
        // pour l'instant pas d'action possible
        assertEquals(0, actions.nbActions);
        // on crée le tableau d'actions et on en ajoute une
        joueur.ajoutActionPose(Coordonnees.depuisCars('f', 'D'), actions, 
                nbPions, Case.CAR_NOIR);
        // l'action est devenue possible
        assertTrue(actions.contient("PfD,1,0"));
        // une action possible mais qui n'a pas encore été ajoutée
        assertFalse(actions.contient("PbH,1,0"));
        // pour l'instant une seule action possible
        assertEquals(1, actions.nbActions);
        // ajout d'une deuxième action possible
        joueur.ajoutActionPose(Coordonnees.depuisCars('b', 'H'), actions, 
                nbPions, Case.CAR_NOIR);
        // l'action a bien été ajoutée
        assertTrue(actions.contient("PbH,1,0"));
        // désormais, deux actions possibles
        assertEquals(2, actions.nbActions);
    }
    
    /**
     * Test de la fonction ajoutActionDoublePose.
     */
    @Test
    public void testAjoutActionDoublePose() {
        JoueurTowa joueur = new JoueurTowa();
        ActionsPossibles actions = new ActionsPossibles();
        NbPions nbPions = new NbPions(0, 0);
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU_NIVEAU2);
        // pour l'instant pas d'action possible
        assertEquals(0, actions.nbActions);
        // on crée le tableau d'actions et on en ajoute une
        joueur.ajouterActionDoublePose(Coordonnees.depuisCars('f', 'D'), actions, 
                nbPions, Case.CAR_NOIR);
        // l'action est devenue possible
        assertTrue(actions.contient("PfD,2,0"));
        // une action possible mais qui n'a pas encore été ajoutée
        assertFalse(actions.contient("PbH,2,0"));
        // pour l'instant une seule action possible
        assertEquals(1, actions.nbActions);
        // ajout d'une deuxième action possible
        joueur.ajouterActionDoublePose(Coordonnees.depuisCars('b', 'H'), actions, 
                nbPions, Case.CAR_NOIR);
        // l'action a bien été ajoutée
        assertTrue(actions.contient("PbH,2,0"));
        // désormais, deux actions possibles
        assertEquals(2, actions.nbActions);
    }
    
    /**
     * Test des fonction de scan des case alentoures.
     */
    @Test
    public void testAdjascences() {
        JoueurTowa joueur = new JoueurTowa();
        Coordonnees coord = new Coordonnees(1, 1);
        /*
        * Tableau de 3x3 cases pour l'exemple : X:N où X est l'initiale de la 
        * couleur et N la hauteur de la tour présente (0 si vide)
        *       ------------------- 
        *       | B:1 | B:3 | N:1 |
        *       ------------------- 
        *       | V:0 | N:2 | B:2 |
        *       ------------------- 
        *       | N:2 | V:0 | B:2 |
        *       ------------------- 
        */
        Case[][] plateau = new Case[][]{
                                        {new Case(Case.CAR_BLANC, 1,0,'x'), 
                                        new Case(Case.CAR_BLANC, 3,0,'x'), 
                                        new Case(Case.CAR_NOIR, 1,0,'x')},
                                        {new Case(Case.CAR_VIDE, 0,0,'x'), 
                                        new Case(Case.CAR_NOIR, 2,0,'x'), 
                                        new Case(Case.CAR_BLANC, 2,0,'x')},
                                        {new Case(Case.CAR_NOIR, 2,0,'x'), 
                                        new Case(Case.CAR_VIDE, 0,0,'x'), 
                                        new Case(Case.CAR_BLANC, 2,0,'x')}
                                        };
        
        // une tour adverse possède une hauteur inférieur à la notre au centre :
        assertEquals(1, 
                     JoueurTowa.toursAdversesDestructibles(
                             plateau,
                             JoueurTowa.toursAdverses(plateau, 
                                     JoueurTowa.toursAdjascentes(plateau, 
                                             coord),
                                     Case.CAR_NOIR),
                             coord));
        // EN tout il y a 6 tours autour :
        assertEquals(6, JoueurTowa.toursAdjascentes(plateau, coord).length);
        // dont 2 alliées :
        assertEquals(2, JoueurTowa.toursAlliees(
                                            plateau, 
                                            JoueurTowa.toursAdjascentes(
                                                    plateau, 
                                                    coord),
                                            Case.CAR_NOIR).length);
        // et 4 ennemies
        assertEquals(4, JoueurTowa.toursAdverses(
                                            plateau, 
                                            JoueurTowa.toursAdjascentes(
                                                    plateau, 
                                                    coord),
                                            Case.CAR_NOIR).length);
    }
    
    @Test 
    public void testConcatTableauCoordonnees() {
        Coordonnees[] tab1 = new Coordonnees[5];
        Coordonnees[] tab2 = new Coordonnees[4];
        Coordonnees[] concatenation = JoueurTowa.concatTableauCoordonnees(tab1, tab2);
        
        assertTrue(concatenation.length == 9);
        Assert.assertArrayEquals(Arrays.copyOf(concatenation, tab1.length), tab1);
    }
    
    @Test
    public void testTrierDirection() {
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU_NIVEAU2);
        Coordonnees coord = Coordonnees.depuisCars('m', 'F');
        
        Coordonnees[] tab = JoueurTowa.toursAdjascentes(plateau, coord);
        Coordonnees[][]  tabTrie = JoueurTowa.trierDirections(tab, coord);
        
        assertEquals(tab[0], tabTrie[4][0]);
        assertEquals(tab[1], tabTrie[0][0]);
        assertEquals(tab[2], tabTrie[5][0]);
        assertEquals(tab[3], tabTrie[3][0]);
        assertEquals(tab[4], tabTrie[2][0]);
        assertEquals(tab[5], tabTrie[7][0]);
        assertEquals(tab[6], tabTrie[1][0]);
        assertEquals(tab[7], tabTrie[6][0]);
    }
    
    @Test
    public void testPremiereTour() {
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU_NIVEAU2);
        Coordonnees coord = Coordonnees.depuisCars('m', 'F');
        Coordonnees[] adj = JoueurTowa.toursAdjascentes(plateau, coord);
        
        Assert.assertArrayEquals(
            JoueurTowa.premiereTourSurRouteCardinale(
                    JoueurTowa.toursAdjascentes(plateau, coord), 
                    JoueurTowa.toursSurRoutesCardinales(plateau, coord), 
                    coord),
            new Coordonnees[]{adj[1],adj[6],adj[4],adj[3],adj[2],adj[0],adj[7],adj[5]});
    }
    
    @Test
    public void testPionsFusionnesPerdus() {
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU_NIVEAU2);
        Coordonnees coord = Coordonnees.depuisCars('h', 'K');
        
        assertEquals(3, pionsFusionnesPerdus(
                            plateau,
                            premiereTourSurRouteCardinale(
                                toursAlliees(
                                        plateau, 
                                        toursAdjascentes(
                                                plateau, 
                                                coord), 
                                        Case.CAR_NOIR), 
                                toursAlliees(
                                        plateau, 
                                        toursSurRoutesCardinales(
                                                plateau, 
                                                coord), 
                                        Case.CAR_NOIR), 
                                coord),
                        coord));
        
        coord = Coordonnees.depuisCars('h', 'C');
        assertEquals(0, pionsFusionnesPerdus(
                            plateau,
                            premiereTourSurRouteCardinale(
                                toursAlliees(
                                        plateau, 
                                        toursAdjascentes(
                                                plateau, 
                                                coord), 
                                        Case.CAR_NOIR), 
                                toursAlliees(
                                        plateau, 
                                        toursSurRoutesCardinales(
                                                plateau, 
                                                coord), 
                                        Case.CAR_NOIR), 
                                coord),
                        coord));
    }
    
    /**
     * Un plateau de base, sous forme de chaîne. Pour construire une telle
     * chaîne depuis votre sortie.log, déclarez simplement : final String
     * MON_PLATEAU = ""; puis copiez le plateau depuis votre sortie.log, et
     * collez-le entre les guillemets. Puis Alt+Shift+f pour mettre en forme.
     */
    final String PLATEAU_NIVEAU1
            = "   A   B   C   D   E   F   G   H   I   J   K   L   M   N   O   P \n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "a|   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "b|   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "c|   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "d|   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "e|   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "f|   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "g|   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "h|   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "i|   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "j|   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "k|   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "l|   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "m|   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "n|   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "o|   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "p|   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+";

    final String PLATEAU_NIVEAU2
            = "   A   B   C   D   E   F   G   H   I   J   K   L   M   N   O   P \n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "a|   |   |   |   |   |   |B1 |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "b|N1 |   |   |   |   |   |   |B1 |   |   |   |B1 |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "c|   |   |B1 |   |B1 |   |   |   |   |   |N4 |B1 |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "d|   |   |   |   |   |   |   |   |B1 |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "e|B1 |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "f|   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "g|   |   |B1 |   |   |   |   |   |   |N1 |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "h|   |   |   |   |   |   |   |   |   |   |N1 |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "i|   |N1 |N1 |   |   |   |   |   |   |   |   |   |N1 |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "j|   |   |   |   |   |   |   |N1 |   |   |   |B1 |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "k|   |   |   |   |N1 |   |   |   |   |N2 |   |   |   |   |B1 |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "l|   |   |   |   |N3 |B4 |B1 |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "m|   |   |   |   |B1 |B2 |N1 |   |   |N1 |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "n|   |   |   |   |N1 |N1 |N2 |   |   |N1 |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "o|   |N1 |   |   |   |   |   |N1 |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "p|   |   |   |   |   |   |B1 |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n";
}
