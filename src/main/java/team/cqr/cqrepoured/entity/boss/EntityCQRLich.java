package team.cqr.cqrepoured.entity.boss;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.joml.Vector3d;

import net.minecraft.core.BlockPos;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.config.CQRConfig;
import team.cqr.cqrepoured.entity.IAnimatableCQR;
import team.cqr.cqrepoured.entity.ai.spells.EntityAIArmorSpell;
import team.cqr.cqrepoured.entity.ai.spells.EntityAIFangAttack;
import team.cqr.cqrepoured.entity.ai.spells.EntityAIShootPoisonProjectiles;
import team.cqr.cqrepoured.entity.ai.spells.EntityAISummonMinionSpell;
import team.cqr.cqrepoured.entity.bases.AbstractEntityCQR;
import team.cqr.cqrepoured.entity.bases.ISummoner;
import team.cqr.cqrepoured.entity.misc.EntitySummoningCircle.ECircleTexture;
import team.cqr.cqrepoured.faction.EDefaultFaction;
import team.cqr.cqrepoured.faction.Faction;
import team.cqr.cqrepoured.init.CQRBlocks;
import team.cqr.cqrepoured.init.CQREntityTypes;

public class EntityCQRLich extends AbstractEntityCQRMageBase implements ISummoner, IAnimatableCQR {

	protected List<Entity> summonedMinions = new ArrayList<>();
	protected BlockPos currentPhylacteryPosition = null;

	public EntityCQRLich(World world) {
		this(CQREntityTypes.LICH.get(), world);
	}

	public EntityCQRLich(EntityType<? extends AbstractEntityCQR> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		List<Entity> tmp = new ArrayList<>();
		for (Entity ent : this.summonedMinions) {
			if (ent == null || !ent.isAlive()) {
				tmp.add(ent);
			}
		}
		for (Entity e : tmp) {
			this.summonedMinions.remove(e);
		}
		// Phylactery
		if (this.currentPhylacteryPosition != null) {
			if (this.level.getBlockState(this.currentPhylacteryPosition).getBlock() == CQRBlocks.PHYLACTERY.get()) {
				this.setMagicArmorActive(true);
			} else {
				this.currentPhylacteryPosition = null;
				this.setMagicArmorActive(false);
			}
		}
	}

	public void setCurrentPhylacteryBlock(BlockPos pos) {
		this.setMagicArmorActive(true);
		this.currentPhylacteryPosition = pos;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.spellHandler.addSpell(0, new EntityAIArmorSpell(this, 50, 30) {
			@Override
			public boolean isInterruptible() {
				return true;
			}
		});
		this.spellHandler.addSpell(1, new EntityAISummonMinionSpell(this, 15, 10, new ResourceLocation(CQRMain.MODID, "zombie"), ECircleTexture.ZOMBIE, true, 25, 5, new Vector3d(0, 0, 0)) {
			@Override
			public boolean isInterruptible() {
				return false;
			}
		});
		this.spellHandler.addSpell(2, new EntityAIFangAttack(this, 20, 5, 1, 12) {
			@Override
			public boolean isInterruptible() {
				return false;
			}
		});
		this.spellHandler.addSpell(3, new EntityAIShootPoisonProjectiles(this, 40, 20) {
			@Override
			public boolean isInterruptible() {
				return false;
			}
		});
	}

	@Override
	public void die(DamageSource cause) {
		// Kill minions
		for (Entity e : this.summonedMinions) {
			if (e != null && e.isAlive()) {
				if (e instanceof LivingEntity) {
					((LivingEntity) e).die(cause);
				}
				e.remove();
			}
		}
		this.summonedMinions.clear();

		super.die(cause);
	}

	@Override
	public double getBaseHealth() {
		return CQRConfig.SERVER_CONFIG.baseHealths.lich.get();
	}

	@Override
	public EDefaultFaction getDefaultFaction() {
		return EDefaultFaction.UNDEAD;
	}

	@Override
	public Faction getSummonerFaction() {
		return this.getFaction();
	}

	@Override
	public List<Entity> getSummonedEntities() {
		return this.summonedMinions;
	}

	@Override
	public LivingEntity getSummoner() {
		return this;
	}

	@Override
	public void addSummonedEntityToList(Entity summoned) {
		this.summonedMinions.add(summoned);
	}

	@Override
	protected void updateCooldownForMagicArmor() {
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		super.addAdditionalSaveData(compound);
		if (this.currentPhylacteryPosition != null) {
			compound.put("currentPhylactery", NBTUtil.writeBlockPos(this.currentPhylacteryPosition));
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("currentPhylactery")) {
			this.currentPhylacteryPosition = NBTUtil.readBlockPos(compound.getCompound("currentPhylactery"));
		}
	}

	@Override
	public CreatureAttribute getMobType() {
		return CreatureAttribute.UNDEAD;
	}

	public boolean hasPhylactery() {
		return (this.currentPhylacteryPosition != null && (this.level.getBlockState(this.currentPhylacteryPosition).getBlock() == CQRBlocks.PHYLACTERY.get()));
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	// Geckolib
	private AnimationFactory factory = GeckoLibUtil.createFactory(this);

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@Override
	public Set<String> getAlwaysPlayingAnimations() {
		return null;
	}
	
	@Override
	public void registerControllers(AnimationData data) {
		this.registerControllers(this, data);
	}

}
