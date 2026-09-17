# TP JUnit – Gestion de comptes bancaires

## 1. Présentation

Ce projet implémente et teste un mini-système de gestion de comptes bancaires :
- `CompteBancaire` gère le solde d'un compte (dépôts, retraits, découvert autorisé, calcul d'intérêts).
- `GestionnaireComptes` gère un ensemble de comptes (ajout, recherche, virement entre comptes, statistiques).

Le comportement est validé par une suite de tests JUnit 5 couvrant les cas nominaux, les cas limites
(bornes du découvert autorisé, montants nuls) et les cas d'erreur (exceptions personnalisées).

## 2. Choix de conception

- **Exceptions métier dédiées** : `MontantInvalideException`, `SoldeInsuffisantException`,
  `CompteDejaExistantException` et `CompteInconnuException` héritent de `RuntimeException`, pour ne
  pas alourdir les signatures des méthodes avec des `throws` répétés à chaque appel.
- **Atomicité du virement** : dans `GestionnaireComptes.virement`, le retrait est toujours effectué
  avant le dépôt. Si `retirer()` lève une exception, l'instruction de dépôt suivante n'est jamais
  atteinte : aucun solde n'est donc modifié en cas d'échec.
- **Approche de développement** : le code métier de chaque classe a été écrit en premier, puis les
  tests juste après pour le valider, plutôt qu'une approche TDD stricte (tests écrits avant le code).

## 3. Comment lancer les tests

Dans Eclipse : clic droit sur `src` (ou sur une classe de test précise) → **Run As** → **JUnit Test**.

## 4. Récapitulatif des tests

| Classe de test            | Nombre de tests | Ce qu'ils couvrent                                                                 |
|----------------------------|:---------------:|-------------------------------------------------------------------------------------|
| `CompteBancaireTest`       | 13               | Dépôt/retrait/intérêts nominaux, retrait exact au découvert autorisé, retrait au-delà du découvert, montants nuls, montants négatifs, taux négatif, détection du découvert |
| `GestionnaireComptesTest`  | 10               | Ajout/recherche nominale, virement réussi, solde total, liste des comptes en découvert, IBAN déjà existant, IBAN inconnu, virement échoué (atomicité vérifiée), virement vers IBAN inconnu |

## 5. Difficultés rencontrées

- Eclipse plaçait par erreur les nouvelles classes de test dans le package `exceptions` au lieu du
  package par défaut (l'assistant "New Class" réutilise le dernier package sélectionné), ce qui cassait
  la compilation. Résolu avec `Refactor > Move...` pour déplacer le fichier au bon endroit.
- Le push vers GitHub a échoué une première fois avec l'erreur "Can't connect to any repository" car la
  branche locale n'était pas encore liée au dépôt distant. Résolu avec `Team > Remote > Push Branch`, qui
  configure ce lien de façon permanente. L'authentification a aussi nécessité un Personal Access Token
  plutôt que le mot de passe GitHub classique, GitHub n'acceptant plus les mots de passe pour ce type
  d'opération depuis 2021.

## 6. Bilan

Ce TP a permis de comprendre plusieurs aspects essentiels des tests unitaires : l'importance de tester
les cas limites (comme un retrait qui atteint exactement la borne du découvert autorisé) et pas
seulement les cas où tout se passe bien, le rôle de `@BeforeEach` pour repartir d'un état propre avant
chaque test et éviter qu'un test en influence un autre, et l'intérêt de vérifier explicitement
l'atomicité d'une opération complexe comme le virement (s'assurer qu'aucun solde ne bouge si une étape
intermédiaire échoue).
