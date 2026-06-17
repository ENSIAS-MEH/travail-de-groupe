"""
=============================================================
  SYSTÈME AUTOMATISÉ DE SURVEILLANCE DES DISPOSITIFS MÉDICAUX
  Réalisé par : Alaa Tardaoui & Nada Seddar
  Encadré par : Pr. Mahmoud El Hamlaoui — 2025-2026
=============================================================
  VERSION FICHIER UNIQUE — Compatible Spyder / tout IDE
  Aucune installation requise. Python 3.7+ suffisant.
=============================================================
"""

# ── Standard library only — nothing to install ────────────────
from abc import ABC, abstractmethod
from datetime import datetime
import json
import csv
import os
import sys


# ╔══════════════════════════════════════════════════════════════╗
# ║                    CLASSE DonneeMedicale                     ║
# ╚══════════════════════════════════════════════════════════════╝

class DonneeMedicale:
    """Stores a single reading from a biomedical sensor."""

    def __init__(self, capteur_id, valeur, unite):
        self.capteur_id = capteur_id        # ID of the sensor
        self.valeur     = valeur            # Measured value
        self.unite      = unite             # Unit (e.g. "bpm", "%")
        self.horodatage = datetime.now()    # Timestamp

    def to_dict(self):
        return {
            "capteur_id" : self.capteur_id,
            "valeur"     : self.valeur,
            "unite"      : self.unite,
            "horodatage" : self.horodatage.strftime('%Y-%m-%d %H:%M:%S')
        }

    def __str__(self):
        return (f"[Donnee] Capteur#{self.capteur_id} | "
                f"{self.valeur} {self.unite} | "
                f"{self.horodatage.strftime('%Y-%m-%d %H:%M:%S')}")


# ╔══════════════════════════════════════════════════════════════╗
# ║               CLASSE ABSTRAITE Capteur                       ║
# ╚══════════════════════════════════════════════════════════════╝

class Capteur(ABC):
    """Abstract base class for all biomedical sensors."""

    def __init__(self, id, type_capteur, unite):
        self.id    = id             # Unique sensor ID
        self.type  = type_capteur   # "Cardiaque" or "Oxygene"
        self.unite = unite          # Unit of measurement

    @abstractmethod
    def lire_valeur(self):
        """Read and return the current sensor value."""
        pass

    @abstractmethod
    def est_anormal(self):
        """Return True if the reading is abnormal."""
        pass

    def enregistrer_donnee(self):
        """Wrap the current reading in a DonneeMedicale object."""
        valeur = self.lire_valeur()
        return DonneeMedicale(self.id, valeur, self.unite)

    def to_dict(self):
        return {"id": self.id, "type": self.type, "unite": self.unite}

    def __str__(self):
        return f"Capteur#{self.id} [{self.type}] (unité: {self.unite})"


# ╔══════════════════════════════════════════════════════════════╗
# ║               CLASSE CapteurCardiaque                        ║
# ╚══════════════════════════════════════════════════════════════╝

class CapteurCardiaque(Capteur):
    """Heart rate sensor. Normal range: bpm_min to bpm_max."""

    def __init__(self, id, frequence_cardiaque=0.0,
                 bpm_min=60.0, bpm_max=100.0):
        super().__init__(id, "Cardiaque", "bpm")
        self.frequence_cardiaque = frequence_cardiaque
        self.bpm_min = bpm_min
        self.bpm_max = bpm_max

    def lire_valeur(self):
        return self.frequence_cardiaque

    def est_anormal(self):
        return (self.frequence_cardiaque < self.bpm_min or
                self.frequence_cardiaque > self.bpm_max)

    def to_dict(self):
        d = super().to_dict()
        d.update({
            "frequence_cardiaque": self.frequence_cardiaque,
            "bpm_min": self.bpm_min,
            "bpm_max": self.bpm_max
        })
        return d

    def __str__(self):
        return (f"CapteurCardiaque#{self.id} | "
                f"FC: {self.frequence_cardiaque} bpm "
                f"(normal: {self.bpm_min}–{self.bpm_max})")


# ╔══════════════════════════════════════════════════════════════╗
# ║                CLASSE CapteurOxygene                         ║
# ╚══════════════════════════════════════════════════════════════╝

class CapteurOxygene(Capteur):
    """Blood oxygen (SpO2) sensor. Normal range: spo2_min to spo2_max."""

    def __init__(self, id, spo2=0.0, spo2_min=95.0, spo2_max=100.0):
        super().__init__(id, "Oxygene", "%")
        self.spo2     = spo2
        self.spo2_min = spo2_min
        self.spo2_max = spo2_max

    def lire_valeur(self):
        return self.spo2

    def est_anormal(self):
        return self.spo2 < self.spo2_min or self.spo2 > self.spo2_max

    def to_dict(self):
        d = super().to_dict()
        d.update({
            "spo2": self.spo2,
            "spo2_min": self.spo2_min,
            "spo2_max": self.spo2_max
        })
        return d

    def __str__(self):
        return (f"CapteurOxygene#{self.id} | "
                f"SpO2: {self.spo2}% "
                f"(normal: {self.spo2_min}–{self.spo2_max})")


# ╔══════════════════════════════════════════════════════════════╗
# ║                     CLASSE Alerte                            ║
# ╚══════════════════════════════════════════════════════════════╝

class Alerte:
    """Alert generated when an anomaly is detected."""

    _id_counter = 1   # Auto-increment ID

    def __init__(self, type_alerte, message, niveau):
        self.id      = Alerte._id_counter
        Alerte._id_counter += 1
        self.type    = type_alerte   # e.g. "Anomalie Physiologique"
        self.message = message       # Human-readable description
        self.date    = datetime.now()
        self.niveau  = niveau        # "INFO", "AVERTISSEMENT", or "CRITIQUE"

    def notifier(self):
        """Print the alert."""
        print(f"\n  {'='*50}")
        print(f"   ALERTE [{self.niveau}] — {self.type}")
        print(f"     Message : {self.message}")
        print(f"     Date    : {self.date.strftime('%Y-%m-%d %H:%M:%S')}")
        print(f"  {'='*50}")

    def est_critique(self):
        return self.niveau.upper() == "CRITIQUE"

    def to_dict(self):
        return {
            "id"      : self.id,
            "type"    : self.type,
            "message" : self.message,
            "date"    : self.date.strftime('%Y-%m-%d %H:%M:%S'),
            "niveau"  : self.niveau
        }

    def __str__(self):
        return f"Alerte#{self.id} [{self.niveau}] {self.type}: {self.message}"


# ╔══════════════════════════════════════════════════════════════╗
# ║                     CLASSE Patient                           ║
# ╚══════════════════════════════════════════════════════════════╝

class Patient:
    """A patient monitored by the surveillance system."""

    def __init__(self, id, nom, age, sexe):
        self.id       = id
        self.nom      = nom
        self.age      = age
        self.sexe     = sexe
        self.etat     = "Stable"   # "Stable", "Anormal", or "Critique"
        self.capteurs = []         # List of Capteur objects

    def ajouter_capteur(self, capteur):
        """Add a sensor to this patient."""
        self.capteurs.append(capteur)
        print(f"   Capteur '{capteur.type}' ajouté au patient {self.nom}.")

    def supprimer_capteur(self, id):
        """Remove a sensor by its ID."""
        before = len(self.capteurs)
        self.capteurs = [c for c in self.capteurs if c.id != id]
        if len(self.capteurs) < before:
            print(f"   Capteur#{id} supprimé du patient {self.nom}.")
        else:
            print(f"   Capteur#{id} introuvable pour le patient {self.nom}.")

    def get_etat(self):
        return self.etat

    def afficher_infos(self):
        print(f"\n  👤 Patient#{self.id}: {self.nom}")
        print(f"     Âge    : {self.age} ans | Sexe : {self.sexe}")
        print(f"     État   : {self.etat}")
        print(f"     Capteurs ({len(self.capteurs)}) :")
        for c in self.capteurs:
            print(f"       - {c}")

    def to_dict(self):
        return {
            "id"       : self.id,
            "nom"      : self.nom,
            "age"      : self.age,
            "sexe"     : self.sexe,
            "etat"     : self.etat,
            "capteurs" : [c.to_dict() for c in self.capteurs]
        }

    def __str__(self):
        return (f"Patient#{self.id} {self.nom} "
                f"({self.age}a, {self.sexe}) — État: {self.etat}")


# ╔══════════════════════════════════════════════════════════════╗
# ║               CLASSE SystemeSurveillance                     ║
# ╚══════════════════════════════════════════════════════════════╝

class SystemeSurveillance:
    """
    Central surveillance system.
    Manages patients, detects anomalies, and generates alerts.
    """

    def __init__(self, id, nom, description):
        self.id          = id
        self.nom         = nom
        self.description = description
        self.patients    = []   # List of Patient
        self.alertes     = []   # List of Alerte

    # ── Patient management ─────────────────────────────────────
    def ajouter_patient(self, patient):
        self.patients.append(patient)
        print(f"  Patient '{patient.nom}' ajouté au système.")

    def supprimer_patient(self, id):
        before = len(self.patients)
        self.patients = [p for p in self.patients if p.id != id]
        if len(self.patients) < before:
            print(f"  Patient#{id} supprimé du système.")
        else:
            print(f"   Patient#{id} introuvable.")

    def _trouver_patient(self, id):
        for p in self.patients:
            if p.id == id:
                return p
        return None

    # ── Analysis ───────────────────────────────────────────────
    def evaluer_etat_patient(self, patient):
        """Check all sensors and update the patient's health state."""
        anomalies = [c for c in patient.capteurs if c.est_anormal()]
        if not anomalies:
            patient.etat = "Stable"
        elif len(anomalies) >= 2:
            patient.etat = "Critique"
        else:
            patient.etat = "Anormal"
        return patient.etat

    def detecter_anomalie(self, patient):
        """Return True if any sensor is abnormal."""
        return any(c.est_anormal() for c in patient.capteurs)

    def generer_alerte(self, patient):
        """Generate and store an alert if the patient has anomalies."""
        etat = self.evaluer_etat_patient(patient)
        if etat == "Stable":
            return None

        messages = []
        for capteur in patient.capteurs:
            if capteur.est_anormal():
                valeur = capteur.lire_valeur()
                if isinstance(capteur, CapteurCardiaque):
                    messages.append(
                        f"FC={valeur} bpm "
                        f"(hors [{capteur.bpm_min}–{capteur.bpm_max}])"
                    )
                elif isinstance(capteur, CapteurOxygene):
                    messages.append(
                        f"SpO2={valeur}% "
                        f"(hors [{capteur.spo2_min}–{capteur.spo2_max}])"
                    )

        message = f"Patient {patient.nom} (#{patient.id}): " + " | ".join(messages)
        niveau  = "CRITIQUE" if etat == "Critique" else "AVERTISSEMENT"
        alerte  = Alerte("Anomalie Physiologique", message, niveau)
        self.alertes.append(alerte)
        alerte.notifier()
        return alerte

    def surveiller_tous(self):
        """Run full surveillance on all patients."""
        print(f"\n  {'='*52}")
        print(f"  Surveillance en cours — {self.nom}")
        print(f"  {'='*52}")
        if not self.patients:
            print("  Aucun patient enregistré.")
            return
        for patient in self.patients:
            etat = self.evaluer_etat_patient(patient)
            print(f"\n   {patient.nom} → État: {etat}")
            if etat != "Stable":
                self.generer_alerte(patient)
        print(f"\n  {'='*52}")
        print(f"  Terminé. {len(self.alertes)} alerte(s) au total.")
        print(f"  {'='*52}\n")

    def afficher_patients(self):
        if not self.patients:
            print("  Aucun patient enregistré.")
            return
        print(f"\n  Patients ({len(self.patients)}) :")
        for p in self.patients:
            print(f"    • {p}")

    def afficher_alertes(self):
        if not self.alertes:
            print("  Aucune alerte générée.")
            return
        print(f"\n  Alertes ({len(self.alertes)}) :")
        for a in self.alertes:
            print(f"    • {a}")

    def to_dict(self):
        return {
            "id"          : self.id,
            "nom"         : self.nom,
            "description" : self.description,
            "patients"    : [p.to_dict() for p in self.patients],
            "alertes"     : [a.to_dict() for a in self.alertes]
        }


# ╔══════════════════════════════════════════════════════════════╗
# ║               FONCTIONS DE GESTION DE FICHIERS               ║
# ╚══════════════════════════════════════════════════════════════╝

def get_data_folder():

    try:
        base = os.path.dirname(os.path.abspath(__file__))
    except NameError:
        base = os.getcwd()
    folder = os.path.join(base, "data")
    os.makedirs(folder, exist_ok=True)
    return folder


def sauvegarder_json(data, chemin=None):
    """Save system state dict to a JSON file."""
    if chemin is None:
        ts     = datetime.now().strftime("%Y%m%d_%H%M%S")
        chemin = os.path.join(get_data_folder(), f"sauvegarde_{ts}.json")
    with open(chemin, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=4)
    print(f"   Sauvegardé dans : {chemin}")
    return chemin


def charger_json(chemin):
   
    if not os.path.exists(chemin):
        print(f"   Fichier introuvable : {chemin}")
        return {}
    with open(chemin, "r", encoding="utf-8") as f:
        data = json.load(f)
    print(f"   Chargé depuis : {chemin}")
    return data


def exporter_alertes_csv(alertes, chemin=None):
    """Export alerts to a CSV file."""
    if chemin is None:
        ts     = datetime.now().strftime("%Y%m%d_%H%M%S")
        chemin = os.path.join(get_data_folder(), f"alertes_{ts}.csv")
    with open(chemin, "w", newline="", encoding="utf-8") as f:
        w = csv.writer(f)
        w.writerow(["ID", "Type", "Message", "Date", "Niveau"])
        for a in alertes:
            w.writerow([a.id, a.type, a.message,
                        a.date.strftime('%Y-%m-%d %H:%M:%S'), a.niveau])
    print(f"   Alertes exportées : {chemin}")
    return chemin


def exporter_patients_csv(patients, chemin=None):
    """Export patients to a CSV file."""
    if chemin is None:
        ts     = datetime.now().strftime("%Y%m%d_%H%M%S")
        chemin = os.path.join(get_data_folder(), f"patients_{ts}.csv")
    with open(chemin, "w", newline="", encoding="utf-8") as f:
        w = csv.writer(f)
        w.writerow(["ID", "Nom", "Age", "Sexe", "Etat", "Nb Capteurs"])
        for p in patients:
            w.writerow([p.id, p.nom, p.age, p.sexe,
                        p.etat, len(p.capteurs)])
    print(f"  Patients exportés : {chemin}")
    return chemin


def lister_sauvegardes():
    """List all JSON files saved in the data folder."""
    folder = get_data_folder()
    return [f for f in os.listdir(folder) if f.endswith(".json")]


# ╔══════════════════════════════════════════════════════════════╗
# ║                  MENU INTERACTIF (CLI)                       ║
# ╚══════════════════════════════════════════════════════════════╝

# ── Global state ──────────────────────────────────────────────
systeme         = SystemeSurveillance(1, "SurveillanceMed",
                                      "Surveillance automatisée des dispositifs médicaux")
_next_patient_id = 1
_next_capteur_id = 1


def nouveau_pid():
    global _next_patient_id
    pid = _next_patient_id
    _next_patient_id += 1
    return pid


def nouveau_cid():
    global _next_capteur_id
    cid = _next_capteur_id
    _next_capteur_id += 1
    return cid


def demander_int(prompt, defaut=None):
    while True:
        val = input(prompt).strip()
        if val == "" and defaut is not None:
            return defaut
        try:
            return int(val)
        except ValueError:
            print("    Entrez un nombre entier valide.")


def demander_float(prompt, defaut=None):
    while True:
        val = input(prompt).strip()
        if val == "" and defaut is not None:
            return defaut
        try:
            return float(val)
        except ValueError:
            print("    Entrez un nombre décimal valide.")


# ── Menu actions ───────────────────────────────────────────────

def action_ajouter_patient():
    print("\n  ── Ajouter un patient ──")
    nom  = input("  Nom complet  : ").strip() or "Patient Inconnu"
    age  = demander_int("  Âge          : ", defaut=30)
    sexe = input("  Sexe (M/F)   : ").strip().upper() or "M"
    systeme.ajouter_patient(Patient(nouveau_pid(), nom, age, sexe))


def action_afficher_patients():
    systeme.afficher_patients()
    if systeme.patients:
        rep = input("\n  Détail d'un patient ? (ID ou Entrée) : ").strip()
        if rep:
            try:
                p = systeme._trouver_patient(int(rep))
                if p:
                    p.afficher_infos()
                else:
                    print("    Patient introuvable.")
            except ValueError:
                pass


def action_supprimer_patient():
    systeme.afficher_patients()
    if not systeme.patients:
        return
    pid = demander_int("\n  ID du patient à supprimer : ")
    systeme.supprimer_patient(pid)


def action_ajouter_capteur():
    systeme.afficher_patients()
    if not systeme.patients:
        return
    pid     = demander_int("\n  ID du patient : ")
    patient = systeme._trouver_patient(pid)
    if not patient:
        print("   Patient introuvable.")
        return

    print("\n  Type de capteur :")
    print("    1. Capteur Cardiaque (fréquence cardiaque)")
    print("    2. Capteur Oxygène   (SpO2)")
    choix = demander_int("  Choix [1-2] : ")
    cid   = nouveau_cid()

    if choix == 1:
        fc      = demander_float("  Fréquence cardiaque (bpm)  : ", defaut=75.0)
        bpm_min = demander_float("  BPM min normal [défaut 60]  : ", defaut=60.0)
        bpm_max = demander_float("  BPM max normal [défaut 100] : ", defaut=100.0)
        patient.ajouter_capteur(CapteurCardiaque(cid, fc, bpm_min, bpm_max))
    elif choix == 2:
        spo2     = demander_float("  SpO2 actuel (%)              : ", defaut=98.0)
        spo2_min = demander_float("  SpO2 min normal [défaut 95]  : ", defaut=95.0)
        spo2_max = demander_float("  SpO2 max normal [défaut 100] : ", defaut=100.0)
        patient.ajouter_capteur(CapteurOxygene(cid, spo2, spo2_min, spo2_max))
    else:
        print("   Choix invalide.")


def action_surveillance():
    if not systeme.patients:
        print("   Aucun patient à surveiller.")
        return
    systeme.surveiller_tous()


def action_sauvegarder():
    sauvegarder_json(systeme.to_dict())


def action_charger():
    fichiers = lister_sauvegardes()
    if fichiers:
        print("  Fichiers disponibles :")
        for i, f in enumerate(fichiers, 1):
            print(f"    {i}. {f}")
    chemin = input("  Chemin complet du fichier JSON : ").strip()
    if not chemin:
        print("   Aucun fichier spécifié.")
        return
    data = charger_json(chemin)
    if data:
        print(f"  Contenu : {len(data.get('patients',[]))} patient(s), "
              f"{len(data.get('alertes',[]))} alerte(s)")


def action_exporter():
    print("    1. Exporter les patients (CSV)")
    print("    2. Exporter les alertes  (CSV)")
    choix = demander_int("  Choix [1-2] : ")
    if choix == 1:
        exporter_patients_csv(systeme.patients)
    elif choix == 2:
        exporter_alertes_csv(systeme.alertes)
    else:
        print("    Choix invalide.")


def charger_demo():
    """Load 3 pre-configured demo patients."""
    global _next_patient_id, _next_capteur_id
    print("\n  Chargement du scénario de démonstration...")

    # Patient 1 — Stable
    p1 = Patient(nouveau_pid(), "Ahmed Benali", 45, "M")
    p1.ajouter_capteur(CapteurCardiaque(nouveau_cid(), 78.0,  60.0, 100.0))
    p1.ajouter_capteur(CapteurOxygene  (nouveau_cid(), 98.0,  95.0, 100.0))
    systeme.ajouter_patient(p1)

    # Patient 2 — Abnormal heart rate
    p2 = Patient(nouveau_pid(), "Fatima Zahra", 62, "F")
    p2.ajouter_capteur(CapteurCardiaque(nouveau_cid(), 130.0, 60.0, 100.0))
    p2.ajouter_capteur(CapteurOxygene  (nouveau_cid(),  97.0, 95.0, 100.0))
    systeme.ajouter_patient(p2)

    # Patient 3 — Critical (both sensors abnormal)
    p3 = Patient(nouveau_pid(), "Karim Idrissi", 70, "M")
    p3.ajouter_capteur(CapteurCardiaque(nouveau_cid(),  38.0, 60.0, 100.0))
    p3.ajouter_capteur(CapteurOxygene  (nouveau_cid(),  88.0, 95.0, 100.0))
    systeme.ajouter_patient(p3)

    print("  Scénario chargé : 3 patients, 6 capteurs.")


def afficher_menu():
    print("\n" + "═" * 52)
    print("   SYSTÈME DE SURVEILLANCE MÉDICALE")
    print("═" * 52)
    print("  1.  Ajouter un patient")
    print("  2.  Afficher les patients")
    print("  3.  Supprimer un patient")
    print("  4.  Ajouter un capteur à un patient")
    print("  5.  Lancer la surveillance")
    print("  6.  Afficher les alertes")
    print("  ─────────────────────────────────────────")
    print("  7.  Sauvegarder (JSON)")
    print("  8.  Charger un fichier JSON")
    print("  9.  Exporter CSV")
    print("  ─────────────────────────────────────────")
    print("  D.  Scénario de démonstration")
    print("  Q.  Quitter")
    print("═" * 52)


# ╔══════════════════════════════════════════════════════════════╗
# ║                        LANCEMENT                             ║
# ╚══════════════════════════════════════════════════════════════╝

def main():
    print("\n  Bienvenue dans le Système de Surveillance Médicale")
    print(f"  {systeme.nom} — {systeme.description}\n")

    actions = {
        "1": action_ajouter_patient,
        "2": action_afficher_patients,
        "3": action_supprimer_patient,
        "4": action_ajouter_capteur,
        "5": action_surveillance,
        "6": systeme.afficher_alertes,
        "7": action_sauvegarder,
        "8": action_charger,
        "9": action_exporter,
        "D": charger_demo,
    }

    while True:
        afficher_menu()
        choix = input("  Votre choix : ").strip().upper()

        if choix == "Q":
            print("\n  Au revoir !\n")
            break
        elif choix in actions:
            actions[choix]()
        else:
            print("    Option invalide. Réessayez.")


# Run automatically when the file is opened in Spyder or executed
main()
