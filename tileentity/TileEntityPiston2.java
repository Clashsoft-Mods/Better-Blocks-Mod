package clashsoft.mods.betterblocks.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;

public class TileEntityPiston2 extends TileEntityPiston
{
	public TileEntity	storedTileEntity;
	
	public int			storedBlockMetadata	= 0;
	
	public float		lastProgress		= 0F;
	public float		progress			= 0F;
	
	public List			pushedObjects		= new ArrayList();
	
	public TileEntityPiston2()
	{
	}
	
	public TileEntityPiston2(int blockID, int blockMetadata, TileEntity tileEntity, int orientation, boolean extending, boolean renderHead)
	{
		super(blockID, blockMetadata, orientation, extending, renderHead);
		this.storedBlockMetadata = blockMetadata;
		this.storedTileEntity = tileEntity;
	}
	
	/**
	 * Get interpolated progress value (between lastProgress and progress) given
	 * the fractional time between ticks as an argument.
	 */
	@Override
	public float getProgress(float time)
	{
		if (time > 1.0F)
		{
			time = 1.0F;
		}
		
		return this.lastProgress + (this.progress - this.lastProgress) * time;
	}
	
	private void updatePushedObjects(float par1, float par2)
	{
		if (this.isExtending())
		{
			par1 = 1.0F - par1;
		}
		else
		{
			--par1;
		}
		
		AxisAlignedBB axisalignedbb = Block.pistonMoving.getAxisAlignedBB(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.getStoredBlockID(), par1, this.getPistonOrientation());
		
		if (axisalignedbb != null)
		{
			List list = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity) null, axisalignedbb);
			
			if (!list.isEmpty())
			{
				this.pushedObjects.addAll(list);
				Iterator iterator = this.pushedObjects.iterator();
				
				while (iterator.hasNext())
				{
					Entity entity = (Entity) iterator.next();
					entity.moveEntity(par2 * Facing.offsetsXForSide[this.getPistonOrientation()], par2 * Facing.offsetsYForSide[this.getPistonOrientation()], par2 * Facing.offsetsZForSide[this.getPistonOrientation()]);
				}
				
				this.pushedObjects.clear();
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public float getOffsetX(float par1)
	{
		return this.isExtending() ? (this.getProgress(par1) - 1.0F) * Facing.offsetsXForSide[this.getPistonOrientation()] : (1.0F - this.getProgress(par1)) * Facing.offsetsXForSide[this.getPistonOrientation()];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public float getOffsetY(float par1)
	{
		return this.isExtending() ? (this.getProgress(par1) - 1.0F) * Facing.offsetsYForSide[this.getPistonOrientation()] : (1.0F - this.getProgress(par1)) * Facing.offsetsYForSide[this.getPistonOrientation()];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public float getOffsetZ(float par1)
	{
		return this.isExtending() ? (this.getProgress(par1) - 1.0F) * Facing.offsetsZForSide[this.getPistonOrientation()] : (1.0F - this.getProgress(par1)) * Facing.offsetsZForSide[this.getPistonOrientation()];
	}
	
	/**
	 * removes a pistons tile entity (and if the piston is moving, stops it)
	 */
	@Override
	public void clearPistonTileEntity()
	{
		if (this.lastProgress < 1.0F && this.worldObj != null)
		{
			this.lastProgress = this.progress = 1.0F;
			this.setBlock();
		}
	}
	
	/**
	 * Allows the entity to update its state. Overridden in most subclasses,
	 * e.g. the mob spawner uses this to count ticks and creates a new spawn
	 * inside its implementation.
	 */
	@Override
	public void updateEntity()
	{
		this.lastProgress = this.progress;
		
		if (this.lastProgress >= 1.0F)
		{
			this.updatePushedObjects(1.0F, 0.25F);
			this.setBlock();
		}
		else
		{
			this.progress += 0.5F;
			
			if (this.progress >= 1.0F)
			{
				this.progress = 1.0F;
			}
			
			if (this.isExtending())
			{
				this.updatePushedObjects(this.progress, this.progress - this.lastProgress + 0.0625F);
			}
		}
	}
	
	public void setBlock()
	{
		if (!this.worldObj.isRemote)
		{
			this.worldObj.removeBlockTileEntity(this.xCoord, this.yCoord, this.zCoord);
			this.invalidate();
			
			if (this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord) == Block.pistonMoving.blockID)
			{
				this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, this.getStoredBlockID(), this.storedBlockMetadata, 3);
				
				if (this.storedTileEntity != null)
				{
					this.storedTileEntity.blockType = Block.blocksList[this.getStoredBlockID()];
					this.storedTileEntity.blockMetadata = this.storedBlockMetadata;
					this.storedTileEntity.validate();
					
					this.worldObj.setBlockTileEntity(this.xCoord, this.yCoord, this.zCoord, this.storedTileEntity);
				}
				
				this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, this.storedBlockMetadata, 3);
				
				this.worldObj.notifyBlockOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getStoredBlockID());
			}
		}
	}
	
	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		
		if (nbt.hasKey("TileEntity"))
		{
			NBTTagCompound tileEntity = nbt.getCompoundTag("TileEntity");
			if (tileEntity.hasNoTags())
				this.storedTileEntity = this;
			else
				this.storedTileEntity = TileEntity.createAndLoadEntity(tileEntity);
		}
		else
			this.storedTileEntity = null;
	}
	
	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		
		if (this.storedTileEntity != null)
		{
			NBTTagCompound tileEntity = new NBTTagCompound();
			if (this.storedTileEntity != this)
				this.storedTileEntity.writeToNBT(tileEntity);
			nbt.setCompoundTag("TileEntity", tileEntity);
		}
	}
}
