package fgt.gameserver.network.clientpackets;

import java.util.Map;

import fgt.gameserver.data.manager.RaidPointManager;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.network.serverpackets.ExGetBossRecord;

public class RequestGetBossRecord extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _bossId;
	
	@Override
	protected void readImpl()
	{
		_bossId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final int points = RaidPointManager.getInstance().getPointsByOwnerId(player.getObjectId());
		final int ranking = RaidPointManager.getInstance().calculateRanking(player.getObjectId());
		final Map<Integer, Integer> list = RaidPointManager.getInstance().getList(player);
		
		player.sendPacket(new ExGetBossRecord(ranking, points, list));
	}
}