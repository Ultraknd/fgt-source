package fgt.gameserver.model.actor.ai.type;

import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.instance.Door;

public class DoorAI extends CreatureAI
{
	public DoorAI(Door door)
	{
		super(door);
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker)
	{
	}
	
	@Override
	protected void onEvtFinishedAttack()
	{
	}
	
	@Override
	protected void onEvtArrived()
	{
	}
	
	@Override
	protected void onEvtArrivedBlocked()
	{
	}
	
	@Override
	protected void onEvtDead()
	{
	}
}