package fgt.gameserver.skills.conditions;

import fgt.gameserver.enums.actors.ClassRace;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.item.kind.Item;
import fgt.gameserver.skills.L2Skill;

public class ConditionPlayerRace extends Condition
{
	private final ClassRace _race;
	
	public ConditionPlayerRace(ClassRace race)
	{
		_race = race;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		return effector instanceof Player && ((Player) effector).getRace() == _race;
	}
}