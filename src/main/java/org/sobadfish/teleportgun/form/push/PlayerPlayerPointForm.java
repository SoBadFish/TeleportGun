package org.sobadfish.teleportgun.form.push;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.tag.CompoundTag;
import org.sobadfish.teleportgun.TeleportGunMainClass;
import org.sobadfish.teleportgun.form.CustomButtonForm;
import org.sobadfish.teleportgun.manager.ColumnManager;
import org.sobadfish.teleportgun.utils.GenerateParticleUtils;

import java.util.ArrayList;
import java.util.List;

public class PlayerPlayerPointForm extends CustomButtonForm {

    public List<Position> publicPoints = new ArrayList<>();

    public PlayerPlayerPointForm(String title, String content, Player playerInfo, Item item) {
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
            tag.putString(ColumnManager.TELEPORT_LOCATION, GenerateParticleUtils.asLocation(pos));
            item.setNamedTag(tag);
            getPlayerInfo().getInventory().setItemInHand(item);
            TeleportGunMainClass.sendMessageToObject("&a成功设定传送点"+response.getClickedButton().getText(),getPlayerInfo());
        }
    }



    @Override
    public void onCreateView() {
        for(Player player : Server.getInstance().getOnlinePlayers().values()){
            if(player.equals(playerInfo)){
                continue;
            }
            Position pos = player.getPosition().getSide(player.getDirection(),2);
            addButton(new ElementButton(player.getName(),new ElementButtonImageData("path","textures/particle/teleport_door")));
            publicPoints.add(pos);
        }

    }

    @Override
    public boolean isCanRemove() {
        return true;
    }
}
