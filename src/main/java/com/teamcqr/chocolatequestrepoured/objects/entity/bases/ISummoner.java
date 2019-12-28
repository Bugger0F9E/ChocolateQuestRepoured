package com.teamcqr.chocolatequestrepoured.objects.entity.bases;

import java.util.List;

import com.teamcqr.chocolatequestrepoured.factions.CQRFaction;
import com.teamcqr.chocolatequestrepoured.objects.entity.misc.EntityFlyingSkullMinion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public interface ISummoner {
	
	public CQRFaction getSummonerFaction();
	
	public List<Entity> getSummonedEntities();
	
	public EntityLivingBase getSummoner();
	
	default void setSummonedEntityFaction(Entity summoned) {
		if(summoned instanceof AbstractEntityCQR) {
			((AbstractEntityCQR)summoned).setLeader(getSummoner());
			((AbstractEntityCQR)summoned).setFaction(getSummonerFaction().getName());
		}
		if(summoned instanceof EntityFlyingSkullMinion) {
			((EntityFlyingSkullMinion)summoned).setSummoner(getSummoner());
		}
	}
	
	public void addSummonedEntityToList(Entity summoned);

}
