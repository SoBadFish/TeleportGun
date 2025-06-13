package org.sobadfish.teleportgun.form.push;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.ElementToggle;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import org.sobadfish.teleportgun.TeleportGunMainClass;
import org.sobadfish.teleportgun.form.CustomInputForm;
import org.sobadfish.teleportgun.manager.ColumnManager;
import org.sobadfish.teleportgun.utils.GenerateParticleUtils;

public class PlayerAdminTeleportSettingForm extends CustomInputForm {

    public PlayerAdminTeleportSettingForm(String title, Player playerInfo, Item item) {
        super(title, playerInfo,item);
    }

    @Override
    public void callback(FormResponseCustom response) {
        boolean togger = response.getToggleResponse(1);
        //写入传送枪
        CompoundTag tag = item.getNamedTag();
        if(tag == null){
            tag = new CompoundTag();
        }
        tag.putBoolean(ColumnManager.ENABLE_PLAYER, togger);
        item.setNamedTag(tag);
        getPlayerInfo().getInventory().setItemInHand(item);
        TeleportGunMainClass.sendMessageToObject("&a传送枪设置成功",getPlayerInfo());
    }

    @Override
    public void onCreateView() {
        boolean enableT = true;
//        int teleportCount = 3;
        if(item.hasCompoundTag()){
            if(item.getNamedTag().contains(ColumnManager.ENABLE_PLAYER)){
                enableT = item.getNamedTag().getBoolean(ColumnManager.ENABLE_PLAYER);
            }
        }


        addElement(new ElementLabel("当前页为传送枪配置选项 \n* 可设置玩家是否可用传送枪打开另一个玩家的坐标点\n* 可允许玩家自行设置传送点坐标\n* 可设置玩家设定传送点坐标数量"));
        addElement(new ElementToggle("是否允许玩家传送", enableT));
    }
}
