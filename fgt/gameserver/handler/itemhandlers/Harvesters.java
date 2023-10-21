package fgt.gameserver.handler.itemhandlers;

import fgt.Config;
import fgt.gameserver.data.SkillTable;
import fgt.gameserver.handler.IItemHandler;
import fgt.gameserver.model.WorldObject;
import fgt.gameserver.model.actor.Playable;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.actor.instance.Monster;
import fgt.gameserver.model.item.instance.ItemInstance;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.skills.L2Skill;

public class Harvesters implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		if (!Config.ALLOW_MANOR)
			return;
		
		final WorldObject target = playable.getTarget();
		if (!(target instanceof Monster))
		{
			playable.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final Monster monster = (Monster) target;
		if (!monster.isDead())
		{
			playable.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final L2Skill skill = SkillTable.getInstance().getInfo(2098, 1);
		if (skill != null)
			playable.getAI().tryToCast(monster, skill);
	}
}