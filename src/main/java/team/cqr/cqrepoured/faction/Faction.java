package team.cqr.cqrepoured.faction;

import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import team.cqr.cqrepoured.capability.faction.IFactionRelationCapability;
import team.cqr.cqrepoured.config.CQRConfig;
import team.cqr.cqrepoured.customtextures.TextureSetNew;
import team.cqr.cqrepoured.faction.EReputationState.EReputationStateRough;
import team.cqr.cqrepoured.init.CQRDatapackLoaders;
import team.cqr.cqrepoured.util.registration.AbstractRegistratableObject;

public class Faction extends AbstractRegistratableObject implements IFactionRelated {
	
	private boolean repuMayChange = true;
	private Map<ResourceLocation, EReputationState> factionRelations;
	private Optional<TextureSetNew> textureSet = Optional.empty();
	private ReputationSettings reputationSettings;

	public static record ReputationSettings(EReputationState defaultReputation, int onKillMember, int onKillAlly, int onKillEnemy) {
		public static final Codec<ReputationSettings> CODEC = RecordCodecBuilder.create(instance -> {
			return instance.group(
					EReputationState.CODEC.fieldOf("defaultReputation").forGetter(ReputationSettings::defaultReputation),
					Codec.INT.fieldOf("reputationChangeOnMemberKill").forGetter(ReputationSettings::onKillMember),
					Codec.INT.fieldOf("reputationChangeOnAllyKill").forGetter(ReputationSettings::onKillAlly),
					Codec.INT.fieldOf("reputationChangeOnEnemyKill").forGetter(ReputationSettings::onKillEnemy)
			).apply(instance, ReputationSettings::new);
		});
	}
	
	public static final Codec<Faction> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				Codec.unboundedMap(ResourceLocation.CODEC, EReputationState.CODEC).fieldOf("relations").forGetter(Faction::getRelations),
				Codec.BOOL.fieldOf("changeableReputation").forGetter(Faction::canRepuChange),
				ReputationSettings.CODEC.fieldOf("reputation").forGetter(Faction::getReputationSettings),
				CQRDatapackLoaders.TEXTURE_SETS.byNameCodec().optionalFieldOf("textureSet").forGetter(Faction::getTextureSet)
		).apply(instance, Faction::new);
	});
	
	public Faction(Map<ResourceLocation, EReputationState> factionRelations, boolean repuCanChange, ReputationSettings repuSettings, Optional<TextureSetNew> optTextureSet) {
		this.factionRelations = factionRelations;
		this.repuMayChange = repuCanChange;
		this.reputationSettings = repuSettings;
		this.textureSet = optTextureSet;
	}
	
	public ReputationSettings getReputationSettings() {
		return this.reputationSettings;
	}
	
	public Optional<TextureSetNew> getTextureSet() {
		return this.textureSet;
	}
	
	public int getRepuMemberKill() {
		return this.reputationSettings.onKillMember();
	}

	public int getRepuAllyKill() {
		return this.reputationSettings.onKillAlly();
	}

	public int getRepuEnemyKill() {
		return this.reputationSettings.onKillEnemy();
	}

	public EReputationState getDefaultReputation() {
		return this.reputationSettings.defaultReputation();
	}

	@Nullable
	public ResourceLocation getRandomTextureFor(Entity entity) {
		if (this.textureSet != null && this.textureSet.isPresent()) {
			return this.textureSet.get().getRandomTextureFor(entity);
		}
		// Debug
		// System.out.println("No texture set defined for faction: " + this.name);
		return null;
	}

	// DONE: Special case for player faction!!
	public <T extends Entity & IFactionRelated> boolean isEnemy(T ent) {
		if (CQRConfig.SERVER_CONFIG.advanced.enableOldFactionMemberTeams.get()) {
			if (ent.getTeam() != null && ent.getTeam().getName().equalsIgnoreCase(this.getId().toString())) {
				return false;
			}
		}
		if (ent.level().getDifficulty() == Difficulty.PEACEFUL) {
			return false;
		}
		return ent.isEnemyOf(this);
	}
	
	public EReputationState getReputationTowards(Faction faction) {
		return this.factionRelations.getOrDefault(faction.getId(), EReputationState.NEUTRAL);
	}
	
	public EReputationStateRough getRoughReputationTowards(Faction faction) {
		return EReputationStateRough.getByRepuScore(this.getReputationTowards(faction).getValue());
	}

	public boolean isEnemy(Faction faction) {
		if (faction == null) {
			return false;
		}
		if (faction == this) {
			return false;
		}
		return this.getRoughReputationTowards(faction).isEnemy();
	}

	// DONE: Special case for player faction!!
	public <T extends Entity & IFactionRelated> boolean isAlly(T ent) {
		if (CQRConfig.SERVER_CONFIG.advanced.enableOldFactionMemberTeams.get()) {
			if (ent.getTeam() != null && ent.getTeam().getName().equalsIgnoreCase(this.getId().toString())) {
				return true;
			}
		}
		return ent.isAllyOf(this);
	}

	public <T extends IFactionRelated & ICapabilityProvider> void decrementReputation(T capProvider, int score, Level level) {
		this.changeReputation(capProvider, -Math.abs(score), level);
	}

	public <T extends IFactionRelated & ICapabilityProvider> void incrementReputation(T capProvider, int score, Level level) {
		this.changeReputation(capProvider, Math.abs(score), level);
	}
	
	protected <T extends IFactionRelated & ICapabilityProvider> void changeReputation(T capProvider, int score, Level level) {
		if (this.repuMayChange && !level.getDifficulty().equals(Difficulty.PEACEFUL) && !(capProvider instanceof Player player && (player.isSpectator() || player.isCreative()))) {
			LazyOptional<IFactionRelationCapability> lOpCap = capProvider.getCapability(IFactionRelationCapability.INSTANCE);
			if (lOpCap != null && lOpCap.isPresent()) {
				Optional<IFactionRelationCapability> opCap = lOpCap.resolve();
				if (opCap.isPresent()) {
					IFactionRelationCapability cap = opCap.get();
					int currentReputation = capProvider.getExactRelationTowards(this);
					int newReputation = score + currentReputation;
					
					newReputation = Mth.clamp(newReputation, EReputationState.ARCH_ENEMY.getValue(), EReputationState.MEMBER.getValue());
					
					cap.setReputationTowards(this, newReputation);
				}
			}
		}
	}

	public boolean canRepuChange() {
		return this.repuMayChange;
	}
	
	public Map<ResourceLocation, EReputationState> getRelations() {
		return this.factionRelations;
	}

	@Override
	public boolean isEnemyOf(Faction faction) {
		return this.isEnemy(faction);
	}

	@Override
	public boolean isMemberOf(Faction faction) {
		// A faction can't be a member of a faction
		return false;
	}

	@Override
	public EReputationState getRelationTowards(Faction faction) {
		return this.getReputationTowards(faction);
	}

	@Override
	public EReputationStateRough getRoughRelationTowards(Faction faction) {
		return this.getRoughRelationTowards(faction);
	}

	@Override
	public int getExactRelationTowards(Faction faction) {
		return this.getReputationTowards(faction).getValue();
	}

}
