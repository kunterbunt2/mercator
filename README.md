# mercator

A computer game implementation of a ***closed economical simulation***.<br>
The current world is generated procedurally and includes %d cities, %d factories, %d traders and %d sims.<br>   
The amount of wealth in the system, including products and money is constant at all times.<br>
Factories pay wages to sims to produce goods that are sold on a free market.<br>
Some sims are traders that buy products in one city and sell them with profit in another city.<br>   
All sims have needs that they need to fulfill else they die.<br>
All sims have cravings that they need to fulfill to keep their satisfaction level up.<br>  
All sounds are generated by a openal based audio render engine for libgdx supporting procedurally generated audio using
**HRTF**.<br>   
Demo song is 'abyss' by Abdalla Bushnaq.<br>
Work in progress...<br>   
Developed using libgdx and gdx-gltf open source frameworks.<br>

## How To Run

mercator is still in development, so you will have to open it in a IDE like eclipse or IntelliJ.<br>
After that, you need to crete the atlas map by running GenerateAtlasTest.java test.
Then you can start mercator by one of its starting classes

1. Launcher2D Launches mercator 2D mode (planned to be used when zooming out).
2. Launcher3D Launches mercator 3D mode.
3. Launcher3DDemo Launches mercator 2D demo mode.
4. Launcher3DDeveMode Launches mercator 3D developer mode (paths have labels).

## Key mapping

| key(s)                | description                    |
|-----------------------|--------------------------------|
| a, w, d, s            | move camera (still broken)     |
| left, up, right, down | move camera (still broken)     |
| q                     | quit                           |
| p                     | pause/unpause                  |
| print                 | print screen/                  |
| v                     | enable/disable vsync           |
| n                     | enable/disable scene manager   |
| h                     | enable/disable hrtf audio      |
| tab                   | show profiler in scene manager |
| f                     | follow selected trader         |
| 1                     | enable/disable always day      |
| 2                     | enable/disable demo mode       |
| 3                     | enable/disable depth filter    |

## Issues

1. Form selecting an item does not show info in the lower pane.
1. Renderer statistics missing.
1. Universe statistics never showing up.
1. Cluster bounding box not working.
2. Camera movement is still bad.
3. Economy is not stable, all sims eventually die of hunger.
1. skybox top not oriented correctly (see reflection screenshot)
2. shadow depth buffer lower half is strange
4. if depth of field is disabled, and info panel is shown, screen becomes red
