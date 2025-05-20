package org.example;

import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.util.Duration;

import java.util.Random;

public class Titre {

    private final StackPane root;
    private final Text title;
    private final Rectangle shimmer;

    // Couleurs (texte) : on fait une transition progressive de old → new
    private Color oldC1, oldC2, oldC3, newC1, newC2, newC3;

    // Couleurs (reflet)
    private Color oldSh, newSh;

    private final Random rand = new Random();

    public Titre() {
        // ====== TEXTE ======
        title = new Text("Grande Loterie de la guilde EVOLUTION");
        title.setFont(Font.font("Poppins", FontWeight.EXTRA_BOLD, 34));
        title.setBoundsType(TextBoundsType.VISUAL);
        title.setCache(true);
        title.setCacheHint(CacheHint.SCALE_AND_ROTATE);

        // Halo néon
        var glow = new DropShadow(14, Color.web("#4facfe"));
        glow.setSpread(0.3);
        title.setEffect(glow);

        // Conteneur (réduit)
        root = new StackPane(title);
        root.setAlignment(Pos.TOP_LEFT);
        // Moins de padding => l'élément en dessous remonte
        root.setPadding(new Insets(2, 0, 1, 20));
        root.setMaxWidth(StackPane.USE_PREF_SIZE);

        // ====== SHIMMER (rectangle mobile) ======
        shimmer = new Rectangle();
        shimmer.widthProperty().bind(Bindings.createDoubleBinding(
                () -> title.getLayoutBounds().getWidth() * 1.2,
                title.layoutBoundsProperty()
        ));
        shimmer.heightProperty().bind(Bindings.createDoubleBinding(
                () -> title.getLayoutBounds().getHeight() * 2,
                title.layoutBoundsProperty()
        ));
        shimmer.setRotate(25);
        root.getChildren().add(shimmer);

        var tt = new TranslateTransition(Duration.seconds(4), shimmer);
        tt.setCycleCount(Animation.INDEFINITE);
        tt.setAutoReverse(true);
        tt.fromXProperty().bind(Bindings.createDoubleBinding(
                () -> -title.getLayoutBounds().getWidth(),
                title.layoutBoundsProperty()));
        tt.toXProperty().bind(Bindings.createDoubleBinding(
                () -> title.getLayoutBounds().getWidth(),
                title.layoutBoundsProperty()));
        tt.play();

        // On lance l’animation continue (texte + shimmer)
        startColorCycle();
    }

    public StackPane getNode() {
        return root;
    }

    // ====================== Animation continue ======================
    private void startColorCycle() {
        // Au début de chaque cycle, old = new (ou random init)
        if (oldC1 == null) {
            oldC1 = randomColor();
            oldC2 = randomColor();
            oldC3 = randomColor();
            oldSh = randomColor();
        } else {
            oldC1 = newC1;
            oldC2 = newC2;
            oldC3 = newC3;
            oldSh = newSh;
        }
        // On pioche un nouveau set
        newC1 = randomColor();
        newC2 = randomColor();
        newC3 = randomColor();
        newSh = randomColor();

        DoubleProperty t = new SimpleDoubleProperty(0);
        t.addListener((o, ov, nv) -> {
            double frac = nv.doubleValue();
            // Interpolation linéaire pour chaque stop
            Color c1 = lerpColor(oldC1, newC1, frac);
            Color c2 = lerpColor(oldC2, newC2, frac);
            Color c3 = lerpColor(oldC3, newC3, frac);
            // On met à jour le dégradé du texte
            title.setFill(makeGradient(c1, c2, c3));

            // Interpolation de la couleur du shimmer
            Color shC = lerpColor(oldSh, newSh, frac);
            shimmer.setFill(new LinearGradient(0,0,1,0,true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.TRANSPARENT),
                    new Stop(.5, shC.deriveColor(0,1,1,0.4)), // alpha .4
                    new Stop(1, Color.TRANSPARENT)));
        });

        // On anime t de 0 → 1 sur 8 s
        Timeline anim = new Timeline(
                new KeyFrame(Duration.ZERO,  new KeyValue(t, 0)),
                new KeyFrame(Duration.seconds(8), new KeyValue(t, 1))
        );
        // À la fin, on relance un nouveau cycle
        anim.setOnFinished(e -> startColorCycle());
        anim.play();
    }

    // ====================== Méthodes d’aide ======================
    private Color randomColor() {
        return Color.hsb(rand.nextDouble()*360, 0.9, 1.0);
    }
    private Color lerpColor(Color a, Color b, double f) {
        return new Color(
                a.getRed()   + (b.getRed()   - a.getRed())   * f,
                a.getGreen() + (b.getGreen() - a.getGreen()) * f,
                a.getBlue()  + (b.getBlue()  - a.getBlue())  * f,
                a.getOpacity()+ (b.getOpacity()-a.getOpacity())* f
        );
    }
    private Paint makeGradient(Color c1, Color c2, Color c3) {
        return new LinearGradient(0,0,1,0,true, CycleMethod.NO_CYCLE,
                new Stop(0,c1),
                new Stop(.5,c2),
                new Stop(1,c3));
    }
}
