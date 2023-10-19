package fgt.gameserver.model.actor.instance;

import fgt.gameserver.data.manager.SevenSignsManager;
import fgt.gameserver.enums.CabalType;
import fgt.gameserver.enums.SealType;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.actor.template.NpcTemplate;
import fgt.gameserver.network.serverpackets.ActionFailed;
import fgt.gameserver.network.serverpackets.NpcHtmlMessage;

public class DuskPriest extends SignsPriest
{
	public DuskPriest(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("Chat"))
			showChatWindow(player);
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		
		String filename = SevenSignsManager.SEVEN_SIGNS_HTML_PATH;
		
		final CabalType winningCabal = SevenSignsManager.getInstance().getWinningCabal();
		
		switch (SevenSignsManager.getInstance().getPlayerCabal(player.getObjectId()))
		{
			case DUSK:
				if (SevenSignsManager.getInstance().isCompResultsPeriod())
					filename += "dusk_priest_5.htm";
				else if (SevenSignsManager.getInstance().isRecruitingPeriod())
					filename += "dusk_priest_6.htm";
				else if (SevenSignsManager.getInstance().isSealValidationPeriod())
				{
					if (winningCabal == CabalType.DUSK)
					{
						if (winningCabal != SevenSignsManager.getInstance().getSealOwner(SealType.GNOSIS))
							filename += "dusk_priest_2c.htm";
						else
							filename += "dusk_priest_2a.htm";
					}
					else if (winningCabal == CabalType.NORMAL)
						filename += "dusk_priest_2d.htm";
					else
						filename += "dusk_priest_2b.htm";
				}
				else
					filename += "dusk_priest_1.htm";
				break;
			
			case DAWN:
				filename += "dusk_priest_3.htm";
				break;
			
			default:
				if (SevenSignsManager.getInstance().isCompResultsPeriod())
					filename += "dusk_priest_5.htm";
				else if (SevenSignsManager.getInstance().isRecruitingPeriod())
					filename += "dusk_priest_6.htm";
				else if (SevenSignsManager.getInstance().isSealValidationPeriod())
				{
					if (winningCabal == CabalType.DUSK)
						filename += "dusk_priest_4.htm";
					else if (winningCabal == CabalType.NORMAL)
						filename += "dusk_priest_2d.htm";
					else
						filename += "dusk_priest_2b.htm";
				}
				else
					filename += "dusk_priest_1.htm";
				break;
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
}