package fgt.gameserver.handler.targethandlers;

import fgt.gameserver.enums.skills.SkillTargetType;
import fgt.gameserver.enums.skills.SkillType;
import fgt.gameserver.handler.ITargetHandler;
import fgt.gameserver.model.actor.Attackable;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Playable;
import fgt.gameserver.model.actor.instance.Monster;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.skills.L2Skill;
import fgt.gameserver.taskmanager.DecayTaskManager;

public class TargetCorpseMob implements ITargetHandler
{
	@Override
	public SkillTargetType getTargetType()
	{
		return SkillTargetType.CORPSE_MOB;
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
		if (!(target instanceof Attackable) || !target.isDead())
		{
			caster.sendPacket(SystemMessageId.INVALID_TARGET);
			return false;
		}
		
		if (target instanceof Monster && skill.getSkillType() == SkillType.DRAIN && !DecayTaskManager.getInstance().isCorpseActionAllowed((Monster) target))
		{
			caster.sendPacket(SystemMessageId.CORPSE_TOO_OLD_SKILL_NOT_USED);
			return false;
		}
		
		return true;
	}
}