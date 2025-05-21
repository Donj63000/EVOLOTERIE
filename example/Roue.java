package org.example;

import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Classe gérant l'affichage et l'animation de la roue.
 */
public class Roue {

    // ---------------------------------------------------------------------
    // 1) Couleur du filet (contour) des secteurs
    // ---------------------------------------------------------------------
    private static final Color SECTOR_STROKE = Color.web("#888888"); // gris doux
    private static final double SECTOR_STROKE_WIDTH = 1.5;           // un peu plus fin

    // ---------------------------------------------------------------------
    // 2) Taille et style du cache central (“hub”)
    // ---------------------------------------------------------------------
    private static final double HUB_RADIUS = Main.WHEEL_RADIUS * 0.28;
    private static final Paint HUB_FILL = new RadialGradient(
            0, 0,
            0.3, 0.3,
            1.0,
            true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#fff9c4")),  // jaune pâle au centre
            new Stop(1, Color.web("#b8860b"))   // doré soutenu en bord
    );
    private static final Color  HUB_STROKE = Color.web("#d4af37");   // liseré or
    private static final double HUB_STROKE_WIDTH = 3.0;

    // ---------------------------------------------------------------------
    // Cache des couleurs pour pseudos (optionnel)
    // ---------------------------------------------------------------------
    private static final Map<String, Color> COLOR_CACHE = new HashMap<>();

    public static void recomputePlayerColors(List<String> players) {
        COLOR_CACHE.clear();
        int n = players.size();
        if (n == 0) return;

        double step = 360.0 / n;
        double hue  = 0.0;

        for (String p : players) {
            Color col = Color.hsb(hue, 0.75, 0.9);
            COLOR_CACHE.put(p, col);
            hue += step;
        }
    }

    public static Color getColorForName(String name) {
        return COLOR_CACHE.getOrDefault(name, Color.GRAY);
    }

    // ---------------------------------------------------------------------
    // Attributs d'instance
    // ---------------------------------------------------------------------
    private final StackPane rootPane;
    private final Group groupSecteurs;
    private final RotateTransition rotateTransition;
    private final Resultat resultat;
    private final List<Arc> listeArcs;

    private String[] seatArrangement;
    private ParallelTransition winnerFx;
    private Polygon curseur;

    private Consumer<String> resultCallback;

    // Variables pour le drag & drop
    private double dragAnchorX, dragAnchorY;

    // ---------------------------------------------------------------------
    // Constructeur
    // ---------------------------------------------------------------------
    public Roue(Resultat resultat) {
        this.resultat = resultat;

        // Durée initiale (modifiable)
        this.rotateTransition = new RotateTransition(
                Duration.seconds(OptionRoue.getSpinDuration())
        );

        // Pane principal
        this.rootPane = new StackPane();
        rootPane.setAlignment(Pos.CENTER);
        rootPane.setPrefSize(Main.WHEEL_RADIUS * 2, Main.WHEEL_RADIUS * 2);
        rootPane.setEffect(new DropShadow(20, Color.BLACK));

        // Groupe tournant (secteurs + anneaux)
        this.groupSecteurs = new Group();

        // ►►► Activation du cache bitmap pour accélérer la rotation
        groupSecteurs.setCache(true);
        groupSecteurs.setCacheHint(CacheHint.ROTATE);

        rootPane.getChildren().add(groupSecteurs);

        // === Curseur (triangle) ===
        this.curseur = new Polygon(
                0.0,  -(Main.WHEEL_RADIUS + 10),
                -15.0, -(Main.WHEEL_RADIUS - 10),
                15.0,  -(Main.WHEEL_RADIUS - 10)
        );
        // 1) On le met en noir par défaut
        curseur.setFill(Color.BLACK);
        curseur.setStroke(Color.WHITE);
        curseur.setStrokeWidth(1.2);
        // Curseur reste fixe
        rootPane.getChildren().add(curseur);

        // Gloss fixe (reflet)
        Circle gloss = new Circle(Main.WHEEL_RADIUS * 0.9, Color.rgb(255, 255, 255, 0.08));
        gloss.setTranslateY(-Main.WHEEL_RADIUS * 0.25);
        rootPane.getChildren().add(gloss);

        this.listeArcs = new ArrayList<>();

        // Drag & drop sur rootPane
        rootPane.setOnMousePressed(e -> {
            dragAnchorX = e.getSceneX() - rootPane.getTranslateX();
            dragAnchorY = e.getSceneY() - rootPane.getTranslateY();
            rootPane.setCursor(Cursor.CLOSED_HAND);
        });
        rootPane.setOnMouseReleased(e -> rootPane.setCursor(Cursor.OPEN_HAND));
        rootPane.setOnMouseDragged(e -> {
            rootPane.setTranslateX(e.getSceneX() - dragAnchorX);
            rootPane.setTranslateY(e.getSceneY() - dragAnchorY);
        });
        rootPane.setCursor(Cursor.OPEN_HAND);
    }

    public void resetPosition() {
        rootPane.setTranslateX(0);
        rootPane.setTranslateY(0);
    }

    public void setOnSpinFinished(Consumer<String> cb) {
        this.resultCallback = cb;
    }

    // ---------------------------------------------------------------------
    // Mise à jour de l’affichage
    // ---------------------------------------------------------------------
    public void updateWheelDisplay(ObservableList<String> participants) {
        // Recalc couleur
        recomputePlayerColors(participants);

        groupSecteurs.setRotate(0);
        rotateTransition.stop();
        groupSecteurs.getChildren().clear();
        listeArcs.clear();

        // Anneaux décoratifs
        Circle ringOuter = new Circle(Main.WHEEL_RADIUS + 1.5, Color.TRANSPARENT);
        ringOuter.setStroke(Color.GOLD);
        ringOuter.setStrokeWidth(3);

        Circle ringInner = new Circle(Main.WHEEL_RADIUS - 3, Color.TRANSPARENT);
        ringInner.setStroke(Color.DARKGRAY);
        ringInner.setStrokeWidth(2);

        groupSecteurs.getChildren().addAll(ringOuter, ringInner);

        int losingTickets  = OptionRoue.getLosingTickets();
        int nbParticipants = participants.size();
        int total          = nbParticipants + losingTickets;

        if (total == 0) {
            seatArrangement = null;
            return;
        }

        seatArrangement = distributeSeats(participants, losingTickets);

        double angleStep  = 360.0 / total;
        double startAngle = 0.0;

        // 1) Création des secteurs
        for (int i = 0; i < total; i++) {
            boolean isWinning = (seatArrangement[i] != null);
            Color fillColor = (isWinning)
                    ? getColorForName(seatArrangement[i]).brighter()
                    : Color.BLACK;

            Arc arc = createArc(startAngle, angleStep, fillColor);
            listeArcs.add(arc);
            groupSecteurs.getChildren().add(arc);

            startAngle += angleStep;
        }

        // 2) Hub (médaillon) par-dessus
        groupSecteurs.getChildren().add(createHub());
    }

    // ---------------------------------------------------------------------
    // Animation (spin)
    // ---------------------------------------------------------------------
    public void spinTheWheel(ObservableList<String> participants) {
        if (winnerFx != null) {
            winnerFx.stop();
            resetHighlightVisuals();
        }
        rotateTransition.stop();
        rotateTransition.setDuration(Duration.seconds(OptionRoue.getSpinDuration()));

        int nbParticipants = participants.size();
        int losingTickets  = OptionRoue.getLosingTickets();
        int total          = nbParticipants + losingTickets;

        if (total == 0) {
            resultat.setMessage("Aucun ticket – impossible de lancer la roue.");
            return;
        }

        // On détermine un index gagnant au hasard
        int winningIndex = ThreadLocalRandom.current().nextInt(total);

        double angleStep  = 360.0 / total;
        double extraTurns = 3.0 * OptionRoue.getSpinSpeed();  // nombre de tours entiers
        // Calcule l'angle final à atteindre. On souhaite que le centre du
        // secteur gagnant se retrouve exactement sous le curseur situé en haut
        // de la roue (à 270°). Chaque tour complet est ajouté pour l'animation
        // puis on soustrait la position du centre du secteur courant.
        double target     = (360 * extraTurns)
                + 270
                - ((winningIndex + 0.5) * angleStep);

        double start = groupSecteurs.getRotate();
        rotateTransition.setNode(groupSecteurs);
        rotateTransition.setFromAngle(start);
        rotateTransition.setToAngle(start + target);
        rotateTransition.setInterpolator(Interpolator.EASE_OUT);

        rotateTransition.setOnFinished(e -> {
            String pseudo = seatArrangement[winningIndex];
            if (pseudo != null) {
                resultat.setMessage(pseudo + " a gagné !");
            } else {
                resultat.setMessage("Perdu !");
            }
            if (resultCallback != null) {
                resultCallback.accept(pseudo);
            }
            playWinnerHighlight(winningIndex);
        });

        rotateTransition.play();
    }

    // ---------------------------------------------------------------------
    // Surbrillance du secteur gagnant + curseur clignotant
    // ---------------------------------------------------------------------
    private void playWinnerHighlight(int idx) {
        if (idx < 0 || idx >= listeArcs.size()) return;
        Arc arc = listeArcs.get(idx);

        // 1) Contour arc-en-ciel (animation de la couleur du contour)
        Timeline rainbow = new Timeline();
        double step = 0.25; // 0.25 s => 10 s pour 40 itérations
        for (int i = 0; i <= 40; i++) {
            rainbow.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(i * step),
                            new KeyValue(
                                    arc.strokeProperty(),
                                    Color.hsb((i * 30) % 360, 1.0, 1.0)
                            )
                    )
            );
        }
        arc.setStrokeWidth(5);

        // 2) Glow pulsé
        Glow glow = new Glow(0.0);
        arc.setEffect(glow);
        Timeline pulse = new Timeline(
                new KeyFrame(Duration.ZERO,  new KeyValue(glow.levelProperty(), 0.0)),
                new KeyFrame(Duration.seconds(.4), new KeyValue(glow.levelProperty(), .9))
        );
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);

        // 3) Curseur : scale (respire)
        ScaleTransition blinkCursor = new ScaleTransition(Duration.seconds(0.8), curseur);
        blinkCursor.setFromX(1.0);
        blinkCursor.setToX(1.2);
        blinkCursor.setFromY(1.0);
        blinkCursor.setToY(1.2);
        blinkCursor.setAutoReverse(true);
        blinkCursor.setCycleCount(Animation.INDEFINITE);

        // 4) Curseur : fill blink (noir <-> blanc)
        FillTransition blinkFill = new FillTransition(Duration.seconds(0.8), curseur,
                Color.BLACK, Color.WHITE);
        blinkFill.setAutoReverse(true);
        blinkFill.setCycleCount(Animation.INDEFINITE);

        // On regroupe tout en parallèle
        winnerFx = new ParallelTransition(rainbow, pulse, blinkCursor, blinkFill);
        winnerFx.setCycleCount(1);  // on le jouera qu'une fois (10s env. pour rainbow)
        winnerFx.setOnFinished(e -> resetHighlightVisuals());
        winnerFx.play();
    }

    // ---------------------------------------------------------------------
    // Reset (retour apparence normale)
    // ---------------------------------------------------------------------
    private void resetHighlightVisuals() {
        for (Arc a : listeArcs) {
            a.setStroke(SECTOR_STROKE);
            a.setStrokeWidth(SECTOR_STROKE_WIDTH);
            a.setEffect(null);
        }
        // Restaure le curseur : taille 1.0 et couleur noire
        curseur.setScaleX(1.0);
        curseur.setScaleY(1.0);
        curseur.setFill(Color.BLACK);
    }

    // ---------------------------------------------------------------------
    // Méthodes utilitaires
    // ---------------------------------------------------------------------
    private String[] distributeSeats(ObservableList<String> participants, int losingTickets) {
        int P = participants.size();
        int T = P + losingTickets;

        String[] arrangement = new String[T];
        for (int i = 0; i < T; i++) {
            arrangement[i] = null;
        }

        double step  = (double) T / P;
        double accum = 0.0;

        for (int i = 0; i < P; i++) {
            int index = (int) Math.round(accum);
            if (index >= T) index = T - 1;
            // On avance jusqu'à trouver une case libre
            while (arrangement[index] != null) {
                index = (index + 1) % T;
            }
            arrangement[index] = participants.get(i);
            accum += step;
        }
        return arrangement;
    }

    private Arc createArc(double startAngle, double angleStep, Color base) {
        Arc a = new Arc(0, 0,
                Main.WHEEL_RADIUS, Main.WHEEL_RADIUS,
                startAngle, angleStep
        );
        a.setType(ArcType.ROUND);

        a.setFill(
                new RadialGradient(
                        0, 0,
                        0.5, 0.5,
                        0.9,
                        true, CycleMethod.NO_CYCLE,
                        new Stop(0, base.brighter()),
                        new Stop(1, base.darker())
                )
        );
        a.setStroke(SECTOR_STROKE);
        a.setStrokeWidth(SECTOR_STROKE_WIDTH);

        // On conserve l'ombre pour ne pas altérer l'aspect visuel
        a.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.7)));
        return a;
    }

    private Circle createHub() {
        Circle hub = new Circle(HUB_RADIUS);
        hub.setFill(HUB_FILL);
        hub.setStroke(HUB_STROKE);
        hub.setStrokeWidth(HUB_STROKE_WIDTH);
        hub.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.6)));
        return hub;
    }

    public Node getRootPane() {
        return rootPane;
    }
}
