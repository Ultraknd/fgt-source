package fgt.gameserver.handler.admincommandhandlers;

import fgt.gameserver.handler.IAdminCommandHandler;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.olympiad.Olympiad;

public class AdminOlympiad implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_endoly",
		"admin_sethero"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		if (command.startsWith("admin_endoly"))
		{
			Olympiad.getInstance().manualSelectHeroes();
			player.sendMessage("Heroes have been formed.");
		}
		else if (command.startsWith("admin_sethero"))
		{
			final Player targetPlayer = getTargetPlayer(player, true);
			targetPlayer.setHero(!targetPlayer.isHero());
			targetPlayer.broadcastUserInfo();
			
			player.sendMessage("You have modified " + targetPlayer.getName() + "'s hero status.");
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}