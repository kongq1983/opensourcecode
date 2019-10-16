/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingsphere.api.algorithm.sharding.complex;

import io.shardingsphere.api.algorithm.sharding.ShardingValue;
import io.shardingsphere.core.routing.strategy.ShardingAlgorithm;

import java.util.Collection;

/** 用于处理使用多键作为分片键进行分片的场景，包含多个分片键的逻辑较复杂，需要应用开发者自行处理其中的复杂度
 * Complex keys sharding algorithm.
 * 复合分片算法 需要配合ComplexShardingStrategy使用
 * @author zhangliang
 */
public interface ComplexKeysShardingAlgorithm extends ShardingAlgorithm {
    
    /**
     * Sharding.
     * 
     * @param availableTargetNames available data sources or tables's names
     * @param shardingValues sharding values
     * @return sharding results for data sources or tables's names
     */
    Collection<String> doSharding(Collection<String> availableTargetNames, Collection<ShardingValue> shardingValues);
}
