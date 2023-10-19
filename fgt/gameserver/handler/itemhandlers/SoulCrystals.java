package fgt.gameserver.handler.itemhandlers;

import fgt.gameserver.handler.IItemHandler;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Playable;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.holder.IntIntHolder;
import fgt.gameserver.model.item.instance.ItemInstance;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.skills.L2Skill;

public class SoulCrystals implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final IntIntHolder[] skills = item.getEtcItem().getSkills();
		if (skills == null)
			return;
		
		final L2Skill skill = skills[0].getSkill();
		if (skill == null || skill.getId() != 2096)
			return;
		
		final Creature target = playable.getTarget() instanceof Creature ? (Creature) playable.getTarget() : null;
		if (target == null)
		{
			playable.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		playable.getAI().tryToCast(target, skill, forceUse, false, 0);
	}
}