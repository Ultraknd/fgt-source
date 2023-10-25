package fgt.gameserver.handler.itemhandlers;

import fgt.gameserver.model.actor.Playable;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.item.instance.ItemInstance;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.network.serverpackets.SystemMessage;

public class Elixirs extends ItemSkills
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
		{
			playable.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ITEM_NOT_FOR_PETS));
			return;
		}
		super.useItem(playable, item, forceUse);
	}
}