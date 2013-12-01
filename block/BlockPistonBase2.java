package clashsoft.mods.betterblocks.block;

import clashsoft.mods.betterblocks.tileentity.TileEntityPiston2;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockSnow;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.Facing;
import net.minecraft.world.World;

public class BlockPistonBase2 extends BlockPistonBase
{
	public boolean	isSticky	= false;
	
	public BlockPistonBase2(int blockID, boolean isSticky)
	{
		super(blockID, isSticky);
		this.isSticky = isSticky;
	}
	
	/**
	 * When this method is called, your block should register all the icons it
	 * needs with the given IconRegister. This is the only chance you get to
	 * register icons.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister)
	{
		super.registerIcons(iconRegister);
		if (isSticky)
			Block.pistonStickyBase.registerIcons(iconRegister);
		else
			Block.pistonBase.registerIcons(iconRegister);
	}
	
	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack)
	{
		int l = determineOrientation(par1World, par2, par3, par4, par5EntityLivingBase);
		par1World.setBlockMetadataWithNotify(par2, par3, par4, l, 2);
		
		if (!par1World.isRemote)
		{
			this.updatePistonState(par1World, par2, par3, par4);
		}
	}
	
	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which
	 * neighbor changed (coordinates passed are their own) Args: x, y, z,
	 * neighbor blockID
	 */
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID)
	{
		if (!world.isRemote)
		{
			this.updatePistonState(world, x, y, z);
		}
	}
	
	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		if (!world.isRemote && world.getBlockTileEntity(x, y, z) == null)
		{
			this.updatePistonState(world, x, y, z);
		}
	}
	
	/**
	 * handles attempts to extend or retract the piston.
	 */
	private void updatePistonState(World world, int x, int y, int z)
	{
		int metadata = world.getBlockMetadata(x, y, z);
		int orientation = getOrientation(metadata);
		
		if (orientation != 7)
		{
			boolean flag = this.isIndirectlyPowered(world, x, y, z, orientation);
			
			if (flag && !isExtended(metadata))
			{
				if (canExtend(world, x, y, z, orientation))
				{
					world.addBlockEvent(x, y, z, this.blockID, 0, orientation);
				}
			}
			else if (!flag && isExtended(metadata))
			{
				world.setBlockMetadataWithNotify(x, y, z, orientation, 2);
				world.addBlockEvent(x, y, z, this.blockID, 1, orientation);
			}
		}
	}
	
	/**
	 * checks the block to that side to see if it is indirectly powered.
	 */
	private boolean isIndirectlyPowered(World world, int x, int y, int z, int side)
	{
		return side != 0 && world.getIndirectPowerOutput(x, y - 1, z, 0) ? true : (side != 1 && world.getIndirectPowerOutput(x, y + 1, z, 1) ? true : (side != 2 && world.getIndirectPowerOutput(x, y, z - 1, 2) ? true : (side != 3 && world.getIndirectPowerOutput(x, y, z + 1, 3) ? true : (side != 5 && world.getIndirectPowerOutput(x + 1, y, z, 5) ? true : (side != 4 && world.getIndirectPowerOutput(x - 1, y, z, 4) ? true : (world.getIndirectPowerOutput(x, y, z, 0) ? true : (world.getIndirectPowerOutput(x, y + 2, z, 1) ? true : (world.getIndirectPowerOutput(x, y + 1, z - 1, 2) ? true : (world.getIndirectPowerOutput(x, y + 1, z + 1, 3) ? true : (world.getIndirectPowerOutput(x - 1, y + 1, z, 4) ? true : world.getIndirectPowerOutput(x + 1, y + 1, z, 5)))))))))));
	}
	
	/**
	 * Called when the block receives a BlockEvent - see World.addBlockEvent. By
	 * default, passes it on to the tile entity at this location. Args: world,
	 * x, y, z, blockID, EventID, event parameter
	 */
	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z, int eventID, int direction)
	{
		if (!world.isRemote)
		{
			boolean flag = this.isIndirectlyPowered(world, x, y, z, direction);
			
			if (flag && eventID == 1)
			{
				world.setBlockMetadataWithNotify(x, y, z, direction | 8, 2);
				return false;
			}
			
			if (!flag && eventID == 0)
			{
				return false;
			}
		}
		
		if (eventID == 0)
		{
			if (!this.tryExtend(world, x, y, z, direction))
			{
				return false;
			}
			
			world.setBlockMetadataWithNotify(x, y, z, direction | 8, 2);
			world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "tile.piston.out", 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
		}
		else if (eventID == 1)
		{
			TileEntity tileentity = world.getBlockTileEntity(x + Facing.offsetsXForSide[direction], y + Facing.offsetsYForSide[direction], z + Facing.offsetsZForSide[direction]);
			
			if (tileentity instanceof TileEntityPiston)
			{
				((TileEntityPiston) tileentity).clearPistonTileEntity();
			}
			
			world.setBlock(x, y, z, Block.pistonMoving.blockID, direction, 3);
			world.setBlockTileEntity(x, y, z, getTileEntity(this.blockID, direction, tileentity, direction, false, true));
			
			if (this.isSticky)
			{
				int x1 = x + Facing.offsetsXForSide[direction] * 2;
				int y1 = y + Facing.offsetsYForSide[direction] * 2;
				int z1 = z + Facing.offsetsZForSide[direction] * 2;
				int blockID = world.getBlockId(x1, y1, z1);
				int blockMetadata = world.getBlockMetadata(x1, y1, z1);
				TileEntity tileEntity = world.getBlockTileEntity(x1, y1, z1);
				boolean flag1 = false;
				
				if (blockID == Block.pistonMoving.blockID)
				{
					TileEntity tileentity1 = world.getBlockTileEntity(x1, y1, z1);
					
					if (tileentity1 instanceof TileEntityPiston2)
					{
						TileEntityPiston2 tileentitypiston = (TileEntityPiston2) tileentity1;
						
						if (tileentitypiston.getPistonOrientation() == direction && tileentitypiston.isExtending())
						{
							tileentitypiston.clearPistonTileEntity();
							blockID = tileentitypiston.getStoredBlockID();
							blockMetadata = tileentitypiston.getBlockMetadata();
							tileEntity = tileentitypiston.storedTileEntity;
							flag1 = true;
						}
					}
				}
				
				if (!flag1 && blockID > 0 && canPushBlock(blockID, world, x1, y1, z1, false) && (Block.blocksList[blockID].getMobilityFlag() == 0 || blockID == Block.pistonBase.blockID || blockID == Block.pistonStickyBase.blockID))
				{
					x += Facing.offsetsXForSide[direction];
					y += Facing.offsetsYForSide[direction];
					z += Facing.offsetsZForSide[direction];
					
					world.setBlock(x, y, z, Block.pistonMoving.blockID, blockMetadata, 3);
					world.setBlockTileEntity(x, y, z, getTileEntity(blockID, blockMetadata, tileEntity, direction, false, false));
					world.removeBlockTileEntity(x1, y1, z1);
					world.setBlockToAir(x1, y1, z1);
				}
				else if (!flag1)
				{
					world.setBlockToAir(x + Facing.offsetsXForSide[direction], y + Facing.offsetsYForSide[direction], z + Facing.offsetsZForSide[direction]);
				}
			}
			else
			{
				world.setBlockToAir(x + Facing.offsetsXForSide[direction], y + Facing.offsetsYForSide[direction], z + Facing.offsetsZForSide[direction]);
			}
			
			world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "tile.piston.in", 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
		}
		
		return true;
	}
	
	/**
	 * returns true if the piston can push the specified block
	 */
	private static boolean canPushBlock(int par0, World par1World, int par2, int par3, int par4, boolean par5)
	{
		if (par0 == Block.obsidian.blockID)
		{
			return false;
		}
		else
		{
			if (par0 != Block.pistonBase.blockID && par0 != Block.pistonStickyBase.blockID)
			{
				if (Block.blocksList[par0].getBlockHardness(par1World, par2, par3, par4) == -1.0F)
				{
					return false;
				}
				
				if (Block.blocksList[par0].getMobilityFlag() == 2)
				{
					return false;
				}
				
				if (Block.blocksList[par0].getMobilityFlag() == 1)
				{
					if (!par5)
					{
						return false;
					}
					
					return true;
				}
			}
			else if (isExtended(par1World.getBlockMetadata(par2, par3, par4)))
			{
				return false;
			}
			
			return true;
		}
	}
	
	/**
	 * checks to see if this piston could push the blocks in front of it.
	 */
	private static boolean canExtend(World par0World, int par1, int par2, int par3, int par4)
	{
		int i1 = par1 + Facing.offsetsXForSide[par4];
		int j1 = par2 + Facing.offsetsYForSide[par4];
		int k1 = par3 + Facing.offsetsZForSide[par4];
		int l1 = 0;
		
		while (true)
		{
			if (l1 < 13)
			{
				if (j1 <= 0 || j1 >= par0World.getHeight() - 1)
				{
					return false;
				}
				
				int i2 = par0World.getBlockId(i1, j1, k1);
				
				if (!par0World.isAirBlock(i1, j1, k1))
				{
					if (!canPushBlock(i2, par0World, i1, j1, k1, true))
					{
						return false;
					}
					
					if (Block.blocksList[i2].getMobilityFlag() != 1)
					{
						if (l1 == 12)
						{
							return false;
						}
						
						i1 += Facing.offsetsXForSide[par4];
						j1 += Facing.offsetsYForSide[par4];
						k1 += Facing.offsetsZForSide[par4];
						++l1;
						continue;
					}
				}
			}
			
			return true;
		}
	}
	
	/**
	 * attempts to extend the piston. returns false if impossible.
	 */
	private boolean tryExtend(World world, int x, int y, int z, int direction)
	{
		int x0 = x + Facing.offsetsXForSide[direction];
		int y0 = y + Facing.offsetsYForSide[direction];
		int z0 = z + Facing.offsetsZForSide[direction];
		int l1 = 0;
		
		while (true)
		{
			int i2;
			
			if (l1 < 13)
			{
				if (y0 <= 0 || y0 >= world.getHeight() - 1)
				{
					return false;
				}
				
				i2 = world.getBlockId(x0, y0, z0);
				
				if (!world.isAirBlock(x0, y0, z0))
				{
					if (!canPushBlock(i2, world, x0, y0, z0, true))
					{
						return false;
					}
					
					if (Block.blocksList[i2].getMobilityFlag() != 1)
					{
						if (l1 == 12)
						{
							return false;
						}
						
						x0 += Facing.offsetsXForSide[direction];
						y0 += Facing.offsetsYForSide[direction];
						z0 += Facing.offsetsZForSide[direction];
						++l1;
						continue;
					}
					
					// With our change to how snowballs are dropped this needs
					// to disallow to mimic vanilla behavior.
					float chance = (Block.blocksList[i2] instanceof BlockSnow ? -1.0f : 1.0f);
					Block.blocksList[i2].dropBlockAsItemWithChance(world, x0, y0, z0, world.getBlockMetadata(x0, y0, z0), chance, 0);
					world.setBlockToAir(x0, y0, z0);
				}
			}
			
			l1 = x0;
			i2 = y0;
			int j2 = z0;
			int k2 = 0;
			int[] aint;
			int x1;
			int y1;
			int z1;
			
			for (aint = new int[13]; x0 != x || y0 != y || z0 != z; z0 = z1)
			{
				x1 = x0 - Facing.offsetsXForSide[direction];
				y1 = y0 - Facing.offsetsYForSide[direction];
				z1 = z0 - Facing.offsetsZForSide[direction];
				int blockID = world.getBlockId(x1, y1, z1);
				int blockMetadata = world.getBlockMetadata(x1, y1, z1);
				TileEntity blockTileEntity = world.getBlockTileEntity(x1, y1, z1);
				
				if (blockID == this.blockID && x1 == x && y1 == y && z1 == z)
				{
					int metadata = direction | (this.isSticky ? 8 : 0);
					world.setBlock(x0, y0, z0, Block.pistonMoving.blockID, metadata, 4);
					world.setBlockTileEntity(x0, y0, z0, getTileEntity(Block.pistonExtension.blockID, metadata, blockTileEntity, direction, true, false));
				}
				else
				{
					world.setBlock(x0, y0, z0, Block.pistonMoving.blockID, blockMetadata, 4);
					world.setBlockTileEntity(x0, y0, z0, getTileEntity(blockID, blockMetadata, blockTileEntity, direction, true, false));
				}
				
				aint[k2++] = blockID;
				x0 = x1;
				y0 = y1;
			}
			
			x0 = l1;
			y0 = i2;
			z0 = j2;
			
			for (k2 = 0; x0 != x || y0 != y || z0 != z; z0 = z1)
			{
				x1 = x0 - Facing.offsetsXForSide[direction];
				y1 = y0 - Facing.offsetsYForSide[direction];
				z1 = z0 - Facing.offsetsZForSide[direction];
				world.notifyBlocksOfNeighborChange(x1, y1, z1, aint[k2++]);
				x0 = x1;
				y0 = y1;
			}
			
			return true;
		}
	}
	
	public static TileEntityPiston getTileEntity(int blockID, int blockMetadata, TileEntity tileEntity, int direction, boolean extending, boolean renderHead)
	{
		return new TileEntityPiston2(blockID, blockMetadata, tileEntity, direction, extending, renderHead);
	}
}
