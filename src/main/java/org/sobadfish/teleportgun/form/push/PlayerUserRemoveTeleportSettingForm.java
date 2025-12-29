package org.sobadfish.teleportgun.form.push;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.item.Item;
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
 * 此页面为传送点的移除页面
 * */
public class PlayerUserRemoveTeleportSettingForm extends CustomInputForm {

    public boolean isPublic;

    public PlayerUserRemoveTeleportSettingForm(String title, Player playerInfo, Item item, boolean isPublic) {
        super(title, playerInfo, item);
        this.isPublic = isPublic;
    }

    @Override
    public void callback(FormResponseCustom response) {
        String deleteName = response.getInputResponse(1).trim();
        if (deleteName.isEmpty()) {
            TeleportGunMainClass.sendMessageToObject("&c请输入要删除的传送点名称", getPlayerInfo());
            return;
        }

        // 处理公共传送点删除
        if (isPublic) {
            ConfigManager configManager = TeleportGunMainClass.INSTANCE.configManager;
            boolean isGreen = item instanceof CustomTeleportItem;
            boolean deleted = false;

            // 根据类型选择对应的传送点集合
            if (isGreen) {
                if (configManager.publicGreenTeleportPoint.containsKey(deleteName)) {
                    configManager.publicGreenTeleportPoint.remove(deleteName);
                    deleted = true;
                }
            } else {
                if (configManager.publicTeleportPoint.containsKey(deleteName)) {
                    configManager.publicTeleportPoint.remove(deleteName);
                    deleted = true;
                }
            }

            if (deleted) {
                configManager.save();
                TeleportGunMainClass.sendMessageToObject("&a成功删除公共传送点：" + deleteName, getPlayerInfo());
            } else {
                TeleportGunMainClass.sendMessageToObject("&c未找到名称为：" + deleteName + " 的公共传送点", getPlayerInfo());
            }
        }
        // 处理私有传送点删除（物品内存储）
        else {
            Item iclone = item.clone();
            CompoundTag tag = iclone.getNamedTag();
            if (tag == null || !tag.contains(ColumnManager.TELEPORT_LIST_TAG)) {
                TeleportGunMainClass.sendMessageToObject("&c此传送枪没有存储任何私有传送点", getPlayerInfo());
                return;
            }

            ListTag<StringTag> positions = tag.getList(ColumnManager.TELEPORT_LIST_TAG, StringTag.class);
            boolean deleted = false;

            // 遍历查找并删除对应的传送点
            for (int i = 0; i < positions.size(); i++) {
                StringTag stringTag = positions.get(i);
                String[] split = stringTag.data.split(":");
                String pointName = split.length > 4 ? split[4] : "未知名称";

                if (pointName.equals(deleteName)) {
                    positions.remove(i);
                    deleted = true;
                    break;
                }
            }

            if (deleted) {
                // 更新物品NBT标签
                tag.putList(ColumnManager.TELEPORT_LIST_TAG, positions);
                iclone.setNamedTag(tag);
                getPlayerInfo().getInventory().removeItem(item);
                getPlayerInfo().getInventory().addItem(iclone);
                TeleportGunMainClass.sendMessageToObject("&a成功删除私有传送点：" + deleteName, getPlayerInfo());
            } else {
                TeleportGunMainClass.sendMessageToObject("&c未找到名称为：" + deleteName + " 的私有传送点", getPlayerInfo());
            }
        }
    }

    @Override
    public void onCreateView() {
        StringBuilder tipBuilder = new StringBuilder();
        tipBuilder.append("* 传送点删除须知\n");
        tipBuilder.append("1. 请输入要删除的传送点名称（必须与创建时完全一致）\n");
        tipBuilder.append("2. 删除后无法恢复，请谨慎操作\n");
        tipBuilder.append("\n当前已存在的传送点：\n");

        // 显示当前已有的传送点列表
        if (isPublic) {
            ConfigManager configManager = TeleportGunMainClass.INSTANCE.configManager;
            boolean isGreen = item instanceof CustomTeleportItem;

            tipBuilder.append("类型：").append(isGreen ? "公共绿色传送点" : "公共普通传送点\n");
            Map<String, String> pointMap = isGreen ? configManager.publicGreenTeleportPoint : configManager.publicTeleportPoint;

            if (pointMap.isEmpty()) {
                tipBuilder.append("无已创建的传送点\n");
            } else {
                for (String name : pointMap.keySet()) {
                    tipBuilder.append("- ").append(name).append("\n");
                }
            }
        } else {
            tipBuilder.append("类型：私有传送点（仅当前传送枪可用）\n");
            CompoundTag tag = item.getNamedTag();

            if (tag == null || !tag.contains(ColumnManager.TELEPORT_LIST_TAG)) {
                tipBuilder.append("无已创建的传送点\n");
            } else {
                ListTag<StringTag> positions = tag.getList(ColumnManager.TELEPORT_LIST_TAG, StringTag.class);
                if (positions.isEmpty()) {
                    tipBuilder.append("无已创建的传送点\n");
                } else {
                    for (StringTag stringTag : positions.getAll()) {
                        String[] split = stringTag.data.split(":");
                        String name = split.length > 4 ? split[4] : "未知名称";
                        tipBuilder.append("- ").append(name).append("\n");
                    }
                }
            }
        }

        // 添加表单元素
        addElement(new ElementLabel(tipBuilder.toString()));
        addElement(new ElementInput("删除名称", "请输入要删除的传送点名称", ""));
    }

    @Override
    public boolean isCanRemove() {
        return true;
    }
}