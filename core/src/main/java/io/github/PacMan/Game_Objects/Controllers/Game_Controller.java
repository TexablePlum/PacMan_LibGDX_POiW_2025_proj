package io.github.PacMan.Game_Objects.Controllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import io.github.PacMan.Game_Objects.Models.*;
import io.github.PacMan.Game_Objects.Models.Barrier.Barrier_Model;
import io.github.PacMan.Game_Objects.Models.Barrier.Barrier_Type;
import io.github.PacMan.PacMan_Helper;
import io.github.PacMan.Stage_Interpreter.StageInitializer;

import java.awt.Point;
import java.util.*;

/**
 * Kontroler główny gry, odpowiedzialny za zarządzanie logiką gry, stanem poziomu,
 * aktualizację modeli (Pac-Man, duszki, grid) oraz obsługę animacji końca etapu.
 *
 * <p>Klasa integruje działanie kontrolerów postaci, steruje przebiegiem gry, wykrywa
 * kolizje oraz zarządza przejściami między etapami.</p>
 */
public class Game_Controller {
    // ---- POLA STERUJĄCE STANEM GRY ----

    /** Flaga, czy dodatkowe życie zostało już przyznane (za 10 000 punktów). */
    private boolean extraLifeAwarded = false;

    // ---- MODELE I REFERENCJE ----

    /** Model planszy (grid) zawierający wszystkie obiekty gry. */
    private final Grid_Model gridModel;

    /** Model interfejsu użytkownika (wynik, życia, highscore itp.). */
    private final UserInterface_Model uiModel;

    /** Mapa pozycji kafelków na planszy (klucz: indeks kafelka, wartość: pozycja w pikselach). */
    private final Map<Point, Vector2> tilePositions;

    /** Model reprezentujący Pac-Mana. */
    private PacMan_Model pacManModel;

    /** Lista modeli duszków. */
    private List<Ghost_Model> ghostModels;

    // ---- KONTROLERY POSTACI ----

    /** Kontroler Pac-Mana. */
    private PacMan_Controller pacManController;

    /** Lista kontrolerów duszków. */
    private final List<Ghost_Controller> ghostControllers;

    /** Flaga aktywacji ruchu duszków – zaczynają się poruszać, gdy Pac-Man się ruszy. */
    private boolean ghostsActive = false;

    // ---- PARAMETRY STANU DUCHA "FRIGHTENED" ----

    /** Czas trwania stanu frightened dla duszków (w sekundach). */
    private static final float FRIGHTENED_DURATION = 10f;

    /** Mnożnik punktacji dla duszków – zwiększa się przy kolejnych zjedzonych duszkach. */
    private int ghostMultiplier = 200;

    // ---- POLA OBSŁUGUJĄCE ANIMACJĘ ZAKOŃCZENIA ETAPU ----

    /** Flaga informująca, czy etap został zakończony. */
    private boolean stageComplete = false;

    /** Timer trwania animacji zakończenia etapu (w sekundach). */
    private float stageCompleteAnimationTimer = 0f;

    /** Czas trwania animacji zakończenia etapu – 2 sekundy. */
    private final float STAGE_COMPLETE_ANIMATION_DURATION = 2.0f;

    /** Interwał migania barier (w sekundach). */
    private final float flashInterval = 0.3f;

    /** Timer migania barier. */
    private float flashTimer = 0f;

    /** Flaga wskazująca, czy aktualnie migają (flash) bariery. */
    private boolean flashOn = false;

    /**
     * Mapa zapamiętująca oryginalne kolory barier (poza drzwiami),
     * aby można było przywrócić ich wygląd po animacji.
     */
    private final Map<Barrier_Model, Color> barrierOriginalColors = new HashMap<>();

    // ---- KONSTRUKTOR ----

    /**
     * Konstruktor kontrolera gry.
     *
     * <p>Inicjalizuje modele (grid, UI), wyszukuje Pac-Mana oraz duszki w gridzie,
     * tworzy kontrolery dla postaci i przekazuje callback do PacMan_Controller,
     * który ustawia stan frightened u duszków po zjedzeniu power-upa.</p>
     *
     * @param gridModel Model planszy gry.
     * @param uiModel   Model interfejsu użytkownika.
     */
    public Game_Controller(Grid_Model gridModel, UserInterface_Model uiModel) {
        this.gridModel = gridModel;
        this.uiModel = uiModel;
        this.tilePositions = gridModel.getTilesPositions();

        pacManModel = findPacManInGrid();
        ghostModels = findGhostsInGrid();

        // Przekazujemy callback, który wywoła metodę powerUpEaten() po zjedzeniu power-upa.
        pacManController = new PacMan_Controller(pacManModel, gridModel, uiModel, this::powerUpEaten);

        ghostControllers = new ArrayList<>();
        for (Ghost_Model ghost : ghostModels) {
            ghostControllers.add(new Ghost_Controller(ghost, gridModel, pacManModel));
        }
    }

    // ---- OBSŁUGA WEJŚCIA ----

    /**
     * Obsługuje wejście użytkownika.
     *
     * <p>Wejście jest obsługiwane tylko, gdy nie trwa animacja zakończenia etapu.</p>
     */
    public void handleInput() {
        if (!stageComplete) {
            pacManController.handleInput();
        }
    }

    // ---- GŁÓWNA METODA AKTUALIZACJI ----

    /**
     * Aktualizuje stan gry.
     *
     * <p>Metoda wykonuje następujące operacje:
     * <ul>
     *   <li>Obsługuje animację zakończenia etapu – miganie barier i zatrzymanie ruchu Pac-Mana.</li>
     *   <li>Jeśli Pac-Man umiera, sprawdza, czy animacja śmierci dobiegła końca i restartuje poziom.</li>
     *   <li>Aktualizuje kontroler Pac-Mana oraz, jeśli aktywowane, kontrolery duszków.</li>
     *   <li>Sprawdza kolizje Pac-Mana z duszkami i odpowiednio aktualizuje stan (umieranie lub jedzenie duszka).</li>
     *   <li>Sprawdza, czy zostały jakieś kropki do zebrania – jeśli nie, uruchamia animację zakończenia etapu.</li>
     *   <li>Aktualizuje highscore oraz przyznaje dodatkowe życie za osiągnięcie 10 000 punktów.</li>
     * </ul>
     * </p>
     *
     * @param delta Czas, który upłynął od ostatniej aktualizacji (w sekundach).
     */
    public void update(float delta) {
        // Jeśli trwa animacja zakończenia etapu, zatrzymujemy ruch Pac-Mana i wykonujemy animację flashingu barier.
        if (stageComplete) {
            pacManModel.setDirection(0, 0);
            stageCompleteAnimationTimer -= delta;
            flashTimer -= delta;
            if (flashTimer <= 0) {
                flashTimer = flashInterval;
                flashOn = !flashOn;
                updateBarrierFlash(flashOn);
            }
            if (stageCompleteAnimationTimer <= 0) {
                resetStage();
                stageComplete = false;
            }
            return; // Pomijamy dalszą aktualizację, gdy trwa animacja.
        }

        // Jeśli Pac-Man umiera, sprawdź czy animacja śmierci dobiegła końca i wykonaj restart poziomu.
        if (pacManModel.isDying()) {
            if (pacManModel.getCurrentFrame() >= 11) {
                restart();
            }
            return;
        }

        pacManController.update(delta);

        // Aktywacja ruchu duszków następuje po pierwszym ruchu Pac-Mana.
        if (!ghostsActive && pacManController.hasMoved()) {
            ghostsActive = true;
        }
        if (ghostsActive) {
            for (Ghost_Controller ghostController : ghostControllers) {
                ghostController.update(delta);
            }
        }

        // Sprawdzamy kolizje Pac-Mana z duszkami.
        for (Ghost_Controller ghostController : ghostControllers) {
            Ghost_Model ghost = ghostController.getGhostModel();
            if (pacManModel.getBounds().overlaps(ghost.getBounds())) {
                // Jeśli duszek nie został jeszcze aktywowany (np. w trakcie opóźnienia aktywacji),
                // Pac-Man umiera.
                if (!ghostController.isActivated()) {
                    pacManModel.setDying(true);
                    pacManModel.setCurrentFrame(0);
                    pacManModel.setDirection(0, 0);
                    removeAllGhostsFromMap();
                    return;
                }
                // Jeśli duszek jest frightened i nie został jeszcze zjedzony,
                // oznacz go jako zjedzonego, resetuj stan frightened oraz przywróć pozycję startową.
                if (ghost.isFrightened() && !ghost.isEaten()) {
                    ghost.setEaten(true);
                    ghost.setFrightened(false);
                    Vector2 startDisplay = ghost.getStartDisplayPosition();
                    ghost.setDisplayPosition(startDisplay.x, startDisplay.y);
                    ghostController.resetActivation();

                    int currentScore = Integer.parseInt(uiModel.getScoreValue());
                    uiModel.setScoreValue(currentScore + ghostMultiplier);
                    ghostMultiplier = (ghostMultiplier < 1600) ? ghostMultiplier * 2 : ghostMultiplier;
                }
                // Jeśli duszek nie jest frightened i nie został zjedzony – Pac-Man umiera.
                else if (!ghost.isFrightened() && !ghost.isEaten()) {
                    pacManModel.setDying(true);
                    pacManModel.setCurrentFrame(0);
                    pacManModel.setDirection(0, 0);
                    removeAllGhostsFromMap();
                    return;
                }
            }
        }

        // Sprawdzamy, czy na planszy pozostały jakieś kropki.
        checkAllDotsCollected();

        // Aktualizacja highscore: jeśli aktualny wynik przekracza highscore, ustaw nowy highscore.
        int currentScore = Integer.parseInt(uiModel.getScoreValue());
        int currentHighScore = uiModel.getHighScoreValue().isEmpty() ? 0 : Integer.parseInt(uiModel.getHighScoreValue());
        if (currentScore > currentHighScore) {
            uiModel.setHighScoreValue(currentScore);
        }

        // Logika przyznawania dodatkowego życia za 10 000 punktów.
        if (!extraLifeAwarded && currentScore >= 10000) {
            extraLifeAwarded = true;
            uiModel.setLivesValue(uiModel.getLivesValue() + 1);
        }
    }

    // ---- METODY ODPOWIEDZIALNE ZA POWER-UP ----

    /**
     * Metoda wywoływana, gdy Pac-Man zje power-up (dużą kropkę).
     *
     * <p>Resetuje mnożnik punktacji i ustawia stan frightened u wszystkich aktywowanych duszków.</p>
     */
    private void powerUpEaten() {
        ghostMultiplier = 200;
        for (Ghost_Controller ghostController : ghostControllers) {
            Ghost_Model ghost = ghostController.getGhostModel();
            if (ghostController.isActivated() && !ghost.isEaten()) {
                ghost.setFrightened(true);
                ghost.setFrightenedTimer(FRIGHTENED_DURATION);
            }
        }
    }

    // ---- METODY WYSZUKUJĄCE OBIEKTY W GRIDZIE ----

    /**
     * Wyszukuje w gridzie obiekt Pac-Mana.
     *
     * @return PacMan_Model jeśli znaleziono, w przeciwnym razie {@code null}.
     */
    private PacMan_Model findPacManInGrid() {
        for (int x = 0; x < gridModel.getCols(); x++) {
            for (int y = 0; y < gridModel.getRows(); y++) {
                GameObject_Model obj = gridModel.getObjectAt(x, y);
                if (obj instanceof PacMan_Model) {
                    return (PacMan_Model) obj;
                }
            }
        }
        return null;
    }

    /**
     * Wyszukuje w gridzie wszystkie obiekty duszków.
     *
     * @return Lista modeli duszków.
     */
    private List<Ghost_Model> findGhostsInGrid() {
        List<Ghost_Model> ghosts = new ArrayList<>();
        for (int x = 0; x < gridModel.getCols(); x++) {
            for (int y = 0; y < gridModel.getRows(); y++) {
                GameObject_Model obj = gridModel.getObjectAt(x, y);
                if (obj instanceof Ghost_Model) {
                    ghosts.add((Ghost_Model) obj);
                }
            }
        }
        return ghosts;
    }

    // ---- METODA RESTARTU / RESETU ETAPU ----

    /**
     * Restartuje poziom w przypadku śmierci Pac-Mana.
     *
     * <p>Jeśli gracz ma jeszcze życia, odejmuje jedno i resetuje pozycję Pac-Mana oraz duszków.
     * W przeciwnym razie ustawia stan GAME OVER w modelu UI.</p>
     */
    private void restart() {
        int lives = uiModel.getLivesValue();
        if (lives > 0) {
            if (lives == 1) {
                uiModel.setLivesValue(0);
                // Dodatkowa logika GAME OVER, jeśli to wymagane
            } else {
                uiModel.setLivesValue(lives - 1);
                Point pacPos = Objects.requireNonNull(findPacManInGrid()).getGridPosition();
                gridModel.setObjectAt(pacPos.x, pacPos.y, null);
                PacMan_Model pacMan = new PacMan_Model(
                    PacMan_Helper.indexToPosition(13, 7).x + 12,
                    PacMan_Helper.indexToPosition(13, 7).y,
                    13,
                    7
                );
                gridModel.setObjectAt(13, 7, pacMan);
                pacManModel = pacMan;
                pacManController = new PacMan_Controller(pacManModel, gridModel, uiModel, this::powerUpEaten);

                // Usunięcie starych pozycji duszków.
                gridModel.setObjectAt(13, 19, null);
                gridModel.setObjectAt(9, 16, null);
                gridModel.setObjectAt(13, 13, null);
                gridModel.setObjectAt(18, 16, null);

                // Utworzenie nowych modeli duszków z odpowiednimi parametrami startowymi.
                Ghost_Model blinky = new Ghost_Model(
                    PacMan_Helper.indexToPosition(13, 19).x + 12,
                    PacMan_Helper.indexToPosition(13, 19).y,
                    13,
                    19,
                    Ghost_Type.BLINKY,
                    0
                );
                Ghost_Model inky = new Ghost_Model(
                    PacMan_Helper.indexToPosition(9, 16).x,
                    PacMan_Helper.indexToPosition(9, 16).y,
                    9,
                    16,
                    Ghost_Type.INKY,
                    2
                );
                Ghost_Model pinky = new Ghost_Model(
                    PacMan_Helper.indexToPosition(13, 13).x + 12,
                    PacMan_Helper.indexToPosition(13, 13).y,
                    13,
                    13,
                    Ghost_Type.PINKY,
                    1
                );
                Ghost_Model clyde = new Ghost_Model(
                    PacMan_Helper.indexToPosition(18, 16).x,
                    PacMan_Helper.indexToPosition(18, 16).y,
                    18,
                    16,
                    Ghost_Type.CLYDE,
                    3
                );

                gridModel.setObjectAt(13, 19, blinky);
                gridModel.setObjectAt(9, 16, inky);
                gridModel.setObjectAt(13, 13, pinky);
                gridModel.setObjectAt(18, 16, clyde);

                ghostModels.clear();
                ghostModels.add(blinky);
                ghostModels.add(inky);
                ghostModels.add(pinky);
                ghostModels.add(clyde);

                ghostControllers.clear();
                ghostControllers.add(new Ghost_Controller(blinky, gridModel, pacManModel));
                ghostControllers.add(new Ghost_Controller(inky, gridModel, pacManModel));
                ghostControllers.add(new Ghost_Controller(pinky, gridModel, pacManModel));
                ghostControllers.add(new Ghost_Controller(clyde, gridModel, pacManModel));

                ghostsActive = false;
                pacManModel.setDying(false);
            }
        } else {
            uiModel.setGameOver(true);
        }
    }

    /**
     * Usuwa wszystkie duszki z mapy (przypisując im nieprawidłowe pozycje).
     */
    private void removeAllGhostsFromMap() {
        for (Ghost_Controller ghostController : ghostControllers) {
            Ghost_Model ghost = ghostController.getGhostModel();
            ghost.setGridPosition(-1, -1);
            ghost.setDisplayPosition(-100, -100);
        }
    }

    // ---- LOGIKA DOTYCZĄCA ZBIERANIA KROPEK ----

    /**
     * Sprawdza, czy na planszy pozostały jeszcze kropki.
     *
     * <p>Jeśli żadna kropka nie została znaleziona, uruchamia animację zakończenia etapu,
     * zatrzymując ruch Pac-Mana i miganie barier.</p>
     */
    private void checkAllDotsCollected() {
        boolean dotsRemaining = false;
        for (int x = 0; x < gridModel.getCols(); x++) {
            for (int y = 0; y < gridModel.getRows(); y++) {
                GameObject_Model obj = gridModel.getObjectAt(x, y);
                if (obj instanceof Dot_Model) {
                    dotsRemaining = true;
                    break;
                }
            }
            if (dotsRemaining) {
                break;
            }
        }
        if (!dotsRemaining && !stageComplete) {
            // Uruchamiamy animację zakończenia etapu.
            stageComplete = true;
            stageCompleteAnimationTimer = STAGE_COMPLETE_ANIMATION_DURATION;
            flashTimer = 0;
            flashOn = false;
            pacManModel.setDirection(0, 0);
            removeAllGhostsFromMap();
            // Zapamiętujemy oryginalne kolory barier (tylko tych, które nie są drzwiami).
            barrierOriginalColors.clear();
            for (int x = 0; x < gridModel.getCols(); x++) {
                for (int y = 0; y < gridModel.getRows(); y++) {
                    GameObject_Model obj = gridModel.getObjectAt(x, y);
                    if (obj instanceof Barrier_Model barrier) {
                        if (barrier.getType() != Barrier_Type.DOOR) {
                            barrierOriginalColors.put(barrier, barrier.getTextureColor());
                        }
                    }
                }
            }
        }
    }

    /**
     * Resetuje etap, inicjując ponownie mapę na podstawie konfiguracji etapu.
     */
    private void resetStage() {
        // Czyścimy grid.
        for (int x = 0; x < gridModel.getCols(); x++) {
            for (int y = 0; y < gridModel.getRows(); y++) {
                gridModel.setObjectAt(x, y, null);
            }
        }
        // Inicjalizacja mapy z pliku JSON.
        StageInitializer.initializeMap(gridModel, "assets/stage.json");

        pacManModel = findPacManInGrid();
        ghostModels = findGhostsInGrid();

        pacManController = new PacMan_Controller(pacManModel, gridModel, uiModel, this::powerUpEaten);

        ghostControllers.clear();
        for (Ghost_Model ghost : ghostModels) {
            ghostControllers.add(new Ghost_Controller(ghost, gridModel, pacManModel));
        }

        ghostsActive = false;
        ghostMultiplier = 200;
    }

    // ---- LOGIKA MIGANIA BARIER ----

    /**
     * Aktualizuje kolor barier, tworząc efekt migania (flash).
     *
     * <p>Dla każdej bariery (z wyjątkiem drzwi) ustawia kolor na biały, gdy flash jest aktywny,
     * a gdy nie – przywraca oryginalny kolor zapisany w mapie barrierOriginalColors.</p>
     *
     * @param flashOn {@code true} jeśli flash ma być włączony, {@code false} w przeciwnym razie.
     */
    private void updateBarrierFlash(boolean flashOn) {
        for (int x = 0; x < gridModel.getCols(); x++) {
            for (int y = 0; y < gridModel.getRows(); y++) {
                GameObject_Model obj = gridModel.getObjectAt(x, y);
                if (obj instanceof Barrier_Model barrier) {
                    if (barrier.getType() == Barrier_Type.DOOR) {
                        continue;
                    }
                    if (flashOn) {
                        barrier.setTextureColor(Color.WHITE);
                    } else {
                        Color original = barrierOriginalColors.get(barrier);
                        if (original != null) {
                            barrier.setTextureColor(original);
                        }
                    }
                }
            }
        }
    }
}
