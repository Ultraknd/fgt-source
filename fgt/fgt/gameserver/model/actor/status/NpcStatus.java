package fgt.gameserver.model.actor.status;

import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Npc;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.entity.Duel.DuelState;

public class NpcStatus<T extends Npc> extends CreatureStatus<T>
{
	public NpcStatus(T actor)
	{
		super(actor);
	}
	
	@Override
	public void reduceHp(double value, Creature attacker)
	{
		reduceHp(value, attacker, true, false, false);
	}
	
	@Override
	public void reduceHp(double value, Creature attacker, boolean awake, boolean isDOT, boolean isHpConsumption)
	{
		if (_actor.isDead())
			return;
		
		if (attacker != null)
		{
			final Player attackerPlayer = attacker.getActingPlayer();
			if (attackerPlayer != null && attackerPlayer.isInDuel())
				attackerPlayer.setDuelState(DuelState.INTERRUPTED);
		}
		
		super.reduceHp(value, attacker, awake, isDOT, isHpConsumption);
	}
	
	@Override
	public int getLevel()
	{
		return _actor.getTemplate().getLevel();
	}
}