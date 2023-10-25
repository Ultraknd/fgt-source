package fgt.gameserver.handler.usercommandhandlers;

import fgt.gameserver.handler.IUserCommandHandler;
import fgt.gameserver.model.actor.Player;

public class Dismount implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		62
	};
	
	@Override
	public void useUserCommand(int id, Player player)
	{
		if (player.isMounted())
			player.dismount();
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}