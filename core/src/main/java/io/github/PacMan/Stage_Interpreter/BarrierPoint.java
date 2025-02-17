package io.github.PacMan.Stage_Interpreter;

import io.github.PacMan.Game_Objects.Models.Barrier.Barrier_Type;
import java.awt.*;

/**
 * Reprezentuje pojedynczy punkt bariery na mapie etapu.
 *
 * <p>Rekord {@code BarrierPoint} łączy informację o pozycji bariery w układzie siatki
 * (jako obiekt {@link Point}) oraz typ bariery (enum {@link Barrier_Type}).</p>
 *
 * <p>Używany jest w procesie interpretacji mapy etapu do określenia, jakie bariery
 * występują na poszczególnych pozycjach, co umożliwia późniejsze doboru właściwych tekstur
 * oraz logikę renderowania.</p>
 *
 * @param barriere_point Pozycja bariery w siatce (gridzie).
 * @param barrier_type   Typ bariery, określający jej charakter (np. BORDER, STRUCTURE, INTERIOR, DOOR).
 */
public record BarrierPoint(Point barriere_point, Barrier_Type barrier_type) { }
