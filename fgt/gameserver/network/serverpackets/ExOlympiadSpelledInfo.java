package fgt.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.holder.EffectHolder;
import fgt.gameserver.skills.L2Skill;

public class ExOlympiadSpelledInfo extends L2GameServerPacket
{
	private final int _objectId;
	private final List<EffectHolder> _effects = new ArrayList<>();
	
	public ExOlympiadSpelledInfo(Player player)
	{
		_objectId = player.getObjectId();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xfe);
		writeH(0x2a);
		
		writeD(_objectId);
		
		writeD(_effects.size());
		for (EffectHolder holder : _effects)
		{
			writeD(holder.getId());
			writeH(holder.getValue());
			writeD(holder.getDuration() / 1000);
		}
	}
	
	public void addEffect(L2Skill skill, int duration)
	{
		_effects.add(new EffectHolder(skill, duration));
	}
}