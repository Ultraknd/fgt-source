package fgt.gameserver.skills.effects;

import fgt.gameserver.enums.skills.EffectType;
import fgt.gameserver.enums.skills.SkillType;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.skills.AbstractEffect;
import fgt.gameserver.skills.L2Skill;

public class EffectNegate extends AbstractEffect
{
	public EffectNegate(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.NEGATE;
	}
	
	@Override
	public boolean onStart()
	{
		for (int negateSkillId : getSkill().getNegateId())
		{
			if (negateSkillId != 0)
				getEffected().stopSkillEffects(negateSkillId);
		}
		
		for (SkillType negateSkillType : getSkill().getNegateStats())
			getEffected().stopSkillEffects(negateSkillType, getSkill().getNegateLvl());
		
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}