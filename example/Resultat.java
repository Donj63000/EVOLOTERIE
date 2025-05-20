package org.example;

import javafx.animation.*;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.util.Duration;

/** Capsule flashy (thème rouge) qui affiche le résultat de la loterie. */
public class Resultat {

    /* ==================== attributs ==================== */
    private final StackPane root  = new StackPane();
    private final Text      icon  = new Text("🎲");
    private final Text      label = new Text("Résultat : ?");
    private       String    lastMessage = "?";

    /** Maintien l'animation en vie (évite la collecte GC). */
    private Timeline gradientLoop;

    /* ==================== constructeur ==================== */
    public Resultat() {
        // typographies & couleurs
        icon .setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 26));
        label.setFont(Font.font("Montserrat",   FontWeight.BOLD, 24));
        icon .setFill(Color.WHITE);
        label.setFill(Color.WHITE);

        // conteneur interne
        HBox content = new HBox(12, icon, label);
        content.setAlignment(Pos.CENTER_LEFT);

        root.getChildren().add(content);
        root.setPadding(new Insets(14, 34, 14, 34));
        root.setMaxWidth(Region.USE_PREF_SIZE);

        // ombre rouge foncé (halo discret)
        root.setEffect(new DropShadow(16, Color.rgb(120, 0, 0, 0.60)));

        // dégradé animé rouge → orange
        startAnimatedGradient();
    }

    /* ====================== API publique ====================== */
    public Pane   getNode()        { return root; }
    public String getLastMessage() { return lastMessage; }

    public void setMessage(String msg) {
        lastMessage = msg;
        label.setText("Résultat : " + msg);

        // icône & teinte dynamiques
        boolean win  = msg.toLowerCase().contains("gagn");
        boolean lose = msg.toLowerCase().contains("perdu");

        icon .setText(win ? "🏆" : lose ? "💔" : "🎲");
        label.setFill(win ? Color.web("#ffeaea")    // rose très pâle
                : lose ? Color.web("#ffd6d6")
                : Color.WHITE);

        // petit rebond d'apparition
        root.setScaleX(.88); root.setScaleY(.88);
        ScaleTransition pop = new ScaleTransition(Duration.millis(260), root);
        pop.setToX(1); pop.setToY(1); pop.play();
    }

    /* ================== implémentation interne ================== */
    /** Anime le dégradé horizontalement (boucle aller‑retour). */
    private void startAnimatedGradient() {
        DoubleProperty offset = new SimpleDoubleProperty(0);
        offset.addListener((obs, oldVal, newVal) -> root.setBackground(new Background(
                new BackgroundFill(makeGradient(newVal.doubleValue()), new CornerRadii(22), Insets.EMPTY))));

        // première application
        root.setBackground(new Background(
                new BackgroundFill(makeGradient(0), new CornerRadii(22), Insets.EMPTY)));

        gradientLoop = new Timeline(
                new KeyFrame(Duration.ZERO,      new KeyValue(offset, 0)),
                new KeyFrame(Duration.seconds(6), new KeyValue(offset, 1))
        );
        gradientLoop.setCycleCount(Animation.INDEFINITE);
        gradientLoop.setAutoReverse(true);
        gradientLoop.play();
    }

    /** Construit le LinearGradient rouge→orange. */
    private LinearGradient makeGradient(double offset) {
        return new LinearGradient(offset, 0, 1 + offset, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#ff4d4d")),  // rouge vif
                new Stop(1, Color.web("#ffae42"))); // orange doux
    }
}