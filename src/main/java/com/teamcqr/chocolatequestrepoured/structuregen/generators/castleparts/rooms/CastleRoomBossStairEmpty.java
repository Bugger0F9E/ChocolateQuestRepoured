package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms;

import com.teamcqr.chocolatequestrepoured.objects.factories.CastleGearedMobFactory;
import com.teamcqr.chocolatequestrepoured.structuregen.dungeons.CastleDungeon;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CastleRoomBossStairEmpty extends CastleRoomDecoratedBase {
	private EnumFacing doorSide;

	public CastleRoomBossStairEmpty(BlockPos startPos, int sideLength, int height, EnumFacing doorSide, int floor) {
		super(startPos, sideLength, height, floor);
		this.roomType = EnumRoomType.STAIRCASE_BOSS;
		this.pathable = true;
		this.doorSide = doorSide;
	}

	@Override
	public void generateRoom(World world, CastleDungeon dungeon) {
	}

	@Override
	public void decorate(World world, CastleDungeon dungeon, CastleGearedMobFactory mobFactory) {
		this.addEdgeDecoration(world, dungeon);
		this.addWallDecoration(world, dungeon);
		this.addSpawners(world, dungeon, mobFactory);
	}

	@Override
	public void addInnerWall(EnumFacing side) {
		if (!(this.doorSide.getAxis() == EnumFacing.Axis.X && side == EnumFacing.NORTH) && !(this.doorSide.getAxis() == EnumFacing.Axis.Z && side == EnumFacing.WEST)) {
			super.addInnerWall(side);
		}
	}
}
