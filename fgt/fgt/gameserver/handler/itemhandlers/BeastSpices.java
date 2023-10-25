package fgt.gameserver.handler.itemhandlers;

import fgt.gameserver.handler.IItemHandler;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Playable;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.actor.instance.FeedableBeast;
import fgt.gameserver.model.item.instance.ItemInstance;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.skills.L2Skill;

public class BeastSpices implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player player = (Player) playable;
		final Creature target = playable.getTarget() instanceof Creature ? (Creature) playable.getTarget() : null;
		
		if (!(target instanceof FeedableBeast))
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final L2Skill skill = item.getEtcItem().getSkills()[0].getSkill();
		if (skill != null)
			player.getAI().tryToCast(target, skill);
	}
}