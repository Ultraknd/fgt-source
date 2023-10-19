package fgt.gameserver.model.zone.type;

import fgt.gameserver.enums.ZoneId;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.zone.type.subtype.ZoneType;

/**
 * A zone extending {@link ZoneType} where restart isn't allowed.
 */
public class NoRestartZone extends ZoneType
{
	public NoRestartZone(final int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(final Creature character)
	{
		if (character instanceof Player)
			character.setInsideZone(ZoneId.NO_RESTART, true);
	}
	
	@Override
	protected void onExit(final Creature character)
	{
		if (character instanceof Player)
			character.setInsideZone(ZoneId.NO_RESTART, false);
	}
}