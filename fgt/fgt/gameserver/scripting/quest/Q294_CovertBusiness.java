package fgt.gameserver.scripting.quest;

import fgt.commons.random.Rnd;

import fgt.gameserver.enums.QuestStatus;
import fgt.gameserver.enums.actors.ClassRace;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Npc;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.scripting.Quest;
import fgt.gameserver.scripting.QuestState;

public class Q294_CovertBusiness extends Quest
{
	private static final String QUEST_NAME = "Q294_CovertBusiness";
	
	// Item
	private static final int BAT_FANG = 1491;
	
	// Reward
	private static final int RING_OF_RACCOON = 1508;
	
	public Q294_CovertBusiness()
	{
		super(294, "Covert Business");
		
		setItemsIds(BAT_FANG);
		
		addStartNpc(30534); // Keef
		addTalkId(30534);
		
		addKillId(20370, 20480); // Barded Bat, Blade Bat
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30534-03.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case CREATED:
				if (player.getRace() != ClassRace.DWARF)
					htmltext = "30534-00.htm";
				else if (player.getStatus().getLevel() < 10)
					htmltext = "30534-01.htm";
				else
					htmltext = "30534-02.htm";
				break;
			
			case STARTED:
				if (st.getCond() == 1)
					htmltext = "30534-04.htm";
				else
				{
					if (!player.getInventory().hasItems(RING_OF_RACCOON))
					{
						htmltext = "30534-05.htm";
						takeItems(player, BAT_FANG, -1);
						giveItems(player, RING_OF_RACCOON, 1);
					}
					else
					{
						htmltext = "30534-06.htm";
						takeItems(player, BAT_FANG, -1);
						rewardItems(player, 57, 2400);
					}
					rewardExpAndSp(player, 0, 600);
					playSound(player, SOUND_FINISH);
					st.exitQuest(true);
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		
		final QuestState st = checkPlayerCondition(player, npc, 1);
		if (st == null)
			return null;
		
		int count = 1;
		final int chance = Rnd.get(10);
		final boolean isBarded = (npc.getNpcId() == 20370);
		
		if (chance < 3)
			count++;
		else if (chance < ((isBarded) ? 5 : 6))
			count += 2;
		else if (isBarded && chance < 7)
			count += 3;
		
		if (dropItemsAlways(player, BAT_FANG, count, 100))
			st.setCond(2);
		
		return null;
	}
}