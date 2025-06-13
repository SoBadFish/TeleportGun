package org.sobadfish.teleportgun.form.push;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.item.Item;
import org.sobadfish.teleportgun.TeleportGunMainClass;
import org.sobadfish.teleportgun.form.CustomButtonForm;

/**
 * 管理员开启的表单界面 可选择控制开启其他不同的页面
 * */
public class PlayerAdminTeleportMoreSettingForm extends CustomButtonForm {



    public PlayerAdminTeleportMoreSettingForm(String content, Player playerInfo,Item item) {
        super("传送枪配置--调整传送枪参数", content, playerInfo,item);
    }

    @Override
    public void callback(FormResponseSimple response) {
        switch (response.getClickedButtonId()) {
            case 0 -> {
                //打开传送枪配置
                PlayerAdminTeleportSettingForm form = new PlayerAdminTeleportSettingForm("传送枪配置--调整传送枪参数--传送枪配置", playerInfo, item);
                TeleportGunMainClass.INSTANCE.formManager.addForm(playerInfo, form);
            }
            case 1 -> {
                PlayerAdminAddTeleportSettingForm form1 = new PlayerAdminAddTeleportSettingForm("调整传送枪参数--公共传送点", playerInfo, item, true);
                TeleportGunMainClass.INSTANCE.formManager.addForm(playerInfo, form1);
            }
            case 2 -> {
                PlayerAdminAddTeleportSettingForm form2 = new PlayerAdminAddTeleportSettingForm("调整传送枪参数--添加传送枪传送点", playerInfo, item, false);
                TeleportGunMainClass.INSTANCE.formManager.addForm(playerInfo, form2);
            }
            default -> {
            }
        }

    }

    @Override
    public void onCreateView() {
        addButton(new ElementButton("传送枪配置",new ElementButtonImageData("path","textures/items/teleport_gun")));
        addButton(new ElementButton("添加公共传送点",new ElementButtonImageData("path","textures/particle/teleport_door")));
        addButton(new ElementButton("添加传送枪传送点",new ElementButtonImageData("path","textures/particle/teleport_door_blue")));

    }
}
