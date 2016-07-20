/*
 *  * Copyright 2016 Skymind, Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 */

package org.datavec.api.transform.condition.column;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import org.datavec.api.transform.condition.SequenceConditionMode;
import org.datavec.api.writable.Writable;
import org.datavec.api.transform.condition.ConditionOp;

import java.util.Set;

/**
 * Condition that applies to the values in an Integer column, using a {@link ConditionOp}
 *
 * @author Alex Black
 */
@EqualsAndHashCode(callSuper = true)
public class IntegerColumnCondition extends BaseColumnCondition {

    private final ConditionOp op;
    private final Integer value;
    private final Set<Integer> set;

    /**
     * Constructor for operations such as less than, equal to, greater than, etc.
     * Uses default sequence condition mode, {@link BaseColumnCondition#DEFAULT_SEQUENCE_CONDITION_MODE}
     *
     * @param columnName Column to check for the condition
     * @param op         Operation (<, >=, !=, etc)
     * @param value      Value to use in the condition
     */
    public IntegerColumnCondition(String columnName, ConditionOp op, int value) {
        this(columnName, DEFAULT_SEQUENCE_CONDITION_MODE, op, value);
    }

    /**
     * Constructor for operations such as less than, equal to, greater than, etc.
     *
     * @param column                Column to check for the condition
     * @param sequenceConditionMode Mode for handling sequence data
     * @param op                    Operation (<, >=, !=, etc)
     * @param value                 Value to use in the condition
     */
    public IntegerColumnCondition(String column, SequenceConditionMode sequenceConditionMode,
                                  ConditionOp op, int value) {
        super(column, sequenceConditionMode);
        if (op == ConditionOp.InSet || op == ConditionOp.NotInSet) {
            throw new IllegalArgumentException("Invalid condition op: cannot use this constructor with InSet or NotInSet ops");
        }
        this.op = op;
        this.value = value;
        this.set = null;
    }

    /**
     * Constructor for operations: ConditionOp.InSet, ConditionOp.NotInSet
     * Uses default sequence condition mode, {@link BaseColumnCondition#DEFAULT_SEQUENCE_CONDITION_MODE}
     *
     * @param column Column to check for the condition
     * @param op     Operation. Must be either ConditionOp.InSet, ConditionOp.NotInSet
     * @param set    Set to use in the condition
     */
    public IntegerColumnCondition(String column, ConditionOp op, Set<Integer> set) {
        this(column, DEFAULT_SEQUENCE_CONDITION_MODE, op, set);
    }

    /**
     * Constructor for operations: ConditionOp.InSet, ConditionOp.NotInSet
     *
     * @param column                Column to check for the condition
     * @param sequenceConditionMode Mode for handling sequence data
     * @param op                    Operation. Must be either ConditionOp.InSet, ConditionOp.NotInSet
     * @param set                   Set to use in the condition
     */
    public IntegerColumnCondition(String column, SequenceConditionMode sequenceConditionMode,
                                  ConditionOp op, Set<Integer> set) {
        super(column, sequenceConditionMode);
        if (op != ConditionOp.InSet && op != ConditionOp.NotInSet) {
            throw new IllegalArgumentException("Invalid condition op: can ONLY use this constructor with InSet or NotInSet ops");
        }
        this.op = op;
        this.value = null;
        this.set = set;
    }

    //Private constructor for Jackson deserialization only
    private IntegerColumnCondition(@JsonProperty("columnName") String columnName, @JsonProperty("op") ConditionOp op,
                                   @JsonProperty("value") Integer value, @JsonProperty("set") Set<Integer> set) {
        super(columnName, DEFAULT_SEQUENCE_CONDITION_MODE);
        this.op = op;
        this.value = (set == null ? value : null);
        this.set = set;
    }


    @Override
    public boolean columnCondition(Writable writable) {
        switch (op) {
            case LessThan:
                return writable.toInt() < value;
            case LessOrEqual:
                return writable.toInt() <= value;
            case GreaterThan:
                return writable.toInt() > value;
            case GreaterOrEqual:
                return writable.toInt() >= value;
            case Equal:
                return writable.toInt() == value;
            case NotEqual:
                return writable.toInt() != value;
            case InSet:
                return set.contains(writable.toInt());
            case NotInSet:
                return !set.contains(writable.toInt());
            default:
                throw new RuntimeException("Unknown or not implemented op: " + op);
        }
    }

    @Override
    public String toString() {
        return "IntegerColumnCondition(columnName=\"" + columnName + "\"," + op + "," +
                (op == ConditionOp.NotInSet || op == ConditionOp.InSet ? set : value) + ")";
    }
}