package com.teamcqr.chocolatequestrepoured.structuregen.dungeons;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import com.teamcqr.chocolatequestrepoured.objects.factories.SpawnerFactory;
import com.teamcqr.chocolatequestrepoured.structuregen.generation.IStructure;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.CavernGenerator;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.IDungeonGenerator;
import com.teamcqr.chocolatequestrepoured.structuregen.lootchests.ELootTable;
import com.teamcqr.chocolatequestrepoured.util.DungeonGenUtils;
import com.teamcqr.chocolatequestrepoured.util.PropertyFileHelper;
import com.teamcqr.chocolatequestrepoured.util.VectorUtil;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/**
 * Copyright (c) 29.04.2019
 * Developed by DerToaster98
 * GitHub: https://github.com/DerToaster98
 */
public class CavernDungeon extends DungeonBase {

	private int minRooms = 1;
	private int maxRooms = 10;
	private int minY = 10;
	private int maxY = 40;
	private int minCaveSize = 10;
	private int maxCaveSize = 28;
	private int minHeight = 6;
	private int maxHeight = 12;
	private int maxRoomDistance = 16;
	private int minRoomDistance = 10;
	private int chestChancePerRoom = 100;
	private boolean buildStaris = true;

	private boolean placeSpawners = true;
	private boolean placeBoss = true;
	private boolean lootChests = true;
	private String mobName = "minecraft:zombie";
	private String bossMobName = "minecraft:wither";
	private Block floorMaterial = Blocks.STONE;
	private Block airBlock = Blocks.AIR;

	@Override
	public IDungeonGenerator getGenerator() {
		return new CavernGenerator();
	}

	public CavernDungeon(File configFile) {
		super(configFile);
		this.enableProtectionSystem = false;
		Properties prop = this.loadConfig(configFile);
		if (prop != null) {
			// super.chance = PropertyFileHelper.getIntProperty(prop, "chance", 0);
			// super.name = configFile.getName().replaceAll(".properties", "");
			// super.allowedDims = PropertyFileHelper.getIntArrayProperty(prop, "allowedDims", new int[]{0});
			// super.unique = PropertyFileHelper.getBooleanProperty(prop, "unique", false);

			this.minRooms = PropertyFileHelper.getIntProperty(prop, "minRooms", 1);
			this.maxRooms = PropertyFileHelper.getIntProperty(prop, "maxRooms", 8);

			this.minY = PropertyFileHelper.getIntProperty(prop, "minY", 30);
			this.maxY = PropertyFileHelper.getIntProperty(prop, "maxY", 50);

			this.minCaveSize = PropertyFileHelper.getIntProperty(prop, "minCaveSize", 5);
			this.maxCaveSize = PropertyFileHelper.getIntProperty(prop, "maxCaveSize", 15);

			this.minHeight = PropertyFileHelper.getIntProperty(prop, "minCaveHeight", 4);
			this.maxHeight = PropertyFileHelper.getIntProperty(prop, "maxCaveHeight", 12);

			this.maxRoomDistance = PropertyFileHelper.getIntProperty(prop, "maxRoomDistance", 20);
			this.minRoomDistance = PropertyFileHelper.getIntProperty(prop, "minRoomDistance", 12);

			this.buildStaris = PropertyFileHelper.getBooleanProperty(prop, "buildStairs", true);

			this.chestChancePerRoom = PropertyFileHelper.getIntProperty(prop, "chestChancePerRoom", 50);

			this.placeBoss = PropertyFileHelper.getBooleanProperty(prop, "spawnBoss", false);
			this.placeSpawners = PropertyFileHelper.getBooleanProperty(prop, "placeSpawners", true);
			this.lootChests = PropertyFileHelper.getBooleanProperty(prop, "lootchests", true);

			this.mobName = prop.getProperty("mobname", "minecraft:zombie");
			this.bossMobName = prop.getProperty("bossmobname", "minecraft:pig");

			this.underGroundOffset = 0;

			this.floorMaterial = PropertyFileHelper.getBlockProperty(prop, "floorblock", Blocks.STONE);

			this.airBlock = PropertyFileHelper.getBlockProperty(prop, "airblock", Blocks.AIR);

			this.closeConfigFile();
		} else {
			this.registeredSuccessful = false;
		}
	}

	// One block below starts y is the floor...
	@Override
	protected void generate(int x, int z, World world, Chunk chunk, Random random) {
		super.generate(x, z, world, chunk, random);

		List<List<? extends IStructure>> lists = new ArrayList<>();
		List<CavernGenerator> caves = new ArrayList<CavernGenerator>();
		HashMap<CavernGenerator, Integer> xMap = new HashMap<CavernGenerator, Integer>();
		HashMap<CavernGenerator, Integer> zMap = new HashMap<CavernGenerator, Integer>();

		int rooms = DungeonGenUtils.getIntBetweenBorders(this.minRooms, this.maxRooms, random);
		int y = DungeonGenUtils.getIntBetweenBorders(this.minY, this.maxY, random);

		if (this.isPosLocked()) {
			y = this.getLockedPos().getY();
		}

		System.out.println("Generating structure " + this.name + " at X: " + x + "  Y: " + y + "  Z: " + z + "  ...");
		int roomIndex = 1;

		BlockPos centerLoc = new BlockPos(x, y, z);

		// int OrigX = new Integer(x);
		// int OrigZ = new Integer(z);

		Vec3i distance = new Vec3i(0, 0, 0);

		do {
			x += distance.getX();
			z += distance.getZ();

			CavernGenerator cave = new CavernGenerator(this);
			// Let the cave calculate its air blocks...
			cave.setSizeAndHeight(DungeonGenUtils.getIntBetweenBorders(this.minCaveSize, this.maxCaveSize, random), DungeonGenUtils.getIntBetweenBorders(this.minCaveSize, this.maxCaveSize, random),
					DungeonGenUtils.getIntBetweenBorders(this.minHeight, this.maxHeight, random));
			cave.preProcess(world, chunk, x + distance.getX(), y, z + distance.getZ(), lists);

			distance = new Vec3i(0, 0, 0);
			int vLength = DungeonGenUtils.getIntBetweenBorders(this.minRoomDistance, this.maxRoomDistance, random);
			distance = new Vec3i(vLength, 0, 0);
			double angle = ((Integer) new Random().nextInt(360)).doubleValue();
			distance = VectorUtil.rotateVectorAroundY(distance, angle);

			caves.add(cave);
			xMap.put(cave, x);
			zMap.put(cave, z);
			System.out.println("cave #" + roomIndex + "  @ x=" + x + "  z=" + z);
			roomIndex++;
		} while (roomIndex < rooms);

		for (int i = 0; i < caves.size(); i++) {
			CavernGenerator cave = caves.get(i);
			// Dig out the cave...
			cave.buildStructure(world, chunk, xMap.get(cave), y - 1, zMap.get(cave), lists);

			// connect the tunnels
			cave.generateTunnel(centerLoc.add(0, 1, 0), cave.getCenter(), world);
		}
		for (int i = 0; i < caves.size(); i++) {
			CavernGenerator cave = caves.get(i);

			// Place a loot chest....
			if (this.lootChests && DungeonGenUtils.PercentageRandom(this.chestChancePerRoom, world.getSeed())) {
				world.setBlockState(cave.getCenter().add(0, -4, 0), Blocks.CHEST.getDefaultState());
				cave.fillChests(world, chunk, cave.getCenter().getX(), cave.getCenter().getY() - 4, cave.getCenter().getZ(), lists);
			}

			// Place a spawner...
			if (this.placeSpawners) {
				cave.placeSpawners(world, chunk, cave.getCenter().getX(), y, cave.getCenter().getZ(), lists);
			}
		}
		Random rdmCI = new Random();
		int bossCaveIndx = rdmCI.nextInt(caves.size());
		if (this.placeBoss) {

			BlockPos bossPos = new BlockPos(xMap.get(caves.get(bossCaveIndx)), y + 1, zMap.get(caves.get(bossCaveIndx)));
			world.setBlockToAir(bossPos.down());

			// BOSS CHEST
			world.setBlockState(bossPos, Blocks.CHEST.getDefaultState());
			TileEntityChest bossChest = (TileEntityChest) world.getTileEntity(bossPos.down());
			bossChest.setLootTable(ELootTable.CQ_VANILLA_END_CITY.getResourceLocation(), world.getSeed());

			// BOSS SPAWNER
			// DONE: spawn the boss
			SpawnerFactory.placeSpawner(new Entity[] { EntityList.createEntityByIDFromName(this.getBossMob(), world) }, false, null, world, bossPos.up());
		}
		if (this.buildStaris) {
			int entryCave = rdmCI.nextInt(caves.size());
			while (entryCave == bossCaveIndx) {
				entryCave = rdmCI.nextInt(caves.size());
			}
			caves.get(entryCave).buildLadder(world);
		}

	}

	int getMinCaveHeight() {
		return this.minHeight;
	}

	int getMaxCaveHeight() {
		return this.maxHeight;
	}

	int getMinCaveSize() {
		return this.minCaveSize;
	}

	int getMaxCaveSize() {
		return this.maxCaveSize;
	}

	public Block getAirBlock() {
		return this.airBlock;
	}

	public Block getFloorBlock() {
		return this.floorMaterial;
	}

	public ResourceLocation getBossMob() {
		String[] bossString = this.bossMobName.split(":");
		// System.out.println("Domain: " + bossString[0]);
		// System.out.println("Path: " + bossString[1]);

		return new ResourceLocation(bossString[0], bossString[1]);
	}

	public ResourceLocation getMob() {
		String[] bossString = this.mobName.split(":");
		// System.out.println("Domain: " + bossString[0]);
		// System.out.println("Path: " + bossString[1]);

		return new ResourceLocation(bossString[0], bossString[1]);
	}

	public boolean doBuildStaris() {
		return this.buildStaris;
	}

}
