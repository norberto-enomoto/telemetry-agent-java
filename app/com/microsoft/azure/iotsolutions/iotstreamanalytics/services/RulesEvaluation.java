// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.services;

import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.models.*;
import play.Logger;
import play.libs.F;

import java.util.ArrayList;

public class RulesEvaluation implements IRulesEvaluation {

    private static final Logger.ALogger log = Logger.of(RulesEvaluation.class);

    // Important! keep the values lowercase
    @SuppressWarnings("SpellCheckingInspection")
    private static final String GT1_OPERATOR = "greaterthan";
    private static final String GT2_OPERATOR = ">";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String GTE1_OPERATOR = "greaterthanorequal";
    private static final String GTE2_OPERATOR = ">=";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String LT1_OPERATOR = "lessthan";
    private static final String LT2_OPERATOR = "<";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String LTE1_OPERATOR = "lessthanorequal";
    private static final String LTE2_OPERATOR = "<=";
    private static final String EQ1_OPERATOR = "equals";
    private static final String EQ2_OPERATOR = "equal";
    private static final String EQ3_OPERATOR = "=";
    private static final String EQ4_OPERATOR = "==";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String NEQ1_OPERATOR = "notequal";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String NEQ2_OPERATOR = "notequals";
    private static final String NEQ3_OPERATOR = "!=";
    private static final String NEQ4_OPERATOR = "<>";

    private final String GT_MESSAGE = "`{}` value `{}` is greater than `{}`";
    private final String GTE_MESSAGE = "`{}` value `{}` is greater than or equal to `{}`";
    private final String LT_MESSAGE = "`{}` value `{}` is less than `{}`";
    private final String LTE_MESSAGE = "`{}` value `{}` is less than or equal to `{}`";
    private final String EQ_MESSAGE = "`{}` value `{}` is equal to `{}`";
    private final String NEQ_MESSAGE = "`{}` value `{}` is not equal to `{}`";

    public F.Tuple<Boolean, String> evaluate(RuleApiModel rule, RawMessage message) {

        log.debug("Evaluating rule {} for device {}", rule.getDescription(), message.getDeviceId());

        F.Tuple<Boolean, String> noMatch = new F.Tuple<>(false, "");
        ArrayList<String> descriptions = new ArrayList<>();

        for (ConditionApiModel c : rule.getConditions()) {
            F.Tuple<Boolean, String> eval = this.evaluateCondition(c, message);
            // break as soon as one condition doesn't match
            if (!eval._1) return noMatch;
            descriptions.add(eval._2);
        }

        return new F.Tuple<>(true, String.join("; ", descriptions));
    }

    private F.Tuple<Boolean, String> evaluateCondition(
        ConditionApiModel condition,
        RawMessage message) {

        F.Tuple<Boolean, String> result = new F.Tuple<>(false, "");

        String field = condition.getField();

        if (!message.has(field)) return result;

        String type = message.typeOf(field);
        String operator = condition.getOperator().toLowerCase();

        if (type.equals(RawMessage.NUMBER)) {

            double actualValue = message.getNumber(field);
            double threshold = Double.parseDouble(condition.getValue());

            switch (operator) {
                case GT1_OPERATOR:
                case GT2_OPERATOR:
                    if (actualValue > threshold) {
                        result = new F.Tuple<>(
                            true,
                            String.format(GT_MESSAGE,
                                field, actualValue, threshold));
                    }
                    break;
                case GTE1_OPERATOR:
                case GTE2_OPERATOR:
                    if (actualValue >= threshold) {
                        result = new F.Tuple<>(
                            true,
                            String.format(GTE_MESSAGE,
                                field, actualValue, threshold));
                    }
                    break;
                case LT1_OPERATOR:
                case LT2_OPERATOR:
                    if (actualValue < threshold) {
                        result = new F.Tuple<>(
                            true,
                            String.format(LT_MESSAGE,
                                field, actualValue, threshold));
                    }
                    break;
                case LTE1_OPERATOR:
                case LTE2_OPERATOR:
                    if (actualValue <= threshold) {
                        result = new F.Tuple<>(
                            true,
                            String.format(LTE_MESSAGE,
                                field, actualValue, threshold));
                    }
                    break;
                case EQ1_OPERATOR:
                case EQ2_OPERATOR:
                case EQ3_OPERATOR:
                case EQ4_OPERATOR:
                    if (actualValue == threshold) {
                        result = new F.Tuple<>(
                            true,
                            String.format(EQ_MESSAGE,
                                field, actualValue, threshold));
                    }
                    break;
                case NEQ1_OPERATOR:
                case NEQ2_OPERATOR:
                case NEQ3_OPERATOR:
                case NEQ4_OPERATOR:
                    if (actualValue != threshold) {
                        result = new F.Tuple<>(
                            true,
                            String.format(NEQ_MESSAGE,
                                field, actualValue, threshold));
                    }
                    break;
            }
        } else if (type.equals(RawMessage.STRING)) {

            String actualValue = message.getString(field);
            String threshold = condition.getValue();

            switch (operator) {
                case GT1_OPERATOR:
                case GT2_OPERATOR:
                    if (actualValue.compareToIgnoreCase(threshold) > 0) {
                        result = new F.Tuple<>(
                            true,
                            String.format(GT_MESSAGE,
                                field, actualValue, threshold));
                    }
                    break;
                case GTE1_OPERATOR:
                case GTE2_OPERATOR:
                    if (actualValue.compareToIgnoreCase(threshold) >= 0) {
                        result = new F.Tuple<>(
                            true,
                            String.format(GTE_MESSAGE,
                                field, actualValue, threshold));
                    }
                    break;
                case LT1_OPERATOR:
                case LT2_OPERATOR:
                    if (actualValue.compareToIgnoreCase(threshold) < 0) {
                        result = new F.Tuple<>(
                            true,
                            String.format(LT_MESSAGE,
                                field, actualValue, threshold));
                    }
                    break;
                case LTE1_OPERATOR:
                case LTE2_OPERATOR:
                    if (actualValue.compareToIgnoreCase(threshold) <= 0) {
                        result = new F.Tuple<>(
                            true,
                            String.format(LTE_MESSAGE,
                                field, actualValue, threshold));
                    }
                    break;
                case EQ1_OPERATOR:
                case EQ2_OPERATOR:
                case EQ3_OPERATOR:
                case EQ4_OPERATOR:
                    if (actualValue.equalsIgnoreCase(threshold)) {
                        result = new F.Tuple<>(
                            true,
                            String.format(EQ_MESSAGE,
                                field, actualValue, threshold));
                    }
                    break;
                case NEQ1_OPERATOR:
                case NEQ2_OPERATOR:
                case NEQ3_OPERATOR:
                case NEQ4_OPERATOR:
                    if (!actualValue.equalsIgnoreCase(threshold)) {
                        result = new F.Tuple<>(
                            true,
                            String.format(NEQ_MESSAGE,
                                field, actualValue, threshold));
                    }
                    break;
            }
        }

        return result;
    }
}
