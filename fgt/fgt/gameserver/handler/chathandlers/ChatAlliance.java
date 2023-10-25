package fgt.gameserver.handler.chathandlers;

import fgt.gameserver.enums.SayType;
import fgt.gameserver.handler.IChatHandler;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.pledge.Clan;
import fgt.gameserver.network.serverpackets.CreatureSay;

public class ChatAlliance implements IChatHandler
{
	private static final SayType[] COMMAND_IDS =
	{
		SayType.ALLIANCE
	};
	
	@Override
	public void handleChat(SayType type, Player player, String target, String text)
	{
		final Clan clan = player.getClan();
		if (clan == null || clan.getAllyId() == 0)
			return;
		
		clan.broadcastToAllyMembers(new CreatureSay(player, type, text));
	}
	
	@Override
	public SayType[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}