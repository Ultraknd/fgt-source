package fgt.gameserver.skills.conditions;

import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.item.kind.Item;
import fgt.gameserver.skills.L2Skill;

public class ConditionPlayerMp extends Condition
{
	private final int _mp;
	
	public ConditionPlayerMp(int mp)
	{
		_mp = mp;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		return effector.getStatus().getMpRatio() * 100 <= _mp;
	}
}