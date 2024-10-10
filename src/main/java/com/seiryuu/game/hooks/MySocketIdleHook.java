package com.seiryuu.game.hooks;

import com.iohao.game.action.skeleton.core.exception.ActionErrorEnum;
import com.iohao.game.action.skeleton.protocol.BarMessage;
import com.iohao.game.external.core.message.ExternalCodecKit;
import com.iohao.game.external.core.netty.hook.SocketIdleHook;
import com.iohao.game.external.core.session.UserSession;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义 心跳钩子事件回调 示例
 * <pre>
 *     给逻辑服发送一个请求
 * </pre>
 *
 * @author 渔民小镇
 * @date 2022-05-28
 */
@Slf4j
public class MySocketIdleHook implements SocketIdleHook {

    @Override
    public boolean callback(UserSession userSession, IdleStateEvent event) {
        IdleState state = event.state();
        if (state == IdleState.READER_IDLE) {
            /* 读超时 */
            log.debug("READER_IDLE 读超时");
        } else if (state == IdleState.WRITER_IDLE) {
            /* 写超时 */
            log.debug("WRITER_IDLE 写超时");
        } else if (state == IdleState.ALL_IDLE) {
            /* 总超时 */
            log.debug("ALL_IDLE 总超时");
        }

        BarMessage message = ExternalCodecKit.createErrorIdleMessage(ActionErrorEnum.idleErrorCode);
        // 错误消息
        message.setValidatorMsg(ActionErrorEnum.idleErrorCode.getMsg() + " : " + state.name());

        // 通知客户端，触发了心跳事件
        userSession.writeAndFlush(message);
        // 返回 true 表示通知框架将当前的用户（玩家）连接关闭
        return true;
    }
}