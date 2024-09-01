package qa.luffy.pseudo.common.util;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import qa.luffy.pseudo.common.Pseudo;

public class PseudoTags {
    public static class Blocks {
        public static final TagKey<Block> INCORRECT_FOR_MESH_TOOL = createTag("incorrect_for_mesh_tool.json");
        public static final TagKey<Block> NEEDS_MESH_TOOL = createTag("needs_mesh_tool");

        public static final TagKey<Block> MITTS_MINEABLE = createTag("mineable/mitts");
        public static final TagKey<Block> SCYTHE_MINEABLE = createTag("mineable/scythe");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, name));
        }
    }
}