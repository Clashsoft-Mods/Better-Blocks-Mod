package clashsoft.mods.betterblocks;

import clashsoft.mods.betterblocks.block.BlockMobSpawner2;
import clashsoft.mods.betterblocks.block.BlockPistonBase2;
import clashsoft.mods.betterblocks.block.BlockSponge2;
import clashsoft.mods.betterblocks.common.BBCommonProxy;
import clashsoft.mods.betterblocks.item.ItemBlockMobSpawner2;
import clashsoft.mods.betterblocks.tileentity.TileEntityPiston2;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = "BetterBlocksMod", name = "Better Blocks Mod", version = "1.1")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class BetterBlocksMod
{
	@Instance("BetterBlocksMod")
	public static BetterBlocksMod	instance;
	
	@SidedProxy(modId = "BetterBlocksMod", clientSide = "clashsoft.mods.betterblocks.client.BBClientProxy", serverSide = "clashsoft.mods.betterblocks.common.BBCommonProxy")
	public static BBCommonProxy		proxy;
	
	public static boolean			spawnerCrafting	= true;
	
	public static BlockMobSpawner2	spawner2;
	public static BlockPistonBase2	pistonBase2;
	public static BlockPistonBase2	pistonStickyBase2;
	public static BlockSponge2		sponge2;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		spawnerCrafting = config.get("Spawners", "Crafting", true).getBoolean(true);
		
		config.save();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		NetworkRegistry.instance().registerGuiHandler(instance, proxy);
		
		Block.blocksList[Block.mobSpawner.blockID] = null;
		spawner2 = (BlockMobSpawner2) new BlockMobSpawner2(Block.mobSpawner.blockID).setUnlocalizedName("mobSpawner").setTextureName("mob_spawner").setHardness(5.0F).setStepSound(Block.soundMetalFootstep).setCreativeTab(CreativeTabs.tabBlock);
		
		Block.blocksList[Block.pistonBase.blockID] = null;
		pistonBase2 = (BlockPistonBase2) new BlockPistonBase2(Block.pistonBase.blockID, false).setUnlocalizedName("pistonBase");
		
		Block.blocksList[Block.pistonStickyBase.blockID] = null;
		pistonStickyBase2 = (BlockPistonBase2) new BlockPistonBase2(Block.pistonStickyBase.blockID, true).setUnlocalizedName("pistonStickyBase");
		
		Block.blocksList[Block.sponge.blockID] = null;
		sponge2 = (BlockSponge2) new BlockSponge2(Block.sponge.blockID).setUnlocalizedName("sponge").setTextureName("sponge").setHardness(0.6F).setStepSound(Block.soundGrassFootstep);
		
		GameRegistry.registerTileEntity(TileEntityPiston2.class, "Piston2");
		
		GameRegistry.registerBlock(spawner2, ItemBlockMobSpawner2.class, "MobSpawner");
		MinecraftForge.setBlockHarvestLevel(spawner2, "pickaxe", 2);
		
		if (spawnerCrafting)
			addSpawnerRecipes();
		
		GameRegistry.addRecipe(new ItemStack(Block.sponge), "wsw", "sws", "wsw", 'w', new ItemStack(Block.cloth, 1, 4), 's', Item.silk);
	}
	
	public void addSpawnerRecipes()
	{
		addSpawnerRecipe("Creeper", Item.gunpowder);
		addSpawnerRecipe("Skeleton", Item.arrow);
		addSpawnerRecipe("Spider", Item.silk);
		addSpawnerRecipe("Giant", Item.bucketWater);
		addSpawnerRecipe("Zombie", Item.rottenFlesh);
		addSpawnerRecipe("Slime", Item.slimeBall);
		addSpawnerRecipe("Ghast", Item.ghastTear);
		addSpawnerRecipe("PigZombie", Item.swordGold);
		addSpawnerRecipe("Enderman", Item.enderPearl);
		addSpawnerRecipe("CaveSpider", Item.spiderEye);
		addSpawnerRecipe("Silverfish", Block.stoneBrick);
		addSpawnerRecipe("Blaze", Item.blazeRod);
		addSpawnerRecipe("LavaSlime", Item.magmaCream);
		addSpawnerRecipe("Bat", Block.stone);
		addSpawnerRecipe("Witch", Item.glassBottle);
		addSpawnerRecipe("Pig", Item.porkRaw);
		addSpawnerRecipe("Sheep", new ItemStack(Block.cloth, 1, 0));
		addSpawnerRecipe("Cow", Item.beefRaw);
		addSpawnerRecipe("Chicken", Item.chickenRaw);
		addSpawnerRecipe("Squid", new ItemStack(Item.dyePowder, 1, 0));
		addSpawnerRecipe("Wolf", Item.bone);
		addSpawnerRecipe("MushroomCow", Block.mushroomRed);
		addSpawnerRecipe("SnowMan", Block.snow);
		addSpawnerRecipe("Ozelot", Item.fishRaw);
		addSpawnerRecipe("VillagerGolem", Block.blockIron);
		addSpawnerRecipe("EntityHorse", Block.hay);
		addSpawnerRecipe("Villager", Item.emerald);
	}
	
	public static void addSpawnerRecipe(String entity, Item entityItem)
	{
		addSpawnerRecipe(entity, new ItemStack(entityItem, 1, OreDictionary.WILDCARD_VALUE));
	}
	
	public static void addSpawnerRecipe(String entity, Block entityBlock)
	{
		addSpawnerRecipe(entity, new ItemStack(entityBlock, 1, OreDictionary.WILDCARD_VALUE));
	}
	
	public static void addSpawnerRecipe(String entity, ItemStack entityItem)
	{
		GameRegistry.addRecipe(spawner2.getSpawnerStack(entity), new Object[] { "dId", "I#I", "dId", 'd', Block.obsidian, 'I', Block.fenceIron, '#', entityItem });
	}
}
