package clashsoft.mods.betterblocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSponge;
import net.minecraft.world.World;

public class BlockSponge2 extends BlockSponge
{
	public BlockSponge2(int blockID)
	{
		super(blockID);
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		super.onBlockAdded(world, x, y, z);
		this.replaceWater(world, x, y, z);
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int side)
	{
		super.onNeighborBlockChange(world, x, y, z, side);
		this.replaceWater(world, x, y, z);
	}
	
	public void replaceWater(World world, int x, int y, int z)
	{
		for (int x1 = x - 2; x1 <= x + 2; x1++)
		{
			for (int y1 = y - 2; y1 <= y + 2; y1++)
			{
				for (int z1 = z - 2; z1 <= z + 2; z1++)
				{
					int blockID = world.getBlockId(x, y, z);
					if (Block.blocksList[blockID] != null && Block.blocksList[blockID].blockMaterial.isLiquid())
						world.setBlockToAir(x1, y1, z1);
				}
			}
		}
	}
}
