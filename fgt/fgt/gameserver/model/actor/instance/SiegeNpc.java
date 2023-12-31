package fgt.gameserver.model.actor.instance;

import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.actor.template.NpcTemplate;
import fgt.gameserver.network.serverpackets.ActionFailed;
import fgt.gameserver.network.serverpackets.NpcHtmlMessage;
import fgt.gameserver.network.serverpackets.SiegeInfo;

public class SiegeNpc extends Folk
{
	public SiegeNpc(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		final boolean isOwningClanLeader = isLordOwner(player);
		final boolean isUnderSiege = ((getSiegableHall() != null && getSiegableHall().isInSiege()) || (getCastle() != null && getCastle().getSiege().isInProgress()));
		
		String htmltext = null;
		
		if (isOwningClanLeader)
			htmltext = (isUnderSiege) ? "03" : "01";
		else if (isUnderSiege)
			htmltext = "02";
		else
		{
			if (getSiegableHall() != null)
				player.sendPacket(new SiegeInfo(getSiegableHall()));
			else if (getCastle() != null)
				player.sendPacket(new SiegeInfo(getCastle()));
		}
		
		if (htmltext != null)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile("data/html/siege/" + htmltext + ".htm");
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
	}
}