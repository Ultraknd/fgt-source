package fgt.gameserver.handler.admincommandhandlers;

import fgt.gameserver.data.xml.DoorData;
import fgt.gameserver.handler.IAdminCommandHandler;
import fgt.gameserver.model.WorldObject;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.actor.instance.Door;
import fgt.gameserver.network.SystemMessageId;

public class AdminDoor implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_open",
		"admin_close"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		if (command.startsWith("admin_open"))
		{
			try
			{
				testDoor(player, DoorData.getInstance().getDoor(Integer.parseInt(command.substring(11))), true);
			}
			catch (Exception e)
			{
				testDoor(player, player.getTarget(), true);
			}
		}
		else if (command.startsWith("admin_close"))
		{
			try
			{
				testDoor(player, DoorData.getInstance().getDoor(Integer.parseInt(command.substring(12))), false);
			}
			catch (Exception e)
			{
				testDoor(player, player.getTarget(), false);
			}
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private static void testDoor(Player player, WorldObject worldObject, boolean isOpenCondition)
	{
		if (worldObject instanceof Door)
			((Door) worldObject).changeState(isOpenCondition, false);
		else
			player.sendPacket(SystemMessageId.INVALID_TARGET);
	}
}