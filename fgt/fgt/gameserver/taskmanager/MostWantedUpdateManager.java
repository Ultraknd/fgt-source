package fgt.gameserver.taskmanager;

import fgt.Config;
import fgt.commons.logging.CLogger;
import fgt.commons.pool.ThreadPool;
import fgt.commons.random.Rnd;
import fgt.gameserver.data.xml.AnnouncementData;
import fgt.gameserver.model.World;
import fgt.gameserver.model.actor.Player;

import java.util.Collection;

public class MostWantedUpdateManager
{
    private static final CLogger _log = new CLogger(MostWantedUpdateManager.class.getName());
        		
	protected MostWantedUpdateManager()
	{                
            ThreadPool.scheduleAtFixedRate(new MostWantedUpdate(), 300000, Config.MOSTWANTED_UPDATE_INTERVAL);
	}
	
	public static MostWantedUpdateManager getInstance()
	{
            return SingletonHolder._instance;
	}
	
	private class MostWantedUpdate implements Runnable
	{
		public MostWantedUpdate()
		{
		}
		
		@Override
		public void run()
		{                    
                   //MostWanted();
                    try
                    {
                        Collection<Player> pls = World.getInstance().getPlayers();
                        for (Player player : pls)
                        {
                            player.setIsMostWanted(false);
                            player.unsetVar("isWanted");
                            
                            if (World.getInstance().getMostWantedPlayer() == null)
                            {
                                if (player.isOnline() && !player.isMostWanted() && Rnd.chance(Config.MOSTWANTED_CHANCE))
                                {
                                    player.setIsMostWanted(true);                            
                                    AnnouncementData.getInstance().handleAnnounce("Внимание!!! Разыскиваемый игрок " + player.getName() + "!!! Убейте его для получения награды!!!", 0, false);
                                    _log.info("MostWanted: Разыскиваемый игрок выбран и назначен!!!");                                    
                                }                                
                            }                            
                        }                         
                    }
                    catch (Exception e)
                    {
			_log.error("WARNING: [MostWanted]: Ошибка выбора и назначения разыскиваемого игрока: " + e);
                    }
		}
	}
        	
	private static class SingletonHolder
	{
            protected static final MostWantedUpdateManager _instance = new MostWantedUpdateManager();
	}
}