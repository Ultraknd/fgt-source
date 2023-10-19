package fgt.gameserver.network.serverpackets;

import fgt.gameserver.taskmanager.GameTimeTaskManager;

public class ClientSetTime extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(0xEC);
		writeD(GameTimeTaskManager.getInstance().getGameTime());
		writeD(6);
	}
}