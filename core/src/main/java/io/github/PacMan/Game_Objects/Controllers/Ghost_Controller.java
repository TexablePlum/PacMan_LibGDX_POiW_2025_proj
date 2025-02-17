package io.github.PacMan.Game_Objects.Controllers;

import com.badlogic.gdx.math.Vector2;
import io.github.PacMan.GameProperties;
import io.github.PacMan.Game_Objects.Models.Ghost_Model;
import io.github.PacMan.Game_Objects.Models.Ghost_Type;
import io.github.PacMan.Game_Objects.Models.Barrier.Barrier_Model;
import io.github.PacMan.Game_Objects.Models.Grid_Model;
import io.github.PacMan.Game_Objects.Models.PacMan_Model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Kontroler ducha w grze Pac-Man.
 *
 * <p>Odpowiada za sterowanie ruchem ducha, jego aktywację, teleportację, a także wybór kierunku ruchu
 * w zależności od stanu (np. frightened) oraz pozycji Pac-Mana.</p>
 *
 * <p>Klasa implementuje mechanikę opóźnionej aktywacji ducha oraz inteligentnego wyboru kierunku ruchu
 * na podstawie pozycji Pac-Mana i dostępnych ścieżek.</p>
 */
public class Ghost_Controller {

    // ---- MODELE I WŁAŚCIWOŚCI ----

    /** Model reprezentujący ducha. */
    private final Ghost_Model ghostModel;

    /** Model planszy gry (grid). */
    private final Grid_Model gridModel;

    /** Model reprezentujący Pac-Mana. */
    private final PacMan_Model pacManModel;

    /**
     * Aktualny kierunek ruchu ducha.
     * Początkowo ustawiony na NONE.
     */
    private Moving_Directions movingDirections = Moving_Directions.NONE;

    /**
     * Mapa pozycji kafelków w gridzie.
     * Klucz: pozycja w gridzie, Wartość: pozycja wyświetlania (w pikselach).
     */
    private final Map<Point, Vector2> tilePositions;

    /** Generator liczb losowych używany do wyboru kierunku ruchu. */
    private final Random random = new Random();

    /** Stała określająca stały krok czasowy używany przy aktualizacji (fixed time step). */
    private static final float FIXED_TIME_STEP = 1f / GameProperties.GameSettings.FRAME_RATE;

    // ---- AKTYWACJA DUCHA ----

    /**
     * Opóźnienie w sekundach przed aktywacją ducha, zależne od typu.
     */
    private final float activationDelay;

    /** Timer liczący czas od rozpoczęcia gry do aktywacji ducha. */
    private float activationTimer = 0f;

    /** Flaga informująca, czy duch został aktywowany. */
    private boolean activated = false;

    // ---- KONSTRUKTOR ----

    /**
     * Konstruktor kontrolera ducha.
     *
     * <p>Inicjalizuje kontroler na podstawie przekazanych modeli oraz ustawia opóźnienie aktywacji
     * w zależności od typu ducha (BLINKY, PINKY, INKY, CLYDE).</p>
     *
     * @param ghostModel  Model reprezentujący ducha.
     * @param gridModel   Model planszy gry.
     * @param pacManModel Model reprezentujący Pac-Mana.
     */
    public Ghost_Controller(Ghost_Model ghostModel, Grid_Model gridModel, PacMan_Model pacManModel) {
        this.ghostModel = ghostModel;
        this.gridModel = gridModel;
        this.pacManModel = pacManModel;
        this.tilePositions = gridModel.getTilesPositions();

        // Ustaw opóźnienie aktywacji w zależności od typu ducha.
        switch (ghostModel.getType()) {
            case BLINKY: this.activationDelay = 0f; break;
            case PINKY:  this.activationDelay = 3f; break;
            case INKY:   this.activationDelay = 6f; break;
            case CLYDE:  this.activationDelay = 9f; break;
            default:     this.activationDelay = 12f; break;
        }
    }

    // ---- GETTERY I METODY AKTUALIZACJI AKTYWACJI ----

    /**
     * Zwraca model ducha.
     *
     * @return Model ducha.
     */
    public Ghost_Model getGhostModel() {
        return ghostModel;
    }

    /**
     * Sprawdza, czy duch został aktywowany.
     *
     * @return {@code true} jeśli duch jest aktywowany, inaczej {@code false}.
     */
    public boolean isActivated() {
        return activated;
    }

    /**
     * Resetuje stan aktywacji ducha (ustawia activated na false i zeruje timer).
     */
    public void resetActivation() {
        activated = false;
        activationTimer = 0f;
    }

    /**
     * Ustawia stan frightened dla ducha.
     *
     * @param frightened {@code true} jeśli duch ma być przestraszony, inaczej {@code false}.
     */
    public void setFrightened(boolean frightened) {
        ghostModel.setFrightened(frightened);
    }

    // ---- GŁÓWNA METODA AKTUALIZACJI ----

    /**
     * Aktualizuje stan ducha.
     *
     * <p>Metoda wykonuje następujące operacje:
     * <ul>
     *   <li>Aktualizuje timer stanu frightened, w tym miganie i resetowanie stanu frightened.</li>
     *   <li>Obsługuje opóźnienie aktywacji ducha – duch nie porusza się, dopóki timer aktywacji nie przekroczy activationDelay.</li>
     *   <li>Podczas aktualizacji, przy użyciu fixed time step, aktualizuje model ducha,
     *       obsługuje teleportację, sprawdza kierunek ruchu i przetwarza logikę kafelka.</li>
     * </ul>
     * </p>
     *
     * @param delta Czas, który upłynął od ostatniej aktualizacji (w sekundach).
     */
    public void update(float delta) {
        // Aktualizacja timera stanu frightened, jeżeli duch jest przestraszony i nie został zjedzony.
        if (ghostModel.isFrightened() && !ghostModel.isEaten()) {
            ghostModel.setFrightenedTimer(ghostModel.getFrightenedTimer() - delta);
            float BLINKING_THRESHOLD = 3f; // próg, po którym duch zaczyna migać
            float BLINK_INTERVAL = 0.3f; // interwał zmiany klatki migania
            if (ghostModel.getFrightenedTimer() <= BLINKING_THRESHOLD) {
                ghostModel.updateBlinking(delta, BLINK_INTERVAL);
            }
            if (ghostModel.getFrightenedTimer() <= 0) {
                ghostModel.setFrightened(false);
                ghostModel.resetBlinking();
            }
        }

        // Obsługa opóźnienia aktywacji ducha.
        if (!activated) {
            activationTimer += delta;
            if (activationTimer < activationDelay) {
                return; // Duch nie zostanie aktywowany, dopóki nie upłynie activationDelay.
            }
            activated = true;
            // Jeśli duch był zjedzony, resetujemy stan i ustawiamy początkowy kierunek ruchu.
            if (ghostModel.isEaten()) {
                ghostModel.setEaten(false);
                setDirectionToModel(getInitialMovingDirectionForType(ghostModel.getType()));
            }
        }

        // Aktualizacja modelu ducha przy użyciu fixed time step.
        float remaining = delta;
        while (remaining > 0f) {
            float step = Math.min(FIXED_TIME_STEP, remaining);
            ghostModel.update(step);
            handleTeleportByPosition();
            // Jeśli duch nie ma ustalonego kierunku, ustaw początkowy kierunek.
            if (ghostModel.getLastDirection() == -1) {
                setDirectionToModel(getInitialMovingDirectionForType(ghostModel.getType()));
            }
            processCurrentTileLogic();
            remaining -= step;
        }
    }

    // ---- METODY POMOCNICZE - USTAWIENIE POCZĄTKOWEGO KIERUNKU ----

    /**
     * Zwraca początkowy kierunek ruchu dla danego typu ducha.
     *
     * @param type Typ ducha.
     * @return Początkowy kierunek ruchu jako {@link Moving_Directions}.
     */
    private Moving_Directions getInitialMovingDirectionForType(Ghost_Type type) {
        switch (type) {
            case BLINKY: return Moving_Directions.LEFT;
            case PINKY:  return Moving_Directions.RIGHT;
            case INKY:   return Moving_Directions.UP;
            case CLYDE:  return Moving_Directions.DOWN;
            default:     return Moving_Directions.LEFT;
        }
    }

    // ---- LOGIKA PRZETWARZANIA AKTUALNEGO KAFELKA ----

    /**
     * Znajduje bieżący kafelek, na którym znajduje się duch.
     *
     * <p>Algorytm:
     * <ul>
     *   <li>Tworzy wektor pozycji ducha na podstawie jego granic.</li>
     *   <li>Iteruje przez mapę kafelków i sprawdza, czy pozycja ducha jest zgodna z pozycją kafelka (z tolerancją epsilon równą 1).</li>
     *   <li>Zwraca pierwszy pasujący wpis lub {@code null}, jeśli nie znaleziono.</li>
     * </ul>
     * </p>
     *
     * @return Wejście z mapy kafelków lub {@code null}.
     */
    private Map.Entry<Point, Vector2> findCurrentTileEntry() {
        Vector2 ghostPos = new Vector2(ghostModel.getBounds().x, ghostModel.getBounds().y);
        for (Map.Entry<Point, Vector2> entry : tilePositions.entrySet()) {
            if (ghostPos.epsilonEquals(entry.getValue(), 1f)) {
                return entry;
            }
        }
        return null;
    }

    /**
     * Przetwarza logikę związaną z aktualnym kafelkiem, na którym znajduje się duch.
     *
     * <p>Metoda:
     * <ul>
     *   <li>Znajduje bieżący kafelek przy użyciu {@link #findCurrentTileEntry()}.</li>
     *   <li>Aktualizuje pozycję ducha w gridzie.</li>
     *   <li>Wybiera nowy kierunek ruchu dla ducha, korzystając z {@link #chooseGhostDirection(Point)}.</li>
     * </ul>
     * </p>
     */
    private void processCurrentTileLogic() {
        Map.Entry<Point, Vector2> currentTileEntry = findCurrentTileEntry();
        if (currentTileEntry == null) return;
        Point tileIndex = currentTileEntry.getKey();
        Vector2 tilePosition = currentTileEntry.getValue();
        updateGhostGridPosition(tileIndex, tilePosition);
        chooseGhostDirection(tileIndex);
    }

    /**
     * Aktualizuje pozycję ducha w gridzie.
     *
     * @param tileIndex    Indeks kafelka.
     * @param tilePosition Pozycja wyświetlania kafelka.
     */
    private void updateGhostGridPosition(Point tileIndex, Vector2 tilePosition) {
        ghostModel.setDisplayPosition(tilePosition.x, tilePosition.y);
        ghostModel.setGridPosition(tileIndex.x, tileIndex.y);
    }

    // ---- WYBÓR KIERUNKU RUCHU DUCHA ----

    /**
     * Wybiera kierunek ruchu ducha na podstawie dostępnych ścieżek i strategii,
     * zależnej od typu ducha.
     *
     * <p>Algorytm:
     * <ul>
     *   <li>Jeśli duch jest w stanie frightened, wywołuje {@link #chooseRandomDirection(Point)}.</li>
     *   <li>Tworzy listę dostępnych kierunków (bez tych, w których znajduje się bariera).</li>
     *   <li>Jeżeli jest więcej niż jeden dostępny kierunek, usuwa z listy kierunek przeciwny do aktualnego,
     *       aby uniknąć zawracania.</li>
     *   <li>Dla danego typu ducha ustala "error chance" oraz cel (targetTile) na podstawie pozycji Pac-Mana
     *       i dodatkowych modyfikatorów (np. przesunięcie w przypadku PINKY, losowy offset dla INKY, warunek dla CLYDE).</li>
     *   <li>Z losowym prawdopodobieństwem (errorChance) wybiera losowy dostępny kierunek.</li>
     *   <li>W przeciwnym razie, wybiera kierunek, który minimalizuje odległość Manhattan do targetTile.</li>
     *   <li>Ustawia wybrany kierunek w modelu ducha.</li>
     * </ul>
     * </p>
     *
     * @param tileIndex Indeks bieżącego kafelka.
     */
    private void chooseGhostDirection(Point tileIndex) {
        if (ghostModel.isFrightened()) {
            chooseRandomDirection(tileIndex);
            return;
        }

        List<Moving_Directions> availableDirs = new ArrayList<>();
        // Zbierz dostępne kierunki, dla których nie występuje bariera.
        for (Moving_Directions dir : Moving_Directions.values()) {
            if (dir == Moving_Directions.NONE) continue;
            if (!isBarrierAhead(tileIndex, dir)) {
                availableDirs.add(dir);
            }
        }
        if (availableDirs.isEmpty()) {
            movingDirections = Moving_Directions.NONE;
            setDirectionToModel(Moving_Directions.NONE);
            return;
        }

        // Jeśli dostępnych jest więcej niż jeden kierunek i duch już się porusza,
        // usuń kierunek przeciwny, aby uniknąć gwałtownego zawracania.
        if (availableDirs.size() > 1 && movingDirections != Moving_Directions.NONE) {
            Moving_Directions opposite = getOppositeDirection(movingDirections);
            availableDirs.remove(opposite);
        }

        // Ustal cel ruchu na podstawie pozycji Pac-Mana.
        Point pacManTile = pacManModel.getGridPosition();
        Point targetTile = new Point(pacManTile);
        double errorChance = 0.0;
        switch (ghostModel.getType()) {
            case BLINKY:
                errorChance = 0.3;
                break;
            case PINKY:
                errorChance = 0.5;
                int offset = 2;
                int pacDirection = pacManModel.getLastDirection();
                // Modyfikacja celu w zależności od kierunku Pac-Mana.
                switch (pacDirection) {
                    case 0: targetTile.translate(-offset, 0); break;
                    case 1: targetTile.translate(offset, 0); break;
                    case 2: targetTile.translate(0, offset); break;
                    case 3: targetTile.translate(0, -offset); break;
                    default: break;
                }
                break;
            case INKY:
                errorChance = 0.6;
                targetTile.translate(random.nextInt(3) - 1, random.nextInt(3) - 1);
                break;
            case CLYDE:
                errorChance = 0.7;
                // Jeśli duch jest blisko Pac-Mana, celuje w przeciwną stronę (np. lewy dolny róg).
                int distance = Math.abs(tileIndex.x - pacManTile.x) + Math.abs(tileIndex.y - pacManTile.y);
                if (distance <= 8) {
                    targetTile = new Point(0, gridModel.getRows() - 1);
                }
                break;
            default:
                break;
        }

        // Wybierz losowy kierunek z prawdopodobieństwem errorChance.
        if (random.nextDouble() < errorChance) {
            movingDirections = availableDirs.get(random.nextInt(availableDirs.size()));
            setDirectionToModel(movingDirections);
            return;
        }

        // W przeciwnym razie, wybierz kierunek minimalizujący odległość Manhattan do targetTile.
        int bestDistance = Integer.MAX_VALUE;
        List<Moving_Directions> bestCandidates = new ArrayList<>();
        for (Moving_Directions dir : availableDirs) {
            int newX = tileIndex.x;
            int newY = tileIndex.y;
            switch (dir) {
                case LEFT -> newX--;
                case RIGHT -> newX++;
                case UP -> newY++;
                case DOWN -> newY--;
                default -> {}
            }
            int distanceToTarget = Math.abs(targetTile.x - newX) + Math.abs(targetTile.y - newY);
            if (distanceToTarget < bestDistance) {
                bestDistance = distanceToTarget;
                bestCandidates.clear();
                bestCandidates.add(dir);
            } else if (distanceToTarget == bestDistance) {
                bestCandidates.add(dir);
            }
        }
        movingDirections = bestCandidates.get(random.nextInt(bestCandidates.size()));
        setDirectionToModel(movingDirections);
    }

    /**
     * Wybiera losowy kierunek ruchu dla ducha, gdy jest on w stanie frightened.
     *
     * @param tileIndex Indeks bieżącego kafelka.
     */
    private void chooseRandomDirection(Point tileIndex) {
        List<Moving_Directions> availableDirs = new ArrayList<>();
        // Zbierz dostępne kierunki, dla których nie występuje bariera.
        for (Moving_Directions dir : Moving_Directions.values()) {
            if (dir == Moving_Directions.NONE) continue;
            if (!isBarrierAhead(tileIndex, dir)) {
                availableDirs.add(dir);
            }
        }
        if (!availableDirs.isEmpty()) {
            if (availableDirs.size() > 1 && movingDirections != Moving_Directions.NONE) {
                Moving_Directions opposite = getOppositeDirection(movingDirections);
                availableDirs.remove(opposite);
            }
            movingDirections = availableDirs.get(random.nextInt(availableDirs.size()));
            setDirectionToModel(movingDirections);
        } else {
            movingDirections = Moving_Directions.NONE;
            setDirectionToModel(Moving_Directions.NONE);
        }
    }

    /**
     * Zwraca przeciwny kierunek do podanego.
     *
     * @param dir Aktualny kierunek.
     * @return Kierunek przeciwny.
     */
    private Moving_Directions getOppositeDirection(Moving_Directions dir) {
        return switch (dir) {
            case LEFT -> Moving_Directions.RIGHT;
            case RIGHT -> Moving_Directions.LEFT;
            case UP -> Moving_Directions.DOWN;
            case DOWN -> Moving_Directions.UP;
            default -> Moving_Directions.NONE;
        };
    }

    /**
     * Ustawia kierunek ruchu ducha w modelu na podstawie podanego kierunku.
     *
     * @param direction Kierunek ruchu.
     */
    private void setDirectionToModel(Moving_Directions direction) {
        float dirX = 0, dirY = 0;
        switch (direction) {
            case LEFT -> dirX = -1;
            case RIGHT -> dirX = 1;
            case UP -> dirY = 1;
            case DOWN -> dirY = -1;
            default -> {}
        }
        ghostModel.setDirection(dirX, dirY);
    }

    // ---- SPRAWDZENIE OBECNOŚCI BARIERY ----

    /**
     * Sprawdza, czy w danym kierunku od wskazanego kafelka znajduje się bariera.
     *
     * <p>Algorytm:
     * <ul>
     *   <li>Oblicza indeks sąsiedniego kafelka na podstawie kierunku.</li>
     *   <li>Jeśli indeks wykracza poza granice gridu, zwraca {@code false}.</li>
     *   <li>Jeśli obiekt na nowym indeksie jest instancją {@link Barrier_Model}, zwraca {@code true}.</li>
     * </ul>
     * </p>
     *
     * @param tileIndex Indeks bieżącego kafelka.
     * @param direction Kierunek, w którym sprawdzamy obecność bariery.
     * @return {@code true} jeśli bariera jest obecna, inaczej {@code false}.
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

    // ---- TELEPORTACJA DUCHA ----

    /**
     * Obsługuje teleportację ducha, gdy wychodzi poza granice świata gry.
     *
     * <p>Algorytm analogiczny do metody {@link PacMan_Controller #handleTeleportByPosition()}:
     * <ul>
     *   <li>Oblicza pozycję ducha oraz rozmiar jego sprite'a.</li>
     *   <li>Określa wymiary świata gry na podstawie rozmiaru gridu i pozycji startowej.</li>
     *   <li>Jeśli duch opuszcza lewą lub dolną krawędź, teleportuje go na przeciwną stronę, i analogicznie dla prawej lub górnej krawędzi.</li>
     * </ul>
     * </p>
     */
    private void handleTeleportByPosition() {
        float ghostX = ghostModel.getBounds().x;
        float ghostY = ghostModel.getBounds().y;
        float spriteSize = GameProperties.GameGridProps.CELL_SIZE;
        float worldWidth = gridModel.getCols() * GameProperties.GameGridProps.CELL_SIZE
            + GameProperties.GameGridProps.POSITION_Y;
        float worldHeight = gridModel.getRows() * GameProperties.GameGridProps.CELL_SIZE
            + GameProperties.GameGridProps.POSITION_Y;
        if (ghostX + spriteSize <= 0) {
            ghostX = worldWidth;
        } else if (ghostX >= worldWidth) {
            ghostX = -spriteSize;
        }
        if (ghostY + spriteSize <= 0) {
            ghostY = worldHeight;
        } else if (ghostY >= worldHeight) {
            ghostY = -spriteSize;
        }
        ghostModel.setDisplayPosition(ghostX, ghostY);
    }
}
