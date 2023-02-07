# SyncRevival
SyncRevival is a mod for Minecraft 1.12 that allows players to revive other players who are in spectator mode.

# Usage
1: Start Minecraft with the SyncRevival mod installed on your server.

2: Find a player in spectator mode and have them stay near the shell storage block.

3: Place the revival item directly inside the unoccupied shell storage block. By default, this is a golden apple.

The nearest player in spectator mode will be revived and placed at the shell storage block, ready to play again in survival mode.

# Installation
SyncRevival requires Minecraft 1.12, Minecraft Forge, and the Sync mod to be installed. To install the mod, simply place the syncrevival.jar file in your Minecraft mods folder.

# Configuration
At this time, there is no configuration file but you're welcome to build the mod and change what you want.

The revival item can be changed by modifying the `revivalItem` variable.

# Known bugs
- When changing back into survival from the dead, your hand will have appeared to disappear. Shift to fix this.
- When initially teleporting, there may be some rubberbanding.
- Occasionally, a player may die from fall damage when respawning.
- If multiple players are nearby in spectator, one golden apple can revive multiple players. Not sure if this should be a "feature" yet.
