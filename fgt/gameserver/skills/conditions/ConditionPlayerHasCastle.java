package fgt.gameserver.skills.conditions;

import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.item.kind.Item;
import fgt.gameserver.model.pledge.Clan;
import fgt.gameserver.skills.L2Skill;

public final class ConditionPlayerHasCastle extends Condition
{
	private final int _castle;
	
	public ConditionPlayerHasCastle(int castle)
	{
		_castle = castle;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		if (!(effector instanceof Player))
			return false;
		
		final Clan clan = ((Player) effector).getClan();
		if (clan == null)
			return _castle == 0;
		
		// Any castle
		if (_castle == -1)
			return clan.hasCastle();
		
		return clan.getCastleId() == _castle;
	}
}