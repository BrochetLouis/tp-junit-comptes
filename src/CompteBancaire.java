
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
}