package org.sobadfish.teleportgun.form.push;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import org.sobadfish.teleportgun.TeleportGunMainClass;
import org.sobadfish.teleportgun.form.CustomButtonForm;
import org.sobadfish.teleportgun.manager.ColumnManager;
import org.sobadfish.teleportgun.utils.GenerateParticleUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 打开传送点
 * */
public class PlayerOpenPrivatePointForm extends CustomButtonForm {

    public List<Position> publicPoints = new ArrayList<>();

    private boolean isCanRemove = true;

    public PlayerOpenPrivatePointForm(String title, String content, Player playerInfo, Item item) {
        super(title, content, playerInfo, item);
    }

    @Override
    public void callback(FormResponseSimple response) {
        //添加传送点...
        if(response.getClickedButtonId() <= 1){
            isCanRemove = false;
            if(response.getClickedButtonId() == 0){
                if(playerInfo.isOp()){
                    PlayerAdminAddTeleportSettingForm playerOpenPointForm = new PlayerAdminAddTeleportSettingForm("传送枪配置-添加传送点",playerInfo,item,false);
                    TeleportGunMainClass.INSTANCE.formManager.addForm(playerInfo,playerOpenPointForm);
                }else{
                    PlayerUserAddTeleportSettingForm playerOpenPointForm = new PlayerUserAddTeleportSettingForm("传送枪配置-添加传送点",playerInfo,item,false);
                    TeleportGunMainClass.INSTANCE.formManager.addForm(playerInfo,playerOpenPointForm);
                }

                return;
            }
            if(response.getClickedButtonId() == 1){
                if(playerInfo.isOp()) {
                    PlayerAdminRemoveTeleportSettingForm playerOpenPointForm = new PlayerAdminRemoveTeleportSettingForm("传送枪配置-移除传送点", playerInfo, item, false);
                    TeleportGunMainClass.INSTANCE.formManager.addForm(playerInfo, playerOpenPointForm);
                    return;
                }else{
                    PlayerUserRemoveTeleportSettingForm playerOpenPointForm = new PlayerUserRemoveTeleportSettingForm("传送枪配置-移除传送点", playerInfo, item, false);
                    TeleportGunMainClass.INSTANCE.formManager.addForm(playerInfo, playerOpenPointForm);
                    return;
                }
            }
        }

        if(publicPoints.size() > response.getClickedButtonId() - 2) {
            Position pos = publicPoints.get(response.getClickedButtonId() - 2);
            //写入传送枪
            Item iclone = item.clone();
            CompoundTag tag = iclone.getNamedTag();
            if(tag == null){
                tag = new CompoundTag();
            }
            tag.putString(ColumnManager.TELEPORT_LOCATION,GenerateParticleUtils.asLocation(pos));
            iclone.setNamedTag(tag);
            getPlayerInfo().getInventory().removeItem(item);
            getPlayerInfo().getInventory().addItem(iclone);
            TeleportGunMainClass.sendMessageToObject("&a成功设定传送点 "+response.getClickedButton().getText(),getPlayerInfo());

        }


    }

    @Override
    public void onCreateView() {
        //首先是公共传送点
        addButton(new ElementButton("添加传送点",new ElementButtonImageData("path","textures/particle/teleport_door")));
        addButton(new ElementButton("移除传送点",new ElementButtonImageData("path","textures/particle/teleport_door_blue")));
//        for(Map.Entry<String,Position> positionEntry: TeleportGunMainClass.INSTANCE.configManager.getAllPosition().entrySet()){
//            addButton(new ElementButton(positionEntry.getKey(),new ElementButtonImageData("path","textures/particle/teleport_door")));
//            publicPoints.add(positionEntry.getValue());
//        }
//        //之后是传送枪保存的
        if(item.hasCompoundTag()){
            CompoundTag tag = item.getNamedTag();
            if(tag.contains(ColumnManager.TELEPORT_LIST_TAG)){
                ListTag<StringTag> st = tag.getList(ColumnManager.TELEPORT_LIST_TAG,StringTag.class);
                for(StringTag stringTag:st.getAll()){
                    String[] split = stringTag.data.split(":");
                    String name = "未知名称";
                    if(split.length > 4){
                        name = split[4];
                    }
                    Position pos = GenerateParticleUtils.asPosition(stringTag.data);
                    if(pos != null){
                        addButton(new ElementButton(name,new ElementButtonImageData("path","textures/particle/teleport_door")));
                        publicPoints.add(pos);
                    }

                }
            }
        }

    }

    @Override
    public boolean isCanRemove() {
        return isCanRemove;
    }
}
