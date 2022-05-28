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

import custom.exceptions.NotDefinedException;
import custom.exceptions.SizeMismatchException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static base.Main.createChoralFile;

public class TestStatefulChor {
    @Test
    public void testStatefulInt() throws Exception {
        Path correctFile = Path.of( "src/test/resources/stateful_correct/nonEmpty/int/run.txt");
        createChoralFile("./src/test/resources/files/Stateful/StatefulAdd.txt","./src/test/resources/generated_results/");
        Path createdFile = Path.of("src/test/resources/generated_results/run.txt");
        List<String> correctList = new ArrayList<>();
        List<String> generatedList = new ArrayList<>();
        correctList.add(Files.readString(correctFile).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFile).replaceAll("\\s",""));
        assertEquals(correctList,generatedList);
    }

    @Test
    public void testStatefulString() throws Exception {
        Path correctFile = Path.of( "src/test/resources/stateful_correct/nonEmpty/string/run.txt");

        createChoralFile("./src/test/resources/files/Stateful/StatefulStringParameter.txt","./src/test/resources/generated_results/");

        Path createdFile = Path.of("src/test/resources/generated_results/run.txt");

        List<String> correctList = new ArrayList<>();
        List<String> generatedList = new ArrayList<>();
        correctList.add(Files.readString(correctFile).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFile).replaceAll("\\s",""));
        assertEquals(correctList,generatedList);
    }

    @Test
    public void testStatefulBoolean() throws Exception {
        Path correctFile = Path.of( "src/test/resources/stateful_correct/nonEmpty/boolean/run.txt");

        createChoralFile("./src/test/resources/files/Stateful/StatefulBoolParameter.txt","./src/test/resources/generated_results/");

        Path createdFile = Path.of("src/test/resources/generated_results/run.txt");

        List<String> correctList = new ArrayList<>();
        List<String> generatedList = new ArrayList<>();
        correctList.add(Files.readString(correctFile).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFile).replaceAll("\\s",""));
        assertEquals(correctList,generatedList);
    }

    @Test
    public void testStatefulDouble() throws Exception {
        Path correctFile = Path.of( "src/test/resources/stateful_correct/nonEmpty/double/run.txt");

        createChoralFile("./src/test/resources/files/Stateful/StatefulDoubleParameter.txt","./src/test/resources/generated_results/");

        Path createdFile = Path.of("src/test/resources/generated_results/run.txt");

        List<String> correctList = new ArrayList<>();
        List<String> generatedList = new ArrayList<>();
        correctList.add(Files.readString(correctFile).replaceAll("\\s",""));
        generatedList.add(Files.readString(createdFile).replaceAll("\\s",""));
        assertEquals(correctList,generatedList);
    }

    @Test
    public void testEmptyStateful() throws Exception {
        Path correctFile = Path.of( "src/test/resources/stateful_correct/empty/StatefulEmptyCorrect.txt");
        createChoralFile("./src/test/resources/files/Stateful/statefulEmpty.txt","./src/test/resources/generated_results/");
        Path createdFile = Path.of("src/test/resources/generated_results/statefulEmpty.txt");
        assertEquals(Files.readString(correctFile).replaceAll("\\s",""),Files.readString(createdFile).replaceAll("\\s",""));
    }

    @Test
    public void testStatefulIDError(){
        Assertions.assertThrows(RuntimeException.class, () -> {
            createChoralFile("./src/test/resources/files/Stateful/withIDError.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testStatefulArrowError(){
        Assertions.assertThrows(RuntimeException.class, () -> {
            createChoralFile("./src/test/resources/files/Stateful/withArrowError.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testStatefulISEPError(){
        Assertions.assertThrows(RuntimeException.class, () -> {
            createChoralFile("./src/test/resources/files/Stateful/withISEPError.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testStatefulEError(){
        Assertions.assertThrows(RuntimeException.class, () -> {
            createChoralFile("./src/test/resources/files/Stateful/withEError.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testStatefulPeriodError(){
        Assertions.assertThrows(RuntimeException.class, () -> {
            createChoralFile("./src/test/resources/files/Stateful/withPeriodError.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testStatefulVariableError(){
        Assertions.assertThrows(RuntimeException.class, () -> {
            createChoralFile("./src/test/resources/files/Stateful/withVariableError.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testStatefulFunctionError(){
        Assertions.assertThrows(RuntimeException.class, () -> {
            createChoralFile("./src/test/resources/files/Stateful/withFunctionError.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testStatefulNumbParameterError(){
        Assertions.assertThrows(SizeMismatchException.class, () -> {
            createChoralFile("./src/test/resources/files/Stateful/withNumbParameterError.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testStatefulParameterError(){
        Assertions.assertThrows(NotDefinedException.class, () -> {
            createChoralFile("./src/test/resources/files/Stateful/withParameterError.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testStatefulFunctionNotInInterface(){
        Assertions.assertThrows(NotDefinedException.class, () -> {
            createChoralFile("./src/test/resources/files/Stateful/FunctionNotInInterfaceError.txt", "./src/test/resources/generated_results/");
        });
    }

    @AfterEach
    public void afterAllCleanUp() throws IOException {
        File directory = new File("./src/test/resources/generated_results");
        FileUtils.cleanDirectory(directory);
    }
}
