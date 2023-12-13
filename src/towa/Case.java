package towa;

/**
 * Case du plateau.
 * 
 * VOUS NE DEVEZ PAS MODIFIER CE FICHIER.
 */
public final class Case {

    /**
     * Caractère pour indiquer une case sans tour (dans l'attribut couleur de
     * Case).
     */
    public final static char CAR_VIDE = ' ';

    /**
     * Caractère pour afficher une tour noire sur le plateau "texte".
     */
    public final static char CAR_NOIR = 'N';

    /**
     * Caractère pour afficher une tour blanche sur le plateau "texte".
     */
    public final static char CAR_BLANC = 'B';

    /**
     * Caractère indiquant la nature "Terre" de la case (dans l'attribut nature
     * de Case).
     */
    public final static char CAR_TERRE = 'T';

    /**
     * Indique si la couleur de la tour sur cette case (s'il y en a une).
     * Convention : 'N' pour noir, 'B' pour blanc.
     */
    char couleur;
    
    /**
     * Hauteur d'une tour.
     */
    int hauteur;

    /**
     * Altitude d'une case.
     */
    int altitude;
    
    /**
     * Nature d'une case.
     */
    char nature;

    /**
     * Constructeur d'une case.
     * 
     * @param uneCouleur couleur de la tour sur cette case
     * @param uneHauteur indique la hauteur de la tour (le cas échéant)
     * @param uneAltitude indique l'altitude de la case
     * @param uneNature indique la nature de la case
     */
    public Case(char uneCouleur, int uneHauteur,
            int uneAltitude, char uneNature) {
        this.couleur = uneCouleur;
        this.hauteur = uneHauteur;
        this.altitude = uneAltitude;
        this.nature = uneNature;
    }
    
    /**
     * Indique si une tour est présente sur cette case (c'est-à-dire si elle
     * n'est pas vide).
     * 
     * @return vrai ssi une tour est présente
     */
    boolean tourPresente() {
        return this.couleur != CAR_VIDE;
    }    
}
