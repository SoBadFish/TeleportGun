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
public class PlayerOpenPointForm extends CustomButtonForm {

    public List<Position> publicPoints = new ArrayList<>();

    public PlayerOpenPointForm(String title, String content, Player playerInfo, Item item) {
        super(title, content, playerInfo, item);
    }

    @Override
    public void callback(FormResponseSimple response) {
        if(publicPoints.size() > response.getClickedButtonId()) {
            Position pos = publicPoints.get(response.getClickedButtonId());
            //写入传送枪
            CompoundTag tag = item.getNamedTag();
            if(tag == null){
                tag = new CompoundTag();
            }
            tag.putString(ColumnManager.TELEPORT_LOCATION,GenerateParticleUtils.asLocation(pos));
            item.setNamedTag(tag);
            getPlayerInfo().getInventory().setItemInHand(item);
            TeleportGunMainClass.sendMessageToObject("&a成功设定传送点 "+response.getClickedButton().getText(),getPlayerInfo());

        }


    }

    @Override
    public void onCreateView() {
        //首先是公共传送点
        for(Map.Entry<String,Position> positionEntry: TeleportGunMainClass.INSTANCE.configManager.getAllPosition().entrySet()){
            addButton(new ElementButton(positionEntry.getKey(),new ElementButtonImageData("path","textures/particle/teleport_door")));
            publicPoints.add(positionEntry.getValue());
        }
        //之后是传送枪保存的
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
}
