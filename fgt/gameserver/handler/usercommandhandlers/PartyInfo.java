package fgt.gameserver.handler.usercommandhandlers;

import fgt.gameserver.handler.IUserCommandHandler;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.group.Party;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.network.serverpackets.SystemMessage;

public class PartyInfo implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		81
	};
	
	@Override
	public void useUserCommand(int id, Player player)
	{
		final Party party = player.getParty();
		if (party == null)
			return;
		
		player.sendPacket(SystemMessageId.PARTY_INFORMATION);
		player.sendPacket(party.getLootRule().getMessageId());
		player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PARTY_LEADER_S1).addString(party.getLeader().getName()));
		player.sendMessage("Members: " + party.getMembersCount() + "/9");
		player.sendPacket(SystemMessageId.FRIEND_LIST_FOOTER);
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}