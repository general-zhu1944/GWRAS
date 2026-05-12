package com.kitty.radar.listener;

import java.io.File;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.apache.commons.jci.monitor.FilesystemAlterationListener;
import org.apache.commons.jci.monitor.FilesystemAlterationObserver;

import com.kitty.radar.RadarParams;
import com.kitty.radar.domain.ListElement;
import com.kitty.radar.gui.GUIManager;

public class FileHandler implements FilesystemAlterationListener {

    public static final Pattern p = Pattern
            .compile("(19|20)\\d{2}(0[1-9]|1[0-2])([0-2]\\d|3[01])[0-2]\\d[0-5]\\d");

    private Set deleteds = new HashSet();

    private Map createds = new TreeMap(new Comparator() {

        public int compare(Object o1, Object o2) {
            String s2 = (String) o2;
            return s2.compareTo((String) o1);
        }

    });

    private Runnable doStop;

    public FileHandler() {
        doStop = new Runnable() {

            public void run() {
                DefaultListModel model = (DefaultListModel) GUIManager.list.getModel();

                // 删除文件
                if (deleteds.size() > 0) {
                    for (int i = 0; i < model.getSize(); i++) {
                        if (deleteds.contains(((ListElement) model.get(i)).getLabel())) {
                            model.remove(i);
                            i--;
                        }
                    }
                    deleteds.clear();
                }

                // 添加文件
                if (createds.size() > 0) {
                    if (model.getSize() == 0) {
                        Iterator keys = createds.keySet().iterator();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            ListElement elem = new ListElement();
                            elem.setValue(key);
                            elem.setLabel((String) createds.get(key));
                            model.addElement(elem);
                        }
                    } else {
                        ListSelectionModel selModel = GUIManager.list.getSelectionModel();
                        Iterator keys = createds.keySet().iterator();
                        int pos = 0;
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            ListElement elem = new ListElement();
                            elem.setValue(key);
                            elem.setLabel((String) createds.get(key));
                            pos = insertListElement(selModel, model, elem, pos);
                        }
                    }
                    createds.clear();
                    if (RadarParams.auto_update) {
                        GUIManager.list.setSelectedIndex(0);
                    }
                }
            }

        };
    }

    public void onDirectoryChange(File file) {

    }

    public void onDirectoryCreate(File file) {

    }

    public void onDirectoryDelete(File file) {

    }

    public void onFileChange(File file) {

    }

    public void onFileCreate(File file) {
        String name = file.getName();
        String time = getTime(name);
        if (time != null) {
            createds.put(time, name);
        }
    }

    public void onFileDelete(File file) {
        deleteds.add(file.getName());
    }

    public void onStart(FilesystemAlterationObserver file) {

    }

    public void onStop(FilesystemAlterationObserver file) {
        try {
            SwingUtilities.invokeAndWait(doStop);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int insertListElement(ListSelectionModel selModel, DefaultListModel model,
            ListElement elem, int pos) {
        int size = model.getSize();
        String value = elem.getValue();
        int i;
        for (i = pos; i < size; i++) {
            if (value.compareTo(((ListElement) model.get(i)).getValue()) > 0) {
                model.add(i, elem);
                selModel.removeSelectionInterval(i, i);
                return i + 1;
            }
        }
        model.addElement(elem);
        return i + 1;
    }

    public static String getTime(String fileName) {
        Matcher m = p.matcher(fileName.replaceAll("\\.", "").replaceAll("_", ""));
        if (m.find()) {
            return m.group();
        }
        return null;
    }

}
