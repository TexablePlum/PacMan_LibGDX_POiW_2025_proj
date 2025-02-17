package io.github.PacMan.Stage_Interpreter;

import io.github.PacMan.GameProperties;
import io.github.PacMan.Game_Objects.Models.Barrier.Barrier_Type;
import java.awt.*;
import java.util.Map;

/**
 * Klasa Neighbours odpowiada za analizę sąsiedztwa bariery w mapie etapu.
 *
 * <p>Dla danego punktu (reprezentującego pozycję bariery) klasa ustala, czy w sąsiednich
 * pozycjach (lewo, prawo, góra, dół oraz przekątne) znajdują się inne bariery oraz jaki jest ich typ.
 * Wynikiem są flagi określające obecność bariery w danym kierunku oraz odpowiadające im typy
 * (Barrier_Type). Dodatkowo, dla krawędzi mapy (np. pierwsza kolumna, ostatnia kolumna, pierwszy wiersz,
 * ostatni wiersz) automatycznie przyjmuje się, że sąsiadujące pola należą do bariery typu BORDER.
 * </p>
 *
 * <p>Klasa wykorzystuje stałe wektory przesunięć, aby określić pozycje sąsiednich komórek względem
 * danego punktu. Metoda konstruktora przyjmuje punkt oraz mapę barier (klucz: Point, wartość: BarrierPoint)
 * i ustala wartości flag i typów dla wszystkich ośmiu kierunków.</p>
 */
public class Neighbours {

    // Flagowe informacje o obecności bariery w poszczególnych kierunkach.
    private boolean bottom;
    private boolean top;
    private boolean left;
    private boolean right;
    private boolean leftBottom;
    private boolean rightBottom;
    private boolean leftTop;
    private boolean rightTop;

    // Typy barier sąsiadujących z danym punktem.
    private Barrier_Type bottomType;
    private Barrier_Type topType;
    private Barrier_Type leftType;
    private Barrier_Type rightType;
    private Barrier_Type leftBottomType;
    private Barrier_Type rightBottomType;
    private Barrier_Type leftTopType;
    private Barrier_Type rightTopType;

    // Stałe przesunięcia reprezentujące pozycje sąsiadów względem danego punktu.
    private static final Point LEFT_POINT       = new Point(-1, 0);
    private static final Point RIGHT_POINT      = new Point(1, 0);
    private static final Point TOP_POINT        = new Point(0, 1);
    private static final Point BOTTOM_POINT     = new Point(0, -1);
    private static final Point LEFT_BOTTOM_POINT  = new Point(-1, -1);
    private static final Point RIGHT_BOTTOM_POINT = new Point(1, -1);
    private static final Point LEFT_TOP_POINT     = new Point(-1, 1);
    private static final Point RIGHT_TOP_POINT    = new Point(1, 1);

    /**
     * Konstruktor klasy Neighbours.
     *
     * <p>Dla danego punktu {@code p} oraz mapy barier (gdzie kluczem jest pozycja typu {@link Point},
     * a wartością obiekt {@link BarrierPoint}) metoda ustala, które z sąsiadujących pól zawierają bariery
     * oraz zapisuje ich typy. Dodatkowo, jeśli punkt {@code p} znajduje się na krawędzi mapy, metoda
     * automatycznie ustawia sąsiedztwo dla tych kierunków jako BARIERĘ typu BORDER.</p>
     *
     * @param p         Punkt, dla którego określane jest sąsiedztwo.
     * @param barriers  Mapa barier, gdzie kluczem jest pozycja (Point), a wartością obiekt BarrierPoint.
     */
    public Neighbours(Point p, Map<Point, BarrierPoint> barriers) {
        // Sprawdzenie sąsiada po lewej stronie.
        var leftNeighbor = new Point(p.x + LEFT_POINT.x, p.y + LEFT_POINT.y);
        if (barriers.containsKey(leftNeighbor)) {
            this.left = true;
            this.leftType = barriers.get(leftNeighbor).barrier_type();
        }

        // Sprawdzenie sąsiada po prawej stronie.
        var rightNeighbor = new Point(p.x + RIGHT_POINT.x, p.y + RIGHT_POINT.y);
        if (barriers.containsKey(rightNeighbor)) {
            this.right = true;
            this.rightType = barriers.get(rightNeighbor).barrier_type();
        }

        // Sprawdzenie sąsiada u góry.
        var topNeighbor = new Point(p.x + TOP_POINT.x, p.y + TOP_POINT.y);
        if (barriers.containsKey(topNeighbor)) {
            this.top = true;
            this.topType = barriers.get(topNeighbor).barrier_type();
        }

        // Sprawdzenie sąsiada u dołu.
        var bottomNeighbor = new Point(p.x + BOTTOM_POINT.x, p.y + BOTTOM_POINT.y);
        if (barriers.containsKey(bottomNeighbor)) {
            this.bottom = true;
            this.bottomType = barriers.get(bottomNeighbor).barrier_type();
        }

        // Sprawdzenie sąsiada po lewej stronie, dolnej przekątnej.
        var leftBottomNeighbor = new Point(p.x + LEFT_BOTTOM_POINT.x, p.y + LEFT_BOTTOM_POINT.y);
        if (barriers.containsKey(leftBottomNeighbor)) {
            this.leftBottom = true;
            this.leftBottomType = barriers.get(leftBottomNeighbor).barrier_type();
        }

        // Sprawdzenie sąsiada po prawej stronie, dolnej przekątnej.
        var rightBottomNeighbor = new Point(p.x + RIGHT_BOTTOM_POINT.x, p.y + RIGHT_BOTTOM_POINT.y);
        if (barriers.containsKey(rightBottomNeighbor)) {
            this.rightBottom = true;
            this.rightBottomType = barriers.get(rightBottomNeighbor).barrier_type();
        }

        // Sprawdzenie sąsiada po lewej stronie, górnej przekątnej.
        var leftTopNeighbor = new Point(p.x + LEFT_TOP_POINT.x, p.y + LEFT_TOP_POINT.y);
        if (barriers.containsKey(leftTopNeighbor)) {
            this.leftTop = true;
            this.leftTopType = barriers.get(leftTopNeighbor).barrier_type();
        }

        // Sprawdzenie sąsiada po prawej stronie, górnej przekątnej.
        var rightTopNeighbor = new Point(p.x + RIGHT_TOP_POINT.x, p.y + RIGHT_TOP_POINT.y);
        if (barriers.containsKey(rightTopNeighbor)) {
            this.rightTop = true;
            this.rightTopType = barriers.get(rightTopNeighbor).barrier_type();
        }

        // Dodatkowe sprawdzanie krawędzi mapy:
        // Jeśli punkt znajduje się na lewej krawędzi, automatycznie ustawiamy lewy, lewy górny i lewy dolny sąsiad jako BORDER.
        if (p.x == 0) {
            this.left = true;
            this.leftType = Barrier_Type.BORDER;
            this.leftTop = true;
            this.leftTopType = Barrier_Type.BORDER;
            this.leftBottom = true;
            this.leftBottomType = Barrier_Type.BORDER;
        }
        // Jeśli punkt znajduje się na prawej krawędzi, ustawiamy odpowiednie flagi i typy na BORDER.
        if (p.x == GameProperties.GameGridProps.COLS_COUNT - 1) {
            this.right = true;
            this.rightType = Barrier_Type.BORDER;
            this.rightTop = true;
            this.rightTopType = Barrier_Type.BORDER;
            this.rightBottom = true;
            this.rightBottomType = Barrier_Type.BORDER;
        }
        // Jeśli punkt znajduje się na dolnej krawędzi, ustawiamy flagi dla dolnego, lewego dolnego i prawego dolnego sąsiada.
        if (p.y == 0) {
            this.bottom = true;
            this.bottomType = Barrier_Type.BORDER;
            this.leftBottom = true;
            this.leftBottomType = Barrier_Type.BORDER;
            this.rightBottom = true;
            this.rightBottomType = Barrier_Type.BORDER;
        }
        // Jeśli punkt znajduje się na górnej krawędzi, ustawiamy flagi dla górnego, lewego górnego i prawego górnego sąsiada.
        if (p.y == GameProperties.GameGridProps.ROWS_COUNT - 1) {
            this.top = true;
            this.topType = Barrier_Type.BORDER;
            this.leftTop = true;
            this.leftTopType = Barrier_Type.BORDER;
            this.rightTop = true;
            this.rightTopType = Barrier_Type.BORDER;
        }
    }

    // Gettery dla flag informujących o obecności sąsiadów.

    public boolean isBottom() {
        return bottom;
    }

    public boolean isTop() {
        return top;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isLeftBottom() {
        return leftBottom;
    }

    public boolean isRightBottom() {
        return rightBottom;
    }

    public boolean isLeftTop() {
        return leftTop;
    }

    public boolean isRightTop() {
        return rightTop;
    }

    // Gettery dla typów barier sąsiadujących z danym punktem.

    public Barrier_Type getBottomType() {
        return bottomType;
    }

    public Barrier_Type getTopType() {
        return topType;
    }

    public Barrier_Type getLeftType() {
        return leftType;
    }

    public Barrier_Type getRightType() {
        return rightType;
    }
}
