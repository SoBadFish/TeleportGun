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
        String levelName = getPlayerInfo().level.getFolderName(); // 使用文件夹名
        if(response.getResponses().size() > 5){
            levelName = response.getInputResponse(5);
        }

        if(name.trim().isEmpty()){
            name = "默认传送点";
        }

        // 关键：判断目标世界是否在禁止设置传送点列表中
        ConfigManager configManager = TeleportGunMainClass.INSTANCE.configManager;
        if(!playerInfo.isOp() && configManager.isBanSetPointWorld(levelName)){
            TeleportGunMainClass.sendMessageToObject("&c世界 [" + levelName + "] 已被禁止设置传送点", getPlayerInfo());
            return;
        }

        Level level = Server.getInstance().getLevelByName(levelName);
        if(level == null && !playerInfo.isOp()){
            levelName = getPlayerInfo().level.getFolderName();
            // 再次判断默认世界是否被禁止
            if(configManager.isBanSetPointWorld(levelName)){
                TeleportGunMainClass.sendMessageToObject("&c世界 [" + levelName + "] 已被禁止设置传送点", getPlayerInfo());
                return;
            }
            level = getPlayerInfo().getLevel();
        }

        if(isPublic){
            //写入配置
            configManager.addPosition(name,x+":"+y+":"+z+":"+levelName,item instanceof CustomTeleportItem);
            TeleportGunMainClass.sendMessageToObject("&a传送枪设置成功",getPlayerInfo());
        }else{
            Item iclone = item.clone();
            CompoundTag tag = iclone.getNamedTag();
            if(tag == null){
                tag = new CompoundTag();
            }
            ListTag<StringTag> positions = new ListTag<>();
            if(tag.contains(ColumnManager.TELEPORT_LIST_TAG)){
                positions = tag.getList(ColumnManager.TELEPORT_LIST_TAG,StringTag.class);
            }
            if(!getPlayerInfo().isOp() && positions.size() >= 3){
                TeleportGunMainClass.sendMessageToObject("&c传送枪最多设置3个传送点",getPlayerInfo());
                return;
            }

            positions.add(new StringTag(name,x+":"+y+":"+z+":"+levelName+":"+name));

            tag.putList(ColumnManager.TELEPORT_LIST_TAG,positions);
            iclone.setNamedTag(tag);
            getPlayerInfo().getInventory().removeItem(item);
            getPlayerInfo().getInventory().addItem(iclone);
            TeleportGunMainClass.sendMessageToObject("&a传送枪设置成功",getPlayerInfo());
        }

    }

    @Override
    public void onCreateView() {
        ConfigManager configManager = TeleportGunMainClass.INSTANCE.configManager;
        StringBuilder banWorldTip = new StringBuilder();
        if(!configManager.getBanSetPointWorlds().isEmpty()){
            banWorldTip.append("* 禁止设置传送点的世界：").append(String.join("、", configManager.getBanSetPointWorlds())).append("\n");
        }

        String location = null;
        String x = "";
        String y = "";
        String z = "";
        String levelName = "";
        Position spawn = getPlayerInfo().getPosition();
        x = spawn.getFloorX()+"";
        y = spawn.getFloorY()+"";
        z = spawn.getFloorZ()+"";
        levelName = spawn.getLevel().getFolderName(); // 使用文件夹名

        addElement(new ElementLabel("* 传送枪使用须知\n" +
                banWorldTip + // 添加禁止世界提示
                "1.使用此传送枪请先预设您要传送的纬度坐标\n" +
                "2.每次传送将会消耗1个单位的传送液，传送液不足将无法开启传送门\n" +
                "3.传送门开启后将维持5秒的时间，5秒后传送门会自动关闭\n" +
                "4.若输入无效纬度将会在本纬度内传送"));
        addElement(new ElementInput("显示名称","请输入传送点名称","设定传送点" ));
        addElement(new ElementInput("纬度坐标X","请输入纬度坐标X", x));
        addElement(new ElementInput("纬度坐标Y","请输入纬度坐标Y", y));
        addElement(new ElementInput("纬度坐标Z","请输入纬度坐标Z", z));
        if(item instanceof CustomTeleportItem){
            addElement(new ElementInput("地图名称","请输入世界文件夹名", levelName));
        }
    }

    @Override
    public boolean isCanRemove() {
        return true;
    }
}