package fgt.gameserver.network.clientpackets;

import fgt.gameserver.handler.IUserCommandHandler;
import fgt.gameserver.handler.UserCommandHandler;
import fgt.gameserver.model.actor.Player;

public class RequestUserCommand extends L2GameClientPacket
{
	private int _commandId;
	
	@Override
	protected void readImpl()
	{
		_commandId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final IUserCommandHandler handler = UserCommandHandler.getInstance().getHandler(_commandId);
		if (handler != null)
			handler.useUserCommand(_commandId, player);
	}
}