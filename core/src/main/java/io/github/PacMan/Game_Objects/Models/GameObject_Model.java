package io.github.PacMan.Game_Objects.Models;

import com.badlogic.gdx.math.Rectangle;

import java.awt.*;

/**
 * Interfejs definiujący model obiektów w grze Pac-Man.
 * Reprezentuje podstawowe właściwości i zachowania wspólne dla wszystkich obiektów gry.
 */
public interface GameObject_Model {

    /**
     * Pobiera aktualną pozycję obiektu w przestrzeni gry.
     *
     * @return Wektor 2D reprezentujący pozycję obiektu w układzie współrzędnych.
     */
    Point getGridPosition();

    /**
     * Ustawia nową pozycję obiektu w przestrzeni gry.
     *
     * @param newX Współrzędna X nowej pozycji obiektu.
     * @param newY Współrzędna Y nowej pozycji obiektu.
     */
    void setGridPosition(int newX, int newY);

    /**
     * Pobiera prostokąt opisujący granice obiektu w przestrzeni gry.
     * Granice mogą być używane do renderowania, sprawdzania kolizji
     * lub innych operacji przestrzennych.
     *
     * @return Obiekt Rectangle definiujący granice obiektu.
     */
    Rectangle getBounds();

    /**
     * Pobiera prostokąt reprezentujący hitbox obiektu w grze.
     * Hitbox może być różny od granic (`getBounds()`) i służyć
     * do sprawdzania kolizji.
     *
     * @return Obiekt Rectangle definiujący hitbox obiektu.
     */
    Rectangle getHitBox();

    /**
     * Aktualizuje stan obiektu gry. Metoda ta jest wywoływana
     * w każdej klatce gry i powinna zawierać logikę odpowiedzialną
     * za zachowanie obiektu, np. ruch, animacje czy interakcje.
     */
    void update(float v);
}
