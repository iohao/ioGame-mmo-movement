package com.seiryuu.game.action;

import cn.hutool.core.convert.Convert;
import com.iohao.game.action.skeleton.annotation.ActionController;
import com.iohao.game.action.skeleton.annotation.ActionMethod;
import com.iohao.game.action.skeleton.core.flow.FlowContext;
import com.iohao.game.bolt.broker.client.kit.UserIdSettingKit;
import com.iohao.game.bolt.broker.core.client.BrokerClientHelper;
import com.iohao.game.common.kit.CollKit;
import com.seiryuu.game.common.cmd.ActionCmd;
import com.seiryuu.game.domain.map.GameMap;
import com.seiryuu.game.managers.MapManager;
import com.seiryuu.game.protocol.movement.PlayerMoveProto;
import com.seiryuu.game.protocol.player.CharacterProto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Seiryuu
 * @description
 * @since 2023-08-29 8:17
 */
@Slf4j
@Component
@ActionController(ActionCmd.cmd)
public class PlayerAction {

    /**
     * 玩家进入游戏
     *
     * @return
     */
    @ActionMethod(ActionCmd.enterGame)
    public void enterGame(FlowContext flowContext, CharacterProto characterProto) {
        String characterId = characterProto.getCharacterId();
        Long mapId = characterProto.mapId;

        // 设置用户id
        UserIdSettingKit.settingUserId(flowContext, Convert.toLong(characterId));
        String str = Convert.toStr(mapId);
        MapManager.INSTANCE.getGameMap(str).characterEnter(characterProto);
    }

    /**
     * 玩家移动
     *
     * @param moveProto
     * @return
     */
    @ActionMethod(ActionCmd.move)
    public void playerMove(PlayerMoveProto moveProto) {

        GameMap gameMap = MapManager.INSTANCE.getGameMap("1");

        CharacterProto characterProto = gameMap.getMapCharacterMap().get(moveProto.characterId);
        characterProto.mapPostX = moveProto.x;
        characterProto.mapPostY = moveProto.y;
        characterProto.mapPostZ = moveProto.z;
        characterProto.orientation = moveProto.r;
        gameMap.getMapCharacterMap().put(characterProto.characterId, characterProto);

        // 有玩家 移动 广播
        List<Long> userIdList = gameMap.currentMapCharacterIds(characterProto.characterId);
        if (CollKit.notEmpty(userIdList)) {
            BrokerClientHelper.getBroadcastContext().broadcast(
                    ActionCmd.of(ActionCmd.move),
                    moveProto,
                    userIdList
            );
        }
    }
}
