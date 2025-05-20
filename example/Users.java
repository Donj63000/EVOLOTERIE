package org.example;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.converter.IntegerStringConverter;

/**
 * Gestion de la liste de participants (ajout, suppression), via un TableView.
 */
public class Users {

    // On stocke désormais des Participant (pas des chaînes de caractères)
    private final ObservableList<Participant> participants;
    private final TableView<Participant> table;

    // Conteneur principal (VBox)
    private final VBox rootPane;

    public Users() {
        // Liste de Participant
        participants = FXCollections.observableArrayList();

        // --- TableView ---
        table = new TableView<>(participants);

        // ↑ On augmente la hauteur pour afficher plus de joueurs
        table.setPrefHeight(600);

        table.setEditable(true); // permet l'édition sur les cellules

        // --- Colonnes : Nom, Kamas, Don ---

        // Colonne Nom
        TableColumn<Participant, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(
                p -> new SimpleStringProperty(p.getValue().getName())
        );

        // Colonne Kamas (éditable, en orange + gras)
        TableColumn<Participant, Integer> colKamas = new TableColumn<>("Kamas");
        colKamas.setCellValueFactory(
                p -> new SimpleIntegerProperty(p.getValue().getKamas()).asObject()
        );
        // --- NOUVELLE cell factory ---
        colKamas.setCellFactory(col ->
                new TextFieldTableCell<Participant, Integer>(new IntegerStringConverter()) {
                    @Override
                    public void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item.toString());
                            setStyle("-fx-text-fill:#FFA500; -fx-font-weight:bold;");
                        }
                    }
                }
        );
        colKamas.setOnEditCommit(ev -> {
            ev.getRowValue().setKamas(ev.getNewValue());
        });

        // Colonne Don (éditable, en vert)
        TableColumn<Participant, String> colDon = new TableColumn<>("Don");
        colDon.setCellValueFactory(
                p -> new SimpleStringProperty(p.getValue().getDonation())
        );
        // --- NOUVELLE cell factory ---
        colDon.setCellFactory(col ->
                new TextFieldTableCell<Participant, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            setStyle("-fx-text-fill:#2ECC71;");
                        }
                    }
                }
        );
        colDon.setOnEditCommit(ev -> {
            ev.getRowValue().setDonation(ev.getNewValue());
        });

        // Ajout des colonnes au TableView
        table.getColumns().addAll(colNom, colKamas, colDon);

        // Thème sombre/bleu
        Theme.styleTableView(table);

        // Cellule personnalisée pour la colonne Nom : police + couleur
        colNom.setCellFactory(column -> new TableCell<Participant, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setFont(Font.font("Arial", FontWeight.BOLD, 15));

                    // Couleur adaptée selon le pseudo
                    javafx.scene.paint.Color c = Roue.getColorForName(item).brighter();
                    setStyle("-fx-text-fill: " + Theme.toWebColor(c) + ";");
                }
            }
        });

        // --- Champs de saisie ---
        TextField txtName  = new TextField();
        txtName.setPromptText("Pseudo");
        Theme.styleTextField(txtName);

        TextField txtKamas = new TextField();
        txtKamas.setPromptText("Kamas");
        Theme.styleTextField(txtKamas);

        TextField txtDon   = new TextField();
        txtDon.setPromptText("Don / objet");
        Theme.styleTextField(txtDon);

        // --- Bouton Ajouter ---
        Button addBtn = new Button("Ajouter");
        Theme.styleButton(addBtn);
        addBtn.setOnAction(e -> {
            String n = txtName.getText().trim();
            if (!n.isEmpty()) {
                int k = txtKamas.getText().isBlank() ? 0 : Integer.parseInt(txtKamas.getText());
                String d = txtDon.getText().trim();

                participants.add(new Participant(n, k, d));
                txtName.clear();
                txtKamas.clear();
                txtDon.clear();
            }
        });

        // --- Bouton Supprimer ---
        Button removeBtn = new Button("Supprimer");
        Theme.styleButton(removeBtn);
        removeBtn.setOnAction(e -> {
            Participant sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                participants.remove(sel);
            }
        });

        // --- Étiquette stylisée "Participants" ---
        Label lblParticipants = new Label("Participants :");
        Theme.styleCapsuleLabel(lblParticipants, "#4facfe", "#00f2fe");

        // --- Layout principal (VBox) ---
        rootPane = new VBox(10);
        rootPane.getChildren().addAll(
                lblParticipants,
                table,
                txtName,
                txtKamas,
                txtDon,
                addBtn,
                removeBtn
        );
    }

    /**
     * Permet à la roue de connaître les noms des participants
     * sous forme de chaînes (pour l'affichage).
     */
    public ObservableList<String> getParticipantNames() {
        return FXCollections.observableArrayList(
                participants.stream()
                        .map(Participant::getName)
                        .toList()
        );
    }

    /** Retourne la liste interne de Participant. */
    public ObservableList<Participant> getParticipants() {
        return participants;
    }

    /** Retourne le conteneur JavaFX pour l'intégrer dans la scène. */
    public Node getRootPane() {
        return rootPane;
    }
}
