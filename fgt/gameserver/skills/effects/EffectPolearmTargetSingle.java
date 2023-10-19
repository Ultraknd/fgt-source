package fgt.gameserver.skills.effects;

import fgt.gameserver.enums.skills.EffectType;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.skills.AbstractEffect;
import fgt.gameserver.skills.L2Skill;

public class EffectPolearmTargetSingle extends AbstractEffect
{
	public EffectPolearmTargetSingle(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.POLEARM_TARGET_SINGLE;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}