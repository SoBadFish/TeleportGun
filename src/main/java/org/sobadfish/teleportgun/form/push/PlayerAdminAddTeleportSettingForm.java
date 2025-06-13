package org.sobadfish.teleportgun.form.push;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import org.sobadfish.teleportgun.TeleportGunMainClass;
import org.sobadfish.teleportgun.customitem.CustomTeleportItem;
import org.sobadfish.teleportgun.form.CustomInputForm;
import org.sobadfish.teleportgun.manager.ColumnManager;
import org.sobadfish.teleportgun.manager.ConfigManager;

import java.util.Map;

/**
 * 此页面为管理员的专属添加页
 * */
public class PlayerAdminAddTeleportSettingForm extends CustomInputForm {


    public boolean isPublic;

    public PlayerAdminAddTeleportSettingForm(String title, Player playerInfo, Item item, boolean isPublic) {
        super(title, playerInfo,item);
        this.isPublic = isPublic;

    }

    @Override
    public void callback(FormResponseCustom response) {
        String name = response.getInputResponse(1);
        String x = response.getInputResponse(2);
        String y = response.getInputResponse(3);
        String z = response.getInputResponse(4);
        String levelName = getPlayerInfo().level.getFolderName();
        if(response.getResponses().size() > 5){
            levelName = response.getInputResponse(5);
        }
        //坐标是整数
        if(!x.matches("-?\\d+") || !y.matches("-?\\d+") || !z.matches("-?\\d+")){
            TeleportGunMainClass.sendMessageToObject("纬度坐标输入无效",getPlayerInfo());
            return;
        }
        if(name.trim().isEmpty()){
            name = "默认传送点";
        }
        Level level = Server.getInstance().getLevelByName(levelName);
        if(level == null){
            levelName = getPlayerInfo().level.getFolderName();
        }
        if(isPublic){
            //写入配置
            ConfigManager configManager = TeleportGunMainClass.INSTANCE.configManager;
            configManager.addPosition(name,x+":"+y+":"+z+":"+levelName);
            TeleportGunMainClass.sendMessageToObject("&a传送枪设置成功",getPlayerInfo());
        }else{
            CompoundTag tag = item.getNamedTag();
            if(tag == null){
                tag = new CompoundTag();
            }
            ListTag<StringTag> positions = new ListTag<>(ColumnManager.TELEPORT_LIST_TAG);
            if(tag.contains(ColumnManager.TELEPORT_LIST_TAG)){
                positions = tag.getList(ColumnManager.TELEPORT_LIST_TAG,StringTag.class);
            }
            positions.add(new StringTag(name,x+":"+y+":"+z+":"+levelName+":"+name));
            tag.putList(ColumnManager.TELEPORT_LIST_TAG,positions);
            item.setNamedTag(tag);
            getPlayerInfo().getInventory().setItemInHand(item);
            TeleportGunMainClass.sendMessageToObject("&a传送枪设置成功",getPlayerInfo());
        }

    }

    @Override
    public void onCreateView() {
        String location = null;
//        if(item.hasCompoundTag() && item.getNamedTag().contains("teleport_location")){
//            location = item.getNamedTag().getString("teleport_location");
//        }
        String x = "";
        String y = "";
        String z = "";
        String levelName = "";
//        if(location != null){
//            x = location.split(":")[0];
//            y = location.split(":")[1];
//            z = location.split(":")[2];
//            levelName = location.split(":")[3];
//
//        }else{
            Position spawn = getPlayerInfo().getPosition();
            x = spawn.getFloorX()+"";
            y = spawn.getFloorY()+"";
            z = spawn.getFloorZ()+"";
            levelName = spawn.getLevel().getName();
//        }
        addElement(new ElementLabel("* 传送枪使用须知\n1.使用此传送枪请先预设您要传送的纬度坐标\n2.每次传送将会消耗1个单位的传送液，传送液不足将无法开启传送门\n3.传送门开启后将维持5秒的时间，5秒后传送门会自动关闭\n4.若输入无效纬度将会在本纬度内传送"));
        addElement(new ElementInput("显示名称","请输入传送点名称","设定传送点" ));
        addElement(new ElementInput("纬度坐标X","请输入纬度坐标X", x));
        addElement(new ElementInput("纬度坐标Y","请输入纬度坐标Y", y));
        addElement(new ElementInput("纬度坐标Z","请输入纬度坐标Z", z));
        if(item instanceof CustomTeleportItem){
            addElement(new ElementInput("纬度名称","请输入纬度名称", levelName));
        }




    }
}
