package fgt.gameserver.network.clientpackets;

import fgt.gameserver.enums.MessageType;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.group.Party;

public final class RequestOustPartyMember extends L2GameClientPacket
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
			return;
		
		party.removePartyMember(_targetName, MessageType.EXPELLED);
	}
}