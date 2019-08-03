package com.teamcqr.chocolatequestrepoured.dungeongen.Generators.castleparts.rooms;

import com.teamcqr.chocolatequestrepoured.util.BlockPlacement;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class CastleRoomStaircase extends CastleRoom
{
    private EnumFacing doorSide;
    private int upperStairWidth;
    private int upperStairLength;
    private int centerStairWidth;
    private int centerStairLength;

    public CastleRoomStaircase(BlockPos startPos, int sideLength, int height, RoomPosition position)
    {
        super(startPos, sideLength, height, position);
        this.roomType = RoomType.STAIRCASE;
        this.doorSide = EnumFacing.EAST;

        upperStairWidth = 0;

        //Determine the width of the center stairs and the two upper side stairs. Find the largest possible
        //side width such that the center width is still greater than or equal to the length of each side.
        do
        {
            upperStairWidth++;
            centerStairWidth = (sideLength - 1) - upperStairWidth * 2;
        } while ((centerStairWidth - 2) >= (upperStairWidth + 1));

        //We need to ascend to height + 1 because of the ceiling. Each stair section should cover half the ascent
        upperStairLength = (height + 1) / 2;
        centerStairLength = (height + 1) - upperStairLength; //center section will either be same length or 1 more
    }

    @Override
    public void generate(ArrayList<BlockPlacement> blocks)
    {
        IBlockState blockToBuild;
        for (int x = 0; x < sideLength - 1; x++)
        {
            for (int z = 0; z < sideLength - 1; z++)
            {
                if (z < 2)
                {
                    buildPlatform(x, z, blocks);
                }
                else if (((x < upperStairWidth) || (x >= centerStairWidth + upperStairWidth)) && z < upperStairLength + 2)
                {
                    buildUpperStair(x, z, blocks);
                }
                else if (((x >= upperStairWidth) || (x < centerStairWidth + upperStairWidth)) && z <= centerStairLength + 2)
                {
                    buildLowerStair(x, z, blocks);
                }
            }
        }
    }

    @Override
    public String getNameShortened()
    {
        return "STR";
    }

    private void setDoorSide(EnumFacing side)
    {
        this.doorSide = side;
    }

    private void buildUpperStair(int x, int z, ArrayList<BlockPlacement> blocks)
    {
        int stairHeight = centerStairLength + (z - 2);
        IBlockState blockToBuild;
        for (int y = 0; y < height; y++)
        {
            if (y < stairHeight)
            {
                blockToBuild = Blocks.STONEBRICK.getDefaultState();
            }
            else if (y == stairHeight)
            {
                blockToBuild = Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
            }
            else
            {
                blockToBuild = Blocks.AIR.getDefaultState();
            }
            blocks.add(new BlockPlacement(getRotatedPlacement(x, y, z), blockToBuild));
        }
    }

    private void buildLowerStair(int x, int z, ArrayList<BlockPlacement> blocks)
    {
        int stairHeight = centerStairLength - (z - 1);
        IBlockState blockToBuild;
        for (int y = 0; y < height; y++)
        {
            if (y < stairHeight)
            {
                blockToBuild = Blocks.STONEBRICK.getDefaultState();
            }
            else if (y == stairHeight)
            {
                blockToBuild = Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH);
            }
            else
            {
                blockToBuild = Blocks.AIR.getDefaultState();
            }
            blocks.add(new BlockPlacement(getRotatedPlacement(x, y, z), blockToBuild));
        }
    }

    private void buildPlatform(int x, int z, ArrayList<BlockPlacement> blocks)
    {
        IBlockState blockToBuild;
        int platformHeight = centerStairLength; //the stair length is also the platform height

        for (int y = 0; y < height; y++)
        {
            if (y < platformHeight)
            {
                blockToBuild = Blocks.STONEBRICK.getDefaultState();
            }
            else
            {
                blockToBuild =  Blocks.AIR.getDefaultState();
            }
            blocks.add(new BlockPlacement(getRotatedPlacement(x, y, z), blockToBuild));
        }
    }

    private BlockPos getRotatedPlacement(int x, int y, int z)
    {
        switch (doorSide)
        {
            case EAST:
                return startPos.add(sideLength - 2 - z, y, x);
            case WEST:
                return startPos.add(z, y, sideLength - 2 - x);
            case NORTH:
                return startPos.add(sideLength - 2 - x, y, sideLength - 2 - z);
            case SOUTH:
            default:
                return startPos.add(x, y, z);
        }
    }
}
