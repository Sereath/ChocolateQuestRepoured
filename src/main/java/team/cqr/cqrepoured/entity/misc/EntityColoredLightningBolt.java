package team.cqr.cqrepoured.entity.misc;

import java.util.List;

import net.minecraft.world.level.block.AbstractFireBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import team.cqr.cqrepoured.config.CQRConfig;
import team.cqr.cqrepoured.init.CQREntityTypes;
import team.cqr.cqrepoured.util.EntityUtil;

public class EntityColoredLightningBolt extends LightningBoltEntity implements IEntityAdditionalSpawnData {

	/** Declares which state the lightning bolt is in. Whether it's in the air, hit the ground, etc. */
	private int lightningState;
	/** Determines the time before the EntityLightningBolt is destroyed. It is a random integer decremented over time. */
	private int boltLivingTime;
	protected boolean hitEntities;
	protected boolean spreadFire;
	public float red;
	public float green;
	public float blue;
	public float alpha;

	public EntityColoredLightningBolt(Level worldIn) {
		this(CQREntityTypes.COLORED_LIGHTNING.get(), worldIn, 0.0D, 0.0D, 0.0D, false, false);
	}

	public EntityColoredLightningBolt(Level worldIn, double x, double y, double z, boolean hitEntities, boolean spreadFire) {
		this(CQREntityTypes.COLORED_LIGHTNING.get(), worldIn, x, y, z, hitEntities, spreadFire, 0.45F, 0.45F, 0.5F, 0.3F);
	}
	
	public EntityColoredLightningBolt(EntityType<? extends EntityColoredLightningBolt> type, Level worldIn) {
		this(worldIn, 0.0D, 0.0D, 0.0D, false, false);
	}

	public EntityColoredLightningBolt(EntityType<? extends EntityColoredLightningBolt> type, Level worldIn, double x, double y, double z, boolean hitEntities, boolean spreadFire) {
		this(worldIn, x, y, z, hitEntities, spreadFire, 0.45F, 0.45F, 0.5F, 0.3F);
	}

	/**
	 * Vanilla color is: 0.45F, 0.45F, 0.5F, 0.3F
	 */
	public EntityColoredLightningBolt(Level worldIn, double x, double y, double z, boolean hitEntities, boolean spreadFire, float red, float green, float blue, float alpha) {
		this(CQREntityTypes.COLORED_LIGHTNING.get(), worldIn, x, y, z, hitEntities, spreadFire, red, green, blue, alpha);
	}
	
	public EntityColoredLightningBolt(EntityType<? extends EntityColoredLightningBolt> type, Level worldIn, double x, double y, double z, boolean hitEntities, boolean spreadFire, float red, float green, float blue, float alpha) {
		super(type, worldIn/*, x, y, z, true*/);
		this.setPos(x, y, z);
		//this.isImmuneToFire = true;
		this.noCulling = true;
		this.lightningState = 2;
		this.boltLivingTime = this.random.nextInt(3) + 1;
		this.hitEntities = hitEntities;
		this.spreadFire = spreadFire;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		BlockPos blockpos = this.blockPosition();

		if (spreadFire && !worldIn.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK) && (worldIn.getDifficulty() == Difficulty.NORMAL || worldIn.getDifficulty() == Difficulty.HARD) && worldIn.isAreaLoaded(blockpos, 10)) {
			 BlockState fireBlockState = AbstractFireBlock.getState(this.level, blockpos);
			if (worldIn.getBlockState(blockpos).getMaterial() == Material.AIR && fireBlockState.canSurvive(worldIn, blockpos)) {
				worldIn.setBlockAndUpdate(blockpos, Blocks.FIRE.defaultBlockState());
			}

			for (int i = 0; i < 4; i++) {
				BlockPos blockpos1 = blockpos.offset(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);

				if (worldIn.getBlockState(blockpos1).getMaterial() == Material.AIR && fireBlockState.canSurvive(worldIn, blockpos1)) {
					worldIn.setBlockAndUpdate(blockpos1, Blocks.FIRE.defaultBlockState());
				}
			}
		}
	}

	/*@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}*/

	@Override
	public void tick() {
		if (!this.level.isClientSide) {
			this.setSharedFlag(6, this.isGlowing());
		}

		this.baseTick();

		if (this.lightningState == 2) {
			this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F);
	         this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F);
		}

		--this.lightningState;

		if (this.lightningState < 0) {
			if (this.boltLivingTime == 0) {
				this.remove();
			} else if (this.lightningState < -this.random.nextInt(10)) {
				--this.boltLivingTime;
				this.lightningState = 1;

				if (this.spreadFire && !this.level.isClientSide) {
					this.seed = this.random.nextLong();
					BlockPos blockpos = this.blockPosition();

					if (this.level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK) && this.level.isAreaLoaded(blockpos, 10) && this.level.getBlockState(blockpos).getMaterial() == Material.AIR && Blocks.FIRE.defaultBlockState().canSurvive(this.level, blockpos)) {
						this.level.setBlockAndUpdate(blockpos, Blocks.FIRE.defaultBlockState());
					}
				}
			}
		}

		if (this.lightningState >= 0) {
			if (this.level.isClientSide) {
				this.level.setSkyFlashTime(2);
			} else if (this.hitEntities) {
				AABB aabb = new AABB(this.getX() - 3.0D, this.getY() - 3.0D, this.getZ() - 3.0D, this.getX() + 3.0D, this.getY() + 6.0D + 3.0D, this.getZ() + 3.0D);
				List<Entity> list = this.level.getEntities(this, aabb);

				for (Entity entity : list) {
					if (!ForgeEventFactory.onEntityStruckByLightning(entity, this)) {
						if (CQRConfig.SERVER_CONFIG.advanced.flyingCowardPenaltyEnabled.get() && (EntityUtil.isEntityFlying(entity) || EntityUtil.isEntityFlying(entity.getControllingPassenger()))) {
							entity.hurt(DamageSource.MAGIC, (float)(double)(CQRConfig.SERVER_CONFIG.advanced.flyingCowardPenaltyDamage.get()));
						}
						entity.thunderHit((ServerLevel)this.level, this);
					}
				}
			}
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putFloat("red", this.red);
		compound.putFloat("green", this.green);
		compound.putFloat("blue", this.blue);
		compound.putFloat("alpha", this.alpha);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.red = compound.getFloat("red");
		this.green = compound.getFloat("green");
		this.blue = compound.getFloat("blue");
		this.alpha = compound.getFloat("alpha");
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		buffer.writeFloat(this.red);
		buffer.writeFloat(this.green);
		buffer.writeFloat(this.blue);
		buffer.writeFloat(this.alpha);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf additionalData) {
		this.red = additionalData.readFloat();
		this.green = additionalData.readFloat();
		this.blue = additionalData.readFloat();
		this.alpha = additionalData.readFloat();
	}
	
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
