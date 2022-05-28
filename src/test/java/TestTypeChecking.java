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


import custom.exceptions.AlreadyDefinedException;
import custom.exceptions.NotDeclaredException;
import custom.exceptions.NotDefinedException;
import custom.exceptions.UnexpectedTypeException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static base.Main.createChoralFile;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTypeChecking {

    @Test
    public void testStatefulParameterTypeError(){
        Assertions.assertThrows(UnexpectedTypeException.class, () -> {
            createChoralFile("./src/test/resources/files/TypeChecking/withParameterTypeError.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testVariableNotDefined(){
        Assertions.assertThrows(NotDeclaredException.class, () -> {
            createChoralFile("./src/test/resources/files/TypeChecking/variableNotDefined.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testCorrect() throws Exception {
        Path correctFile = Path.of( "src/test/resources/typeCheck_correct/correct/run.txt");

        createChoralFile("./src/test/resources/files/TypeChecking/Correct.txt","./src/test/resources/generated_results/");

        Path createdFile = Path.of("src/test/resources/generated_results/run.txt");

        List<String> correctList = new ArrayList<>();
        List<String> generatedList = new ArrayList<>();
        correctList.add(Files.readString(correctFile).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFile).replaceAll("\\s",""));
        assertEquals(correctList,generatedList);
    }

    @Test
    public void testSameProcessTwice(){
        Assertions.assertThrows(AlreadyDefinedException.class, () -> {
            createChoralFile("./src/test/resources/files/TypeChecking/sameProcessTwice.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testFunctionNotDefined(){
        Assertions.assertThrows(NotDefinedException.class, () -> {
            createChoralFile("./src/test/resources/files/TypeChecking/functionNotDefined.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testFunctionAsParameterDefined() throws Exception {
        Path correctFile = Path.of( "src/test/resources/typeCheck_correct/function_parameter/run.txt");

        createChoralFile("./src/test/resources/files/TypeChecking/FunctionAsParameterDefined.txt","./src/test/resources/generated_results/");

        Path createdFile = Path.of("src/test/resources/generated_results/run.txt");

        List<String> correctList = new ArrayList<>();
        List<String> generatedList = new ArrayList<>();
        correctList.add(Files.readString(correctFile).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFile).replaceAll("\\s",""));
        assertEquals(correctList,generatedList);
    }

    @Test
    public void testFunctionAsParameterNotDefined(){
        Assertions.assertThrows(NotDefinedException.class, () -> {
            createChoralFile("./src/test/resources/files/TypeChecking/functionAsParameterNotDefined.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testFunctionNotRightReturn(){
        Assertions.assertThrows(UnexpectedTypeException.class, () -> {
            createChoralFile("./src/test/resources/files/TypeChecking/functionNotRightReturn.txt", "./src/test/resources/generated_results/");
        });
    }

    @AfterEach
    public void afterAllCleanUp() throws IOException {
        File directory = new File("./src/test/resources/generated_results");
        FileUtils.cleanDirectory(directory);
    }

}
