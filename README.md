# ZhanVisuals 3.0.0

Client-side Fabric mod for Minecraft Java 1.21.11.

## Build
Requires Java 21 and Gradle 8.14.3.

```bash
gradle build --no-daemon
```

The mod jar is created in `build/libs/`.

## GitHub Actions (phone-friendly)
1. Upload the contents of this project to a GitHub repository.
2. Open **Actions**.
3. Select **Build ZhanVisuals**.
4. Press **Run workflow**.
5. Download artifact `ZhanVisuals-3.0.0`.
6. Put the built JAR in `.minecraft/mods` alongside Fabric API.

## Controls
- Right Shift: open ClickGUI
- Click a module tile: toggle it
- Escape: close

## Current implementation
- Right Shift ClickGUI
- Saved toggles in `config/zhanvisuals.json`
- HUD panel with FPS, coordinates, health and totem count

The other listed feature groups are planned extensions, not implemented in this starter build.
This source archive is not a compiled or runtime-tested JAR. MojoLauncher compatibility must be tested on the target device.
