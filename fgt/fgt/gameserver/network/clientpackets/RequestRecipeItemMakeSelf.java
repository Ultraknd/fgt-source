package fgt.gameserver.network.clientpackets;

import fgt.gameserver.data.xml.RecipeData;
import fgt.gameserver.enums.FloodProtector;
import fgt.gameserver.enums.actors.OperateType;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.craft.RecipeItemMaker;
import fgt.gameserver.model.item.Recipe;
import fgt.gameserver.network.SystemMessageId;

public final class RequestRecipeItemMakeSelf extends L2GameClientPacket
{
	private int _recipeId;
	
	@Override
	protected void readImpl()
	{
		_recipeId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (!getClient().performAction(FloodProtector.MANUFACTURE))
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (player.getOperateType() == OperateType.MANUFACTURE || player.isCrafting())
			return;
		
		if (player.isInDuel() || player.isInCombat())
		{
			player.sendPacket(SystemMessageId.CANT_OPERATE_PRIVATE_STORE_DURING_COMBAT);
			return;
		}
		
		final Recipe recipe = RecipeData.getInstance().getRecipeList(_recipeId);
		if (recipe == null)
			return;
		
		if (!player.getRecipeBook().hasRecipeOnSpecificBook(_recipeId, recipe.isDwarven()))
			return;
		
		final RecipeItemMaker maker = new RecipeItemMaker(player, recipe, player);
		if (maker._isValid)
			maker.run();
	}
}