package fgt.gameserver.network.serverpackets;

import java.util.List;

import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.item.Henna;

public class HennaUnequipList extends L2GameServerPacket
{
	private final int _adena;
	private final int _emptySlots;
	private final List<Henna> _hennas;
	
	public HennaUnequipList(Player player)
	{
		_adena = player.getAdena();
		_emptySlots = player.getHennaList().getEmptySlotsAmount();
		_hennas = player.getHennaList().getHennas();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xe5);
		writeD(_adena);
		writeD(_emptySlots);
		writeD(_hennas.size());
		
		for (Henna henna : _hennas)
		{
			writeD(henna.getSymbolId());
			writeD(henna.getDyeId());
			writeD(Henna.REMOVE_AMOUNT);
			writeD(henna.getRemovePrice());
			writeD(0x01);
		}
	}
}