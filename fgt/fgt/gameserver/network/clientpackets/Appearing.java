package fgt.gameserver.network.clientpackets;

import fgt.gameserver.model.actor.Player;
import fgt.gameserver.network.serverpackets.UserInfo;

public final class Appearing extends L2GameClientPacket
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
		
		if (player.isTeleporting())
			player.onTeleported();
		
		sendPacket(new UserInfo(player));
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return false;
	}
}