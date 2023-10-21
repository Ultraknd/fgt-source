package fgt.gameserver.scripting.task;

import fgt.gameserver.data.sql.ClanTable;
import fgt.gameserver.model.pledge.Clan;
import fgt.gameserver.model.pledge.ClanMember;
import fgt.gameserver.scripting.ScheduledQuest;

public class ClanLeaderTransfer extends ScheduledQuest
{
	public ClanLeaderTransfer()
	{
		super(-1, "task");
	}
	
	@Override
	public final void onStart()
	{
		for (Clan clan : ClanTable.getInstance().getClans())
		{
			if (clan.getNewLeaderId() <= 0)
				continue;
			
			final ClanMember member = clan.getClanMember(clan.getNewLeaderId());
			if (member == null)
			{
				clan.setNewLeaderId(0, true);
				continue;
			}
			
			clan.setNewLeader(member);
		}
	}
	
	@Override
	public final void onEnd()
	{
	}
}