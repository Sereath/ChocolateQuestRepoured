package team.cqr.cqrepoured.world.structure.generation.inhabitants;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.block.banner.EBanners;
import team.cqr.cqrepoured.util.PropertyFileHelper;

import javax.annotation.Nullable;
import java.util.Properties;
import java.util.Random;

public class DungeonInhabitant {

	private String name;

	private ResourceLocation[] entityIDs = new ResourceLocation[] {};
	private ResourceLocation[] bossIDs = new ResourceLocation[] {};

	private EBanners assignedBanner = null;

	private Item shieldReplacement = null;

	private String factionOverride = null;

	private static final Random random = new Random();
	private static final ResourceLocation EMPTY_RES_LOC = new ResourceLocation(CQRMain.MODID, "dummy");

	public DungeonInhabitant(Properties prop) {
		this.name = prop.getProperty(ConfigKeys.KEY_NAME, "missingNo");
		this.entityIDs = PropertyFileHelper.getResourceLocationArrayProperty(prop, ConfigKeys.KEY_ENTITY_ID_LIST, this.entityIDs, true);
		this.bossIDs = PropertyFileHelper.getResourceLocationArrayProperty(prop, ConfigKeys.KEY_BOSS_ID_LIST, this.bossIDs, true);
		String tmp = prop.getProperty(ConfigKeys.KEY_BANNER, "UNUSED");
		if (!tmp.equalsIgnoreCase("UNUSED")) {
			this.assignedBanner = EBanners.valueOf(tmp);
		}
		tmp = prop.getProperty(ConfigKeys.KEY_FACTION_OVERRIDE, "UNUSED");
		if (!tmp.equalsIgnoreCase("UNUSED")) {
			this.factionOverride = tmp;
		}
		String stTmp = prop.getProperty(ConfigKeys.KEY_SHIELD_ITEM, null);
		if (stTmp != null && !stTmp.isEmpty() && !stTmp.equalsIgnoreCase("UNUSED")) {
			ResourceLocation itemResLoc = new ResourceLocation(stTmp);
			this.shieldReplacement = ForgeRegistries.ITEMS.getValue(itemResLoc);
		}
	}

	public DungeonInhabitant(EDefaultInhabitants inha) {
		this.entityIDs = inha.getEntityIDs();
		this.bossIDs = inha.getBossIDs();
		this.assignedBanner = inha.getBanner();
		this.shieldReplacement = inha.getShieldItem().getItem();
		this.name = inha.name();
	}

	public String getName() {
		return this.name;
	}

	public ResourceLocation getEntityID() {
		return (this.entityIDs == null || this.entityIDs.length <= 0) ? EMPTY_RES_LOC : this.entityIDs[random.nextInt(this.entityIDs.length)];
	}

	public ResourceLocation getBossID() {
		return this.bossIDs.length <= 0 ? null : this.bossIDs[random.nextInt(this.bossIDs.length)];
	}

	@Nullable
	public EBanners getBanner() {
		return this.assignedBanner;
	}

	public Item getShieldReplacement() {
		return this.shieldReplacement == null ? Items.SHIELD : this.shieldReplacement;
	}

	@Nullable
	public String getFactionOverride() {
		if (this.factionOverride == null || this.factionOverride.isEmpty()) {
			return null;
		}
		return this.factionOverride;
	}

	private static class ConfigKeys {
		public static String KEY_NAME = "name";
		public static String KEY_ENTITY_ID_LIST = "possibleEntities";
		public static String KEY_BOSS_ID_LIST = "possibleBosses";
		public static String KEY_BANNER = "banner";
		public static String KEY_SHIELD_ITEM = "shieldReplacement";
		public static String KEY_FACTION_OVERRIDE = "factionOverride";
	}

}
