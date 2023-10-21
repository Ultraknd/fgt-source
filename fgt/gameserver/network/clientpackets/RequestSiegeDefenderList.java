package fgt.gameserver.network.clientpackets;

import fgt.gameserver.data.manager.CastleManager;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.entity.Castle;
import fgt.gameserver.network.serverpackets.SiegeDefenderList;

public final class RequestSiegeDefenderList extends L2GameClientPacket
{
	private int _id;
	
	@Override
	protected void readImpl()
	{
		_id = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		// Check Castle entity associated to the id.
		final Castle castle = CastleManager.getInstance().getCastleById(_id);
		if (castle == null)
			return;
		
		sendPacket(new SiegeDefenderList(castle));
	}
}