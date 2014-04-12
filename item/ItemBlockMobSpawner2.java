package clashsoft.mods.betterblocks.item;

import java.util.List;

import clashsoft.cslib.minecraft.lang.I18n;
import clashsoft.mods.betterblocks.block.BlockMobSpawner2;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ItemBlockMobSpawner2 extends ItemBlock
{
	public ItemBlockMobSpawner2(Block block)
	{
		super(block);
	}
	
	public static TileEntityMobSpawner getSpawner(ItemStack stack)
	{
		TileEntityMobSpawner spawner = new TileEntityMobSpawner();
		if (stack != null && stack.hasTagCompound())
		{
			spawner.readFromNBT(stack.getTagCompound());
			BlockMobSpawner2.defaultSpawner(spawner);
			spawner.writeToNBT(stack.getTagCompound());
		}
		return spawner;
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag)
	{
		TileEntityMobSpawner spawner = getSpawner(stack);
		
		if (spawner != null)
		{
			MobSpawnerBaseLogic logic = spawner.func_145881_a();
			
			String italic = "" + EnumChatFormatting.ITALIC;
			list.add("Entity: " + EnumChatFormatting.ITALIC + I18n.getString("entity." + logic.getEntityNameToSpawn() + ".name"));
			
			if (flag)
			{
				list.add("Spawn Delay: " + EnumChatFormatting.ITALIC + logic.spawnDelay);
			}
		}
	}
	
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		if (!world.setBlock(x, y, z, this.field_150939_a, metadata, 3))
		{
			return false;
		}
		
		TileEntityMobSpawner spawner = getSpawner(stack);
		
		world.setTileEntity(x, y, z, spawner);
		
		if (world.getBlock(x, y, z) == this.field_150939_a)
		{
			this.field_150939_a.onBlockPlacedBy(world, x, y, z, player, stack);
			this.field_150939_a.onPostBlockPlaced(world, x, y, z, metadata);
		}
		
		return true;
	}
}
