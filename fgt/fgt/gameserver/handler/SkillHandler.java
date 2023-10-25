package fgt.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import fgt.gameserver.enums.skills.SkillType;
import fgt.gameserver.handler.skillhandlers.BalanceLife;
import fgt.gameserver.handler.skillhandlers.Blow;
import fgt.gameserver.handler.skillhandlers.Cancel;
import fgt.gameserver.handler.skillhandlers.CombatPointHeal;
import fgt.gameserver.handler.skillhandlers.Continuous;
import fgt.gameserver.handler.skillhandlers.CpDamPercent;
import fgt.gameserver.handler.skillhandlers.Craft;
import fgt.gameserver.handler.skillhandlers.Disablers;
import fgt.gameserver.handler.skillhandlers.DrainSoul;
import fgt.gameserver.handler.skillhandlers.Dummy;
import fgt.gameserver.handler.skillhandlers.Extractable;
import fgt.gameserver.handler.skillhandlers.Fishing;
import fgt.gameserver.handler.skillhandlers.FishingSkill;
import fgt.gameserver.handler.skillhandlers.GetPlayer;
import fgt.gameserver.handler.skillhandlers.GiveSp;
import fgt.gameserver.handler.skillhandlers.Harvest;
import fgt.gameserver.handler.skillhandlers.Heal;
import fgt.gameserver.handler.skillhandlers.HealPercent;
import fgt.gameserver.handler.skillhandlers.InstantJump;
import fgt.gameserver.handler.skillhandlers.ManaHeal;
import fgt.gameserver.handler.skillhandlers.Manadam;
import fgt.gameserver.handler.skillhandlers.Mdam;
import fgt.gameserver.handler.skillhandlers.Pdam;
import fgt.gameserver.handler.skillhandlers.Resurrect;
import fgt.gameserver.handler.skillhandlers.Sow;
import fgt.gameserver.handler.skillhandlers.Spoil;
import fgt.gameserver.handler.skillhandlers.StriderSiegeAssault;
import fgt.gameserver.handler.skillhandlers.SummonCreature;
import fgt.gameserver.handler.skillhandlers.SummonFriend;
import fgt.gameserver.handler.skillhandlers.Sweep;
import fgt.gameserver.handler.skillhandlers.TakeCastle;
import fgt.gameserver.handler.skillhandlers.Unlock;

public class SkillHandler
{
	private final Map<Integer, ISkillHandler> _entries = new HashMap<>();
	
	protected SkillHandler()
	{
		registerHandler(new BalanceLife());
		registerHandler(new Blow());
		registerHandler(new Cancel());
		registerHandler(new CombatPointHeal());
		registerHandler(new Continuous());
		registerHandler(new CpDamPercent());
		registerHandler(new Craft());
		registerHandler(new Disablers());
		registerHandler(new DrainSoul());
		registerHandler(new Dummy());
		registerHandler(new Extractable());
		registerHandler(new Fishing());
		registerHandler(new FishingSkill());
		registerHandler(new GetPlayer());
		registerHandler(new GiveSp());
		registerHandler(new Harvest());
		registerHandler(new Heal());
		registerHandler(new HealPercent());
		registerHandler(new InstantJump());
		registerHandler(new Manadam());
		registerHandler(new ManaHeal());
		registerHandler(new Mdam());
		registerHandler(new Pdam());
		registerHandler(new Resurrect());
		registerHandler(new Sow());
		registerHandler(new Spoil());
		registerHandler(new StriderSiegeAssault());
		registerHandler(new SummonFriend());
		registerHandler(new SummonCreature());
		registerHandler(new Sweep());
		registerHandler(new TakeCastle());
		registerHandler(new Unlock());
	}
	
	private void registerHandler(ISkillHandler handler)
	{
		for (SkillType t : handler.getSkillIds())
			_entries.put(t.ordinal(), handler);
	}
	
	public ISkillHandler getHandler(SkillType skillType)
	{
		return _entries.get(skillType.ordinal());
	}
	
	public int size()
	{
		return _entries.size();
	}
	
	public static SkillHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SkillHandler INSTANCE = new SkillHandler();
	}
}