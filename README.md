# EVOLOTERIE

EVOLOTERIE est une petite application JavaFX simulant une loterie pour la guilde **EVOLUTION**. Elle permet d'entrer les participants avec leurs mises, de configurer les gains et d'animer une roue de la fortune pour déterminer un gagnant. Le projet a été conçu comme un exemple didactique.

## Prise en main rapide

```bash
git clone <repo>
cd EVOLOTERIE
mvn javafx:run        # lance l'application en mode développement
mvn package           # construit le jar autonome
java -jar target/demoloterie.jar
```

Le projet nécessite Java 21 et Maven. Aucune base de données n'est requise, toutes les informations sont stockées dans des fichiers texte.

## Fonctionnalités principales

- Gestion interactive des participants et de leurs mises
- Configuration de la cagnotte et des objets à gagner
- Roue animée entièrement paramétrable (tickets perdants et durée de rotation)
- Historique des tirages persistant
- Sauvegarde et restauration automatiques

## Fonctionnement général

L'interface graphique est constituée de plusieurs panneaux :

- **Participants** : liste éditable des joueurs (classe `Users`).
- **Gains** : cagnotte totale et objets en jeu (classe `Gains`).
- **Roue** : roue animée affichant chaque ticket (classe `Roue`).
- **Résultat** : capsule qui affiche le vainqueur après chaque tirage (classe `Resultat`).
- **Historique** : fenêtre listant tous les tirages précédents (classe `Historique`).

Le point d'entrée est `Launcher` qui appelle `Main`. Le fichier d'arrière-plan se trouve dans `resources/img.png`.

Les paramètres de la roue (nombre de tickets perdants, durée de rotation et vitesse initiale) sont modifiables via la fenêtre `OptionRoue`.

Les classes principales sont localisées dans le dossier `example/` et appartiennent au package `org.example`.

## Configuration

Depuis l'application il est possible d'ouvrir la fenêtre **OptionRoue** pour ajuster :

- le nombre de tickets perdants affichés sur la roue ;
- la durée de rotation avant de révéler le gagnant.
- la vitesse de rotation de la roue (réglable via un curseur, sans influer sur la position finale).

Ces réglages sont conservés pour la session en cours et permettent de personnaliser la loterie à chaque utilisation.

## Sauvegarde et historique

Deux fichiers texte sont utilisés afin de persister les données :

- `loterie-save.txt` : état courant (participants et objets et bonus). Les méthodes `Save.save` et `Save.reset` gèrent ce fichier.
- `loterie-historique.txt` : liste datée des tirages (gérée par `Historique`).

Extrait de la méthode `save` montrant le format très simple utilisé :

```java
    private static final Path FILE = Path.of("loterie-save.txt");

    /* ---------- Sauvegarde ---------- */
    public static void save(ObservableList<Participant> participants,
                            ObservableList<String> objets,
                            int extraKamas) throws IOException {

        StringBuilder sb = new StringBuilder("#Participants\n");
        for (Participant p : participants) {
            // nom;kamas;don
            sb.append(p.getName()).append(';')
                    .append(p.getKamas()).append(';')
                    .append(p.getDonation()).append('\n');
        }

        sb.append("#Objets\n");
        for (String o : objets) {
            sb.append(o).append('\n');
        }

        sb.append("#Bonus\n");
        sb.append(extraKamas).append('\n');

        Files.writeString(FILE, sb.toString());
    }
```

## Compilation

Le projet utilise Maven avec Java 21 et JavaFX 21.
Pour exécuter l'application en développement :

```bash
mvn javafx:run
```

Pour générer le jar autonome dans `target/demoloterie.jar` :

```bash
mvn package
```

Le jar produit contient toutes les dépendances (plugin Shade). Vous pouvez alors lancer la loterie avec :

```bash
java -jar target/demoloterie.jar
```

Par défaut, la configuration Maven cible la plate-forme Windows (`javafx.platform=win`).
Pour construire pour une autre plate-forme (linux, mac), modifiez cette propriété dans `pom.xml` avant d'exécuter `mvn package`.

## Structure des sources

```
example/          # code Java principal
resources/        # ressources (image de fond)
pom.xml           # configuration Maven
```

Les classes sont regroupées dans le package `org.example`.

## Licence

Ce projet est fourni à titre d'exemple et sans garantie. Vous pouvez le modifier librement pour vos besoins.


Copyright DonJ
