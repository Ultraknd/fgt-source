package fgt.gameserver.skills.effects;

import fgt.gameserver.enums.skills.EffectType;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.skills.AbstractEffect;
import fgt.gameserver.skills.L2Skill;

public class EffectManaHealOverTime extends AbstractEffect
{
	public EffectManaHealOverTime(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.MANA_HEAL_OVER_TIME;
	}
	
	@Override
	public boolean onActionTime()
	{
		if (!getEffected().canBeHealed())
			return false;
		
		getEffected().getStatus().addMp(getTemplate().getValue());
		return true;
	}
}