package fgt.gameserver.network.serverpackets;

import java.util.List;

import fgt.gameserver.enums.actors.HennaType;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.actor.container.player.HennaList;
import fgt.gameserver.model.item.Henna;

public final class HennaInfo extends L2GameServerPacket
{
	private final HennaList _hennaList;
	
	public HennaInfo(Player player)
	{
		_hennaList = player.getHennaList();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xe4);
		
		writeC(_hennaList.getStat(HennaType.INT));
		writeC(_hennaList.getStat(HennaType.STR));
		writeC(_hennaList.getStat(HennaType.CON));
		writeC(_hennaList.getStat(HennaType.MEN));
		writeC(_hennaList.getStat(HennaType.DEX));
		writeC(_hennaList.getStat(HennaType.WIT));
		
		writeD(_hennaList.getMaxSize());
		
		final List<Henna> hennas = _hennaList.getHennas();
		writeD(hennas.size());
		for (Henna h : hennas)
		{
			writeD(h.getSymbolId());
			writeD(_hennaList.canBeUsedBy(h) ? h.getSymbolId() : 0);
		}
	}
}