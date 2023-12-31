package fgt.gameserver.skills.effects;

import fgt.gameserver.enums.skills.EffectFlag;
import fgt.gameserver.enums.skills.EffectType;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.actor.Summon;
import fgt.gameserver.skills.AbstractEffect;
import fgt.gameserver.skills.L2Skill;

public class EffectBetray extends AbstractEffect
{
	public EffectBetray(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.BETRAY;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffector() instanceof Player && getEffected() instanceof Summon)
		{
			Player target = getEffected().getActingPlayer();
			getEffected().getAI().tryToAttack(target, false, false);
			return true;
		}
		return false;
	}
	
	@Override
	public void onExit()
	{
		Player target = getEffected().getActingPlayer();
		getEffected().getAI().tryToFollow(target, false);
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public int getEffectFlags()
	{
		return EffectFlag.BETRAYED.getMask();
	}
}