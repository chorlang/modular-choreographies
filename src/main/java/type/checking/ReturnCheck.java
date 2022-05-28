/*
 * Copyright 2022 Anne Møller Madsen <annemadsen05@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package type.checking;

import custom.exceptions.ReturnException;
import simple.chore.stuctures.*;

public class ReturnCheck {

    /**
     *
     * @param e Empty, is nothing
     * @param shouldReturn Boolean, True if the procedure should return, False if not.
     * @param isbranch Boolean, True if we are in a branch, False if we are in the main branch
     * @return Boolean, returns if the procedure should not return.
     * @throws ReturnException Thrown when a return statement is missing.
     */
    public static Boolean nilRule(Empty e, Boolean shouldReturn, Boolean isbranch) throws ReturnException {
        if (shouldReturn && !isbranch){
            throw new ReturnException("Missing return statement in procedure");
        }
        return Boolean.FALSE;
    }

    /**
     *
     * @param r Return, recursive structure of the instruction Return.
     * @param shouldReturn Boolean, True if the procedure should return, False if not.
     * @return Boolean, returns when the procedure should return
     * @throws ReturnException Thrown when a procedure returns when it should not
     */
    public static Boolean returnRule(Return r, Boolean shouldReturn) throws ReturnException {
        if (!shouldReturn){
            throw new ReturnException("Procedure returns when it should not");
        }
        return Boolean.TRUE;
    }

    /**
     *
     * @param i IfState, contains the information of the if-statement.
     * @param shouldReturn Boolean, True if the procedure should return, False if not.
     * @param isbranch Boolean, True if we are in a branch, False if we are in the main branch
     * @return Boolean, returns if one branch returns and the other does not.
     * @throws Exception Thrown when mismatch of return.
     */
    public static Boolean ifRule(IfState i, Boolean shouldReturn, Boolean isbranch) throws Exception {
        // Checks if there is return statements in the branches
        Boolean then = returnCheck(i.then(),shouldReturn, Boolean.TRUE);
        Boolean else_ = returnCheck(i.else_(),shouldReturn, Boolean.TRUE);
        // If there are return statements in both branches and the procedure should return
        if (then && else_ && shouldReturn){
            // Then the main branch should not return
            return returnCheck(i.continuation(),Boolean.FALSE,isbranch);
        } else if (!then && !else_ && shouldReturn){ // Branches does not return but procedure should
            // Check that main branch returns
           return returnCheck(i.continuation(),shouldReturn,isbranch);
        }
        // If one branch returns and the other does not.
        return Boolean.FALSE;
    }

    /**
     *
     * @param c Choreography, a recursive structure that contains the choreography.
     * @param shouldReturn Boolean, True if the procedure should return, False if not.
     * @param isbranch Boolean, True if we are in a branch, False if we are in the main branch
     * @return Boolean, result of the return check.
     * @throws Exception Thrown when mismatch of return.
     */
    public static Boolean returnCheck(Choreography c, Boolean shouldReturn, Boolean isbranch) throws Exception {
        switch (c){
            case Empty e -> {return nilRule(e,shouldReturn,isbranch);}
            case StatefulCom s -> {return returnCheck(s.continuation(),shouldReturn,isbranch);}
            case StatefulComVar sv -> {return returnCheck(sv.continuation(),shouldReturn,isbranch);}
            case Com com -> { }
            case Return r -> {return returnRule(r, shouldReturn);}
            case ProcInvoke p -> {return returnCheck(p.continuation(),shouldReturn,isbranch);}
            case IfState i -> {return ifRule(i,shouldReturn,isbranch);}
            case Declaration d -> {return returnCheck(d.continuation(),shouldReturn,isbranch);}
            case Initialization in -> {return returnCheck(in.continuation(),shouldReturn,isbranch);}
            case InitialDeclare inDe -> {return returnCheck(inDe.continuation(),shouldReturn,isbranch);}
            case Selection sel -> {return returnCheck(sel.continuation(),shouldReturn,isbranch);}
        }
        return null;
    }
}
