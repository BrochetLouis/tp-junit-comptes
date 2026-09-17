import exceptions.CompteDejaExistantException;
import exceptions.CompteInconnuException;
import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

public class GestionnaireComptes {

    private final Map<String, CompteBancaire> comptes = new HashMap<>();
    public void ajouterCompte(CompteBancaire compte) {
        if (comptes.containsKey(compte.getIban())) {
            throw new CompteDejaExistantException(
                "Un compte avec l'IBAN " + compte.getIban() + " existe déjà");
        }
        comptes.put(compte.getIban(), compte);
    }
    public CompteBancaire rechercherCompte(String iban) {
        CompteBancaire compte = comptes.get(iban);
        if (compte == null) {
            throw new CompteInconnuException("Aucun compte trouvé pour l'IBAN " + iban);
        }
        return compte;
    }
    public void virement(String ibanSource, String ibanDestination, double montant) {
        CompteBancaire source = rechercherCompte(ibanSource);
        CompteBancaire destination = rechercherCompte(ibanDestination);

        source.retirer(montant);
        destination.deposer(montant);
    }
    public double soldeTotal() {
        double total = 0.0;
        for (CompteBancaire compte : comptes.values()) {
            total += compte.getSolde();
        }
        return total;
    }
    public List<CompteBancaire> listeComptesEnDecouvert() {
        List<CompteBancaire> resultat = new ArrayList<>();
        for (CompteBancaire compte : comptes.values()) {
            if (compte.estEnDecouvert()) {
                resultat.add(compte);
            }
        }
        return resultat;
    }
}