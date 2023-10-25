package fgt.gameserver.network.clientpackets;

import fgt.gameserver.model.World;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.pledge.Clan;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.network.serverpackets.AskJoinAlly;
import fgt.gameserver.network.serverpackets.SystemMessage;

public final class RequestJoinAlly extends L2GameClientPacket
{
	private int _targetId;
	
	@Override
	protected void readImpl()
	{
		_targetId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Clan clan = player.getClan();
		if (clan == null)
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER);
			return;
		}
		
		final Player target = World.getInstance().getPlayer(_targetId);
		if (target == null)
		{
			player.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
			return;
		}
		
		if (!Clan.checkAllyJoinCondition(player, target))
			return;
		
		if (!player.getRequest().setRequest(target, this))
			return;
		
		target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_ALLIANCE_LEADER_OF_S1_REQUESTED_ALLIANCE).addString(clan.getAllyName()).addCharName(player));
		target.sendPacket(new AskJoinAlly(player.getObjectId(), clan.getAllyName()));
	}
}