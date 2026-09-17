import exceptions.CompteDejaExistantException;
import exceptions.CompteInconnuException;
import exceptions.SoldeInsuffisantException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GestionnaireComptesTest {

    private GestionnaireComptes gestionnaire;
    private CompteBancaire compteAlice;
    private CompteBancaire compteBob;

    @BeforeEach
    void setUp() {
        gestionnaire = new GestionnaireComptes();
        compteAlice = new CompteBancaire("FR001", "Alice", 100.0, 50.0);
        compteBob = new CompteBancaire("FR002", "Bob", 200.0, 0.0);
        gestionnaire.ajouterCompte(compteAlice);
        gestionnaire.ajouterCompte(compteBob);
    }
 // ---------- Cas nominaux ----------

    @Test
    void testAjoutCompte() {
        CompteBancaire nouveau = new CompteBancaire("FR003", "Charlie", 0.0, 0.0);
        gestionnaire.ajouterCompte(nouveau);
        assertEquals(nouveau, gestionnaire.rechercherCompte("FR003"),
            "Le compte ajouté doit être retrouvable par son IBAN");
    }

    @Test
    void testRechercheCompteExistant() {
        CompteBancaire trouve = gestionnaire.rechercherCompte("FR001");
        assertEquals("Alice", trouve.getTitulaire(), "Le compte FR001 doit appartenir à Alice");
    }

    @Test
    void testVirementReussi() {
        gestionnaire.virement("FR002", "FR001", 50.0);
        assertEquals(150.0, gestionnaire.rechercherCompte("FR002").getSolde(), 0.001,
            "Le solde source doit diminuer du montant viré");
        assertEquals(150.0, gestionnaire.rechercherCompte("FR001").getSolde(), 0.001,
            "Le solde destination doit augmenter du montant viré");
    }

    @Test
    void testSoldeTotal() {
        assertEquals(300.0, gestionnaire.soldeTotal(), 0.001,
            "Le solde total doit être la somme des soldes de tous les comptes");
    }

    // ---------- Cas limites ----------

    @Test
    void testListeComptesEnDecouvertVideParDefaut() {
        assertTrue(gestionnaire.listeComptesEnDecouvert().isEmpty(),
            "Aucun compte ne doit être en découvert au départ");
    }

    @Test
    void testListeComptesEnDecouvertApresRetrait() {
        gestionnaire.rechercherCompte("FR001").retirer(120.0); // solde Alice devient -20
        List<CompteBancaire> enDecouvert = gestionnaire.listeComptesEnDecouvert();
        assertEquals(1, enDecouvert.size(), "Un seul compte doit être en découvert");
        assertEquals("FR001", enDecouvert.get(0).getIban());
    }

    // ---------- Cas d'erreur ----------

    @Test
    void testAjoutCompteIbanExistantLeveCompteDejaExistantException() {
        CompteBancaire doublon = new CompteBancaire("FR001", "Autre Personne", 0.0, 0.0);
        assertThrows(CompteDejaExistantException.class, () -> gestionnaire.ajouterCompte(doublon),
            "Ajouter un IBAN déjà présent doit lever CompteDejaExistantException");
    }

    @Test
    void testRechercheCompteInconnuLeveCompteInconnuException() {
        assertThrows(CompteInconnuException.class, () -> gestionnaire.rechercherCompte("FR999"),
            "Rechercher un IBAN inconnu doit lever CompteInconnuException");
    }

    @Test
    void testVirementEchoueAucunSoldeNeBouge() {
        assertThrows(SoldeInsuffisantException.class,
            () -> gestionnaire.virement("FR001", "FR002", 1000.0),
            "Le virement doit échouer si le compte source n'a pas les fonds");

        assertEquals(100.0, gestionnaire.rechercherCompte("FR001").getSolde(), 0.001,
            "Le solde source ne doit pas avoir changé après un virement échoué");
        assertEquals(200.0, gestionnaire.rechercherCompte("FR002").getSolde(), 0.001,
            "Le solde destination ne doit pas avoir changé après un virement échoué");
    }

    @Test
    void testVirementVersCompteInconnuNeModifiePasLaSource() {
        assertThrows(CompteInconnuException.class,
            () -> gestionnaire.virement("FR001", "FR999", 10.0),
            "Un virement vers un IBAN inconnu doit lever CompteInconnuException");
        assertEquals(100.0, gestionnaire.rechercherCompte("FR001").getSolde(), 0.001,
            "Le solde source ne doit pas bouger si le compte destination n'existe pas");
    }
}