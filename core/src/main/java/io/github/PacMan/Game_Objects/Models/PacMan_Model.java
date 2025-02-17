package io.github.PacMan.Game_Objects.Models;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.PacMan.GameProperties;
import io.github.PacMan.PacMan_Helper;

import java.awt.*;

/**
 * Model reprezentujący postać Pac-Mana w grze.
 * Klasa implementuje interfejs {@link GameObject_Model} i definiuje
 * właściwości takie jak pozycja, prędkość, hitbox, animacja oraz stany (ruch, śmierć).
 */
public class PacMan_Model implements GameObject_Model {

    // ---- PODSTAWOWE WŁAŚCIWOŚCI OBIEKTU ----

    /** Pozycja w gridzie (logiczna pozycja Pac-Mana). */
    private Point position;

    /** Pozycja wyświetlania sprite'a (do renderowania). */
    private Vector2 spritePosition;

    /** Wektor prędkości ruchu Pac-Mana. */
    private Vector2 velocity;

    /** Prostokąt określający granice obiektu (do renderowania). */
    private Rectangle bounds;

    /** Prostokąt określający hitbox (do wykrywania kolizji). */
    private Rectangle hitBox;

    // ---- STANY I KIERUNEK ----

    /** Ostatni kierunek ruchu (0: lewo, 1: prawo, 2: góra, 3: dół). */
    private int lastDirection;

    /** Flaga określająca, czy Pac-Man aktualnie się porusza. */
    private boolean isMoving;

    /** Flaga określająca, czy Pac-Man jest w trakcie umierania. */
    private boolean isDying;

    // ---- POLA ANIMACJI ----

    /** Aktualny numer klatki animacji Pac-Mana. */
    private int currentFrame;

    // ---- KONSTRUKTORY ----

    /**
     * Konstruktor inicjujący Pac-Mana z domyślną pozycją gridową (0,0).
     *
     * @param startX Początkowa pozycja X w pikselach.
     * @param startY Początkowa pozycja Y w pikselach.
     */
    public PacMan_Model(float startX, float startY) {
        position = new Point(0, 0);
        initialization(startX, startY);
    }

    /**
     * Konstruktor inicjujący Pac-Mana z określoną pozycją gridową.
     *
     * @param startX Początkowa pozycja X w pikselach.
     * @param startY Początkowa pozycja Y w pikselach.
     * @param gridX  Pozycja w gridzie na osi X.
     * @param gridY  Pozycja w gridzie na osi Y.
     */
    public PacMan_Model(float startX, float startY, int gridX, int gridY) {
        position = new Point(gridX, gridY);
        initialization(startX, startY);
    }

    // ---- METODY INICJALIZACYJNE ----

    /**
     * Inicjalizuje podstawowe właściwości Pac-Mana, takie jak prędkość, granice, hitbox oraz pozycję sprite'a.
     *
     * @param startX Początkowa pozycja X w pikselach.
     * @param startY Początkowa pozycja Y w pikselach.
     */
    private void initialization(float startX, float startY) {
        velocity = new Vector2(0, 0);
        bounds = new Rectangle(
            startX,
            startY,
            GameProperties.GameGridProps.CELL_SIZE,
            GameProperties.GameGridProps.CELL_SIZE
        );
        // Ustalanie pozycji hitboxa na podstawie przelicznika
        var pos = PacMan_Helper.positioner(GameProperties.GameSpriteSizes.DOT_SIZE, bounds);
        hitBox = new Rectangle(
            pos.x,
            pos.y,
            GameProperties.GameSpriteSizes.DOT_SIZE,
            GameProperties.GameSpriteSizes.DOT_SIZE
        );
        spritePosition = PacMan_Helper.positioner(GameProperties.GameSpriteSizes.PACMAN_SIZE, bounds);

        lastDirection = 1;  // Domyślnie skierowany w prawo
        isMoving = false;   // Na początku Pac-Man stoi w miejscu
        isDying = false;

        // Inicjalizacja klatki animacji
        currentFrame = 0;
    }

    // ---- IMPLEMENTACJA INTERFEJSU GameObject_Model ----

    /**
     * {@inheritDoc}
     */
    @Override
    public Point getGridPosition() {
        return position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGridPosition(int newX, int newY) {
        position = new Point(newX, newY);
    }

    /**
     * Ustawia nową pozycję wyświetlania Pac-Mana oraz aktualizuje hitbox i pozycję sprite'a.
     *
     * @param newX Nowa pozycja X w pikselach.
     * @param newY Nowa pozycja Y w pikselach.
     */
    public void setDisplayPosition(float newX, float newY) {
        bounds.setPosition(newX, newY);
        hitBox.setPosition(PacMan_Helper.positioner(GameProperties.GameSpriteSizes.DOT_SIZE, bounds));
        spritePosition = PacMan_Helper.positioner(GameProperties.GameSpriteSizes.PACMAN_SIZE, bounds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle getHitBox() {
        return hitBox;
    }

    /**
     * Aktualizuje stan Pac-Mana. Na podstawie wektora prędkości obliczana jest nowa pozycja,
     * a także aktualizowany jest ostatni kierunek ruchu, jeżeli Pac-Man się porusza.
     *
     * @param delta Czas, który upłynął od ostatniej aktualizacji (w sekundach).
     */
    @Override
    public void update(float delta) {
        // Aktualizacja pozycji na podstawie wektora prędkości
        float newX = bounds.x + velocity.x * delta;
        float newY = bounds.y + velocity.y * delta;
        setDisplayPosition(newX, newY);

        // Aktualizacja ostatniego kierunku tylko gdy Pac-Man się porusza
        if (!velocity.isZero()) {
            lastDirection = getDirection();
        }
    }

    // ---- METODY DOTYCZĄCE RENDEROWANIA I KIERUNKU ----

    /**
     * Zwraca pozycję sprite'a używaną do renderowania.
     *
     * @return Pozycja sprite'a jako obiekt {@link Vector2}.
     */
    public Vector2 getSpritePosition() {
        return spritePosition;
    }

    /**
     * Zwraca ostatni ustalony kierunek ruchu Pac-Mana.
     *
     * @return Kierunek (0: lewo, 1: prawo, 2: góra, 3: dół).
     */
    public int getLastDirection() {
        return lastDirection;
    }

    /**
     * Określa kierunek ruchu na podstawie wektora prędkości.
     *
     * @return Kierunek (0: lewo, 1: prawo, 2: góra, 3: dół). Jeśli brak ruchu, zwraca ostatni kierunek.
     */
    private int getDirection() {
        if (velocity.x < 0) return 0; // Lewo
        if (velocity.x > 0) return 1; // Prawo
        if (velocity.y > 0) return 2; // Góra
        if (velocity.y < 0) return 3; // Dół
        return lastDirection;
    }

    /**
     * Ustawia kierunek ruchu Pac-Mana.
     * Wektor kierunku jest normalizowany i skalowany zgodnie z prędkością zdefiniowaną w GameProperties.
     *
     * @param dirX Składowa X kierunku.
     * @param dirY Składowa Y kierunku.
     */
    public void setDirection(float dirX, float dirY) {
        Vector2 dir = new Vector2(dirX, dirY);
        if (dir.len() > 0) {
            dir.nor().scl(GameProperties.GameAttributes.PAC_MAN_SPEED);
            isMoving = true; // Pac-Man zaczyna się poruszać
        } else {
            isMoving = false; // Pac-Man przestaje się poruszać
        }
        this.velocity.set(dir);
    }

    /**
     * Sprawdza, czy Pac-Man się porusza.
     *
     * @return {@code true} jeśli Pac-Man jest w ruchu, inaczej {@code false}.
     */
    public boolean isMoving() {
        return isMoving;
    }

    // ---- METODY DOTYCZĄCE ANIMACJI ----

    /**
     * Zwraca aktualny numer klatki animacji.
     *
     * @return Numer klatki animacji.
     */
    public int getCurrentFrame() {
        return currentFrame;
    }

    /**
     * Ustawia numer aktualnej klatki animacji.
     *
     * @param currentFrame Nowa wartość numeru klatki.
     */
    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    // ---- METODY DOTYCZĄCE STANU ŻYCIA ----

    /**
     * Sprawdza, czy Pac-Man jest w trakcie umierania.
     *
     * @return {@code true} jeśli Pac-Man jest w stanie umierania, inaczej {@code false}.
     */
    public boolean isDying() {
        return isDying;
    }

    /**
     * Ustawia stan umierania Pac-Mana.
     *
     * @param dying {@code true} jeśli Pac-Man ma być w stanie umierania, inaczej {@code false}.
     */
    public void setDying(boolean dying) {
        this.isDying = dying;
    }
}
