package gestion;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Calcule et affiche des statistiques sur un catalogue.
 * Ne modifie jamais le catalogue.
 */
public class RapportCatalogue {

    private final CatalogueEtudiants catalogue;

    public RapportCatalogue(CatalogueEtudiants catalogue) {
        Objects.requireNonNull(catalogue, "Le catalogue ne peut pas etre null.");
        this.catalogue = catalogue;
    }

    // ── Statistiques ──────────────────────────────────────────────────────────

    /**
     * Retourne le nombre d'étudiants par filière.
     * @return Map filière -> nombre
     */
    public Map<String, Long> nbParFiliere() {
        Map<String, Long> compteur = new LinkedHashMap<>();
        for (Etudiant e : catalogue) {
            compteur.merge(e.getFiliere(), 1L, Long::sum);
        }
        return compteur;
    }

    /**
     * Retourne la moyenne d'âge des étudiants.
     * @return 0.0 si le catalogue est vide
     */
    public double moyenneAge() {
        if (catalogue.size() == 0) return 0.0;
        double somme = 0;
        int count = 0;
        for (Etudiant e : catalogue) {
            somme += e.getAge();
            count++;
        }
        return somme / count;
    }

    /**
     * Retourne la liste des étudiants boursiers.
     */
    public List<Etudiant> boursiers() {
        List<Etudiant> liste = new ArrayList<>();
        for (Etudiant e : catalogue) {
            if (e.isBoursier()) liste.add(e);
        }
        return liste;
    }

    /**
     * Retourne les n filières les plus peuplées, par ordre décroissant.
     * @param n nombre de filières à retourner
     */
    public List<Map.Entry<String, Long>> topFilieres(int n) {
        return nbParFiliere().entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    // ── Affichage ─────────────────────────────────────────────────────────────

    /**
     * Affiche le rapport complet dans le terminal selon le format demandé.
     */
    public void afficher() {
        String sep  = "==========================================";
        String line = "------------------------------------------";

        System.out.println(sep);
        System.out.println("RAPPORT DU CATALOGUE");
        System.out.println(sep);
        System.out.printf("Total etudiants  : %d%n", catalogue.size());
        System.out.printf("Moyenne d'age    : %.1f ans%n", moyenneAge());
        System.out.printf("Boursiers        : %d etudiant(s)%n", boursiers().size());
        System.out.println(line);
        System.out.println("Repartition par filiere :");
        for (Map.Entry<String, Long> entry : nbParFiliere().entrySet()) {
            System.out.printf("  %-20s : %d etudiant(s)%n",
                    entry.getKey(), entry.getValue());
        }
        System.out.println(sep);
    }
}
