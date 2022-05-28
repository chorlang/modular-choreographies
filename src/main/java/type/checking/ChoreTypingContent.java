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
import simple.chore.stuctures.*;
import stateful.chore.structures.L;
import stateful.chore.structures.N;
import type.checking.structures.ProcedureType;
import type.checking.structures.*;

import java.util.*;

public class ChoreTypingContent {

    /**
     *
     * @param procedureTypes List<ProcedureType>, list of information about each procedure.
     * @return Map<String, Pairs<Map<Pid, Map<String,DataType>>, List<L>>>, the gamma
     * @throws Exception Is thrown when a process is defined twice.
     */
     public static Map<String, Pairs<Map<Pid, Map<String,DataType>>, List<L>>> createGlobalGamma(List<ProcedureType> procedureTypes) throws Exception {
        Map<String, Pairs<Map<Pid, Map<String,DataType>>, List<L>>> gamma = new HashMap<>();
        // Go through each procedure and adds its information to the gamma.
        for (ProcedureType procedureType : procedureTypes){
            Pairs<Map<Pid, Map<String, DataType>>, List<L>> inputAndOutput = new Pairs<>(procedureType.input(),procedureType.output());
            Pairs<Map<Pid, Map<String, DataType>>, List<L>> result = gamma.putIfAbsent(procedureType.name(),inputAndOutput);
            if (result != null){ // Is thrown if a procedure has the same name as another procedure.
                throw new AlreadyDefinedException("Procedure ".concat(procedureType.name()).concat(" already exists"));
            }
        }
        return gamma;
    }

    /**
     *
     * @param e Empty object, is nothing.
     */
    public static void nilRule(Empty e){ }

    /**
     *
     * @param process Pid, a process.
     * @param input Map<Pid, Map<String, DataType>>, the processes in the procedure.
     * @param procedure String, the name of the procedure.
     * @throws Exception is thrown if a process does not exist in the input.
     */
    private static void processGiven(Pid process,  Map<Pid, Map<String, DataType>> input, String procedure) throws Exception {
        if (input.get(process) == null){
            throw new NotDefinedException("Process "+process.name()+" not defined in procedure "+procedure);
        }
    }

    /**
     *
     * @param statefulCom StatefulCom object, contains the information of the stateful communication between two processes.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output.
     * @param procedure String, the name of the procedure.
     * @throws Exception Thrown when the type of the expression does not match the assigned type in the Gamma.
     */
    public static void comRule(StatefulCom statefulCom, Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma, String procedure) throws Exception {
        // Find the type of the expression
        DataType eType = LocalTypingContent.typeCheck(statefulCom.e(),gamma.get(procedure), statefulCom.sender());
        // Check the processes exist in the procedure
        processGiven(statefulCom.sender(),gamma.get(procedure).first(), procedure);
        processGiven(statefulCom.receiver(),gamma.get(procedure).first(), procedure);
        // Get the type of the variable
        DataType varType = gamma.get(procedure).first().get(statefulCom.receiver()).get(statefulCom.varName());
        if (varType == null){ // Check that the variable is defined
            throw new NotDefinedException("Variable in communication between "+statefulCom.sender().name()+" and "+statefulCom.receiver().name()+" not defined.");
        }
        // Check that the variable type matches the expression type
        if (!(varType.equals(eType))){
            throw new UnexpectedTypeException("Type for defined variable does not match type of e()");
        }
        typeCheck(procedure, gamma,statefulCom.continuation());
    }


    /**
     *
     * @param statefulComVar StatefulComVar, contains the information of the communication between two processes, where the variable is declared.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output.
     * @param procedure String, the name of the procedure.
     * @throws Exception Thrown when the type of the expression does not match the variable type.
     */
    public static void comVarRule(StatefulComVar statefulComVar, Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma, String procedure) throws Exception {
        // Find the type of the expression
        DataType eType = LocalTypingContent.typeCheck(statefulComVar.e(),gamma.get(procedure), new Pid(statefulComVar.sender().name()));
        Pairs<Map<Pid, Map<String, DataType>>, List<L>> rightTypeList = gamma.get(procedure);
        // Check the processes exist in the procedure
        processGiven(statefulComVar.sender(),rightTypeList.first(), procedure);
        processGiven(statefulComVar.receiver(),rightTypeList.first(), procedure);
        // Check that the variable type matches the expression type
        if (!(statefulComVar.type().equals(eType))){
            throw new UnexpectedTypeException("Type defined for variable does not match type of e()");
        }
        // Add the variable to the gamma
        Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> copy = copyGamma(statefulComVar.receiver(), statefulComVar.varName(), statefulComVar.type(),procedure,gamma);
        typeCheck(procedure, copy,statefulComVar.continuation());
    }

    /**
     *
     * @param process Pid, the process the variable to be added is located at.
     * @param name String, the name of the variable to be added.
     * @param type DataType, the type of the variable to be added.
     * @param procedure String, the procedure the process is in.
     * @param original Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, the original gamma.
     * @return Map<String, Pairs<Map<Pid, Map  <String, DataType>>, List<L>>>, a copy of the gamma with the variable added.
     */
    public static Map<String, Pairs<Map<Pid, Map  <String, DataType>>, List<L>>> copyGamma(Pid process, String name, DataType type, String procedure, Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> original) {
        Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> copy = new HashMap<>();
        // Go through the gamma
        for (Map.Entry<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> entry : original.entrySet()){
            // Find the correct procedure
            if (entry.getKey().equals(procedure)){
                Map<Pid, Map<String, DataType>> newLinedMap = new LinkedHashMap<>();
                // Go through the processes in the procedure
                for (Map.Entry<Pid, Map<String, DataType>> processes : entry.getValue().first().entrySet()){
                    Map<String, DataType> newLinkedDataMap = new LinkedHashMap<>();
                    // Add all the variables/function in the process
                    for (Map.Entry<String,DataType> variables : processes.getValue().entrySet()){
                        newLinkedDataMap.put(variables.getKey(), variables.getValue());
                    }
                    // If it is the correct process add the new variable
                    if (processes.getKey().equals(process)){
                        newLinkedDataMap.put(name,type);
                    }
                    // Add the process map to the procedure
                    newLinedMap.put(processes.getKey(), newLinkedDataMap);
                }
                // Add the procedure to the copy of the gamma
                copy.put(entry.getKey(), new Pairs<>(newLinedMap,new ArrayList<>(entry.getValue().second())));
            } else{ // Is not the correct procedure
                Map<Pid, Map<String, DataType>> newLinedMap = new LinkedHashMap<>();
                // Go through the processes in the procedure
                for (Map.Entry<Pid, Map<String, DataType>> processes : entry.getValue().first().entrySet()){
                    Map<String, DataType> newLinkedDataMap = new LinkedHashMap<>();
                    // Add all the variables/function in the process
                    for (Map.Entry<String,DataType> variables : processes.getValue().entrySet()){
                        newLinkedDataMap.put(variables.getKey(), variables.getValue());
                    }
                    // Add the process map to the procedure
                    newLinedMap.put(processes.getKey(), newLinkedDataMap);
                }
                // Add the process map to the procedure
                copy.put(entry.getKey(), new Pairs<>(newLinedMap,new ArrayList<>(entry.getValue().second())));
            }
        }
        return copy;
    }


    /**
     *
     * @param r Return, recursive structure of the instruction Return.
     * @param gamma Pairs<Map<Pid, Map<String, DataType>>, List<L>>, the information of the procedure we are in.
     * @param procedure String, the name of the procedure.
     * @throws Exception is thrown if a type or process does not match the expected.
     */
    public static void returnRule(Return r, Pairs<Map<Pid, Map<String, DataType>>, List<L>> gamma, String procedure) throws Exception {
        List<L> value = gamma.second();
        // Check that all the processes are defined in the procedure
        for (N val : r.returnValues()){
            processGiven(val.process(),gamma.first(), procedure);
        }
        // Go through all the return values
        for (int i = 0; i < value.size(); i++){
            for (int j = 0; j < value.get(i).types().size(); j++){
                // Get the type of the return value
                DataType type = LocalTypingContent.typeCheck(r.returnValues().get(i).value().get(j),gamma,r.returnValues().get(i).process());
                if (!(value.get(i).types().get(j).equals(type))){ // Check that the type is the expected type
                    if (type!= null){
                        throw new UnexpectedTypeException("Type of return does not match the expected type");
                    }
                } else if (!(value.get(i).process().equals(r.returnValues().get(i).process()))){ // check that it is the correct process.
                    throw new UnexpectedInputException("The process in the return statement does not match expected process");
                }
            }
        }
    }

    /**
     *
     * @param p ProcInvoke, recursive structure of the instruction ProcInvoke.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output.
     * @param procedure String, the name of the procedure.
     * @throws Exception is thrown if the procedures does not have the same number of processes.
     */
    public static void invokeRule(ProcInvoke p, Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma, String procedure) throws Exception {
        Map<String, String> renaming = new HashMap<>();
        // Create a renaming map, for when processes of other names but same structure is used.
        if (p.arguments().size() == gamma.get(procedure).first().size()){
            Set<Pid> s2 = gamma.get(p.procedure().name()).first().keySet();
            Iterator<Pid> it2 = s2.iterator();
            for (N process : p.arguments()){
                String processName = it2.next().name();
                renaming.put(process.process().name(),processName); // the procedure we are in -> the procedure invoked
                renaming.put(processName,process.process().name());
            }
        } else {
            throw new ProcessMismatchException("The procedures "+p.procedure().name()+" and "+procedure+" does not have the same number of processes");
        }
        checkVariables(p,gamma,procedure,renaming);
        checkArguments(p,gamma,procedure,renaming);
        typeCheck(procedure,gamma,p.continuation());
    }

    /**
     *
     * @param p ProcInvoke, recursive structure of the instruction ProcInvoke.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma, has all the procedures, and their input and output.
     * @param procedure String, the name of the procedure.
     * @param renaming Map<String, String>, maps the original process names with the given process names.
     * @throws Exception is thrown if the variables does not match the expected.
     */
    private static void checkVariables(ProcInvoke p, Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma, String procedure, Map<String, String> renaming) throws Exception {
        if (p.variables().size() == 0){ // There are no variables
            if (gamma.get(p.procedure().name()).second().size() != 0){ // Checks that the procedure does not return
                throw new UnexpectedInputException("Procedure "+p.procedure().name()+" does not return void");
            }
        } else { // There are variables
            if (gamma.get(p.procedure().name()).second().size() == 0){ // Checks if the procedure does not return
                throw new UnexpectedInputException("Procedure "+p.procedure().name()+" returns void");
            }
            // Go through the processes of the variables
            for (int i = 0; i < p.variables().size(); i++) {
                // Check that the processes match
                if (!(gamma.get(p.procedure().name()).second().get(i).process().name().equals(renaming.get(p.variables().get(i).process().name())))){
                    throw new ProcessMismatchException("Mismatch of processes in invoke in procedure "+procedure);
                }
                // Go through the variables
                for (int j = 0; j < p.variables().get(i).value().size(); j++){
                    // Get the type of the variable
                    DataType type = LocalTypingContent.typeCheck(p.variables().get(i).value().get(j),gamma.get(procedure),p.variables().get(i).process());
                    // Check that the expected type matches the variable type
                    if (!(gamma.get(p.procedure().name()).second().get(i).types().get(j).equals(type))) {
                        throw new UnexpectedTypeException("Type of variable does not match the expected type");
                    }
                }
            }
        }
    }

    /**
     *
     * @param p ProcInvoke, recursive structure of the instruction ProcInvoke.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output.
     * @param procedure String, the name of the procedure.
     * @param renaming Map<String, String>, maps the original process names with the given process names.
     * @throws Exception is thrown if the arguments does not match the expected.
     */
    private static void checkArguments(ProcInvoke p, Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma, String procedure, Map<String, String> renaming) throws Exception {
        int index = 0;
        // Check tha the number of arguments matches the expected number
        for (Map.Entry<Pid, Map<String, DataType>> entry : gamma.get(p.procedure().name()).first().entrySet()){
            if (entry.getValue().size() != p.arguments().get(index).value().size()){
                if (entry.getValue().size() == 0 && p.arguments().get(index).value() == null)
                    throw new SizeMismatchException("Number of arguments defined for procedure "+p.procedure().name()+" does not match required number");
            }
            index++;
        }
        int i = 0;
        // Go through the processes of the arguments
        for (Map.Entry<Pid, Map<String,DataType>> entry: p.procedure().input().entrySet()){
            // Check that the processes names match
            if (!(entry.getKey().name().equals(renaming.get(p.arguments().get(i).process().name())))){
                throw new ProcessMismatchException("Mismatch of processes in invoke of procedure "+p.procedure().name()+" in procedure "+procedure);
            }
            // Is the case where just a process is given as argument
            if (entry.getValue().size() == 0 && p.arguments().get(i).value() == null){
                continue;
            }
            int j = 0;
            // Go through the arguments
            for (Map.Entry<String,DataType> args : entry.getValue().entrySet()){
                // Get the type of the argument
                DataType type = LocalTypingContent.typeCheck(p.arguments().get(i).value().get(j),gamma.get(procedure), new Pid(renaming.get(entry.getKey().name())));
                // Get the expected type
                DataType rightType = args.getValue();
                // If the argument is a function
                if (p.arguments().get(i).value().get(j) instanceof FuncCall fCall){
                    // Get the function DataType
                    DataType fType = getDataType(fCall);
                    // If both the expected type and the argument is functions
                    if (rightType instanceof FuncType func && fType instanceof FuncType f) {
                        // Check that the number of parameters match
                        if (func.parameters().size() != f.parameters().size()) {
                            throw new SizeMismatchException("Size of parameters for function in invocation of procedure " + p.procedure().name() + " does not match the number expected");
                        }
                        // Go though the parameters and check that the types match
                        for (int n = 0; n < func.parameters().size(); n++) {
                            if (!(f.parameters().get(n).equals(func.parameters().get(n)))) {
                                throw new UnexpectedInputException("Function in parameter for invocation of procedure " + p.procedure().name() + " does not have the input expected");
                            }
                        }
                        // Set the expected type to be the return type of the function
                        rightType = func.returnType();
                    }
                }
                // Check that the expected type and actual type matches
                if (!(type.equals(rightType))) {
                    throw new UnexpectedTypeException("Type of argument number " + i + " does not match expected type in procedure " + p.procedure().name());
                }
                j++;
            }
            i++;
        }
    }

    /**
     *
     * @param i IfState, contains the information of the if-statement.
     * @param procedure String, the name of the procedure.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output.
     * @throws Exception thrown if the condition is not of type boolean
     */
    public static void ifRule(IfState i, String procedure, Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma) throws Exception {
        // Get the type of the conditional expression
        DataType type = LocalTypingContent.typeCheck(i.e(),gamma.get(procedure),i.process());
        // Check that it is of type boolean
        if (!(type instanceof BoolType)){
            throw new ConditionException("Condition in if-statement is not of type Boolean");
        }
        // Check the different branches
        typeCheck(procedure,gamma,i.then());
        typeCheck(procedure,gamma,i.else_());
        typeCheck(procedure,gamma,i.continuation());
    }

    /**
     *
     * @param d Declaration, contains the information of the declaration instruction.
     * @param procedure String, the name of the procedure.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output.
     * @throws Exception Thrown when the variable is already declared.
     */
    public static void declarationRule(Declaration d, String procedure, Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma) throws Exception {
        // Check if a variable of the same name in the same process already exists
        DataType exists = gamma.get(procedure).first().get(d.process()).get(d.variableName());
        if (exists != null){
            throw new AlreadyDefinedException("Variable with name "+d.variableName()+" already declared");
        }
        // Add the variable to the gamma
        Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> copy = copyGamma(d.process(),d.variableName(),d.type(),procedure,gamma);
        typeCheck(procedure,copy,d.continuation());
    }

    /**
     *
     * @param in Initialization, contains the information of the assignment instruction.
     * @param procedure String, the name of the procedure.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output.
     * @throws Exception Thrown then the variable is assigned to different type that what it was declared to.
     */
    public static void initializationRule(Initialization in, String procedure, Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma) throws Exception {
        // Get the type it was declared to
        DataType rightType = gamma.get(procedure).first().get(in.process()).get(in.varName());
        // Get the type of the expression
        DataType eType = LocalTypingContent.typeCheck(in.e(),gamma.get(procedure),in.process());
        // Check that it is assigned to the correct type
        if (!(rightType.equals(eType))){
            throw new UnexpectedTypeException("Variable "+in.varName()+" assigned to an unexpected type");
        }
        typeCheck(procedure,gamma,in.continuation());
    }

    /**
     *
     * @param inDe InitialDeclare, contains the information of the instruction where the declaration and initialization is combined.
     * @param procedure String, the name of the procedure.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output.
     * @throws Exception Thrown if the variable is already declared or that it is initialized to the wrong type.
     */
    public static void initialDeclareRule(InitialDeclare inDe, String procedure, Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma) throws Exception {
        // Check if the variable already exists
        DataType exists = gamma.get(procedure).first().get(inDe.process()).get(inDe.varName());
        if (exists != null){
            throw new AlreadyDefinedException("Variable with name "+inDe.varName()+" already declared");
        }
        // Add the variable to the gamma
        Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> copy = copyGamma(inDe.process(),inDe.varName(),inDe.type(),procedure,gamma);
        // Get the type it is declared to be
        DataType type = copy.get(procedure).first().get(inDe.process()).get(inDe.varName());
        // Get the type of the expression
        DataType eType = LocalTypingContent.typeCheck(inDe.e(),gamma.get(procedure),inDe.process());
        // Check that the type of the expression match the expected type
        if (!(type.equals(eType))){
            throw new UnexpectedTypeException("Variable "+inDe.varName()+" initialized to unexpected type");
        }
        typeCheck(procedure,copy,inDe.continuation());
    }

    /**
     *
     * @param sel Selection, contains the information of the selection statement.
     * @param procedure String, the name of the procedure.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output.
     * @throws Exception Thrown if the processes are not defined in the procedure
     */
    public static void selectRule(Selection sel, String procedure, Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma) throws Exception {
        // Check the processes are defined in the procedure
        processGiven(sel.sender(),gamma.get(procedure).first(),procedure);
        processGiven(sel.receiver(),gamma.get(procedure).first(),procedure);
        typeCheck(procedure,gamma,sel.continuation());
    }

    /**
     *
     * @param e Expr, expression object.
     * @return Datatype, is the Datatype object that matches the given Expr object.
     */
    public static DataType getDataType(Expr e){
        // Get the DataType that matches the expression
        switch (e){
            case BoolExpr b -> { return new BoolType(); }
            case IntExpr i -> { return new IntType(); }
            case StringExpr s -> { return new StringType(); }
            case DoubleExpr d -> { return new DoubleType(); }
            case VariableExpr v -> { return getDataType(v.value());}
            case FuncCall f -> {
                List<DataType> list = new ArrayList<>();
                for (Expr para : f.parameter()){
                    list.add(getDataType(para));
                }
                DataType returnType = getDataType(f.returnValue());
                return new FuncType(list,returnType);
            }
            case null -> { return null;}
        }
        return null;
    }

    /**
     *
     * @param procedure String, the name of the procedure.
     * @param gamma Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>>, has all the procedures, and their input and output.
     * @param c Choreography, a recursive structure that contains the choreography.
     * @throws Exception Throws the exceptions thrown by the rules used.
     */
    public static void typeCheck(String procedure, Map<String, Pairs<Map<Pid, Map<String, DataType>>, List<L>>> gamma, Choreography c) throws Exception {
        // Call the method that correspond to the instruction.
        switch (c){
            case Empty e -> nilRule(e);
            case StatefulCom s -> comRule(s,gamma, procedure);
            case StatefulComVar sv -> comVarRule(sv,gamma,procedure);
            case Com com -> { }
            case Return r -> returnRule(r, gamma.get(procedure), procedure);
            case ProcInvoke p -> invokeRule(p,gamma,procedure);
            case IfState i -> ifRule(i, procedure,gamma);
            case Declaration d -> declarationRule(d,procedure,gamma);
            case Initialization in -> initializationRule(in,procedure,gamma);
            case InitialDeclare inDe -> initialDeclareRule(inDe,procedure,gamma);
            case Selection sel -> selectRule(sel,procedure,gamma);
        }
    }
}
