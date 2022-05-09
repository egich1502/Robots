package gui;

import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import gui.WindowStateController.*;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 */
public class MainApplicationFrame extends JFrame implements StateSaver {
    private final JDesktopPane desktopPane = new JDesktopPane();

    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        File jsonStatesFile = new File("windowStates.json");
        HashMap<String, HashMap> states = new HashMap<>();
        if (jsonStatesFile.exists() && !jsonStatesFile.isDirectory()) {
            states = WindowStateController.restoreState(jsonStatesFile);
        }

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        ArrayList<JInternalFrame> windows = new ArrayList<>();
        windows.add(logWindow);
        windows.add(gameWindow);

        if (states != null){
            for(JInternalFrame frame: windows){
                if (states.containsKey(frame.getTitle())){
                    HashMap currentWindowState = states.get(frame.getTitle());
                    WindowStateController.resetState(frame, currentWindowState);
                }
            }
        }
        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int reply = JOptionPane.showConfirmDialog(null,
                        "Really Quit?", "Quit", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    WindowStateController.saveState(WindowStateController.getState(windows));
                    System.exit(0);
                }
            }
        });
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

//    protected JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
//
//        //Set up the lone menu.
//        JMenu menu = new JMenu("Document");
//        menu.setMnemonic(KeyEvent.VK_D);
//        menuBar.add(menu);
//
//        //Set up the first menu item.
//        JMenuItem menuItem = new JMenuItem("New");
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
//
//        //Set up the second menu item.
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
//
//        return menuBar;
//    }

    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        Menu lookAndFeelMenu = new Menu("Режим отображения",
                "Управление режимом отображения приложения",
                KeyEvent.VK_V);

        lookAndFeelMenu.createDroppingMenu("Системная схема",
                (event) -> {
                    setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    this.invalidate();
                }, KeyEvent.VK_S);

        lookAndFeelMenu.createDroppingMenu("Универсальная схема", (event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        }, KeyEvent.VK_S);

        Menu testMenu = new Menu("Тесты", "Тестовые команды", KeyEvent.VK_T);

        testMenu.createDroppingMenu("Сообщение в лог", (event) -> {
            Logger.debug("Новая строка");
        }, KeyEvent.VK_S);

        Menu exitMenu = new Menu("Выход", "Выход из приложения", KeyEvent.VK_Q);

        exitMenu.createDroppingMenu("Выход", (event) -> {
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                    new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }, KeyEvent.VK_X);

        menuBar.add(lookAndFeelMenu.getMenu());
        menuBar.add(testMenu.getMenu());
        menuBar.add(exitMenu.getMenu());

        return menuBar;
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }

    @Override
    public Map<String, String> CreateSaveState(JInternalFrame frame) {
        Map<String, String> map = new HashMap<>();
        map.put(".width", String.valueOf(frame.getWidth()));
        map.put(".height", String.valueOf(frame.getHeight()));
        map.put(".location.x", String.valueOf(frame.getX()));
        map.put(".location.y", String.valueOf(frame.getY()));
        return map;
    }

    @Override
    public Map<String, String> RestoreStateFromFile() {
        Map<String, String> map = new HashMap<>();
        try {
            FileReader fileReader = new FileReader("save.txt");
            Scanner scanner = new Scanner(fileReader);
            while (scanner.hasNext()) {
                String[] abc = scanner.nextLine().split(" ");
                map.put(abc[0], abc[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public void CreateSaveFile(Map<String, String>[] maps) {
        try {
            File file = new File("save.txt");
            FileWriter fileWriter = new FileWriter("save.txt");
            for (Map<String, String> map : maps)
                for (Map.Entry<String, String> entry : map.entrySet())
                    fileWriter.write(entry.getKey() + " " + entry.getValue() + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
