package clashsoft.mods.betterblocks.block;

import clashsoft.mods.betterblocks.tileentity.TileEntityPiston2;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockSnow;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.Facing;
import net.minecraft.world.World;

public class BlockPistonBase2 extends BlockPistonBase
{
	public boolean	isSticky	= false;
	
	public BlockPistonBase2(boolean isSticky)
	{
		super(isSticky);
		this.isSticky = isSticky;
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase licing, ItemStack stack)
	{
		int l = determineOrientation(world, x, y, z, licing);
		world.setBlockMetadataWithNotify(x, y, z, l, 2);
		
		if (!world.isRemote)
		{
			this.updatePistonState(world, x, y, z);
		}
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock)
	{
		if (!world.isRemote)
		{
			this.updatePistonState(world, x, y, z);
		}
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		if (!world.isRemote && world.getTileEntity(x, y, z) == null)
		{
			this.updatePistonState(world, x, y, z);
		}
	}
	
	public void updatePistonState(World world, int x, int y, int z)
	{
		int metadata = world.getBlockMetadata(x, y, z);
		int orientation = 0;
		
		if (orientation != 7)
		{
			boolean flag = this.isIndirectlyPowered(world, x, y, z, orientation);
			
			if (flag && !isExtended(metadata))
			{
				if (this.canExtend(world, x, y, z, orientation))
				{
					world.addBlockEvent(x, y, z, this, 0, orientation);
				}
			}
			else if (!flag && isExtended(metadata))
			{
				world.setBlockMetadataWithNotify(x, y, z, orientation, 2);
				world.addBlockEvent(x, y, z, this, 1, orientation);
			}
		}
	}
	
	public boolean isIndirectlyPowered(World world, int x, int y, int z, int side)
	{
		return side != 0 && world.getIndirectPowerOutput(x, y - 1, z, 0) ? true : (side != 1 && world.getIndirectPowerOutput(x, y + 1, z, 1) ? true : (side != 2 && world.getIndirectPowerOutput(x, y, z - 1, 2) ? true : (side != 3 && world.getIndirectPowerOutput(x, y, z + 1, 3) ? true : (side != 5 && world.getIndirectPowerOutput(x + 1, y, z, 5) ? true : (side != 4 && world.getIndirectPowerOutput(x - 1, y, z, 4) ? true : (world.getIndirectPowerOutput(x, y, z, 0) ? true : (world.getIndirectPowerOutput(x, y + 2, z, 1) ? true : (world.getIndirectPowerOutput(x, y + 1, z - 1, 2) ? true : (world.getIndirectPowerOutput(x, y + 1, z + 1, 3) ? true : (world.getIndirectPowerOutput(x - 1, y + 1, z, 4) ? true : world.getIndirectPowerOutput(x + 1, y + 1, z, 5)))))))))));
	}
	
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
			TileEntity tileentity = world.getTileEntity(x + Facing.offsetsXForSide[direction], y + Facing.offsetsYForSide[direction], z + Facing.offsetsZForSide[direction]);
			
			if (tileentity instanceof TileEntityPiston)
			{
				((TileEntityPiston) tileentity).clearPistonTileEntity();
			}
			
			world.setBlock(x, y, z, Blocks.piston_extension, direction, 3);
			world.setTileEntity(x, y, z, getTileEntity(this, direction, tileentity, direction, false, true));
			
			if (this.isSticky)
			{
				int x1 = x + Facing.offsetsXForSide[direction] * 2;
				int y1 = y + Facing.offsetsYForSide[direction] * 2;
				int z1 = z + Facing.offsetsZForSide[direction] * 2;
				
				Block block = world.getBlock(x1, y1, z1);
				int blockMetadata = world.getBlockMetadata(x1, y1, z1);
				TileEntity tileEntity = world.getTileEntity(x1, y1, z1);
				boolean flag1 = false;
				
				if (block == Blocks.piston_extension)
				{
					TileEntity tileentity1 = world.getTileEntity(x1, y1, z1);
					
					if (tileentity1 instanceof TileEntityPiston2)
					{
						TileEntityPiston2 tileentitypiston = (TileEntityPiston2) tileentity1;
						
						if (tileentitypiston.getPistonOrientation() == direction && tileentitypiston.isExtending())
						{
							tileentitypiston.clearPistonTileEntity();
							block = tileentitypiston.getStoredBlockID();
							blockMetadata = tileentitypiston.getBlockMetadata();
							tileEntity = tileentitypiston.storedTileEntity;
							flag1 = true;
						}
					}
				}
				
				if (!flag1 && this.canPushBlock(block, world, x1, y1, z1, false) && (block.getMobilityFlag() == 0 || block == Blocks.piston || block == Blocks.sticky_piston))
				{
					x += Facing.offsetsXForSide[direction];
					y += Facing.offsetsYForSide[direction];
					z += Facing.offsetsZForSide[direction];
					
					world.setBlock(x, y, z, Blocks.piston_extension, blockMetadata, 3);
					world.setTileEntity(x, y, z, getTileEntity(block, blockMetadata, tileEntity, direction, false, false));
					world.removeTileEntity(x1, y1, z1);
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
	
	public boolean canPushBlock(Block block, World world, int x, int y, int z, boolean pull)
	{
		if (block == Blocks.obsidian)
		{
			return false;
		}
		else
		{
			if (block != Blocks.piston && block != Blocks.sticky_piston)
			{
				if (block.getBlockHardness(world, x, y, z) == -1.0F)
				{
					return false;
				}
				
				if (block.getMobilityFlag() == 2)
				{
					return false;
				}
				
				if (block.getMobilityFlag() == 1)
				{
					if (!pull)
					{
						return false;
					}
					
					return true;
				}
			}
			else if (isExtended(world.getBlockMetadata(x, y, z)))
			{
				return false;
			}
			
			return true;
		}
	}
	
	public boolean canExtend(World world, int x, int y, int z, int direction)
	{
		int i1 = x + Facing.offsetsXForSide[direction];
		int j1 = y + Facing.offsetsYForSide[direction];
		int k1 = z + Facing.offsetsZForSide[direction];
		int l1 = 0;
		
		while (true)
		{
			if (l1 < 13)
			{
				if (j1 <= 0 || j1 >= world.getHeight() - 1)
				{
					return false;
				}
				
				Block block = world.getBlock(i1, j1, k1);
				
				if (!world.isAirBlock(i1, j1, k1))
				{
					if (!this.canPushBlock(block, world, i1, j1, k1, true))
					{
						return false;
					}
					
					if (block.getMobilityFlag() != 1)
					{
						if (l1 == 12)
						{
							return false;
						}
						
						i1 += Facing.offsetsXForSide[direction];
						j1 += Facing.offsetsYForSide[direction];
						k1 += Facing.offsetsZForSide[direction];
						++l1;
						continue;
					}
				}
			}
			
			return true;
		}
	}
	
	private boolean tryExtend(World world, int x, int y, int z, int direction)
	{
		int x0 = x + Facing.offsetsXForSide[direction];
		int y0 = y + Facing.offsetsYForSide[direction];
		int z0 = z + Facing.offsetsZForSide[direction];
		int l1 = 0;
		
		while (true)
		{
			if (l1 < 13)
			{
				if (y0 <= 0 || y0 >= world.getHeight() - 1)
				{
					return false;
				}
				
				Block block = world.getBlock(x0, y0, z0);
				
				if (!world.isAirBlock(x0, y0, z0))
				{
					if (!this.canPushBlock(block, world, x0, y0, z0, true))
					{
						return false;
					}
					
					if (block.getMobilityFlag() != 1)
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
					
					float chance = (block instanceof BlockSnow ? -1.0f : 1.0f);
					block.dropBlockAsItemWithChance(world, x0, y0, z0, world.getBlockMetadata(x0, y0, z0), chance, 0);
					world.setBlockToAir(x0, y0, z0);
				}
			}
			
			int i1 = x0;
			int j1 = y0;
			int k1 = z0;
			int k2 = 0;
			Block[] aint;
			int x1;
			int y1;
			int z1;
			
			for (aint = new Block[13]; x0 != x || y0 != y || z0 != z; z0 = z1)
			{
				x1 = x0 - Facing.offsetsXForSide[direction];
				y1 = y0 - Facing.offsetsYForSide[direction];
				z1 = z0 - Facing.offsetsZForSide[direction];
				Block block = world.getBlock(x1, y1, z1);
				int blockMetadata = world.getBlockMetadata(x1, y1, z1);
				TileEntity blockTileEntity = world.getTileEntity(x1, y1, z1);
				
				if (block == this && x1 == x && y1 == y && z1 == z)
				{
					int metadata = direction | (this.isSticky ? 8 : 0);
					world.setBlock(x0, y0, z0, Blocks.piston_extension, metadata, 4);
					world.setTileEntity(x0, y0, z0, getTileEntity(Blocks.piston_head, metadata, blockTileEntity, direction, true, false));
				}
				else
				{
					world.setBlock(x0, y0, z0, Blocks.piston_extension, blockMetadata, 4);
					world.setTileEntity(x0, y0, z0, getTileEntity(block, blockMetadata, blockTileEntity, direction, true, false));
				}
				
				aint[k2++] = block;
				x0 = x1;
				y0 = y1;
			}
			
			x0 = i1;
			y0 = j1;
			z0 = k1;
			
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
	
	public static TileEntityPiston getTileEntity(Block block, int blockMetadata, TileEntity tileEntity, int direction, boolean extending, boolean renderHead)
	{
		return new TileEntityPiston2(block, blockMetadata, tileEntity, direction, extending, renderHead);
	}
}
