package org.example;

/**
 * Simple classe de données décrivant un participant :
 * - Pseudo (name)
 * - Mise en kamas
 * - Donation (objet éventuel)
 */
public class Participant {
    private final String name;
    private int kamas;         // Mise en kamas
    private String donation;   // Texte libre : "Cape Obsi", "-", etc.

    public Participant(String name, int kamas, String donation) {
        this.name = name;
        this.kamas = kamas;
        this.donation = donation;
    }

    /* ============== Getters et Setters ============== */
    public String getName() {
        return name;
    }

    public int getKamas() {
        return kamas;
    }

    public void setKamas(int kamas) {
        this.kamas = kamas;
    }

    public String getDonation() {
        return donation;
    }

    public void setDonation(String donation) {
        this.donation = donation;
    }
}
