package io.github.PacMan.Game_Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.PacMan.GameProperties;
import io.github.PacMan.Menu.MenuStatement;
import io.github.PacMan.PacMan;
import io.github.PacMan.PacMan_Helper;

import java.time.Year;

/**
 * Ekran tytułowy gry Pac-Man.
 *
 * <p>Klasa zarządza wyświetlaniem loga (tytułu) oraz menu startowego. Odpowiada za:
 * <ul>
 *   <li>Inicjalizację i ładowanie zasobów (SpriteBatch, fonty, tekstury, menu).</li>
 *   <li>Renderowanie tła, loga, menu oraz informacji o aplikacji.</li>
 *   <li>Obsługę wejścia myszy (hover i kliknięcia) dla opcji menu.</li>
 * </ul>
 * </p>
 */
public class Title_Screen implements Screen {

    /** Referencja do głównego obiektu gry. */
    private final PacMan game;

    /** SpriteBatch używany do renderowania wszystkich elementów ekranu. */
    private SpriteBatch batch;

    /** Czcionka używana do renderowania tytułu gry. */
    private BitmapFont logoFont;

    /** Czcionka używana do renderowania tekstów menu. */
    private BitmapFont menuFont;

    /** Czcionka używana do renderowania informacji (np. wersji, copyright). */
    private BitmapFont infoFont;

    /** Tekstura tła wyświetlana za logiem. */
    private Texture logoBackground;

    /** Obiekt reprezentujący menu startowe (lista opcji oraz ich konfiguracja). */
    private MenuStatement menuStatement;

    /**
     * Konstruktor ekranu tytułowego.
     *
     * @param game Główny obiekt gry PacMan.
     */
    public Title_Screen(PacMan game) {
        this.game = game;
    }

    /**
     * Metoda wywoływana po przejściu na ten ekran.
     *
     * <p>Inicjalizuje wszystkie niezbędne obiekty i zasoby (SpriteBatch, fonty, tekstury, menu),
     * dzięki czemu ekran jest gotowy do renderowania.</p>
     */
    @Override
    public void show() {
        initSpriteBatch();
        initFonts();
        initTextures();
        initMenu();
    }

    /**
     * Główna metoda renderująca, wywoływana w pętli gry.
     *
     * <p>Algorytm działania:
     * <ul>
     *   <li>Najpierw aktualizuje się logika ekranu (obsługa pozycji myszy, kliknięć itp.) poprzez metodę {@link #update(float)}.</li>
     *   <li>Następnie rozpoczyna się rysowanie:
     *     <ul>
     *       <li>Rysowane jest tło loga przy użyciu {@link #renderBackground()}.</li>
     *       <li>Rysowane są informacje o aplikacji przy użyciu {@link #renderInfo()}.</li>
     *       <li>Rysowane jest menu startowe poprzez {@link #renderMenu()}.</li>
     *       <li>Rysowany jest tytuł gry (logo) z efektem cienia i obrysu przy użyciu {@link #renderLogo()}.</li>
     *     </ul>
     *   </li>
     * </ul>
     * </p>
     *
     * @param delta Czas (w sekundach) od ostatniej klatki.
     */
    @Override
    public void render(float delta) {
        update(delta);

        batch.begin();
        renderBackground();
        renderInfo();
        renderMenu();
        renderLogo();
        batch.end();
    }

    /**
     * Metoda wywoływana przy zmianie rozmiaru okna/ekranu.
     *
     * <p>W tej grze rozmiar ekranu jest stały, więc metoda nie zawiera implementacji.</p>
     *
     * @param width  Nowa szerokość okna.
     * @param height Nowa wysokość okna.
     */
    @Override
    public void resize(int width, int height) {
        // Brak implementacji - rozmiar okna jest stały.
    }

    @Override
    public void pause() {
        // Ekran tytułowy nie wymaga obsługi pauzy.
    }

    @Override
    public void resume() {
        // Ekran tytułowy nie wymaga obsługi wznowienia.
    }

    @Override
    public void hide() {
        // Metoda hide nie jest wykorzystywana w tym ekranie.
    }

    /**
     * Metoda wywoływana przy niszczeniu ekranu.
     *
     * <p>Zwalnia zasoby (fonty, tekstury, menu, SpriteBatch), aby uniknąć wycieków pamięci.</p>
     */
    @Override
    public void dispose() {
        logoFont.dispose();
        menuFont.dispose();
        logoBackground.dispose();
        menuStatement.dispose();
        batch.dispose();
    }

    // ------------------------
    // Metody pomocnicze
    // ------------------------

    /**
     * Inicjalizuje SpriteBatch, jeśli jeszcze nie został utworzony.
     */
    private void initSpriteBatch() {
        if (batch == null) {
            batch = new SpriteBatch();
        }
    }

    /**
     * Ładuje fonty niezbędne do wyświetlania tytułu, menu i informacji.
     *
     * <p>Wykorzystuje metodę {@link PacMan_Helper#loadFont(String, int, Color, Color, int)} do tworzenia fontów.</p>
     */
    private void initFonts() {
        if (logoFont == null) {
            logoFont = PacMan_Helper.loadFont("fonts/PacFontGood.ttf",
                GameProperties.TitleScreenProps.TITLE_SIZE,
                Color.WHITE,
                Color.BLACK,
                2);
        }
        if (menuFont == null) {
            menuFont = PacMan_Helper.loadFont("fonts/Emulogic.ttf",
                GameProperties.TitleScreenProps.MENU_ITEM_SIZE,
                Color.WHITE,
                Color.BLACK,
                0);
        }
        if (infoFont == null) {
            infoFont = PacMan_Helper.loadFont("fonts/Emulogic.ttf",
                GameProperties.TitleScreenProps.INFO_SIZE,
                Color.WHITE,
                Color.BLACK,
                0);
        }
    }

    /**
     * Ładuje tekstury niezbędne dla ekranu tytułowego.
     *
     * <p>Przykładowo ładuje tło loga z pliku "title_screen/logo-background.png".</p>
     */
    private void initTextures() {
        if (logoBackground == null) {
            logoBackground = new Texture("title_screen/logo-background.png");
        }
    }

    /**
     * Tworzy i konfiguruje menu startowe.
     *
     * <p>Metoda:
     * <ul>
     *   <li>Tworzy obiekt MenuStatement z określonymi parametrami (pozycja, font, kolory, wyrównanie, offset).</li>
     *   <li>Dodaje opcje menu: "new game", "options", "exit".</li>
     *   <li>Ustawia pozycję menu na ekranie i dezaktywuje opcję "options".</li>
     * </ul>
     * </p>
     */
    private void initMenu() {
        if (menuStatement == null) {
            menuStatement = new MenuStatement(
                new Vector2(0, 0),
                menuFont,
                GameProperties.TitleScreenProps.MENU_TEXT_COLOR,
                GameProperties.TitleScreenProps.MENU_HOVER_COLOR,
                GameProperties.TitleScreenProps.MENU_ALIGNMENT_TYPE,
                GameProperties.TitleScreenProps.MENU_ITEMS_OFFSET,
                true
            );
            menuStatement.addOption("new game", () -> game.setScreen(new Game_Screen(game)));
            menuStatement.addOption("options", null);
            menuStatement.addOption("exit", () -> Gdx.app.exit());
            menuStatement.setPosition(
                PacMan_Helper.simplyVerticalCenter(GameProperties.GameSettings.SCREEN_RESOLUTION.x, menuStatement.getWidth()),
                GameProperties.TitleScreenProps.MENU_POSITION_Y
            );
            // Wyłączenie opcji "options"
            menuStatement.getMenuOption(1).setEnabled(false);
        }
    }

    /**
     * Aktualizuje stan logiczny ekranu.
     *
     * <p>Metoda:
     * <ul>
     *   <li>Pobiera aktualną pozycję myszy w układzie współrzędnych gry.</li>
     *   <li>Jeśli nastąpiło kliknięcie (justTouched), wywołuje metodę {@link MenuStatement#handleClick(Vector2)}.</li>
     * </ul>
     * </p>
     *
     * @param delta Czas od ostatniej klatki (w sekundach).
     */
    private void update(float delta) {
        Vector2 mousePos = getMousePositionInGameCoords();
        if (Gdx.input.justTouched()) {
            menuStatement.handleClick(mousePos);
        }
    }

    /**
     * Rysuje tło loga.
     *
     * <p>Oblicza wyśrodkowaną pozycję X dla tła i rysuje je w ustalonej pozycji Y.</p>
     */
    private void renderBackground() {
        float x = PacMan_Helper.simplyVerticalCenter(
            GameProperties.GameSettings.SCREEN_RESOLUTION.x,
            logoBackground.getWidth()
        );
        batch.draw(logoBackground, x, GameProperties.TitleScreenProps.LOGO_POSITION_Y);
    }

    /**
     * Rysuje logo (tytuł gry) z efektem cienia i obrysu.
     *
     * <p>Oblicza wyśrodkowaną pozycję X na podstawie szerokości tekstu oraz ustala pozycję Y powyżej tła,
     * a następnie wywołuje metodę {@link #drawLogoWithShadowAndOutline(float, float)}.</p>
     */
    private void renderLogo() {
        float x = PacMan_Helper.simplyVerticalCenter(
            GameProperties.GameSettings.SCREEN_RESOLUTION.x,
            PacMan_Helper.getTextWidth(logoFont, GameProperties.TitleScreenProps.LOGO_TITLE)
        );
        float y = GameProperties.TitleScreenProps.LOGO_POSITION_Y + 95;
        drawLogoWithShadowAndOutline(x, y);
    }

    /**
     * Rysuje menu startowe.
     *
     * <p>Pobiera aktualną pozycję myszy w układzie gry i przekazuje ją do MenuStatement,
     * który obsługuje wyróżnienie opcji pod myszką (hover) oraz kliknięcia.</p>
     */
    private void renderMenu() {
        Vector2 mousePos = getMousePositionInGameCoords();
        menuStatement.render(batch, mousePos);
    }

    /**
     * Rysuje informacje o aplikacji.
     *
     * <p>Wyświetla napisy takie jak copyright oraz wersja aplikacji,
     * umieszczone w odpowiednich miejscach ekranu.</p>
     */
    private void renderInfo() {
        int offset = 5;
        String currentYear = String.valueOf(Year.now().getValue());
        String copyrights = "©" + currentYear + " poiw-libgdx pacman proj.";
        String previous = "version: " + GameProperties.TitleScreenProps.APPLICATION_VERSION;
        infoFont.draw(batch, copyrights, offset, PacMan_Helper.getTextHeight(infoFont, copyrights) + offset);
        infoFont.draw(batch, previous,
            GameProperties.GameSettings.SCREEN_RESOLUTION.x - PacMan_Helper.getTextWidth(infoFont, previous) - offset,
            PacMan_Helper.getTextHeight(infoFont, previous) + offset);
    }

    /**
     * Rysuje tytuł gry z efektem cienia i obrysu.
     *
     * <p>Metoda rysuje kilka warstw tekstu:
     * <ul>
     *   <li>Biały "outline" przesunięty o kilka pikseli.</li>
     *   <li>Ciemny cień przesunięty w przeciwnym kierunku.</li>
     *   <li>Główny napis w kolorze żółtym.</li>
     * </ul>
     * </p>
     *
     * @param x Pozycja X wyśrodkowana względem ekranu.
     * @param y Pozycja Y, ustalona powyżej tła.
     */
    private void drawLogoWithShadowAndOutline(float x, float y) {
        int whiteOffset = 3;
        int darkOffset = 5;

        // Rysowanie białego outline
        logoFont.setColor(Color.WHITE);
        logoFont.draw(batch, GameProperties.TitleScreenProps.LOGO_TITLE, x - whiteOffset, y + whiteOffset);

        // Rysowanie ciemnego cienia
        logoFont.setColor(Color.DARK_GRAY);
        logoFont.draw(batch, GameProperties.TitleScreenProps.LOGO_TITLE, x + darkOffset, y - darkOffset);

        // Rysowanie głównego żółtego napisu
        logoFont.setColor(Color.YELLOW);
        logoFont.draw(batch, GameProperties.TitleScreenProps.LOGO_TITLE, x, y);
    }

    /**
     * Pobiera pozycję myszy przeliczając ją z układu okna (0,0 w lewym-górnym rogu)
     * na układ "gry" (0,0 w lewym-dolnym rogu), zgodnie z LibGDX.
     *
     * @return Wektor z przeliczoną pozycją myszy.
     */
    private Vector2 getMousePositionInGameCoords() {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();
        mouseY = GameProperties.GameSettings.SCREEN_RESOLUTION.y - mouseY;
        return new Vector2(mouseX, mouseY);
    }
}
