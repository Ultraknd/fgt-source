package fgt.gameserver.model.actor.status;

import fgt.gameserver.model.actor.Attackable;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.instance.Monster;

public class AttackableStatus extends NpcStatus<Attackable>
{
	public AttackableStatus(Attackable actor)
	{
		super(actor);
	}
	
	@Override
	public final void reduceHp(double value, Creature attacker)
	{
		reduceHp(value, attacker, true, false, false);
	}
	
	@Override
	public final void reduceHp(double value, Creature attacker, boolean awake, boolean isDOT, boolean isHpConsumption)
	{
		if (_actor.isDead())
			return;
		
		if (_actor instanceof Monster)
			((Monster) _actor).getOverhitState().test(attacker, value);
		
		super.reduceHp(value, attacker, awake, isDOT, isHpConsumption);
	}
}