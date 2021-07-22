package org.eric;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.LinkedList;
import java.util.List;

public class XDecoder extends ByteToMessageDecoder {

    //head长度
    private static final int HEAD_LEN = 2;
    //临时缓存
    private ByteBuf tempMsg = Unpooled.buffer();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        ByteBuf message = null;
        int tmpLen = tempMsg.readableBytes();
        if (tmpLen > 0){
            //如果暂存上次余下的报文则合并
            message = Unpooled.buffer();
            message.writeBytes(tempMsg);
            message.writeBytes(in);
        }else {
            message = in;
        }
        List<ByteBuf> results = new LinkedList<>();
        //主要处理逻辑
        handleMessage(message, results);
        out.addAll(results);
    }

    private void handleMessage(ByteBuf message, List<ByteBuf> results) {
        int length = message.readableBytes();
        //报文长度不足head长度则暂存
        if (length < HEAD_LEN){
            tempMsg.clear();
            tempMsg.writeBytes(message);
            return;
        }
        byte[] headBytes = new byte[HEAD_LEN];
        message.readBytes(headBytes);
        int bodyLen = Integer.parseInt(new String(headBytes));
        int readBodyLen = message.readableBytes();
        //报文中的body长度不足，暂存
        if (readBodyLen < bodyLen){
            tempMsg.clear();
            tempMsg.writeBytes(headBytes);
            tempMsg.writeBytes(message);
            return;
        }

        //报文中的body正常
        byte[] bodyBytes = new byte[bodyLen];
        message.readBytes(bodyBytes);
        //取出一个完整的请求body存到结果集中
        results.add(Unpooled.copiedBuffer(bodyBytes));
        tempMsg.clear();

        //如果还有剩余报文则继续递归处理
        int surplusLen = message.readableBytes();
        if (surplusLen != 0){
            handleMessage(message, results);
        }
    }
}
