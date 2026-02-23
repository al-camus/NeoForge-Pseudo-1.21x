package qa.luffy.pseudo.client.renderer.item;

import qa.luffy.pseudo.client.model.item.MeshDrillItemModel;
import qa.luffy.pseudo.common.item.MeshDrillItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public final class MeshDrillRenderer extends GeoItemRenderer<MeshDrillItem> {
    public MeshDrillRenderer() {
        super(new MeshDrillItemModel());
    }
}