package fgt.gameserver.handler.chathandlers;

import fgt.gameserver.enums.FloodProtector;
import fgt.gameserver.enums.SayType;
import fgt.gameserver.handler.IChatHandler;
import fgt.gameserver.model.World;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.network.serverpackets.CreatureSay;

public class ChatHeroVoice implements IChatHandler
{
	private static final SayType[] COMMAND_IDS =
	{
		SayType.HERO_VOICE
	};
	
	@Override
	public void handleChat(SayType type, Player player, String target, String text)
	{
		if (!player.isHero())
			return;
		
		if (!player.getClient().performAction(FloodProtector.HERO_VOICE))
			return;
		
		World.toAllOnlinePlayers(new CreatureSay(player, type, text));
	}
	
	@Override
	public SayType[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}