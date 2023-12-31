package fgt.gameserver.network.clientpackets;

import fgt.gameserver.data.manager.PartyMatchRoomManager;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.group.PartyMatchRoom;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.network.serverpackets.ActionFailed;
import fgt.gameserver.network.serverpackets.ExPartyRoomMember;
import fgt.gameserver.network.serverpackets.PartyMatchDetail;
import fgt.gameserver.network.serverpackets.PartyMatchList;

public final class RequestListPartyWaiting extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		readD(); // auto
		readD(); // loc
		readD(); // lvl
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (!player.isInPartyMatchRoom() && player.getParty() != null && player.getParty().getLeader() != player)
		{
			player.sendPacket(SystemMessageId.CANT_VIEW_PARTY_ROOMS);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isInPartyMatchRoom())
		{
			final PartyMatchRoom room = PartyMatchRoomManager.getInstance().getRoom(player.getPartyRoom());
			if (room == null)
				return;
			
			player.sendPacket(new PartyMatchDetail(room));
			player.sendPacket(new ExPartyRoomMember(room, 2));
			player.broadcastUserInfo();
		}
		else
		{
			// Add to waiting list.
			PartyMatchRoomManager.getInstance().addWaitingPlayer(player);
			
			// Send Room list.
			player.sendPacket(new PartyMatchList(player));
		}
	}
}