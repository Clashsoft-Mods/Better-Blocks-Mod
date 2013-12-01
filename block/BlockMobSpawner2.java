package clashsoft.mods.betterblocks.block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.BlockMobSpawner;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityEggInfo;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BlockMobSpawner2 extends BlockMobSpawner
{
	public TileEntityMobSpawner	temp	= null;
	
	public BlockMobSpawner2(int par1)
	{
		super(par1);
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
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (String s : getSpawnableEntityNames())
		{
			par3List.add(getSpawnerStack(s));
		}
	}
	
	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta)
	{
		return true;
	}
	
	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
	{
		return new ArrayList<ItemStack>();
	}
	
	@Override
	public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6)
	{
		temp = (TileEntityMobSpawner) par1World.getBlockTileEntity(par2, par3, par4);
		par1World.removeBlockTileEntity(par2, par3, par4);
	}
	
	@Override
	public void harvestBlock(World par1World, EntityPlayer par2EntityPlayer, int par3, int par4, int par5, int par6)
	{
		par2EntityPlayer.addStat(StatList.mineBlockStatArray[this.blockID], 1);
		par2EntityPlayer.addExhaustion(0.025F);
		
		if (!par2EntityPlayer.capabilities.isCreativeMode)
		{
			if (EnchantmentHelper.getSilkTouchModifier(par2EntityPlayer))
			{
				ItemStack itemstack = new ItemStack(this, 1, 0);
				
				if (temp == null)
					temp = (TileEntityMobSpawner) par1World.getBlockTileEntity(par3, par4, par5);
				
				if (temp != null)
				{
					itemstack = getSpawnerStack(temp);
					
					if (itemstack != null)
					{
						this.dropBlockAsItem_do(par1World, par3, par4, par5, itemstack);
					}
					temp = null;
				}
				par1World.removeBlockTileEntity(par3, par4, par5);
			}
			else
			{
				super.harvestBlock(par1World, par2EntityPlayer, par3, par4, par5, par6);
				int xp = 15 + par1World.rand.nextInt(15) + par1World.rand.nextInt(15);
				this.dropXpOnBlockBreak(par1World, par3, par4, par5, xp);
			}
		}
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		return getSpawnerStack((TileEntityMobSpawner) world.getBlockTileEntity(x, y, z));
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
		spawner.getSpawnerLogic().setMobID(entityname);
		return getSpawnerStack(spawner);
	}
	
	public static TileEntityMobSpawner defaultSpawner(TileEntityMobSpawner spawner)
	{
		if (spawner != null)
		{
			spawner.getSpawnerLogic().spawnDelay = 0;
			spawner.xCoord = 0;
			spawner.yCoord = 0;
			spawner.zCoord = 0;
		}
		return spawner;
	}
}
