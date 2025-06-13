package org.sobadfish.teleportgun.customitem;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.ItemCreativeCategory;
import cn.nukkit.item.customitem.data.ItemCreativeGroup;
import cn.nukkit.item.customitem.data.RenderOffsets;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.sobadfish.teleportgun.utils.GenerateParticleUtils;

public class CustomTeleportItem extends BaseTeleportGunItem {

    public CustomTeleportItem() {
        super("minecraft:teleport_gun", "跨纬度传送枪", "teleport_gun");
    }




}

