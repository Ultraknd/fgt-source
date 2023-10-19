package fgt.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import fgt.gameserver.handler.admincommandhandlers.AdminAdmin;
import fgt.gameserver.handler.admincommandhandlers.AdminAnnouncements;
import fgt.gameserver.handler.admincommandhandlers.AdminBookmark;
import fgt.gameserver.handler.admincommandhandlers.AdminClanHall;
import fgt.gameserver.handler.admincommandhandlers.AdminCursedWeapon;
import fgt.gameserver.handler.admincommandhandlers.AdminDoor;
import fgt.gameserver.handler.admincommandhandlers.AdminEditChar;
import fgt.gameserver.handler.admincommandhandlers.AdminEffects;
import fgt.gameserver.handler.admincommandhandlers.AdminEnchant;
import fgt.gameserver.handler.admincommandhandlers.AdminFind;
import fgt.gameserver.handler.admincommandhandlers.AdminGeoEngine;
import fgt.gameserver.handler.admincommandhandlers.AdminInfo;
import fgt.gameserver.handler.admincommandhandlers.AdminItem;
import fgt.gameserver.handler.admincommandhandlers.AdminKnownlist;
import fgt.gameserver.handler.admincommandhandlers.AdminMaintenance;
import fgt.gameserver.handler.admincommandhandlers.AdminManage;
import fgt.gameserver.handler.admincommandhandlers.AdminManor;
import fgt.gameserver.handler.admincommandhandlers.AdminMovieMaker;
import fgt.gameserver.handler.admincommandhandlers.AdminOlympiad;
import fgt.gameserver.handler.admincommandhandlers.AdminPetition;
import fgt.gameserver.handler.admincommandhandlers.AdminPledge;
import fgt.gameserver.handler.admincommandhandlers.AdminPolymorph;
import fgt.gameserver.handler.admincommandhandlers.AdminPunish;
import fgt.gameserver.handler.admincommandhandlers.AdminReload;
import fgt.gameserver.handler.admincommandhandlers.AdminSiege;
import fgt.gameserver.handler.admincommandhandlers.AdminSkill;
import fgt.gameserver.handler.admincommandhandlers.AdminSpawn;
import fgt.gameserver.handler.admincommandhandlers.AdminSummon;
import fgt.gameserver.handler.admincommandhandlers.AdminTarget;
import fgt.gameserver.handler.admincommandhandlers.AdminTeleport;
import fgt.gameserver.handler.admincommandhandlers.AdminTest;
import fgt.gameserver.handler.admincommandhandlers.AdminZone;

public class AdminCommandHandler
{
	private final Map<Integer, IAdminCommandHandler> _entries = new HashMap<>();
	
	protected AdminCommandHandler()
	{
		registerHandler(new AdminAdmin());
		registerHandler(new AdminAnnouncements());
		registerHandler(new AdminBookmark());
		registerHandler(new AdminClanHall());
		registerHandler(new AdminCursedWeapon());
		registerHandler(new AdminDoor());
		registerHandler(new AdminEditChar());
		registerHandler(new AdminEffects());
		registerHandler(new AdminEnchant());
		registerHandler(new AdminFind());
		registerHandler(new AdminGeoEngine());
		registerHandler(new AdminInfo());
		registerHandler(new AdminItem());
		registerHandler(new AdminKnownlist());
		registerHandler(new AdminMaintenance());
		registerHandler(new AdminManage());
		registerHandler(new AdminManor());
		registerHandler(new AdminMovieMaker());
		registerHandler(new AdminOlympiad());
		registerHandler(new AdminPetition());
		registerHandler(new AdminPledge());
		registerHandler(new AdminPolymorph());
		registerHandler(new AdminPunish());
		registerHandler(new AdminReload());
		registerHandler(new AdminSiege());
		registerHandler(new AdminSkill());
		registerHandler(new AdminSpawn());
		registerHandler(new AdminSummon());
		registerHandler(new AdminTarget());
		registerHandler(new AdminTeleport());
		registerHandler(new AdminTest());
		registerHandler(new AdminZone());
	}
	
	private void registerHandler(IAdminCommandHandler handler)
	{
		for (String id : handler.getAdminCommandList())
			_entries.put(id.hashCode(), handler);
	}
	
	public IAdminCommandHandler getHandler(String adminCommand)
	{
		String command = adminCommand;
		
		if (adminCommand.indexOf(" ") != -1)
			command = adminCommand.substring(0, adminCommand.indexOf(" "));
		
		return _entries.get(command.hashCode());
	}
	
	public int size()
	{
		return _entries.size();
	}
	
	public static AdminCommandHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AdminCommandHandler INSTANCE = new AdminCommandHandler();
	}
}