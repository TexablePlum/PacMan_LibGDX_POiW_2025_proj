package io.github.PacMan.Game_Objects.Views;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.PacMan.GameProperties;
import io.github.PacMan.Game_Objects.Models.Ghost_Model;
import io.github.PacMan.Game_Objects.Models.Ghost_Type;

import java.util.HashMap;
import java.util.Map;

/**
 * Widok (View) duszka w grze Pac-Man.
 *
 * <p>Klasa odpowiedzialna jest za:
 * <ul>
 *   <li>Ładowanie i przechowywanie tekstur duszków dla różnych stanów:</li>
 *      <ul>
 *         <li>Normalny stan – animowany w zależności od kierunku ruchu.</li>
 *         <li>Stan frightened – duszek staje się niebieski, a następnie migający (przełączający między niebieskimi i białymi teksturami).</li>
 *      </ul>
 *   <li>Renderowanie duszka na ekranie przy użyciu odpowiednich tekstur w zależności od stanu i kierunku ruchu.</li>
 *   <li>Zwalnianie zasobów graficznych.</li>
 * </ul>
 * </p>
 */
public class Ghost_View {

    /** Tekstury dla normalnego stanu duszka, animowanych wg kierunków.
     * Klucz: Ghost_Type; wartość: dwuwymiarowa tablica tekstur,
     * gdzie pierwszy wymiar odpowiada kierunkowi (0: left, 1: right, 2: up, 3: down),
     * a drugi wymiar – klatce animacji.
     */
    private static final Map<Ghost_Type, Texture[][]> allGhostTextures = new HashMap<>();

    /** Liczba klatek animacji dla normalnego stanu (każda animacja ma 2 klatki). */
    private final int framesCount = 2;

    /** Liczba klatek animacji dla stanu frightened. */
    private static final int FRIGHTENED_FRAMES = 2;

    /** Tablica tekstur dla stanu frightened (niebieskie duszki). */
    private static final Texture[] frightenedTextures = new Texture[FRIGHTENED_FRAMES];

    /** Liczba klatek animacji dla stanu blinking (miganie – białe tekstury). */
    private static final int BLINKING_FRAMES = 2;

    /** Tablica tekstur dla stanu blinking (białe duszki). */
    private static final Texture[] blinkingTextures = new Texture[BLINKING_FRAMES];

    /** Próg czasu (w sekundach), poniżej którego duszek zaczyna migotać, czyli przełączać się między teksturami. */
    private static final float BLINKING_THRESHOLD = 3f;

    /**
     * Konstruktor widoku duszka.
     *
     * <p>W konstruktorze:
     * <ul>
     *   <li>Ładowane są tekstury dla normalnego stanu duszka – jeśli jeszcze nie zostały załadowane.</li>
     *   <li>Ładowane są tekstury dla stanu frightened (niebieskie) oraz blinking (białe).</li>
     * </ul>
     * </p>
     */
    public Ghost_View() {
        // Ładujemy tekstury dla normalnego stanu, jeśli jeszcze nie zostały załadowane.
        if (allGhostTextures.isEmpty()) {
            loadAllGhostTextures();
        }
        // Ładujemy tekstury dla stanu frightened, jeśli nie zostały wcześniej załadowane.
        if (frightenedTextures[0] == null) {
            for (int i = 0; i < FRIGHTENED_FRAMES; i++) {
                // Ścieżka do tekstur frightened – przykładowo: "ghosts/pursuit/pursuit-1.png", "ghosts/pursuit/pursuit-2.png"
                frightenedTextures[i] = new Texture("ghosts/pursuit/pursuit-" + (i + 1) + ".png");
            }
        }
        // Ładujemy tekstury dla stanu blinking, jeśli nie zostały wcześniej załadowane.
        if (blinkingTextures[0] == null) {
            for (int i = 0; i < BLINKING_FRAMES; i++) {
                // Ścieżka do tekstur blinking – przykładowo: "ghosts/pursuit/pursuit-endless-1.png", "ghosts/pursuit/pursuit-endless-2.png"
                blinkingTextures[i] = new Texture("ghosts/pursuit/pursuit-endless-" + (i + 1) + ".png");
            }
        }
    }

    /**
     * Ładuje wszystkie tekstury dla normalnego stanu duszka.
     *
     * <p>Algorytm:
     * <ul>
     *   <li>Dla każdego typu duszka (Ghost_Type) tworzy się dwuwymiarowa tablica tekstur.</li>
     *   <li>Dla każdego kierunku ruchu ("left", "right", "up", "down") i dla każdej klatki animacji:
     *       <ul>
     *         <li>Generuje się ścieżkę do pliku, np.: "ghosts/left/blinky/blinky-left-1.png".</li>
     *         <li>Ładuje teksturę z tej ścieżki i zapisuje ją w tablicy.</li>
     *       </ul>
     *   </li>
     *   <li>Gotowa tablica tekstur dla danego typu duszka jest zapisywana w mapie allGhostTextures.</li>
     * </ul>
     * </p>
     */
    private void loadAllGhostTextures() {
        // Definicja dostępnych kierunków – kolejność odpowiada indeksom w tablicy (0: left, 1: right, 2: up, 3: down)
        String[] directions = {"left", "right", "up", "down"};
        // Dla każdego typu duszka
        for (Ghost_Type ghostType : Ghost_Type.values()) {
            // Tworzymy dwuwymiarową tablicę tekstur: pierwszy wymiar – kierunek, drugi – klatka animacji.
            Texture[][] textures = new Texture[directions.length][framesCount];
            // Folder dla danego typu duszka, np. "blinky"
            String ghostFolder = ghostType.toString().toLowerCase();
            for (int i = 0; i < directions.length; i++) {
                for (int j = 0; j < framesCount; j++) {
                    // Generowanie ścieżki do pliku, np.: "ghosts/left/blinky/blinky-left-1.png"
                    String path = "ghosts/" + directions[i] + "/" + ghostFolder + "/" +
                        ghostFolder + "-" + directions[i] + "-" + (j + 1) + ".png";
                    textures[i][j] = new Texture(path);
                }
            }
            // Zapisujemy tablicę tekstur dla danego typu duszka.
            allGhostTextures.put(ghostType, textures);
        }
    }

    /**
     * Renderuje duszka na ekranie.
     *
     * <p>Algorytm renderowania zależy od stanu duszka:
     * <ul>
     *   <li>Jeśli duszek jest w stanie frightened, renderowana jest animacja stanu frightened:
     *       <ul>
     *         <li>Jeżeli pozostały czas frightened jest poniżej progu blinking (BLINKING_THRESHOLD),
     *             przełączane są tekstury między niebieskimi (frightenedTextures) i białymi (blinkingTextures) w zależności od blinkFrame.</li>
     *         <li>W przeciwnym razie wyświetlana jest standardowa animacja frightened (tylko niebieskie tekstury).</li>
     *       </ul>
     *   </li>
     *   <li>Jeśli duszek nie jest frightened, renderowana jest animacja normalna:
     *       <ul>
     *         <li>Na podstawie ostatniego kierunku ruchu (lastDirection) wybierana jest odpowiednia tablica tekstur.</li>
     *         <li>Aktualizowany jest timer animacji; gdy przekroczy zadany próg, przełączana jest klatka animacji.</li>
     *       </ul>
     *   </li>
     * </ul>
     * </p>
     *
     * @param batch     SpriteBatch używany do renderowania.
     * @param model     Model duszka, zawierający informacje o stanie, pozycji, animacji, frightened, blinking itp.
     * @param deltaTime Czas, który upłynął od ostatniej klatki (w sekundach).
     */
    public void render(SpriteBatch batch, Ghost_Model model, float deltaTime) {
        // Renderowanie stanu frightened
        if (model.isFrightened()) {
            // Aktualizacja animacji – gdy duszek stoi, resetujemy klatkę do 0.
            if (!model.isMoving()) {
                model.setCurrentFrame(0);
            } else {
                float timer = model.getAnimationTimer() + deltaTime;
                if (timer >= GameProperties.GameAttributes.GHOST_ANIMATION_SPEED) {
                    timer = 0;
                    model.setCurrentFrame((model.getCurrentFrame() + 1) % FRIGHTENED_FRAMES);
                }
                model.setAnimationTimer(timer);
            }
            batch.begin();
            // Jeżeli pozostały czas frightened jest mniejszy lub równy progowi blinking,
            // przełączamy się między teksturami frightened a blinking.
            if (model.getFrightenedTimer() <= BLINKING_THRESHOLD) {
                int currentFrame = model.getCurrentFrame();
                Texture textureToDraw = (model.getBlinkFrame() == 1)
                    ? frightenedTextures[currentFrame]
                    : blinkingTextures[currentFrame];
                batch.draw(
                    textureToDraw,
                    model.getSpritePosition().x,
                    model.getSpritePosition().y,
                    GameProperties.GameSpriteSizes.PACMAN_SIZE,
                    GameProperties.GameSpriteSizes.PACMAN_SIZE
                );
            } else {
                // Normalna animacja frightened – tylko niebieskie tekstury.
                batch.draw(
                    frightenedTextures[model.getCurrentFrame()],
                    model.getSpritePosition().x,
                    model.getSpritePosition().y,
                    GameProperties.GameSpriteSizes.PACMAN_SIZE,
                    GameProperties.GameSpriteSizes.PACMAN_SIZE
                );
            }
            batch.end();
            return;
        }

        // Renderowanie normalne – gdy duszek nie jest frightened.
        Texture[][] textures = allGhostTextures.get(model.getType());
        int direction = model.getLastDirection();

        // Aktualizacja klatki animacji, gdy duszek się porusza.
        if (!model.isMoving()) {
            model.setCurrentFrame(0);
        } else {
            float timer = model.getAnimationTimer() + deltaTime;
            if (timer >= GameProperties.GameAttributes.GHOST_ANIMATION_SPEED) {
                timer = 0;
                int newFrame = (model.getCurrentFrame() + 1) % framesCount;
                model.setCurrentFrame(newFrame);
            }
            model.setAnimationTimer(timer);
        }

        // W przypadku nieprawidłowej wartości kierunku, ustawiamy domyślny.
        if (direction < 0 || direction >= textures.length) {
            switch (model.getType()) {
                case BLINKY -> direction = 0;
                case PINKY  -> direction = 1;
                case INKY   -> direction = 2;
                case CLYDE  -> direction = 3;
                default     -> direction = 0;
            }
        }

        batch.begin();
        batch.draw(
            textures[direction][model.getCurrentFrame()],
            model.getSpritePosition().x,
            model.getSpritePosition().y,
            GameProperties.GameSpriteSizes.PACMAN_SIZE,
            GameProperties.GameSpriteSizes.PACMAN_SIZE
        );
        batch.end();
    }

    /**
     * Zwalnia zasoby graficzne używane przez widok duszka.
     *
     * <p>Metoda iteruje przez wszystkie przechowywane tekstury (zarówno dla normalnych stanów, jak i frightened/blinking)
     * i wywołuje dispose() na każdej z nich.</p>
     */
    public static void dispose() {
        // Zwalnianie tekstur dla normalnych stanów duszka.
        for (Texture[][] textures : allGhostTextures.values()) {
            for (Texture[] directionTextures : textures) {
                for (Texture texture : directionTextures) {
                    texture.dispose();
                }
            }
        }
        allGhostTextures.clear();
        // Zwalnianie tekstur dla stanu frightened.
        for (Texture texture : frightenedTextures) {
            if (texture != null) {
                texture.dispose();
            }
        }
        // Zwalnianie tekstur dla stanu blinking.
        for (Texture texture : blinkingTextures) {
            if (texture != null) {
                texture.dispose();
            }
        }
    }
}
