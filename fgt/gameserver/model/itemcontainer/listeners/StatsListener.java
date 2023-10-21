package fgt.gameserver.model.itemcontainer.listeners;

import fgt.gameserver.enums.Paperdoll;
import fgt.gameserver.model.actor.Playable;
import fgt.gameserver.model.item.instance.ItemInstance;

public class StatsListener implements OnEquipListener
{
	private static StatsListener instance = new StatsListener();
	
	public static StatsListener getInstance()
	{
		return instance;
	}
	
	@Override
	public void onEquip(Paperdoll slot, ItemInstance item, Playable playable)
	{
		playable.addStatFuncs(item.getStatFuncs(playable));
	}
	
	@Override
	public void onUnequip(Paperdoll slot, ItemInstance item, Playable playable)
	{
		playable.removeStatsByOwner(item);
	}
}