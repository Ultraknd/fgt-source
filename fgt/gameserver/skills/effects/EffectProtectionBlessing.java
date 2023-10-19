package fgt.gameserver.skills.effects;

import fgt.gameserver.enums.skills.EffectFlag;
import fgt.gameserver.enums.skills.EffectType;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Playable;
import fgt.gameserver.skills.AbstractEffect;
import fgt.gameserver.skills.L2Skill;

public class EffectProtectionBlessing extends AbstractEffect
{
	public EffectProtectionBlessing(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.PROTECTION_BLESSING;
	}
	
	@Override
	public boolean onStart()
	{
		return false;
	}
	
	@Override
	public void onExit()
	{
		((Playable) getEffected()).stopProtectionBlessing(this);
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public int getEffectFlags()
	{
		return EffectFlag.PROTECTION_BLESSING.getMask();
	}
}