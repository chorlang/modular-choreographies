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

import choral.ast.Name;
import choral.ast.body.*;
import choral.ast.type.FormalTypeParameter;
import choral.ast.type.FormalWorldParameter;
import choral.ast.type.TypeExpression;
import choral.ast.type.WorldArgument;
import choral.ast.visitors.PrettyPrinterVisitor;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static base.Main.createFullReturnPath;
import static base.ReturnFile.createReturnFile;
import static base.ReturnFile.writeToFile;

public class FunctionPrintStructure {

    /**
     *
     * @param size int, the number of parameters in the function.
     * @param returnPath String, the path where all result files are created.
     */
    public static void makeFunctionInterface(int size, String returnPath) {
        // Parameter: name
        Name interName = new Name("Function"+size);
        // Parameter: methods
        List<InterfaceMethodDefinition> methods = createMethods(size);
        // Parameter: worldParameters
        List<FormalWorldParameter> worldParameters = createWorlds();
        // Parameter: typeParameters
        List<FormalTypeParameter> typeParameters = createTypeParameters(size);
        choral.ast.body.Interface inter = new Interface(
                interName,
                worldParameters,
                typeParameters,
                List.of(),
                methods,
                List.of(),
                EnumSet.of(InterfaceModifier.PUBLIC),
                null
        );
        PrettyPrinterVisitor print = new PrettyPrinterVisitor();
        String returnText = print.visit(inter);
        String returnFileInter = createFullReturnPath(returnPath,"Function"+size);
        createReturnFile(returnFileInter);
        writeToFile(returnFileInter,returnText);
    }

    /**
     *
     * @return List of FormalWorldParameters, list of the different processes used in the interface.
     */
    private static List<FormalWorldParameter> createWorlds(){
        List<FormalWorldParameter> worldParameters = new ArrayList<>();
        Name pidName = new Name("A");
        FormalWorldParameter proc = new FormalWorldParameter(pidName);
        worldParameters.add(proc);
        return worldParameters;
    }

    /**
     *
     * @param size int, the number of parameters in the function.
     * @return List of FormalTypeParameters, the types used in the interface.
     */
    private static List<FormalTypeParameter> createTypeParameters(int size){
        List<FormalTypeParameter> typeParameters = new ArrayList<>();
        // Add a world and type for each parameter
        for (int i = 0; i < size; i++){
            List<FormalWorldParameter> worldParameters = new ArrayList<>();
            worldParameters.add(new FormalWorldParameter(new Name("A")));
            Name name = new Name("T"+ (i + 1));
            FormalTypeParameter type = new FormalTypeParameter(name,worldParameters,List.of(),List.of());
            typeParameters.add(type);
        }
        List<FormalWorldParameter> worldParameter = new ArrayList<>();
        // Add the return value type and world
        worldParameter.add(new FormalWorldParameter(new Name("A")));
        typeParameters.add(new FormalTypeParameter(new Name("R"), worldParameter,List.of(), List.of()));
        return typeParameters;
    }


    /**
     *
     * @param size int, the number of parameters in the function.
     * @return List of InterfaceMethodDefinition, list of methods of the interface.
     */
    private static List<InterfaceMethodDefinition> createMethods(int size){
        List<InterfaceMethodDefinition> methods = new ArrayList<>();
        List<FormalMethodParameter> formalList = new ArrayList<>();
        // Add a world and type for each parameter
        for (int i = 0; i < size; i++){
            List<WorldArgument> worldArgumentList = new ArrayList<>();
            worldArgumentList.add(new WorldArgument(new Name("A")));
            TypeExpression type = new TypeExpression(new Name("T"+(i+1)), worldArgumentList, List.of());
            FormalMethodParameter formalPara = new FormalMethodParameter(new Name("t"+(i+1)), type,List.of(), null);
            formalList.add(formalPara);
        }
        List<WorldArgument> worldArgumentList = new ArrayList<>();
        // Add the return value type and world
        worldArgumentList.add(new WorldArgument(new Name("A")));
        TypeExpression returnType = new TypeExpression(new Name("R"), worldArgumentList, List.of());
        // Create the apply method
        MethodSignature signature = new MethodSignature(new Name("apply"), List.of(), formalList, returnType, null);
        // Create the interface method
        InterfaceMethodDefinition interMeth = new InterfaceMethodDefinition(signature, List.of(), EnumSet.of(InterfaceMethodModifier.PUBLIC), null);
        methods.add(interMeth);
        return methods;
    }

}
