package fgt.gameserver.skills.conditions;

import fgt.commons.random.Rnd;

import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.item.kind.Item;
import fgt.gameserver.skills.L2Skill;

public class ConditionGameChance extends Condition
{
	private final int _chance;
	
	public ConditionGameChance(int chance)
	{
		_chance = chance;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		return Rnd.get(100) < _chance;
	}
}