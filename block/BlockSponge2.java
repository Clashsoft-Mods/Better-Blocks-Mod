package clashsoft.mods.betterblocks.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSponge;
import net.minecraft.block.material.Material;
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
		if (!world.isRemote)
		{
			for (int x1 = x - 2; x1 <= x + 2; x1++)
			{
				for (int y1 = y - 2; y1 <= y + 2; y1++)
				{
					for (int z1 = z - 2; z1 <= z + 2; z1++)
					{
						int blockID = world.getBlockId(x1, y1, z1);
						Block block = Block.blocksList[blockID];
						if (block != null && (block.blockMaterial.isLiquid() || block.blockMaterial == Material.water || block.blockMaterial == Material.lava))
							world.setBlock(x1, y1, z1, 0, 0, 2);
					}
				}
			}
		}
	}
}
