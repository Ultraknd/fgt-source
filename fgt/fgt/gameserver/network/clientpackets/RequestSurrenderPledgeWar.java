package fgt.gameserver.network.clientpackets;

import fgt.gameserver.data.sql.ClanTable;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.pledge.Clan;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.network.serverpackets.SystemMessage;

public final class RequestSurrenderPledgeWar extends L2GameClientPacket
{
	private String _pledgeName;
	
	@Override
	protected void readImpl()
	{
		_pledgeName = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Clan playerClan = player.getClan();
		if (playerClan == null)
			return;
		
		// Check if player who does the request has the correct rights to do it
		if (!player.hasClanPrivileges(Clan.CP_CL_PLEDGE_WAR))
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		final Clan clan = ClanTable.getInstance().getClanByName(_pledgeName);
		if (clan == null)
			return;
		
		if (!playerClan.isAtWarWith(clan.getClanId()))
		{
			player.sendPacket(SystemMessageId.NOT_INVOLVED_IN_WAR);
			return;
		}
		
		player.applyDeathPenalty(false, false);
		player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_SURRENDERED_TO_THE_S1_CLAN).addString(_pledgeName));
		ClanTable.getInstance().deleteClansWars(playerClan.getClanId(), clan.getClanId());
	}
}