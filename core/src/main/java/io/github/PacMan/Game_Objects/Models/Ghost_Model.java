package io.github.PacMan.Game_Objects.Models;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.PacMan.GameProperties;
import io.github.PacMan.PacMan_Helper;

import java.awt.*;

/**
 * Model reprezentujący ducha w grze Pac-Man.
 * Klasa implementuje interfejs {@link GameObject_Model} oraz definiuje
 * właściwości, takie jak pozycja, prędkość, animacja oraz stany (frightened, eaten, miganie).
 */
public class Ghost_Model implements GameObject_Model {

    // ---- PODSTAWOWE WŁAŚCIWOŚCI OBIEKTU ----

    /** Pozycja w gridzie (logiczna pozycja duszka). */
    private Point position;

    /** Pozycja służąca do renderowania sprite'a (odpowiadająca bounds). */
    private Vector2 spritePosition;

    /** Typ duszka, określający wygląd i zachowanie. */
    private final Ghost_Type type;

    /** Prędkość ruchu ducha. */
    private Vector2 velocity;

    /** Prostokąt określający granice obiektu, używany przy renderowaniu. */
    private Rectangle bounds;

    /** Prostokąt określający hitbox obiektu, używany przy sprawdzaniu kolizji. */
    private Rectangle hitBox;

    /** Ostatni kierunek ruchu (wartość -1 oznacza, że kierunek nie został jeszcze ustalony). */
    private int lastDirection = -1;

    /** Flaga określająca, czy duch aktualnie się porusza. */
    private boolean isMoving;

    // ---- POLA ANIMACJI ----

    /** Timer animacji, służący do kontroli zmiany klatek animacji. */
    private float animationTimer = 0;

    /** Indeks aktualnej klatki animacji. */
    private int currentFrame = 0;

    // ---- POLA STANU "FRIGHTENED" ----

    /** Flaga informująca, czy duch znajduje się w stanie przestraszenia. */
    private boolean frightened = false;

    /** Timer stanu frightened, określający jak długo duch pozostanie przestraszony. */
    private float frightenedTimer = 0f;

    // ---- POLA STANU "EATEN" ----

    /** Flaga informująca, czy duch został zjedzony przez Pac-Mana. */
    private boolean eaten = false;

    // ---- POZYCJE STARTOWE ----

    /** Początkowa pozycja w gridzie, gdzie duch się pojawia. */
    private final Point startGridPosition;

    /** Początkowa pozycja wyświetlania ducha (używana przy resetowaniu). */
    private final Vector2 startDisplayPosition;

    // ---- POLA DO MIGANIA (BLINKING) ----

    /** Timer migania, służący do przełączania klatek migania. */
    private float blinkTimer = 0f;

    /** Numer aktualnej klatki migania (1 lub 2). */
    private int blinkFrame = 1;

    // ---- KONSTRUKTORY ----

    /**
     * Konstruktor inicjujący ducha przyjmując pozycję startową wyrażoną w pikselach.
     *
     * @param startX         Początkowa pozycja X w pikselach.
     * @param startY         Początkowa pozycja Y w pikselach.
     * @param type           Typ ducha.
     * @param startDirection Kierunek startowy (0: lewo, 1: prawo, 2: góra, 3: dół).
     */
    public Ghost_Model(float startX, float startY, Ghost_Type type, int startDirection) {
        position = new Point(0, 0);
        this.type = type;
        initialization(startX, startY);
        setDirectionFromInt(startDirection);
        startGridPosition = new Point(0, 0);
        startDisplayPosition = new Vector2(bounds.x, bounds.y);
    }

    /**
     * Konstruktor inicjujący ducha z określoną pozycją w gridzie.
     *
     * @param startX         Początkowa pozycja X w pikselach.
     * @param startY         Początkowa pozycja Y w pikselach.
     * @param gridX          Pozycja X w gridzie.
     * @param gridY          Pozycja Y w gridzie.
     * @param type           Typ ducha.
     * @param startDirection Kierunek startowy (0: lewo, 1: prawo, 2: góra, 3: dół).
     */
    public Ghost_Model(float startX, float startY, int gridX, int gridY, Ghost_Type type, int startDirection) {
        position = new Point(gridX, gridY);
        this.type = type;
        initialization(startX, startY);
        setDirectionFromInt(startDirection);
        startGridPosition = new Point(gridX, gridY);
        startDisplayPosition = new Vector2(bounds.x, bounds.y);
    }

    // ---- METODY INICJALIZACYJNE ----

    /**
     * Metoda inicjalizująca podstawowe pola: prędkość, bounds, hitbox oraz pozycję sprite'a.
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
        hitBox = new Rectangle(
            PacMan_Helper.positioner(GameProperties.GameSpriteSizes.DOT_SIZE, bounds).x,
            PacMan_Helper.positioner(GameProperties.GameSpriteSizes.DOT_SIZE, bounds).y,
            GameProperties.GameSpriteSizes.DOT_SIZE,
            GameProperties.GameSpriteSizes.DOT_SIZE
        );
        spritePosition = PacMan_Helper.positioner(GameProperties.GameSpriteSizes.PACMAN_SIZE, bounds);
        isMoving = false;
    }

    /**
     * Ustawia kierunek ruchu ducha na podstawie liczby całkowitej.
     *
     * <ul>
     *   <li>0 - lewo</li>
     *   <li>1 - prawo</li>
     *   <li>2 - góra</li>
     *   <li>3 - dół</li>
     * </ul>
     *
     * @param direction Kierunek jako liczba całkowita.
     */
    private void setDirectionFromInt(int direction) {
        switch (direction) {
            case 0: setDirection(-1, 0); break;
            case 1: setDirection(1, 0); break;
            case 2: setDirection(0, 1); break;
            case 3: setDirection(0, -1); break;
            default: setDirection(0, 0); break;
        }
        lastDirection = direction;
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
     * {@inheritDoc}
     *
     * Uaktualnia pozycję wyświetlania ducha, a następnie przelicza hitbox i pozycję sprite'a.
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
     * {@inheritDoc}
     *
     * Aktualizuje pozycję ducha w oparciu o prędkość i deltaTime.
     * Jeśli duch się porusza, aktualizuje kierunek ruchu.
     *
     * @param deltaTime Czas, który upłynął od ostatniej aktualizacji (w sekundach).
     */
    @Override
    public void update(float deltaTime) {
        float newX = bounds.x + velocity.x * deltaTime;
        float newY = bounds.y + velocity.y * deltaTime;
        setDisplayPosition(newX, newY);

        if (!velocity.isZero()) {
            lastDirection = getDirectionFromVelocity();
            isMoving = true;
        } else {
            isMoving = false;
        }
    }

    /**
     * Pobiera kierunek ruchu na podstawie aktualnej prędkości.
     *
     * @return Liczba całkowita reprezentująca kierunek (0: lewo, 1: prawo, 2: góra, 3: dół).
     */
    private int getDirectionFromVelocity() {
        if (velocity.x < 0) return 0;
        if (velocity.x > 0) return 1;
        if (velocity.y > 0) return 2;
        if (velocity.y < 0) return 3;
        return lastDirection;
    }

    // ---- GETTERY I SETTERY ----

    /**
     * Zwraca ostatni kierunek ruchu ducha.
     *
     * @return Liczba całkowita reprezentująca ostatni kierunek.
     */
    public int getLastDirection() {
        return lastDirection;
    }

    /**
     * Ustawia kierunek ruchu ducha na podstawie wektora.
     *
     * @param dirX Składowa X kierunku.
     * @param dirY Składowa Y kierunku.
     */
    public void setDirection(float dirX, float dirY) {
        Vector2 dir = new Vector2(dirX, dirY);
        if (dir.len() > 0) {
            dir.nor().scl(GameProperties.GameAttributes.GHOST_SPEED);
            isMoving = true;
        } else {
            isMoving = false;
        }
        this.velocity.set(dir);
    }

    /**
     * Sprawdza, czy duch aktualnie się porusza.
     *
     * @return {@code true} jeśli duch się porusza, w przeciwnym wypadku {@code false}.
     */
    public boolean isMoving() {
        return isMoving;
    }

    /**
     * Zwraca typ ducha.
     *
     * @return Typ ducha {@link Ghost_Type}.
     */
    public Ghost_Type getType() {
        return type;
    }

    /**
     * Zwraca pozycję sprite'a używaną do renderowania.
     *
     * @return Wektor 2D reprezentujący pozycję sprite'a.
     */
    public Vector2 getSpritePosition() {
        return spritePosition;
    }

    /**
     * Zwraca aktualny timer animacji.
     *
     * @return Czas animacji.
     */
    public float getAnimationTimer() {
        return animationTimer;
    }

    /**
     * Ustawia wartość timera animacji.
     *
     * @param animationTimer Nowa wartość timera.
     */
    public void setAnimationTimer(float animationTimer) {
        this.animationTimer = animationTimer;
    }

    /**
     * Zwraca numer aktualnej klatki animacji.
     *
     * @return Numer klatki.
     */
    public int getCurrentFrame() {
        return currentFrame;
    }

    /**
     * Ustawia numer aktualnej klatki animacji.
     *
     * @param currentFrame Nowy numer klatki.
     */
    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    // ---- METODY DOTYCZĄCE STANU "FRIGHTENED" ----

    /**
     * Sprawdza, czy duch jest w stanie frightened.
     *
     * @return {@code true} jeśli duch jest przestraszony, inaczej {@code false}.
     */
    public boolean isFrightened() {
        return frightened;
    }

    /**
     * Ustawia stan frightened dla ducha.
     * Po wyłączeniu stanu frightened resetowane są timer oraz miganie.
     *
     * @param frightened {@code true} jeśli duch ma być przestraszony, inaczej {@code false}.
     */
    public void setFrightened(boolean frightened) {
        this.frightened = frightened;
        if (!frightened) {
            frightenedTimer = 0;
            resetBlinking();
        }
    }

    /**
     * Zwraca timer stanu frightened.
     *
     * @return Czas trwania stanu frightened.
     */
    public float getFrightenedTimer() {
        return frightenedTimer;
    }

    /**
     * Ustawia timer stanu frightened.
     *
     * @param timer Nowa wartość timera.
     */
    public void setFrightenedTimer(float timer) {
        this.frightenedTimer = timer;
    }

    // ---- METODY DOTYCZĄCE STANU "EATEN" ----

    /**
     * Sprawdza, czy duch został zjedzony przez Pac-Mana.
     *
     * @return {@code true} jeśli duch został zjedzony, w przeciwnym wypadku {@code false}.
     */
    public boolean isEaten() {
        return eaten;
    }

    /**
     * Ustawia stan "eaten" dla ducha.
     *
     * @param eaten {@code true} jeśli duch został zjedzony, inaczej {@code false}.
     */
    public void setEaten(boolean eaten) {
        this.eaten = eaten;
    }

    // ---- GETTERY POZYCJI STARTOWEJ ----

    /**
     * Zwraca początkową pozycję ducha w gridzie.
     *
     * @return Pozycja startowa w gridzie.
     */
    public Point getStartGridPosition() {
        return startGridPosition;
    }

    /**
     * Zwraca początkową pozycję wyświetlania ducha.
     *
     * @return Pozycja startowa do renderowania.
     */
    public Vector2 getStartDisplayPosition() {
        return startDisplayPosition;
    }

    // ---- METODY DOTYCZĄCE MIGANIA (BLINKING) ----

    /**
     * Zwraca numer aktualnej klatki migania.
     *
     * @return Numer klatki migania (1 lub 2).
     */
    public int getBlinkFrame() {
        return blinkFrame;
    }

    /**
     * Aktualizuje stan migania. Metoda powinna być wywoływana, gdy czas stanu frightened
     * spada poniżej określonego progu, aby wizualnie zasygnalizować zmianę stanu.
     *
     * @param delta         Czas, który upłynął od ostatniej aktualizacji.
     * @param blinkInterval Interwał czasowy po którym następuje zmiana klatki migania.
     */
    public void updateBlinking(float delta, float blinkInterval) {
        blinkTimer += delta;
        if (blinkTimer >= blinkInterval) {
            blinkTimer = 0;
            blinkFrame = (blinkFrame == 1) ? 2 : 1;
        }
    }

    /**
     * Resetuje stan migania do wartości początkowych.
     */
    public void resetBlinking() {
        blinkTimer = 0f;
        blinkFrame = 1;
    }
}
