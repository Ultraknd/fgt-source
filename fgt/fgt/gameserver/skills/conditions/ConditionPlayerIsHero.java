package fgt.gameserver.skills.conditions;

import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.item.kind.Item;
import fgt.gameserver.skills.L2Skill;

public class ConditionPlayerIsHero extends Condition
{
	private final boolean _val;
	
	public ConditionPlayerIsHero(boolean val)
	{
		_val = val;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		return effector instanceof Player && ((Player) effector).isHero() == _val;
	}
}