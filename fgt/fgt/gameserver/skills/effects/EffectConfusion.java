package fgt.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.List;

import fgt.commons.random.Rnd;

import fgt.gameserver.enums.AiEventType;
import fgt.gameserver.enums.skills.EffectFlag;
import fgt.gameserver.enums.skills.EffectType;
import fgt.gameserver.model.WorldObject;
import fgt.gameserver.model.actor.Attackable;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Playable;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.actor.instance.Chest;
import fgt.gameserver.model.actor.instance.Door;
import fgt.gameserver.skills.AbstractEffect;
import fgt.gameserver.skills.L2Skill;

public class EffectConfusion extends AbstractEffect
{
	public EffectConfusion(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.CONFUSION;
	}
	
	@Override
	public boolean onStart()
	{
		// Abort move.
		getEffected().getMove().stop();
		
		// Refresh abnormal effects.
		getEffected().updateAbnormalEffect();
		
		onActionTime();
		return true;
	}
	
	@Override
	public void onExit()
	{
		getEffected().removeEffect(this);
		
		if (!(getEffected() instanceof Player))
			getEffected().getAI().notifyEvent(AiEventType.THINK, null, null);
		
		// Refresh abnormal effects.
		getEffected().updateAbnormalEffect();
	}
	
	@Override
	public boolean onActionTime()
	{
		final List<Creature> targetList = new ArrayList<>();
		
		// Getting the possible targets
		for (final WorldObject obj : getEffected().getKnownType(WorldObject.class))
		{
			// Attackable NPCs and playable characters (players, summons) are put in the list.
			if ((obj instanceof Attackable || obj instanceof Playable) && (obj != getEffected()))
				// Don't put doors nor chests on it.
				if (!(obj instanceof Door || obj instanceof Chest))
					targetList.add((Creature) obj);
		}
		
		// if there is no target, exit function
		if (targetList.isEmpty())
			return true;
		
		// Choosing randomly a new target
		final Creature target = Rnd.get(targetList);
		
		// Attacking the target
		getEffected().setTarget(target);
		getEffected().getAI().tryToAttack(target);
		
		// Add aggro to that target aswell. The aggro power is random.
		final int aggro = (5 + Rnd.get(5)) * getEffector().getStatus().getLevel();
		((Attackable) getEffected()).getAggroList().addDamageHate(target, 0, aggro);
		
		return true;
	}
	
	@Override
	public int getEffectFlags()
	{
		return EffectFlag.CONFUSED.getMask();
	}
}