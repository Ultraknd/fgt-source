package fgt.gameserver.network.clientpackets;

import fgt.gameserver.model.actor.Player;

public final class RequestPrivateStoreManageBuy extends L2GameClientPacket
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
		
		player.tryOpenPrivateBuyStore();
	}
}