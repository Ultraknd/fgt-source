package fgt.gameserver.skills.conditions;

import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.item.kind.Item;
import fgt.gameserver.skills.L2Skill;

public class ConditionPlayerInvSize extends Condition
{
	private final int _size;
	
	public ConditionPlayerInvSize(int size)
	{
		_size = size;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		if (effector instanceof Player)
		{
			final Player player = (Player) effector;
			return player.getInventory().getSize() <= (player.getStatus().getInventoryLimit() - _size);
		}
		return true;
	}
}