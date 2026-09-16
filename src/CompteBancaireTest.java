import exceptions.MontantInvalideException;
import exceptions.SoldeInsuffisantException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompteBancaireTest {

    private CompteBancaire compte;

    @BeforeEach
    void setUp() {
        compte = new CompteBancaire("FR001", "Alice", 100.0, 50.0);
    }
    
    // ---------- Cas nominaux ----------
    
    @Test
    void testDepotNormal() {
        compte.deposer(50.0);
        assertEquals(150.0, compte.getSolde(), 0.001, "Le solde doit augmenter du montant déposé");
    }
    
    @Test
    void testRetraitNormal() {
        compte.retirer(30.0);
        assertEquals(70.0, compte.getSolde(), 0.001, "Le solde doit diminuer du montant retiré");
    }

    @Test
    void testCalculInteretsSurSoldePositif() {
        double interets = compte.calculerInterets(0.05);
        assertEquals(5.0, interets, 0.001, "Les intérêts doivent valoir solde * taux");
        assertEquals(100.0, compte.getSolde(), 0.001, "Le calcul des intérêts ne doit pas modifier le solde");
    }

    // ---------- Cas limites ----------

    @Test
    void testRetraitJusquauDecouvertAutoriseExact() {
        compte.retirer(150.0); // solde 100 - 150 = -50, exactement le découvert autorisé
        assertEquals(-50.0, compte.getSolde(), 0.001, "Le solde doit pouvoir atteindre exactement -decouvertAutorise");
    }

    @Test
    void testRetraitDUnCentimeDePlusQueLeDecouvertRefuse() {
        assertThrows(SoldeInsuffisantException.class, () -> compte.retirer(150.01),
            "Un retrait dépassant le découvert autorisé, même d'un centime, doit être refusé");
    }

    @Test
    void testDepotMontantZeroRefuse() {
        assertThrows(MontantInvalideException.class, () -> compte.deposer(0.0),
            "Un dépôt de montant nul doit être refusé");
    }

    @Test
    void testRetraitMontantZeroRefuse() {
        assertThrows(MontantInvalideException.class, () -> compte.retirer(0.0),
            "Un retrait de montant nul doit être refusé");
    }

    // ---------- Cas d'erreur (exceptions) ----------

    @Test
    void testDepotMontantNegatifLeveMontantInvalideException() {
        assertThrows(MontantInvalideException.class, () -> compte.deposer(-10.0),
            "Un dépôt négatif doit lever MontantInvalideException");
    }

    @Test
    void testRetraitMontantNegatifLeveMontantInvalideException() {
        assertThrows(MontantInvalideException.class, () -> compte.retirer(-10.0),
            "Un retrait négatif doit lever MontantInvalideException");
    }

    @Test
    void testRetraitDepassantDecouvertLeveSoldeInsuffisantException() {
        assertThrows(SoldeInsuffisantException.class, () -> compte.retirer(1000.0),
            "Un retrait trop important doit lever SoldeInsuffisantException");
    }

    @Test
    void testInteretsAvecTauxNegatifLeveIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> compte.calculerInterets(-0.01),
            "Un taux négatif doit être rejeté");
    }

    @Test
    void testEstEnDecouvertApresGrosRetrait() {
        compte.retirer(120.0); // solde devient -20
        assertTrue(compte.estEnDecouvert(), "Un solde négatif doit être détecté comme découvert");
    }

    @Test
    void testEstEnDecouvertFauxParDefaut() {
        assertFalse(compte.estEnDecouvert(), "Un solde positif ne doit pas être un découvert");
    }
}