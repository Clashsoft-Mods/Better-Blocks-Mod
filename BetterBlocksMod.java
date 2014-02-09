package clashsoft.mods.betterblocks;

import clashsoft.cslib.minecraft.block.CSBlocks;
import clashsoft.cslib.minecraft.update.CSUpdate;
import clashsoft.cslib.minecraft.util.CSConfig;
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
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = BetterBlocksMod.MODID, name = BetterBlocksMod.NAME, version = BetterBlocksMod.VERSION)
public class BetterBlocksMod
{
	public static final String		MODID			= "betterblocks";
	public static final String		NAME			= "Better Blocks Mod";
	public static final int			REVISION		= 0;
	public static final String		VERSION			= CSUpdate.CURRENT_VERSION + "-" + REVISION;
	
	@Instance(MODID)
	public static BetterBlocksMod	instance;
	
	@SidedProxy(clientSide = "clashsoft.mods.betterblocks.client.BBClientProxy", serverSide = "clashsoft.mods.betterblocks.common.BBCommonProxy")
	public static BBCommonProxy		proxy;
	
	public static boolean			spawnerCrafting	= true;
	
	public static BlockMobSpawner2	spawner2;
	public static BlockPistonBase2	piston2;
	public static BlockPistonBase2	stickyPiston2;
	public static BlockSponge2		sponge2;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		CSConfig.loadConfig(event.getSuggestedConfigurationFile(), NAME);
		
		spawnerCrafting = CSConfig.getBool("Spawners", "Crafting", true);
		
		CSConfig.saveConfig();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
		
		spawner2 = (BlockMobSpawner2) new BlockMobSpawner2().setBlockName("mob_spawner").setBlockTextureName("mob_spawner").setHardness(5.0F).setStepSound(Block.soundTypeMetal).setCreativeTab(CreativeTabs.tabBlock);
		spawner2.setHarvestLevel("pickaxe", 2);
		CSBlocks.overrideBlock(spawner2, ItemBlockMobSpawner2.class, "mob_spawner");
		
		piston2 = (BlockPistonBase2) new BlockPistonBase2(false).setBlockName("pistonBase");
		CSBlocks.overrideBlock(piston2, "piston");
		
		stickyPiston2 = (BlockPistonBase2) new BlockPistonBase2(true).setBlockName("pistonStickyBase");
		CSBlocks.overrideBlock(stickyPiston2, "sticky_piston");
		
		sponge2 = (BlockSponge2) new BlockSponge2().setBlockName("sponge").setBlockTextureName("sponge").setHardness(0.6F).setStepSound(Block.soundTypeGrass);
		
		GameRegistry.registerTileEntity(TileEntityPiston2.class, "Piston2");
		
		if (spawnerCrafting)
			this.addSpawnerRecipes();
		
		GameRegistry.addRecipe(new ItemStack(sponge2), "wsw", "sws", "wsw", 'w', new ItemStack(Blocks.wool, 1, 4), 's', Items.string);
	}
	
	public void addSpawnerRecipes()
	{
		addSpawnerRecipe("Creeper", Items.gunpowder);
		addSpawnerRecipe("Skeleton", Items.arrow);
		addSpawnerRecipe("Spider", Items.string);
		addSpawnerRecipe("Giant", Items.water_bucket);
		addSpawnerRecipe("Zombie", Items.rotten_flesh);
		addSpawnerRecipe("Slime", Items.slime_ball);
		addSpawnerRecipe("Ghast", Items.ghast_tear);
		addSpawnerRecipe("PigZombie", Items.golden_sword);
		addSpawnerRecipe("Enderman", Items.ender_pearl);
		addSpawnerRecipe("CaveSpider", Items.spider_eye);
		addSpawnerRecipe("Silverfish", Blocks.stonebrick);
		addSpawnerRecipe("Blaze", Items.blaze_rod);
		addSpawnerRecipe("LavaSlime", Items.magma_cream);
		addSpawnerRecipe("Bat", Blocks.stone);
		addSpawnerRecipe("Witch", Items.glass_bottle);
		addSpawnerRecipe("Pig", Items.porkchop);
		addSpawnerRecipe("Sheep", new ItemStack(Blocks.wool, 1, 0));
		addSpawnerRecipe("Cow", Items.beef);
		addSpawnerRecipe("Chicken", Items.chicken);
		addSpawnerRecipe("Squid", new ItemStack(Items.dye, 1, 0));
		addSpawnerRecipe("Wolf", Items.bone);
		addSpawnerRecipe("MushroomCow", Blocks.red_mushroom);
		addSpawnerRecipe("SnowMan", Blocks.snow);
		addSpawnerRecipe("Ozelot", Items.fish);
		addSpawnerRecipe("VillagerGolem", Blocks.iron_block);
		addSpawnerRecipe("EntityHorse", Blocks.hay_block);
		addSpawnerRecipe("Villager", Items.emerald);
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
		GameRegistry.addRecipe(spawner2.getSpawnerStack(entity), new Object[] {
				"dId",
				"I#I",
				"dId",
				'd',
				Blocks.obsidian,
				'I',
				Blocks.iron_bars,
				'#',
				entityItem });
	}
}
