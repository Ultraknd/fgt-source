package fgt.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.actor.template.NpcTemplate;
import fgt.gameserver.model.clanhall.SiegableHall;
import fgt.gameserver.model.pledge.Clan;

/**
 * An instance type extending {@link Doorman}, used by castle doorman.<br>
 * <br>
 * isUnderSiege() checks current siege state associated to the doorman castle, while isOwnerClan() checks if the user is part of clan owning the castle and got the rights to open/close doors.
 */
public class CastleDoorman extends Doorman
{
	public CastleDoorman(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	protected void openDoors(Player player, String command)
	{
		final StringTokenizer st = new StringTokenizer(command.substring(10), ", ");
		st.nextToken();
		
		while (st.hasMoreTokens())
		{
			if (getSiegableHall() != null)
				getSiegableHall().openCloseDoor(Integer.parseInt(st.nextToken()), true);
			else if (getCastle() != null)
				getCastle().openDoor(player, Integer.parseInt(st.nextToken()));
		}
	}
	
	@Override
	protected final void closeDoors(Player player, String command)
	{
		final StringTokenizer st = new StringTokenizer(command.substring(11), ", ");
		st.nextToken();
		
		while (st.hasMoreTokens())
		{
			if (getSiegableHall() != null)
				getSiegableHall().openCloseDoor(Integer.parseInt(st.nextToken()), false);
			else if (getCastle() != null)
				getCastle().closeDoor(player, Integer.parseInt(st.nextToken()));
		}
	}
	
	@Override
	protected final boolean isOwnerClan(Player player)
	{
		if (player.getClan() != null)
		{
			if (getSiegableHall() != null)
				return player.getClanId() == getSiegableHall().getOwnerId() && player.hasClanPrivileges(Clan.CP_CH_OPEN_DOOR);
			
			if (getCastle() != null)
				return player.getClanId() == getCastle().getOwnerId() && player.hasClanPrivileges(Clan.CP_CS_OPEN_DOOR);
		}
		return false;
	}
	
	@Override
	protected final boolean isUnderSiege()
	{
		final SiegableHall hall = getSiegableHall();
		if (hall != null)
			return hall.isInSiege();
		
		return getCastle() != null && getCastle().getSiegeZone().isActive();
	}
}