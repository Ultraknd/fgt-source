package fgt.gameserver.handler.itemhandlers;

import fgt.gameserver.handler.IItemHandler;
import fgt.gameserver.model.WorldObject;
import fgt.gameserver.model.actor.Playable;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.actor.instance.Chest;
import fgt.gameserver.model.holder.IntIntHolder;
import fgt.gameserver.model.item.instance.ItemInstance;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.skills.L2Skill;

/**
 * That handler is used for the different types of keys. Such items aren't consumed until the skill is definitively launched.
 */
public class Keys implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player player = (Player) playable;
		if (player.isSitting())
		{
			player.sendPacket(SystemMessageId.CANT_MOVE_SITTING);
			return;
		}
		
		if (player.isMovementDisabled())
			return;
		
		final WorldObject target = playable.getTarget();
		// Target must be a valid chest (not dead or already interacted).
		if (!(target instanceof Chest))
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final Chest chest = (Chest) target;
		if (chest.isDead() || chest.isInteracted())
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final IntIntHolder[] skills = item.getEtcItem().getSkills();
		if (skills == null)
		{
			LOGGER.warn("{} doesn't have any registered skill for handler.", item.getName());
			return;
		}
		
		// TODO Necessary for loop?
		for (IntIntHolder skillInfo : skills)
		{
			if (skillInfo == null)
				continue;
			
			final L2Skill itemSkill = skillInfo.getSkill();
			if (itemSkill == null)
				continue;
			
			// Key consumption is made on skill call, not on item call.
			playable.getAI().tryToCast(chest, itemSkill);
		}
	}
}