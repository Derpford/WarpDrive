package cr0s.warpdrive;

import cr0s.warpdrive.config.Dictionary;
import cr0s.warpdrive.data.StarMapRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class GravityManager {
	private static final double OVERWORLD_ENTITY_GRAVITY = 0.080000000000000002D;	// Default value from Vanilla
	private static final double OVERWORLD_ITEM_GRAVITY = 0.039999999105930328D;	// Default value from Vanilla
	private static final double OVERWORLD_ITEM_GRAVITY2 = 0.9800000190734863D;	// Default value from Vanilla
	private static final double HYPERSPACE_FIELD_ENTITY_GRAVITY = 0.035D;
	private static final double HYPERSPACE_VOID_ENTITY_JITTER = 0.005D;
	private static final double SPACE_FIELD_ENTITY_GRAVITY = 0.025D;
	private static final double SPACE_FIELD_ITEM_GRAVITY = 0.02D;
	private static final double SPACE_FIELD_ITEM_GRAVITY2 = 0.60D;
	private static final double SPACE_VOID_GRAVITY = 0.001D;
	private static final double SPACE_VOID_GRAVITY_JETPACK_SNEAK = 0.02D;
	private static final double SPACE_VOID_GRAVITY_RAW_SNEAK = 0.005D; // 0.001 = no mvt
	
	public static double getGravityForEntity(Entity entity) {
		
		final double gravity = StarMapRegistry.getGravity(entity);
		if (gravity < 1.0D) {
			// Is entity in hyper-space?
			boolean inHyperspace = gravity > 0.4D;
			
			if (isEntityInGraviField(entity)) {
				if (inHyperspace) {
					return HYPERSPACE_FIELD_ENTITY_GRAVITY;
				} else {
					return SPACE_FIELD_ENTITY_GRAVITY;
				}
			} else {
				double jitter = inHyperspace ? (entity.worldObj.rand.nextDouble() - 0.5D) * 2.0D * HYPERSPACE_VOID_ENTITY_JITTER : 0.0D;
				if (entity instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) entity;
					
					if (player.isSneaking()) {
						for (ItemStack armor : player.getArmorInventoryList()) {
							if (armor != null) {
								if (Dictionary.ITEMS_FLYINSPACE.contains(armor.getItem())) {
									return SPACE_VOID_GRAVITY_JETPACK_SNEAK;
								}
							}
						}
						return SPACE_VOID_GRAVITY_RAW_SNEAK;
					} else {
						// FIXME: compensate jetpack
					}
				}
				
				return SPACE_VOID_GRAVITY + jitter;
			}
		}
		
		return OVERWORLD_ENTITY_GRAVITY;
	}
	
	public static double getItemGravity(EntityItem entity) {
		final double gravity = StarMapRegistry.getGravity(entity);
		if (gravity < 1.0D) {
			if (isEntityInGraviField(entity)) {
				return SPACE_FIELD_ITEM_GRAVITY;
			} else {
				return SPACE_VOID_GRAVITY;
			}
		} else {
			return OVERWORLD_ITEM_GRAVITY; // On Earth
		}
	}
	
	public static double getItemGravity2(EntityItem entity) {
		final double gravity = StarMapRegistry.getGravity(entity);
		if (gravity < 1.0D) {
			if (isEntityInGraviField(entity)) {
				return SPACE_FIELD_ITEM_GRAVITY2;
			} else {
				return SPACE_VOID_GRAVITY;
			}
		} else {
			return OVERWORLD_ITEM_GRAVITY2;
		}
	}
	
	public static boolean isEntityInGraviField(Entity entity) {
		int y = MathHelper.floor_double(entity.posY);
		int x = MathHelper.floor_double(entity.posX);
		int z = MathHelper.floor_double(entity.posZ);
		final int CHECK_DISTANCE = 20;
		
		// Search non-air blocks under player
		for (int ny = y; ny > (y - CHECK_DISTANCE); ny--) {
			if (!entity.worldObj.isAirBlock(new BlockPos(x, ny, z))) {
				return true;
			}
		}
		
		return false;
	}
}
