package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms;

import com.teamcqr.chocolatequestrepoured.structuregen.dungeons.CastleDungeon;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.segments.RoomWallBuilder;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.segments.WalkableRoofWallBuilder;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CastleRoomWalkableRoof extends CastleRoomBase {
	public CastleRoomWalkableRoof(BlockPos startPos, int sideLength, int height, int floor) {
		super(startPos, sideLength, height, floor);
		this.roomType = EnumRoomType.WALKABLE_ROOF;
		this.pathable = false;
	}

	@Override
	public void generateRoom(World world, CastleDungeon dungeon) {
		for (BlockPos pos : this.getDecorationArea()) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
	}

	@Override
	public void addInnerWall(EnumFacing side) {
		; // Do nothing because walkable roofs don't need inner walls
	}

	@Override
	protected void createAndGenerateWallBuilder(World world, CastleDungeon dungeon, EnumFacing side, int wallLength, BlockPos wallStart) {
		RoomWallBuilder builder = new WalkableRoofWallBuilder(wallStart, this.height, wallLength, this.walls.getOptionsForSide(side), side);
		builder.generate(world, dungeon);
	}
}
