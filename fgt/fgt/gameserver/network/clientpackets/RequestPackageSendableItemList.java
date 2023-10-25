package fgt.gameserver.network.clientpackets;

import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.item.instance.ItemInstance;
import fgt.gameserver.network.serverpackets.PackageSendableList;

public final class RequestPackageSendableItemList extends L2GameClientPacket
{
	private int _objectId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}
	
	@Override
	public void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final ItemInstance[] items = player.getInventory().getAvailableItems(true, false, false);
		if (items == null)
			return;
		
		sendPacket(new PackageSendableList(items, _objectId));
	}
}