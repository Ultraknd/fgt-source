package fgt.gameserver.network.clientpackets;

import fgt.gameserver.data.sql.ClanTable;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.network.SystemMessageId;

public final class RequestReplyStartPledgeWar extends L2GameClientPacket
{
	private int _answer;
	
	@Override
	protected void readImpl()
	{
		_answer = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Player requestor = player.getActiveRequester();
		if (requestor == null)
			return;
		
		if (_answer == 1)
			ClanTable.getInstance().storeClansWars(requestor.getClanId(), player.getClanId());
		else
			requestor.sendPacket(SystemMessageId.WAR_PROCLAMATION_HAS_BEEN_REFUSED);
		
		player.setActiveRequester(null);
		requestor.onTransactionResponse();
	}
}