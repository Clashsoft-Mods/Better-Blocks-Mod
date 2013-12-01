package clashsoft.mods.betterblocks.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import clashsoft.mods.betterblocks.block.BlockMobSpawner2;

public class ItemBlockMobSpawner2 extends ItemBlock
{	
	public ItemBlockMobSpawner2(int par1)
	{
		super(par1);
	}

	public static TileEntityMobSpawner getSpawner(ItemStack par1)
	{
		TileEntityMobSpawner spawner = new TileEntityMobSpawner();
		if (par1 != null && par1.hasTagCompound())
		{
			spawner.readFromNBT(par1.getTagCompound());
		}
		BlockMobSpawner2.defaultSpawner(spawner);
		if (!par1.hasTagCompound())
			par1.setTagCompound(new NBTTagCompound());
		spawner.writeToNBT(par1.getTagCompound());
		return spawner;
	}

	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		TileEntityMobSpawner spawner = getSpawner(par1ItemStack);

		if (spawner != null)
		{
			String italic = "" + EnumChatFormatting.ITALIC;
			par3List.add("Entity: " + EnumChatFormatting.ITALIC + spawner.getSpawnerLogic().getEntityNameToSpawn());

			if(Keyboard.isKeyDown(Keyboard.KEY_CAPITAL))
			{
				par3List.add("Spawn Delay: " + EnumChatFormatting.ITALIC + spawner.getSpawnerLogic().spawnDelay);
				par3List.add("@SpawnerPos(" + spawner.getSpawnerLogic().getSpawnerX() + "," + spawner.getSpawnerLogic().getSpawnerY() + "," + spawner.getSpawnerLogic().getSpawnerZ() + ")");
			}
		}
	}

	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		if (!world.setBlock(x, y, z, this.getBlockID(), metadata, 3))
		{
			return false;
		}

		TileEntityMobSpawner spawner = getSpawner(stack);

		world.setBlockTileEntity(x, y, z, spawner);

		if (world.getBlockId(x, y, z) == this.getBlockID())
		{
			Block.blocksList[this.getBlockID()].onBlockPlacedBy(world, x, y, z, player, stack);
			Block.blocksList[this.getBlockID()].onPostBlockPlaced(world, x, y, z, metadata);
		}

		return true;
	}
}
