package cr0s.warpdrive.block.breathing;

import cr0s.warpdrive.data.StateAir;
import cr0s.warpdrive.event.ChunkHandler;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockAirFlow extends BlockAbstractAir {
	
	public BlockAirFlow() {
		super();
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		if (!world.isRemote) {
			final StateAir stateAir = ChunkHandler.getStateAir(world, x, y, z);
			if (!stateAir.isAirFlow() || stateAir.concentration == 0) {
				world.setBlockToAir(x, y, z);
			}
		}
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}
}