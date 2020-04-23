/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.query;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.MySQLPacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.payload.MySQLPacketPayload;

/**
 * COM_QUERY response field count packet for MySQL.
 * 
 * @see <a href="https://dev.mysql.com/doc/internals/en/com-query-response.html">COM_QUERY field count</a>
 *
 * @author zhangliang
 */
@RequiredArgsConstructor
@Getter
public final class MySQLFieldCountPacket implements MySQLPacket {
    
    private final int sequenceId;
    
    private final int columnCount;
    
    public MySQLFieldCountPacket(final MySQLPacketPayload payload) {
        this(payload.readInt1(), payload.readInt1());
    }
    
    @Override
    public void write(final MySQLPacketPayload payload) {
        payload.writeIntLenenc(columnCount);
    }
}
