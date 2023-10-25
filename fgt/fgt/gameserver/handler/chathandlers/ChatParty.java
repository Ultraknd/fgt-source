package fgt.gameserver.handler.chathandlers;

import fgt.gameserver.enums.SayType;
import fgt.gameserver.handler.IChatHandler;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.group.Party;
import fgt.gameserver.network.serverpackets.CreatureSay;

public class ChatParty implements IChatHandler
{
	private static final SayType[] COMMAND_IDS =
	{
		SayType.PARTY
	};
	
	@Override
	public void handleChat(SayType type, Player player, String target, String text)
	{
		final Party party = player.getParty();
		if (party == null)
			return;
		
		party.broadcastPacket(new CreatureSay(player, type, text));
	}
	
	@Override
	public SayType[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}