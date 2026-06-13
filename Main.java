package gestion;

import java.util.*;

/**
 * Programme principal : menu textuel interactif.
 * Ne plante jamais : toute erreur est capturée et affichée proprement.
 */
public class Main {

    private static final CatalogueEtudiants catalogue = new CatalogueEtudiants();
    private static final Scanner scanner = new Scanner(System.in);

    // ── Point d'entrée ────────────────────────────────────────────────────────

    public static void main(String[] args) {
        // Données de démonstration
        chargerDonneesDemoInstration();

        boolean continuer = true;
        while (continuer) {
            afficherMenu();
            String choix = scanner.nextLine().trim();
            switch (choix) {
                case "1" -> afficherTous();
                case "2" -> rechercherEtudiant();
                case "3" -> ajouterEtudiant();
                case "4" -> modifierEtudiant();
                case "5" -> supprimerEtudiant();
                case "6" -> new RapportCatalogue(catalogue).afficher();
                case "0" -> {
                    System.out.println("Au revoir !");
                    continuer = false;
                }
                default  -> System.out.println("Choix invalide. Veuillez entrer un numero entre 0 et 6.");
            }
        }
        scanner.close();
    }

    // ── Menu ─────────────────────────────────────────────────────────────────

    private static void afficherMenu() {
        System.out.println();
        System.out.println("==========================================");
        System.out.println("         GESTION DES ETUDIANTS           ");
        System.out.println("==========================================");
        System.out.println("  1. Afficher tous les etudiants");
        System.out.println("  2. Rechercher un etudiant");
        System.out.println("  3. Ajouter un etudiant");
        System.out.println("  4. Modifier un etudiant");
        System.out.println("  5. Supprimer un etudiant");
        System.out.println("  6. Afficher le rapport");
        System.out.println("  0. Quitter");
        System.out.println("------------------------------------------");
        System.out.print("Votre choix : ");
    }

    // ── Option 1 — Afficher tous ─────────────────────────────────────────────

    private static void afficherTous() {
        System.out.println();
        if (catalogue.size() == 0) {
            System.out.println("Le catalogue est vide.");
            return;
        }
        System.out.println("-- Liste des etudiants (" + catalogue.size() + ") --");
        int i = 1;
        for (Etudiant e : catalogue) {
            System.out.printf("  %2d. %s%n", i++, e);
        }
    }

    // ── Option 2 — Rechercher ────────────────────────────────────────────────

    private static void rechercherEtudiant() {
        System.out.println();
        System.out.println("-- Rechercher un etudiant --");
        System.out.print("Terme de recherche (vide = tous) : ");
        String terme = scanner.nextLine().trim();

        List<Etudiant> resultats = catalogue.rechercher(terme);
        if (resultats.isEmpty()) {
            System.out.println("Aucun etudiant trouve pour \"" + terme + "\".");
        } else {
            System.out.println(resultats.size() + " etudiant(s) trouve(s) :");
            int i = 1;
            for (Etudiant e : resultats) {
                System.out.printf("  %2d. %s%n", i++, e);
            }
        }
    }

    // ── Option 3 — Ajouter ───────────────────────────────────────────────────

    private static void ajouterEtudiant() {
        System.out.println();
        System.out.println("-- Ajouter un etudiant --");
        try {
            String matricule = lireChamp("Matricule", "matricule");
            String nom       = lireChamp("Nom", "nom");
            String prenom    = lireChamp("Prenom", "prenom");
            int    age       = lireAge();
            String email     = lireChamp("Email", "email");
            String filiere   = lireFiliere();
            boolean boursier = lireOuiNon("Boursier (o/n)");
            boolean interne  = lireOuiNon("Interne  (o/n)");

            Etudiant e = new Etudiant(matricule, nom, prenom, age,
                                      email, filiere, boursier, interne);
            catalogue.ajouter(e);
            System.out.println("Etudiant ajoute : " + e);

        } catch (Exception ex) {
            System.out.println("Erreur : " + ex.getMessage());
        }
    }

    /**
     * Lit un champ textuel en validant via le setter correspondant.
     * Redemande uniquement ce champ en cas d'erreur.
     */
    private static String lireChamp(String label, String champ) {
        while (true) {
            System.out.print(label + " : ");
            String valeur = scanner.nextLine().trim();
            try {
                // Validation via un étudiant fantôme pour déclencher les setters
                validerChamp(champ, valeur);
                return valeur;
            } catch (IllegalArgumentException ex) {
                System.out.println("Erreur " + ex.getMessage());
            }
        }
    }

    /** Valide une valeur textuelle via le setter approprié sans créer d'objet réel. */
    private static void validerChamp(String champ, String valeur) {
        // On crée un étudiant valide minimal et on lui applique le setter
        // pour déclencher la validation sans aucun effet de bord.
        switch (champ) {
            case "matricule" -> {
                // Valider via le pattern directement
                Etudiant tmp = new Etudiant("GI2-2024-000","Tmp","Tmp",20,
                        "tmp@mail.ma","Informatique",false,false);
                tmp.setMatricule(valeur);
            }
            case "nom" -> {
                Etudiant tmp = new Etudiant("GI2-2024-000","Tmp","Tmp",20,
                        "tmp@mail.ma","Informatique",false,false);
                tmp.setNom(valeur);
            }
            case "prenom" -> {
                Etudiant tmp = new Etudiant("GI2-2024-000","Tmp","Tmp",20,
                        "tmp@mail.ma","Informatique",false,false);
                tmp.setPrenom(valeur);
            }
            case "email" -> {
                Etudiant tmp = new Etudiant("GI2-2024-000","Tmp","Tmp",20,
                        "tmp@mail.ma","Informatique",false,false);
                tmp.setEmail(valeur);
            }
        }
    }

    private static int lireAge() {
        while (true) {
            System.out.print("Age : ");
            String ligne = scanner.nextLine().trim();
            try {
                int age = Integer.parseInt(ligne);
                // Déclenche la validation
                Etudiant tmp = new Etudiant("GI2-2024-000","Tmp","Tmp",20,
                        "tmp@mail.ma","Informatique",false,false);
                tmp.setAge(age);
                return age;
            } catch (NumberFormatException ex) {
                System.out.println("Erreur [age] : Veuillez entrer un nombre entier.");
            } catch (IllegalArgumentException ex) {
                System.out.println("Erreur " + ex.getMessage());
            }
        }
    }

    private static String lireFiliere() {
        while (true) {
            System.out.print("Filiere : ");
            String valeur = scanner.nextLine().trim();
            try {
                Etudiant tmp = new Etudiant("GI2-2024-000","Tmp","Tmp",20,
                        "tmp@mail.ma","Informatique",false,false);
                tmp.setFiliere(valeur);
                return valeur;
            } catch (IllegalArgumentException ex) {
                System.out.println("Erreur " + ex.getMessage());
            }
        }
    }

    private static boolean lireOuiNon(String label) {
        while (true) {
            System.out.print(label + " : ");
            String rep = scanner.nextLine().trim().toLowerCase();
            if (rep.equals("o") || rep.equals("oui")) return true;
            if (rep.equals("n") || rep.equals("non")) return false;
            System.out.println("Entrez 'o' pour oui ou 'n' pour non.");
        }
    }

    // ── Option 4 — Modifier ───────────────────────────────────────────────────

    private static void modifierEtudiant() {
        System.out.println();
        System.out.println("-- Modifier un etudiant --");
        System.out.print("Matricule de l'etudiant a modifier : ");
        String matricule = scanner.nextLine().trim();

        if (!catalogue.contains(matricule)) {
            System.out.println("Erreur : Aucun etudiant trouve avec le matricule " + matricule + ".");
            return;
        }

        Etudiant e = catalogue.get(matricule);
        System.out.println("Etudiant trouve : " + e);
        System.out.println("Laissez vide pour conserver la valeur actuelle.");

        try {
            Map<String, Object> modifs = new LinkedHashMap<>();

            System.out.print("Nouveau nom [" + e.getNom() + "] : ");
            String v = scanner.nextLine().trim();
            if (!v.isEmpty()) modifs.put("nom", v);

            System.out.print("Nouveau prenom [" + e.getPrenom() + "] : ");
            v = scanner.nextLine().trim();
            if (!v.isEmpty()) modifs.put("prenom", v);

            System.out.print("Nouvel age [" + e.getAge() + "] : ");
            v = scanner.nextLine().trim();
            if (!v.isEmpty()) {
                try {
                    modifs.put("age", Integer.parseInt(v));
                } catch (NumberFormatException ex) {
                    System.out.println("Age ignore (valeur non numerique).");
                }
            }

            System.out.print("Nouvel email [" + e.getEmail() + "] : ");
            v = scanner.nextLine().trim();
            if (!v.isEmpty()) modifs.put("email", v);

            System.out.print("Nouvelle filiere [" + e.getFiliere() + "] : ");
            v = scanner.nextLine().trim();
            if (!v.isEmpty()) modifs.put("filiere", v);

            System.out.print("Boursier [" + (e.isBoursier() ? "o" : "n") + "] (o/n, vide=inchange) : ");
            v = scanner.nextLine().trim().toLowerCase();
            if (v.equals("o")) modifs.put("boursier", true);
            else if (v.equals("n")) modifs.put("boursier", false);

            System.out.print("Interne [" + (e.isInterne() ? "o" : "n") + "] (o/n, vide=inchange) : ");
            v = scanner.nextLine().trim().toLowerCase();
            if (v.equals("o")) modifs.put("interne", true);
            else if (v.equals("n")) modifs.put("interne", false);

            if (modifs.isEmpty()) {
                System.out.println("Aucune modification effectuee.");
            } else {
                catalogue.modifier(matricule, modifs);
                System.out.println("Etudiant mis a jour : " + catalogue.get(matricule));
            }

        } catch (IllegalArgumentException ex) {
            System.out.println("Erreur " + ex.getMessage());
        }
    }

    // ── Option 5 — Supprimer ─────────────────────────────────────────────────

    private static void supprimerEtudiant() {
        System.out.println();
        System.out.println("-- Supprimer un etudiant --");
        System.out.print("Matricule de l'etudiant a supprimer : ");
        String matricule = scanner.nextLine().trim();
        try {
            Etudiant supprime = catalogue.supprimer(matricule);
            System.out.println("Etudiant supprime : " + supprime);
        } catch (NoSuchElementException ex) {
            System.out.println("Erreur : " + ex.getMessage());
        }
    }

    // ── Données de démonstration ─────────────────────────────────────────────

    private static void chargerDonneesDemoInstration() {
        try {
            catalogue.ajouter(new Etudiant("GI2-2024-001", "Benali",  "Amira",  21,
                    "amira.benali@mail.ma",  "Informatique",    true,  false));
            catalogue.ajouter(new Etudiant("RS2-2024-042", "Khalil",  "Youssef",22,
                    "youssef.khalil@mail.ma","Reseaux",          false, true ));
            catalogue.ajouter(new Etudiant("CYBER-2025-001","Mansouri","Sara",  20,
                    "sara.mansouri@mail.ma", "Cybersecurite",    true,  true ));
            catalogue.ajouter(new Etudiant("GI2-2024-002", "El Idrissi","Karim",23,
                    "karim.elidrissi@mail.ma","Informatique",    false, false));
            catalogue.ajouter(new Etudiant("IA2-2024-010", "Bouchta", "Fatima", 19,
                    "fatima.bouchta@mail.ma","IA et Data",       false, true ));
        } catch (Exception ex) {
            System.err.println("Erreur chargement demo : " + ex.getMessage());
        }
    }
}
