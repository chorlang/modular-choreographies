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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static base.Main.*;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.io.FileUtils;


public class TestSimpleChor {
    @Test
    public void testSimple() throws Exception {
        Path correctFile = Path.of( "src/test/resources/simple_correct/WE/run.txt");
        createChoralFile("./src/test/resources/files/Simple/WE.txt","./src/test/resources/generated_results/");
        Path createdFile = Path.of("src/test/resources/generated_results/run.txt");
        assertEquals(Files.readString(correctFile),Files.readString(createdFile));
    }

    @Test
    public void testSimpleEmpty() throws Exception {
        Path correctFile = Path.of( "src/test/resources/simple_correct/empty/SimpleEmpty.txt");
        createChoralFile("./src/test/resources/files/Simple/SimpleEmpty.txt","./src/test/resources/generated_results/");
        Path createdFile = Path.of("src/test/resources/generated_results/SimpleEmpty.txt");
        assertEquals(Files.readString(correctFile).replaceAll("\\s",""),Files.readString(createdFile).replaceAll("\\s",""));
    }

    @Test
    public void testSimpleArrowError(){
        Assertions.assertThrows(RuntimeException.class, () -> {
            createChoralFile("./src/test/resources/files/Simple/withArrowError.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testSimpleIDError(){
        Assertions.assertThrows(RuntimeException.class, () -> {
            createChoralFile("./src/test/resources/files/Simple/withIDError.txt", "./src/test/resources/generated_results/");
        });
    }

    @Test
    public void testSimpleISEPError(){
        Assertions.assertThrows(RuntimeException.class, () -> {
            createChoralFile("./src/test/resources/files/Simple/withISEPError.txt", "./src/test/resources/generated_results/");
        });
    }

    @AfterAll
    static void cleanUp() throws IOException {
        File directory = new File("./src/test/resources/generated_results");
        FileUtils.cleanDirectory(directory);
    }
}
