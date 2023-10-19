package fgt.gameserver.handler.chathandlers;

import fgt.gameserver.data.manager.PetitionManager;
import fgt.gameserver.enums.SayType;
import fgt.gameserver.handler.IChatHandler;
import fgt.gameserver.model.Petition;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.network.SystemMessageId;

public class ChatPetition implements IChatHandler
{
	private static final SayType[] COMMAND_IDS =
	{
		SayType.PETITION_PLAYER,
		SayType.PETITION_GM
	};
	
	@Override
	public void handleChat(SayType type, Player player, String target, String text)
	{
		final Petition petition = PetitionManager.getInstance().getPetitionInProcess(player);
		if (petition == null)
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_IN_PETITION_CHAT);
			return;
		}
		
		petition.sendMessage(player, text);
	}
	
	@Override
	public SayType[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}