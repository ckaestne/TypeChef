/*
 * TypeChef Variability-Aware Lexer.
 * Copyright 2010-2011, Christian Kaestner, Paolo Giarrusso
 * Licensed under GPL 3.0
 *
 * built on top of
 *
 * Anarres C Preprocessor
 * Copyright (c) 2007-2008, Shevek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package de.fosd.typechef.lexer;

import java.util.Arrays;
import java.util.Collection;

/**
 * Warning classes which may optionally be emitted by the Preprocessor.
 */
public enum Warning {
    TRIGRAPHS,
    UNDEF, /*UNUSED_MACROS,*/ ENDIF_LABELS, ERROR;

    public static Collection<Warning> allWarnings() {
        return Arrays.asList(new Warning[]{TRIGRAPHS, UNDEF,
                /*UNUSED_MACROS,*/ ENDIF_LABELS});

    }
}
