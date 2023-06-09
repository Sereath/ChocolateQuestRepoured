package team.cqr.cqrepoured.init;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Items;
import net.minecraft.item.crafting.Ingredient;
import team.cqr.cqrepoured.config.CQRArmorMaterial;
import team.cqr.cqrepoured.config.CQRConfig;
import team.cqr.cqrepoured.config.CQRItemTier;

public class CQRMaterials {

	public static class ArmorMaterials {

		public static final CQRArmorMaterial ARMOR_BACKPACK = new CQRArmorMaterial("backpack", CQRConfig.SERVER_CONFIG.materials.armorMaterials.backpack, SoundEvents.ARMOR_EQUIP_ELYTRA, () -> Ingredient.of(Items.LEATHER));
		public static final CQRArmorMaterial ARMOR_BULL = new CQRArmorMaterial("bull", CQRConfig.SERVER_CONFIG.materials.armorMaterials.bull, SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.of(CQRItems.LEATHER_BULL.get()));
		public static final CQRArmorMaterial ARMOR_CLOUD = new CQRArmorMaterial("cloud", CQRConfig.SERVER_CONFIG.materials.armorMaterials.cloud, SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.of(Items.DIAMOND));
		public static final CQRArmorMaterial ARMOR_DRAGON = new CQRArmorMaterial("dragon", CQRConfig.SERVER_CONFIG.materials.armorMaterials.dragon, SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.EMPTY);
		public static final CQRArmorMaterial ARMOR_HEAVY_DIAMOND = new CQRArmorMaterial("heavy_diamond", CQRConfig.SERVER_CONFIG.materials.armorMaterials.heavyDiamond, SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.of(Items.DIAMOND));
		public static final CQRArmorMaterial ARMOR_HEAVY_IRON = new CQRArmorMaterial("heavy_iron", CQRConfig.SERVER_CONFIG.materials.armorMaterials.heavyIron, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(Items.IRON_INGOT));
		public static final CQRArmorMaterial ARMOR_INQUISITION = new CQRArmorMaterial("inquisition", CQRConfig.SERVER_CONFIG.materials.armorMaterials.inquisition, SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.of(Items.DIAMOND));
		public static final CQRArmorMaterial ARMOR_CROWN = new CQRArmorMaterial("king_crown", CQRConfig.SERVER_CONFIG.materials.armorMaterials.kingCrown, SoundEvents.ARMOR_EQUIP_GOLD, () -> Ingredient.of(Items.GOLD_INGOT));
		public static final CQRArmorMaterial ARMOR_SLIME = new CQRArmorMaterial("slime", CQRConfig.SERVER_CONFIG.materials.armorMaterials.slime, SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.of(CQRItems.BALL_SLIME.get()));
		public static final CQRArmorMaterial ARMOR_SPIDER = new CQRArmorMaterial("spider", CQRConfig.SERVER_CONFIG.materials.armorMaterials.spider, SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.of(CQRItems.LEATHER_SPIDER.get()));
		public static final CQRArmorMaterial ARMOR_TURTLE = new CQRArmorMaterial("turtle", CQRConfig.SERVER_CONFIG.materials.armorMaterials.turtle, SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.of(CQRItems.SCALE_TURTLE.get()));

	}

	public static class CQRItemTiers {

		public static final CQRItemTier TOOL_BULL = new CQRItemTier(CQRConfig.SERVER_CONFIG.materials.itemTiers.bull, () -> Ingredient.of(CQRItems.LEATHER_BULL.get()));
		public static final CQRItemTier TOOL_MONKING = new CQRItemTier(CQRConfig.SERVER_CONFIG.materials.itemTiers.monking, () -> Ingredient.of(CQRItems.BONE_MONKING.get()));
		public static final CQRItemTier TOOL_MOONLIGHT = new CQRItemTier(CQRConfig.SERVER_CONFIG.materials.itemTiers.moonlight, () -> Ingredient.EMPTY);
		public static final CQRItemTier TOOL_NINJA = new CQRItemTier(CQRConfig.SERVER_CONFIG.materials.itemTiers.ninja, () -> Ingredient.EMPTY);
		public static final CQRItemTier TOOL_SPIDER = new CQRItemTier(CQRConfig.SERVER_CONFIG.materials.itemTiers.spider, () -> Ingredient.of(CQRItems.LEATHER_SPIDER.get()));
		public static final CQRItemTier TOOL_SUNSHINE = new CQRItemTier(CQRConfig.SERVER_CONFIG.materials.itemTiers.sunshine, () -> Ingredient.EMPTY);
		public static final CQRItemTier TOOL_TURTLE = new CQRItemTier(CQRConfig.SERVER_CONFIG.materials.itemTiers.turtle, () -> Ingredient.of(CQRItems.SCALE_TURTLE.get()));
		public static final CQRItemTier TOOL_WALKER = new CQRItemTier(CQRConfig.SERVER_CONFIG.materials.itemTiers.walker, () -> Ingredient.EMPTY);

	}

}
