package cr0s.warpdrive.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.hull.BlockHullGlass;
import cr0s.warpdrive.block.hull.BlockHullPlain;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

public class Dictionary {
	private static boolean adjustResistance = false;
	
	// Tagged blocks and entities (loaded from configuration file at PreInit, parsed at PostInit)
	private static HashMap<String, String> taggedBlocks = null;
	private static HashMap<String, String> taggedEntities = null;
	private static HashMap<String, String> taggedItems = null;
	
	// Blocks dictionary
	public static HashSet<Block> BLOCKS_ORES = null;
	public static HashSet<Block> BLOCKS_SOILS = null;
	public static HashSet<Block> BLOCKS_LOGS = null;
	public static HashSet<Block> BLOCKS_LEAVES = null;
	public static HashSet<Block> BLOCKS_ANCHOR = null;
	public static HashSet<Block> BLOCKS_NOMASS = null;
	public static HashSet<Block> BLOCKS_LEFTBEHIND = null;
	public static HashSet<Block> BLOCKS_EXPANDABLE = null;
	public static HashSet<Block> BLOCKS_MINING = null;
	public static HashSet<Block> BLOCKS_NOMINING = null;
	public static HashMap<Block, Integer> BLOCKS_PLACE = null;
	
	// Entities dictionary
	public static HashSet<String> ENTITIES_ANCHOR = null;
	public static HashSet<String> ENTITIES_NOMASS = null;
	public static HashSet<String> ENTITIES_LEFTBEHIND = null;
	public static HashSet<String> ENTITIES_NONLIVINGTARGET = null;
	
	// Items dictionary
	public static HashSet<Item> ITEMS_FLYINSPACE = null;
	public static HashSet<Item> ITEMS_NOFALLDAMAGE = null;
	public static HashSet<Item> ITEMS_BREATHINGIC2 = null;
	
	public static void loadConfig(Configuration config) {
		
		// Block dictionary
		{
			config.addCustomCategoryComment("block_tags",
					  "Use this section to enable special behavior on blocks using tags.\n"
					+ "Most blocks are already supported automatically. Only modify this section when something doesn't work!\n" + "\n"
					+ "Tags shall be separated by at least one space, comma or tabulation.\n"
					+ "Invalid tags will be ignored silently. Tags and block names are case sensitive.\n"
					+ "In case of conflicts, the latest tag overwrite the previous ones.\n"
					+ "- Soil: this block is a soil for plants (default: dirt, farmland, grass, sand & soul sand).\n"
					+ "- Log: this block is harvestable as a wood log (default: all 'log*', '*log' & '*logs' blocks from the ore dictionnary).\n"
					+ "- Leaf: this block is harvestable as a leaf (default: all 'leave*', '*leave' & '*leaves' blocks from the ore dictionnary).\n"
					+ "- Anchor: ship can't move with this block aboard (default: bedrock and assimilated).\n"
					+ "- NoMass: this block doesn't count when calculating ship volume/mass (default: leaves, all 'air' blocks).\n"
					+ "- LeftBehind: this block won't move with your ship (default: RailCraft heat, WarpDrive gases).\n"
					+ "- Expandable: this block will be squished/ignored in case of collision.\n"
					+ "- Mining: this block is mineable (default: all 'ore' blocks from the ore dictionnary).\n"
					+ "- NoMining: this block is non-mineable (default: forcefields).\n"
					+ "- PlaceEarliest: this block will be removed last and placed first (default: ship hull and projectors).\n"
					+ "- PlaceEarlier: this block will be placed fairly soon (default: forcefield blocks).\n"
					+ "- PlaceNormal: this block will be removed and placed with non-tile entities.\n"
					+ "- PlaceLater: this block will be placed fairly late (default: IC2 Reactor core).\n"
					+ "- PlaceLatest: this block will be removed first and placed last (default: IC2 Reactor chamber).");
			
			ConfigCategory categoryBlockTags = config.getCategory("block_tags");
			String[] taggedBlocksName = categoryBlockTags.getValues().keySet().toArray(new String[0]);
			if (taggedBlocksName.length == 0) {
				// farming
				config.get("block_tags", "minecraft:dirt"                   , "Soil").getString();
				config.get("block_tags", "minecraft:farmland"               , "Soil").getString();
				config.get("block_tags", "minecraft:grass"                  , "Soil").getString();
				config.get("block_tags", "minecraft:mycelium"               , "Soil").getString();
				config.get("block_tags", "minecraft:sand"                   , "Soil").getString();
				config.get("block_tags", "minecraft:soul_sand"              , "Soil").getString();
				config.get("block_tags", "IC2:blockRubWood"                 , "Log").getString();
				
				// anchors
				config.get("block_tags", "minecraft:bedrock"                , "Anchor NoMining").getString();
				config.get("block_tags", "IC2:blockPersonal"                , "Anchor NoMining").getString();
				config.get("block_tags", "Artifacts:invisible_bedrock"      , "Anchor NoMining").getString();
				config.get("block_tags", "Artifacts:invisible_bedrock"      , "Anchor NoMining").getString();
				config.get("block_tags", "Artifacts:anti_anti_builder_stone", "Anchor NoMining").getString();
				config.get("block_tags", "Artifacts:anti_builder"           , "Anchor NoMining").getString();
				
				// placement priorities
				config.get("block_tags", "minecraft:lever"                  , "PlaceLater").getString();
				config.get("block_tags", "IC2:blockReinforcedFoam"          , "PlaceEarliest NoMining").getString();
				config.get("block_tags", "IC2:blockAlloy"                   , "PlaceEarliest NoMining").getString();
				config.get("block_tags", "IC2:blockAlloyGlass"              , "PlaceEarliest NoMining").getString();
				config.get("block_tags", "minecraft:obsidian"               , "PlaceEarliest Mining").getString();
				config.get("block_tags", "AdvancedRepulsionSystems:field"   , "PlaceEarlier NoMining").getString();
				// FIXME config.get("block_tags", "MFFS:field"						, "PlaceEarlier NoMining"	).getString();
				config.get("block_tags", "IC2:blockGenerator"               , "PlaceLater").getString();
				config.get("block_tags", "IC2:blockReactorChamber"          , "PlaceLatest").getString();
				config.get("block_tags", "ImmersiveEngineering:metalDevice" , "PlaceLatest").getString();	// FIXME: need to fine tune at metadata level
				// config.get("block_tags", "SGCraft:stargateBase"             , "PlaceEarliest").getString();
				// config.get("block_tags", "SGCraft:stargateBase"             , "PlaceEarliest").getString();
				// config.get("block_tags", "SGCraft:stargateRing"             , "PlaceEarlier").getString();
				// config.get("block_tags", "SGCraft:stargateController"       , "PlaceLatest").getString();
				
				// expandables, a.k.a. "don't blow my ship with this..."
				config.get("block_tags", "WarpDrive:blockGas"               , "LeftBehind Expandable").getString();
				config.get("block_tags", "Railcraft:residual.heat"          , "LeftBehind Expandable").getString();
				config.get("block_tags", "InvisibLights:blockLightSource"   , "NoMass Expandable").getString();
				config.get("block_tags", "WarpDrive:blockAir"               , "NoMass Expandable PlaceLatest").getString();
				
				// mining a mineshaft...
				config.get("block_tags", "minecraft:web"                    , "Mining").getString();
				config.get("block_tags", "minecraft:fence"                  , "Mining").getString();
				config.get("block_tags", "minecraft:torch"                  , "Mining").getString();
				config.get("block_tags", "minecraft:glowstone"              , "Mining").getString();
				config.get("block_tags", "minecraft:redstone_block"         , "Mining").getString();
				
				// mining an 'end' moon
				config.get("block_tags", "WarpDrive:blockIridium"           , "Mining").getString();	// stronger than obsidian but can still be mined (see ender moon)
				taggedBlocksName = categoryBlockTags.getValues().keySet().toArray(new String[0]);
			}
			taggedBlocks = new HashMap(taggedBlocksName.length);
			for (String name : taggedBlocksName) {
				String tags = config.get("block_tags", name, "").getString();
				taggedBlocks.put(name, tags);
			}
		}
		
		// Entity dictionary
		{
			config.addCustomCategoryComment("entity_tags", 
					  "Use this section to enable special behavior on entities using tags.\n"
					+ "Most entities are already supported automatically. Only modify this section when something doesn't work!\n" + "\n"
					+ "Tags shall be separated by at least one space, comma or tabulation.\n" + "Invalid tags will be ignored silently. Tags and block names are case sensitive.\n"
					+ "In case of conflicts, the latest tag overwrite the previous ones.\n" + "- Anchor: ship can't move with this entity aboard (default: none).\n"
					+ "- NoMass: this entity doesn't count when calculating ship volume/mass (default: Galacticraft air bubble).\n"
					+ "- LeftBehind: this entity won't move with your ship (default: Galacticraft air bubble).\n"
					+ "- NonLivingTarget: this non-living entity can be targeted/removed by weapons (default: ItemFrame, Painting).");
			
			ConfigCategory categoryEntityTags = config.getCategory("entity_tags");
			String[] taggedEntitiesName = categoryEntityTags.getValues().keySet().toArray(new String[0]);
			if (taggedEntitiesName.length == 0) {
				config.get("entity_tags", "GalacticraftCore.OxygenBubble", "NoMass LeftBehind").getString();
				config.get("entity_tags", "ItemFrame"                    , "NoMass NonLivingTarget").getString();
				config.get("entity_tags", "Painting"                     , "NoMass NonLivingTarget").getString();
				config.get("entity_tags", "LeashKnot"                    , "NoMass NonLivingTarget").getString();
				config.get("entity_tags", "Boat"                         , "NoMass NonLivingTarget").getString();
				config.get("entity_tags", "MinecartRideable"             , "NoMass NonLivingTarget").getString();
				config.get("entity_tags", "MinecartChest"                , "NoMass NonLivingTarget").getString();
				config.get("entity_tags", "MinecartFurnace"              , "NoMass NonLivingTarget").getString();
				config.get("entity_tags", "MinecartTNT"                  , "NoMass NonLivingTarget").getString();
				config.get("entity_tags", "MinecartHopper"               , "NoMass NonLivingTarget").getString();
				config.get("entity_tags", "MinecartSpawner"              , "NoMass NonLivingTarget").getString();
				config.get("entity_tags", "EnderCrystal"                 , "NoMass NonLivingTarget").getString();
				
				config.get("entity_tags", "IC2.BoatCarbon"               , "NoMass NonLivingTarget").getString();
				config.get("entity_tags", "IC2.BoatRubber"               , "NoMass NonLivingTarget").getString();
				config.get("entity_tags", "IC2.BoatElectric"             , "NoMass NonLivingTarget").getString();
				config.get("entity_tags", "IC2.Nuke"                     , "NoMass NonLivingTarget").getString();
				config.get("entity_tags", "IC2.Itnt"                     , "NoMass NonLivingTarget").getString();
				config.get("entity_tags", "IC2.StickyDynamite"           , "NoMass NonLivingTarget").getString();
				config.get("entity_tags", "IC2.Dynamite"                 , "NoMass NonLivingTarget").getString();
				taggedEntitiesName = categoryEntityTags.getValues().keySet().toArray(new String[0]);
			}
			taggedEntities = new HashMap(taggedEntitiesName.length);
			for (String name : taggedEntitiesName) {
				String tags = config.get("entity_tags", name, "").getString();
				taggedEntities.put(name, tags);
			}
		}
		
		// Item dictionary
		{
			config.addCustomCategoryComment("item_tags", "Use this section to enable special behavior on items using tags.\n"
					+ "Most items are already supported automatically. Only modify this section when something doesn't work!\n" + "\n"
					+ "Tags shall be separated by at least one space, comma or tabulation.\n" + "Invalid tags will be ignored silently. Tags and block names are case sensitive.\n"
					+ "In case of conflicts, the latest tag overwrite the previous ones.\n" + "- FlyInSpace: player can move without gravity effect while wearing this item (default: jetpacks).\n"
					+ "- NoFallDamage: player doesn't take fall damage while wearing this armor item (default: IC2 rubber boots).\n"
					+ "- BreathingIC2: player can breath IC2 compressed air while wearing this armor item (default: IC2 nano helmet and Cie).\n");
			
			ConfigCategory categoryItemTags = config.getCategory("item_tags");
			String[] taggedItemsName = categoryItemTags.getValues().keySet().toArray(new String[0]);
			if (taggedItemsName.length == 0) {
				config.get("item_tags", "IC2:itemArmorHazmatHelmet", "BreathingIC2").getString();
				config.get("item_tags", "IC2:itemSolarHelmet", "BreathingIC2").getString();
				config.get("item_tags", "IC2:itemArmorNanoHelmet", "BreathingIC2").getString();
				config.get("item_tags", "IC2:itemArmorQuantumHelmet", "BreathingIC2").getString();
				config.get("item_tags", "AdvancedSolarPanel:advanced_solar_helmet", "BreathingIC2").getString();
				config.get("item_tags", "AdvancedSolarPanel:hybrid_solar_helmet", "BreathingIC2").getString();
				config.get("item_tags", "AdvancedSolarPanel:ultimate_solar_helmet", "BreathingIC2").getString();
				
				config.get("item_tags", "IC2:itemArmorJetpack", "FlyInSpace NoFallDamage").getString();
				config.get("item_tags", "IC2:itemArmorJetpackElectric", "FlyInSpace NoFallDamage").getString();
				config.get("item_tags", "GraviSuite:advJetpack", "FlyInSpace NoFallDamage").getString();
				config.get("item_tags", "GraviSuite:advNanoChestPlate", "FlyInSpace NoFallDamage").getString();
				config.get("item_tags", "GraviSuite:graviChestPlate", "FlyInSpace NoFallDamage").getString();
				
				config.get("item_tags", "IC2:itemArmorRubBoots", "NoFallDamage").getString();
				config.get("item_tags", "IC2:itemArmorQuantumBoots", "NoFallDamage").getString();
				taggedItemsName = categoryItemTags.getValues().keySet().toArray(new String[0]);
			}
			taggedItems = new HashMap(taggedItemsName.length);
			for (String name : taggedItemsName) {
				String tags = config.get("item_tags", name, "").getString();
				taggedItems.put(name, tags);
			}
		}
		
	}
	
	public static void apply() {
		// get default settings from parsing ore dictionary
		BLOCKS_ORES = new HashSet<Block>();
		BLOCKS_LOGS = new HashSet<Block>();
		BLOCKS_LEAVES = new HashSet<Block>();
		String[] oreNames = OreDictionary.getOreNames();
		for (String oreName : oreNames) {
			String lowerOreName = oreName.toLowerCase();
			if (oreName.length() > 4 && oreName.substring(0, 3).equals("ore")) {
				ArrayList<ItemStack> itemStacks = OreDictionary.getOres(oreName);
				for (ItemStack itemStack : itemStacks) {
					BLOCKS_ORES.add(Block.getBlockFromItem(itemStack.getItem()));
					// WarpDrive.logger.info("- added " + oreName + " to ores as " + itemStack);
				}
			}
			if (lowerOreName.startsWith("log") || lowerOreName.endsWith("log") || lowerOreName.endsWith("logs")) {
				ArrayList<ItemStack> itemStacks = OreDictionary.getOres(oreName);
				for (ItemStack itemStack : itemStacks) {
					BLOCKS_LOGS.add(Block.getBlockFromItem(itemStack.getItem()));
					// WarpDrive.logger.info("- added " + oreName + " to logs as " + itemStack);
				}
			}
			if (lowerOreName.startsWith("leave") || lowerOreName.endsWith("leave") || lowerOreName.endsWith("leaves")) {
				ArrayList<ItemStack> itemStacks = OreDictionary.getOres(oreName);
				for (ItemStack itemStack : itemStacks) {
					BLOCKS_LEAVES.add(Block.getBlockFromItem(itemStack.getItem()));
					// WarpDrive.logger.info("- added " + oreName + " to leaves as " + itemStack);
				}
			}
		}
		
		// translate tagged blocks
		BLOCKS_SOILS = new HashSet(taggedBlocks.size());
		BLOCKS_ANCHOR = new HashSet(taggedBlocks.size());
		BLOCKS_NOMASS = new HashSet(taggedBlocks.size() + BLOCKS_LEAVES.size());
		BLOCKS_NOMASS.addAll(BLOCKS_LEAVES);
		BLOCKS_LEFTBEHIND = new HashSet(taggedBlocks.size());
		BLOCKS_EXPANDABLE = new HashSet(taggedBlocks.size() + BLOCKS_LEAVES.size());
		BLOCKS_EXPANDABLE.addAll(BLOCKS_LEAVES);
		BLOCKS_MINING = new HashSet(taggedBlocks.size());
		BLOCKS_NOMINING = new HashSet(taggedBlocks.size());
		BLOCKS_PLACE = new HashMap(taggedBlocks.size());
		for (Entry<String, String> taggedBlock : taggedBlocks.entrySet()) {
			Block block = Block.getBlockFromName(taggedBlock.getKey());
			if (block == null) {
				WarpDrive.logger.info("Ignoring missing block " + taggedBlock.getKey());
				continue;
			}
			for (String tag : taggedBlock.getValue().replace("\t", " ").replace(",", " ").replace("  ", " ").split(" ")) {
				switch (tag) {
				case "Soil"         : BLOCKS_SOILS.add(block); break;
				case "Log"          : BLOCKS_LOGS.add(block); break;
				case "Leaf"         : BLOCKS_LEAVES.add(block); break;
				case "Anchor"       : BLOCKS_ANCHOR.add(block); break;
				case "NoMass"       : BLOCKS_NOMASS.add(block); break;
				case "LeftBehind"   : BLOCKS_LEFTBEHIND.add(block); break;
				case "Expandable"   : BLOCKS_EXPANDABLE.add(block); break;
				case "Mining"       : BLOCKS_MINING.add(block); break;
				case "NoMining"     : BLOCKS_NOMINING.add(block); break;
				case "PlaceEarliest": BLOCKS_PLACE.put(block, 0); break;
				case "PlaceEarlier" : BLOCKS_PLACE.put(block, 1); break;
				case "PlaceNormal"  : BLOCKS_PLACE.put(block, 2); break;
				case "PlaceLater"   : BLOCKS_PLACE.put(block, 3); break;
				case "PlaceLatest"  : BLOCKS_PLACE.put(block, 4); break;
				default:
					WarpDrive.logger.error("Unsupported tag '" + tag + "' for block " + block);
					break;
				}
			}
		}
		
		// translate tagged entities
		ENTITIES_ANCHOR = new HashSet(taggedEntities.size());
		ENTITIES_NOMASS = new HashSet(taggedEntities.size());
		ENTITIES_LEFTBEHIND = new HashSet(taggedEntities.size());
		ENTITIES_NONLIVINGTARGET = new HashSet(taggedEntities.size());
		for (Entry<String, String> taggedEntity : taggedEntities.entrySet()) {
			String entityId = taggedEntity.getKey();
			/* we can't detect missing entities, since some of them are 'hacked' in
			if (!EntityList.stringToIDMapping.containsKey(entityId)) {
				WarpDrive.logger.info("Ignoring missing entity " + entityId);
				continue;
			}
			/**/
			for (String tag : taggedEntity.getValue().replace("\t", " ").replace(",", " ").replace("  ", " ").split(" ")) {
				switch (tag) {
				case "Anchor"          : ENTITIES_ANCHOR.add(entityId); break;
				case "NoMass"          : ENTITIES_NOMASS.add(entityId); break;
				case "LeftBehind"      : ENTITIES_LEFTBEHIND.add(entityId); break;
				case "NonLivingTarget" : ENTITIES_NONLIVINGTARGET.add(entityId); break;
				default:
					WarpDrive.logger.error("Unsupported tag '" + tag + "' for entity " + entityId);
					break;
				}
			}
		}
		
		// translate tagged items
		ITEMS_FLYINSPACE = new HashSet(taggedItems.size());
		ITEMS_NOFALLDAMAGE = new HashSet(taggedItems.size());
		ITEMS_BREATHINGIC2 = new HashSet(taggedItems.size());
		for (Entry<String, String> taggedItem : taggedItems.entrySet()) {
			String itemId = taggedItem.getKey();
			Item item = GameData.getItemRegistry().getObject(itemId);
			if (item == null) {
				WarpDrive.logger.info("Ignoring missing item " + itemId);
				continue;
			}
			for (String tag : taggedItem.getValue().replace("\t", " ").replace(",", " ").replace("  ", " ").split(" ")) {
				switch (tag) {
				case "FlyInSpace"     : ITEMS_FLYINSPACE.add(item); break;
				case "NoFallDamage"   : ITEMS_NOFALLDAMAGE.add(item); break;
				case "BreathingIC2"   : ITEMS_BREATHINGIC2.add(item); break;
				default:
					WarpDrive.logger.error("Unsupported tag '" + tag + "' for item " + item);
					break;
				}
			}
		}
		
		adjustHardnessAndResistance();
		print();
	}
	
	private static void print() {
		// translate tagged blocks
		WarpDrive.logger.info("Active blocks dictionnary:");
		WarpDrive.logger.info("- " + BLOCKS_ORES.size() + " ores: " + getHashMessage(BLOCKS_ORES));
		WarpDrive.logger.info("- " + BLOCKS_SOILS.size() + " soils: " + getHashMessage(BLOCKS_SOILS));
		WarpDrive.logger.info("- " + BLOCKS_LOGS.size() + " logs: " + getHashMessage(BLOCKS_LOGS));
		WarpDrive.logger.info("- " + BLOCKS_LEAVES.size() + " leaves: " + getHashMessage(BLOCKS_LEAVES));
		WarpDrive.logger.info("- " + BLOCKS_ANCHOR.size() + " anchors: " + getHashMessage(BLOCKS_ANCHOR));
		WarpDrive.logger.info("- " + BLOCKS_NOMASS.size() + " with NoMass tag: " + getHashMessage(BLOCKS_NOMASS));
		WarpDrive.logger.info("- " + BLOCKS_LEFTBEHIND.size() + " with LeftBehind tag: " + getHashMessage(BLOCKS_LEFTBEHIND));
		WarpDrive.logger.info("- " + BLOCKS_EXPANDABLE.size() + " expandable: " + getHashMessage(BLOCKS_EXPANDABLE));
		WarpDrive.logger.info("- " + BLOCKS_MINING.size() + " with Mining tag: " + getHashMessage(BLOCKS_MINING));
		WarpDrive.logger.info("- " + BLOCKS_NOMINING.size() + " with NoMining tag: " + getHashMessage(BLOCKS_NOMINING));
		WarpDrive.logger.info("- " + BLOCKS_PLACE.size() + " with Placement priority: " + getHashMessage(BLOCKS_PLACE));
		
		// translate tagged entities
		WarpDrive.logger.info("Active entities dictionnary:");
		WarpDrive.logger.info("- " + ENTITIES_ANCHOR.size() + " anchors: " + getHashMessage(ENTITIES_ANCHOR));
		WarpDrive.logger.info("- " + ENTITIES_NOMASS.size() + " with NoMass tag: " + getHashMessage(ENTITIES_NOMASS));
		WarpDrive.logger.info("- " + ENTITIES_LEFTBEHIND.size() + " with LeftBehind tag: " + getHashMessage(ENTITIES_LEFTBEHIND));
		WarpDrive.logger.info("- " + ENTITIES_NONLIVINGTARGET.size() + " with NonLivingTarget tag: " + getHashMessage(ENTITIES_NONLIVINGTARGET));
		
		// translate tagged items
		WarpDrive.logger.info("Active items dictionnary:");
		WarpDrive.logger.info("- " + ITEMS_FLYINSPACE.size() + " allowing fly in space: " + getHashMessage(ITEMS_FLYINSPACE));
		WarpDrive.logger.info("- " + ITEMS_NOFALLDAMAGE.size() + " absorbing fall damages: " + getHashMessage(ITEMS_NOFALLDAMAGE));
		WarpDrive.logger.info("- " + ITEMS_BREATHINGIC2.size() + " allowing breathing compressed air: " + getHashMessage(ITEMS_BREATHINGIC2));
	}
	
	private static void adjustHardnessAndResistance() {
		// Apply explosion resistance adjustments
		Blocks.obsidian.setResistance(60.0F);
		Blocks.enchanting_table.setResistance(60.0F);
		Blocks.ender_chest.setResistance(60.0F);
		Blocks.anvil.setResistance(60.0F);
		Blocks.water.setResistance(30.0F);
		Blocks.flowing_water.setResistance(30.0F);
		Blocks.lava.setResistance(30.0F);
		Blocks.flowing_lava.setResistance(30.0F);
		
		// adjust IC2 Reinforced stone stats
		if (WarpDriveConfig.isIndustrialCraft2loaded) {
			Block blockReinforcedStone = (Block) Block.blockRegistry.getObject("IC2:blockAlloy");
			// TODO
		}
		
		// scan blocks registry
		for(Object blockKey : Block.blockRegistry.getKeys()) {
			Object block = Block.blockRegistry.getObject(blockKey);
			WarpDrive.logger.debug("Checking block registry for '" + blockKey + "': " + block);
			if (!(block instanceof Block)) {
				WarpDrive.logger.error("Block registry for '" + blockKey + "': this is not a block? " + block);
			} else {
				// get hardness and blast resistance
				float hardness = -2.0F;
				if (WarpDrive.fieldBlockHardness != null) {
					// WarpDrive.fieldBlockHardness.setAccessible(true);
					try {
						hardness = (float)WarpDrive.fieldBlockHardness.get(block);
					} catch (IllegalArgumentException | IllegalAccessException exception) {
						exception.printStackTrace();
						WarpDrive.logger.error("Unable to access block hardness value '" + blockKey + "' " + block);
					}
				}
				
				float blastResistance = ((Block)block).getExplosionResistance(null);
				
				// check actual values
				if (hardness != -2.0F) {
					if (hardness < 0 && !(BLOCKS_ANCHOR.contains(block))) {// unbreakable block
						WarpDrive.logger.warn("Warning: non-anchor block with unbreakable hardness '" + blockKey + "' " + block + " (" + hardness + ")");
					} else if (hardness > WarpDriveConfig.HULL_HARDNESS[0] && !(block instanceof BlockHullPlain || block instanceof BlockHullGlass || BLOCKS_ANCHOR.contains(block))) {
						WarpDrive.logger.warn("Warning: non-hull block with high hardness '" + blockKey + "' " + block + " (" + hardness + ")");
					}
				}
				if (blastResistance > WarpDriveConfig.HULL_BLAST_RESISTANCE[0] && !(block instanceof BlockHullPlain || block instanceof BlockHullGlass || BLOCKS_ANCHOR.contains(block))) {
					((Block)block).setResistance(WarpDriveConfig.HULL_BLAST_RESISTANCE[0]);
					WarpDrive.logger.warn("Warning: non-anchor block with crazy blast resistance '" + blockKey + "' " + block + " (" + hardness + ")");
					if (adjustResistance) {
						WarpDrive.logger.warn("Adjusting blast resistance of '" + blockKey + "' " + block + " from " + blastResistance + " to " + ((Block)block).getExplosionResistance(null));
						if (((Block)block).getExplosionResistance(null) > WarpDriveConfig.HULL_BLAST_RESISTANCE[0]) {
							WarpDrive.logger.error("Blacklisting block with crazy blast resistance '" + blockKey + "' " + block + " (" + blastResistance + ")");
							BLOCKS_ANCHOR.add((Block)block);
							BLOCKS_NOMINING.add((Block)block);
						}
					}
				}
				
				if (WarpDriveConfig.LOGGING_DICTIONARY) {
					WarpDrive.logger.info("Block registry for '" + blockKey + "': Block " + block
						+ " with hardness " + (WarpDrive.fieldBlockHardness != null ? hardness : "-") + " resistance " + ((Block)block).getExplosionResistance(null));
				}
			}
		}
	}
	
	private static String getHashMessage(HashSet hashSet) {
		String message = "";
		for (Object object : hashSet) {
			if (!message.isEmpty()) {
				message += ", ";
			}
			if (object instanceof Block) {
				message += GameRegistry.findUniqueIdentifierFor((Block) object);
			} else if (object instanceof String) {
				message += (String) object;
			} else {
				message += object;
			}
			
		}
		return message;
	}
	
	private static String getHashMessage(HashMap<Block, Integer> hashMap) {
		String message = "";
		for (Entry<Block, Integer> entry : hashMap.entrySet()) {
			if (!message.isEmpty()) {
				message += ", ";
			}
			message += GameRegistry.findUniqueIdentifierFor(entry.getKey()) + "=" + entry.getValue();
		}
		return message;
	}
}
