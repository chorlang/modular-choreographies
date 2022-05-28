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

import custom.exceptions.NotDeclaredException;
import custom.exceptions.NotInitializedException;
import simple.chore.stuctures.*;
import stateful.chore.structures.L;
import stateful.chore.structures.N;
import type.checking.structures.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitializationCheck {

    /**
     *
     * @param c Choreography, a recursive structure that contains the choreography.
     * @param gamma  Map<Pairs<Pid,String>,Boolean>, maps the variable and the process it is in to a boolean
     * @throws Exception Thrown when a variable have not been declared or not initialized.
     */
    public static void initializingCheck(Choreography c, Map<Pairs<Pid,String>,Boolean> gamma) throws Exception {
        // Call the method corresponding to the instruction.
        switch (c){
            case Initialization in -> initialization(in, gamma);
            case InitialDeclare inDe -> initialDeclare(inDe, gamma);
            case Declaration d -> declaration(d, gamma);
            case Empty e -> {}
            case Com com -> initializingCheck(com.continuation(),gamma);
            case IfState i -> {
                initializingCheck(i.then(),gamma);
                initializingCheck(i.else_(),gamma);
                initializingCheck(i.continuation(),gamma);
            }
            case ProcInvoke p -> procInvoke(p,gamma);
            case Return r -> return_(r,gamma);
            case StatefulCom s -> statfulCom(s, gamma);
            case StatefulComVar sv -> statefulComVar(sv,gamma);
            case Selection sel -> selection(sel,gamma);
        }
    }

    /**
     *
     * @param s StatefulComVar, contains the information of the communication between two processes, where the variable is declared.
     * @param gamma  Map<Pairs<Pid,String>,Boolean>, maps the variable and the process it is in to a boolean
     * @throws Exception Thrown when a variable have not been declared or not initialized.
     */
    private static void statefulComVar(StatefulComVar s, Map<Pairs<Pid,String>,Boolean> gamma) throws Exception {
        // If expression is a function
        if (s.e() instanceof FuncCall f ){
            // Check all parameters
            for (Expr expr : f.parameter()){
                // If they are variables check that they are initialized.
                if (expr instanceof VariableExpr v){
                    checkIfInitialized(s.sender(),v.name(),gamma);
                }
            }
        }
        // If expression is a variable check that it is initialized
        if (s.e() instanceof VariableExpr v){
            checkIfInitialized(s.sender(),v.name(),gamma);
        }
        // Add the variable declared and initialized to the gamma.
        Map<Pairs<Pid,String>,Boolean> copy = copyGamma(s.receiver(),s.varName(),Boolean.TRUE,gamma);
        initializingCheck(s.continuation(),copy);
    }

    /**
     *
     * @param s StatefulCom object, contains the information of the stateful communication between two processes.
     * @param gamma  Map<Pairs<Pid,String>,Boolean>, maps the variable and the process it is in to a boolean
     * @throws Exception Thrown when a variable have not been declared or not initialized.
     */
    private static void statfulCom(StatefulCom s, Map<Pairs<Pid,String>,Boolean> gamma) throws Exception {
        // If expression is a function
        if (s.e() instanceof FuncCall f ){
            // Check all parameters
            for (Expr expr : f.parameter()){
                // If they are variables check that they are initialized.
                if (expr instanceof VariableExpr v){
                    checkIfInitialized(s.sender(),v.name(),gamma);
                }
            }
        }
        // If expression is a variable check that it is initialized
        if (s.e() instanceof VariableExpr v){
            checkIfInitialized(s.sender(),v.name(),gamma);
        }
        // Check that the variable is declared
        checkIfDeclared(s.receiver(),s.varName(),gamma);
        // Initialize the variable
        Map<Pairs<Pid,String>,Boolean> newGamma = copyGamma(s.receiver(),s.varName(),Boolean.TRUE,gamma);
        initializingCheck(s.continuation(),newGamma);
    }

    /**
     *
     * @param r Return, recursive structure of the instruction Return.
     * @param gamma  Map<Pairs<Pid,String>,Boolean>, maps the variable and the process it is in to a boolean
     * @throws Exception Thrown when a variable have not been declared or not initialized.
     */
    private static void return_(Return r, Map<Pairs<Pid,String>,Boolean> gamma) throws Exception {
        // Check that if the return values are variable that they have been initialized
        for (N values : r.returnValues()){
            for (Expr expr : values.value()){
                if (expr instanceof VariableExpr v){
                    checkIfInitialized(values.process(),v.name(),gamma);
                }
            }
        }
    }

    /**
     *
     * @param p ProcInvoke, recursive structure of the instruction ProcInvoke.
     * @param gamma  Map<Pairs<Pid,String>,Boolean>, maps the variable and the process it is in to a boolean
     * @throws Exception Thrown when a variable have not been declared or not initialized.
     */
    private static void procInvoke(ProcInvoke p, Map<Pairs<Pid,String>,Boolean> gamma) throws Exception {
        Map<Pairs<Pid,String>,Boolean> newGamma = gamma;
        // Check that all variables are declared
        for (N var : p.variables()){
            for (Expr expr : var.value()){
                if (expr instanceof VariableExpr v){
                    checkIfDeclared(var.process(),v.name(),gamma);
                    newGamma = copyGamma(var.process(),v.name(),Boolean.TRUE,newGamma);
                }
            }
        }
        // Check that if arguments are variables that they are initialized
        for (N arg : p.arguments()){
            for (Expr expr : arg.value()){
                if (expr instanceof VariableExpr v){
                    checkIfInitialized(arg.process(),v.name(),gamma);
                }
            }
        }
        initializingCheck(p.continuation(),newGamma);
    }

    /**
     *
     * @param d Declaration, contains the information of the declaration instruction.
     * @param gamma  Map<Pairs<Pid,String>,Boolean>, maps the variable and the process it is in to a boolean
     * @throws Exception Thrown when a variable have not been declared or not initialized.
     */
    private static void declaration(Declaration d, Map<Pairs<Pid,String>,Boolean> gamma) throws Exception {
        // Add the variable to the gamma as declared.
        Map<Pairs<Pid,String>,Boolean> newGamma = copyGamma(d.process(),d.variableName(),Boolean.FALSE,gamma);
        initializingCheck(d.continuation(),newGamma);
    }

    /**
     *
     * @param inDe InitialDeclare, contains the information of the instruction where the declaration and initialization is combined.
     * @param gamma  Map<Pairs<Pid,String>,Boolean>, maps the variable and the process it is in to a boolean
     * @throws Exception Thrown when a variable have not been declared or not initialized.
     */
    private static void initialDeclare(InitialDeclare inDe, Map<Pairs<Pid,String>,Boolean> gamma) throws Exception {
        // Add the variable to the gamma as initialized.
        Map<Pairs<Pid,String>,Boolean> newGamma = copyGamma(inDe.process(),inDe.varName(),Boolean.TRUE,gamma);
        initializingCheck(inDe.continuation(),newGamma);
    }

    /**
     *
     * @param i Initialization, contains the information of the assignment instruction.
     * @param gamma  Map<Pairs<Pid,String>,Boolean>, maps the variable and the process it is in to a boolean
     * @throws Exception Thrown when a variable have not been declared or not initialized.
     */
    private static void initialization(Initialization i, Map<Pairs<Pid, String>,Boolean> gamma) throws Exception {
        // Check that the variable is declared
        checkIfDeclared(i.process(),i.varName(),gamma);
        // Add the variable to the gamma as initialized
        Map<Pairs<Pid,String>,Boolean> newGamma = copyGamma(i.process(),i.varName(),Boolean.TRUE,gamma);
        initializingCheck(i.continuation(),newGamma);
    }

    /**
     *
     * @param sel Selection, contains the information of the selection statement.
     * @param gamma  Map<Pairs<Pid,String>,Boolean>, maps the variable and the process it is in to a boolean
     * @throws Exception Thrown when a variable have not been declared or not initialized.
     */
    private static void selection(Selection sel, Map<Pairs<Pid,String>,Boolean> gamma) throws Exception {
        initializingCheck(sel.continuation(),gamma);
    }

    /**
     *
     * @param process Pid, the process the variable is located at.
     * @param varName String, the name of the variable
     * @param gamma  Map<Pairs<Pid,String>,Boolean>, maps the variable and the process it is in to a boolean
     * @throws Exception Thrown when a variable have not been declared or not initialized.
     */
    public static void checkIfInitialized(Pid process, String varName, Map<Pairs<Pid,String>,Boolean> gamma) throws Exception {
        Boolean value = gamma.get(new Pairs<>(process,varName));
        if (value == null){ // Check if variable is declared
            throw new NotDeclaredException("Variable "+varName+" not declared");
        }
        if (value == Boolean.FALSE){ // Check if variable is initialized
            throw new NotInitializedException("Variable "+varName+" not initialized");
        }
    }

    /**
     *
     * @param process Pid, the process the variable is located at.
     * @param varName String, the name of the variable
     * @param gamma  Map<Pairs<Pid,String>,Boolean>, maps the variable and the process it is in to a boolean
     * @throws Exception Thrown when a variable have not been declared.
     */
    public static void checkIfDeclared(Pid process, String varName, Map<Pairs<Pid,String>,Boolean> gamma) throws Exception {
        Boolean value = gamma.get(new Pairs<>(process,varName));
        if (value == null){ //Check if the variable is declared
            throw new NotDeclaredException("Variable "+varName+" not declared");
        }
    }

    /**
     *
     * @param process Pid, the process the variable is located at.
     * @param name String, the name of the variable
     * @param value Boolean, False if the variable is just declared, True if it is initialized
     * @param original Map<Pairs<Pid,String>,Boolean>, maps the variable and the process it is in to a boolean
     * @return Map<Pairs<Pid,String>, Boolean>, the new gamma with the added variable
     */
    public static Map<Pairs<Pid,String>, Boolean> copyGamma(Pid process, String name, Boolean value, Map<Pairs<Pid,String>,Boolean> original){
        Map<Pairs<Pid,String>,Boolean> copy = new HashMap<>();
        // Add all the variables already in the gamma
        for (Map.Entry<Pairs<Pid,String>,Boolean> entry : original.entrySet()){
            copy.put(new Pairs<>(entry.getKey().first(),entry.getKey().second()),entry.getValue());
        }
        // Add the new variable
        copy.put(new Pairs<>(process,name),value);
        return copy;
    }

    /**
     *
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output.
     * @return Map<Pairs<Pid,String>,Boolean>, maps the variable and the process it is in to a boolean
     */
    public static Map<Pairs<Pid,String>, Boolean> createGamma(Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma){
        Map<Pairs<Pid,String>,Boolean> copy = new HashMap<>();
        // Add what exists in the processes to the initialization gamma
        for (Map.Entry<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> entry : gamma.entrySet()){
            for (Map.Entry<Pid, Map<String, DataType>> processes : entry.getValue().first().entrySet()){
                for(Map.Entry<String,DataType> ids : processes.getValue().entrySet()){
                    copy.put(new Pairs<>(processes.getKey(),ids.getKey()),Boolean.TRUE);
                }
            }
        }
        return copy;
    }
}
