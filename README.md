# Kot — Fabric mod for Minecraft 26.2

The player is a cat:
- looks like a cat to others and in third person (F5); sneaking = sitting
- **M** = meow (heard by everyone nearby), cat sounds when hurt
- half size (cat eye level), 25% faster, no fall damage, permanent night vision
- creepers run away

## Build
Requires JDK 25.

    ./gradlew build          # Windows: gradlew.bat build

Result: `build/libs/catmod-2.0.0.jar` (not the `-sources` one).

## Install
1. Install Fabric Loader for 26.2 (https://fabricmc.net/use/installer/).
2. Put `catmod-2.0.0.jar` and Fabric API for 26.2 (https://modrinth.com/mod/fabric-api) into `%APPDATA%\.minecraft\mods`.
3. Start the "fabric-loader-26.2" profile in the launcher.

Quick test without installing: `./gradlew runClient`.

## Tweaks
`CatMod.java`: `CAT_SCALE`, `CAT_SPEED_BONUS`. `CatMod.isCat()` is the single on/off switch.
