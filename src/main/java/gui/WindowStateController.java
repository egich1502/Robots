package gui;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class WindowStateController {

    static HashMap<String, HashMap> getState(ArrayList<JInternalFrame> frames) {
        HashMap<String, HashMap> frameInfo = new HashMap<>();
        for (JInternalFrame frame : frames) {
            HashMap<String, Object> frameStateInfo = new HashMap<>();
            frameStateInfo.put("width", frame.getWidth());
            frameStateInfo.put("height", frame.getHeight());
            frameStateInfo.put("x", frame.getX());
            frameStateInfo.put("y", frame.getY());
            frameStateInfo.put("isMaximized", frame.isMaximum());
            frameStateInfo.put("isIcon", frame.isIcon());
            frameInfo.put(frame.getTitle(), frameStateInfo);
        }
        return frameInfo;
    }

    static void saveState(HashMap<String, HashMap> windowInfo) {
        JSONObject json = new JSONObject(windowInfo);
        try {
            FileWriter fileWriter = new FileWriter("save.json");
            json.writeJSONString(fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static HashMap<String, HashMap> restoreState(File json) {
        JSONParser jsonParser = new JSONParser();
        try (FileReader fileReader = new FileReader(json)) {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);
            return new HashMap<String, HashMap>(jsonObject);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    static void resetState(JInternalFrame frame, HashMap currentState) {
        frame.setLocation((int) currentState.get("x"), (int) currentState.get("y"));
        frame.setSize((int) currentState.get("width"), (int) currentState.get("height"));
        try {
            frame.setMaximum((boolean) currentState.get("isMaximized"));
            frame.setIcon((boolean) currentState.get("isIcon"));
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }
}
