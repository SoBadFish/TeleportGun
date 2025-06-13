package org.sobadfish.teleportgun.customitem;


import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.ItemCreativeCategory;
import cn.nukkit.item.customitem.data.ItemCreativeGroup;
import cn.nukkit.item.customitem.data.RenderOffsets;


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
                .simpleBuilder(this, ItemCreativeCategory.ITEMS)
                .creativeGroup(ItemCreativeGroup.MISC_FOOD)
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
