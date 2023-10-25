package fgt.gameserver.network.clientpackets;

import fgt.gameserver.network.serverpackets.NewCharacterSuccess;

public final class RequestNewCharacter extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		sendPacket(NewCharacterSuccess.STATIC_PACKET);
	}
}