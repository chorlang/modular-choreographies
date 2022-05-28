/*
 * Copyright 2022 Anne Møller Madsen <annemadsen05@gmail.com>
 * Copyright 2022 Fabrizio Montesi <famontesi@gmail.com>
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

import custom.exceptions.*;
import simple.chore.stuctures.Pid;
import stateful.chore.structures.L;
import type.checking.structures.*;

import java.util.List;
import java.util.Map;

public class LocalTypingContent {

    /**
     *
     * @param func FuncCall object, which is the structure of a function expression.
     * @param gamma Pairs<Map<Pid, Map<String, DataType>>, List<L>>, procedure information about the procedure we are in.
     * @param sender Pid, a process.
     * @return Datatype, the return type of the function given.
     * @throws Exception Thrown when information from the Gamma does not match the information given.
     */
    public static DataType callRule(FuncCall func, Pairs<Map<Pid, Map<String, DataType>>, List<L>> gamma, Pid sender) throws Exception {
        // Get the type of the function
        DataType funcType = gamma.first().get(sender).get( func.name());
        if (funcType == null){ // check that the function is defined.
            throw new NotDefinedException("Function "+func.name()+" is not defined in the Gamma");
        }
        if (funcType instanceof FuncType f) { // Check that it is a function type
            // Check that the number of parameters match with the expected number
            if (f.parameters().size() != func.parameter().size()) {
                throw new SizeMismatchException("Number of parameters in function ".concat(func.name()).concat(" does not match defined number"));
            }
            // Check that the types of parameters match with the expected type
            for (int i = 0; i < func.parameter().size(); i++) {
                DataType paraType = typeCheck(func.parameter().get(i), gamma, sender);
                if (paraType == null) {
                    throw new NotDefinedException("Parameter in function ".concat(func.name()).concat(" is not defined"));
                }
                if (!(paraType.equals(f.parameters().get(i)))) {
                    throw new UnexpectedTypeException("Type of parameter in function ".concat(func.name()).concat(" does not match assigned type"));
                }
            }
            // Get the return type
            DataType returnType = typeCheck(func.returnValue(), gamma, sender);
            if (returnType == null) { // Check that it is defined
                throw new NotDefinedException("ReturnType in function ".concat(func.name()).concat(" is not defined"));
            }
            // Check that the return type matches with the expected return type.
            if (!(f.returnType().equals(returnType))) {
                throw new UnexpectedTypeException("Type of return value in function ".concat(func.name()).concat(" does not match assigned type"));
            }
            return returnType;
        } else { // If not function type an exception is thrown
            throw new UnexpectedTypeException("Function is not FuncType");
        }
    }

    /**
     *
     * @return BoolType
     */
    public static BoolType boolRule(){ return new BoolType(); }

    /**
     *
     * @param variableExpr VariableExpr object, which is the structure of a variable expression.
     * @param gamma Pairs<Map<Pid, Map<String, DataType>>, List<L>>, procedure information about the procedure we are in.
     * @param sender Pid, a process.
     * @return Datatype, the type of the variable defined in the Gamma.
     */
    public static DataType idRule(VariableExpr variableExpr, Pairs<Map<Pid, Map<String, DataType>>, List<L>> gamma, Pid sender) throws Exception {
        // Get the type of the variable
        DataType type = gamma.first().get(sender).get(variableExpr.name());
        if (type == null){ // Check that the variable is defined
            throw new Exception(sender.name()+"."+variableExpr.name()+" is not defined");
        }
        return type;
    }

    /**
     *
     * @return IntType.
     */
    public static IntType intRule(){ return new IntType(); }

    /**
     *
     * @return StringType.
     */
    public static StringType stringRule(){
        return new StringType();
    }

    /**
     *
     * @return DoubleType
     */
    public static DoubleType doubleRule() {
        return new DoubleType();
    }

    /**
     *
     * @param e Expr, expression object.
     * @param gamma Map, has all the procedures, and their input and output.
     * @param sender Pid, a process.
     * @return DataType, the DataType of the Expr given.
     * @throws Exception Throws the exceptions thrown by the rules used.
     */
    public static DataType typeCheck(Expr e, Pairs<Map<Pid, Map<String, DataType>>, List<L>> gamma, Pid sender) throws Exception {
        if (e == null){ // Check if the expression is null
            throw new NullValueException("e is null for sender "+sender.name());
        }
        // Run the method corresponding to the expression.
        switch (e){
            case IntExpr i -> {return intRule();}
            case BoolExpr b ->{return boolRule();}
            case StringExpr s -> {return stringRule();}
            case DoubleExpr d -> {return doubleRule();}
            case VariableExpr v -> {return idRule(v,gamma,sender);}
            case FuncCall f -> {
                return callRule(f,gamma, sender);}
            default -> {throw new UnexpectedInputException("Expr does not match any Expr defined: Local type checking");
            }
        }
    }
}
