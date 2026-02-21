package org.sobadfish.teleportgun.customitem;


import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.RenderOffsets;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemCategory;


/**
 * 炼药炉物品
 * @author Sobadfish
 * @date 2023/11/27
 */
public class CustomTeleportWaterItem extends ItemCustom {

    public CustomTeleportWaterItem() {
        super("minecraft:teleport_water", "传送液", "teleport_water");
    }


    public int scaleOffset() {
        return 32; // 需要是16的倍数，如 32、64、128
    }

    @Override
    public CustomItemDefinition getDefinition() {

        return CustomItemDefinition
                .simpleBuilder(this, CreativeItemCategory.ITEMS)
                .handEquipped(true)
                .renderOffsets(RenderOffsets.scaleOffset(scaleOffset()))
                .build();
    }


    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }





}
