package cr0s.warpdrive.world;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.data.StarMapRegistry;
import cr0s.warpdrive.render.RenderBlank;
import cr0s.warpdrive.render.RenderSpaceSky;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SpaceWorldProvider extends WorldProvider {
	
	public SpaceWorldProvider() {
		biomeProvider = new BiomeProviderSingle(WarpDrive.spaceBiome);
		hasNoSky = false;
	}
	
	@Nonnull
	@Override
	public DimensionType getDimensionType() {
		return WarpDrive.dimensionTypeSpace;
	}
	
	@Override
	public boolean canRespawnHere() {
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public float getStarBrightness(float partialTicks) {
		return 0.9F;
	}
	
	@Override
	public boolean isSurfaceWorld() {
		return true;
	}
	
	@Override
	public int getAverageGroundLevel() {
		return 1;
	}
	
	@Override
	public double getHorizon() {
		return -256;
	}
	
	@Override
	public void updateWeather() {
		super.resetRainAndThunder();
	}
	
	@Nonnull
	@Override
	public Biome getBiomeForCoords(@Nonnull BlockPos blockPos) {
		return WarpDrive.spaceBiome;
	}
	
	@Override
	public void setAllowedSpawnTypes(boolean allowHostile, boolean allowPeaceful) {
		super.setAllowedSpawnTypes(true, true);
	}
	
	@Override
	public float calculateCelestialAngle(long time, float partialTick) {
		return 0.0F;
	}
	
	@Override
	protected void generateLightBrightnessTable() {
		float f = 0.0F;	// 0.1F
		
		for (int i = 0; i <= 15; ++i) {
			float f1 = 1.0F - i / 15.0F;
			lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public String getSaveFolder() {
		return (getDimensionType().getId() == 0 ? null : "WarpDriveSpace" + getDimensionType().getId());
	}
	
	/*
	@Override
	public boolean canCoordinateBeSpawn(int par1, int par2) {
		Block var3 = worldObj.getTopSolidOrLiquidBlock(par1, par2);
		return var3 != 0;
	}
	/**/
	
	@Nonnull
	@Override
	public Vec3d getSkyColor(@Nonnull Entity cameraEntity, float partialTicks) {
		if (getCloudRenderer() == null) {
			setCloudRenderer(RenderBlank.getInstance());
		}
		if (getSkyRenderer() == null) {
			setSkyRenderer(RenderSpaceSky.getInstance());
		}
		return new Vec3d(0.0D, 0.0D, 0.0D);
	}
	
	@Nonnull
	@Override
	public Vec3d getFogColor(float par1, float par2) {
		return new Vec3d(0.0D, 0.0D, 0.0D);
	}
	
	@Override
	public boolean isSkyColored() {
		return false;
	}
		
	@Override
	public int getRespawnDimension(EntityPlayerMP player) {
		if (player == null || player.worldObj == null) {
			WarpDrive.logger.error("Invalid player passed to getRespawnDimension: " + player);
			return 0;
		}
		return StarMapRegistry.getSpaceDimensionId(player.worldObj, (int) player.posX, (int) player.posZ);
	}
	
	@Nonnull
	@Override
	public IChunkGenerator createChunkGenerator() {
		return new SpaceChunkProvider(worldObj, 45);
	}
	
	@Override
	public boolean canBlockFreeze(@Nonnull BlockPos blockPos, boolean byWater) {
		return false;
	}
	
	/*
	@Override
	public BlockPos getRandomizedSpawnPoint() {
		BlockPos var5 = new BlockPos(worldObj.getSpawnPoint());
		
		//boolean isAdventure = worldObj.getWorldInfo().getGameType() == EnumGameType.ADVENTURE;
		int spawnFuzz = 1000;
		int spawnFuzzHalf = spawnFuzz / 2;
		
		{
			var5.posX += worldObj.rand.nextInt(spawnFuzz) - spawnFuzzHalf;
			var5.posZ += worldObj.rand.nextInt(spawnFuzz) - spawnFuzzHalf;
			var5.posY = 200;
		}
		
		if (worldObj.isAirBlock(var5.posX, var5.posY, var5.posZ)) {
			worldObj.setBlock(var5.posX, var5.posY, var5.posZ, Blocks.stone, 0, 2);
			
			worldObj.setBlock(var5.posX + 1, var5.posY + 1, var5.posZ, Blocks.glass, 0, 2);
			worldObj.setBlock(var5.posX + 1, var5.posY + 2, var5.posZ, Blocks.glass, 0, 2);
			
			worldObj.setBlock(var5.posX - 1, var5.posY + 1, var5.posZ, Blocks.glass, 0, 2);
			worldObj.setBlock(var5.posX - 1, var5.posY + 2, var5.posZ, Blocks.glass, 0, 2);
			
			worldObj.setBlock(var5.posX, var5.posY + 1, var5.posZ + 1, Blocks.glass, 0, 2);
			worldObj.setBlock(var5.posX, var5.posY + 2, var5.posZ + 1, Blocks.glass, 0, 2);
			
			worldObj.setBlock(var5.posX, var5.posY + 1, var5.posZ - 1, Blocks.glass, 0, 2);
			worldObj.setBlock(var5.posX, var5.posY + 3, var5.posZ - 1, Blocks.glass, 0, 2);
			
			// worldObj.setBlockWithNotify(var5.posX, var5.posY + 3, var5.posZ, Block.glass.blockID);
		}
		return var5;
	}
	/**/
	
	@Override
	public boolean isDaytime() {
		return true;
	}
	
	@Override
	public boolean canDoLightning(Chunk chunk) {
		return false;
	}
	
	@Override
	public boolean canDoRainSnowIce(Chunk chunk) {
		return false;
	}
}