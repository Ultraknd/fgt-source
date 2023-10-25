package fgt.gameserver.network.clientpackets;

import fgt.gameserver.data.manager.DuelManager;

public final class RequestDuelSurrender extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		DuelManager.getInstance().doSurrender(getClient().getPlayer());
	}
}