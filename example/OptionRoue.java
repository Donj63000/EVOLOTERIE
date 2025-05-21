package org.example;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
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

    // Vitesse initiale de la roue (1.0 = normal)
    private static double spinSpeed = 1.0;

    public OptionRoue() {
        setTitle("Options de la roue");

        GridPane form = new GridPane();
        form.setVgap(10);
        form.setHgap(10);
        form.setPadding(new Insets(10));

        // Champ pour le nombre de tickets perdants
        Label lblTickets = new Label("Nombre de tickets perdants :");
        TextField txtTickets = new TextField(String.valueOf(losingTickets));
        Theme.styleTextField(txtTickets);

        // Champ pour la durée de rotation
        Label lblDuration = new Label("Durée de rotation (secondes) :");
        TextField txtDuration = new TextField(String.valueOf(spinDuration));
        Theme.styleTextField(txtDuration);

        // Champ pour la vitesse initiale
        Label lblSpeed = new Label("Vitesse de rotation :");
        Slider speedSlider = new Slider(0.5, 3.0, spinSpeed);
        speedSlider.setShowTickMarks(true);
        speedSlider.setShowTickLabels(true);
        Label lblSpeedVal = new Label(String.format("%.2f", spinSpeed));
        speedSlider.valueProperty().addListener((o, oldV, newV) ->
                lblSpeedVal.setText(String.format("%.2f", newV.doubleValue())));

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

                spinSpeed = speedSlider.getValue();

                // On ferme la fenêtre après sauvegarde
                close();

            } catch (NumberFormatException ex) {
                // Gère l'erreur éventuelle, on peut ignorer ou afficher un message
            }
        });

        // Style Material sur le bouton
        Theme.styleButton(btnSave);

        form.add(lblTickets, 0, 0);
        form.add(txtTickets, 1, 0);
        form.add(lblDuration, 0, 1);
        form.add(txtDuration, 1, 1);
        form.add(lblSpeed, 0, 2);
        form.add(speedSlider, 1, 2);
        form.add(lblSpeedVal, 2, 2);

        VBox root = new VBox(15, form, btnSave);
        root.setAlignment(Pos.CENTER_RIGHT);

        Scene scene = new Scene(root, 380, 220);
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

    // Méthode statique pour récupérer la vitesse initiale
    public static double getSpinSpeed() {
        return spinSpeed;
    }
}
