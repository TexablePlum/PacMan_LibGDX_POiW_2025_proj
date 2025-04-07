# 🎮 PacMan LibGDX 🍒

**A student project for a PacMan game** developed from scratch in Java using the professional LibGDX framework (modules: `core` and `lwjgl3`). This project was carried out as part of the **Object-Oriented and Cross-Platform Programming** course.

## 🎯 Project Goal

To create a faithful reproduction of the classic **PacMan** game with the ability to design custom levels. The player assumes the role of the iconic PacMan, whose objective is to:

- 🟡 Collect all the dots (food) on the board.
- 👻 Avoid ghosts or — after obtaining Power-Ups — eliminate them.

The game supports advanced level creation through `.json` files (more details in the [🗺️ Map Interpreter](#%EF%B8%8F-map-interpreter-json) section).

---

## 🛠️ Technologies

- ☕ **Java**
- 🟥 **LibGDX** (`core`, `lwjgl3`)
- ✒️ **FreeType Font Library**
- 🔁 **GSON Library** – (map deserialization)
- 🎨 **Aseprite** – for creating custom textures

---

## 🎮 Controls

- ⬆️⬇️⬅️➡️ **Arrow keys** – control PacMan

---

## ✨ Features

### ✅ **Completed**

- 🔸 PacMan movement
- 🔸 Dual ghost AI system:
  - 🎯 **Aggressive Mode** – ghosts chase PacMan
  - 🌀 **Escape Mode** – ghosts flee after a Power-Up is collected
- 🔸 Score tracking
- 🔸 Lives system and respawn for both PacMan and ghosts
- 🔸 Collision detection system (works well, though it requires minor adjustments)
- 🔸 Support for custom maps (`.json`)
- 🔸 Direction-aware movement animations
- 🔸 Custom UI

### 🚧 **To be Improved / Added**

- 🔹 Animations for ghosts exiting their base
- 🔹 Animation for a ghost returning to the base
- 🔹 Enhancements to collision detection accuracy
- 🔹 Removal of hardcoded ghost spawn positions
- 🔹 Addition of sound effects 🎶
- 🔹 Adaptation of the code for other platforms (Android, HTML5, etc.) 📱🌐
- 🔹 Implementation of progressive difficulty levels
- 🔹 A user-friendly level editor that generates `.json` maps
- 🔹 Addition of game settings, cutscenes, and extra collectible items

---

## 🗺️ Map Interpreter (`.json`)

The project uses a **modular** approach to interpret level maps via `.json` files, which enables the creation and editing of custom boards (currently done manually in the file, with plans for an external editor in the future).

The **main process** of loading and converting the map is located in the `StageInitializer` class. It consists of several stages:

1. **Loading the JSON File**  
   The `loadFromFile()` method opens the file, parses its content using the GSON library, and populates a two-dimensional character array (`char[][]`) representing the raw level map.  
   During loading, the Y-axis is flipped (many editors and the JSON file itself assume (0,0) is the top-left corner, whereas in LibGDX (0,0) is the bottom-left).

2. **Recognizing Symbols and Building Structures**  
   The `setBarriersTypes(char[][], Grid_Model)` method iterates over the loaded character layout, identifying symbols such as `B`, `S`, `I`, `D`, `F`, `U`, `p`, etc.  
   - Based on these symbols, `BarrierPoint` objects are created (denoting the barrier type and its coordinates).  
   - Dots (`Dot_Model`) are generated – both regular (`F`) and “power-ups” (`U`).  
   - A single `PacMan_Model` object is placed, and `Ghost_Model` objects are initialized.  
   - For example, `B` represents `BORDER`, `S` – `STRUCTURE`, `I` – `INTERIOR`, and `D` – `DOOR`.

3. **Determining Barrier Adjacency**  
   Each barrier (`BarrierPoint`) is analyzed by the `Neighbours` class to detect adjacent barriers to the left, right, top, bottom, and diagonally.  
   This neighbor information allows for the correct assignment of corners, straight segments, and junctions during rendering.

4. **Assigning Textures**  
   The `barriersTextures(Map<BarrierPoint, Neighbours>)` method determines the specific sprite type (enum `Texture_Type`) for each barrier – for example, a left turn, an inner arc, a single line, etc.

5. **Conversion to Game Objects**  
   Finally, the `toGameObject(Map<BarrierPoint, Texture_Type>, Grid_Model)` method creates the appropriate objects (`Barrier_Model`) in the `Grid_Model` at their designated positions.  
   As a result, the map is constructed, and all elements (including PacMan and the ghosts) are ready for gameplay and rendering.

> **Note:** Currently, the spawn positions for ghosts and PacMan are partially hardcoded in `StageInitializer`. Eventually, this will be entirely moved to the `.json` file.

### Suggested Changes / Refinements

- Move the hardcoded ghost positions to a JSON configuration section.
- Refactor the prototype code to better separate the responsibilities of the `StageInitializer` class and its methods.
- Tag symbol names (`B`, `S`, `I`, `D`, etc.) in the JSON file comments to clarify their meanings for other contributors.

---

## ⚙️ Debug Mode

The project includes simple static debugging methods labeled as `DTrender`, which display:  
- 🔲 **Game Logic Grid** – to help verify the interpreter's correct operation.  
- 🎯 **Object Hitboxes and Bounds** – for visualizing collisions and positions on the map.

These features are available in the view classes.

---

## ▶️ Running the Project

1. Clone the repository.
2. Open it in IntelliJ IDEA.
3. Wait for Gradle synchronization.
4. Run the main class (`DesktopLauncher`).

---

## 📸 Game Screenshots

| 📌 Main Menu | 🎲 Standard Gameplay |
|---|---|
| ![2](https://github.com/user-attachments/assets/fe0bfae2-a273-445c-abca-078556270ca4) | ![6](https://github.com/user-attachments/assets/23c67307-81f0-4bd9-8dcc-e6bfa0d666f3) |

| 🛠️ Debug Mode (grid and hitboxes) | 💪 Ghosts in Escape Mode |
|---|---|
| ![1](https://github.com/user-attachments/assets/511565a2-c31c-43a6-aed8-69813a05c62d) | ![5](https://github.com/user-attachments/assets/fb01403d-64c6-457d-96d3-cf56a48df708) |

| 👻 Ghost Elimination | 💀 Game Over |
|---|---|
| ![4](https://github.com/user-attachments/assets/4d3e2c45-7149-4974-9972-7c5a489f32c4) | ![7](https://github.com/user-attachments/assets/972eb8b8-8eae-407f-9be4-cd45b83b760f) |

---

## 👨‍💻 Author

- **TexablePlum** – [GitHub 🌟](https://github.com/TexablePlum)

---

## 🚀 Future Roadmap

- 🧹 Refactoring and code optimization
- 🛠️ Fixing known bugs
- 🎞️ Adding missing elements
- 📱🌐 Support for other platforms available in LibGDX
- 🗺️ Expanding the map interpreter and level editor

---

## 📜 License

This project is available under the **MIT** license.  
See the `LICENSE` file for more details.

---

**Enjoy gaming! 👾**
