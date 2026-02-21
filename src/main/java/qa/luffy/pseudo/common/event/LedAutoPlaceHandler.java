package qa.luffy.pseudo.common.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.block.LedBlock;
import qa.luffy.pseudo.common.block.PseudoBlocks;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@EventBusSubscriber(modid = Pseudo.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class LedAutoPlaceHandler {
    private LedAutoPlaceHandler() {}

    private static final String K_PX = "pseudo_led_px";
    private static final String K_PY = "pseudo_led_py";
    private static final String K_PZ = "pseudo_led_pz";

    // How far we “probe” for a touching surface (in blocks). Small = “instant stick” feel.
    private static final double PROBE_DIST = 0.22;

    // When moving upward, be a bit more aggressive so we catch undersides/ceilings.
    private static final double PROBE_UP_DIST = 0.38;

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity e = event.getEntity();
        if (!(e instanceof ItemEntity itemEntity)) return;

        Level level = itemEntity.level();
        if (level.isClientSide) return;

        ItemStack stack = itemEntity.getItem();
        if (stack.isEmpty() || stack.getCount() != 1) return;

        if (stack.getItem() != PseudoBlocks.LED.get().asItem()) return;

        CompoundTag tag = itemEntity.getPersistentData();

        // IMPORTANT: use the entity CENTER for collision/raycast accuracy (especially for ceilings)
        Vec3 now = center(itemEntity);

        if (!tag.contains(K_PX)) {
            tag.putDouble(K_PX, now.x);
            tag.putDouble(K_PY, now.y);
            tag.putDouble(K_PZ, now.z);
            return;
        }

        Vec3 prev = new Vec3(tag.getDouble(K_PX), tag.getDouble(K_PY), tag.getDouble(K_PZ));

        // update stored position early
        tag.putDouble(K_PX, now.x);
        tag.putDouble(K_PY, now.y);
        tag.putDouble(K_PZ, now.z);

        // 1) First try: raytrace actual movement (best “first contact” detector)
        BlockHitResult moveHit = clip(level, itemEntity, prev, now);
        if (tryPlaceFromHit(level, itemEntity, moveHit)) return;

        // 2) Sticky probes: check surfaces the item is currently touching/approaching.
        // Order probes by velocity axis magnitude so it sticks to the first surface it hits.
        Vec3 v = itemEntity.getDeltaMovement();
        for (Direction dir : probeOrder(v, itemEntity.onGround())) {
            double dist = PROBE_DIST;

            // If we're moving upward, be more aggressive about detecting ceilings / undersides.
            if (dir == Direction.UP && v.y > 0.0) {
                dist = PROBE_UP_DIST;
            }

            Vec3 end = now.add(dir.getStepX() * dist, dir.getStepY() * dist, dir.getStepZ() * dist);
            BlockHitResult probeHit = clip(level, itemEntity, now, end);
            if (tryPlaceFromHit(level, itemEntity, probeHit)) return;
        }

        // 3) Final fallback: if basically still and on ground, do a short downward trace
        // (kept from your old behavior, but now it’s last, not first)
        if (v.lengthSqr() < 1.0E-6 && itemEntity.onGround()) {
            BlockHitResult downHit = clip(level, itemEntity,
                    now.add(0.0, 0.05, 0.0),
                    now.add(0.0, -0.35, 0.0)
            );
            tryPlaceFromHit(level, itemEntity, downHit);
        }
    }

    private static Vec3 center(ItemEntity e) {
        Vec3 p = e.position();
        return p.add(0.0, e.getBbHeight() * 0.5, 0.0);
    }

    private static BlockHitResult clip(Level level, ItemEntity itemEntity, Vec3 start, Vec3 end) {
        return level.clip(new ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                itemEntity
        ));
    }

    private static boolean tryPlaceFromHit(Level level, ItemEntity itemEntity, BlockHitResult hit) {
        if (hit.getType() != HitResult.Type.BLOCK) return false;

        Direction face = hit.getDirection();   // outward face hit (same meaning as clickedFace)
        BlockPos anchor = hit.getBlockPos();
        BlockPos placePos = anchor.relative(face);

        BlockState existing = level.getBlockState(placePos);
        if (!existing.canBeReplaced()) return false;

        boolean water = level.getFluidState(placePos).getType() == Fluids.WATER;

        BlockState placeState = PseudoBlocks.LED.get().defaultBlockState()
                .setValue(LedBlock.FACING, face)
                .setValue(LedBlock.WATERLOGGED, water);

        if (!placeState.canSurvive(level, placePos)) return false;
        if (!level.setBlock(placePos, placeState, 3)) return false;

        SoundType snd = placeState.getSoundType(level, placePos, null);
        level.playSound(null, placePos,
                snd.getPlaceSound(),
                net.minecraft.sounds.SoundSource.BLOCKS,
                1.0f, 1.0f
        );

        itemEntity.setItem(ItemStack.EMPTY);
        itemEntity.discard();
        return true;
    }

    /**
     * Build an order of directions to probe based on velocity.
     * We probe the direction of movement first (most likely contact), then remaining directions,
     * with DOWN moved later unless we’re on ground.
     */
    private static List<Direction> probeOrder(Vec3 v, boolean onGround) {
        double ax = Math.abs(v.x);
        double ay = Math.abs(v.y);
        double az = Math.abs(v.z);

        Direction dx = v.x >= 0 ? Direction.EAST : Direction.WEST;
        Direction dy = v.y >= 0 ? Direction.UP : Direction.DOWN;
        Direction dz = v.z >= 0 ? Direction.SOUTH : Direction.NORTH;

        // Choose primary/secondary/tertiary by axis magnitude
        Direction first, second, third;
        if (ax >= ay && ax >= az) {
            first = dx;
            second = (ay >= az) ? dy : dz;
            third  = (ay >= az) ? dz : dy;
        } else if (ay >= ax && ay >= az) {
            first = dy;
            second = (ax >= az) ? dx : dz;
            third  = (ax >= az) ? dz : dx;
        } else {
            first = dz;
            second = (ax >= ay) ? dx : dy;
            third  = (ax >= ay) ? dy : dx;
        }

        // Fill remaining directions without duplicates
        EnumSet<Direction> used = EnumSet.noneOf(Direction.class);
        List<Direction> out = new ArrayList<>(6);

        add(out, used, first);
        add(out, used, second);
        add(out, used, third);

        // If we’re not on ground, don’t let DOWN dominate; we want side/bottom/ceiling contacts first.
        // If on ground, still don’t put DOWN first (movement already handled), but we can include it earlier.
        if (onGround) {
            add(out, used, Direction.DOWN);
        }

        // Add remaining directions
        for (Direction d : Direction.values()) add(out, used, d);

        // If not on ground, ensure DOWN is not near the front unless it’s the movement primary
        if (!onGround && out.size() >= 2 && out.getFirst() != Direction.DOWN) {
            out.remove(Direction.DOWN);
            out.add(Direction.DOWN);
        }

        return out;
    }

    private static void add(List<Direction> out, EnumSet<Direction> used, Direction d) {
        if (used.add(d)) out.add(d);
    }
}
