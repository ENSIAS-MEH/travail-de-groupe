package gestion;

import java.util.Set;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Représente un étudiant avec ses informations personnelles et académiques.
 * Chaque champ est soumis à une validation stricte.
 *
 * @author Système de gestion d'étudiants
 * @version 1.0
 */
public class Etudiant {

    // ── Constantes de validation ──────────────────────────────────────────────

    /** Pattern : 2-4 lettres majuscules, tiret, 4 chiffres, tiret, 3 chiffres */
    private static final Pattern PATTERN_MATRICULE =
            Pattern.compile("^[A-Z]{2,4}-\\d{4}-\\d{3}$");

    /** Pattern email : doit contenir '@' et un point après le '@' */
    private static final Pattern PATTERN_EMAIL =
            Pattern.compile("^[^@]+@[^@]+\\.[^@]+$");

    /** Filières autorisées */
    private static final Set<String> FILIERES_VALIDES = Set.of(
            "Informatique", "Reseaux", "Cybersecurite", "IA et Data"
    );

    // ── Attributs privés ─────────────────────────────────────────────────────

    private String matricule;
    private String nom;
    private String prenom;
    private int    age;
    private String email;
    private String filiere;
    private boolean boursier;
    private boolean interne;

    // ── Constructeur ─────────────────────────────────────────────────────────

    /**
     * Crée un étudiant avec tous ses attributs.
     * Chaque valeur est validée immédiatement.
     */
    public Etudiant(String matricule, String nom, String prenom, int age,
                    String email, String filiere, boolean boursier, boolean interne) {
        setMatricule(matricule);
        setNom(nom);
        setPrenom(prenom);
        setAge(age);
        setEmail(email);
        setFiliere(filiere);
        this.boursier = boursier;
        this.interne  = interne;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String  getMatricule() { return matricule; }
    public String  getNom()       { return nom;       }
    public String  getPrenom()    { return prenom;    }
    public int     getAge()       { return age;       }
    public String  getEmail()     { return email;     }
    public String  getFiliere()   { return filiere;   }
    public boolean isBoursier()   { return boursier;  }
    public boolean isInterne()    { return interne;   }

    // ── Setters avec validation ───────────────────────────────────────────────

    /**
     * Modifie le matricule après validation du format.
     * @throws IllegalArgumentException si le format est invalide
     */
    public void setMatricule(String matricule) {
        if (matricule == null || !PATTERN_MATRICULE.matcher(matricule.trim()).matches()) {
            throw new IllegalArgumentException(
                "[matricule] : Format invalide. Attendu : 2-4 lettres majuscules, tiret, " +
                "4 chiffres (annee), tiret, 3 chiffres. Ex : GI2-2024-001"
            );
        }
        this.matricule = matricule.trim();
    }

    /**
     * Modifie le nom après validation (au moins 2 caractères hors espaces).
     */
    public void setNom(String nom) {
        if (nom == null || nom.trim().length() < 2) {
            throw new IllegalArgumentException(
                "[nom] : Doit contenir au moins 2 caracteres (espaces ignores)."
            );
        }
        this.nom = nom.trim();
    }

    /**
     * Modifie le prénom après validation (au moins 2 caractères hors espaces).
     */
    public void setPrenom(String prenom) {
        if (prenom == null || prenom.trim().length() < 2) {
            throw new IllegalArgumentException(
                "[prenom] : Doit contenir au moins 2 caracteres (espaces ignores)."
            );
        }
        this.prenom = prenom.trim();
    }

    /**
     * Modifie l'âge après validation (entre 16 et 40 inclus).
     */
    public void setAge(int age) {
        if (age < 16 || age > 40) {
            throw new IllegalArgumentException(
                "[age] : Doit etre entre 16 et 40."
            );
        }
        this.age = age;
    }

    /**
     * Modifie l'email après validation (doit contenir '@' et un point après).
     */
    public void setEmail(String email) {
        if (email == null || !PATTERN_EMAIL.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException(
                "[email] : Doit contenir @ et un point apres le @."
            );
        }
        this.email = email.trim();
    }

    /**
     * Modifie la filière après validation parmi les valeurs autorisées.
     */
    public void setFiliere(String filiere) {
        if (filiere == null || !FILIERES_VALIDES.contains(filiere.trim())) {
            throw new IllegalArgumentException(
                "[filiere] : Doit etre parmi Informatique, Reseaux, Cybersecurite, IA et Data."
            );
        }
        this.filiere = filiere.trim();
    }

    public void setBoursier(boolean boursier) { this.boursier = boursier; }
    public void setInterne(boolean interne)   { this.interne  = interne;  }

    // ── Méthodes Object ───────────────────────────────────────────────────────

    /**
     * Format exact : "GI2-2024-001 | Amira Benali | Informatique"
     */
    @Override
    public String toString() {
        return String.format("%s | %s %s | %s", matricule, prenom, nom, filiere);
    }

    /**
     * Deux étudiants sont égaux si et seulement si leurs matricules sont identiques.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Etudiant)) return false;
        Etudiant autre = (Etudiant) o;
        return Objects.equals(this.matricule, autre.matricule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matricule);
    }

    // ── Utilitaire statique ───────────────────────────────────────────────────

    /**
     * Retourne les filières valides sous forme lisible.
     */
    public static String getFilieresValides() {
        return String.join(", ", FILIERES_VALIDES);
    }
}
