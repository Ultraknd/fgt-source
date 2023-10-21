package fgt.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import fgt.gameserver.enums.skills.SkillTargetType;
import fgt.gameserver.handler.targethandlers.TargetAlly;
import fgt.gameserver.handler.targethandlers.TargetArea;
import fgt.gameserver.handler.targethandlers.TargetAreaCorpseMob;
import fgt.gameserver.handler.targethandlers.TargetAreaSummon;
import fgt.gameserver.handler.targethandlers.TargetAura;
import fgt.gameserver.handler.targethandlers.TargetAuraUndead;
import fgt.gameserver.handler.targethandlers.TargetBehindAura;
import fgt.gameserver.handler.targethandlers.TargetClan;
import fgt.gameserver.handler.targethandlers.TargetCorpseAlly;
import fgt.gameserver.handler.targethandlers.TargetCorpseMob;
import fgt.gameserver.handler.targethandlers.TargetCorpsePet;
import fgt.gameserver.handler.targethandlers.TargetCorpsePlayer;
import fgt.gameserver.handler.targethandlers.TargetEnemySummon;
import fgt.gameserver.handler.targethandlers.TargetFrontArea;
import fgt.gameserver.handler.targethandlers.TargetFrontAura;
import fgt.gameserver.handler.targethandlers.TargetGround;
import fgt.gameserver.handler.targethandlers.TargetHoly;
import fgt.gameserver.handler.targethandlers.TargetOne;
import fgt.gameserver.handler.targethandlers.TargetOwnerPet;
import fgt.gameserver.handler.targethandlers.TargetParty;
import fgt.gameserver.handler.targethandlers.TargetPartyMember;
import fgt.gameserver.handler.targethandlers.TargetPartyOther;
import fgt.gameserver.handler.targethandlers.TargetSelf;
import fgt.gameserver.handler.targethandlers.TargetSummon;
import fgt.gameserver.handler.targethandlers.TargetUndead;
import fgt.gameserver.handler.targethandlers.TargetUnlockable;

public class TargetHandler
{
	private final Map<SkillTargetType, ITargetHandler> _entries = new HashMap<>();
	
	protected TargetHandler()
	{
		registerHandler(new TargetAlly());
		registerHandler(new TargetArea());
		registerHandler(new TargetAreaCorpseMob());
		registerHandler(new TargetAreaSummon());
		registerHandler(new TargetAura());
		registerHandler(new TargetAuraUndead());
		registerHandler(new TargetBehindAura());
		registerHandler(new TargetClan());
		registerHandler(new TargetCorpseAlly());
		registerHandler(new TargetCorpseMob());
		registerHandler(new TargetCorpsePet());
		registerHandler(new TargetCorpsePlayer());
		registerHandler(new TargetEnemySummon());
		registerHandler(new TargetFrontArea());
		registerHandler(new TargetFrontAura());
		registerHandler(new TargetGround());
		registerHandler(new TargetHoly());
		registerHandler(new TargetOne());
		registerHandler(new TargetOwnerPet());
		registerHandler(new TargetParty());
		registerHandler(new TargetPartyMember());
		registerHandler(new TargetPartyOther());
		registerHandler(new TargetSelf());
		registerHandler(new TargetSummon());
		registerHandler(new TargetUndead());
		registerHandler(new TargetUnlockable());
	}
	
	private void registerHandler(ITargetHandler handler)
	{
		_entries.put(handler.getTargetType(), handler);
	}
	
	public ITargetHandler getHandler(SkillTargetType targetType)
	{
		return _entries.get(targetType);
	}
	
	public int size()
	{
		return _entries.size();
	}
	
	public static TargetHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final TargetHandler INSTANCE = new TargetHandler();
	}
}