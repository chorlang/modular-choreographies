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

package base;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ReturnFile {

    /**
     *
     * @param filename String, the name of the file
     * @return Boolean. Returns true if the file was created, false otherwise.
     */
    public static Boolean createReturnFile(String filename){
        try {
            File myObj = new File(filename);
            return myObj.createNewFile();
        } catch (IOException e) {
            System.out.println("An error occurred when creating the output file.");
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param fileName String, name of the file.
     * @param text String, the text that should be written to the file.
     */
    public static void writeToFile(String fileName, String text){
        try {
            FileWriter myWriter = new FileWriter(fileName);
            myWriter.write(text);
            myWriter.flush();
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred writing to the output file.");
            e.printStackTrace();
        }
    }
}
