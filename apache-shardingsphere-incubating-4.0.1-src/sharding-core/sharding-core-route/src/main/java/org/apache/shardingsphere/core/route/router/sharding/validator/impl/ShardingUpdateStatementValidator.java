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

package org.apache.shardingsphere.core.route.router.sharding.validator.impl;

import java.util.Collection;
import java.util.List;

import org.apache.shardingsphere.core.exception.ShardingException;
import org.apache.shardingsphere.sql.parser.relation.segment.table.TablesContext;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.assignment.AssignmentSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.expr.ExpressionSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.expr.simple.LiteralExpressionSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.expr.simple.ParameterMarkerExpressionSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.predicate.AndPredicate;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.predicate.PredicateSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.predicate.WhereSegment;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.predicate.value.PredicateCompareRightValue;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.predicate.value.PredicateInRightValue;
import org.apache.shardingsphere.sql.parser.sql.segment.dml.predicate.value.PredicateRightValue;
import org.apache.shardingsphere.sql.parser.sql.statement.dml.UpdateStatement;
import org.apache.shardingsphere.core.route.router.sharding.validator.ShardingStatementValidator;
import org.apache.shardingsphere.core.rule.ShardingRule;

import com.google.common.base.Optional;

/**
 * Sharding update statement validator.
 *
 * @author zhangliang
 */
public final class ShardingUpdateStatementValidator implements ShardingStatementValidator<UpdateStatement> {

    @Override
    public void validate(final ShardingRule shardingRule, final UpdateStatement sqlStatement, final List<Object> parameters) {
        String tableName = new TablesContext(sqlStatement).getSingleTableName();
        for (AssignmentSegment each : sqlStatement.getSetAssignment().getAssignments()) {
            String shardingColumn = each.getColumn().getName();
            if (shardingRule.isShardingColumn(shardingColumn, tableName)) {
                Optional<Object> shardingColumnSetAssignmentValue = getShardingColumnSetAssignmentValue(each, parameters);
                Optional<Object> shardingValue = Optional.absent();
                Optional<WhereSegment> whereSegmentOptional = sqlStatement.getWhere();
                if (whereSegmentOptional.isPresent()) {
                    shardingValue = getShardingValue(whereSegmentOptional.get(), parameters, shardingColumn);
                }
                if (shardingColumnSetAssignmentValue.isPresent() && shardingValue.isPresent() && shardingColumnSetAssignmentValue.get().equals(shardingValue.get())) {
                    continue;
                }
                throw new ShardingException("Can not update sharding key, logic table: [%s], column: [%s].", tableName, each);
            }
        }
    }

    private Optional<Object> getShardingColumnSetAssignmentValue(final AssignmentSegment assignmentSegment, final List<Object> parameters) {
        ExpressionSegment segment = assignmentSegment.getValue();
        int shardingSetAssignIndex = -1;
        if (segment instanceof ParameterMarkerExpressionSegment) {
            shardingSetAssignIndex = ((ParameterMarkerExpressionSegment) segment).getParameterMarkerIndex();
        }
        if (segment instanceof LiteralExpressionSegment) {
            return Optional.of(((LiteralExpressionSegment) segment).getLiterals());
        }
        if (-1 == shardingSetAssignIndex || shardingSetAssignIndex > parameters.size() - 1) {
            return Optional.absent();
        }
        return Optional.of(parameters.get(shardingSetAssignIndex));
    }

    private Optional<Object> getShardingValue(final WhereSegment whereSegment, final List<Object> parameters, final String shardingColumn) {
        for (AndPredicate each : whereSegment.getAndPredicates()) {
            return getShardingValue(each, parameters, shardingColumn);
        }
        return Optional.absent();
    }

    private Optional<Object> getShardingValue(final AndPredicate andPredicate, final List<Object> parameters, final String shardingColumn) {
        for (PredicateSegment each : andPredicate.getPredicates()) {
            if (!shardingColumn.equalsIgnoreCase(each.getColumn().getName())) {
                continue;
            }
            PredicateRightValue rightValue = each.getRightValue();
            if (rightValue instanceof PredicateCompareRightValue) {
                ExpressionSegment segment = ((PredicateCompareRightValue) rightValue).getExpression();
                return getPredicateCompareShardingValue(segment, parameters, shardingColumn);
            }
            if (rightValue instanceof PredicateInRightValue) {
                Collection<ExpressionSegment> segments = ((PredicateInRightValue) rightValue).getSqlExpressions();
                return getPredicateInShardingValue(segments, parameters, shardingColumn);
            }
        }
        return Optional.absent();
    }

    private Optional<Object> getPredicateCompareShardingValue(final ExpressionSegment segment, final List<Object> parameters, final String shardingColumn) {
        int shardingValueParameterMarkerIndex = -1;
        if (segment instanceof ParameterMarkerExpressionSegment) {
            shardingValueParameterMarkerIndex = ((ParameterMarkerExpressionSegment) segment).getParameterMarkerIndex();
            if (-1 == shardingValueParameterMarkerIndex || shardingValueParameterMarkerIndex > parameters.size() - 1) {
                return Optional.absent();
            }
            return Optional.of(parameters.get(shardingValueParameterMarkerIndex));
        }
        if (segment instanceof LiteralExpressionSegment) {
            return Optional.of(((LiteralExpressionSegment) segment).getLiterals());
        }
        return Optional.absent();
    }

    private Optional<Object> getPredicateInShardingValue(final Collection<ExpressionSegment> segments, final List<Object> parameters, final String shardingColumn) {
        int shardingColumnWhereIndex = -1;
        for (ExpressionSegment each : segments) {
            if (each instanceof ParameterMarkerExpressionSegment) {
                shardingColumnWhereIndex = ((ParameterMarkerExpressionSegment) each).getParameterMarkerIndex();
                if (-1 == shardingColumnWhereIndex || shardingColumnWhereIndex > parameters.size() - 1) {
                    continue;
                }
                return Optional.of(parameters.get(shardingColumnWhereIndex));
            }
            if (each instanceof LiteralExpressionSegment) {
                return Optional.of(((LiteralExpressionSegment) each).getLiterals());
            }
        }
        return Optional.absent();
    }
}
