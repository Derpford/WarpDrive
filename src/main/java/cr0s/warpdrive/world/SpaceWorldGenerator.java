package cr0s.warpdrive.world;

import cr0s.warpdrive.LocalProfiler;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.config.filler.Filler;
import cr0s.warpdrive.config.structures.AbstractStructure;
import cr0s.warpdrive.config.structures.Orb.OrbShell;
import cr0s.warpdrive.config.structures.OrbInstance;
import cr0s.warpdrive.config.structures.StructureManager;
import cr0s.warpdrive.data.CelestialObject;
import cr0s.warpdrive.data.StarMapRegistry;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import cpw.mods.fml.common.IWorldGenerator;

public class SpaceWorldGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		try {
			final int x = (chunkX * 16) + (5 - random.nextInt(10));
			final int z = (chunkZ * 16) + (5 - random.nextInt(10));
			CelestialObject celestialObject = StarMapRegistry.getCelestialObject(world, x, z);
			if (celestialObject == null) {
				// as observed on 1.7.10: during world transition, the generator from the previous world is still called
				return;
			}
			if ( celestialObject.borderRadiusX > 0
			  && ( Math.abs(x - celestialObject.dimensionCenterX) > celestialObject.borderRadiusX
			    || Math.abs(z - celestialObject.dimensionCenterZ) > celestialObject.borderRadiusZ ) ) {
				return;
			}
			int y = WarpDriveConfig.SPACE_GENERATOR_Y_MIN_CENTER
			      + random.nextInt(WarpDriveConfig.SPACE_GENERATOR_Y_MAX_CENTER - WarpDriveConfig.SPACE_GENERATOR_Y_MIN_CENTER);
			
			String group = celestialObject.getRandomGeneration(random);
			if (group == null) {
				return;
			}
			switch (group) {
			case "moon":
				AbstractStructure moon = StructureManager.getStructure(world.rand, StructureManager.GROUP_MOONS, null);
				moon.generate(world, world.rand, x, y, z);
				break;
				
			case "asteroid":
				AbstractStructure asteroid = StructureManager.getStructure(world.rand, StructureManager.GROUP_ASTEROIDS, null);
				asteroid.generate(world, world.rand, x, y, z);
				break;
				
			case "asteroidField":
				generateAsteroidField(world, x, y, z);
				break;
				
			default:
				break;
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private static void generateSmallShip(World world, int x, int y, int z, int jitter) {
		int x2 = x + (((world.rand.nextBoolean()) ? -1 : 1) * world.rand.nextInt(jitter));
		int y2 = y + (((world.rand.nextBoolean()) ? -1 : 1) * world.rand.nextInt(jitter));
		int z2 = z + (((world.rand.nextBoolean()) ? -1 : 1) * world.rand.nextInt(jitter));
		WarpDrive.logger.info("Generating small ship at " + x2 + "," + y2 + "," + z2);
		new WorldGenSmallShip(world.rand.nextFloat() > 0.2F).generate(world, world.rand, x2, y2, z2);
	}

	private static void generateStation(World world, int x, int y, int z, int jitter) {
		int x2 = x + (((world.rand.nextBoolean()) ? -1 : 1) * world.rand.nextInt(jitter));
		int y2 = y + (((world.rand.nextBoolean()) ? -1 : 1) * world.rand.nextInt(jitter));
		int z2 = z + (((world.rand.nextBoolean()) ? -1 : 1) * world.rand.nextInt(jitter));
		WarpDrive.logger.info("Generating small ship at " + x2 + "," + y2 + "," + z2);
		new WorldGenStation(world.rand.nextBoolean()).generate(world, world.rand, x2, y2, z2);
	}

	private static float binomialRandom(World world) {
		float linear = world.rand.nextFloat();
		// ideal sphere repartition = x ^ 0.5 (sqrt)
		// Dilution but slow to compute = 0.5 * ( x ^ 0.3 + 1 + (x - 1) ^ 3 )
		// Optimized 'pushed out' form = 1.25 - 0.625 / (0.5 + 2 * x)
		// Natural sphere with ring = (1 - x ^ 2.5) * x ^ 0.5 + x ^ 4

		// rectangular approach: return 0.5F * linear + 0.5F * linear * linear;
		return 1.25F - 0.625F / (0.5F + 2.0F * linear);
	}

	public static void generateAsteroidField(World world, int x, int y1, int z) {
		LocalProfiler.start("SpaceWorldGenerator.generateAsteroidField");
		// 6.0.1 au = 120 radius with 60 to 140 big + 60 to 140 small + 5 to 13 gaz
		// 45238 blocks surface with 120 to 280 asteroids => 161 to 376 blocks per asteroid (big & small)

		// 6.0.2 av big = 80 to 180 radius with 40 to 90 big + 80 to 200 small + 5 to 13 gaz
		// 20106 to 101787 surface with 120 to 290 asteroids => 69 to 848 blocks per asteroid

		// 6.0.2 av small = 30 to 80 radius with 2 to 22 big + 15 to 75 small + 0 to 3 gaz
		// 2827 to 20106 surface with 17 to 97 asteroids => 29 to 1182 blocks per asteroid

		// random distanced one = 89727 surface 256 asteroids => 350 blocks per asteroid

		/*
		boolean isBig = world.rand.nextInt(3) == 1;
		int numOfBigAsteroids, numOfSmallAsteroids, numOfClouds, maxDistance, maxHeight;
		if (isBig) {
			numOfBigAsteroids = 40 + world.rand.nextInt(50);
			numOfSmallAsteroids = 80 + world.rand.nextInt(120);
			numOfClouds = 5 + world.rand.nextInt(8);
			maxDistance = 80 + world.rand.nextInt(100);
			maxHeight = 40 + world.rand.nextInt(40);
		} else {
			numOfBigAsteroids = 2 + world.rand.nextInt(20);
			numOfSmallAsteroids = 15 + world.rand.nextInt(60);
			numOfClouds = 0 + world.rand.nextInt(3);
			maxDistance = 30 + world.rand.nextInt(50);
			maxHeight = 30 + world.rand.nextInt(30);
		}
		/**/

		float surfacePerAsteroid = 80.0F + world.rand.nextFloat() * 300;
		int maxDistance = 30 + world.rand.nextInt(170);
		int maxDistanceBig = Math.round(maxDistance * (0.6F + 0.2F * world.rand.nextFloat()));
		int maxDistanceSmall = Math.round(maxDistance * 1.1F);
		float bigRatio = 0.3F + world.rand.nextFloat() * 0.3F;
		float surfaceBig = (float) (Math.PI * Math.pow(maxDistanceBig, 2));
		float surfaceSmall = (float) (Math.PI * Math.pow(maxDistanceSmall, 2));
		int numOfBigAsteroids = Math.round(bigRatio * surfaceBig / surfacePerAsteroid);
		int numOfSmallAsteroids = Math.round((1.0F - bigRatio) * surfaceSmall / surfacePerAsteroid);
		int numOfClouds = Math.round(numOfBigAsteroids * 1.0F / (10.0F + world.rand.nextInt(10)));
		int maxHeight = 70 + world.rand.nextInt(50);
		int y2 = Math.min(WarpDriveConfig.SPACE_GENERATOR_Y_MAX_BORDER - maxHeight,
			 Math.max(y1, WarpDriveConfig.SPACE_GENERATOR_Y_MIN_BORDER + maxHeight));
		WarpDrive.logger.info("Generating asteroid field at " + x + "," + y2 + "," + z + " qty " + numOfBigAsteroids + ", " + numOfSmallAsteroids + ", "
				+ numOfClouds + " over " + maxDistance + ", " + maxHeight + " surfacePerAsteroid " + String.format("%.1f", surfacePerAsteroid));

		// Setting up of big asteroids
		for (int i = 1; i <= numOfBigAsteroids; i++) {
			float binomial = binomialRandom(world);
			double bearing = world.rand.nextFloat() * 2.0D * Math.PI;
			double yawn = world.rand.nextFloat() * Math.PI;
			float horizontalRange = Math.max(6.0F, binomial * maxDistanceBig);
			float verticalRange = Math.max(3.0F, binomial * maxHeight);

			int aX = (int) (x + Math.round(horizontalRange * Math.cos(bearing)));
			int aY = (int) (y2 + Math.round(verticalRange * Math.cos(yawn)));
			int aZ = (int) (z + Math.round(horizontalRange * Math.sin(bearing)));

			if (WarpDriveConfig.LOGGING_WORLDGEN) {
				WarpDrive.logger.info(String.format("Big asteroid: %.3f %.3f r %.3f r makes %3d, %3d, %3d",
					(double) binomial, bearing, yawn, aX, aY, aZ));
			}

			// Place an asteroid
			AbstractStructure moon = StructureManager.getStructure(world.rand, StructureManager.GROUP_ASTEROIDS, null);
			moon.generate(world, world.rand, aX, aY, aZ);
		}

		// Setting up small asteroids
		for (int i = 1; i <= numOfSmallAsteroids; i++) {
			float binomial = binomialRandom(world);
			double bearing = world.rand.nextFloat() * 2.0D * Math.PI;
			double yawn = world.rand.nextFloat() * Math.PI;
			float horizontalRange = Math.max(6.0F, binomial * maxDistanceSmall);
			float verticalRange = Math.max(3.0F, binomial * maxHeight);

			int aX = (int) (x + Math.round(horizontalRange * Math.cos(bearing)));
			int aY = (int) (y2 + Math.round(verticalRange * Math.cos(yawn)));
			int aZ = (int) (z + Math.round(horizontalRange * Math.sin(bearing)));

			// Placing
			if (world.rand.nextInt(400) != 1) {
				AbstractStructure moon = StructureManager.getStructure(world.rand, StructureManager.GROUP_ASTEROIDS, null);
				moon.generate(world, world.rand, aX, aY, aZ);
			} else {
				if (world.rand.nextInt(20) != 1) {
					generateSmallShip(world, aX, aY, aZ, 8);
				} else {
					generateStation(world, aX, aY, aZ, 8);
				}
			}
		}

		// Setting up gas clouds
		for (int i = 1; i <= numOfClouds; i++) {
			float binomial = binomialRandom(world);
			double bearing = world.rand.nextFloat() * 2.0D * Math.PI;
			double yawn = world.rand.nextFloat() * Math.PI;
			float horizontalRange = Math.max(6.0F, binomial * maxDistanceBig);
			float verticalRange = Math.max(3.0F, binomial * maxHeight);

			int aX = (int) (x + Math.round(horizontalRange * Math.cos(bearing)));
			int aY = (int) (y2 + Math.round(verticalRange * Math.cos(yawn)));
			int aZ = (int) (z + Math.round(horizontalRange * Math.sin(bearing)));

			// Placing
			if (world.rand.nextBoolean()) {
				AbstractStructure gasCloud = StructureManager.getStructure(world.rand, StructureManager.GROUP_GASCLOUDS, null);
				if (gasCloud != null) {
					gasCloud.generate(world, world.rand, aX, aY, aZ);
				}
			}
		}

		LocalProfiler.stop();
	}
	
	/**
	 * 
	 * @deprecated reference design for EntitySphereGenerator
	 **/
	@Deprecated
	public static void generateSphereDirect(
	                                       OrbInstance orbInstance, World world, int xCoord, int yCoord, int zCoord) {
		double radiusC = orbInstance.getTotalThickness() + 0.5D; // Radius from center of block
		double radiusSq = radiusC * radiusC; // Optimization to avoid square roots...
		// sphere
		int ceilRadius = (int) Math.ceil(radiusC);
		
		// Pass the cube and check points for sphere equation x^2 + y^2 + z^2 = r^2
		for (int x = 0; x <= ceilRadius; x++) {
			double dX2 = (x + 0.5D) * (x + 0.5D);
			for (int y = 0; y <= ceilRadius; y++) {
				double dX2Y2 = dX2 + (y + 0.5D) * (y + 0.5D);
				for (int z = 0; z <= ceilRadius; z++) {
					double dZ2 = (z + 0.5D) * (z + 0.5D);
					double dSq = dX2Y2 + dZ2; // squared distance from current position
					
					// Skip too far blocks
					if (dSq > radiusSq) {
						continue;
					}
					
					// Place blocks
					// cheat by using axial symmetry so we don't create random numbers too frequently
					
					OrbShell orbShell = orbInstance.getShellForSqRadius(dSq);
					Filler filler = orbShell.getRandomBlock(world.rand);
					filler.setBlock(world, xCoord + x, yCoord + y, zCoord + z);
					filler.setBlock(world, xCoord - x, yCoord + y, zCoord + z);
					filler.setBlock(world, xCoord + x, yCoord - y, zCoord + z);
					filler.setBlock(world, xCoord + x, yCoord + y, zCoord - z);
					filler.setBlock(world, xCoord - x, yCoord - y, zCoord + z);
					filler.setBlock(world, xCoord + x, yCoord - y, zCoord - z);
					filler.setBlock(world, xCoord - x, yCoord + y, zCoord - z);
					filler.setBlock(world, xCoord - x, yCoord - y, zCoord - z);
				}
			}
		}
	}
}
