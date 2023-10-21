package fgt.gameserver.handler.chathandlers;

import fgt.gameserver.data.xml.MapRegionData;
import fgt.gameserver.enums.FloodProtector;
import fgt.gameserver.enums.SayType;
import fgt.gameserver.handler.IChatHandler;
import fgt.gameserver.model.World;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.network.serverpackets.CreatureSay;

public class ChatShout implements IChatHandler
{
	private static final SayType[] COMMAND_IDS =
	{
		SayType.SHOUT
	};
	
	@Override
	public void handleChat(SayType type, Player player, String target, String text)
	{
		if (!player.getClient().performAction(FloodProtector.GLOBAL_CHAT))
			return;
		
		final CreatureSay cs = new CreatureSay(player, type, text);
		final int region = MapRegionData.getInstance().getMapRegion(player.getX(), player.getY());
		
		for (Player worldPlayer : World.getInstance().getPlayers())
		{
			if (region == MapRegionData.getInstance().getMapRegion(worldPlayer.getX(), worldPlayer.getY()))
				worldPlayer.sendPacket(cs);
		}
	}
	
	@Override
	public SayType[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}