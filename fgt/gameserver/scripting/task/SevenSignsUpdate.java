package fgt.gameserver.scripting.task;

import fgt.gameserver.data.manager.FestivalOfDarknessManager;
import fgt.gameserver.data.manager.SevenSignsManager;
import fgt.gameserver.scripting.ScheduledQuest;

public final class SevenSignsUpdate extends ScheduledQuest
{
	public SevenSignsUpdate()
	{
		super(-1, "task");
	}
	
	@Override
	public final void onStart()
	{
		if (!SevenSignsManager.getInstance().isSealValidationPeriod())
			FestivalOfDarknessManager.getInstance().saveFestivalData(false);
		
		SevenSignsManager.getInstance().saveSevenSignsData();
		SevenSignsManager.getInstance().saveSevenSignsStatus();
	}
	
	@Override
	public final void onEnd()
	{
	}
}