package qa.luffy.pseudo.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.data.PseudoDataComponents;
import qa.luffy.pseudo.common.data.clipboard.ClipboardContent;

@SuppressWarnings("deprecation")
public class ClipboardBlockEntity extends BlockEntity {
    private static final String CONTENT_KEY = "clipboard_content";

    private ClipboardContent content = ClipboardContent.DEFAULT;

    public ClipboardBlockEntity(BlockPos pos, BlockState state) {
        super(PseudoBlockEntities.CLIPBOARD_TYPE.get(), pos, state);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(CONTENT_KEY)) {
            setContent(decode(tag.get(CONTENT_KEY)));
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put(CONTENT_KEY, encode(getContent()));
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.@NotNull DataComponentInput input) {
        super.applyImplicitComponents(input);
        setContent(input.getOrDefault(PseudoDataComponents.CLIPBOARD_CONTENT.get(), ClipboardContent.DEFAULT));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.@NotNull Builder components) {
        super.collectImplicitComponents(components);
        if (!content.equals(ClipboardContent.DEFAULT)) {
            components.set(PseudoDataComponents.CLIPBOARD_CONTENT.get(), content);
        }
    }

    @Override
    public void removeComponentsFromTag(@NotNull CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove(CONTENT_KEY);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        if (!content.equals(ClipboardContent.DEFAULT)) {
            tag.put(CONTENT_KEY, encode(getContent()));
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.handleUpdateTag(tag, registries);
        if (tag.contains(CONTENT_KEY)) {
            setContent(decode(tag.get(CONTENT_KEY)));
        }
    }

    public ClipboardContent getContent() {
        return content;
    }

    public void setContent(ClipboardContent content) {
        this.content = content == null ? ClipboardContent.DEFAULT : content;
        setChanged();
    }

    private static Tag encode(ClipboardContent value) {
        return ClipboardContent.CODEC.encodeStart(NbtOps.INSTANCE, value).getOrThrow();
    }

    private static ClipboardContent decode(Tag tag) {
        return ClipboardContent.CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow();
    }
}
