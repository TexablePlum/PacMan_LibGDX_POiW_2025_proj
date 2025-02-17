package io.github.PacMan.Game_Objects.Models;

import com.badlogic.gdx.math.Vector2;
import io.github.PacMan.GameProperties;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Model reprezentujący planszę gry.
 * Klasa ta przechowuje obiekty znajdujące się w gridzie oraz
 * odpowiada za przeliczanie pozycji poszczególnych pól planszy.
 */
public class Grid_Model {

    // ---- WŁAŚCIWOŚCI GRIDU ----

    /** Pozycja planszy w przestrzeni gry (offset względem początku ekranu). */
    private final Vector2 position;

    /** Liczba wierszy w gridzie. */
    private final int rows;

    /** Liczba kolumn w gridzie. */
    private final int cols;

    /** Dwuwymiarowa tablica obiektów gry przypisanych do pól gridu. */
    private final GameObject_Model[][] grid;

    /** Mapa indeksów gridu (klucz: pozycja w gridzie, wartość: pozycja wyświetlania w pikselach). */
    private final Map<Point, Vector2> indexPossitions;

    // ---- KONSTRUKTOR ----

    /**
     * Konstruktor tworzący grid na podstawie właściwości z GameProperties.
     * Inicjalizuje pozycję, rozmiary gridu, strukturę przechowywania obiektów oraz
     * oblicza pozycje poszczególnych pól gridu.
     */
    public Grid_Model() {
        position = new Vector2(GameProperties.GameGridProps.POSITION_X, GameProperties.GameGridProps.POSITION_Y);
        rows = GameProperties.GameGridProps.ROWS_COUNT;
        cols = GameProperties.GameGridProps.COLS_COUNT;
        grid = new GameObject_Model[cols][rows];
        indexPossitions = new HashMap<>();
        setIndexPositions();
    }

    // ---- METODY DOSTĘPU DO OBIEKTÓW W GRIDZIE ----

    /**
     * Ustawia obiekt gry w określonym polu gridu.
     *
     * @param col    Numer kolumny.
     * @param row    Numer wiersza.
     * @param object Obiekt gry do umieszczenia w gridzie.
     */
    public void setObjectAt(int col, int row, GameObject_Model object) {
        grid[col][row] = object;
    }

    /**
     * Pobiera obiekt gry znajdujący się w określonym polu gridu.
     *
     * @param col Numer kolumny.
     * @param row Numer wiersza.
     * @return Obiekt gry umieszczony w danym polu lub {@code null} jeśli pole jest puste.
     */
    public GameObject_Model getObjectAt(int col, int row) {
        return grid[col][row];
    }

    /**
     * Zwraca całą dwuwymiarową tablicę obiektów gry.
     *
     * @return Tablica obiektów gry.
     */
    public GameObject_Model[][] getGrid() {
        return grid;
    }

    // ---- GETTERY ROZMIARÓW I POZYCJI GRIDU ----

    /**
     * Zwraca liczbę wierszy w gridzie.
     *
     * @return Liczba wierszy.
     */
    public int getRows() {
        return rows;
    }

    /**
     * Zwraca liczbę kolumn w gridzie.
     *
     * @return Liczba kolumn.
     */
    public int getCols() {
        return cols;
    }

    /**
     * Zwraca pozycję planszy w przestrzeni gry.
     *
     * @return Pozycja planszy jako obiekt {@link Vector2}.
     */
    public Vector2 getPosition() {
        return position;
    }

    // ---- METODY POMOCNICZE ----

    /**
     * Oblicza i ustawia pozycje wyświetlania dla każdego pola gridu.
     * Każde pole posiada indeks (x, y) oraz odpowiadającą mu pozycję w pikselach,
     * która jest obliczana jako:
     * <pre>
     * positionX = x * CELL_SIZE + offsetX;
     * positionY = y * CELL_SIZE + offsetY;
     * </pre>
     */
    private void setIndexPositions() {
        float positionX;
        float positionY;

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                positionX = x * GameProperties.GameGridProps.CELL_SIZE + position.x;
                positionY = y * GameProperties.GameGridProps.CELL_SIZE + position.y;
                indexPossitions.put(new Point(x, y), new Vector2(positionX, positionY));
            }
        }
    }

    /**
     * Zwraca mapę pozycji pól gridu.
     * Kluczami mapy są obiekty {@link Point} reprezentujące indeksy gridu,
     * a wartościami - obiekty {@link Vector2} określające pozycje wyświetlania.
     *
     * @return Mapa pozycji pól gridu.
     */
    public Map<Point, Vector2> getTilesPositions() {
        return indexPossitions;
    }
}
