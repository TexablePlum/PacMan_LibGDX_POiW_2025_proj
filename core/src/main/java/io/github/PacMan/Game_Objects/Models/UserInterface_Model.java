package io.github.PacMan.Game_Objects.Models;

import io.github.PacMan.GameProperties;

/**
 * Model interfejsu użytkownika w grze Pac-Man.
 * Klasa zarządza wyświetlanymi wartościami, takimi jak wynik, liczba żyć oraz stan gry (game over).
 */
public class UserInterface_Model {

    // ---- WŁAŚCIWOŚCI INTERFEJSU ----

    /** Aktualna wartość wyniku wyrażona jako łańcuch znaków. */
    private String score_value;

    /** Aktualna liczba żyć gracza. */
    private int lives_value;

    /** Flaga określająca, czy gra zakończyła się stanem "game over". */
    private boolean isGameOver = false;

    /** Tekst wyświetlany w przypadku zakończenia gry. */
    private final String gameOver_text;

    // ---- KONSTRUKTOR ----

    /**
     * Konstruktor inicjujący model interfejsu użytkownika.
     * Ustawia początkowy wynik na "0" oraz przydziela liczbę żyć.
     *
     * @param lives_value Początkowa liczba żyć.
     */
    public UserInterface_Model(int lives_value) {
        score_value = "0";
        this.lives_value = lives_value;
        gameOver_text = "GAME OVER!";
    }

    // ---- METODY DOSTĘPU DO WYNIKU ----

    /**
     * Zwraca aktualną wartość wyniku.
     *
     * @return Wynik jako łańcuch znaków.
     */
    public String getScoreValue() {
        return score_value;
    }

    /**
     * Ustawia nową wartość wyniku.
     *
     * @param scoreValue Nowy wynik.
     */
    public void setScoreValue(int scoreValue) {
        this.score_value = String.valueOf(scoreValue);
    }

    // ---- METODY DOSTĘPU DO WYNIKU WYSOKIEGO ----

    /**
     * Zwraca aktualną wartość wyniku wysokiego.
     * Wartość ta jest pobierana z właściwości gry.
     *
     * @return Wartość wyniku wysokiego jako łańcuch znaków.
     */
    public String getHighScoreValue() {
        return GameProperties.GameAttributes.HIGH_SCORE_VALUE;
    }

    /**
     * Ustawia nową wartość wyniku wysokiego.
     *
     * @param highScoreValue Nowa wartość wyniku wysokiego.
     */
    public void setHighScoreValue(int highScoreValue) {
        GameProperties.GameAttributes.HIGH_SCORE_VALUE = String.valueOf(highScoreValue);
    }

    // ---- METODY DOSTĘPU DO LICZBY ŻYC ----

    /**
     * Zwraca aktualną liczbę żyć gracza.
     *
     * @return Liczba żyć.
     */
    public int getLivesValue() {
        return lives_value;
    }

    /**
     * Ustawia nową liczbę żyć gracza.
     *
     * @param livesValue Nowa liczba żyć.
     */
    public void setLivesValue(int livesValue) {
        this.lives_value = livesValue;
    }

    // ---- METODY DOTYCZĄCE STANU GRY ----

    /**
     * Sprawdza, czy gra zakończyła się stanem "game over".
     *
     * @return {@code true} jeśli gra jest zakończona, w przeciwnym wypadku {@code false}.
     */
    public boolean isGameOver() {
        return isGameOver;
    }

    /**
     * Ustawia stan gry jako zakończony lub nie.
     *
     * @param gameOver {@code true} jeśli gra ma być zakończona, w przeciwnym wypadku {@code false}.
     */
    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    // ---- METODA DO POBIERANIA TEKSTU "GAME OVER" ----

    /**
     * Zwraca tekst wyświetlany w przypadku zakończenia gry.
     *
     * @return Tekst "GAME OVER!".
     */
    public String getGameOverText() {
        return gameOver_text;
    }
}
