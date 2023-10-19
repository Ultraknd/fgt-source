package fgt.gameserver.skills.effects;

import fgt.gameserver.enums.skills.EffectFlag;
import fgt.gameserver.enums.skills.EffectType;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.skills.AbstractEffect;
import fgt.gameserver.skills.L2Skill;

public class EffectSilenceMagicPhysical extends AbstractEffect
{
	public EffectSilenceMagicPhysical(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.SILENCE_MAGIC_PHYSICAL;
	}
	
	@Override
	public boolean onStart()
	{
		// Abort cast.
		getEffected().getCast().stop();
		
		// Refresh abnormal effects.
		getEffected().updateAbnormalEffect();
		
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public void onExit()
	{
		// Refresh abnormal effects.
		getEffected().updateAbnormalEffect();
	}
	
	@Override
	public int getEffectFlags()
	{
		return EffectFlag.MUTED.getMask() | EffectFlag.PHYSICAL_MUTED.getMask();
	}
}