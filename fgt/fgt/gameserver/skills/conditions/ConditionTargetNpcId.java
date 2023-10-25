package fgt.gameserver.skills.conditions;

import java.util.List;

import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Npc;
import fgt.gameserver.model.actor.instance.Door;
import fgt.gameserver.model.item.kind.Item;
import fgt.gameserver.skills.L2Skill;

public class ConditionTargetNpcId extends Condition
{
	private final List<Integer> _npcIds;
	
	public ConditionTargetNpcId(List<Integer> npcIds)
	{
		_npcIds = npcIds;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		if (effected instanceof Npc)
			return _npcIds.contains(((Npc) effected).getNpcId());
		
		if (effected instanceof Door)
			return _npcIds.contains(((Door) effected).getDoorId());
		
		return false;
	}
}