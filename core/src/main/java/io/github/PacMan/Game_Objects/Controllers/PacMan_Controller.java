package io.github.PacMan.Game_Objects.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import io.github.PacMan.GameProperties;
import io.github.PacMan.Game_Objects.Models.*;
import io.github.PacMan.Game_Objects.Models.Barrier.Barrier_Model;

import java.awt.*;
import java.util.Map;

/**
 * Kontroler postaci Pac-Man.
 *
 * <p>Odpowiada za:
 * <ul>
 *   <li>Odczyt wejścia z klawiatury i ustalanie kierunku ruchu</li>
 *   <li>Aktualizację pozycji Pac-Mana w oparciu o stan modelu</li>
 *   <li>Logikę teleportacji przy opuszczeniu granic planszy</li>
 *   <li>Przetwarzanie logiki związanej z aktualnie zajmowanym kafelkiem (np. kolizja z kropką, zmiana kierunku)</li>
 *   <li>Aktualizację wyniku oraz wywołanie callbacka po zjedzeniu power-upa</li>
 * </ul>
 * </p>
 */
public class PacMan_Controller {

    // ---- MODELE I WŁAŚCIWOŚCI ----

    /** Model reprezentujący Pac-Mana. */
    private final PacMan_Model pacManModel;

    /** Model reprezentujący planszę gry (grid). */
    private final Grid_Model gridModel;

    /** Model interfejsu użytkownika, zawierający m.in. wynik i liczbę żyć. */
    private final UserInterface_Model uiModel;

    /**
     * Callback wywoływany, gdy Pac-Man zje dużą kropkę (power-up),
     * np. w celu zmiany stanu duchów.
     */
    private final Runnable powerUpCallback;

    /** Aktualny kierunek ruchu Pac-Mana. */
    private Moving_Directions movingDirections;

    /** Bufor kierunku – przechowuje ostatnio wciśnięty kierunek, oczekujący na przetworzenie. */
    private Moving_Directions directionBuffer = Moving_Directions.NONE;

    /** Mapa pozycji poszczególnych kafelków na planszy. Klucz to indeks kafelka, wartość to pozycja wyświetlania. */
    private final Map<Point, Vector2> tilePositions;

    /** Flaga informująca, czy Pac-Man kiedykolwiek poruszył się od startu gry. */
    private boolean hasMoved = false;

    // ---- KONSTRUKTOR ----

    /**
     * Konstruktor kontrolera Pac-Mana.
     *
     * @param pacManModel     Model reprezentujący Pac-Mana.
     * @param gridModel       Model planszy gry.
     * @param uiModel         Model interfejsu użytkownika.
     * @param powerUpCallback Callback, który zostanie wywołany po zjedzeniu power-upa.
     */
    public PacMan_Controller(PacMan_Model pacManModel,
                             Grid_Model gridModel,
                             UserInterface_Model uiModel,
                             Runnable powerUpCallback) {
        this.pacManModel = pacManModel;
        this.gridModel = gridModel;
        this.uiModel = uiModel;
        this.powerUpCallback = powerUpCallback;
        this.tilePositions = gridModel.getTilesPositions();
        this.movingDirections = GameProperties.GameAttributes.START_MOVING_DIRECTIONS;
    }

    // ---- OBSŁUGA WEJŚCIA I AKTUALIZACJA STANU ----

    /**
     * Odczytuje wejście z klawiatury i ustawia kierunek ruchu Pac-Mana.
     *
     * <p>Metoda:
     * <ol>
     *   <li>Wywołuje {@link #readDirectionFromKeyboard()} w celu pobrania kierunku z klawiatury.</li>
     *   <li>Aktualizuje model Pac-Mana, przekazując ustalony kierunek poprzez {@link #setDirectionToModel(Moving_Directions)}.</li>
     * </ol>
     * </p>
     */
    public void handleInput() {
        readDirectionFromKeyboard();
        setDirectionToModel(movingDirections);
    }

    /**
     * Aktualizuje stan Pac-Mana.
     *
     * <p>Metoda wykonuje następujące kroki:
     * <ul>
     *   <li>Aktualizuje model Pac-Mana (pozycja, animacja) na podstawie delta time.</li>
     *   <li>Obsługuje teleportację, jeśli Pac-Man wychodzi poza granice planszy.</li>
     *   <li>Sprawdza, czy istnieje oczekujący kierunek zmiany, gdy Pac-Man zatrzymał się.</li>
     *   <li>Przetwarza logikę związaną z aktualnym kafelkiem (kolizje, zmiana kierunku, zjadanie kropek).</li>
     * </ul>
     * </p>
     *
     * @param delta Czas, który upłynął od ostatniej aktualizacji (w sekundach).
     */
    public void update(float delta) {
        pacManModel.update(delta);
        handleTeleportByPosition();
        checkPendingDirectionWhenStopped();
        processCurrentTileLogic();
    }

    // ---- TELEPORTACJA POZYCJI ----

    /**
     * Obsługuje teleportację Pac-Mana, gdy wychodzi poza granice świata.
     *
     * <p>Algorytm:
     * <ul>
     *   <li>Oblicza aktualną pozycję Pac-Mana oraz wymiary jego sprite'a.</li>
     *   <li>Określa wymiary świata gry na podstawie rozmiaru gridu i pozycji startowej.</li>
     *   <li>Jeśli Pac-Man całkowicie opuści lewą lub dolną krawędź, teleportuje go na przeciwną stronę.</li>
     *   <li>Analogicznie, jeśli przekroczy prawą lub górną krawędź, ustawia pozycję na przeciwną stronę.</li>
     * </ul>
     * </p>
     */
    private void handleTeleportByPosition() {
        float pacX = pacManModel.getBounds().x;
        float pacY = pacManModel.getBounds().y;
        float spriteWidth  = GameProperties.GameGridProps.CELL_SIZE;
        float spriteHeight = GameProperties.GameGridProps.CELL_SIZE;
        float worldWidth  = gridModel.getCols() * GameProperties.GameGridProps.CELL_SIZE
            + GameProperties.GameGridProps.POSITION_Y;
        float worldHeight = gridModel.getRows() * GameProperties.GameGridProps.CELL_SIZE
            + GameProperties.GameGridProps.POSITION_Y;

        // Teleportacja w osi X
        if (pacX + spriteWidth <= 0) {
            pacX = worldWidth;
        } else if (pacX >= worldWidth) {
            pacX = -spriteWidth;
        }

        // Teleportacja w osi Y
        if (pacY + spriteHeight <= 0) {
            pacY = worldHeight;
        } else if (pacY >= worldHeight) {
            pacY = -spriteHeight;
        }
        pacManModel.setDisplayPosition(pacX, pacY);
    }

    // ---- ODCZYT WEJŚCIA Z KLAWIATURY ----

    /**
     * Odczytuje kierunek z klawiatury i zapisuje go w buforze.
     *
     * <p>Algorytm:
     * <ul>
     *   <li>Sprawdza, czy użytkownik nacisnął klawisze strzałek (LEFT, RIGHT, UP, DOWN).</li>
     *   <li>Aktualizuje {@code directionBuffer} na podstawie ostatnio wciśniętego klawisza.</li>
     *   <li>Ustawia flagę {@code hasMoved} na {@code true} po wykryciu ruchu.</li>
     * </ul>
     * </p>
     */
    private void readDirectionFromKeyboard() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            directionBuffer = Moving_Directions.LEFT;
            hasMoved = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            directionBuffer = Moving_Directions.RIGHT;
            hasMoved = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            directionBuffer = Moving_Directions.UP;
            hasMoved = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            directionBuffer = Moving_Directions.DOWN;
            hasMoved = true;
        }
    }

    // ---- USTAWIANIE KIERUNKU RUCHU ----

    /**
     * Ustawia kierunek ruchu Pac-Mana w modelu na podstawie podanego kierunku.
     *
     * <p>Algorytm:
     * <ul>
     *   <li>Przypisuje wartości wektora (dirX, dirY) w zależności od wybranego kierunku.</li>
     *   <li>Wywołuje {@link PacMan_Model#setDirection(float, float)}, aby zaktualizować wektor prędkości.</li>
     * </ul>
     * </p>
     *
     * @param direction Kierunek ruchu, reprezentowany przez enum {@link Moving_Directions}.
     */
    private void setDirectionToModel(Moving_Directions direction) {
        float dirX = 0;
        float dirY = 0;
        switch (direction) {
            case LEFT -> dirX = -1;
            case RIGHT -> dirX = 1;
            case UP -> dirY = 1;
            case DOWN -> dirY = -1;
        }
        pacManModel.setDirection(dirX, dirY);
    }

    // ---- OBSŁUGA ZMIANY KIERUNKU ----

    /**
     * Sprawdza, czy istnieje oczekujący kierunek (w buforze), gdy Pac-Man jest zatrzymany.
     *
     * <p>Jeśli Pac-Man nie porusza się (movingDirections == NONE) oraz
     * {@code directionBuffer} zawiera wartość inną niż NONE, metoda sprawdza,
     * czy w wybranym kierunku nie ma bariery. Jeśli nie ma, buforowany kierunek zostaje zastosowany.</p>
     */
    private void checkPendingDirectionWhenStopped() {
        if (movingDirections == Moving_Directions.NONE && directionBuffer != Moving_Directions.NONE) {
            Point currentTile = pacManModel.getGridPosition();
            if (!isBarrierAhead(currentTile, directionBuffer)) {
                applyBufferedDirection();
            }
        }
    }

    // ---- LOGIKA OBSŁUGI AKTUALNEGO KAFELKA ----

    /**
     * Przetwarza logikę związaną z aktualnym kafelkiem, na którym znajduje się Pac-Man.
     *
     * <p>Metoda:
     * <ul>
     *   <li>Znajduje aktualny kafelek na podstawie pozycji Pac-Mana.</li>
     *   <li>Aktualizuje pozycję Pac-Mana w gridzie.</li>
     *   <li>Obsługuje kolizję z kropkami (Dot_Model), aktualizując wynik i usuwając kropkę z gridu.</li>
     *   <li>Sprawdza, czy należy zmienić kierunek ruchu lub zatrzymać Pac-Mana, gdy napotka barierę.</li>
     * </ul>
     * </p>
     */
    private void processCurrentTileLogic() {
        Map.Entry<Point, Vector2> currentTileEntry = findCurrentTileEntry();
        if (currentTileEntry == null) return;
        Point tileIndex = currentTileEntry.getKey();
        Vector2 tilePosition = currentTileEntry.getValue();
        updatePacManGridPosition(tileIndex, tilePosition);
        handleDotCollision(tileIndex);
        checkForDirectionChange(tileIndex);
        checkCurrentDirectionBlockage(tileIndex);
    }

    /**
     * Znajduje bieżący kafelek na podstawie pozycji Pac-Mana.
     *
     * <p>Algorytm:
     * <ul>
     *   <li>Tworzy wektor pozycji Pac-Mana z jego granic.</li>
     *   <li>Iteruje przez mapę pozycji kafelków i porównuje pozycję Pac-Mana z pozycją każdego kafelka z tolerancją epsilon równą 1.</li>
     *   <li>Zwraca pierwszy pasujący wpis lub {@code null}, jeśli nie znaleziono odpowiedniego kafelka.</li>
     * </ul>
     * </p>
     *
     * @return Wejście z mapy reprezentujące bieżący kafelek lub {@code null}, jeśli nie znaleziono.
     */
    private Map.Entry<Point, Vector2> findCurrentTileEntry() {
        Vector2 pacManPosition = new Vector2(
            pacManModel.getBounds().x,
            pacManModel.getBounds().y
        );
        for (Map.Entry<Point, Vector2> entry : tilePositions.entrySet()) {
            if (pacManPosition.epsilonEquals(entry.getValue(), 1f)) {
                return entry;
            }
        }
        return null;
    }

    /**
     * Aktualizuje pozycję Pac-Mana w gridzie na podstawie indeksu i pozycji wyświetlania aktualnego kafelka.
     *
     * @param tileIndex    Indeks aktualnego kafelka.
     * @param tilePosition Pozycja wyświetlania aktualnego kafelka.
     */
    private void updatePacManGridPosition(Point tileIndex, Vector2 tilePosition) {
        pacManModel.setDisplayPosition(tilePosition.x, tilePosition.y);
        pacManModel.setGridPosition(tileIndex.x, tileIndex.y);
    }

    /**
     * Obsługuje kolizję Pac-Mana z kropką znajdującą się na danym kafelku.
     *
     * <p>Algorytm:
     * <ul>
     *   <li>Pobiera obiekt z gridu na pozycji {@code tileIndex}.</li>
     *   <li>Jeśli obiekt jest instancją {@link Dot_Model}, usuwa go z gridu.</li>
     *   <li>Aktualizuje wynik (10 punktów za zwykłą kropkę, 50 punktów za power-up).</li>
     *   <li>Jeśli kropka jest power-upem, wywołuje {@code powerUpCallback} (jeśli nie jest null).</li>
     * </ul>
     * </p>
     *
     * @param tileIndex Indeks kafelka, na którym doszło do kolizji.
     */
    private void handleDotCollision(Point tileIndex) {
        GameObject_Model tileObject = gridModel.getObjectAt(tileIndex.x, tileIndex.y);
        if (!(tileObject instanceof Dot_Model dot)) return;
        gridModel.setObjectAt(tileIndex.x, tileIndex.y, null);
        updateScore(dot.isPowerUp() ? 50 : 10);
        if (dot.isPowerUp() && powerUpCallback != null) {
            powerUpCallback.run();
        }
    }

    /**
     * Sprawdza, czy należy zmienić kierunek ruchu na podstawie bufora.
     *
     * <p>Jeśli {@code directionBuffer} zawiera wartość różną od aktualnego kierunku, a w kierunku tym
     * nie ma bariery, kierunek zostaje zmieniony.</p>
     *
     * @param tileIndex Indeks bieżącego kafelka.
     */
    private void checkForDirectionChange(Point tileIndex) {
        if (directionBuffer != Moving_Directions.NONE && directionBuffer != movingDirections) {
            if (!isBarrierAhead(tileIndex, directionBuffer)) {
                applyBufferedDirection();
            }
        }
    }

    /**
     * Sprawdza, czy bieżący kierunek ruchu jest zablokowany przez barierę.
     *
     * <p>Jeśli w kierunku ruchu znajduje się bariera, ustawiany jest kierunek {@link Moving_Directions#NONE},
     * co powoduje zatrzymanie Pac-Mana.</p>
     *
     * @param tileIndex Indeks bieżącego kafelka.
     */
    private void checkCurrentDirectionBlockage(Point tileIndex) {
        if (isBarrierAhead(tileIndex, movingDirections)) {
            movingDirections = Moving_Directions.NONE;
            setDirectionToModel(Moving_Directions.NONE);
        }
    }

    // ---- AKTUALIZACJA WYNIKU ----

    /**
     * Aktualizuje wynik w interfejsie użytkownika.
     *
     * <p>Algorytm:
     * <ul>
     *   <li>Pobiera aktualny wynik jako liczbę całkowitą.</li>
     *   <li>Dodaje do niego podaną liczbę punktów i aktualizuje model UI.</li>
     * </ul>
     * </p>
     *
     * @param pointsToAdd Liczba punktów do dodania.
     */
    private void updateScore(int pointsToAdd) {
        int currentScore = Integer.parseInt(uiModel.getScoreValue());
        uiModel.setScoreValue(currentScore + pointsToAdd);
    }

    // ---- APLIKACJA KIERUNKU Z BUFORA ----

    /**
     * Ustawia buforowany kierunek ruchu jako aktualny i resetuje bufor.
     */
    private void applyBufferedDirection() {
        movingDirections = directionBuffer;
        directionBuffer = Moving_Directions.NONE;
    }

    // ---- SPRAWDZENIE BARIERY ----

    /**
     * Sprawdza, czy w danym kierunku od wskazanego kafelka znajduje się bariera.
     *
     * <p>Algorytm:
     * <ul>
     *   <li>Oblicza indeks nowego kafelka w zależności od kierunku (LEFT, RIGHT, UP, DOWN).</li>
     *   <li>Jeśli nowy indeks wychodzi poza granice gridu, metoda zwraca {@code false}.</li>
     *   <li>Jeśli w nowym kafelku znajduje się obiekt będący instancją {@link Barrier_Model},
     *       metoda zwraca {@code true}.</li>
     * </ul>
     * </p>
     *
     * @param tileIndex Indeks bieżącego kafelka.
     * @param direction Kierunek, w którym sprawdzamy obecność bariery.
     * @return {@code true} jeśli w kierunku znajduje się bariera, inaczej {@code false}.
     */
    private boolean isBarrierAhead(Point tileIndex, Moving_Directions direction) {
        int newX = tileIndex.x;
        int newY = tileIndex.y;
        switch (direction) {
            case LEFT -> newX--;
            case RIGHT -> newX++;
            case UP -> newY++;
            case DOWN -> newY--;
            default -> { return false; }
        }
        if (newX < 0 || newX >= gridModel.getCols() || newY < 0 || newY >= gridModel.getRows()) {
            return false;
        }
        return gridModel.getObjectAt(newX, newY) instanceof Barrier_Model;
    }

    // ---- GETTERY ----

    /**
     * Zwraca flagę informującą, czy Pac-Man kiedykolwiek poruszył się od startu gry.
     *
     * @return {@code true} jeśli Pac-Man się poruszył, inaczej {@code false}.
     */
    public boolean hasMoved() {
        return hasMoved;
    }
}
