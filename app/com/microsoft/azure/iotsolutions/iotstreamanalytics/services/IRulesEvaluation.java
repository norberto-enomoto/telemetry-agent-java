// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.models.RawMessage;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.models.RuleApiModel;
import play.libs.F;

@ImplementedBy(RulesEvaluation.class)
public interface IRulesEvaluation {
    F.Tuple<Boolean, String> evaluate(RuleApiModel rule, RawMessage message);
}
