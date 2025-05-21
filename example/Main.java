package org.example;

import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.example.Historique;

public class Main extends Application {

    // Dimensions de la fenêtre
    public static final double SCENE_WIDTH   = 1200;
    public static final double SCENE_HEIGHT  = 900;

    // Rayon de la roue
    public static final double WHEEL_RADIUS  = 320;

    // Durée du spin
    public static final double SPIN_DURATION = 5.0; // en secondes

    @Override
    public void start(Stage primaryStage) {
        // Root principal
        BorderPane root = new BorderPane();

        // === 1) Titre + Résultat (en haut) ===
        Titre bandeau = new Titre();
        Resultat resultat = new Resultat();

        // Rapprochés : spacing = 4 px
        HBox topBox = new HBox(resultat.getNode());
        topBox.setAlignment(Pos.CENTER);

        VBox topContainer = new VBox(4,
                bandeau.getNode(),
                topBox
        );
        topContainer.setAlignment(Pos.TOP_LEFT);
        root.setTop(topContainer);

        // Image de fond
        root.setBackground(Theme.makeBackgroundCover("/img.png"));

        // === 2) Participants (gauche) ===
        Users users = new Users();
        VBox leftBox = new VBox(10, users.getRootPane());
        // On supprime le padding-top
        leftBox.setPadding(new Insets(0, 10, 10, 20));
        leftBox.setAlignment(Pos.TOP_CENTER);

        // Agrandit la zone : 420 px large × 820 px haut
        leftBox.setPrefSize(420, 820);
        root.setLeft(leftBox);

        // === 3) Gains (droite) ===
        Gains gains = new Gains(users.getParticipants());
        Historique historique = new Historique(gains);
        VBox rightBox = new VBox(10, gains.getRootPane());
        // Padding-top = 0 => ils sont “collés” sous le titre
        rightBox.setPadding(new Insets(0, 20, 10, 10));
        rightBox.setAlignment(Pos.TOP_CENTER);

        // Agrandit la zone : 460 px large × 820 px haut
        rightBox.setPrefSize(460, 820);
        root.setRight(rightBox);

        // === 4) Roue au centre ===
        Roue roue = new Roue(resultat);
        roue.setOnSpinFinished(pseudo -> historique.logResult(pseudo));
        StackPane centerPane = new StackPane(roue.getRootPane());
        centerPane.setAlignment(Pos.CENTER);
        centerPane.setMaxSize(WHEEL_RADIUS * 2 + 50, WHEEL_RADIUS * 2 + 50);
        root.setCenter(centerPane);

        // Recharge la sauvegarde, s’il y en a une
        try {
            Path f = Path.of("loterie-save.txt");
            if (Files.exists(f)) {
                var lines = Files.readAllLines(f);
                boolean objetsPart = false;
                boolean bonusPart  = false;
                for (String line : lines) {
                    if (line.startsWith("#")) {
                        objetsPart = line.startsWith("#Objets");
                        bonusPart  = line.startsWith("#Bonus");
                        continue;
                    }
                    if (bonusPart) {
                        try {
                            gains.setExtraKamas(Integer.parseInt(line.trim()));
                        } catch (NumberFormatException ignore) {
                            gains.setExtraKamas(0);
                        }
                    } else if (objetsPart) {
                        gains.getObjets().add(line);
                    } else {
                        String[] parts = line.split(";", 3);
                        if (parts.length == 3) {
                            users.getParticipants().add(
                                    new Participant(
                                            parts[0],
                                            Integer.parseInt(parts[1]),
                                            parts[2]
                                    )
                            );
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Impossible de relire la sauvegarde : " + ex.getMessage());
        }

        // Mise à jour initiale de la roue
        roue.updateWheelDisplay(users.getParticipantNames());

        // Surveille les changements sur la liste de participants
        users.getParticipants().addListener(
                (ListChangeListener<Participant>) change -> {
                    roue.updateWheelDisplay(users.getParticipantNames());
                }
        );

        // === 5) Boutons en bas ===
        Button spinButton = new Button("Lancer la roue !");
        spinButton.setFont(Font.font("Arial", 16));
        spinButton.setOnAction(e -> {
            roue.updateWheelDisplay(users.getParticipantNames());
            roue.spinTheWheel(users.getParticipantNames());
        });

        Button optionsButton = new Button("Options...");
        optionsButton.setOnAction(e -> {
            OptionRoue optWin = new OptionRoue();
            optWin.showAndWait();
            roue.updateWheelDisplay(users.getParticipantNames());
        });

        Button resetButton = new Button("Reset Position");
        resetButton.setOnAction(e -> roue.resetPosition());

        Button saveButton = new Button("Sauvegarder état");
        saveButton.setOnAction(e -> {
            try {
                Save.save(users.getParticipants(),
                        gains.getObjets(),
                        gains.getExtraKamas());
                resultat.setMessage("État sauvegardé ✔");
            } catch (IOException ex) {
                resultat.setMessage("Erreur de sauvegarde ✖");
                ex.printStackTrace();
            }
        });

        Button cleanButton = new Button("Nettoyer");
        cleanButton.setOnAction(e -> {
            Save.reset(users.getParticipants(), gains.getObjets());
            gains.setExtraKamas(0);
            roue.updateWheelDisplay(users.getParticipantNames());
            resultat.setMessage("Nouvelle loterie prête");
        });

        // Bouton Historique
        Button historyButton = new Button("Historique");
        historyButton.setOnAction(e -> historique.show());

        // === Nouveau bouton "Plein écran" ===
        Button fullScreenButton = new Button("Plein écran");
        fullScreenButton.setOnAction(e -> {
            // Bascule l'état "fullscreen" à chaque clic
            boolean current = primaryStage.isFullScreen();
            primaryStage.setFullScreen(!current);
        });

        // Style
        Theme.styleButton(spinButton);
        Theme.styleButton(optionsButton);
        Theme.styleButton(resetButton);
        Theme.styleButton(saveButton);
        Theme.styleButton(cleanButton);
        Theme.styleButton(historyButton);
        Theme.styleButton(fullScreenButton);

        HBox bottomBox = new HBox(30,
                spinButton, optionsButton, resetButton,
                saveButton, cleanButton,
                fullScreenButton,
                historyButton
        );
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(16, 0, 20, 0));
        root.setBottom(bottomBox);

        // === 6) Scène + Stage ===
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setTitle("Loterie de la guilde EVOLUTION");
        primaryStage.setScene(scene);

        // -> Optionnel : enlever l'indication pour quitter le fullscreen
        // primaryStage.setFullScreenExitHint("");

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}