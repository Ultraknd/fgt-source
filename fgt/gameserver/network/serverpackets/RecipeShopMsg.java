package fgt.gameserver.network.serverpackets;

import fgt.gameserver.model.actor.Player;

public class RecipeShopMsg extends L2GameServerPacket
{
	private final Player _player;
	
	public RecipeShopMsg(Player player)
	{
		_player = player;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xdb);
		
		writeD(_player.getObjectId());
		writeS(_player.getManufactureList().getStoreName());
	}
}