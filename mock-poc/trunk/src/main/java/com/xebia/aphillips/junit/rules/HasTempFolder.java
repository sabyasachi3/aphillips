package com.xebia.aphillips.junit.rules;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * The TemporaryFolder Rule allows creation of files and folders that are
 * guaranteed to be deleted when the test method finishes (whether it passes
 * or fails).
 */
public class HasTempFolder {
    private String tempFilePath;
    private String tempFolderPath;
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testUsingTempFolder() throws IOException {
        File createdFile = folder.newFile("myfile.txt");
        File createdFolder = folder.newFolder("subfolder");
        assertTrue("Expected file to exist", createdFile.exists());
        assertTrue("Expected folder to exist", createdFolder.exists());
        tempFilePath = createdFile.getPath();
        tempFolderPath = createdFolder.getPath();
    }
    
    @After
    public void checkTempFolder() throws IOException {
        if (new File(tempFilePath).exists()) {
            throw new AssertionError(String.format("'%s' exists?!?", tempFilePath));
        }
        
        if (new File(tempFolderPath).exists()) {
            throw new AssertionError(String.format("'%s' exists?!?", tempFolderPath));
        }
    }
}