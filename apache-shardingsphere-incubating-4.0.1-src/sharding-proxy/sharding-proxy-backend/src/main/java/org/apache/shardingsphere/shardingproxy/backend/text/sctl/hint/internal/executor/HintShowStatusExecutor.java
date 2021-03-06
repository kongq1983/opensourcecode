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

package org.apache.shardingsphere.shardingproxy.backend.text.sctl.hint.internal.executor;

import org.apache.shardingsphere.api.hint.HintManager;
import org.apache.shardingsphere.sharding.merge.dal.common.MultipleLocalDataMergedResult;
import org.apache.shardingsphere.shardingproxy.backend.response.query.QueryHeader;
import org.apache.shardingsphere.shardingproxy.backend.text.sctl.hint.internal.HintShardingType;
import org.apache.shardingsphere.shardingproxy.backend.text.sctl.hint.internal.command.HintShowStatusCommand;
import org.apache.shardingsphere.underlying.merge.MergedResult;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Hint show status command executor.
 *
 * @author liya
 */
public final class HintShowStatusExecutor extends AbstractHintQueryExecutor<HintShowStatusCommand> {
    
    @Override
    protected List<QueryHeader> createQueryHeaders() {
        List<QueryHeader> queryHeaders = new ArrayList<>(2);
        queryHeaders.add(new QueryHeader("", "", "master_only", "", 5, Types.CHAR, 0, false, false, false, false));
        queryHeaders.add(new QueryHeader("", "", "sharding_type", "", 255, Types.CHAR, 0, false, false, false, false));
        return queryHeaders;
    }
    
    @Override
    protected MergedResult createMergedResult() {
        HintShardingType shardingType = HintManager.isDatabaseShardingOnly() ? HintShardingType.DATABASES_ONLY : HintShardingType.DATABASES_TABLES;
        List<Object> row = createRow(HintManager.isMasterRouteOnly(), shardingType);
        return new MultipleLocalDataMergedResult(Collections.singletonList(row));
    }
    
    private List<Object> createRow(final boolean masterOnly, final HintShardingType shardingType) {
        List<Object> row = new ArrayList<>(2);
        row.add(String.valueOf(masterOnly).toLowerCase());
        row.add(String.valueOf(shardingType).toLowerCase());
        return row;
    }
}
