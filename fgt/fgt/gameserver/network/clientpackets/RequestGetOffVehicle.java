package fgt.gameserver.network.clientpackets;

import fgt.commons.math.MathUtil;

import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.location.Location;
import fgt.gameserver.model.location.Point2D;
import fgt.gameserver.network.serverpackets.ActionFailed;
import fgt.gameserver.network.serverpackets.GetOffVehicle;
import fgt.gameserver.network.serverpackets.MoveToLocation;
import fgt.gameserver.network.serverpackets.StopMoveInVehicle;

public final class RequestGetOffVehicle extends L2GameClientPacket
{
	private int _boatId;
	private int _x;
	private int _y;
	private int _z;
	
	@Override
	protected void readImpl()
	{
		_boatId = readD();
		_x = readD();
		_y = readD();
		_z = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (!player.isInBoat() || player.getBoat().getObjectId() != _boatId)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		player.broadcastPacket(new StopMoveInVehicle(player, _boatId));
		player.setBoat(null);
		player.broadcastPacket(new GetOffVehicle(player.getObjectId(), _boatId, _x, _y, _z));
		
		// Proper heading has been set when we clicked outside of the ship, just move player forward.
		final Point2D outsidePoint = MathUtil.getNewLocationByDistanceAndHeading(_x, _y, player.getPosition().getHeading(), 60);
		
		player.setXYZ(outsidePoint.getX(), outsidePoint.getY(), _z);
		player.revalidateZone(true);
		
		player.broadcastPacket(new MoveToLocation(player, new Location(outsidePoint.getX(), outsidePoint.getY(), _z)));
	}
}