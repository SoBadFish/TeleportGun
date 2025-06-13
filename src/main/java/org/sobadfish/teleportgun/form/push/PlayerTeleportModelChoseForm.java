package org.sobadfish.teleportgun.form.push;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.item.Item;
import org.sobadfish.teleportgun.TeleportGunMainClass;
import org.sobadfish.teleportgun.form.CustomButtonForm;
import org.sobadfish.teleportgun.manager.ColumnManager;

/**
 * 这个页面用于选择传送功能 例如传送玩家与传送坐标点
 * */
public class PlayerTeleportModelChoseForm extends CustomButtonForm {

    public PlayerTeleportModelChoseForm(String content, Player playerInfo,Item item) {
        super("传送枪配置", content, playerInfo,item);

    }

    @Override
    public void callback(FormResponseSimple response) {
       if(response.getClickedButtonId() == 0){
           PlayerOpenPointForm playerOpenPointForm = new PlayerOpenPointForm("传送枪配置-传送点","",playerInfo,item);
           TeleportGunMainClass.INSTANCE.formManager.addForm(playerInfo,playerOpenPointForm);
       }
        if(item.hasCompoundTag() && item.getNamedTag().contains(ColumnManager.ENABLE_PLAYER)
                && item.getNamedTag().getBoolean(ColumnManager.ENABLE_PLAYER)){
            if(response.getClickedButtonId() == 1){
                PlayerPlayerPointForm playerOpenPointForm = new PlayerPlayerPointForm("传送枪配置-传送玩家","",playerInfo,item);
                TeleportGunMainClass.INSTANCE.formManager.addForm(playerInfo,playerOpenPointForm);
            }
            if(response.getClickedButtonId() == 2){
                PlayerAdminTeleportMoreSettingForm playerAdminTeleportMoreSettingForm = new PlayerAdminTeleportMoreSettingForm("",playerInfo,item);
                TeleportGunMainClass.INSTANCE.formManager.addForm(playerInfo,playerAdminTeleportMoreSettingForm);
            }
        }else{
            if(response.getClickedButtonId() == 1){
                PlayerAdminTeleportMoreSettingForm playerAdminTeleportMoreSettingForm = new PlayerAdminTeleportMoreSettingForm("",playerInfo,item);
                TeleportGunMainClass.INSTANCE.formManager.addForm(playerInfo,playerAdminTeleportMoreSettingForm);
            }
        }

    }

    @Override
    public void onCreateView() {
        setContent("* 使用须知:\n1. 请先选择好传送坐标再开启传送门 否则打开的传送门会是上一次保存的坐标\n2. 传送门开启后, 将会存在5秒。请尽快移动\n3. 每次开启传送门将会消耗1个单位的传送能量，请合理安排使用");
        addButton(new ElementButton("传送点",new ElementButtonImageData("path","textures/particle/teleport_door")));
        if(item.hasCompoundTag() && item.getNamedTag().contains(ColumnManager.ENABLE_PLAYER) && item.getNamedTag().getBoolean(ColumnManager.ENABLE_PLAYER)){
            addButton(new ElementButton("传送玩家",new ElementButtonImageData("path","textures/ui/FriendsDiversity")));
        }
        if(playerInfo.isOp()){
            addButton(new ElementButton("调整传送枪参数",new ElementButtonImageData("path","textures/ui/FriendsDiversity")));
        }


    }
}
