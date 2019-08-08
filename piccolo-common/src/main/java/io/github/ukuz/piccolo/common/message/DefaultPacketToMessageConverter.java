/*
 * Copyright 2019 ukuz90
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.ukuz.piccolo.common.message;

import io.github.ukuz.piccolo.api.connection.Connection;
import io.github.ukuz.piccolo.api.exchange.protocol.Packet;
import io.github.ukuz.piccolo.api.exchange.support.BaseMessage;
import io.github.ukuz.piccolo.api.exchange.support.PacketToMessageConverter;

import io.github.ukuz.piccolo.common.constants.CommandType;
import io.netty.channel.Channel;


/**
 * @author ukuz90
 */
public class DefaultPacketToMessageConverter implements PacketToMessageConverter {
    @Override
    public BaseMessage convert(Packet packet, Connection connection) {
        CommandType cmd = CommandType.toCMD(packet.getCommandType());
        switch (cmd) {
            case ERROR:
                return new ErrorMessage(connection);
            case HANDSHAKE:
                return new HandshakeMessage(connection);
            case HEARTBEAT:
                return new HeartbeatMessage(connection);
            default:
                return null;
        }
    }
}
