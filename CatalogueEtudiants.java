package gestion;

import java.util.*;

/**
 * Collection intelligente d'étudiants.
 * Offre les opérations CRUD, la recherche par mot-clé,
 * et implémente Iterable pour le parcours en boucle.
 */
public class CatalogueEtudiants implements Iterable<Etudiant> {

    /** Structure interne : LinkedHashMap pour conserver l'ordre d'insertion */
    private final Map<String, Etudiant> etudiants = new LinkedHashMap<>();

    // ── Ajouter ───────────────────────────────────────────────────────────────

    /**
     * Ajoute un étudiant dans le catalogue.
     * @throws IllegalArgumentException si le matricule existe déjà
     */
    public void ajouter(Etudiant etudiant) {
        Objects.requireNonNull(etudiant, "L'etudiant ne peut pas etre null.");
        if (etudiants.containsKey(etudiant.getMatricule())) {
            throw new IllegalArgumentException(
                "Un etudiant avec le matricule " + etudiant.getMatricule() + " existe deja."
            );
        }
        etudiants.put(etudiant.getMatricule(), etudiant);
    }

    // ── Rechercher ────────────────────────────────────────────────────────────

    /**
     * Retourne la liste des étudiants dont le nom, prénom, matricule ou filière
     * contient le terme (insensible à la casse).
     * Si le terme est vide ou null, retourne tous les étudiants.
     */
    public List<Etudiant> rechercher(String terme) {
        if (terme == null || terme.trim().isEmpty()) {
            return new ArrayList<>(etudiants.values());
        }
        String t = terme.trim().toLowerCase();
        List<Etudiant> resultats = new ArrayList<>();
        for (Etudiant e : etudiants.values()) {
            if (e.getMatricule().toLowerCase().contains(t)
             || e.getNom().toLowerCase().contains(t)
             || e.getPrenom().toLowerCase().contains(t)
             || e.getFiliere().toLowerCase().contains(t)) {
                resultats.add(e);
            }
        }
        return resultats;
    }

    // ── Modifier ──────────────────────────────────────────────────────────────

    /**
     * Met à jour un ou plusieurs champs d'un étudiant identifié par son matricule.
     * La map {@code modifications} peut contenir les clés :
     * nom, prenom, age, email, filiere, boursier, interne.
     *
     * @throws NoSuchElementException   si le matricule est introuvable
     * @throws IllegalArgumentException si une valeur est invalide
     */
    public void modifier(String matricule, Map<String, Object> modifications) {
        Etudiant e = getOuLever(matricule);
        for (Map.Entry<String, Object> entry : modifications.entrySet()) {
            switch (entry.getKey().toLowerCase()) {
                case "nom"      -> e.setNom((String) entry.getValue());
                case "prenom"   -> e.setPrenom((String) entry.getValue());
                case "age"      -> e.setAge((int) entry.getValue());
                case "email"    -> e.setEmail((String) entry.getValue());
                case "filiere"  -> e.setFiliere((String) entry.getValue());
                case "boursier" -> e.setBoursier((boolean) entry.getValue());
                case "interne"  -> e.setInterne((boolean) entry.getValue());
                default -> throw new IllegalArgumentException(
                    "Champ inconnu : " + entry.getKey()
                );
            }
        }
    }

    // ── Supprimer ─────────────────────────────────────────────────────────────

    /**
     * Supprime l'étudiant identifié par le matricule et le retourne.
     * @throws NoSuchElementException si le matricule est introuvable
     */
    public Etudiant supprimer(String matricule) {
        Etudiant e = getOuLever(matricule);
        etudiants.remove(matricule);
        return e;
    }

    // ── Taille ────────────────────────────────────────────────────────────────

    /** Retourne le nombre d'étudiants dans le catalogue. */
    public int size() {
        return etudiants.size();
    }

    // ── Appartenance ──────────────────────────────────────────────────────────

    /** Retourne true si le matricule est présent dans le catalogue. */
    public boolean contains(String matricule) {
        return etudiants.containsKey(matricule);
    }

    // ── Accès direct ─────────────────────────────────────────────────────────

    /**
     * Retourne l'étudiant correspondant au matricule.
     * @throws NoSuchElementException si introuvable
     */
    public Etudiant get(String matricule) {
        return getOuLever(matricule);
    }

    // ── Iterable ─────────────────────────────────────────────────────────────

    /**
     * Permet le parcours avec une boucle for-each.
     */
    @Override
    public Iterator<Etudiant> iterator() {
        return Collections.unmodifiableCollection(etudiants.values()).iterator();
    }

    // ── Utilitaire privé ──────────────────────────────────────────────────────

    private Etudiant getOuLever(String matricule) {
        Etudiant e = etudiants.get(matricule);
        if (e == null) {
            throw new NoSuchElementException(
                "Aucun etudiant trouve avec le matricule : " + matricule
            );
        }
        return e;
    }
}
