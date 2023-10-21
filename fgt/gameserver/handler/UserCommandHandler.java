package fgt.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import fgt.gameserver.handler.usercommandhandlers.ChannelDelete;
import fgt.gameserver.handler.usercommandhandlers.ChannelLeave;
import fgt.gameserver.handler.usercommandhandlers.ChannelListUpdate;
import fgt.gameserver.handler.usercommandhandlers.ClanPenalty;
import fgt.gameserver.handler.usercommandhandlers.ClanWarsList;
import fgt.gameserver.handler.usercommandhandlers.Dismount;
import fgt.gameserver.handler.usercommandhandlers.Escape;
import fgt.gameserver.handler.usercommandhandlers.Loc;
import fgt.gameserver.handler.usercommandhandlers.Mount;
import fgt.gameserver.handler.usercommandhandlers.OlympiadStat;
import fgt.gameserver.handler.usercommandhandlers.PartyInfo;
import fgt.gameserver.handler.usercommandhandlers.SiegeStatus;
import fgt.gameserver.handler.usercommandhandlers.Time;

public class UserCommandHandler
{
	private final Map<Integer, IUserCommandHandler> _entries = new HashMap<>();
	
	protected UserCommandHandler()
	{
		registerHandler(new ChannelDelete());
		registerHandler(new ChannelLeave());
		registerHandler(new ChannelListUpdate());
		registerHandler(new ClanPenalty());
		registerHandler(new ClanWarsList());
		registerHandler(new Dismount());
		registerHandler(new Escape());
		registerHandler(new Loc());
		registerHandler(new Mount());
		registerHandler(new OlympiadStat());
		registerHandler(new PartyInfo());
		registerHandler(new SiegeStatus());
		registerHandler(new Time());
	}
	
	private void registerHandler(IUserCommandHandler handler)
	{
		for (int id : handler.getUserCommandList())
			_entries.put(id, handler);
	}
	
	public IUserCommandHandler getHandler(int userCommand)
	{
		return _entries.get(userCommand);
	}
	
	public int size()
	{
		return _entries.size();
	}
	
	public static UserCommandHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final UserCommandHandler INSTANCE = new UserCommandHandler();
	}
}