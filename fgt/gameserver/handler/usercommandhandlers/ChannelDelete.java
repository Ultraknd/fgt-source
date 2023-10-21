package fgt.gameserver.handler.usercommandhandlers;

import fgt.gameserver.handler.IUserCommandHandler;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.group.CommandChannel;
import fgt.gameserver.model.group.Party;

public class ChannelDelete implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		93
	};
	
	@Override
	public void useUserCommand(int id, Player player)
	{
		final Party party = player.getParty();
		if (party == null || !party.isLeader(player))
			return;
		
		final CommandChannel channel = party.getCommandChannel();
		if (channel == null || !channel.isLeader(player))
			return;
		
		channel.disband();
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}