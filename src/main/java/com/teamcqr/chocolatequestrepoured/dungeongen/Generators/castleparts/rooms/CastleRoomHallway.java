package com.teamcqr.chocolatequestrepoured.dungeongen.Generators.castleparts.rooms;

import com.teamcqr.chocolatequestrepoured.util.BlockPlacement;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class CastleRoomHallway extends CastleRoom
{
    public CastleRoomHallway(BlockPos startPos, int sideLength, int height, RoomPosition position, boolean vertical)
    {
        super(startPos, sideLength, height, position);
        this.roomType = RoomType.HALLWAY;
        if (vertical)
        {
            buildNorthWall = false;
            buildSouthWall = false;
        }
        else
        {
            buildWestWall = false;
            buildEastWall = false;
        }
    }

    @Override
    public void generateRoom(ArrayList<BlockPlacement> blocks)
    {
        for (int z = 0; z < (buildEastWall ? sideLength - 1 : sideLength); z++)
        {
            for (int x = 0; x < (buildSouthWall ? sideLength - 1 : sideLength); x++)
            {
                BlockPos pos = startPos.add(x, 0, z);
                blocks.add(new BlockPlacement(pos, Blocks.WOOL.getDefaultState()));
            }
        }
        generateWalls(blocks);
    }

    @Override
    public String getNameShortened()
    {
        return "HLL";
    }
}