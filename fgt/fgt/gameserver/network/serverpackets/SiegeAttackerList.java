package fgt.gameserver.network.serverpackets;

import java.util.List;

import fgt.gameserver.model.clanhall.SiegableHall;
import fgt.gameserver.model.entity.Castle;
import fgt.gameserver.model.pledge.Clan;

public class SiegeAttackerList extends L2GameServerPacket
{
	private final int _id;
	private final List<Clan> _attackers;
	
	public SiegeAttackerList(Castle castle)
	{
		_id = castle.getCastleId();
		_attackers = castle.getSiege().getAttackerClans();
	}
	
	public SiegeAttackerList(SiegableHall hall)
	{
		_id = hall.getId();
		_attackers = hall.getSiege().getAttackerClans();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xca);
		writeD(_id);
		writeD(0x00);
		writeD(0x01);
		writeD(0x00);
		
		final int size = _attackers.size();
		
		if (size > 0)
		{
			writeD(size);
			writeD(size);
			
			for (Clan clan : _attackers)
			{
				writeD(clan.getClanId());
				writeS(clan.getName());
				writeS(clan.getLeaderName());
				writeD(clan.getCrestId());
				writeD(0x00);
				writeD(clan.getAllyId());
				writeS(clan.getAllyName());
				writeS("");
				writeD(clan.getAllyCrestId());
			}
		}
		else
		{
			writeD(0x00);
			writeD(0x00);
		}
	}
}