package fgt.gameserver.network.clientpackets;

import fgt.gameserver.data.manager.PartyMatchRoomManager;
import fgt.gameserver.model.actor.Player;

public class RequestDismissPartyRoom extends L2GameClientPacket
{
	private int _roomId;
	
	@Override
	protected void readImpl()
	{
		_roomId = readD();
		readD(); // Not used.
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		PartyMatchRoomManager.getInstance().deleteRoom(_roomId);
	}
}