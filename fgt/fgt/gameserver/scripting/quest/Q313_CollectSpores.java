package fgt.gameserver.scripting.quest;

import fgt.gameserver.enums.QuestStatus;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Npc;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.scripting.Quest;
import fgt.gameserver.scripting.QuestState;

public class Q313_CollectSpores extends Quest
{
	private static final String QUEST_NAME = "Q313_CollectSpores";
	
	// Item
	private static final int SPORE_SAC = 1118;
	
	public Q313_CollectSpores()
	{
		super(313, "Collect Spores");
		
		setItemsIds(SPORE_SAC);
		
		addStartNpc(30150); // Herbiel
		addTalkId(30150);
		
		addKillId(20509); // Spore Fungus
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30150-05.htm"))
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
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		String htmltext = getNoQuestMsg();
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = (player.getStatus().getLevel() < 8) ? "30150-02.htm" : "30150-03.htm";
				break;
			
			case STARTED:
				if (st.getCond() == 1)
					htmltext = "30150-06.htm";
				else
				{
					htmltext = "30150-07.htm";
					takeItems(player, SPORE_SAC, -1);
					rewardItems(player, 57, 3500);
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
		
		if (dropItems(player, SPORE_SAC, 1, 10, 400000))
			st.setCond(2);
		
		return null;
	}
}