package fgt.gameserver.handler.chathandlers;

import fgt.gameserver.data.manager.PartyMatchRoomManager;
import fgt.gameserver.enums.SayType;
import fgt.gameserver.handler.IChatHandler;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.group.PartyMatchRoom;
import fgt.gameserver.network.serverpackets.CreatureSay;

public class ChatPartyMatchRoom implements IChatHandler
{
	private static final SayType[] COMMAND_IDS =
	{
		SayType.PARTYMATCH_ROOM
	};
	
	@Override
	public void handleChat(SayType type, Player player, String target, String text)
	{
		if (!player.isInPartyMatchRoom())
			return;
		
		final PartyMatchRoom room = PartyMatchRoomManager.getInstance().getRoom(player.getPartyRoom());
		if (room == null)
			return;
		
		room.broadcastPacket(new CreatureSay(player, type, text));
	}
	
	@Override
	public SayType[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}