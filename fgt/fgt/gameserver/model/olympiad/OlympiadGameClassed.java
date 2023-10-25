package fgt.gameserver.model.olympiad;

import java.util.List;

import fgt.commons.random.Rnd;

import fgt.Config;
import fgt.gameserver.enums.OlympiadType;
import fgt.gameserver.model.holder.IntIntHolder;

public class OlympiadGameClassed extends OlympiadGameNormal
{
	private OlympiadGameClassed(int id, Participant[] opponents)
	{
		super(id, opponents);
	}
	
	@Override
	public final OlympiadType getType()
	{
		return OlympiadType.CLASSED;
	}
	
	@Override
	protected final int getDivider()
	{
		return Config.OLY_DIVIDER_CLASSED;
	}
	
	@Override
	protected final IntIntHolder[] getReward()
	{
		return Config.OLY_CLASSED_REWARD;
	}
	
	protected static final OlympiadGameClassed createGame(int id, List<List<Integer>> classList)
	{
		if (classList == null || classList.isEmpty())
			return null;
		
		List<Integer> list;
		Participant[] opponents;
		while (!classList.isEmpty())
		{
			list = Rnd.get(classList);
			if (list == null || list.size() < 2)
			{
				classList.remove(list);
				continue;
			}
			
			opponents = OlympiadGameNormal.createListOfParticipants(list);
			if (opponents == null)
			{
				classList.remove(list);
				continue;
			}
			
			return new OlympiadGameClassed(id, opponents);
		}
		return null;
	}
}