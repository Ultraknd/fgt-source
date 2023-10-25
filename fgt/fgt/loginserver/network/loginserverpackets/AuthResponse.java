package fgt.loginserver.network.loginserverpackets;

import fgt.loginserver.data.manager.GameServerManager;
import fgt.loginserver.network.serverpackets.ServerBasePacket;

public class AuthResponse extends ServerBasePacket
{
	public AuthResponse(int serverId)
	{
		writeC(0x02);
		writeC(serverId);
		writeS(GameServerManager.getInstance().getServerNames().get(serverId));
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}