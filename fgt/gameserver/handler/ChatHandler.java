package fgt.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import fgt.gameserver.enums.SayType;
import fgt.gameserver.handler.chathandlers.ChatAll;
import fgt.gameserver.handler.chathandlers.ChatAlliance;
import fgt.gameserver.handler.chathandlers.ChatClan;
import fgt.gameserver.handler.chathandlers.ChatHeroVoice;
import fgt.gameserver.handler.chathandlers.ChatParty;
import fgt.gameserver.handler.chathandlers.ChatPartyMatchRoom;
import fgt.gameserver.handler.chathandlers.ChatPartyRoomAll;
import fgt.gameserver.handler.chathandlers.ChatPartyRoomCommander;
import fgt.gameserver.handler.chathandlers.ChatPetition;
import fgt.gameserver.handler.chathandlers.ChatShout;
import fgt.gameserver.handler.chathandlers.ChatTell;
import fgt.gameserver.handler.chathandlers.ChatTrade;

public class ChatHandler
{
	private final Map<SayType, IChatHandler> _entries = new HashMap<>();
	
	protected ChatHandler()
	{
		registerHandler(new ChatAll());
		registerHandler(new ChatAlliance());
		registerHandler(new ChatClan());
		registerHandler(new ChatHeroVoice());
		registerHandler(new ChatParty());
		registerHandler(new ChatPartyMatchRoom());
		registerHandler(new ChatPartyRoomAll());
		registerHandler(new ChatPartyRoomCommander());
		registerHandler(new ChatPetition());
		registerHandler(new ChatShout());
		registerHandler(new ChatTell());
		registerHandler(new ChatTrade());
	}
	
	private void registerHandler(IChatHandler handler)
	{
		for (SayType type : handler.getChatTypeList())
			_entries.put(type, handler);
	}
	
	public IChatHandler getHandler(SayType chatType)
	{
		return _entries.get(chatType);
	}
	
	public int size()
	{
		return _entries.size();
	}
	
	public static ChatHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ChatHandler INSTANCE = new ChatHandler();
	}
}