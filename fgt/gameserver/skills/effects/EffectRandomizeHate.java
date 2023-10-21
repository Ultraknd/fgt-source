package fgt.gameserver.skills.effects;

import fgt.gameserver.enums.skills.EffectType;
import fgt.gameserver.model.actor.Attackable;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.skills.AbstractEffect;
import fgt.gameserver.skills.L2Skill;

public class EffectRandomizeHate extends AbstractEffect
{
	public EffectRandomizeHate(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.RANDOMIZE_HATE;
	}
	
	@Override
	public boolean onStart()
	{
		if (!(getEffected() instanceof Attackable))
			return false;
			
		// if (getEffected().isUnresponsive()) TODO
		// return false;
		
		((Attackable) getEffected()).getAggroList().randomizeAttack();
		
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