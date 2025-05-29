# EVOLOTERIE

EVOLOTERIE est une petite application JavaFX simulant une loterie pour la guilde **EVOLUTION**(Made By Coca IG). Elle permet d'entrer les participants avec leurs mises, de configurer les gains et d'animer une roue de la fortune pour déterminer un gagnant. Le projet a été conçu comme un exemple didactique.

## Installation de Java

L'application s'exécute avec **Java&nbsp;21**. Si vous ne disposez pas de Java sur votre ordinateur, suivez les étapes ci-dessous :

### Windows

1. Rendez-vous sur le site Oracle ou un fournisseur d'OpenJDK.
2. Téléchargez l'installateur de la version **JDK&nbsp;21** (64&nbsp;bits).
3. Lancez l'installateur puis suivez les instructions jusqu'au bout.
4. Une fois l'installation terminée, ouvrez un terminal (*Invite de commandes*) et tapez :

   ```bash
   java -version
   ```

   Vous devriez voir un numéro de version commençant par `21`.

### Linux

Selon votre distribution, utilisez votre gestionnaire de paquets préféré. Par exemple sur **Debian/Ubuntu** :

```bash
sudo apt-get install openjdk-21-jdk
```

Vérifiez ensuite la présence de Java avec `java -version`.

### macOS

Pour macOS, l'installation la plus simple passe par **Homebrew** :

```bash
brew install openjdk@21
```

Suivez ensuite les instructions affichées par Homebrew pour configurer votre environnement, puis contrôlez la version avec `java -version`.

Une fois Java installé, vous êtes prêt à utiliser l'application.

## Prise en main rapide

```bash
git clone <repo>
cd EVOLOTERIE
mvn javafx:run        # lance l'application en mode développement
mvn package           # construit le jar autonome
java -jar target/demoloterie.jar
```

Le projet nécessite Java 21 et Maven. Aucune base de données n'est requise, toutes les informations sont stockées dans des fichiers texte.

## Téléchargement du JAR

Si vous ne souhaitez pas compiler le projet vous-même, le dépôt contient déjà un fichier `demoloterie.jar` précompilé.

1. Rendez-vous sur la page GitHub du projet.
2. Cliquez sur le fichier `demoloterie.jar` puis sur le bouton **Download** afin de récupérer l'archive sur votre ordinateur.
3. Placez le fichier dans un dossier de travail (évitez les emplacements avec des espaces pour simplifier).

Une fois téléchargé, vous pouvez directement exécuter la loterie avec :

```bash
java -jar demoloterie.jar
```

La fenêtre principale devrait alors s'ouvrir. Vous n'avez plus qu'à renseigner des participants et à lancer la roue.

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
 - la vitesse de rotation de la roue (réglable via un curseur).

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

## Tester l'application

Pour vous familiariser avec la loterie, lancez simplement le JAR (soit celui que
vous avez téléchargé, soit celui obtenu après la compilation). Une fenêtre
s'ouvre avec plusieurs onglets :

1. **Participants** : ajoutez quelques noms et les mises associées en
   utilisant les boutons prévus à cet effet.
2. **Gains** : saisissez la cagnotte et les objets éventuellement en jeu.
3. **Roue** : configurez le nombre de tickets perdants dans le menu Options si
   vous le souhaitez, puis cliquez sur *Lancer la roue*.
4. Observez l'animation jusqu'à ce qu'un gagnant soit affiché dans l'onglet
   **Résultat**. Chaque tirage est ensuite archivé dans l'onglet **Historique**.

N'hésitez pas à effectuer plusieurs tirages pour vous entraîner. Toutes les
données sont enregistrées dans `loterie-save.txt` entre chaque session.

## Licence

Ce projet est fourni à titre d'exemple et sans garantie. Vous pouvez le modifier librement pour vos besoins.


Copyright DonJ
