package fgt.gameserver.handler.admincommandhandlers;

import fgt.gameserver.handler.IAdminCommandHandler;
import fgt.gameserver.model.World;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.network.SystemMessageId;

public class AdminTarget implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_target"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		if (command.startsWith("admin_target"))
		{
			try
			{
				final Player worldPlayer = World.getInstance().getPlayer(command.substring(13));
				if (worldPlayer == null)
				{
					player.sendPacket(SystemMessageId.CONTACT_CURRENTLY_OFFLINE);
					return;
				}
				
				worldPlayer.onAction(player, false, false);
			}
			catch (IndexOutOfBoundsException e)
			{
				player.sendPacket(SystemMessageId.INCORRECT_CHARACTER_NAME_TRY_AGAIN);
			}
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}