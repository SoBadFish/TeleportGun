package org.sobadfish.teleportgun.customitem;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.ItemDurable;
import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustomTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemCategory;
import org.sobadfish.teleportgun.TeleportGunMainClass;

public class BaseTeleportGunItem extends ItemCustomTool  implements ItemDurable {




    public BaseTeleportGunItem(String id, String name, String textureName) {
        super(id, name, textureName);
    }



    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition.toolBuilder(this, CreativeItemCategory.ITEMS)
                .allowOffHand(false)
                .build();
    }

    @Override
    public int getMaxDurability() {
        return 100;
    }

    @Override
    public boolean useOn(Block block) {
        return true;
    }

    @Override
    public boolean isTool() {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }








    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
            //点击方块
        TeleportGunMainClass.lockedPlayers.add(player);
        if(player.isSneaking()){
//            if(!player.isOp()){
//                TeleportGunMainClass.sendMessageToObject("&c您无法修改此传送枪的设定",player);
//                return true;
//            }
            TeleportGunMainClass.INSTANCE.openSettingPanel(player,this);
//            PlayerTeleportSettingForm playerCreateTeamForm = new PlayerTeleportSettingForm(player,this);
//            TeleportGunMainClass.INSTANCE.formManager.addForm(player,playerCreateTeamForm);
            return true;
        }
        if(this.getDamage()  >= this.getMaxDurability()){
            //TODO 传送液不足
            TeleportGunMainClass.sendMessageToObject("&c没有足够的传送液",player);
            return false;
        }
        if(TeleportGunMainClass.openDoor(player,block,face,this,true)){
            this.setDamage(this.getDamage() + 1);
            player.getInventory().setItemInHand(this);
        }
        return false;
    }



}
