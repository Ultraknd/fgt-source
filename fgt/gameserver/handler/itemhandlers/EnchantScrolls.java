package fgt.gameserver.handler.itemhandlers;

import fgt.gameserver.handler.IItemHandler;
import fgt.gameserver.model.actor.Playable;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.item.instance.ItemInstance;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.network.serverpackets.ChooseInventoryItem;

public class EnchantScrolls implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player player = (Player) playable;
		
		if (player.getActiveEnchantItem() == null)
			player.sendPacket(SystemMessageId.SELECT_ITEM_TO_ENCHANT);
		
		player.setActiveEnchantItem(item);
		player.sendPacket(new ChooseInventoryItem(item.getItemId()));
	}
}
