package net.digitaltsunami.tmeter.record;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import net.digitaltsunami.tmeter.NamedTimeTracker;
import net.digitaltsunami.tmeter.TimeTracker;
import net.digitaltsunami.tmeter.Timer;
import net.digitaltsunami.tmeter.TimerLogType;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileTimeRecorderTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    @Test
    public void testRecord() {
        Timer timer = new Timer("TEST");
        // Set timer output to byte array so the value can be captured.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileTimeRecorder recorder = new FileTimeRecorder(new PrintStream(out),TimerLogType.CSV);
        timer.setTimeRecorder(recorder);
        timer.start();
        timer.stop();
        String outputLine = out.toString();
        assertTrue("Failed to write to recorder", outputLine.length() > 0);
    }
    
    @Test
    public void testDefaultLogType() {
        FileTimeRecorder recorder = new FileTimeRecorder(System.out);
        assertEquals(TimerLogType.TEXT, recorder.getLogType());
    }

    @Test
    public void testTimerLogType() {
        FileTimeRecorder recorder = new FileTimeRecorder(System.out, TimerLogType.CSV);
        assertEquals(TimerLogType.CSV, recorder.getLogType());
    }
    
    @Test
    public void testFileNameConstructorDefaultType() throws FileNotFoundException, IOException {
        File outFolder = tempFolder.newFolder("testFolder");
        String fileName =  outFolder.getCanonicalPath() + File.separator + "testFileName.text";
        FileTimeRecorder recorder = new FileTimeRecorder(fileName);
        assertEquals(TimerLogType.TEXT, recorder.getLogType());
        
    }
       /*
    @Test
    public void testFileNameConstructorProvidedType() throws FileNotFoundException, IOException {
        Commented out for now until closure can be gauranteed.
       File outFolder = tempFolder.newFolder("testFolderCsv");
        
        String fileName =  outFolder.getCanonicalPath() + File.separator + "testFileName.csv";
        FileTimeRecorder recorder = new FileTimeRecorder(fileName, TimerLogType.CSV);
        assertEquals(TimerLogType.CSV, recorder.getLogType());
        NamedTimeTracker tt = TimeTracker.named("testFileRecorder");
        tt.setDefaultTimeRecorder(recorder);
        for (int i =0;i < 10 ; i++) {
            Timer timer = tt.startRecording("TEST");
	        timer.stop();
        }
        File results = new File(fileName);
        assertTrue("File should have been created", results.exists());
    }
        */
}
