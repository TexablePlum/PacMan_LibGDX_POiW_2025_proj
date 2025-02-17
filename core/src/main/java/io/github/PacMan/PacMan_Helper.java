package io.github.PacMan;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.PacMan.Game_Objects.Models.GameObject_Model;

/**
 * Klasa pomocnicza {@code PacMan_Helper} zawiera metody ułatwiające pracę z czcionkami,
 * przeliczaniem pozycji oraz debugowaniem obiektów gry.
 *
 * <p>Metody zawarte w tej klasie obejmują:
 * <ul>
 *   <li>Ładowanie czcionek TrueType z określonymi parametrami (rozmiar, kolor, obrys),</li>
 *   <li>Obliczanie szerokości i wysokości tekstu przy użyciu {@link GlyphLayout},</li>
 *   <li>Obliczanie pozycji wyśrodkowanej w poziomie,</li>
 *   <li>Rysowanie debugowych prostokątów dla hitboxów i bounds obiektów gry,</li>
 *   <li>Przeliczanie indeksu w gridzie na pozycję w pikselach.</li>
 * </ul>
 * </p>
 */
public class PacMan_Helper {

    /**
     * Ładuje czcionkę TrueType na podstawie podanej ścieżki i parametrów.
     *
     * <p>Metoda tworzy instancję {@link FreeTypeFontGenerator} dla podanego pliku,
     * ustawia rozmiar czcionki, kolor wypełnienia, kolor obrysu oraz grubość obrysu,
     * generuje czcionkę i zwalnia zasoby generatora.</p>
     *
     * @param path         Ścieżka do pliku czcionki TrueType.
     * @param size         Rozmiar czcionki.
     * @param fillColor    Kolor wypełnienia tekstu.
     * @param outlineColor Kolor obrysu tekstu.
     * @param borderSize   Grubość obrysu.
     * @return Wygenerowana czcionka typu {@link BitmapFont}.
     */
    public static BitmapFont loadFont(String path, int size, Color fillColor, Color outlineColor, int borderSize) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;              // Ustawia rozmiar czcionki.
        parameter.color = fillColor;          // Ustawia kolor wypełnienia.
        parameter.borderColor = outlineColor; // Ustawia kolor obrysu.
        parameter.borderWidth = borderSize;   // Ustawia grubość obrysu.
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();                // Zwalnia zasoby generatora.
        return font;
    }

    /**
     * Oblicza szerokość podanego tekstu przy użyciu danego fontu.
     *
     * @param font Font używany do renderowania tekstu.
     * @param text Tekst, dla którego obliczana jest szerokość.
     * @return Szerokość tekstu w pikselach.
     */
    public static float getTextWidth(BitmapFont font, String text) {
        GlyphLayout layout = new GlyphLayout(font, text);
        return layout.width;
    }

    /**
     * Oblicza wysokość podanego tekstu przy użyciu danego fontu.
     *
     * @param font Font używany do renderowania tekstu.
     * @param text Tekst, dla którego obliczana jest wysokość.
     * @return Wysokość tekstu w pikselach.
     */
    public static float getTextHeight(BitmapFont font, String text) {
        GlyphLayout layout = new GlyphLayout(font, text);
        return layout.height;
    }

    /**
     * Oblicza pozycję wyśrodkowaną w poziomie dla elementu o zadanej szerokości.
     *
     * <p>Metoda zwraca wartość, która pozwala wyśrodkować element wewnątrz kontenera o określonej szerokości.</p>
     *
     * @param containerWidth Szerokość kontenera.
     * @param elementWidth   Szerokość elementu, który ma być wyśrodkowany.
     * @return Przesunięcie w poziomie, umożliwiające wyśrodkowanie elementu.
     */
    public static float simplyVerticalCenter(float containerWidth, float elementWidth) {
        return (containerWidth - elementWidth) / 2f;
    }

    /**
     * Renderuje dodatkowe informacje debugujące dla obiektu gry.
     *
     * <p>Metoda rysuje dwa prostokąty za pomocą {@link ShapeRenderer}:
     * <ul>
     *   <li>Jeden prostokąt przedstawiający granice obiektu (bounds) – rysowany kolorem PURPLE.</li>
     *   <li>Drugi prostokąt przedstawiający hitbox obiektu – rysowany kolorem RED.</li>
     * </ul>
     * </p>
     *
     * @param batch         SpriteBatch używany do renderowania.
     * @param model         Obiekt gry implementujący interfejs {@link GameObject_Model}.
     * @param shapeRenderer ShapeRenderer używany do rysowania prostokątów.
     */
    public static void Game_Object_DTrender(SpriteBatch batch, GameObject_Model model, ShapeRenderer shapeRenderer) {
        // Ustawienie macierzy projekcji na tę samą, co w SpriteBatch.
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

        // Rysowanie bounds obiektu (żółty prostokąt).
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.PURPLE);
        shapeRenderer.rect(
            model.getBounds().x + 1,
            model.getBounds().y,
            model.getBounds().width - 1,
            model.getBounds().height - 1
        );
        shapeRenderer.end();

        // Rysowanie hitbox obiektu (czerwony prostokąt).
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(
            model.getHitBox().x + 1,
            model.getHitBox().y,
            model.getHitBox().width - 1,
            model.getHitBox().height - 1
        );
        shapeRenderer.end();
    }

    /**
     * Oblicza pozycję obiektu w obrębie prostokąta (bounds) tak, aby był on wyśrodkowany.
     *
     * <p>Metoda przyjmuje rozmiar obiektu oraz prostokąt, a następnie zwraca nową pozycję,
     * w której obiekt będzie wyśrodkowany względem tego prostokąta.</p>
     *
     * @param objectSize Rozmiar obiektu (szerokość lub wysokość).
     * @param bounds     Prostokąt, w którym obiekt ma być wyśrodkowany.
     * @return Pozycja obiektu jako {@link Vector2}.
     */
    public static Vector2 positioner(int objectSize, Rectangle bounds) {
        float newX = bounds.x + (bounds.width - objectSize) / 2;
        float newY = bounds.y + (bounds.height - objectSize) / 2;
        return new Vector2(newX, newY);
    }

    /**
     * Przelicza indeks kolumny i wiersza na pozycję w pikselach w gridzie.
     *
     * <p>Metoda wykorzystuje ustawienia gridu (rozmiar komórki, pozycję startową) z klasy
     * {@link GameProperties.GameGridProps} i zwraca pozycję obiektu w przestrzeni gry.</p>
     *
     * @param col Numer kolumny.
     * @param row Numer wiersza.
     * @return Pozycja w pikselach jako {@link Vector2}.
     */
    public static Vector2 indexToPosition(int col, int row) {
        int positionX = col * GameProperties.GameGridProps.CELL_SIZE + GameProperties.GameGridProps.POSITION_X;
        int positionY = row * GameProperties.GameGridProps.CELL_SIZE + GameProperties.GameGridProps.POSITION_Y;
        return new Vector2(positionX, positionY);
    }
}
