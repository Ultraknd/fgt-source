package fgt.gameserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.logging.LogManager;

import fgt.commons.lang.StringUtil;
import fgt.commons.logging.CLogger;
import fgt.commons.mmocore.SelectorConfig;
import fgt.commons.mmocore.SelectorThread;
import fgt.commons.pool.ConnectionPool;
import fgt.commons.pool.ThreadPool;
import fgt.commons.util.SysUtil;

import fgt.Config;
import fgt.gameserver.communitybbs.CommunityBoard;
import fgt.gameserver.data.SkillTable;
import fgt.gameserver.data.cache.CrestCache;
import fgt.gameserver.data.cache.HtmCache;
import fgt.gameserver.data.manager.BoatManager;
import fgt.gameserver.data.manager.BufferManager;
import fgt.gameserver.data.manager.BuyListManager;
import fgt.gameserver.data.manager.CastleManager;
import fgt.gameserver.data.manager.CastleManorManager;
import fgt.gameserver.data.manager.ClanHallManager;
import fgt.gameserver.data.manager.CoupleManager;
import fgt.gameserver.data.manager.CursedWeaponManager;
import fgt.gameserver.data.manager.DayNightManager;
import fgt.gameserver.data.manager.DerbyTrackManager;
import fgt.gameserver.data.manager.DimensionalRiftManager;
import fgt.gameserver.data.manager.FestivalOfDarknessManager;
import fgt.gameserver.data.manager.FishingChampionshipManager;
import fgt.gameserver.data.manager.FourSepulchersManager;
import fgt.gameserver.data.manager.GrandBossManager;
import fgt.gameserver.data.manager.HeroManager;
import fgt.gameserver.data.manager.LotteryManager;
import fgt.gameserver.data.manager.PartyMatchRoomManager;
import fgt.gameserver.data.manager.PetitionManager;
import fgt.gameserver.data.manager.RaidBossManager;
import fgt.gameserver.data.manager.RaidPointManager;
import fgt.gameserver.data.manager.SevenSignsManager;
import fgt.gameserver.data.manager.ZoneManager;
import fgt.gameserver.data.sql.AutoSpawnTable;
import fgt.gameserver.data.sql.BookmarkTable;
import fgt.gameserver.data.sql.ClanTable;
import fgt.gameserver.data.sql.PlayerInfoTable;
import fgt.gameserver.data.sql.ServerMemoTable;
import fgt.gameserver.data.sql.SpawnTable;
import fgt.gameserver.data.xml.AdminData;
import fgt.gameserver.data.xml.AnnouncementData;
import fgt.gameserver.data.xml.ArmorSetData;
import fgt.gameserver.data.xml.AugmentationData;
import fgt.gameserver.data.xml.DoorData;
import fgt.gameserver.data.xml.FishData;
import fgt.gameserver.data.xml.HennaData;
import fgt.gameserver.data.xml.HerbDropData;
import fgt.gameserver.data.xml.InstantTeleportData;
import fgt.gameserver.data.xml.ItemData;
import fgt.gameserver.data.xml.MapRegionData;
import fgt.gameserver.data.xml.MultisellData;
import fgt.gameserver.data.xml.NewbieBuffData;
import fgt.gameserver.data.xml.NpcData;
import fgt.gameserver.data.xml.PlayerData;
import fgt.gameserver.data.xml.PlayerLevelData;
import fgt.gameserver.data.xml.RecipeData;
import fgt.gameserver.data.xml.ScriptData;
import fgt.gameserver.data.xml.SkillTreeData;
import fgt.gameserver.data.xml.SoulCrystalData;
import fgt.gameserver.data.xml.SpellbookData;
import fgt.gameserver.data.xml.StaticObjectData;
import fgt.gameserver.data.xml.SummonItemData;
import fgt.gameserver.data.xml.TeleportData;
import fgt.gameserver.data.xml.WalkerRouteData;
import fgt.gameserver.geoengine.GeoEngine;
import fgt.gameserver.handler.AdminCommandHandler;
import fgt.gameserver.handler.ChatHandler;
import fgt.gameserver.handler.ItemHandler;
import fgt.gameserver.handler.SkillHandler;
import fgt.gameserver.handler.TargetHandler;
import fgt.gameserver.handler.UserCommandHandler;
import fgt.gameserver.idfactory.IdFactory;
import fgt.gameserver.model.World;
import fgt.gameserver.model.boat.BoatGiranTalking;
import fgt.gameserver.model.boat.BoatGludinRune;
import fgt.gameserver.model.boat.BoatInnadrilTour;
import fgt.gameserver.model.boat.BoatRunePrimeval;
import fgt.gameserver.model.boat.BoatTalkingGludin;
import fgt.gameserver.model.olympiad.Olympiad;
import fgt.gameserver.model.olympiad.OlympiadGameManager;
import fgt.gameserver.network.GameClient;
import fgt.gameserver.network.GamePacketHandler;
import fgt.gameserver.taskmanager.AttackStanceTaskManager;
import fgt.gameserver.taskmanager.DecayTaskManager;
import fgt.gameserver.taskmanager.GameTimeTaskManager;
import fgt.gameserver.taskmanager.ItemsOnGroundTaskManager;
import fgt.gameserver.taskmanager.PvpFlagTaskManager;
import fgt.gameserver.taskmanager.RandomAnimationTaskManager;
import fgt.gameserver.taskmanager.ShadowItemTaskManager;
import fgt.gameserver.taskmanager.WaterTaskManager;
import fgt.util.DeadLockDetector;
import fgt.util.IPv4Filter;

public class GameServer
{
	private static final CLogger LOGGER = new CLogger(GameServer.class.getName());
	
	private final SelectorThread<GameClient> _selectorThread;
	
	private static GameServer _gameServer;
	
	public static void main(String[] args) throws Exception
	{
		_gameServer = new GameServer();
	}
	
	public GameServer() throws Exception
	{
		// Create log folder
		new File("./log").mkdir();
		new File("./log/chat").mkdir();
		new File("./log/console").mkdir();
		new File("./log/error").mkdir();
		new File("./log/gmaudit").mkdir();
		new File("./log/item").mkdir();
		new File("./data/crests").mkdirs();
		
		// Create input stream for log file -- or store file data into memory
		try (InputStream is = new FileInputStream(new File("config/logging.properties")))
		{
			LogManager.getLogManager().readConfiguration(is);
		}
		
		StringUtil.printSection("Config");
		Config.loadGameServer();
		
		StringUtil.printSection("Poolers");
		ConnectionPool.init();
		ThreadPool.init();
		
		StringUtil.printSection("IdFactory");
		IdFactory.getInstance();
		
		StringUtil.printSection("Cache");
		HtmCache.getInstance();
		CrestCache.getInstance();
		
		StringUtil.printSection("World");
		World.getInstance();
		MapRegionData.getInstance();
		AnnouncementData.getInstance();
		ServerMemoTable.getInstance();
		
		StringUtil.printSection("Skills");
		SkillTable.getInstance();
		SkillTreeData.getInstance();
		
		StringUtil.printSection("Items");
		ItemData.getInstance();
		SummonItemData.getInstance();
		HennaData.getInstance();
		BuyListManager.getInstance();
		MultisellData.getInstance();
		RecipeData.getInstance();
		ArmorSetData.getInstance();
		FishData.getInstance();
		SpellbookData.getInstance();
		SoulCrystalData.getInstance();
		AugmentationData.getInstance();
		CursedWeaponManager.getInstance();
		
		StringUtil.printSection("Admins");
		AdminData.getInstance();
		BookmarkTable.getInstance();
		PetitionManager.getInstance();
		
		StringUtil.printSection("Characters");
		PlayerData.getInstance();
		PlayerInfoTable.getInstance();
		PlayerLevelData.getInstance();
		PartyMatchRoomManager.getInstance();
		RaidPointManager.getInstance();
		
		StringUtil.printSection("Community server");
		CommunityBoard.getInstance();
		
		StringUtil.printSection("Clans");
		ClanTable.getInstance();
		
		StringUtil.printSection("Geodata & Pathfinding");
		GeoEngine.getInstance();
		
		StringUtil.printSection("Zones");
		ZoneManager.getInstance();
		
		StringUtil.printSection("Castles & Clan Halls");
		CastleManager.getInstance();
		ClanHallManager.getInstance();
		
		StringUtil.printSection("Task Managers");
		AttackStanceTaskManager.getInstance();
		DecayTaskManager.getInstance();
		GameTimeTaskManager.getInstance();
		ItemsOnGroundTaskManager.getInstance();
		PvpFlagTaskManager.getInstance();
		RandomAnimationTaskManager.getInstance();
		ShadowItemTaskManager.getInstance();
		WaterTaskManager.getInstance();
		
		StringUtil.printSection("Auto Spawns");
		AutoSpawnTable.getInstance();
		
		StringUtil.printSection("Seven Signs");
		SevenSignsManager.getInstance().spawnSevenSignsNPC();
		FestivalOfDarknessManager.getInstance();
		
		StringUtil.printSection("Manor Manager");
		CastleManorManager.getInstance();
		
		StringUtil.printSection("NPCs");
		BufferManager.getInstance();
		HerbDropData.getInstance();
		NpcData.getInstance();
		WalkerRouteData.getInstance();
		DoorData.getInstance().spawn();
		StaticObjectData.getInstance();
		SpawnTable.getInstance();
		RaidBossManager.getInstance();
		GrandBossManager.getInstance();
		DayNightManager.getInstance().notifyChangeMode();
		DimensionalRiftManager.getInstance();
		NewbieBuffData.getInstance();
		InstantTeleportData.getInstance();
		TeleportData.getInstance();
		
		StringUtil.printSection("Olympiads & Heroes");
		OlympiadGameManager.getInstance();
		Olympiad.getInstance();
		HeroManager.getInstance();
		
		StringUtil.printSection("Four Sepulchers");
		FourSepulchersManager.getInstance();
		
		StringUtil.printSection("Quests & Scripts");
		ScriptData.getInstance();
		
		if (Config.ALLOW_BOAT)
		{
			BoatManager.getInstance();
			BoatGiranTalking.load();
			BoatGludinRune.load();
			BoatInnadrilTour.load();
			BoatRunePrimeval.load();
			BoatTalkingGludin.load();
		}
		
		StringUtil.printSection("Events");
		DerbyTrackManager.getInstance();
		LotteryManager.getInstance();
		
		if (Config.ALLOW_WEDDING)
			CoupleManager.getInstance();
		
		if (Config.ALLOW_FISH_CHAMPIONSHIP)
			FishingChampionshipManager.getInstance();
		
		StringUtil.printSection("Handlers");
		LOGGER.info("Loaded {} admin command handlers.", AdminCommandHandler.getInstance().size());
		LOGGER.info("Loaded {} chat handlers.", ChatHandler.getInstance().size());
		LOGGER.info("Loaded {} item handlers.", ItemHandler.getInstance().size());
		LOGGER.info("Loaded {} skill handlers.", SkillHandler.getInstance().size());
		LOGGER.info("Loaded {} target handlers.", TargetHandler.getInstance().size());
		LOGGER.info("Loaded {} user command handlers.", UserCommandHandler.getInstance().size());
		
		StringUtil.printSection("System");
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		
		if (Config.DEADLOCK_DETECTOR)
		{
			LOGGER.info("Deadlock detector is enabled. Timer: {}s.", Config.DEADLOCK_CHECK_INTERVAL);
			
			final DeadLockDetector deadDetectThread = new DeadLockDetector();
			deadDetectThread.setDaemon(true);
			deadDetectThread.start();
		}
		else
			LOGGER.info("Deadlock detector is disabled.");
		
		LOGGER.info("Gameserver has started, used memory: {} / {} Mo.", SysUtil.getUsedMemory(), SysUtil.getMaxMemory());
		LOGGER.info("Maximum allowed players: {}.", Config.MAXIMUM_ONLINE_USERS);
		
		StringUtil.printSection("Login");
		LoginServerThread.getInstance().start();
		
		final SelectorConfig sc = new SelectorConfig();
		sc.MAX_READ_PER_PASS = Config.MMO_MAX_READ_PER_PASS;
		sc.MAX_SEND_PER_PASS = Config.MMO_MAX_SEND_PER_PASS;
		sc.SLEEP_TIME = Config.MMO_SELECTOR_SLEEP_TIME;
		sc.HELPER_BUFFER_COUNT = Config.MMO_HELPER_BUFFER_COUNT;
		
		final GamePacketHandler handler = new GamePacketHandler();
		_selectorThread = new SelectorThread<>(sc, handler, handler, handler, new IPv4Filter());
		
		InetAddress bindAddress = null;
		if (!Config.GAMESERVER_HOSTNAME.equals("*"))
		{
			try
			{
				bindAddress = InetAddress.getByName(Config.GAMESERVER_HOSTNAME);
			}
			catch (Exception e)
			{
				LOGGER.error("The GameServer bind address is invalid, using all available IPs.", e);
			}
		}
		
		try
		{
			_selectorThread.openServerSocket(bindAddress, Config.GAMESERVER_PORT);
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to open server socket.", e);
			System.exit(1);
		}
		_selectorThread.start();
	}
	
	public static GameServer getInstance()
	{
		return _gameServer;
	}
	
	public SelectorThread<GameClient> getSelectorThread()
	{
		return _selectorThread;
	}
}