package fgt.gameserver.model.actor.instance;

import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Npc;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.actor.template.NpcTemplate;
import fgt.gameserver.network.serverpackets.ActionFailed;

public class EffectPoint extends Npc
{
	private final Player _owner;
	
	public EffectPoint(int objectId, NpcTemplate template, Creature owner)
	{
		super(objectId, template);
		
		_owner = (owner == null) ? null : owner.getActingPlayer();
	}
	
	@Override
	public Player getActingPlayer()
	{
		return _owner;
	}
	
	@Override
	public void onAction(Player player, boolean isCtrlPressed, boolean isShiftPressed)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}
	
	@Override
	public boolean isAttackableBy(Creature attacker)
	{
		return false;
	}
}