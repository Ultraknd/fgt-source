package fgt.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import fgt.gameserver.handler.itemhandlers.BeastSoulShots;
import fgt.gameserver.handler.itemhandlers.BeastSpices;
import fgt.gameserver.handler.itemhandlers.BeastSpiritShots;
import fgt.gameserver.handler.itemhandlers.BlessedSpiritShots;
import fgt.gameserver.handler.itemhandlers.Books;
import fgt.gameserver.handler.itemhandlers.Calculators;
import fgt.gameserver.handler.itemhandlers.Elixirs;
import fgt.gameserver.handler.itemhandlers.EnchantScrolls;
import fgt.gameserver.handler.itemhandlers.FishShots;
import fgt.gameserver.handler.itemhandlers.Harvesters;
import fgt.gameserver.handler.itemhandlers.ItemSkills;
import fgt.gameserver.handler.itemhandlers.Keys;
import fgt.gameserver.handler.itemhandlers.Maps;
import fgt.gameserver.handler.itemhandlers.MercenaryTickets;
import fgt.gameserver.handler.itemhandlers.PaganKeys;
import fgt.gameserver.handler.itemhandlers.PetFoods;
import fgt.gameserver.handler.itemhandlers.Recipes;
import fgt.gameserver.handler.itemhandlers.RollingDices;
import fgt.gameserver.handler.itemhandlers.ScrollsOfResurrection;
import fgt.gameserver.handler.itemhandlers.Seeds;
import fgt.gameserver.handler.itemhandlers.SevenSignsRecords;
import fgt.gameserver.handler.itemhandlers.SoulCrystals;
import fgt.gameserver.handler.itemhandlers.SoulShots;
import fgt.gameserver.handler.itemhandlers.SpecialXMas;
import fgt.gameserver.handler.itemhandlers.SpiritShots;
import fgt.gameserver.handler.itemhandlers.SummonItems;
import fgt.gameserver.model.item.kind.EtcItem;

public class ItemHandler
{
	private final Map<Integer, IItemHandler> _entries = new HashMap<>();
	
	protected ItemHandler()
	{
		registerHandler(new BeastSoulShots());
		registerHandler(new BeastSpices());
		registerHandler(new BeastSpiritShots());
		registerHandler(new BlessedSpiritShots());
		registerHandler(new Books());
		registerHandler(new Calculators());
		registerHandler(new Elixirs());
		registerHandler(new EnchantScrolls());
		registerHandler(new FishShots());
		registerHandler(new Harvesters());
		registerHandler(new ItemSkills());
		registerHandler(new Keys());
		registerHandler(new Maps());
		registerHandler(new MercenaryTickets());
		registerHandler(new PaganKeys());
		registerHandler(new PetFoods());
		registerHandler(new Recipes());
		registerHandler(new RollingDices());
		registerHandler(new ScrollsOfResurrection());
		registerHandler(new Seeds());
		registerHandler(new SevenSignsRecords());
		registerHandler(new SoulShots());
		registerHandler(new SpecialXMas());
		registerHandler(new SoulCrystals());
		registerHandler(new SpiritShots());
		registerHandler(new SummonItems());
	}
	
	private void registerHandler(IItemHandler handler)
	{
		_entries.put(handler.getClass().getSimpleName().intern().hashCode(), handler);
	}
	
	public IItemHandler getHandler(EtcItem item)
	{
		if (item == null || item.getHandlerName() == null)
			return null;
		
		return _entries.get(item.getHandlerName().hashCode());
	}
	
	public int size()
	{
		return _entries.size();
	}
	
	public static ItemHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemHandler INSTANCE = new ItemHandler();
	}
}