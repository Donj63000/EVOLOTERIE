package org.example;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Fenêtre optionnelle pour régler la configuration
 * de la roue (ex. nombre de tickets perdants, durée de rotation, etc.).
 */
public class OptionRoue extends Stage {

    // Variable statique : nombre de tickets perdants (100 par défaut).
    private static int losingTickets = 100;

    // Nouvelle variable statique : durée de rotation (3.0 s par défaut)
    private static double spinDuration = 3.0;

    public OptionRoue() {
        setTitle("Options de la roue");

        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));

        // Champ pour le nombre de tickets perdants
        Label lblTickets = new Label("Nombre de tickets perdants :");
        TextField txtTickets = new TextField(String.valueOf(losingTickets));

        // Champ pour la durée de rotation
        Label lblDuration = new Label("Durée de rotation (secondes) :");
        TextField txtDuration = new TextField(String.valueOf(spinDuration));

        // Bouton pour enregistrer la valeur
        Button btnSave = new Button("Enregistrer");
        btnSave.setOnAction(e -> {
            try {
                // Lecture du nombre de tickets perdants
                int val = Integer.parseInt(txtTickets.getText().trim());
                if (val >= 0) {
                    losingTickets = val;
                }

                // Lecture de la durée de rotation
                double dur = Double.parseDouble(txtDuration.getText().trim());
                if (dur > 0) {
                    spinDuration = dur;
                }

                // On ferme la fenêtre après sauvegarde
                close();

            } catch (NumberFormatException ex) {
                // Gère l'erreur éventuelle, on peut ignorer ou afficher un message
            }
        });

        // Style Material sur le bouton
        Theme.styleButton(btnSave);

        root.getChildren().addAll(lblTickets, txtTickets, lblDuration, txtDuration, btnSave);

        Scene scene = new Scene(root, 300, 160);
        setScene(scene);
    }

    // Méthode statique pour récupérer la config du nombre de tickets perdants
    public static int getLosingTickets() {
        return losingTickets;
    }

    // Méthode statique pour récupérer la config de la durée de rotation (en secondes)
    public static double getSpinDuration() {
        return spinDuration;
    }
}
