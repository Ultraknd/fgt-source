package fgt.gameserver.skills.effects;

import fgt.gameserver.enums.skills.EffectType;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.skills.AbstractEffect;
import fgt.gameserver.skills.L2Skill;

public class EffectRemoveTarget extends AbstractEffect
{
	public EffectRemoveTarget(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.REMOVE_TARGET;
	}
	
	@Override
	public boolean onStart()
	{
		getEffected().setTarget(null);
		getEffected().getAttack().stop();
		getEffected().getCast().stop();
		
		return true;
	}
	
	@Override
	public void onExit()
	{
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}