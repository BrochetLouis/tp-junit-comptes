
public class CompteBancaire {

    private final String iban;
    private String titulaire;
    private double solde;
    private double decouvertAutorise;

    public CompteBancaire(String iban, String titulaire, double soldeInitial, double decouvertAutorise) {
        this.iban = iban;
        this.titulaire = titulaire;
        this.solde = soldeInitial;
        this.decouvertAutorise = decouvertAutorise;
    }
    public void deposer(double montant) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant du dépôt doit être positif");
        }
        solde += montant;
    }
    public void retirer(double montant) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant du retrait doit être positif");
        }
        double nouveauSolde = solde - montant;
        if (nouveauSolde < -decouvertAutorise) {
            throw new IllegalArgumentException("Solde insuffisant compte tenu du découvert autorisé");
        }
        solde = nouveauSolde;
    }
    public double calculerInterets(double taux) {
        if (taux < 0) {
            throw new IllegalArgumentException("Le taux ne peut pas être négatif");
        }
        if (solde > 0) {
            return solde * taux;
        }
        return 0.0;
    }
    public boolean estEnDecouvert() {
        return solde < 0;
    }
    public double getSolde() {
        return solde;
    }
    public String getTitulaire() {
        return titulaire;
    }
    public String getIban() {
        return iban;
    }
}