package fgt.gameserver.network.clientpackets;

import fgt.gameserver.model.actor.Player;
import fgt.gameserver.network.serverpackets.ExSendManorList;

public class RequestManorList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		player.sendPacket(ExSendManorList.STATIC_PACKET);
	}
}