package org.example;

/**
 * Classe lanceur intermédiaire qui démarre l'application JavaFX.
 * <p>
 * Elle ne doit PAS étendre {@code javafx.application.Application} :
 * son unique rôle est de transférer les arguments à {@link Main},
 * classe qui, elle, étend {@code Application} et configure JavaFX.
 */
public class Launcher {

    /**
     * Point d'entrée standard pour un exécutable JAR / fichier natif.
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {
        Main.main(args); // délègue intégralement à votre classe Application
    }
}