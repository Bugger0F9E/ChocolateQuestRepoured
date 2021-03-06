package com.teamcqr.chocolatequestrepoured.objects.entity.mobs;

import com.teamcqr.chocolatequestrepoured.factions.EDefaultFaction;
import com.teamcqr.chocolatequestrepoured.objects.entity.EBaseHealths;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.EntityAIAttack;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.EntityAIAttackRanged;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.EntityAIBackstab;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.EntityAIFireFighter;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.EntityAIHealingPotion;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.EntityAIIdleSit;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.EntityAIMoveToHome;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.EntityAIMoveToLeader;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.EntityAISearchMount;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.EntityAITameAndLeashPet;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.EntityAITorchIgniter;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.target.EntityAICQRNearestAttackTarget;
import com.teamcqr.chocolatequestrepoured.objects.entity.ai.target.EntityAIHurtByTarget;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;

import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityCQRMandril extends AbstractEntityCQR {

	public EntityCQRMandril(World worldIn) {
		super(worldIn);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(5, new EntityAIHealingPotion(this));
		this.tasks.addTask(8, new EntityAIAttackRanged(this));
		this.tasks.addTask(9, new EntityAIBackstab(this));
		this.tasks.addTask(10, new EntityAILeapAtTarget(this, 0.6F));
		this.tasks.addTask(11, new EntityAIAttack(this));
		this.tasks.addTask(14, new EntityAIFireFighter(this));
		this.tasks.addTask(15, new EntityAIMoveToLeader(this));
		this.tasks.addTask(16, new EntityAISearchMount(this));
		this.tasks.addTask(17, new EntityAITameAndLeashPet(this));
		this.tasks.addTask(20, new EntityAIMoveToHome(this));
		this.tasks.addTask(21, new EntityAIIdleSit(this));
		this.tasks.addTask(22, new EntityAITorchIgniter(this));

		this.targetTasks.addTask(0, new EntityAICQRNearestAttackTarget(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this));
	}

	@Override
	protected ResourceLocation getLootTable() {
		return null;
	}

	@Override
	public float getBaseHealth() {
		return EBaseHealths.MANDRILS.getValue();
	}

	@Override
	public EDefaultFaction getDefaultFaction() {
		return EDefaultFaction.BEASTS;
	}

	@Override
	public int getTextureCount() {
		return 1;
	}

	@Override
	public boolean canRide() {
		return true;
	}

	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEFINED;
	}

	@Override
	public boolean canOpenDoors() {
		return true;
	}

	@Override
	public float getEyeHeight() {
		return this.height * 0.84F;
	}

	@Override
	public float getDefaultWidth() {
		return 0.6F;
	}

	@Override
	public float getDefaultHeight() {
		return 1.9F;
	}

}
