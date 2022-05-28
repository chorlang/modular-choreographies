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

package stateful.chore;

import custom.exceptions.AlreadyDefinedException;
import custom.exceptions.NotRecognizedException;
import parser.SimpleParser.SimpleParser;
import simple.chore.stuctures.Pid;
import stateful.chore.structures.L;
import type.checking.structures.ProcedureType;
import type.checking.structures.*;

import java.util.*;


public class ParseTreeConversion {

    /**
     *
     * @param start ProgramContext, is part of the parser tree starting at rule program.
     * @param prefixFunc List<ProcedureType>, list to return.
     * @return List<ProcedureType>, List of procedures.
     * @throws Exception is thrown when an error occurred.
     */
    public static List<ProcedureType> interfaceHandling(SimpleParser.ProgramContext start, List<ProcedureType> prefixFunc) throws Exception {
        if (start != null && start.procedure != null){
            // Get the procedure name from the parse tree
            String procedureName = start.procedure.getText();
            // Create the procedure type
            prefixFunc.add(new ProcedureType(procedureName, getInput(start.prefix(), new LinkedHashMap<>()), getOutput(start.procReturn(),new ArrayList<>())));
            return interfaceHandling(start.program(), prefixFunc);
        }
        return prefixFunc;
    }

    /**
     *
     * @param prefix PrefixContext, is part of the parser tree starting at rule program.
     * @param input Map<Pid, Map<String, DataType>>, the processes and their functions/variables. Map to return.
     * @return Map<Pid, Map<String, DataType>>, containing the processes name and the variables and function for that process.
     * @throws Exception is thrown when an error occurred.
     */
    public static Map<Pid, Map<String, DataType>> getInput(SimpleParser.PrefixContext prefix, Map<Pid, Map<String, DataType>> input ) throws Exception {
        if (prefix == null || prefix.process == null) { // Base case
            return input;
        }
        // Get the definition of the procedure
        Map<String,DataType> typeMap = getDefinitions(prefix.definition(), new LinkedHashMap<>());
        if (input.get(new Pid(prefix.process.getText())) != null){ // Checks if procedure name already used
            throw new AlreadyDefinedException("Process "+prefix.process.getText()+" already defined");
        }
        input.put(new Pid(prefix.process.getText()),typeMap); // Add the procedure to the map
        return getInput(prefix.prefix(),input);
    }

    /**
     *
     * @param output ProcReturnContext, is part of the parser tree starting at rule procReturn.
     * @param list List<L>, the list to return.
     * @return List<L>, list of the record L.
     * @throws Exception is thrown when an error occurred.
     */
    public static List<L> getOutput(SimpleParser.ProcReturnContext output, List<L> list) throws Exception {
        if (output == null || output.procReturnValues() == null){ // Base case
            return list;
        }
        List<DataType> typeList = new ArrayList<>();
        // Go through each return value
        for (int i = 0; i < output.procReturnValues().WORD().size(); i++){
            // Get the type of the value
            DataType type = getDatatypeFromString(output.procReturnValues().WORD().get(i).getText());
            if (type == null){ // Checks that it is a defined type
                throw new NotRecognizedException("Datatype for output of process "+output.process.getText()+ "not recognized");
            }
            typeList.add(type);
        }
        // Puts the values with the process they are located at.
        list.add(new L(new Pid(output.process.getText()),typeList));
        return getOutput(output.procReturn(),list);
    }

    /**
     *
     * @param def DefinitionContext, is part of the parser tree starting at rule procReturn.
     * @param map Map<String, DataType>, map of function/variables and their types. Map to be returned.
     * @return Map<String, DataType>, map of variable/function names and their types.
     */
    public static Map<String, DataType> getDefinitions(SimpleParser.DefinitionContext def, Map<String, DataType> map) {
        if (def == null) { // Base case
            return map;
        }
        if (def.functiondefinition() != null) { // It is a function
            SimpleParser.FunctiondefinitionContext function = def.functiondefinition();
            // Create the function and add it to the map
            map.put(function.id.getText(),new FuncType(getInterfaceParameters(function.intParameters(), new ArrayList<>()), getDatatypeFromString(function.returnvalue.getText())));
            return getDefinitions(function.definition(),map);
        }
        if (def.variabledefinition() != null) { // It is a variable
            // Create the variable and add it to the map
            map.put(def.variabledefinition().id.getText(),getDatatypeFromString(def.variabledefinition().type.getText()));
            return getDefinitions(def.variabledefinition().definition(),map);
        }
        return map;
    }

    /**
     *
     * @param paraCon List of IntParameterContext, parameter of a function from the parse tree.
     * @param list List<DataType>, list of the parameters of the function. List to be returned.
     * @return List<DataType>, list of the parameters.
     */
    private static List<DataType> getInterfaceParameters(SimpleParser.IntParametersContext paraCon, List<DataType> list){
        if( paraCon == null || paraCon.intParameter() == null ){ // Base case
            return list;
        }
        // Get the type of the parameter and add it to the list.
        list.add(getDatatypeFromString(paraCon.intParameter().WORD().getText()));
        return getInterfaceParameters(paraCon.intParameters(),list);
    }


    /**
     *
     * @param type String.
     * @return Datatype, is the Datatype associated with the string given.
     */
    public static DataType getDatatypeFromString(String type){
        if (type.equalsIgnoreCase("int") || type.equalsIgnoreCase("integer")){
            return new IntType();
        } else if (type.equalsIgnoreCase("string")){
            return new StringType();
        } else if (type.equalsIgnoreCase("bool") || type.equalsIgnoreCase("boolean")) {
            return new BoolType();
        } else if (type.equalsIgnoreCase("double")){
            return new DoubleType();
        }
        return null;
    }



}
