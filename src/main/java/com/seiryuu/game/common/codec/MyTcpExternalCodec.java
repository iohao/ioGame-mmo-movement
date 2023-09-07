/*
 * ioGame
 * Copyright (C) 2021 - 2023  渔民小镇 （262610965@qq.com、luoyizhu@gmail.com） . All Rights Reserved.
 * # iohao.com . 渔民小镇
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.seiryuu.game.common.codec;

import com.iohao.game.action.skeleton.core.DataCodecKit;
import com.iohao.game.external.core.message.ExternalMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.nio.ByteOrder;
import java.util.List;

/**
 * Tcp 编解码器
 *
 * @author 渔民小镇
 * @date 2023-02-21
 */
@ChannelHandler.Sharable
public class MyTcpExternalCodec extends MessageToMessageCodec<ByteBuf, ExternalMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ExternalMessage externalMessage, List<Object> out) throws Exception {
        /*
         *【游戏对外服】发送消息给【游戏客户端】
         * 编码器 - ExternalMessage ---> 字节数组，将消息发送到请求端（客户端）
         */
        byte[] bytes = DataCodecKit.encode(externalMessage);

        /*
         * 使用默认 buffer 。如果没有做任何配置，通常默认实现为池化的 direct （直接内存，也称为堆外内存）
         * 优点：使用的系统内存，读写效率高（少一次拷贝），且不受 GC 影响
         * 缺点：分配效率低
         */
        ByteBuf buffer = ctx.alloc().buffer(bytes.length + 4);
        // 消息长度，因为客户端使用的是C#,需要采用小端方式编入int
        buffer.writeIntLE(bytes.length);
        // 消息
        buffer.writeBytes(bytes);

        // 消息
//        buffer.writeBytes(add4Bytes(bytes));

        out.add(buffer);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        // 解码器 - 字节数组 ---> ExternalMessage，接收请求端的消息（客户端）

        // 读取消息长度
        int length = msg.readInt();
        // 消息
        byte[] msgBytes = new byte[length];
        msg.readBytes(msgBytes);

        ExternalMessage message = DataCodecKit.decode(msgBytes, ExternalMessage.class);
        //【游戏对外服】接收【游戏客户端】的消息
        out.add(message);
    }

    public MyTcpExternalCodec() {
    }

    public static MyTcpExternalCodec me() {
        return Holder.ME;
    }

    /** 通过 JVM 的类加载机制, 保证只加载一次 (singleton) */
    private static class Holder {
        static final MyTcpExternalCodec ME = new MyTcpExternalCodec();
    }

    // 俩数组合并,这里  因为客户端使用的是C#,需要采用小端方式编入int
    private byte[] add4Bytes(byte[] data2) {
        //数组结束位,存放内存起始位, 即:高位在后
        int num = data2.length;
        byte[] data1 = new byte[4];
        data1[0] = (byte) (num & 0xff);
        data1[1] = (byte) (num >> 8 & 0xff);
        data1[2] = (byte) (num >> 16 & 0xff);
        data1[3] = (byte) (num >> 24 & 0xff);


        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;
    }
}
