package fgt.gameserver.handler.targethandlers;

import fgt.gameserver.enums.skills.SkillTargetType;
import fgt.gameserver.handler.ITargetHandler;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Playable;
import fgt.gameserver.model.actor.Summon;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.skills.L2Skill;

public class TargetSummon implements ITargetHandler
{
	@Override
	public SkillTargetType getTargetType()
	{
		return SkillTargetType.SUMMON;
	}
	
	@Override
	public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill)
	{
		final Summon summon = caster.getSummon();
		if (summon == null)
			return EMPTY_TARGET_ARRAY;
		
		return new Creature[]
		{
			summon
		};
	}
	
	@Override
	public Creature getFinalTarget(Creature caster, Creature target, L2Skill skill)
	{
		final Summon summon = caster.getSummon();
		if (summon == null)
			return null;
		
		return summon;
	}
	
	@Override
	public boolean meetCastConditions(Playable caster, Creature target, L2Skill skill, boolean isCtrlPressed)
	{
		final Summon summon = caster.getSummon();
		if (summon == null || summon.isDead())
		{
			caster.sendPacket(SystemMessageId.INVALID_TARGET);
			return false;
		}
		
		return true;
	}
}