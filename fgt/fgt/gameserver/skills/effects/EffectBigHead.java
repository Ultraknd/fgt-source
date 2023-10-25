package fgt.gameserver.skills.effects;

import fgt.gameserver.enums.skills.AbnormalEffect;
import fgt.gameserver.enums.skills.EffectType;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.skills.AbstractEffect;
import fgt.gameserver.skills.L2Skill;

public class EffectBigHead extends AbstractEffect
{
	public EffectBigHead(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.BUFF;
	}
	
	@Override
	public boolean onStart()
	{
		getEffected().startAbnormalEffect(AbnormalEffect.BIG_HEAD);
		return true;
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopAbnormalEffect(AbnormalEffect.BIG_HEAD);
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}