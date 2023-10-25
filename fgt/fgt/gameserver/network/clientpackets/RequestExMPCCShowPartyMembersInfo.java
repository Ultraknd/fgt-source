package fgt.gameserver.network.clientpackets;

import fgt.gameserver.model.World;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.network.serverpackets.ExMPCCShowPartyMemberInfo;

public final class RequestExMPCCShowPartyMembersInfo extends L2GameClientPacket
{
	private int _partyLeaderId;
	
	@Override
	protected void readImpl()
	{
		_partyLeaderId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Player leader = World.getInstance().getPlayer(_partyLeaderId);
		if (leader != null && leader.isInParty())
			player.sendPacket(new ExMPCCShowPartyMemberInfo(leader.getParty()));
	}
}