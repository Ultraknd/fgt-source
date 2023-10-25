package fgt.gameserver.skills.conditions;

import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.item.kind.Item;
import fgt.gameserver.skills.L2Skill;
import fgt.gameserver.taskmanager.GameTimeTaskManager;

public class ConditionGameTime extends Condition
{
	private final boolean _night;
	
	public ConditionGameTime(boolean night)
	{
		_night = night;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		return GameTimeTaskManager.getInstance().isNight() == _night;
	}
}