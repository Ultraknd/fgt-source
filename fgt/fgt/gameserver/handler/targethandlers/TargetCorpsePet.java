package fgt.gameserver.handler.targethandlers;

import fgt.gameserver.enums.skills.SkillTargetType;
import fgt.gameserver.handler.ITargetHandler;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Playable;
import fgt.gameserver.model.actor.instance.Pet;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.network.serverpackets.SystemMessage;
import fgt.gameserver.skills.L2Skill;

public class TargetCorpsePet implements ITargetHandler
{
	@Override
	public SkillTargetType getTargetType()
	{
		return SkillTargetType.CORPSE_PET;
	}
	
	@Override
	public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill)
	{
		return new Creature[]
		{
			target
		};
	}
	
	@Override
	public Creature getFinalTarget(Creature caster, Creature target, L2Skill skill)
	{
		return target;
	}
	
	@Override
	public boolean meetCastConditions(Playable caster, Creature target, L2Skill skill, boolean isCtrlPressed)
	{
		if (!target.isDead())
		{
			caster.sendPacket(SystemMessageId.INVALID_TARGET);
			return false;
		}
		
		if (!(target instanceof Pet))
		{
			caster.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addSkillName(skill));
			return false;
		}
		return true;
	}
}