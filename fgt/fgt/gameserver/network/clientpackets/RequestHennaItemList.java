package fgt.gameserver.network.clientpackets;

import fgt.gameserver.model.actor.Player;
import fgt.gameserver.network.serverpackets.HennaEquipList;

public final class RequestHennaItemList extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _unknown;
	
	@Override
	protected void readImpl()
	{
		_unknown = readD(); // ??
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		player.sendPacket(new HennaEquipList(player));
	}
}