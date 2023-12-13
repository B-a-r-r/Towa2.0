package towa;

/**
 * Nombre de pions, pour chaque joueur.
 */
class NbPions {
    
    /**
     * Nombre de pionse pour le joueur noir.
     */
    int nbPionsNoirs;
    
    /**
     * Nombre de pions pour le joueur blanc.
     */
    int nbPionsBlancs;
    
    /**
     * Constructeur vide.
     */
    NbPions() {
        nbPionsNoirs = 0;
        nbPionsBlancs = 0;
    }
    
    /**
     * Constructeur à partir de valeurs connues.
     * 
     * @param pionsNoir nombre de pions du joueur noir
     * @param pionsBlancs nombre de pions du joueur blanc
     */
    NbPions(int pionsNoir, int pionsBlancs) {
        nbPionsNoirs = pionsNoir;
        nbPionsBlancs = pionsBlancs;
    }

    /**
     * Constructeur par copie : permet d'obtenir une copie de cet objet.
     * 
     * @param nbPions l'objet à copier
     */
    NbPions(NbPions nbPions) {
        this.nbPionsNoirs = nbPions.nbPionsNoirs;
        this.nbPionsBlancs = nbPions.nbPionsBlancs;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.nbPionsNoirs;
        hash = 89 * hash + this.nbPionsBlancs;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NbPions other = (NbPions) obj;
        if (this.nbPionsNoirs != other.nbPionsNoirs) {
            return false;
        }
        if (this.nbPionsBlancs != other.nbPionsBlancs) {
            return false;
        }
        return true;
    }
}
