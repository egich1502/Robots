package gui;

import javax.swing.*;
import java.util.Map;

public interface StateSaver {

     Map<String, String> CreateSaveState(JInternalFrame frame);

     Map<String, String> RestoreStateFromFile();

     void CreateSaveFile(Map<String, String>[] maps);
}
