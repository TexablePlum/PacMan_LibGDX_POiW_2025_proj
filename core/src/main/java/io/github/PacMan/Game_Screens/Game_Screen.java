package io.github.PacMan.Game_Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.PacMan.Game_Objects.Controllers.Game_Controller;
import io.github.PacMan.Game_Objects.Models.Grid_Model;
import io.github.PacMan.Game_Objects.Models.UserInterface_Model;
import io.github.PacMan.Game_Objects.Views.Grid_View;
import io.github.PacMan.Game_Objects.Views.UserInterface_View;
import io.github.PacMan.PacMan;
import io.github.PacMan.Stage_Interpreter.StageInitializer;

/**
 * Ekran gry (Game Screen) w grze Pac-Man.
 *
 * <p>Klasa ta odpowiada za:
 * <ul>
 *   <li>Inicjalizację modeli, widoków oraz kontrolera głównego gry (grid, interfejsu użytkownika).</li>
 *   <li>Obsługę logiki gry, renderowanie obiektów oraz przejścia między ekranami (np. powrót do ekranu tytułowego po GAME OVER).</li>
 *   <li>Zarządzanie opóźnionym przejściem do ekranu tytułowego po utracie ostatniego życia.</li>
 * </ul>
 * </p>
 */
public class Game_Screen implements Screen {
    /** Główny obiekt gry (Game) przekazany przy tworzeniu ekranu. */
    private final Game game;

    /** SpriteBatch używany do renderowania wszystkich elementów ekranu. */
    private final SpriteBatch batch;

    /** Model interfejsu użytkownika zawierający m.in. wynik, highscore, liczbę żyć oraz flagę game over. */
    private UserInterface_Model uiModel;

    /** Widok interfejsu użytkownika odpowiedzialny za renderowanie UI. */
    private UserInterface_View uiView;

    /** Model planszy (grid) zawierający obiekty gry (kropki, bariery, postacie). */
    private Grid_Model gridModel;

    /** Widok gridu odpowiedzialny za renderowanie obiektów umieszczonych w gridzie. */
    private Grid_View gridView;

    /** Kontroler gry, który zarządza logiką gry, aktualizacją modeli oraz obsługą wejścia. */
    private Game_Controller gridController;

    // ---- ZMIENNE DO PRZEJŚCIA PO GAME OVER ----

    /** Timer opóźnienia przed przejściem do ekranu tytułowego po GAME OVER. */
    private float gameOverTimer = 0f;

    /** Stała określająca opóźnienie (w sekundach) przed przejściem do ekranu tytułowego po GAME OVER. */
    private static final float GAME_OVER_DELAY = 3f;

    /** Flaga sygnalizująca, że przejście do ekranu tytułowego zostało już uruchomione. */
    private boolean gameOverTransitionTriggered = false;

    /**
     * Konstruktor ekranu gry.
     *
     * @param game Główny obiekt gry (Game), który jest wykorzystywany do zmiany ekranów.
     */
    public Game_Screen(Game game) {
        this.game = game;
        batch = new SpriteBatch();
    }

    /**
     * Metoda wywoływana przy przejściu na ten ekran.
     *
     * <p>Inicjalizuje modele UI oraz gridu, wczytuje mapę etapu na podstawie pliku JSON,
     * tworzy kontroler gry oraz widok gridu.</p>
     */
    @Override
    public void show() {
        uiModel = new UserInterface_Model(3);
        uiView = new UserInterface_View();
        gridModel = new Grid_Model();
        StageInitializer.initializeMap(gridModel, "assets/stage.json");
        gridController = new Game_Controller(gridModel, uiModel);
        gridView = new Grid_View(gridModel);
    }

    /**
     * Główna metoda renderująca, wywoływana w pętli gry.
     *
     * <p>Algorytm:
     * <ul>
     *   <li>Renderuje interfejs użytkownika (UI) przy użyciu UserInterface_View.</li>
     *   <li>Obsługuje wejście dla gry oraz aktualizuje logikę gry przez Game_Controller.</li>
     *   <li>Renderuje obiekty znajdujące się w gridzie przy użyciu Grid_View.</li>
     *   <li>Jeśli liczba żyć osiągnie 0, uruchamia się opóźniony timer, po którym następuje przejście do ekranu tytułowego.</li>
     * </ul>
     * </p>
     *
     * @param delta Czas (w sekundach) od ostatniej klatki.
     */
    @Override
    public void render(float delta) {
        // Renderowanie interfejsu użytkownika.
        uiView.render(batch, uiModel, delta);

        // Obsługa wejścia oraz aktualizacja logiki gry.
        gridController.handleInput();
        gridController.update(delta);

        // Renderowanie obiektów w gridzie.
        gridView.render(batch, delta);

        // Jeśli Pac-Man nie ma już żyć, rozpoczynamy timer przejścia do ekranu tytułowego.
        if (uiModel.getLivesValue() == 0 && !gameOverTransitionTriggered) {
            gameOverTimer += delta;
            if (gameOverTimer >= GAME_OVER_DELAY) {
                gameOverTransitionTriggered = true;
                // Przejście do ekranu tytułowego.
                game.setScreen(new Title_Screen((PacMan) game));
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // Brak implementacji - rozmiar ekranu jest stały.
    }

    @Override
    public void pause() {
        // Brak implementacji.
    }

    @Override
    public void resume() {
        // Brak implementacji.
    }

    @Override
    public void hide() {
        // Brak implementacji.
    }

    /**
     * Metoda wywoływana przy zamknięciu ekranu.
     *
     * <p>Zwalnia zasoby: interfejs użytkownika, widok gridu oraz SpriteBatch.</p>
     */
    @Override
    public void dispose() {
        uiView.dispose();
        gridView.dispose();
        batch.dispose();
    }
}
