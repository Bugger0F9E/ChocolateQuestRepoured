package com.teamcqr.chocolatequestrepoured.structuregen.dungeons;

import java.io.File;
import java.util.Properties;
import java.util.Random;

import com.teamcqr.chocolatequestrepoured.CQRMain;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.DefaultSurfaceGenerator;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.IDungeonGenerator;
import com.teamcqr.chocolatequestrepoured.structuregen.structurefile.CQStructure;
import com.teamcqr.chocolatequestrepoured.util.DungeonGenUtils;
import com.teamcqr.chocolatequestrepoured.util.PropertyFileHelper;

import net.minecraft.init.Blocks;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.template.PlacementSettings;

/**
 * Copyright (c) 29.04.2019
 * Developed by DerToaster98
 * GitHub: https://github.com/DerToaster98
 */
public class DefaultSurfaceDungeon extends DungeonBase {

	protected File structureFolderPath;

	public DefaultSurfaceDungeon(File configFile) {
		super(configFile);
		Properties prop = this.loadConfig(configFile);
		if (prop != null) {
			this.structureFolderPath = PropertyFileHelper.getFileProperty(prop, "structureFolder", "defaultFolder");

			this.closeConfigFile();
		} else {
			this.registeredSuccessful = false;
		}
	}

	@Override
	public IDungeonGenerator getGenerator() {
		return new DefaultSurfaceGenerator(null, null, null);
	}

	protected File pickStructure() {
		if (this.structureFolderPath == null) {
			return null;
		}
		return this.getStructureFileFromDirectory(this.structureFolderPath);
	}

	@Override
	protected void generate(int x, int z, World world, Chunk chunk, Random random) {
		super.generate(x, z, world, chunk, random);

		File structureF = this.pickStructure();
		if (structureF != null && structureF.exists() && structureF.isFile()) {
			CQStructure structure = new CQStructure(structureF);

			PlacementSettings settings = new PlacementSettings();
			settings.setMirror(Mirror.NONE);
			if (this.rotateDungeon()) {
				settings.setRotation(Rotation.values()[random.nextInt(4)]);
			} else {
				settings.setRotation(Rotation.NONE);
			}
			settings.setReplacedBlock(Blocks.STRUCTURE_VOID);
			settings.setIntegrity(1.0F);

			int y = DungeonGenUtils.getHighestYAt(chunk, x, z, false);
			// For position locked dungeons, use the positions y
			if (this.isPosLocked()) {
				y = this.getLockedPos().getY();
			}

			if (this.getUnderGroundOffset() != 0) {
				y -= this.getUnderGroundOffset();
			}
			if (this.yOffset != 0) {
				y += Math.abs(this.yOffset);
			}

			CQRMain.logger.info("Placing dungeon: " + this.name);
			CQRMain.logger.info("Generating structure " + structureF.getName() + " at X: " + x + "  Y: " + y + "  Z: " + z + "  ...");
			DefaultSurfaceGenerator generator = new DefaultSurfaceGenerator(this, structure, settings);
			generator.generate(world, chunk, x, y, z);
		}
	}

}
