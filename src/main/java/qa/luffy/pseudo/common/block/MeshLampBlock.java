package qa.luffy.pseudo.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;

public class MeshLampBlock extends Block {

    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");

    // Player-head-sized shape: (4,0,4) -> (12,8,12)
    private static final VoxelShape SHAPE = Block.box(
            4.0D, 0.0D, 4.0D,
            12.0D, 8.0D, 12.0D
    );

    // Whether this *instance* (normal or inverted) should spawn ON by default
    private final boolean defaultOn;

    public MeshLampBlock(Properties properties, boolean defaultOn) {
        // noOcclusion so it doesn't behave like a full cube visually
        super(properties.noOcclusion());
        this.defaultOn = defaultOn;

        // Default blockstate when placed:
        // - mesh_lamp: ACTIVATED = false
        // - mesh_lamp_inverted: ACTIVATED = true
        this.registerDefaultState(
                this.defaultBlockState().setValue(ACTIVATED, defaultOn)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }

    // Right-click without item: toggle ACTIVATED for both variants
    @Override
    protected InteractionResult useWithoutItem(BlockState state,
                                               Level level,
                                               BlockPos pos,
                                               Player player,
                                               BlockHitResult hitResult) {
        if (!level.isClientSide) {
            boolean current = state.getValue(ACTIVATED);
            level.setBlock(pos, state.setValue(ACTIVATED, !current), Block.UPDATE_ALL);
        }
        return InteractionResult.SUCCESS;
    }

    // Selection box / outline
    @Override
    public VoxelShape getShape(BlockState state,
                               BlockGetter level,
                               BlockPos pos,
                               CollisionContext context) {
        return SHAPE;
    }

    // Collision box
    @Override
    public VoxelShape getCollisionShape(BlockState state,
                                        BlockGetter level,
                                        BlockPos pos,
                                        CollisionContext context) {
        return SHAPE;
    }

    // Occlusion (lighting/face culling) – empty so it doesn't act as a full cube
    @Override
    public VoxelShape getOcclusionShape(BlockState state,
                                        BlockGetter level,
                                        BlockPos pos) {
        return Shapes.empty();
    }
}
