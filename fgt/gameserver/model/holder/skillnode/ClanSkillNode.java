package fgt.gameserver.model.holder.skillnode;

import fgt.commons.data.StatSet;

/**
 * A datatype used by clan skill types. It extends {@link GeneralSkillNode}.
 */
public final class ClanSkillNode extends GeneralSkillNode
{
	private final int _itemId;
	
	public ClanSkillNode(StatSet set)
	{
		super(set);
		
		_itemId = set.getInteger("itemId");
	}
	
	public int getItemId()
	{
		return _itemId;
	}
}