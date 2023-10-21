package fgt.gameserver.handler.targethandlers;

import fgt.gameserver.enums.skills.SkillTargetType;
import fgt.gameserver.geoengine.GeoEngine;
import fgt.gameserver.handler.ITargetHandler;
import fgt.gameserver.model.WorldRegion;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Playable;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.location.Location;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.network.serverpackets.SystemMessage;
import fgt.gameserver.network.serverpackets.ValidateLocation;
import fgt.gameserver.skills.L2Skill;

public class TargetGround implements ITargetHandler
{
	@Override
	public SkillTargetType getTargetType()
	{
		return SkillTargetType.GROUND;
	}
	
	@Override
	public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill)
	{
		return new Creature[]
		{
			caster
		};
	}
	
	@Override
	public Creature getFinalTarget(Creature caster, Creature target, L2Skill skill)
	{
		return caster;
	}
	
	@Override
	public boolean meetCastConditions(Playable caster, Creature target, L2Skill skill, boolean isCtrlPressed)
	{
		final WorldRegion region = caster.getRegion();
		if (region == null || !(caster instanceof Player))
			return false;
		
		final Player player = (Player) caster;
		
		final Location signetLocation = player.getCast().getSignetLocation();
		if (!GeoEngine.getInstance().canSeeLocation(player, signetLocation))
		{
			player.sendPacket(SystemMessageId.CANT_SEE_TARGET);
			return false;
		}
		
		if (!region.checkEffectRangeInsidePeaceZone(skill, signetLocation))
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addSkillName(skill));
			return false;
		}
		
		player.getPosition().setHeadingTo(signetLocation);
		player.broadcastPacket(new ValidateLocation(player));
		return true;
	}
}