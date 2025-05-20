package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Fenêtre affichant l'historique des tirages.
 * Chaque tirage est ajouté sous forme de ligne descriptive.
 */
public class Historique extends Stage {

    private final ObservableList<String> lignes = FXCollections.observableArrayList();
    private final ListView<String> listView;
    private final Gains gains;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Historique(Gains gains) {
        this.gains = gains;
        setTitle("Historique des tirages");

        listView = new ListView<>(lignes);
        Theme.styleListView(listView);

        Button btnSuppr = new Button("Supprimer");
        Theme.styleButton(btnSuppr);
        btnSuppr.setOnAction(e -> {
            int idx = listView.getSelectionModel().getSelectedIndex();
            if (idx >= 0) {
                lignes.remove(idx);
            }
        });

        VBox root = new VBox(10, listView, btnSuppr);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root, 400, 300);
        setScene(scene);
    }

    /** Ajoute une ligne dans l'historique pour le tirage indiqué. */
    public void logResult(String pseudo) {
        StringBuilder sb = new StringBuilder();
        sb.append(LocalDateTime.now().format(FORMATTER)).append(" - ");
        if (pseudo != null) {
            sb.append("Vainqueur : ").append(pseudo).append(" - Gains : ")
              .append(gains.getTotalKamas()).append(" k");
            if (!gains.getObjets().isEmpty()) {
                sb.append(" + ").append(String.join(", ", gains.getObjets()));
            }
        } else {
            sb.append("Perdu");
        }
        lignes.add(sb.toString());
    }
}
