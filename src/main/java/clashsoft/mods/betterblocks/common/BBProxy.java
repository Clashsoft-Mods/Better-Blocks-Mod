package clashsoft.mods.betterblocks.common;

import clashsoft.cslib.minecraft.common.BaseProxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BBProxy extends BaseProxy
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}
}
