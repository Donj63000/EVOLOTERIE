package org.example;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Gains {

    // Données principales
    private final ObservableList<Participant> participants;
    private final ObservableList<String> objets;
    private final SimpleIntegerProperty extraKamas;

    // UI
    private final TextField txtExtra;
    private final Label lblTotal;
    private final ListView<String> listView;

    private final VBox root;

    /** Constructeur */
    public Gains(ObservableList<Participant> participants) {
        this.participants = participants;
        this.objets       = FXCollections.observableArrayList();
        this.extraKamas   = new SimpleIntegerProperty(0);

        /* ========== 1) CAGNOTTE ========== */
        txtExtra = new TextField("0");
        txtExtra.setPrefWidth(60);
        txtExtra.setOnAction(e -> parseExtra());
        txtExtra.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) parseExtra();
        });
        Theme.styleTextField(txtExtra);

        lblTotal = new Label();
        Theme.styleCapsuleLabel(lblTotal, "#4facfe", "#00f2fe");

        // ► Binding qui formate la cagnotte avec des espaces tous les 3 chiffres
        lblTotal.textProperty().bind(
                Bindings.createStringBinding(() -> {
                    int total = participants.stream().mapToInt(Participant::getKamas).sum()
                            + extraKamas.get();
                    // Mise en forme : ",d" produit "23,000,000" → on remplace ',' par ' '
                    String formatted = String.format("%,d", total).replace(',', ' ');
                    return "Cagnotte : " + formatted + " 𝚔";
                }, participants, extraKamas)
        );

        // Bouton : ajoute le montant du champ à la cagnote
        Button btnAddKamas = new Button("Ajouter");
        Theme.styleButton(btnAddKamas);
        btnAddKamas.setOnAction(e -> parseExtra());

        // Bouton : supprime le montant complémentaire
        Button btnRemoveKamas = new Button("Supprimer");
        Theme.styleButton(btnRemoveKamas);
        btnRemoveKamas.setOnAction(e -> {
            extraKamas.set(0);
            txtExtra.setText("0");
        });

        /* ========== 2) OBJETS ========== */
        listView = new ListView<>(objets);
        listView.setPrefSize(160, 300);
        Theme.styleListView(listView);

        // Gros, vert
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-size: 18px; -fx-text-fill: green;");
                }
            }
        });

        // Saisie / ajout / suppression d'objets
        TextField txtNew = new TextField();
        txtNew.setPromptText("Nouvel objet…");
        Theme.styleTextField(txtNew);

        Button btnAdd = new Button("Ajouter");
        Theme.styleButton(btnAdd);
        btnAdd.setOnAction(e -> {
            String v = txtNew.getText().trim();
            if (!v.isEmpty()) {
                objets.add(v);
                txtNew.clear();
            }
        });

        Button btnDel = new Button("Supprimer");
        Theme.styleButton(btnDel);
        btnDel.setOnAction(e -> {
            String sel = listView.getSelectionModel().getSelectedItem();
            if (sel != null) {
                objets.remove(sel);
            }
        });

        Label lblObjets = new Label("Objets :");
        Theme.styleCapsuleLabel(lblObjets, "#ff9a9e", "#fad0c4");

        // Mise à jour auto depuis les participants
        participants.addListener((ListChangeListener<Participant>) change -> refreshObjets());
        refreshObjets();

        VBox objetsBox = new VBox(6, lblObjets, listView, txtNew, btnAdd, btnDel);
        objetsBox.setPadding(new Insets(8, 0, 0, 0));

        VBox vbKamas = new VBox(6,
                txtExtra,
                new HBox(10, btnAddKamas, btnRemoveKamas)
        );

        root = new VBox(10,
                lblTotal,
                vbKamas,
                new Separator(),
                objetsBox
        );
    }

    private void parseExtra() {
        try {
            extraKamas.set(Integer.parseInt(txtExtra.getText().trim()));
        } catch (NumberFormatException ex) {
            txtExtra.setText(String.valueOf(extraKamas.get()));
        }
    }

    private void refreshObjets() {
        objets.setAll(
                participants.stream()
                        .map(Participant::getDonation)
                        .filter(s -> s != null && !s.isBlank() && !s.equals("-"))
                        .toList()
        );
    }

    public Node getRootPane() {
        return root;
    }

    public int getExtraKamas() {
        return extraKamas.get();
    }

    public void setExtraKamas(int value) {
        extraKamas.set(value);
        txtExtra.setText(String.valueOf(value));
    }

    public int getTotalKamas() {
        // On récupère la valeur brute (sans espaces) via la regex
        return Integer.parseInt(lblTotal.getText().replaceAll("[^0-9]", ""));
    }

    public ObservableList<String> getObjets() {
        return objets;
    }
}