package fgt.gameserver.network.clientpackets;

import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.group.Party;
import fgt.gameserver.network.SystemMessageId;

public final class RequestChangePartyLeader extends L2GameClientPacket
{
	private String _targetName;
	
	@Override
	protected void readImpl()
	{
		_targetName = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Party party = player.getParty();
		if (party == null || !party.isLeader(player))
		{
			player.sendPacket(SystemMessageId.ONLY_A_PARTY_LEADER_CAN_TRANSFER_ONES_RIGHTS_TO_ANOTHER_PLAYER);
			return;
		}
		
		party.changePartyLeader(_targetName);
	}
}