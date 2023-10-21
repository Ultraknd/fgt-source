package fgt.gameserver.model.itemcontainer.listeners;

import fgt.gameserver.enums.Paperdoll;
import fgt.gameserver.model.actor.Playable;
import fgt.gameserver.model.item.instance.ItemInstance;

public interface OnEquipListener
{
	public void onEquip(Paperdoll slot, ItemInstance item, Playable actor);
	
	public void onUnequip(Paperdoll slot, ItemInstance item, Playable actor);
}