package io.github.PacMan.Stage_Interpreter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.PacMan.GameProperties;
import io.github.PacMan.Game_Objects.Models.*;
import io.github.PacMan.Game_Objects.Models.Barrier.BarrierModel_Factory;
import io.github.PacMan.Game_Objects.Models.Barrier.Barrier_Type;
import io.github.PacMan.Game_Objects.Models.Barrier.Texture_Type;
import io.github.PacMan.PacMan_Helper;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Klasa StageInitializer odpowiada za interpretację mapy etapu z pliku JSON i jej konwersję na
 * obiekty gry. Proces inicjalizacji etapu przebiega według następujących kroków:
 *
 * <ol>
 *   <li><b>Wczytanie mapy z pliku:</b>
 *       Metoda {@link #loadFromFile(String)} odczytuje zawartość pliku JSON,
 *       interpretuje tablicę znaków (reprezentujących różne typy elementów mapy)
 *       i zapisuje ją w dwuwymiarowej tablicy char[][]. Przy tym uwzględniana jest zmiana układu współrzędnych
 *       (odwracanie osi Y) – ponieważ w JSON (oraz wielu edytorach) 0,0 jest na górze, a w LibGDX 0,0 jest w lewym dolnym rogu.
 *   </li>
 *
 *   <li><b>Przypisanie typów barier:</b>
 *       Metoda {@link #setBarriersTypes(char[][], Grid_Model)} iteruje po wczytanej mapie i:
 *       <ul>
 *         <li>Na podstawie symboli (np. 'B', 'S', 'I', 'D') tworzy obiekty {@link BarrierPoint}
 *             (które zawierają informację o pozycji i typie bariery).</li>
 *         <li>Dodatkowo, dla symboli odpowiadających kropkom ('F' dla zwykłych, 'U' dla power-upów)
 *             oraz Pac-Man-owi ('p') – tworzone są odpowiednie obiekty (Dot_Model, PacMan_Model) i umieszczane w gridzie.</li>
 *         <li>W przypadku Pac-Mana wykonywana jest dodatkowa logika określająca przesunięcie początkowej pozycji
 *             (na podstawie symbolu 'm' występującego obok).</li>
 *         <li>Metoda sprawdza również, czy występuje dokładnie jeden obiekt Pac-Man-a, w przeciwnym razie rzuca wyjątek.</li>
 *       </ul>
 *   </li>
 *
 *   <li><b>Określenie sąsiedztwa barier:</b>
 *       Metoda {@link #barriersNeighbours(Map)} przyjmuje mapę punktów barier (klucz: {@link Point}, wartość: {@link BarrierPoint})
 *       i dla każdego takiego punktu tworzy obiekt {@link Neighbours}. Obiekt Neighbours analizuje otoczenie danej bariery,
 *       określając, które z sąsiednich pól (lewo, prawo, góra, dół oraz przekątne) również zawierają bariery.
 *       Wynik zapisywany jest w nowej mapie, gdzie kluczem jest dany {@link BarrierPoint} a wartością obiekt {@link Neighbours}.
 *   </li>
 *
 *   <li><b>Dobór tekstury bariery:</b>
 *       Metoda {@link #barriersTextures(Map)} iteruje przez mapę, która zawiera dla każdej bariery obiekt Neighbours,
 *       i na podstawie analizowanych sąsiednich barier decyduje, jaki typ tekstury powinien zostać użyty dla danej bariery.
 *       W zależności od tego, czy dana bariera tworzy linię, zewnętrzny lub wewnętrzny róg, lub łącznik z inną barierą,
 *       przypisywany jest odpowiedni enum z {@link Texture_Type}.
 *   </li>
 *
 *   <li><b>Konwersja na obiekty gry:</b>
 *       Metoda {@link #toGameObject(Map, Grid_Model)} iteruje przez wszystkie pola gridu. Dla każdego punktu,
 *       jeśli w mapie tekstur (z mapą typu Map&lt;BarrierPoint, Texture_Type&gt;) istnieje przypisana tekstura inna niż DEFAULT,
 *       metoda tworzy obiekt bariery przy użyciu fabryki {@link BarrierModel_Factory} i umieszcza go w gridzie.
 *   </li>
 * </ol>
 *
 * <p>Cały proces inicjalizacji mapy etapu odbywa się w metodzie {@link #initializeMap(Grid_Model, String)}.
 * Dzięki temu etap w grze zostaje poprawnie skonfigurowany – wszystkie bariery, kropki, Pac-Man oraz duszki
 * są utworzone i umieszczone w odpowiednich pozycjach na planszy.</p>
 */
public class StageInitializer {

    /**
     * Główna metoda inicjalizująca mapę etapu.
     *
     * <p>Metoda wykonuje następujące kroki:
     * <ol>
     *   <li>Wczytuje mapę z pliku JSON przy użyciu {@link #loadFromFile(String)}.</li>
     *   <li>Przypisuje typy barier i tworzy obiekty (BarrierPoint) przy użyciu {@link #setBarriersTypes(char[][], Grid_Model)}.</li>
     *   <li>Dla każdego punktu bariery tworzy obiekt sąsiedztwa (Neighbours) przy użyciu {@link #barriersNeighbours(Map)}.</li>
     *   <li>Na podstawie analizy sąsiedztwa określa właściwy typ tekstury dla każdej bariery przy użyciu {@link #barriersTextures(Map)}.</li>
     *   <li>Konwertuje otrzymane informacje na obiekty gry i umieszcza je w gridzie przy użyciu {@link #toGameObject(Map, Grid_Model)}.</li>
     * </ol>
     * </p>
     *
     * @param grid      Model planszy (Grid_Model), w którym zostaną umieszczone utworzone obiekty.
     * @param file_path Ścieżka do pliku JSON zawierającego definicję mapy etapu.
     */
    public static void initializeMap(Grid_Model grid, String file_path) {
        // Wczytanie mapy jako dwuwymiarowej tablicy znaków.
        var mapFromFile = loadFromFile(file_path);
        // Przypisanie typów barier i utworzenie obiektów BarrierPoint.
        var barriersTypes = setBarriersTypes(mapFromFile, grid);
        // Dla każdego BarrierPoint utworzenie obiektu Neighbours opisującego otoczenie bariery.
        var barriersNeighbours = barriersNeighbours(barriersTypes);
        // Na podstawie analizy sąsiedztwa wybór odpowiedniej tekstury (Texture_Type) dla każdej bariery.
        var barriersTextures = barriersTextures(barriersNeighbours);
        // Konwersja wyznaczonych barier na obiekty gry i umieszczenie ich w gridzie.
        toGameObject(barriersTextures, grid);
    }

    /**
     * Wczytuje mapę etapu z pliku JSON.
     *
     * <p>Metoda:
     * <ul>
     *   <li>Otwiera plik JSON i wykorzystuje bibliotekę Gson do zparsowania zawartości.</li>
     *   <li>Odczytuje tablicę "grid" jako JsonArray.</li>
     *   <li>Tworzy dwuwymiarową tablicę znaków o rozmiarze określonym w GameProperties.</li>
     *   <li>Iteruje po komórkach JSON, uwzględniając odwrócenie osi Y, i zapisuje znaki w tablicy.</li>
     * </ul>
     * </p>
     *
     * @param file_path Ścieżka do pliku JSON.
     * @return Dwuwymiarowa tablica char[][] reprezentująca mapę etapu.
     */
    private static char[][] loadFromFile(String file_path) {
        char[][] charsMap;
        try (FileReader reader = new FileReader(file_path)){
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonArray grid_array = jsonObject.getAsJsonArray("grid");

            int rows = GameProperties.GameGridProps.ROWS_COUNT;
            int cols = GameProperties.GameGridProps.COLS_COUNT;
            charsMap = new char[cols][rows];

            // Przepisanie danych z JSON do tablicy, odwracając oś Y.
            for (int x = 0; x < cols; x++) {
                for (int y = 0; y < rows; y++) {
                    int reversedY = rows - y - 1;
                    charsMap[x][reversedY] = grid_array.get(y).getAsJsonArray().get(x).getAsString().charAt(0);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return charsMap;
    }

    /**
     * Przypisuje typy barier oraz tworzy obiekty {@link BarrierPoint} na podstawie wczytanej mapy.
     *
     * <p>Metoda iteruje po wszystkich komórkach tablicy mapy:
     * <ul>
     *   <li>Dla symboli 'B', 'S', 'I', 'D' tworzy BarrierPoint z odpowiednim typem bariery
     *       (np. BORDER, STRUCTURE, INTERIOR, DOOR) i zapisuje je w mapie, gdzie kluczem jest punkt (Point).</li>
     *   <li>Dla symboli 'F' i 'U' tworzy obiekty {@link Dot_Model} z flagą określającą power-up.</li>
     *   <li>Dla symbolu 'p' tworzy obiekt {@link PacMan_Model}. Wykonywana jest dodatkowa logika określająca przesunięcie
     *       pozycji Pac-Mana, zależnie od występowania symbolu 'm' obok.</li>
     *   <li>Sprawdzana jest liczba utworzonych Pac-Man-ów – dokładnie jeden powinien być obecny, inaczej rzucany jest wyjątek.</li>
     *   <li>Na końcu, przykładowo, tworzone są obiekty duszków (Ghost_Model) i umieszczane w określonych pozycjach w gridzie.</li>
     * </ul>
     * </p>
     *
     * @param map  Dwuwymiarowa tablica znaków reprezentująca mapę etapu.
     * @param grid Model planszy, w którym zapisywane są obiekty gry.
     * @return Mapa, w której kluczem jest punkt (Point), a wartością obiekt {@link BarrierPoint}.
     */
    private static Map<Point, BarrierPoint> setBarriersTypes(char[][] map, Grid_Model grid) {
        Map<Point, BarrierPoint> barrierPoints = new HashMap<>();

        int cols = map.length;
        int rows = map[0].length;
        int pacManCount = 0;

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                char symbol = map[x][y];
                Point point = new Point(x, y);
                var indexPosition = PacMan_Helper.indexToPosition(x, y);
                switch (symbol) {
                    case 'B':
                        barrierPoints.put(point, new BarrierPoint(new Point(x, y), Barrier_Type.BORDER));
                        break;
                    case 'S':
                        barrierPoints.put(point, new BarrierPoint(new Point(x, y), Barrier_Type.STRUCTURE));
                        break;
                    case 'I':
                        barrierPoints.put(point, new BarrierPoint(new Point(x, y), Barrier_Type.INTERIOR));
                        break;
                    case 'D':
                        barrierPoints.put(point, new BarrierPoint(new Point(x, y), Barrier_Type.DOOR));
                        break;
                    case 'F':
                        grid.setObjectAt(x, y, new Dot_Model(indexPosition.x, indexPosition.y, false, x, y));
                        break;
                    case 'U':
                        grid.setObjectAt(x, y, new Dot_Model(indexPosition.x, indexPosition.y, true, x, y));
                        break;
                    case 'p':
                        pacManCount++;
                        if (pacManCount != 1) {
                            throw new RuntimeException("Za duża liczba PacMan-ów w stage.json!");
                        } else {
                            // Określenie przesunięcia Pac-Mana w zależności od sąsiadującego symbolu 'm'
                            if (map[x + 1][y] == 'm') {
                                grid.setObjectAt(x, y, new PacMan_Model(
                                    indexPosition.x + GameProperties.GameGridProps.CELL_SIZE * 0.5f,
                                    indexPosition.y,
                                    x,
                                    y)
                                );
                            } else if (map[x - 1][y] == 'm') {
                                grid.setObjectAt(x, y, new PacMan_Model(
                                    indexPosition.x - GameProperties.GameGridProps.CELL_SIZE * 0.5f,
                                    indexPosition.y,
                                    x,
                                    y)
                                );
                            } else if (map[x][y + 1] == 'm') {
                                grid.setObjectAt(x, y, new PacMan_Model(
                                    indexPosition.x,
                                    indexPosition.y + GameProperties.GameGridProps.CELL_SIZE * 0.5f,
                                    x,
                                    y)
                                );
                            } else if (map[x][y - 1] == 'm') {
                                grid.setObjectAt(x, y, new PacMan_Model(
                                    indexPosition.x,
                                    indexPosition.y - GameProperties.GameGridProps.CELL_SIZE * 0.5f,
                                    x,
                                    y)
                                );
                            } else {
                                grid.setObjectAt(x, y, new PacMan_Model(
                                    indexPosition.x,
                                    indexPosition.y,
                                    x,
                                    y)
                                );
                            }
                        }
                        break;
                    default:
                        // Ignorujemy inne symbole.
                }
            }
        }
        if (pacManCount == 0) {
            throw new RuntimeException("Nie zainicjalizowano obiektu PacMan-a w stage.json!");
        }

        // Przykładowe ustawienie duszków w określonych pozycjach.
        grid.setObjectAt(13, 19, new Ghost_Model(
            PacMan_Helper.indexToPosition(13, 19).x + 12,
            PacMan_Helper.indexToPosition(13, 19).y,
            13,
            19,
            Ghost_Type.BLINKY,
            0
        ));

        grid.setObjectAt(9, 16, new Ghost_Model(
            PacMan_Helper.indexToPosition(9, 16).x,
            PacMan_Helper.indexToPosition(9, 16).y,
            9,
            16,
            Ghost_Type.INKY,
            2
        ));

        grid.setObjectAt(13, 13, new Ghost_Model(
            PacMan_Helper.indexToPosition(13, 13).x + 12,
            PacMan_Helper.indexToPosition(13, 13).y,
            13,
            13,
            Ghost_Type.PINKY,
            1
        ));

        grid.setObjectAt(18, 16, new Ghost_Model(
            PacMan_Helper.indexToPosition(18, 16).x,
            PacMan_Helper.indexToPosition(18, 16).y,
            18,
            16,
            Ghost_Type.CLYDE,
            3
        ));

        return barrierPoints;
    }

    /**
     * Dla każdej bariery określa jej sąsiedztwo.
     *
     * <p>Metoda iteruje po mapie obiektów {@link BarrierPoint} i dla każdego punktu tworzy obiekt {@link Neighbours},
     * który analizuje otoczenie danej bariery (czy są bariery po lewej, prawej, górze, dole oraz po przekątnych).
     * Wynik zapisywany jest w nowej mapie, gdzie kluczem jest dany {@link BarrierPoint}, a wartością obiekt {@link Neighbours}.</p>
     *
     * @param barriers Mapa, gdzie kluczem jest punkt (Point), a wartością obiekt {@link BarrierPoint}.
     * @return Mapa, gdzie kluczem jest {@link BarrierPoint} a wartością obiekt {@link Neighbours}.
     */
    private static Map<BarrierPoint, Neighbours> barriersNeighbours(Map<Point, BarrierPoint> barriers) {
        Map<BarrierPoint, Neighbours> results = new HashMap<>();

        for (Map.Entry<Point, BarrierPoint> entry : barriers.entrySet()) {
            Point point = entry.getKey();
            BarrierPoint barrierPoint = entry.getValue();

            // Tworzymy obiekt Neighbours dla danego punktu bariery
            Neighbours neighbours = new Neighbours(point, barriers);

            results.put(barrierPoint, neighbours);
        }

        return results;
    }

    /**
     * Na podstawie analizy sąsiedztwa barier wybiera odpowiedni typ tekstury dla każdej bariery.
     *
     * <p>Metoda iteruje przez mapę, gdzie dla każdego {@link BarrierPoint} wraz z jego obiektem {@link Neighbours}
     * określa, jaki typ tekstury ({@link Texture_Type}) powinien zostać zastosowany.
     * Warunki obejmują:
     * <ul>
     *   <li>Proste linie (poziome lub pionowe),</li>
     *   <li>Zewnętrzne rogi (outer arcs),</li>
     *   <li>Wewnętrzne rogi (inside arcs),</li>
     *   <li>Łączniki między barierami, w zależności od typu bariery oraz typu sąsiedniej struktury.</li>
     * </ul>
     * </p>
     *
     * @param barriers Mapa, gdzie kluczem jest {@link BarrierPoint} a wartością obiekt {@link Neighbours}.
     * @return Mapa, gdzie kluczem jest {@link BarrierPoint}, a wartością wybrany {@link Texture_Type}.
     */
    private static Map<BarrierPoint, Texture_Type> barriersTextures(Map<BarrierPoint, Neighbours> barriers) {
        Map<BarrierPoint, Texture_Type> texturesMap = new HashMap<>();

        for (Map.Entry<BarrierPoint, Neighbours> entry : barriers.entrySet()) {
            BarrierPoint barrierPoint = entry.getKey();
            Neighbours neighbours = entry.getValue();

            // Pobieramy informacje o sąsiedztwie
            boolean isLeft = neighbours.isLeft();
            boolean isRight = neighbours.isRight();
            boolean isTop = neighbours.isTop();
            boolean isBottom = neighbours.isBottom();
            boolean isLeftBottom = neighbours.isLeftBottom();
            boolean isRightBottom = neighbours.isRightBottom();
            boolean isLeftTop = neighbours.isLeftTop();
            boolean isRightTop = neighbours.isRightTop();

            Texture_Type texture = Texture_Type.DEFAULT;

            // Warunki dla prostych linii
            if (isBottom && isLeft && isRight && !isTop) {
                texture = Texture_Type.STRAIGHT_HORIZONTAL_DOWN;
            } else if (isTop && isLeft && isRight && !isBottom) {
                texture = Texture_Type.STRAIGHT_HORIZONTAL_UP;
            } else if (isLeft && isTop && isBottom && !isRight) {
                texture = Texture_Type.STRAIGHT_VERTICAL_LEFT;
            } else if (isRight && isTop && isBottom && !isLeft) {
                texture = Texture_Type.STRAIGHT_VERTICAL_RIGHT;
            }

            // Warunki dla zewnętrznych rogów
            if (isLeft && isTop && isLeftTop && !isRight && !isBottom) {
                texture = Texture_Type.OUTER_ARC_TOP_LEFT;
            } else if (isLeft && isBottom && isLeftBottom && !isRight && !isTop) {
                texture = Texture_Type.OUTER_ARC_BOTTOM_LEFT;
            } else if (isRight && isBottom && isRightBottom && !isLeft && !isTop) {
                texture = Texture_Type.OUTER_ARC_BOTTOM_RIGHT;
            } else if (isRight && isTop && isRightTop && !isLeft && !isBottom) {
                texture = Texture_Type.OUTER_ARC_TOP_RIGHT;
            }

            // Warunki dla wewnętrznych rogów
            if (isLeft && isRight && isTop && isBottom) {
                if (isLeftTop && isRightTop && isLeftBottom && !isRightBottom) {
                    texture = Texture_Type.INSIDE_ARC_TOP_LEFT;
                } else if (isLeftBottom && isRightBottom && isLeftTop && !isRightTop) {
                    texture = Texture_Type.INSIDE_ARC_BOTTOM_LEFT;
                } else if (isLeftTop && isRightTop && isRightBottom && !isLeftBottom) {
                    texture = Texture_Type.INSIDE_ARC_TOP_RIGHT;
                } else if (isLeftBottom && isRightBottom && isRightTop && !isLeftTop) {
                    texture = Texture_Type.INSIDE_ARC_BOTTOM_RIGHT;
                }
            }

            // Warunki dla łączników między barierami, jeśli bariera należy do typu BORDER.
            if (barrierPoint.barrier_type() == Barrier_Type.BORDER && isLeft && isRight && isTop && isBottom) {
                if (neighbours.getRightType() == Barrier_Type.STRUCTURE) {
                    if (!isRightBottom) {
                        texture = Texture_Type.BORDER_VERTICAL_LEFT_TOP_CONNECTOR;
                    } else if (!isRightTop) {
                        texture = Texture_Type.BORDER_VERTICAL_LEFT_BOTTOM_CONNECTOR;
                    }
                } else if (neighbours.getLeftType() == Barrier_Type.STRUCTURE) {
                    if (!isLeftTop) {
                        texture = Texture_Type.BORDER_VERTICAL_RIGHT_BOTTOM_CONNECTOR;
                    } else if (!isLeftBottom) {
                        texture = Texture_Type.BORDER_VERTICAL_RIGHT_TOP_CONNECTOR;
                    }
                } else if (neighbours.getBottomType() == Barrier_Type.STRUCTURE) {
                    if (!isLeftBottom) {
                        texture = Texture_Type.BORDER_HORIZONTAL_RIGHT_TOP_CONNECTOR;
                    } else if (!isRightBottom) {
                        texture = Texture_Type.BORDER_HORIZONTAL_LEFT_TOP_CONNECTOR;
                    }
                } else if (neighbours.getTopType() == Barrier_Type.STRUCTURE) {
                    if (!isRightTop) {
                        texture = Texture_Type.BORDER_HORIZONTAL_LEFT_BOTTOM_CONNECTOR;
                    } else if (!isLeftTop) {
                        texture = Texture_Type.BORDER_HORIZONTAL_RIGHT_BOTTOM_CONNECTOR;
                    }
                }
            }

            // Dodatkowe warunki dla usuwania niechcianych barier i kształtów
            if (isLeft && isRight && isTop && isBottom && isLeftBottom && isRightBottom && isLeftTop && isRightTop) {
                if (barrierPoint.barrier_type() == Barrier_Type.BORDER) {
                    if (neighbours.getRightType() == Barrier_Type.STRUCTURE) {
                        texture = Texture_Type.BORDER_STRAIGHT_SINGLELINE_VERTICAL_LEFT;
                    } else if (neighbours.getLeftType() == Barrier_Type.STRUCTURE) {
                        texture = Texture_Type.BORDER_STRAIGHT_SINGLELINE_VERTICAL_RIGHT;
                    } else if (neighbours.getBottomType() == Barrier_Type.STRUCTURE) {
                        texture = Texture_Type.BORDER_STRAIGHT_SINGLELINE_HORIZONTAL_UP;
                    } else if (neighbours.getTopType() == Barrier_Type.STRUCTURE) {
                        texture = Texture_Type.BORDER_STRAIGHT_SINGLELINE_HORIZONTAL_DOWN;
                    }
                }
            }

            texturesMap.put(barrierPoint, texture);
        }

        return texturesMap;
    }

    /**
     * Konwertuje zmapowane bariery (z przypisanym typem tekstury) na obiekty gry.
     *
     * <p>Metoda iteruje po wszystkich komórkach gridu i, dla każdej pozycji, sprawdza,
     * czy w mapie {@code barriers} znajduje się BarrierPoint odpowiadający tej pozycji.
     * Jeśli tak i wybrany typ tekstury nie jest DEFAULT, tworzy obiekt bariery przy użyciu fabryki
     * {@link BarrierModel_Factory} i umieszcza go w gridzie.</p>
     *
     * @param barriers Mapa, gdzie kluczem jest {@link BarrierPoint} a wartością wybrany {@link Texture_Type}.
     * @param grid     Model planszy (Grid_Model), w którym umieszczane są obiekty gry.
     */
    private static void toGameObject(Map<BarrierPoint, Texture_Type> barriers, Grid_Model grid) {
        int rows = GameProperties.GameGridProps.ROWS_COUNT;
        int cols = GameProperties.GameGridProps.COLS_COUNT;

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                var indexPosition = PacMan_Helper.indexToPosition(i, j);
                var location = new Point(i, j);
                for (BarrierPoint barrierKey : barriers.keySet()) {
                    if (barrierKey.barriere_point().equals(location)) {
                        Barrier_Type barrierType = barrierKey.barrier_type();
                        Texture_Type textureType = barriers.get(barrierKey);
                        if (textureType != Texture_Type.DEFAULT) {
                            grid.setObjectAt(i, j, BarrierModel_Factory.createBarrier(
                                indexPosition.x, indexPosition.y, barrierType, textureType));
                        }
                    }
                }
            }
        }
    }
}
