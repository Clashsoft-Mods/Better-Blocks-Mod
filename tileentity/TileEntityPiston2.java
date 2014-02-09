package clashsoft.mods.betterblocks.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
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
	
	public TileEntityPiston2(Block block, int blockMetadata, TileEntity tileEntity, int orientation, boolean extending, boolean renderHead)
	{
		super(block, blockMetadata, orientation, extending, renderHead);
		this.storedBlockMetadata = blockMetadata;
		this.storedTileEntity = tileEntity;
	}
	
	private void updatePushedObjects(float progress, float delta)
	{
		if (this.isExtending())
		{
			progress = 1.0F - progress;
		}
		else
		{
			--progress;
		}
		
		AxisAlignedBB axisalignedbb = Blocks.piston_extension.func_149964_a(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.getStoredBlockID(), progress, this.getPistonOrientation());
		
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
					entity.moveEntity(delta * Facing.offsetsXForSide[this.getPistonOrientation()], delta * Facing.offsetsYForSide[this.getPistonOrientation()], delta * Facing.offsetsZForSide[this.getPistonOrientation()]);
				}
				
				this.pushedObjects.clear();
			}
		}
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
			this.worldObj.removeTileEntity(this.xCoord, this.yCoord, this.zCoord);
			this.invalidate();
			
			if (this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord) == Blocks.piston_extension)
			{
				this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, this.getStoredBlockID(), this.storedBlockMetadata, 3);
				
				if (this.storedTileEntity != null)
				{
					this.storedTileEntity.blockType = this.getStoredBlockID();
					this.storedTileEntity.blockMetadata = this.storedBlockMetadata;
					this.storedTileEntity.validate();
					
					this.worldObj.setTileEntity(this.xCoord, this.yCoord, this.zCoord, this.storedTileEntity);
				}
				
				this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, this.storedBlockMetadata, 3);
				
				this.worldObj.notifyBlockOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getStoredBlockID());
			}
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		
		if (nbt.hasKey("TileEntity"))
		{
			NBTTagCompound tileEntity = nbt.getCompoundTag("TileEntity");
			if (tileEntity.hasNoTags()) {
				this.storedTileEntity = this;}
			else {
				this.storedTileEntity = TileEntity.createAndLoadEntity(tileEntity);}
		}
		else {
			this.storedTileEntity = null;}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		
		if (this.storedTileEntity != null)
		{
			NBTTagCompound tileEntity = new NBTTagCompound();
			if (this.storedTileEntity != this) {
				this.storedTileEntity.writeToNBT(tileEntity);}
			nbt.setTag("TileEntity", tileEntity);
		}
	}
}
