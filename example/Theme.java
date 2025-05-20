package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Theme {

    // --- Palette dark-blue ---
    public static final Color DARK_BG        = Color.web("#121212");
    public static final Color DARK_ELEVATION = Color.web("#1e1e1e");
    public static final Color ACCENT         = Color.web("#2196F3"); // Material Blue 500
    public static final Color ACCENT_LIGHT   = Color.web("#42A5F5");
    public static final Color TEXT_DEFAULT   = Color.WHITE;

    // Police par défaut
    public static final Font MAIN_FONT = Font.font("Arial", 14);

    /*--------------------------------------------------------------*/
    /*  BACKGROUND « cover »                                        */
    /*--------------------------------------------------------------*/
    public static Background makeBackgroundCover(String imagePath) {
        Image bgImage = new Image(Theme.class.getResourceAsStream(imagePath));
        BackgroundSize bSize = new BackgroundSize(
                1, 1, true, true,
                false, true   // contain=false, cover=true
        );
        BackgroundImage bImg = new BackgroundImage(
                bgImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, bSize
        );
        return new Background(bImg);
    }

    /*--------------------------------------------------------------*/
    /*  BOUTON « Material »                                          */
    /*--------------------------------------------------------------*/
    public static void styleButton(Button b) {
        String normal =
                "-fx-background-radius: 8;" +
                        "-fx-background-color: linear-gradient(#2196F3 0%, #1e88e5 100%);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;";
        String hover   = "-fx-background-color: linear-gradient(#42a5f5 0%, #2196F3 100%);";
        String pressed = "-fx-background-color: #1565c0;";

        b.setStyle(normal);
        b.setCursor(Cursor.HAND);

        b.setOnMouseEntered(e -> b.setStyle(normal + hover));
        b.setOnMouseExited (e -> b.setStyle(normal));
        b.pressedProperty().addListener((obs, oldV, newV) -> {
            if (newV) b.setStyle(normal + pressed);
            else      b.setStyle(normal);
        });
    }

    /*--------------------------------------------------------------*/
    /*  LISTVIEW sombre + sélection bleue                           */
    /*--------------------------------------------------------------*/
    public static void styleListView(ListView<?> lv) {
        lv.setStyle(
                "-fx-control-inner-background:#1e1e1e;" +
                        "-fx-background-insets:0;" +
                        "-fx-selection-bar:#2196F3;" +
                        "-fx-selection-bar-non-focused:#1565c0;"
        );
    }

    /*--------------------------------------------------------------*/
    /*  TABLEVIEW sombre + sans bordure                             */
    /*--------------------------------------------------------------*/
    public static void styleTableView(TableView<?> tv) {
        styleControl(tv); // teinte sombre de base
        tv.setStyle(
                tv.getStyle() +
                        "-fx-table-cell-border-color: transparent;" +
                        "-fx-table-header-border-color: transparent;" +
                        "-fx-border-color: transparent;"
        );
    }

    /*--------------------------------------------------------------*/
    /*  Contrôle générique : fond sombre                            */
    /*--------------------------------------------------------------*/
    public static void styleControl(Control c) {
        c.setStyle(
                "-fx-control-inner-background:#1e1e1e;" +
                        "-fx-background-insets:0;" +
                        "-fx-selection-bar:#2196F3;" +
                        "-fx-selection-bar-non-focused:#1565c0;"
        );
    }

    /*--------------------------------------------------------------*/
    /*  Color → web #RRGGBB                                         */
    /*--------------------------------------------------------------*/
    public static String toWebColor(Color c) {
        int r = (int) (c.getRed() * 255);
        int g = (int) (c.getGreen() * 255);
        int b = (int) (c.getBlue() * 255);
        return String.format("#%02X%02X%02X", r, g, b);
    }

    /*--------------------------------------------------------------*/
    /*  TEXTFIELD sombre                                            */
    /*--------------------------------------------------------------*/
    public static void styleTextField(TextField tf) {
        String normal =
                "-fx-background-radius:6;" +
                        "-fx-background-color:#1e1e1e;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #bbbbbb;" +
                        "-fx-border-color:#2196F3;" +
                        "-fx-border-radius:6;" +
                        "-fx-border-width:1;";
        String focused = "-fx-border-color:#42A5F5;";

        tf.setStyle(normal);
        tf.focusedProperty().addListener((o, oldV, newV) ->
                tf.setStyle(newV ? normal + focused : normal));
    }

    /*--------------------------------------------------------------*/
    /*  TEXTAREA sombre                                             */
    /*--------------------------------------------------------------*/
    public static void styleTextArea(TextArea ta) {
        String normal =
                "-fx-background-radius:6;" +
                        "-fx-background-color:#1e1e1e;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #bbbbbb;" +
                        "-fx-border-color:#2196F3;" +
                        "-fx-border-radius:6;" +
                        "-fx-border-width:1;";
        String focused = "-fx-border-color:#42A5F5;";

        ta.setStyle(normal);
        ta.focusedProperty().addListener((o, oldV, newV) ->
                ta.setStyle(newV ? normal + focused : normal));
    }

    /*--------------------------------------------------------------*/
    /*  NOUVEAU  :  Capsule / Badge stylé pour Label                */
    /*--------------------------------------------------------------*/
    public static void styleCapsuleLabel(Label label,
                                         String startColor,
                                         String endColor) {
        label.setFont(Font.font("Roboto", FontWeight.BOLD, 18));
        label.setTextFill(Color.WHITE);
        label.setPadding(new Insets(8, 16, 8, 16));
        label.setAlignment(Pos.CENTER);

        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 0,            // horizontal
                true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(startColor)),
                new Stop(1, Color.web(endColor))
        );

        label.setBackground(new Background(
                new BackgroundFill(gradient, new CornerRadii(15), Insets.EMPTY)
        ));
        label.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.35)));
    }
}
