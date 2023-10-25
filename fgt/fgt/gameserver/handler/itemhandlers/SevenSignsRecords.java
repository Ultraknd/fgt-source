package fgt.gameserver.handler.itemhandlers;

import fgt.gameserver.handler.IItemHandler;
import fgt.gameserver.model.actor.Playable;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.item.instance.ItemInstance;
import fgt.gameserver.network.serverpackets.SSQStatus;

public class SevenSignsRecords implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		playable.sendPacket(new SSQStatus(playable.getObjectId(), 1));
	}
}