package io.github.PacMan.Game_Objects.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.PacMan.GameProperties;
import io.github.PacMan.Game_Objects.Models.Barrier.Barrier_Model;
import io.github.PacMan.Game_Objects.Models.Barrier.Barrier_Type;
import io.github.PacMan.Game_Objects.Models.Barrier.Texture_Type;

import java.util.HashMap;
import java.util.Map;

/**
 * Widok (View) bariery w grze Pac-Man.
 *
 * <p>Klasa odpowiada za:
 * <ul>
 *   <li>Ładowanie tekstur dla różnych typów bariery oraz wariantów tekstur (Texture_Type).</li>
 *   <li>Renderowanie bariery przy użyciu odpowiedniej tekstury oraz koloru.</li>
 *   <li>Wyczyść zasoby (dispose) przy zamknięciu gry.</li>
 * </ul>
 * </p>
 */
public class Barrier_View {

    /**
     * Mapa przechowująca tekstury dla każdej bariery.
     * Klucz główny: Barrier_Type.
     * Wartość: mapa, gdzie kluczem jest Texture_Type, a wartością odpowiednia tekstura.
     */
    private Map<Barrier_Type, Map<Texture_Type, Texture>> textures;

    /**
     * Konstruktor widoku bariery.
     * Inicjalizuje mapę tekstur i wywołuje metodę ładowania tekstur.
     */
    public Barrier_View() {
        textures = new HashMap<>();
        loadTextures();
    }

    /**
     * Renderuje barierę przy użyciu przekazanego SpriteBatcha.
     *
     * <p>Algorytm:
     * <ul>
     *   <li>Pobiera mapę tekstur dla typu bariery z modelu.</li>
     *   <li>Wybiera teksturę na podstawie aktualnego Texture_Type modelu.</li>
     *   <li>Ustawia kolor (np. efekt flashingu) pobrany z modelu bariery.</li>
     *   <li>Renderuje teksturę na pozycji sprite'a bariery.</li>
     *   <li>Resetuje kolor batcha do domyślnego (biały).</li>
     * </ul>
     * </p>
     *
     * @param batch SpriteBatch używany do renderowania.
     * @param model Model bariery zawierający informacje o położeniu, typie, kolorze i wybranej teksturze.
     */
    public void render(SpriteBatch batch, Barrier_Model model) {
        Map<Texture_Type, Texture> textureMap = textures.get(model.getType());
        if (textureMap == null) {
            return;
        }

        Texture toDraw = textureMap.get(model.getTextureType());
        if (toDraw == null) {
            return;
        }

        batch.begin();
        // Ustawienie koloru bariery, który może być modyfikowany (np. flashowanie)
        batch.setColor(model.getTextureColor());
        batch.draw(
            toDraw,
            model.getSpritePosition().x,
            model.getSpritePosition().y,
            GameProperties.GameSpriteSizes.BARRIER_SIZE,
            GameProperties.GameSpriteSizes.BARRIER_SIZE
        );
        // Resetowanie koloru do domyślnego, aby nie wpływać na inne obiekty
        batch.setColor(Color.WHITE);
        batch.end();
    }

    /**
     * Zwalnia zasoby tekstur używanych przez widok bariery.
     * Należy wywołać tę metodę przy zamknięciu gry, aby uniknąć wycieków pamięci.
     */
    public void dispose() {
        for (Map<Texture_Type, Texture> map : textures.values()) {
            for (Texture texture : map.values()) {
                if (texture != null) {
                    texture.dispose();
                }
            }
        }
    }

    /**
     * Ładuje tekstury dla wszystkich typów bariery i ich wariantów.
     *
     * <p>Algorytm:
     * <ul>
     *   <li>Tworzy nową mapę tekstur.</li>
     *   <li>Dla każdego typu bariery (Barrier_Type):
     *     <ul>
     *       <li>Tworzy osobną mapę tekstur dla danego typu.</li>
     *       <li>Określa ścieżkę do folderu, w którym znajdują się pliki .png dla tego typu bariery.</li>
     *       <li>Sprawdza, czy folder istnieje i jest katalogiem.</li>
     *       <li>Iteruje po wszystkich plikach w folderze, filtrując pliki .png.</li>
     *       <li>Mapuje nazwę pliku (bez rozszerzenia) na wartość enum Texture_Type.</li>
     *       <li>Ładuje teksturę z pliku i zapisuje ją w mapie dla danego Texture_Type.</li>
     *     </ul>
     *   </li>
     *   <li>Dodaje mapę tekstur dla danego Barrier_Type do głównej mapy tekstur.</li>
     * </ul>
     * </p>
     *
     * <p>W przypadku, gdy folder nie istnieje lub nie jest katalogiem, wyrzucany jest wyjątek.</p>
     */
    private void loadTextures() {
        textures = new HashMap<>();
        String basePath = "assets/barriers/";

        // Iterujemy po każdym typie bariery
        for (Barrier_Type barrierType : Barrier_Type.values()) {
            // Dla danego typu bariery tworzymy osobną mapę tekstur
            Map<Texture_Type, Texture> textureMapForBarrier = new HashMap<>();

            // Ścieżka do folderu, np. "assets/barriers/border/"
            String folderName = barrierType.name().toLowerCase();
            FileHandle folderHandle = Gdx.files.internal(basePath + folderName);

            if (!folderHandle.exists() || !folderHandle.isDirectory()) {
                throw new IllegalArgumentException("Folder " + folderName + " nie istnieje lub nie jest katalogiem!");
            }

            // Iterujemy po wszystkich plikach w folderze
            for (FileHandle file : folderHandle.list()) {
                if (file.extension().equals("png")) {
                    // Nazwa pliku bez rozszerzenia, np. "straight_horizontal_up"
                    String fileName = file.nameWithoutExtension();

                    // Mapujemy nazwę pliku na wartość enum Texture_Type (wartości w enum powinny być pisane wielkimi literami)
                    Texture_Type textureType = null;
                    try {
                        textureType = Texture_Type.valueOf(fileName.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        // Jeśli plik nie odpowiada żadnej wartości w enum, można pominąć go lub zalogować ostrzeżenie.
                    }

                    // Jeśli udało się uzyskać Texture_Type, ładujemy teksturę i zapisujemy ją w mapie.
                    if (textureType != null) {
                        textureMapForBarrier.put(textureType, new Texture(file));
                    }
                }
            }
            // Zapisujemy mapę tekstur dla danego Barrier_Type w głównej mapie.
            textures.put(barrierType, textureMapForBarrier);
        }
    }
}
