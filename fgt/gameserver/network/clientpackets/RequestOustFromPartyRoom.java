package fgt.gameserver.network.clientpackets;

import fgt.gameserver.data.manager.PartyMatchRoomManager;
import fgt.gameserver.model.World;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.group.PartyMatchRoom;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.network.serverpackets.PartyMatchList;

public final class RequestOustFromPartyRoom extends L2GameClientPacket
{
	private int _targetId;
	
	@Override
	protected void readImpl()
	{
		_targetId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Player target = World.getInstance().getPlayer(_targetId);
		if (target == null)
			return;
		
		final PartyMatchRoom room = PartyMatchRoomManager.getInstance().getRoom(target.getPartyRoom());
		if (room == null)
			return;
		
		// Abort if current Player isn't the leader of the target's PartyMatchRoom.
		if (!room.isLeader(player))
			return;
		
		// You can't dismiss a Party member.
		if (player.isInParty() && target.isInParty() && player.getParty().getLeaderObjectId() == target.getParty().getLeaderObjectId())
			player.sendPacket(SystemMessageId.CANNOT_DISMISS_PARTY_MEMBER);
		else
		{
			// Remove PartyMatchRoom member.
			room.removeMember(target);
			
			// Add the Player back on waiting list.
			PartyMatchRoomManager.getInstance().addWaitingPlayer(target);
			
			// Send Room list.
			target.sendPacket(new PartyMatchList(target));
			
			target.sendPacket(SystemMessageId.OUSTED_FROM_PARTY_ROOM);
		}
	}
}