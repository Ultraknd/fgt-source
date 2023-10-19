package fgt.gameserver.network.clientpackets;

import fgt.gameserver.enums.ShortcutType;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.item.instance.ItemInstance;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.network.serverpackets.ExVariationCancelResult;
import fgt.gameserver.network.serverpackets.InventoryUpdate;
import fgt.gameserver.network.serverpackets.SystemMessage;

public final class RequestRefineCancel extends L2GameClientPacket
{
	private int _objectId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final ItemInstance item = player.getInventory().getItemByObjectId(_objectId);
		if (item == null)
		{
			player.sendPacket(new ExVariationCancelResult(0));
			return;
		}
		
		if (item.getOwnerId() != player.getObjectId())
			return;
		
		// cannot remove augmentation from a not augmented item
		if (!item.isAugmented())
		{
			player.sendPacket(SystemMessageId.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM);
			player.sendPacket(new ExVariationCancelResult(0));
			return;
		}
		
		// get the price
		int price = 0;
		switch (item.getItem().getCrystalType())
		{
			case C:
				if (item.getCrystalCount() < 1720)
					price = 95000;
				else if (item.getCrystalCount() < 2452)
					price = 150000;
				else
					price = 210000;
				break;
			
			case B:
				if (item.getCrystalCount() < 1746)
					price = 240000;
				else
					price = 270000;
				break;
			
			case A:
				if (item.getCrystalCount() < 2160)
					price = 330000;
				else if (item.getCrystalCount() < 2824)
					price = 390000;
				else
					price = 420000;
				break;
			
			case S:
				price = 480000;
				break;
			
			// any other item type is not augmentable
			default:
				player.sendPacket(new ExVariationCancelResult(0));
				return;
		}
		
		// try to reduce the players adena
		if (!player.reduceAdena("RequestRefineCancel", price, null, true))
		{
			player.sendPacket(new ExVariationCancelResult(0));
			return;
		}
		
		// unequip item
		if (item.isEquipped())
			player.disarmWeapon(false);
		
		// remove the augmentation
		item.removeAugmentation();
		
		// send ExVariationCancelResult
		player.sendPacket(new ExVariationCancelResult(1));
		
		// send inventory update
		InventoryUpdate iu = new InventoryUpdate();
		iu.addModifiedItem(item);
		player.sendPacket(iu);
		
		// Refresh shortcuts.
		player.getShortcutList().refreshShortcuts(s -> item.getObjectId() == s.getId() && s.getType() == ShortcutType.ITEM);
		
		// send system message
		player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.AUGMENTATION_HAS_BEEN_SUCCESSFULLY_REMOVED_FROM_YOUR_S1).addItemName(item));
	}
}