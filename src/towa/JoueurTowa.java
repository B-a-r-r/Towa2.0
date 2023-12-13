package towa;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Joueur implémentant les actions possibles à partir d'un plateau, pour un
 * niveau donné.
 */
public  class JoueurTowa implements IJoueurTowa {
    /**
     * Cette méthode renvoie, pour un plateau donné et un joueur donné, toutes
     * les actions possibles pour ce joueur.
     *
     * @param plateau le plateau considéré
     * @param couleurJoueur couleur du joueur
     * @param niveau le niveau de la partie à jouer
     * @return l'ensemble des actions possibles
     */
    @Override
    public String[] actionsPossibles(Case[][] plateau, char couleurJoueur, int niveau) {
        // afficher l'heure de lancement
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        System.out.println("actionsPossibles : lancement le " + format.format(new Date()));
        // se préparer à stocker les actions possibles
        ActionsPossibles actions = new ActionsPossibles();
        // on compte le nombre de pions sur le plateau avant action
        NbPions nbPions = nbPions(plateau);
        // pour chaque ligne
        for (int lig = 0; lig < Coordonnees.NB_LIGNES; lig++) {
            // pour chaque colonne
            for (int col = 0; col < Coordonnees.NB_COLONNES; col++) {
                Coordonnees coord = new Coordonnees(lig, col);
                
                switch (niveau) {
                  
                    case 9:
                        if (posePossible(plateau, coord, couleurJoueur)) {
                            if (poseDoublePossible(plateau, coord, couleurJoueur)) {
                                ajouterActionDoublePose(coord, actions, nbPions, couleurJoueur);
                            } else {
                                ajoutActionPose(coord, actions, nbPions, couleurJoueur);
                            }
                        }
                        if (activationPossible(plateau, coord, couleurJoueur)) {
                            ajoutActionActivationCardinaleLimitee(plateau, coord, actions, nbPions, couleurJoueur);
                        }
                        if (fusionPossible(plateau, coord, couleurJoueur)) {
                            ajoutActionFusion(plateau, coord, actions, nbPions, couleurJoueur);
                        }
                        break;
                    
                    default:
                        System.err.println("Aucun niveau correspondant.");
                }  
            }
        }
        ajoutActionKamikazes(plateau, actions, nbPions);
        
        System.out.println("actionsPossibles : fin");
        return actions.nettoyer();
    }

    /**
     * Indique s'il est possible de poser un pion sur une case pour ce plateau,
     * ce joueur, dans ce niveau.
     *
     * @param plateau le plateau
     * @param coord coordonnées de la case à considérer
     * @param couleur couleur du joueur
     * @return vrai si la pose d'un pion sur cette case est autorisée dans ce
     * niveau
     */
    boolean posePossible(Case[][] plateau, Coordonnees coord, char couleur) {
        return
            estDansPlateau(coord)
            &&
                ((plateau[coord.ligne][coord.colonne].tourPresente()
                && plateau[coord.ligne][coord.colonne].couleur == couleur
                && plateau[coord.ligne][coord.colonne].hauteur<4)

                ||

                !(plateau[coord.ligne][coord.colonne].tourPresente()));
    }
    
    /**
     * Indique s'il est possible de faire une double pose sur une case pour 
     * ce plateau, ce joueur, dans ce niveau.
     *
     * @param plateau le plateau
     * @param coord coordonnées de la case à considérer
     * @param couleur couleur du joueur
     * @return vrai si la pose d'un pion sur cette case est autorisée dans ce
     * niveau
     */
    boolean poseDoublePossible(Case[][] plateau, Coordonnees centre, char couleur) {
        return 
            estDansPlateau(centre)
            &&
                !(plateau[centre.ligne][centre.colonne].tourPresente())
                
                &&
                
                toursAdverses(
                    plateau, 
                    toursAdjascentes(
                            plateau, 
                            centre), 
                    couleur).length > 0;
    }
    
    /**
     * Indique s'il est possible d'activer une toure sur une case 
     * pour ce plateau, ce joueur, dans ce niveau.
     *
     * @param plateau le plateau
     * @param coord coordonnées de la case à considérer
     * @param couleur couleur du joueur
     * @return vrai si la pose d'un pion sur cette case est autorisée dans ce
     * niveau
     */
    boolean activationPossible(Case[][] plateau, Coordonnees coord, char couleur) {
        return 
            
            estDansPlateau(coord)
            &&
                
                ((plateau[coord.ligne][coord.colonne].tourPresente()
                && plateau[coord.ligne][coord.colonne].couleur == couleur));
    }
    
    boolean fusionPossible(Case[][] plateau, Coordonnees coord, char couleur) {
        return
                estDansPlateau(coord)
                &&
                    (plateau[coord.ligne][coord.colonne].tourPresente()
                    && plateau[coord.ligne][coord.colonne].couleur == couleur);

    }

    /**
     * Nombre de pions sur le plateau, de chaque couleur.
     *
     * @param plateau le plateau
     * @return le nombre de pions sur le plateau, de chaque couleur
     */
    static NbPions nbPions(Case[][] plateau) {
        int blancs = 0;
        int noires = 0;
        for (Case[] plateau1 : plateau) {
            for (Case colonne : plateau1) {
                if (colonne.tourPresente()) {
                    switch (colonne.couleur) {
                        case 'N':
                            noires+=colonne.hauteur;
                            break;
                        default:
                            blancs+=colonne.hauteur;
                            break;
                    }
                }
            }
        }
        return new NbPions(noires, blancs); 
    }

    /**
     * Ajout d'une action de pose dans l'ensemble des actions possibles.
     *
     * @param coord coordonnées de la case où poser un pion
     * @param actions l'ensemble des actions possibles (en construction)
     * @param nbPions le nombre de pions par couleur sur le plateau avant de 
     * jouer l'action
     * @param couleur la couleur du pion à ajouter
     */
    void ajoutActionPose(Coordonnees coord, ActionsPossibles actions,
            NbPions nbPions, char couleur) {
        
        int pionsNoirs = nbPions.nbPionsNoirs;
        int pionsBlancs = nbPions.nbPionsBlancs;
        
        if (couleur == Case.CAR_NOIR) {
            pionsNoirs = nbPions.nbPionsNoirs + 1;
        } else if (couleur == Case.CAR_BLANC) {
            pionsBlancs = nbPions.nbPionsBlancs + 1;
        }
        
        String action = "P" + coord.carLigne() + coord.carColonne() + "," 
                + (pionsNoirs) + ","
                + (pionsBlancs);
        actions.ajouterAction(action);
    }
    
    /**
     * Ajout d'une action de double pose dans l'ensemble des actions possibles.
     * 
     * @param coord les coordonnées de la double pose.
     * @param actions les actions déjà stockées
     * @param nbPions le nombre de pions du plateau en cours.
     * @param couleur la couleur de la case de double pose.
     */
    void ajouterActionDoublePose(Coordonnees coord, ActionsPossibles actions,
            NbPions nbPions, char couleur) {
        
        int pionsNoirs = nbPions.nbPionsNoirs;
        int pionsBlancs = nbPions.nbPionsBlancs;
        
        if (couleur == Case.CAR_BLANC) {
            pionsBlancs = nbPions.nbPionsBlancs + 2;
        } else if (couleur == Case.CAR_NOIR) {
            pionsNoirs = nbPions.nbPionsNoirs + 2;
        }
        
        String action = "P" + coord.carLigne() + coord.carColonne() + "," 
                + (pionsNoirs) + ","
                + (pionsBlancs);
        actions.ajouterAction(action);
    }
    
    /**
     * Ajout d'une action d'activation dans l'ensemble des actions possibles.
     *
     * @param coord coordonnées de la case où activer un pion
     * @param actions l'ensemble des actions possibles (en construction)
     * @param nbPions le nombre de pions par couleur sur le plateau avant de 
     * jouer l'action
     * @param couleur la couleur du pion à activer
     */
    void ajoutActionActivationCardinaleLimitee(Case[][] plateau, Coordonnees coord, 
            ActionsPossibles actions, NbPions nbPions, char couleur) {
        
        int pionsNoirs = nbPions.nbPionsNoirs;
        int pionsBlancs = nbPions.nbPionsBlancs;
        
        if (couleur == Case.CAR_NOIR) {
            pionsBlancs = 
                nbPions.nbPionsBlancs 
                    - toursAdversesDestructibles(plateau, 
                            toursAdverses(
                                    plateau, 
                                    premiereTourSurRouteCardinale(
                                    toursAdjascentes(
                                                    plateau, 
                                                    coord), 
                                    toursSurRoutesCardinales(
                                                    plateau, 
                                                    coord),
                                    coord),
                                    couleur),
                            coord);
            
        } else if (couleur == Case.CAR_BLANC) {
            pionsNoirs = 
                nbPions.nbPionsNoirs
                    - toursAdversesDestructibles(plateau, 
                            toursAdverses(
                                    plateau, 
                                    premiereTourSurRouteCardinale(
                                    toursAdjascentes(
                                                    plateau, 
                                                    coord), 
                                    toursSurRoutesCardinales(
                                                    plateau, 
                                                    coord),
                                    coord),
                                    couleur),
                            coord); 
        }
        
        String action = "A" + coord.carLigne() + coord.carColonne() + "," 
                + (pionsNoirs) + ","
                + (pionsBlancs);
        actions.ajouterAction(action);
    }
    
    /**
     * Ajout d'une action de fusion dans l'ensemble des actions possibles.
     * 
     * @param coord les coordonnées de la fusion.
     * @param actions les actions déjà stockées
     * @param nbPions le nombre de pions du plateau en cours.
     * @param couleur la couleur de la case de fusionS.
     */
    void ajoutActionFusion(Case[][] plateau, Coordonnees coord, ActionsPossibles actions,
            NbPions nbPions, char couleur) {
        
        int pionsNoirs = nbPions.nbPionsNoirs;
        int pionsBlancs = nbPions.nbPionsBlancs;
        
        if (couleur == Case.CAR_BLANC) {
            pionsBlancs = nbPions.nbPionsBlancs
                    - pionsFusionnesPerdus(
                            plateau,
                            toursAlliees(plateau,
                                premiereTourSurRouteCardinale(
                                            toursAdjascentes(
                                                    plateau, 
                                                    coord),
                                            toursSurRoutesCardinales(
                                                    plateau, 
                                                    coord), 
                                    coord),
                                couleur),
                        coord);
            
        } else if (couleur == Case.CAR_NOIR) {
            pionsNoirs = nbPions.nbPionsNoirs 
                    - pionsFusionnesPerdus(
                            plateau,
                            toursAlliees(plateau,
                                premiereTourSurRouteCardinale(
                                            toursAdjascentes(
                                                    plateau, 
                                                    coord),
                                            toursSurRoutesCardinales(
                                                    plateau, 
                                                    coord), 
                                    coord),
                                couleur),
                        coord);
        }
        
        String action = "F" + coord.carLigne() + coord.carColonne() + "," 
                + (pionsNoirs) + ","
                + (pionsBlancs);
        actions.ajouterAction(action);
    }
    
    /**
     * Ajout d'une action de lancement de chat kamikazes dans l'ensemble des 
     * actions possibles.
     * 
     * @param actions les actions déjà stockées
     * @param nbPions le nombre de pions du plateau en cours.
     */
    void ajoutActionKamikazes(Case[][] plateau, ActionsPossibles actions,
            NbPions nbPions) {
        
        // bord nord
        deployerKamicazesNord(plateau, nbPions, actions);
        // bord sud
        deployerKamicazesSud(plateau, nbPions, actions);
        // bord est
        deployerKamicazesEst(plateau, nbPions, actions);
        // bord ouest
        deployerKamicazesOuest(plateau, nbPions, actions);
    }
    
    /**
     * Vérifie que les coordonnées passées en paramètres représentent bien une
     * case du plateau.
     * 
     * @param coord les coordonnées à vérifier.
     * @return true si les coordonnées sont correctes, false sinon.
     */
    static boolean estDansPlateau(Coordonnees coord) {
        return (0<=coord.ligne && coord.ligne<Coordonnees.NB_LIGNES
        && 0<=coord.colonne && coord.colonne<Coordonnees.NB_COLONNES);
    }
    
    /**
     * Repertorie dans un tableau les tours directement adjascentes à la case 
     * passée en paramètre .
     * 
     * @param plateau le plateau sur lequel on itère.
     * @param coordCentre la case autour de laquelle on cherche.
     * @return la tableau contenant les coordonnées des voisins.
     */
    static Coordonnees[] toursAdjascentes(Case[][] plateau, Coordonnees coordCentre) {
        Coordonnees[] Alentoures = new Coordonnees[8];
        int nombreVoisins = 0;
        
        for (int y=coordCentre.ligne-1; y<=coordCentre.ligne+1; y++) {
            for (int x=coordCentre.colonne-1; x<=coordCentre.colonne+1; x++) {
                
                if (estDansPlateau(new Coordonnees(y, x))
                    && plateau[y][x].tourPresente()
                    && (y != coordCentre.ligne || x != coordCentre.colonne)) {
                    
                    Alentoures[nombreVoisins] = new Coordonnees(y, x);
                    nombreVoisins++;
                }
            }
        }
        
        return Arrays.copyOf(Alentoures, nombreVoisins);
    }
    
    /**
     * Identifie les tours alliées dans le tableau de tours, 
     * passée en paramètre. 
     * 
     * @param plateau le plateau sur lequel on travaille.
     * @param voisins le tableau des coordonnées des tours voisines.
     * @param couleur la couleur de la case étudiée.
     * @return un tableau contenant les coordonnéees des tours adverses.
     */
    static Coordonnees[] toursAlliees(Case[][] plateau, 
            Coordonnees[] voisins, char couleur) {
        
        Coordonnees[] toursAdjascentesAlliees = new Coordonnees[Coordonnees.NB_COLONNES 
                                                                + Coordonnees.NB_LIGNES];
        int nbToursAlliees = 0;
        
        for (Coordonnees voisin : voisins) {
            
            if (plateau[voisin.ligne][voisin.colonne].couleur == couleur) {
                
                toursAdjascentesAlliees[nbToursAlliees] = 
                        new Coordonnees(voisin.ligne, voisin.colonne);
                nbToursAlliees++;
            }
        }
        return Arrays.copyOf(toursAdjascentesAlliees, nbToursAlliees);
    }
    
    /**
     * Identifie les tours adverses dans le tableau de tours, 
     * passée en paramètre. 
     * 
     * @param plateau le plateau sur lequel on travaille.
     * @param voisins le tableau des coordonnées des tours voisines.
     * @param couleur la couleur de la case étudiée.
     * @return un tableau contenant les coordonnéees des tours adverses.
     */
    static Coordonnees[] toursAdverses(Case[][] plateau, 
            Coordonnees[] voisins, char couleur) {
        
        Coordonnees[] toursAdjascentesAdverses = new Coordonnees[Coordonnees.NB_COLONNES 
                                                                + Coordonnees.NB_LIGNES];
        int nbToursAdverses = 0;
        
        for (Coordonnees voisin : voisins) {
            
            if (plateau[voisin.ligne][voisin.colonne].couleur != couleur) {
                
                toursAdjascentesAdverses[nbToursAdverses] = 
                        new Coordonnees(voisin.ligne, voisin.colonne);
                nbToursAdverses++;
            }
        }
        return Arrays.copyOf(toursAdjascentesAdverses, nbToursAdverses);
    }
    
    /**
     * Indique le nombre de tours adverses destructibles par l'activation dans 
     * le tableau de coordonnées passé en paramètre.
     * 
     * @param plateau dans lequel on travail.
     * @param voisinsAdverses le tableau contenant les voisins adverses.
     * @param coordCentre les coordonnées de la case qui sera activée.
     * @return le nombre de tours adverses destructibles.
     */
    static int toursAdversesDestructibles(Case[][] plateau, 
            Coordonnees[] voisinsAdverses, Coordonnees coordCentre) {
        
        int nbDestructibles = 0;
        
        for (Coordonnees voisinAdverse : voisinsAdverses) {
            
            if (plateau[voisinAdverse.ligne][voisinAdverse.colonne].hauteur
                < plateau[coordCentre.ligne][coordCentre.colonne].hauteur) {
                
                nbDestructibles += plateau[voisinAdverse.ligne][voisinAdverse.colonne].hauteur;
            }
        }
        return nbDestructibles;
    }
    
    /**
     * Repertorie dans un tableau les tours présentes sur les routes cardinales relatives à la 
     * case passée en paramètre.
     * 
     * @param plateau le plateau sur lequel on itère.
     * @param coordCentre la case autour de laquelle on cherche.
     * @return la tableau contenant les coordonnées des voisins.
     */
    static Coordonnees[] toursSurRoutesCardinales(Case[][] plateau, Coordonnees coordCentre) {
        Coordonnees[] toursSurRoutesCardinales = new Coordonnees[Coordonnees.NB_COLONNES 
                                                                + Coordonnees.NB_LIGNES];
        int nombreVoisins = 0;
        
        // Au Nord
        for (int y=coordCentre.ligne-2; 0<=y; y--) {
            if (estDansPlateau(new Coordonnees(y, coordCentre.colonne))
                && plateau[y][coordCentre.colonne].tourPresente()) {
                
                toursSurRoutesCardinales[nombreVoisins] = 
                        new Coordonnees(y, coordCentre.colonne);
                nombreVoisins++;
            }
        }
        
        // Au Sud
        for (int y=coordCentre.ligne+2; y<plateau.length; y++) {
            if (estDansPlateau(new Coordonnees(y, coordCentre.colonne))
                && plateau[y][coordCentre.colonne].tourPresente()) {
                
                toursSurRoutesCardinales[nombreVoisins] = 
                        new Coordonnees(y, coordCentre.colonne);
                nombreVoisins++;
            }
        }
        
        // A l'Ouest
        for (int x=coordCentre.colonne-2; 0<=x; x--) {
            if (estDansPlateau(new Coordonnees(coordCentre.ligne, x))
                && plateau[coordCentre.ligne][x].tourPresente()) {
                
                toursSurRoutesCardinales[nombreVoisins] = 
                        new Coordonnees(coordCentre.ligne, x);
                nombreVoisins++;
            }
        }
        
        // A l'Est
        for (int x=coordCentre.colonne+2; x<plateau[0].length; x++) {
            if (estDansPlateau(new Coordonnees(coordCentre.ligne, x))
                && plateau[coordCentre.ligne][x].tourPresente()) {
                
                toursSurRoutesCardinales[nombreVoisins] = 
                        new Coordonnees(coordCentre.ligne, x);
                nombreVoisins++;
            }
        }
        
        return Arrays.copyOf(toursSurRoutesCardinales, nombreVoisins);
    }
    
    /**
     * Permet de concaténer deux tableux de coordonnées.
     * 
     * @param tab1
     * @param tab2
     * @return le tableau, concaténation de tab1 et tab2.
     */
    static Coordonnees[] concatTableauCoordonnees(Coordonnees[] tab1, Coordonnees[] tab2) {
        Coordonnees[] concatenation = new Coordonnees[tab1.length + tab2.length];
        
        if (tab1 != null) {
            for (int i=0; i<tab1.length; i++) {
                if (tab1[i] != null) {
                    concatenation[i] = tab1[i];
                }
            }
        }
        if (tab2 != null) {
            for (int i=0; i<tab2.length; i++) {
                if (tab2[i] != null) {
                    concatenation[i+tab1.length] = tab2[i];
                }
            }
        }
            
        return concatenation;
    }
    
    /**
     * Depuis un tableau de coordonnées, trie les valeur d'après leur direction.
     * 
     * @param tab le tableau dont on veut trier les coordonnées.
     * @param coord les coordonnées du centre autour duquel ont été prises les valeurs de tab.
     * @return un tableau contenant 4 autres tableaux chacun représentatif d'une 
     *         direction cardianle et contenant les coordonnées dans cette direction
     *         par rapport à coord.
     */
    static Coordonnees[][] trierDirections(Coordonnees[] tab, Coordonnees coord) {
        
        Coordonnees[][] tabTrie = new Coordonnees[8][Coordonnees.NB_LIGNES];
        
        int auNord = 0; int auSud = 0; int alEst = 0; int alOuest = 0;
        int auNordEst = 0; int auNordOuest = 0; int auSudEst = 0; int auSudOuest = 0;
        
        for (Coordonnees c : tab) {
            
            // Au Nord ?
            if (c.ligne <= coord.ligne-1  
                && coord.colonne == c.colonne) {
                
                for (int i=0; i<tabTrie[0].length; i++) {
                    if (tabTrie[0][i] == null) {
                        tabTrie[0][i] = c;
                        break;
                    }
                }
                auNord++;
            }
            
            // Au Sud ?
            if (c.ligne >= coord.ligne+1
                && coord.colonne == c.colonne) {
                
                for (int i=0; i<tabTrie[1].length; i++) {
                    if (tabTrie[1][i] == null) {
                        tabTrie[1][i] = c;
                        break;
                    }
                }
                auSud++;
            }
            
            // A l'Est ?
            if (coord.ligne == c.ligne
                && c.colonne >= coord.colonne+1) {
                
                for (int i=0; i<tabTrie[2].length; i++) {
                    if (tabTrie[2][i] == null) {
                        tabTrie[2][i] = c;
                        break;
                    }
                }
                alEst++;
            }
            
            // A l'Ouest ?
            if (coord.ligne == c.ligne
                && c.colonne <= coord.colonne-1) {
                
                for (int i=0; i<tabTrie[3].length; i++) {
                    if (tabTrie[3][i] == null) {
                        tabTrie[3][i] = c;
                        break;
                    }
                }
                alOuest++;
            }
        
            // Au Nord-Est ?
            if (c.ligne <= coord.ligne-1
                && c.colonne >= coord.colonne+1) {
                
                for (int i=0; i<tabTrie[4].length; i++) {
                    if (tabTrie[4][i] == null) {
                        tabTrie[4][i] = c;
                        break;
                    }
                }
                auNordEst++;
            }
        
            // Au Nord-Ouest ?
            if (c.ligne <= coord.ligne-1
                && c.colonne <= coord.colonne-1) {
                
                for (int i=0; i<tabTrie[5].length; i++) {
                    if (tabTrie[5][i] == null) {
                        tabTrie[5][i] = c;
                        break;
                    }
                }
                auNordOuest++;
            }
        
            // Au Sud-Est ?
            if (c.ligne >= coord.ligne+1
                && c.colonne >= coord.colonne+1) {
                
                for (int i=0; i<tabTrie[6].length; i++) {
                    if (tabTrie[6][i] == null) {
                        tabTrie[6][i] = c;
                        break;
                    }
                }
                auSudEst++;
            }
            
            // Au Sud-Ouest ?
            if (coord.ligne+1 == c.ligne
                && coord.colonne-1 == c.colonne) {
                
                for (int i=0; i<tabTrie[7].length; i++) {
                    if (tabTrie[7][i] == null) {
                        tabTrie[7][i] = c;
                        break;
                    }
                }
                auSudOuest++;
            }
        }
        
        return new Coordonnees[][]{Arrays.copyOf(tabTrie[0], auNord),
                                   Arrays.copyOf(tabTrie[1], auSud),
                                   Arrays.copyOf(tabTrie[2], alEst),
                                   Arrays.copyOf(tabTrie[3], alOuest),
                                   Arrays.copyOf(tabTrie[4], auNordEst),
                                   Arrays.copyOf(tabTrie[5], auNordOuest),
                                   Arrays.copyOf(tabTrie[6], auSudEst),
                                   Arrays.copyOf(tabTrie[7], auSudOuest)};
    }
    
    /**
     * Permet d'identifier les tours rencontrées en premier dans chacune des
     * directions cardinales, à partir des coordonnées d'une case de référence.
     * 
     * @param voisinDirectes tableau des coordonnées des tours (adverses ou non)
     *        adjascentes.
     * @param voisinsDistants tableau des coordonnées des tours (adverses ou non)
     *        atteignables par l'activation mais non adjascentes.
     * @param coord les coordonnées de la case de référence pour la recherche des
     *        vosins.
     * @return un tableau contenant les coordonnées des premieres toursrencontrées.
     */
    static Coordonnees[] premiereTourSurRouteCardinale(Coordonnees[] voisinDirectes,
            Coordonnees[] voisinsDistants, Coordonnees coord) {
        
        Coordonnees[][] toursTriees = trierDirections(
                                                concatTableauCoordonnees(
                                                        voisinDirectes, 
                                                        voisinsDistants), 
                                                    coord);
        
        Coordonnees[] premiereTourSurRouteCardinale = new Coordonnees[Coordonnees.NB_LIGNES];
        int nbElementsNonNull = 0;
        
        for (Coordonnees[] direction : toursTriees) {
            
            if (direction.length != 0) {
                
                for (int j = 0; j<premiereTourSurRouteCardinale.length; j++) {
                    
                    if (premiereTourSurRouteCardinale[j] == null) {
                        
                        premiereTourSurRouteCardinale[j] = direction[0];
                        nbElementsNonNull++;
                        break;
                    }
                } 
            }
        }
        
        return Arrays.copyOf(premiereTourSurRouteCardinale, nbElementsNonNull);
    }
    
    /**
     * Indique le nombre eventuel de pion(s) perdu(s) après une action de fusion.
     * 
     * @param plateau dans lequel on considère les pions.
     * @param voisinsAllies les coordonnées des premiers voisins alliées sur les 
     *        routes cardinales pour la fusion.
     * @param coord les coordonnées de la case de fusion.
     * @return le nombre de pions perdus.
     */
    static int pionsFusionnesPerdus(Case[][] plateau, Coordonnees[] voisinsAllies,
            Coordonnees coord) {
        
        int hauteurSujet = plateau[coord.ligne][coord.colonne].hauteur;
        int hauteurTotale = 0;
        
        for (Coordonnees c : voisinsAllies) {
            hauteurTotale += plateau[c.ligne][c.colonne].hauteur;
        }
        while (hauteurSujet < 4 && hauteurTotale > 0) {
            hauteurSujet++;
            hauteurTotale--;
        }
        
        return hauteurTotale;
    }
    
    /**
     * Ne retourne qu'un tableau de coordonnées correspondant à une direction 
     * indiquée, au lieu d'un tableau de tableau des coordonnées dans toutes
     * les directions.
     * 
     * @param direction à isoler.
     * @param coord la coordonnée à partir de laquelle on considère les directions.
     * @param tab le tableau de coordonnées à trier, afin d'obtenir les coordonnées
     *        de la direction voulue.
     * @return le tableau des coordonnées dans la direction voulue par rapport à coord.
     */
    static Coordonnees[] isolerDirection(String direction, Coordonnees coord,
            Coordonnees[] tab) {
        
        Coordonnees[][] tabTrie = trierDirections(tab, coord);
        
        switch (direction) {
            case "Nord":
                return tabTrie[0];
            case "Sud":
                return tabTrie[1];
            case "Est":
                return tabTrie[2];
            case "Ouest":
                return tabTrie[3];
            case "Nord Est":
                return tabTrie[4];
            case "Nord Ouest":
                return tabTrie[5];
            case "Sud Est":
                return tabTrie[6];
            case "Sud Ouest":
                return tabTrie[7];
            default:
                System.err.println("Libellé de direction incorrecte.");
                return null;
        }
    }
    
    /**
     * Permet d'ajouter l'action des kamikazes sur le bord nord.
     * 
     * @param plateau sur lequel on considère le bord nord.
     * @param pionsBlancs le nombre de pionsBlancs avant action.
     * @param pionsNoirs le nombre de pions noirs après action.
     * @param actions le tableau de stockage des actions possibles.
     */
    static void deployerKamicazesNord(Case[][] plateau, NbPions  nbPions, 
            ActionsPossibles actions) {
        
        int pionsNoirs = nbPions.nbPionsNoirs;
        int pionsBlancs = nbPions.nbPionsBlancs;
        
        // On parcours le bord nord du plateau de la première à la dernière case
        for (int nord=0; nord<Coordonnees.NB_COLONNES; nord++) {
            // s'il n'y a pas de tour sur la case en cours de ce bord
            if (!(plateau[0][nord].tourPresente())) {
                // alors on cherche la première tour rencontrée vers le bas seulement
                Coordonnees tmpCentre = new Coordonnees(0, nord);
                Coordonnees[] premiereAuSud = isolerDirection(
                                        "Sud", 
                                        tmpCentre,
                                        premiereTourSurRouteCardinale(
                                            toursAdjascentes(
                                                    plateau, 
                                                    tmpCentre), 
                                            toursSurRoutesCardinales(
                                                    plateau, 
                                                    tmpCentre), 
                                            tmpCentre));
                // s'il y a bien au moins une tour dans cette direction
                if (premiereAuSud.length > 0) {
                    // on défausse les pions de celle ci, en fonction de sa couleur
                    if (plateau[premiereAuSud[0].ligne][premiereAuSud[0].colonne].couleur
                        == Case.CAR_BLANC) {
                        pionsBlancs -= plateau[premiereAuSud[0].ligne]
                                              [premiereAuSud[0].colonne].hauteur;
                    } else {
                        pionsNoirs -= plateau[premiereAuSud[0].ligne]
                                              [premiereAuSud[0].colonne].hauteur;
                    }
                } 
                // sinon on ne fait rien
            // s'il y a une tour sur la case en cours de ce bord
            // si cette tour est blanche
            } else if (plateau[0][nord].couleur == Case.CAR_BLANC) {
                // le kamikaze l'explose et ces pions blancs sont alors perdus
                pionsBlancs -= plateau[0][nord].hauteur;
            
            // de même pour les pions noirs si la tour est noir
            } else {
                pionsNoirs -= plateau[0][nord].hauteur;
            }
            
        }
        // une fois le compte des pions défaussés fait, on ajoute l'action
        String action = "CN" + "," 
                + (pionsNoirs) + ","
                + (pionsBlancs);
        actions.ajouterAction(action);
    }
    
    /**
     * Permet d'ajouter l'action des kamikazes sur le bord sud.
     * 
     * @param plateau sur lequel on considère le bord sud.
     * @param pionsBlancs le nombre de pionsBlancs avant action.
     * @param pionsNoirs le nombre de pions noirs après action.
     * @param actions le tableau de stockage des actions possibles.
     */
    static void deployerKamicazesSud(Case[][] plateau, NbPions  nbPions, 
            ActionsPossibles actions) {
        
        int pionsNoirs = nbPions.nbPionsNoirs;
        int pionsBlancs = nbPions.nbPionsBlancs;
        
        for (int sud=0; sud<Coordonnees.NB_COLONNES; sud++) {
            
            if (!(plateau[Coordonnees.NB_LIGNES-1][sud].tourPresente())) {
                
                Coordonnees tmpCentre = new Coordonnees(Coordonnees.NB_LIGNES-1, 
                                        sud);
                Coordonnees[] premiereAuNord = isolerDirection(
                                        "Nord", 
                                        tmpCentre,
                                        premiereTourSurRouteCardinale(
                                            toursAdjascentes(
                                                    plateau, 
                                                    tmpCentre), 
                                            toursSurRoutesCardinales(
                                                    plateau, 
                                                    tmpCentre), 
                                            tmpCentre));
                
                if (premiereAuNord.length > 0) {
                    if (plateau[premiereAuNord[0].ligne][premiereAuNord[0].colonne].couleur
                        == Case.CAR_BLANC) {
                        pionsBlancs -= plateau[premiereAuNord[0].ligne]
                                              [premiereAuNord[0].colonne].hauteur;
                    } else {
                        pionsNoirs -= plateau[premiereAuNord[0].ligne]
                                              [premiereAuNord[0].colonne].hauteur;
                    }
                }
            
            } else if (plateau[Coordonnees.NB_LIGNES-1][sud].couleur == Case.CAR_BLANC) {
                pionsBlancs -= plateau[Coordonnees.NB_LIGNES-1][sud].hauteur;
                
            } else {
                pionsNoirs -= plateau[Coordonnees.NB_LIGNES-1][sud].hauteur;
            }
        }
        String action = "CS" + "," 
                + (pionsNoirs) + ","
                + (pionsBlancs);
        actions.ajouterAction(action);
    }
    
    /**
     * Permet d'ajouter l'action des kamikazes sur le bord est.
     * 
     * @param plateau sur lequel on considère le bord est.
     * @param pionsBlancs le nombre de pionsBlancs avant action.
     * @param pionsNoirs le nombre de pions noirs après action.
     * @param actions le tableau de stockage des actions possibles.
     */
    static void deployerKamicazesEst(Case[][] plateau, NbPions  nbPions, 
            ActionsPossibles actions) {
        
        int pionsNoirs = nbPions.nbPionsNoirs;
        int pionsBlancs = nbPions.nbPionsBlancs;
        
        for (int est=0; est<Coordonnees.NB_LIGNES; est++) {
            
            if (!(plateau[est][Coordonnees.NB_COLONNES-1].tourPresente())) {
                
                Coordonnees tmpCentre = new Coordonnees(est, 
                                                        Coordonnees.NB_COLONNES-1);
                Coordonnees[] premiereAlOuest = isolerDirection(
                                        "Ouest", 
                                        tmpCentre,
                                        premiereTourSurRouteCardinale(
                                            toursAdjascentes(
                                                    plateau, 
                                                    tmpCentre), 
                                            toursSurRoutesCardinales(
                                                    plateau, 
                                                    tmpCentre), 
                                            tmpCentre));
                
                if (premiereAlOuest.length > 0) {
                    if (plateau[premiereAlOuest[0].ligne][premiereAlOuest[0].colonne].couleur
                        == Case.CAR_BLANC) {
                        pionsBlancs -= plateau[premiereAlOuest[0].ligne]
                                              [premiereAlOuest[0].colonne].hauteur;
                    } else {
                        pionsNoirs -= plateau[premiereAlOuest[0].ligne]
                                              [premiereAlOuest[0].colonne].hauteur;
                    }
                }
            
            } else if (plateau[est][Coordonnees.NB_COLONNES-1].couleur == Case.CAR_BLANC) {
                pionsBlancs -= plateau[est][Coordonnees.NB_COLONNES-1].hauteur;
                
            } else {
                pionsNoirs -= plateau[est][Coordonnees.NB_COLONNES-1].hauteur;
            }
        }
        String action = "CE" + "," 
                + (pionsNoirs) + ","
                + (pionsBlancs);
        actions.ajouterAction(action);
    }
    
    /**
     * Permet d'ajouter l'action des kamikazes sur le bord ouest.
     * 
     * @param plateau sur lequel on considère le bord ouest.
     * @param pionsBlancs le nombre de pionsBlancs avant action.
     * @param pionsNoirs le nombre de pions noirs après action.
     * @param actions le tableau de stockage des actions possibles.
     */
    static void deployerKamicazesOuest(Case[][] plateau, NbPions  nbPions, 
            ActionsPossibles actions) {
        
        int pionsNoirs = nbPions.nbPionsNoirs;
        int pionsBlancs = nbPions.nbPionsBlancs;
        
        for (int ouest=0; ouest<Coordonnees.NB_LIGNES; ouest++) {
            
            if (!(plateau[ouest][0].tourPresente())) {
                
                Coordonnees tmpCentre = new Coordonnees(ouest, 0);
                Coordonnees[] premiereAlEst = isolerDirection(
                                        "Est", 
                                        tmpCentre,
                                        premiereTourSurRouteCardinale(
                                            toursAdjascentes(
                                                    plateau, 
                                                    tmpCentre), 
                                            toursSurRoutesCardinales(
                                                    plateau, 
                                                    tmpCentre), 
                                            tmpCentre));
                
                if (premiereAlEst.length > 0) {
                    if (plateau[premiereAlEst[0].ligne][premiereAlEst[0].colonne].couleur
                        == Case.CAR_BLANC) {
                        pionsBlancs -= plateau[premiereAlEst[0].ligne]
                                              [premiereAlEst[0].colonne].hauteur;
                    } else {
                        pionsNoirs -= plateau[premiereAlEst[0].ligne]
                                              [premiereAlEst[0].colonne].hauteur;
                    }
                }
            
            } else if (plateau[ouest][0].couleur == Case.CAR_BLANC) {
                pionsBlancs -= plateau[ouest][0].hauteur;
                
            } else {
                pionsNoirs -= plateau[ouest][0].hauteur;
            }
        }
        String action = "CO" + "," 
                + (pionsNoirs) + ","
                + (pionsBlancs);
        actions.ajouterAction(action);
    }
}
