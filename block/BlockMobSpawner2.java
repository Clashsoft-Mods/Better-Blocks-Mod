package clashsoft.mods.betterblocks.block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BlockMobSpawner2 extends BlockMobSpawner
{
	public TileEntityMobSpawner	temp	= null;
	
	public BlockMobSpawner2()
	{
		this.disableStats();
	}
	
	public static List<String> getSpawnableEntityNames()
	{
		List<String> list = new LinkedList<String>();
		
		Iterator iterator = EntityList.entityEggs.values().iterator();
		
		while (iterator.hasNext())
		{
			EntityEggInfo entityegginfo = (EntityEggInfo) iterator.next();
			list.add(EntityList.getStringFromID(entityegginfo.spawnedID));
		}
		
		list.add("SnowMan");
		list.add("VillagerGolem");
		list.add("Giant");
		
		return list;
	}
	
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list)
	{
		for (String s : getSpawnableEntityNames())
		{
			list.add(this.getSpawnerStack(s));
		}
	}
	
	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta)
	{
		return true;
	}
	
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
	{
		return new ArrayList<ItemStack>();
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldMetadata)
	{
		this.temp = (TileEntityMobSpawner) world.getTileEntity(x, y, z);
		super.breakBlock(world, x, y, z, oldBlock, oldMetadata);
	}
	
	@Override
	public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int metadata)
	{
		player.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
		player.addExhaustion(0.025F);
		
		if (!player.capabilities.isCreativeMode)
		{
			if (EnchantmentHelper.getSilkTouchModifier(player))
			{
				ItemStack itemstack = new ItemStack(this, 1, 0);
				
				if (this.temp == null)
				{
					this.temp = (TileEntityMobSpawner) world.getTileEntity(x, y, z);
				}
				
				if (this.temp != null)
				{
					itemstack = this.getSpawnerStack(this.temp);
					
					if (itemstack != null)
					{
						this.dropBlockAsItem(world, x, y, z, itemstack);
					}
					this.temp = null;
				}
				world.removeTileEntity(x, y, z);
			}
			else
			{
				super.harvestBlock(world, player, x, y, z, metadata);
				int xp = 15 + world.rand.nextInt(15) + world.rand.nextInt(15);
				this.dropXpOnBlockBreak(world, x, y, z, xp);
			}
		}
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		return this.getSpawnerStack((TileEntityMobSpawner) world.getTileEntity(x, y, z));
	}
	
	public ItemStack getSpawnerStack(TileEntityMobSpawner spawner)
	{
		ItemStack itemstack = new ItemStack(this, 1, 0);
		
		if (itemstack.getTagCompound() == null)
			itemstack.setTagCompound(new NBTTagCompound());
		spawner = defaultSpawner(spawner);
		spawner.writeToNBT(itemstack.getTagCompound());
		
		return itemstack;
	}
	
	public ItemStack getSpawnerStack(String entityname)
	{
		TileEntityMobSpawner spawner = new TileEntityMobSpawner();
		spawner.func_145881_a().setEntityName(entityname);
		;
		return this.getSpawnerStack(spawner);
	}
	
	public static TileEntityMobSpawner defaultSpawner(TileEntityMobSpawner spawner)
	{
		if (spawner != null)
		{
			spawner.func_145881_a().spawnDelay = 0;
			spawner.xCoord = 0;
			spawner.yCoord = 0;
			spawner.zCoord = 0;
		}
		return spawner;
	}
}
