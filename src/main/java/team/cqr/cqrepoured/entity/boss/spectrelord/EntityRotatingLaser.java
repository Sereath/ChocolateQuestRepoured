package team.cqr.cqrepoured.entity.boss.spectrelord;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import team.cqr.cqrepoured.entity.boss.AbstractEntityLaser;

public class EntityRotatingLaser extends AbstractEntityLaser {

	private float deltaRotationYawPerTick;
	private float deltaRotationPitchPerTick;

	public EntityRotatingLaser(World worldIn) {
		this(worldIn, null, 4.0F, 1.0F, 0.0F);
	}

	public EntityRotatingLaser(World worldIn, EntityLivingBase caster, float length, float deltaRotationYawPerTick, float deltaRotationPitchPerTick) {
		super(worldIn, caster, length);
		this.deltaRotationYawPerTick = deltaRotationYawPerTick;
		this.deltaRotationPitchPerTick = deltaRotationPitchPerTick;
	}

	@Override
	public void setupPositionAndRotation() {
		// TODO reduce unnecessary vec3d creation
		Vec3d vec1 = new Vec3d(this.caster.posX, this.caster.posY, this.caster.posZ);
		vec1 = vec1.add(this.getOffsetVector());
		this.setPosition(vec1.x, vec1.y, vec1.z);
	}

	@Override
	public void updatePositionAndRotation() {
		this.rotationYawCQR = MathHelper.wrapDegrees(this.rotationYawCQR + this.deltaRotationYawPerTick);
		this.rotationPitchCQR = MathHelper.wrapDegrees(this.rotationPitchCQR + this.deltaRotationPitchPerTick);
		// TODO reduce unnecessary vec3d creation
		Vec3d vec1 = new Vec3d(this.caster.posX, this.caster.posY, this.caster.posZ);
		vec1 = vec1.add(this.getOffsetVector());
		this.setPosition(vec1.x, vec1.y, vec1.z);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		super.writeSpawnData(buffer);
		buffer.writeFloat(this.deltaRotationYawPerTick);
		buffer.writeFloat(this.deltaRotationPitchPerTick);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		super.readSpawnData(additionalData);
		this.deltaRotationYawPerTick = additionalData.readFloat();
		this.deltaRotationPitchPerTick = additionalData.readFloat();
	}

}
