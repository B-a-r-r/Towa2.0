package towa;

/**
 * Quelques fonctions utiles au projet. Vous devez comprendre ce que font ces
 * méthodes (voir leur documentation), mais pas comment elles le font (leur
 * code).
 *
 * VOUS NE DEVEZ PAS MODIFIER CE FICHIER.
 */
public class Utils {

    /**
     * Construit un plateau à partir de sa représentation sour forme texte,
     * comme renvoyé par formatTexte(), avec coordonnées et séparateurs.
     *
     * @param texteOriginal le texte du plateau
     * @return le plateau
     */
    public static Case[][] plateauDepuisTexte(final String texteOriginal) {
        final Case[][] plateau = new Case[Coordonnees.NB_LIGNES][Coordonnees.NB_COLONNES];
        final String[] lignes = texteOriginal.split("\n");
        for (int lig = 0; lig < Coordonnees.NB_LIGNES; lig++) {
            final String ligne1 = lignes[2 * lig + 1];
            final String ligne2 = lignes[2 * lig + 2];
            for (int col = 0; col < Coordonnees.NB_COLONNES; col++) {
                final String codageLigne1 = ligne1.substring(2 + 4 * col, 2 + 4 * col + 3);
                final String codageLigne2 = ligne2.substring(2 + 4 * col, 2 + 4 * col + 3);
                plateau[lig][col] = caseDepuisCodage(codageLigne1, codageLigne2);
            }
        }
        return plateau;
    }

    /**
     * Construit une case depuis son codage.
     *
     * @param ligne1 codage de la case, première ligne
     * @param ligne2 codage de la case, deuxième ligne
     * @return case correspondante
     */
    public static Case caseDepuisCodage(final String ligne1, final String ligne2) {
        // vérification des arguments
        if (ligne1.length() != 3 || ligne2.length() != 3) {
            throw new IllegalArgumentException(
                    "Un codage de ligne doit être sur 3 caractères par ligne.");
        }
        final Case laCase = new Case(Case.CAR_VIDE, 0, 0, Case.CAR_TERRE);
        //
        // ligne 1
        //
        // 1er caractère : nature
        char carNature = ligne1.charAt(0);
        if (carNature == '-') {
            laCase.nature = Case.CAR_TERRE;
        } else {
            laCase.nature = carNature;
        }
        // 2ème caractère : rien
        // 3ème caractère : rien
        //
        // ligne 2
        //
        // 1er caractère : couleur (indique aussi la présence d'une tour)
        char carCouleur = ligne2.charAt(0);
        if (carCouleur != Case.CAR_VIDE) {
            if (carCouleur != Case.CAR_NOIR && carCouleur != Case.CAR_BLANC) {
                throw new IllegalArgumentException(
                        "Caractère couleur non admis : " + carCouleur);
            }
        }
        laCase.couleur = carCouleur;
        // 2ème caractère : hauteur
        char carHauteur = ligne2.charAt(1);
        if (!laCase.tourPresente()) {
            if (carHauteur != ' ') {
                throw new IllegalArgumentException("Cette case ne contient pas de tour,"
                        + " donc ne devrait pas avoir de hauteur associée.");
            }
        } else {
            laCase.hauteur = Integer.parseInt("" + carHauteur);
        }
        // 3ème caractère : altitude
        char carAltitude = ligne2.charAt(2);
        if (carAltitude != ' ') {
            laCase.altitude = Integer.parseInt("" + carAltitude);
        }
        return laCase;
    }
}
