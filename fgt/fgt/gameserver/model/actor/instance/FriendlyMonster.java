package fgt.gameserver.model.actor.instance;

import java.util.List;

import fgt.commons.random.Rnd;

import fgt.gameserver.enums.ScriptEventType;
import fgt.gameserver.model.actor.Attackable;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.actor.template.NpcTemplate;
import fgt.gameserver.scripting.Quest;

/**
 * This class represents Friendly Mobs lying over the world.<br>
 * These friendly mobs should only attack players with karma > 0 and it is always aggro, since it just attacks players with karma.
 */
public class FriendlyMonster extends Attackable
{
	public FriendlyMonster(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onInteract(Player player)
	{
		if (hasRandomAnimation())
			onRandomAnimation(Rnd.get(8));
		
		player.getQuestList().setLastQuestNpcObjectId(getObjectId());
		
		final List<Quest> scripts = getTemplate().getEventQuests(ScriptEventType.ON_FIRST_TALK);
		if (scripts.size() == 1)
			scripts.get(0).notifyFirstTalk(this, player);
		else
			showChatWindow(player);
	}
	
	@Override
	public boolean isAggressive()
	{
		return true;
	}
}