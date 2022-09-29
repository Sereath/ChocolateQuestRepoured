package team.cqr.cqrepoured.entity.boss.endercalamity;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import team.cqr.cqrepoured.config.CQRConfig;
import team.cqr.cqrepoured.entity.ai.target.TargetUtil;
import team.cqr.cqrepoured.faction.Faction;
import team.cqr.cqrepoured.faction.FactionRegistry;
import team.cqr.cqrepoured.util.DungeonGenUtils;

public class EntityCalamityCrystal extends Entity {

	private EntityLiving owningEntity;
	private EntityLivingBase currentTarget;
	public int innerRotation;

	private int noTargetTicks = 0;
	private static final int MAX_NO_TARGET_TICKS = 100;

	private float absorbedHealth = 0F;

	private static final DataParameter<Optional<BlockPos>> BEAM_TARGET = EntityDataManager.<Optional<BlockPos>>createKey(EntityCalamityCrystal.class, DataSerializers.OPTIONAL_BLOCK_POS);
	private static final DataParameter<Boolean> ABSORBING = EntityDataManager.<Boolean>createKey(EntityCalamityCrystal.class, DataSerializers.BOOLEAN);

	private static final int EXPLOSION_EFFECT_RADIUS = 16;

	public EntityCalamityCrystal(World worldIn) {
		super(worldIn);
		this.preventEntitySpawning = true;
		this.setSize(2.0F, 2.0F);
		this.innerRotation = this.rand.nextInt(100_000);
	}

	public EntityCalamityCrystal(World world, EntityLiving owningEntity, double x, double y, double z) {
		this(world);
		this.owningEntity = owningEntity;
		this.setPosition(x, y, z);
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	protected void entityInit() {
		this.getDataManager().register(BEAM_TARGET, Optional.absent());
		this.getDataManager().register(ABSORBING, true);
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("BeamTarget", 10)) {
			this.setBeamTarget(NBTUtil.getPosFromTag(compound.getCompoundTag("BeamTarget")));
		}
		if (compound.hasKey("Absorbing", Constants.NBT.TAG_BYTE)) {
			this.setAbsorbing(compound.getBoolean("Absorbing"));
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		if (this.getBeamTarget() != null) {
			compound.setTag("BeamTarget", NBTUtil.createPosTag(this.getBeamTarget()));
		}
		compound.setBoolean("Absorbing", this.isAbsorbing());
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		++this.innerRotation;

		// Following code must not be run on the client
		if (this.world.isRemote) {
			return;
		}

		this.checkCurrentTarget();
		if (this.currentTarget != null && this.currentTarget.isEntityAlive()) {
			if (this.noTargetTicks != 0) {
				this.noTargetTicks = 0;
			}
			this.setBeamTarget(this.currentTarget.getPosition());
			// Only absorb health every 10 ticks, other wise it is too op
			if (this.ticksExisted % 10 == 0) {
				if (this.isAbsorbing()) {

					if (this.currentTarget.attackEntityFrom(DamageSource.MAGIC, 2F)) {
						this.absorbedHealth += 2F;
					}

					if (this.absorbedHealth >= CQRConfig.bosses.enderCalamityHealingCrystalAbsorbAmount * MathHelper.clamp(this.world.getDifficulty().getId() + 1, 1, EnumDifficulty.values().length -1 /* Ignore peaceful*/)) {
						this.setAbsorbing(false);
						this.currentTarget = this.owningEntity;
						if (this.owningEntity == null) {
							this.setBeamTarget(null);
						}
					}
				}
			}
			if (!this.isAbsorbing() && this.owningEntity != null) {
				this.owningEntity.heal(1F);
				this.absorbedHealth--;
				if (this.absorbedHealth <= 0F) {
					this.setDead();
					this.onCrystalDestroyed(DamageSource.OUT_OF_WORLD);
				}
			} else if (!this.isAbsorbing()) {
				this.currentTarget = null;
			}
		} else {
			this.noTargetTicks++;
			if (this.noTargetTicks >= MAX_NO_TARGET_TICKS) {
				if (this.isAbsorbing()) {
					this.setAbsorbing(false);
					this.currentTarget = this.owningEntity;
					if (this.currentTarget == null) {
						this.setBeamTarget(null);
					}
				} else {
					this.setDead();
					this.onCrystalDestroyed(DamageSource.OUT_OF_WORLD);
				}
			}
		}
	}

	private void checkCurrentTarget() {
		if (this.currentTarget != null) {
			if (this.currentTarget.isDead || !this.currentTarget.isEntityAlive() || (this.currentTarget.getHealth() / this.currentTarget.getMaxHealth() <= 0.25F)) {
				// Target is dead or remove or has too few hp=> search a different one!
				this.currentTarget = null;
				this.setBeamTarget(null);
			} else if (this.getDistance(this.currentTarget) >= 3 * EXPLOSION_EFFECT_RADIUS) {
				this.currentTarget = null;
				this.setBeamTarget(null);
			}
		}
		// Our old target was not good, we need a new one
		if (this.currentTarget == null) {
			// DONE: Create faction based predicate that checks for entities, also check their health
			Vec3d p1 = this.getPositionVector().add(2 * EXPLOSION_EFFECT_RADIUS, 2 * EXPLOSION_EFFECT_RADIUS, 2 * EXPLOSION_EFFECT_RADIUS);
			Vec3d p2 = this.getPositionVector().subtract(2 * EXPLOSION_EFFECT_RADIUS, 2 * EXPLOSION_EFFECT_RADIUS, 2 * EXPLOSION_EFFECT_RADIUS);
			AxisAlignedBB aabb = new AxisAlignedBB(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
			List<EntityLiving> affectedEntities = this.world.getEntitiesWithinAABB(EntityLiving.class, aabb, this::doesEntityFitForAbsorbing);
			if (!affectedEntities.isEmpty()) {
				this.currentTarget = affectedEntities.get(DungeonGenUtils.randomBetween(0, affectedEntities.size() - 1, this.rand));
			}
		}
	}

	@Override
	public void onKillCommand() {
		this.onCrystalDestroyed(DamageSource.GENERIC);
		super.onKillCommand();
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isEntityInvulnerable(source)) {
			return false;
		} else if (source.getTrueSource() == this.owningEntity) {
			return false;
		} else {
			if (!this.isDead && !this.world.isRemote) {
				this.setDead();

				if (!this.world.isRemote) {
					if (!source.isExplosion()) {
						this.world.createExplosion((Entity) null, this.posX, this.posY, this.posZ, 6.0F, true);
					}

					this.onCrystalDestroyed(source);
				}
			}

			return true;
		}
	}

	private void onCrystalDestroyed(DamageSource source) {

		// Special case when dying normally => it is not out of world
		if (source != DamageSource.OUT_OF_WORLD) {
			// DONE: Implement healing of all entities nearby

			Vec3d p1 = this.getPositionVector().add(EXPLOSION_EFFECT_RADIUS, EXPLOSION_EFFECT_RADIUS, EXPLOSION_EFFECT_RADIUS);
			Vec3d p2 = this.getPositionVector().subtract(EXPLOSION_EFFECT_RADIUS, EXPLOSION_EFFECT_RADIUS, EXPLOSION_EFFECT_RADIUS);
			AxisAlignedBB aabb = new AxisAlignedBB(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
			List<EntityLiving> affectedEntities = this.world.getEntitiesWithinAABB(EntityLiving.class, aabb);
			if (!affectedEntities.isEmpty()) {
				final float healingAmount = (this.absorbedHealth / affectedEntities.size());
				affectedEntities.forEach(arg0 -> arg0.heal(healingAmount));
			}
		}
		// TODO: Play some fancy effects and sounds
	}

	private void setBeamTarget(@Nullable BlockPos beamTarget) {
		this.getDataManager().set(BEAM_TARGET, Optional.fromNullable(beamTarget));
	}

	@Nullable
	public BlockPos getBeamTarget() {
		return this.getDataManager().get(BEAM_TARGET).orNull();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		return super.isInRangeToRenderDist(distance) || this.getBeamTarget() != null;
	}

	@Nullable
	private Faction getFaction() {
		if (this.world.isRemote) {
			return null;
		}
		if (this.owningEntity != null) {
			return FactionRegistry.instance(this).getFactionOf(this.owningEntity);
		}
		return null;
	}

	private boolean doesEntityFitForAbsorbing(EntityLiving living) {
		if (living != this.owningEntity) {
			if (TargetUtil.PREDICATE_LIVING.apply(living)) {
				/*
				 * CQRFaction myFaction = this.getFaction();
				 * if (myFaction != null) {
				 * if (myFaction.isAlly(living)) {
				 * return false;
				 * }
				 * }
				 */
				return living.getHealth() / living.getMaxHealth() >= 0.25F;
			}
		}
		return false;
	}

	public boolean isAbsorbing() {
		return this.dataManager.get(ABSORBING);
	}

	private void setAbsorbing(boolean value) {
		if (!this.world.isRemote) {
			this.dataManager.set(ABSORBING, value);
		}
	}

}
