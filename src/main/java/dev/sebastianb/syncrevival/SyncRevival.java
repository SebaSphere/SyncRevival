package dev.sebastianb.syncrevival;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(
        modid = SyncRevival.MOD_ID,
        acceptableRemoteVersions = "*",
        acceptedMinecraftVersions = "[1.12]"
)
public class SyncRevival {

    public static final String MOD_ID = "syncrevival";

    public static Item revivalItem = Items.GOLDEN_APPLE;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(SyncRevival.class);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isServer()) {
            EntityPlayer player = event.player;
            BlockPos closestUnoccupiedShellStorage = findClosestUnoccupiedShellStorage(player);
            if (!event.player.isSpectator()) {
                if (closestUnoccupiedShellStorage != null) {
                    EntityItem entityRevivalItem = getClosestRevivalItem(player.world, closestUnoccupiedShellStorage);
                    // if the revival item is directly on it
                    if (entityRevivalItem != null) {
                        if (entityRevivalItem.getItem().getItem() == revivalItem) {
                            EntityPlayer spectatingPlayer = getClosestSpectatingPlayer(player.world, closestUnoccupiedShellStorage);
                            if (spectatingPlayer != null) {
                                entityRevivalItem.setDead();
                                spectatingPlayer.setPositionAndUpdate(
                                        closestUnoccupiedShellStorage.getX() + 0.5,
                                        closestUnoccupiedShellStorage.getY(),
                                        closestUnoccupiedShellStorage.getZ() + 0.5
                                );
                                spectatingPlayer.setGameType(GameType.SURVIVAL);
                                // TODO: fix bug where player doesn't properly sync clientside till shift
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Finds the closest shell storage block to a player
     *
     * @param player The player to search around
     * @return The position of the closest shell storage block, or null if none was found
     */
    private static BlockPos findClosestUnoccupiedShellStorage(EntityPlayer player) {
        World world = player.world;
        BlockPos playerPos = player.getPosition();
        int searchRadius = 10; // change this value to control the search radius
        BlockPos closestShellStoragePos = null;
        double closestDistance = Double.MAX_VALUE;
        BlockPos minPos = playerPos.add(-searchRadius, -searchRadius, -searchRadius);
        BlockPos maxPos = playerPos.add(searchRadius, searchRadius, searchRadius);
        for (BlockPos pos : BlockPos.getAllInBox(minPos, maxPos)) {
            Block block = world.getBlockState(pos).getBlock();
            if ("sync:block_multi".equals(block.getRegistryName().toString())) {
                TileEntity blockMulti = world.getTileEntity(pos);
                NBTTagCompound blockMultiNBT = blockMulti.writeToNBT(new NBTTagCompound());
                // if it's a shell storage
                if ("minecraft:sync_teshellstorage".equals(blockMultiNBT.getString("id"))) {
                    // get bottom block and if it is NOT occupied
                    if (!blockMultiNBT.getBoolean("top") && !blockMultiNBT.getBoolean("occupied")) {
                        double distance = playerPos.distanceSq(pos);
                        if (distance < closestDistance) {
                            closestShellStoragePos = pos;
                            closestDistance = distance;
                        }
                    }
                }

            }
        }
        return closestShellStoragePos;
    }

    public static EntityItem getClosestRevivalItem(World world, BlockPos blockPos) {
        double checkRadius = 0.5; // change this value to control the check radius

        BlockPos minPos = blockPos.add(-checkRadius, -checkRadius, -checkRadius);
        BlockPos maxPos = blockPos.add(checkRadius, checkRadius, checkRadius);
        EntityItem closestDiamondItem = null;
        double closestDistance = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.getAllInBox(minPos, maxPos)) {
            for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
                if (entity instanceof EntityItem) {
                    EntityItem item = (EntityItem) entity;
                    if (item.getPosition().equals(blockPos)) {
                        if (item.getItem().getItem() == revivalItem) {
                            double distance = blockPos.distanceSq(entity.getPosition());
                            if (distance < closestDistance) {
                                closestDiamondItem = item;
                                closestDistance = distance;
                            }
                        }
                    }
                }
            }
        }
        return closestDiamondItem;
    }

    public static EntityPlayer getClosestSpectatingPlayer(World world, BlockPos pos) {
        double minDistance = Double.MAX_VALUE;
        EntityPlayer closestPlayer = null;

        for (EntityPlayer player : world.playerEntities) {
            if (!player.isSpectator()) {
                continue;
            }

            double distance = player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ());
            if (distance < minDistance) {
                minDistance = distance;
                closestPlayer = player;
            }
        }

        return closestPlayer;
    }

}