package fgt.gameserver.network.clientpackets;

import fgt.Config;
import fgt.gameserver.data.manager.FishingChampionshipManager;
import fgt.gameserver.model.actor.Player;

public final class RequestExFishRanking extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (Config.ALLOW_FISH_CHAMPIONSHIP)
			FishingChampionshipManager.getInstance().showMidResult(player);
	}
}