package team.cqr.cqrepoured.item;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTeleportStone extends Item {

	private static final String X = "x";
	private static final String Y = "y";
	private static final String Z = "z";
	private static final String DIMENSION = "dimension";

	public ItemTeleportStone() {
		this.setMaxDamage(100);

		this.setMaxStackSize(1);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

		if (isSelected && entityIn instanceof EntityPlayer && worldIn.isRemote && worldIn.getTotalWorldTime() % 4 == 0) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag != null && tag.hasKey(X) && tag.hasKey(Y) && tag.hasKey(Z) && tag.hasKey(DIMENSION) && worldIn.provider.getDimension() == tag.getInteger(DIMENSION)) {
				double x = MathHelper.floor(tag.getDouble(X)) + MathHelper.clamp(worldIn.rand.nextGaussian() * 0.3D, -0.5D, 0.5D);
				double y = MathHelper.floor(tag.getDouble(Y)) + MathHelper.clamp(worldIn.rand.nextGaussian() * 0.1D, -0.1D, 0.1D);
				double z = MathHelper.floor(tag.getDouble(Z)) + MathHelper.clamp(worldIn.rand.nextGaussian() * 0.3D, -0.5D, 0.5D);
				worldIn.spawnParticle(EnumParticleTypes.DRAGON_BREATH, x + 0.5D, y + 0.1D, z + 0.5D, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 40;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		playerIn.setActiveHand(handIn);
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	/**
	 * Taken from CoFHCore's EntityHelper
	 * (https://github.com/CoFH/CoFHCore/blob/1.12/src/main/java/cofh/core/util/helpers/EntityHelper.java)
	 */
	private static void transferPlayerToDimension(EntityPlayerMP player, int dimension, PlayerList manager) {
		int oldDim = player.dimension;
		WorldServer oldWorld = manager.getServerInstance().getWorld(player.dimension);
		player.dimension = dimension;
		WorldServer newWorld = manager.getServerInstance().getWorld(player.dimension);
		player.connection.sendPacket(new SPacketRespawn(player.dimension, newWorld.getDifficulty(), newWorld.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
		oldWorld.removeEntityDangerously(player);
		if (player.isBeingRidden()) {
			player.removePassengers();
		}
		if (player.isRiding()) {
			player.dismountRidingEntity();
		}
		player.isDead = false;
		transferEntityToWorld(player, oldWorld, newWorld);
		manager.preparePlayer(player, oldWorld);
		player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
		player.interactionManager.setWorld(newWorld);
		manager.updateTimeAndWeatherForPlayer(player, newWorld);
		manager.syncPlayerInventory(player);

		for (PotionEffect potioneffect : player.getActivePotionEffects()) {
			player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
		}
		FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldDim, dimension);
	}

	/**
	 * Taken from CoFHCore's EntityHelper
	 * (https://github.com/CoFH/CoFHCore/blob/1.12/src/main/java/cofh/core/util/helpers/EntityHelper.java)
	 */
	private static void transferEntityToWorld(Entity entity, WorldServer oldWorld, WorldServer newWorld) {
		WorldProvider oldWorldProvider = oldWorld.provider;
		WorldProvider newWorldProvider = newWorld.provider;
		double moveFactor = oldWorldProvider.getMovementFactor() / newWorldProvider.getMovementFactor();
		double x = entity.posX * moveFactor;
		double z = entity.posZ * moveFactor;

		oldWorld.profiler.startSection("placing");
		x = MathHelper.clamp(x, -29_999_872, 29_999_872);
		z = MathHelper.clamp(z, -29_999_872, 29_999_872);
		if (entity.isEntityAlive()) {
			entity.setLocationAndAngles(x, entity.posY, z, entity.rotationYaw, entity.rotationPitch);
			newWorld.spawnEntity(entity);
			newWorld.updateEntityWithOptionalForce(entity, false);
		}
		oldWorld.profiler.endSection();

		entity.setWorld(newWorld);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		if (entityLiving instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entityLiving;
			player.getCooldownTracker().setCooldown(stack.getItem(), 60);

			if (player.isSneaking() && stack.hasTagCompound()) {
				stack.getTagCompound().removeTag(X);
				stack.getTagCompound().removeTag(Y);
				stack.getTagCompound().removeTag(Z);
				worldIn.playSound(player.posX, player.posY, player.posZ, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.AMBIENT, 1.0F, 1.0F, false);
				for (int i = 0; i < 10; i++) {
					worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, player.posX + worldIn.rand.nextDouble() - 0.5D, player.posY + 0.5D, player.posZ + worldIn.rand.nextDouble() - 0.5D, 0D, 0D, 0D);
				}
			}

			else if (this.getPoint(stack) == null || !stack.hasTagCompound()) {
				this.setPoint(stack, player);
				for (int i = 0; i < 10; i++) {
					worldIn.spawnParticle(EnumParticleTypes.FLAME, player.posX + worldIn.rand.nextDouble() - 0.5D, player.posY + 0.5D, player.posZ + worldIn.rand.nextDouble() - 0.5D, 0D, 0D, 0D);
				}
				worldIn.playSound(player.posX, player.posY, player.posZ, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.AMBIENT, 1.0F, 1.0F, false);

				return super.onItemUseFinish(stack, worldIn, entityLiving);
			}

			else if (stack.hasTagCompound() && !player.isSneaking()) {
				if (stack.getTagCompound().hasKey(X) && stack.getTagCompound().hasKey(Y) && stack.getTagCompound().hasKey(Z)) {
					int dimension = stack.getTagCompound().hasKey(DIMENSION, Constants.NBT.TAG_INT) ? stack.getTagCompound().getInteger(DIMENSION) : 0;
					BlockPos pos = this.getPoint(stack);

					if (player.isBeingRidden()) {
						player.removePassengers();
					}
					if (player.isRiding()) {
						player.dismountRidingEntity();
					}

					if (dimension != player.getEntityWorld().provider.getDimension()) {
						MinecraftServer server = player.world.getMinecraftServer();
						if (server != null) {
							transferPlayerToDimension(player, dimension, server.getPlayerList());
						}
					}
					player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
					// player.attemptTeleport(stack.getTagCompound().getDouble(this.X), stack.getTagCompound().getDouble(this.Y),
					// stack.getTagCompound().getDouble(this.Z));
					/*
					 * if(worldIn.provider.getDimension() != dimension) {
					 * 
					 * WorldServer worldServer = player.getServer().getWorld(dimension);
					 * WorldServer worldServerOld = player.getServerWorld();
					 * player.moveToBlockPosAndAngles(new BlockPos(stack.getTagCompound().getDouble(this.X),
					 * stack.getTagCompound().getDouble(this.Y),
					 * stack.getTagCompound().getDouble(this.Z)), player.rotationYaw, player.rotationPitch);
					 * worldServerOld.removeEntity(player);
					 * boolean flag = player.forceSpawn;
					 * player.forceSpawn = true;
					 * worldServer.spawnEntity(player);
					 * player.forceSpawn = flag;
					 * worldServer.updateEntityWithOptionalForce(player, false);
					 * 
					 * }
					 */
					// player.connection.setPlayerLocation(stack.getTagCompound().getDouble(this.X),
					// stack.getTagCompound().getDouble(this.Y),
					// stack.getTagCompound().getDouble(this.Z), player.rotationYaw, player.rotationPitch);
					for (int i = 0; i < 30; i++) {
						worldIn.spawnParticle(EnumParticleTypes.PORTAL, player.posX + worldIn.rand.nextDouble() - 0.5D, player.posY + 0.5D, player.posZ + worldIn.rand.nextDouble() - 0.5D, 0D, 0D, 0D);
					}
					worldIn.playSound(player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.AMBIENT, 1.0F, 1.0F, false);

					if (!player.capabilities.isCreativeMode) {
						stack.damageItem(1, entityLiving);
					}

					return super.onItemUseFinish(stack, worldIn, entityLiving);
				}
			}

		}
		return super.onItemUseFinish(stack, worldIn, entityLiving);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			tooltip.add(TextFormatting.BLUE + I18n.format("description.teleport_stone.name"));

			if (stack.hasTagCompound()) {
				if (stack.getTagCompound().hasKey(X) && stack.getTagCompound().hasKey(Y) && stack.getTagCompound().hasKey(Z)) {
					tooltip.add(TextFormatting.BLUE + I18n.format("description.teleport_stone_position.name"));
					tooltip.add(TextFormatting.BLUE + I18n.format("X: " + (int) stack.getTagCompound().getDouble(X)));
					tooltip.add(TextFormatting.BLUE + I18n.format("Y: " + (int) stack.getTagCompound().getDouble(Y)));
					tooltip.add(TextFormatting.BLUE + I18n.format("Z: " + (int) stack.getTagCompound().getDouble(Z)));
					tooltip.add(TextFormatting.BLUE + I18n.format("Dimension: " + (stack.getTagCompound().hasKey(DIMENSION, Constants.NBT.TAG_INT) ? stack.getTagCompound().getInteger(DIMENSION) : 0)));
				}
			}
		} else {
			tooltip.add(TextFormatting.BLUE + I18n.format("description.click_shift.name"));
		}
	}

	private void setPoint(ItemStack stack, EntityPlayerMP player) {
		NBTTagCompound stone = stack.getTagCompound();

		if (stone == null) {
			stone = new NBTTagCompound();
			stack.setTagCompound(stone);
		}

		if (!stone.hasKey(X)) {
			stone.setDouble(X, player.posX);
		}

		if (!stone.hasKey(Y)) {
			stone.setDouble(Y, player.posY);
		}

		if (!stone.hasKey(Z)) {
			stone.setDouble(Z, player.posZ);
		}

		// Don't re-enable this check, if it is enabled it will never trigger correctly for whatever reason
		// if (!stone.hasKey(this.Dimension)) {
		stone.setInteger(DIMENSION, player.dimension);
		// }
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return this.getPoint(stack) != null;
	}

	@Nullable
	private BlockPos getPoint(ItemStack stack) {
		if (stack.hasTagCompound()) {
			if (stack.getTagCompound().hasKey(X) && stack.getTagCompound().hasKey(Y) && stack.getTagCompound().hasKey(Z)) {
				NBTTagCompound stone = stack.getTagCompound();

				double x = stone.getDouble(X);
				double y = stone.getDouble(Y);
				double z = stone.getDouble(Z);

				return new BlockPos(x, y, z);
			}
		}
		return null;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return false;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

}
