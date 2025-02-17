package io.github.PacMan.Game_Objects.Views;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.PacMan.GameProperties;
import io.github.PacMan.Game_Objects.Models.PacMan_Model;

/**
 * Widok (View) postaci Pac-Man.
 *
 * <p>Klasa odpowiada za:
 * <ul>
 *   <li>Ładowanie tekstur animacji dla ruchu Pac-Mana oraz animacji śmierci.</li>
 *   <li>Renderowanie Pac-Mana w zależności od stanu – normalnego ruchu lub animacji śmierci.</li>
 *   <li>Aktualizację animacji poprzez przełączanie klatek zgodnie z zadanym tempem.</li>
 *   <li>Zwalnianie zasobów graficznych przy zamknięciu gry.</li>
 * </ul>
 * </p>
 */
public class PacMan_View {
    /** Dwuwymiarowa tablica tekstur dla animacji ruchu Pac-Mana.
     * Pierwszy wymiar odpowiada kierunkowi (kolejność: left, right, up, down),
     * a drugi – klatce animacji. */
    private final Texture[][] textures;

    /** Tablica tekstur animacji śmierci Pac-Mana. */
    private final Texture[] deathTextures;

    /** Timer używany do przełączania klatek animacji. */
    private float animationTimer;

    /**
     * Konstruktor widoku Pac-Mana.
     *
     * <p>W konstruktorze:
     * <ul>
     *   <li>Ładowane są tekstury animacji ruchu Pac-Mana przy użyciu metody loadTextures.</li>
     *   <li>Ładowane są tekstury animacji śmierci przy użyciu metody loadDeathTextures.</li>
     *   <li>Inicjalizowany jest timer animacji.</li>
     * </ul>
     * </p>
     */
    public PacMan_View() {
        this.textures = loadTextures(4);
        this.deathTextures = loadDeathTextures(12);
        this.animationTimer = 0;
    }

    /**
     * Ładuje tekstury animacji ruchu Pac-Mana.
     *
     * <p>Algorytm:
     * <ul>
     *   <li>Dla każdego kierunku (left, right, up, down) iteruje przez zadaną liczbę klatek animacji.</li>
     *   <li>Generuje ścieżkę do pliku (np. "pacman-left/1.png", "pacman-right/2.png" itd.).</li>
     *   <li>Ładuje tekstury do dwuwymiarowej tablicy.</li>
     * </ul>
     * </p>
     *
     * @param framesCount Liczba klatek animacji dla każdego kierunku.
     * @return Dwuwymiarowa tablica tekstur.
     */
    public static Texture[][] loadTextures(int framesCount) {
        String[] directions = {"left", "right", "up", "down"};
        Texture[][] textures = new Texture[directions.length][framesCount];

        for (int i = 0; i < directions.length; i++) {
            for (int j = 0; j < framesCount; j++) {
                String path = "pacman-" + directions[i] + "/" + (j + 1) + ".png";
                textures[i][j] = new Texture(path);
            }
        }
        return textures;
    }

    /**
     * Ładuje tekstury animacji śmierci Pac-Mana.
     *
     * <p>Algorytm:
     * <ul>
     *   <li>Iteruje przez zadaną liczbę klatek animacji śmierci.</li>
     *   <li>Generuje ścieżkę do pliku (np. "pacman-die/1.png", "pacman-die/2.png" itd.).</li>
     *   <li>Ładuje tekstury do tablicy.</li>
     * </ul>
     * </p>
     *
     * @param framesCount Liczba klatek animacji śmierci.
     * @return Tablica tekstur animacji śmierci.
     */
    private Texture[] loadDeathTextures(int framesCount) {
        Texture[] deathFrames = new Texture[framesCount];
        for (int i = 0; i < framesCount; i++) {
            String path = "pacman-die/" + (i + 1) + ".png";
            deathFrames[i] = new Texture(path);
        }
        return deathFrames;
    }

    /**
     * Renderuje Pac-Mana na ekranie.
     *
     * <p>Algorytm renderowania:
     * <ul>
     *   <li>Rozpoczyna batch rendering.</li>
     *   <li>Jeśli Pac-Man jest w stanie umierania, wywoływana jest metoda updateDeathAnimation() i renderowana jest animacja śmierci.</li>
     *   <li>W przeciwnym razie, wyświetlana jest standardowa animacja ruchu – tekstura jest wybierana na podstawie kierunku i aktualnej klatki animacji.</li>
     *   <li>Timer animacji jest aktualizowany, a klatka animacji przełączana zgodnie z tempem określonym w GameProperties.</li>
     *   <li>Zamyka batch rendering.</li>
     * </ul>
     * </p>
     *
     * @param batch     SpriteBatch używany do renderowania.
     * @param model     Model Pac-Mana, zawierający informacje o pozycji, kierunku, stanie ruchu i animacji.
     * @param deltaTime Czas, który upłynął od ostatniej klatki (w sekundach).
     */
    public void render(SpriteBatch batch, PacMan_Model model, float deltaTime) {
        batch.begin();
        if (model.isDying()) {
            // Odtwarzanie animacji śmierci.
            updateDeathAnimation(model, deltaTime);
            batch.draw(deathTextures[model.getCurrentFrame()],
                model.getSpritePosition().x,
                model.getSpritePosition().y,
                GameProperties.GameSpriteSizes.PACMAN_SIZE,
                GameProperties.GameSpriteSizes.PACMAN_SIZE
            );
        } else {
            // Odtwarzanie standardowej animacji ruchu.
            int direction = model.getLastDirection();
            if (model.isMoving()) {
                updateAnimation(model, deltaTime);
            }
            batch.draw(textures[direction][model.getCurrentFrame()],
                model.getSpritePosition().x,
                model.getSpritePosition().y,
                GameProperties.GameSpriteSizes.PACMAN_SIZE,
                GameProperties.GameSpriteSizes.PACMAN_SIZE
            );
        }
        batch.end();
    }

    /**
     * Aktualizuje animację ruchu Pac-Mana.
     *
     * <p>Algorytm:
     * <ul>
     *   <li>Dodaje deltaTime do timer'a animacji.</li>
     *   <li>Jeśli timer przekroczy wartość PAC_MAN_ANIMATION_SPEED, przełącza klatkę animacji (modulo liczba klatek) i resetuje timer.</li>
     *   <li>Aktualizuje currentFrame w modelu Pac-Mana.</li>
     * </ul>
     * </p>
     *
     * @param model     Model Pac-Mana.
     * @param deltaTime Czas, który upłynął od ostatniej klatki (w sekundach).
     */
    private void updateAnimation(PacMan_Model model, float deltaTime) {
        animationTimer += deltaTime;
        if (animationTimer >= GameProperties.GameAttributes.PAC_MAN_ANIMATION_SPEED) {
            animationTimer = 0;
            int newFrame = (model.getCurrentFrame() + 1) % textures[0].length;
            model.setCurrentFrame(newFrame);
        }
    }

    /**
     * Aktualizuje animację śmierci Pac-Mana.
     *
     * <p>Algorytm:
     * <ul>
     *   <li>Dodaje deltaTime do timer'a animacji.</li>
     *   <li>Jeśli timer przekroczy ustaloną wartość (np. 0.1 sekundy), przełącza klatkę animacji śmierci.</li>
     *   <li>Jeśli aktualna klatka osiągnie ostatnią, zatrzymuje animację na ostatniej klatce (można także sygnalizować zakończenie animacji).</li>
     * </ul>
     * </p>
     *
     * @param model     Model Pac-Mana.
     * @param deltaTime Czas, który upłynął od ostatniej klatki (w sekundach).
     */
    private void updateDeathAnimation(PacMan_Model model, float deltaTime) {
        animationTimer += deltaTime;
        // Zmiana klatki co 0.1 sekundy.
        if (animationTimer >= 0.1f) {
            animationTimer = 0;
            int newFrame = model.getCurrentFrame() + 1;
            if (newFrame >= deathTextures.length) {
                newFrame = deathTextures.length - 1; // Zatrzymanie animacji na ostatniej klatce.
            }
            model.setCurrentFrame(newFrame);
        }
    }

    /**
     * Zwalnia zasoby graficzne używane przez widok Pac-Mana.
     *
     * <p>Metoda iteruje przez wszystkie załadowane tekstury animacji ruchu oraz animacji śmierci
     * i wywołuje metodę dispose() na każdej z nich.</p>
     */
    public void dispose() {
        for (Texture[] directionTextures : textures) {
            for (Texture texture : directionTextures) {
                texture.dispose();
            }
        }
        for (Texture texture : deathTextures) {
            texture.dispose();
        }
    }
}
