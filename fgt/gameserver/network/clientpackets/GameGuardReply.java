package fgt.gameserver.network.clientpackets;

import fgt.gameserver.model.actor.Player;

public class GameGuardReply extends L2GameClientPacket
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
		
		getClient().setGameGuardOk(true);
	}
}