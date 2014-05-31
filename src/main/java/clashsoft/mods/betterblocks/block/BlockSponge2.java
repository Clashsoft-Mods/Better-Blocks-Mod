package clashsoft.mods.betterblocks.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSponge;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class BlockSponge2 extends BlockSponge
{
	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		super.onBlockAdded(world, x, y, z);
		this.replaceWater(world, x, y, z);
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock)
	{
		super.onNeighborBlockChange(world, x, y, z, neighborBlock);
		this.replaceWater(world, x, y, z);
	}
	
	public void replaceWater(World world, int x, int y, int z)
	{
		if (!world.isRemote)
		{
			for (int x1 = x - 2; x1 <= x + 2; x1++)
			{
				for (int y1 = y - 2; y1 <= y + 2; y1++)
				{
					for (int z1 = z - 2; z1 <= z + 2; z1++)
					{
						Block block = world.getBlock(x1, y1, z1);
						if (block != null)
						{
							Material material = block.getMaterial();
							if (material.isLiquid())
							{
								world.setBlockToAir(x1, y1, z1);
							}
						}
					}
				}
			}
		}
	}
}
