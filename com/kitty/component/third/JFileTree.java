package com.kitty.component.third;

import java.awt.Component;
import java.awt.Cursor;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * <p>Title: OpenSwing</p>
 * <p>Description: JFileTree 文件树</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author <a href="mailto:sunkingxie@hotmail.com">SunKing</a>
 * @version 1.0
 */

public class JFileTree extends JTree implements Serializable {
    public static final FileSystemView fileSystemView = FileSystemView.getFileSystemView();

    DefaultTreeModel treeModel;

    /**
     * 只浏览目录的目录树
     */
    public JFileTree() {
        this(new DirFilter());
    }

    /**
     * 指定过滤器的文件树
     * @param filter FileFilter 指定过滤器
     */
    public JFileTree(java.io.FileFilter filter) {
        FileNode root = new FileNode(fileSystemView.getRoots()[0], filter);
        treeModel = new DefaultTreeModel(root);
        root.explore();
        treeModel.nodeStructureChanged(root);
        this.setModel(treeModel);
        addTreeExpansionListener(new JFileTreeExpandsionListener());
        setCellRenderer(new JFileTreeCellRenderer());
    }

    /**
     * 取得当前选择的节点
     * @return FileNode
     */
    public FileNode getSelectFileNode() {
        TreePath path = getSelectionPath();
        if (path == null || path.getLastPathComponent() == null) {
            return null;
        }
        return (FileNode) path.getLastPathComponent();
    }

    /**
     * 设置当前选择的节点
     * @param f FileNode
     * @throws Exception
     */
    public void setSelectFileNode(FileNode f) throws Exception {
        this.setSelectFile(f.getFile());
    }

    /**
     * 取得当前选择的文件或目录
     * @return File
     */
    public File getSelectFile() {
        FileNode node = getSelectFileNode();
        return node == null ? null : node.getFile();
    }

    /**
     * 设置当前选择的文件或目录
     * @param f File
     * @throws Exception
     */
    public void setSelectFile(File f) {
        FileNode node = this.expandFile(f);
        if (node != null) {
            TreePath path = new TreePath(node.getPath());
            this.scrollPathToVisible(path);
            this.setSelectionPath(path);
            this.repaint();
        }
    }

    /**
     * 展开指定的文件或目录
     * @param f File
     * @return FileNode
     * @throws Exception
     */
    public FileNode expandFile(File f) {
        if (!f.exists()) {
            return null;
        }
        Vector vTemp = new Vector();
        File fTemp = f;
        while (fTemp != null) {
            vTemp.add(fTemp);
            fTemp = fileSystemView.getParentDirectory(fTemp);
        }

        FileNode nParent = (FileNode) treeModel.getRoot();
        for (int i = vTemp.size() - 1; i >= 0; i--) {
            fTemp = (File) vTemp.get(i);
            nParent.explore();
            for (int j = 0; j < nParent.getChildCount(); j++) {
                FileNode nChild = (FileNode) nParent.getChildAt(j);
                if (nChild.getFile().equals(fTemp)) {
                    nParent = nChild;
                }
            }
        }
        return nParent;
    }

    /**
     *
     * <p>Title: OpenSwing</p>
     *
     * <p>Description: 文件树修饰器</p>
     *
     * <p>Copyright: Copyright (c) 2004</p>
     *
     * <p>Company: </p>
     *
     * @author <a href="mailto:sunkingxie@hotmail.com">SunKing</a>
     * @version 1.0
     */
    class JFileTreeCellRenderer extends DefaultTreeCellRenderer {
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            try {
                closedIcon = fileSystemView.getSystemIcon(((FileNode) value).getFile());
                openIcon = closedIcon;
                setIcon(closedIcon);
            } catch (Exception ex) {
            }
            return this;
        }
    }

    /**
     *
     * <p>Title: OpenSwing</p>
     *
     * <p>Description: 文件树展开事件监听器</p>
     *
     * <p>Copyright: Copyright (c) 2004</p>
     *
     * <p>Company: </p>
     *
     * @author <a href="mailto:sunkingxie@hotmail.com">SunKing</a>
     * @version 1.0
     */
    class JFileTreeExpandsionListener implements TreeExpansionListener {
        public JFileTreeExpandsionListener() {
        }

        public void treeExpanded(TreeExpansionEvent event) {
            TreePath path = event.getPath();
            if (path == null || path.getLastPathComponent() == null)
                return;
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            FileNode node = (FileNode) path.getLastPathComponent();
            node.explore();
            JTree tree = (JTree) event.getSource();
            DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
            treeModel.nodeStructureChanged(node);
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        public void treeCollapsed(TreeExpansionEvent event) {
        }
    }

    /**
     *
     * <p>Title: OpenSwing</p>
     *
     * <p>Description:文件节点 </p>
     *
     * <p>Copyright: Copyright (c) 2004</p>
     *
     * <p>Company: </p>
     *
     * @author <a href="mailto:sunkingxie@hotmail.com">SunKing</a>
     * @version 1.0
     */
    public static class FileNode extends DefaultMutableTreeNode {
        private boolean explored = false;

        private java.io.FileFilter filter = null;

        public FileNode(File file, java.io.FileFilter filter) {
            if (filter == null) {
                this.filter = new DirFilter();
            } else {
                this.filter = filter;
            }
            setUserObject(file);
        }

        public boolean getAllowsChildren() {
            return !isLeaf();
        }

        public boolean isLeaf() {
            return false;
        }

        public File getFile() {
            return (File) getUserObject();
        }

        public boolean isExplored() {
            return explored;
        }

        public void setExplored(boolean b) {
            explored = b;
        }

        public String toString() {
            if (getFile() instanceof File)
                return fileSystemView.getSystemDisplayName((File) getFile());
            else
                return getFile().toString();
        }

        /**
         * 展开节点
         */
        public void explore() {
            if (!explored) {
                explored = true;
                File file = getFile();
                //如果这里使用 file.listFiles(filter) 有BUG
                File[] children = file.listFiles();
                if (children == null || children.length == 0) {
                    return;
                }
                //过滤后排序,选加入排序后的目录, 再加入排序后的文件
                ArrayList listDir = new ArrayList();
                ArrayList listFile = new ArrayList();
                for (int i = 0; i < children.length; ++i) {
                    File f = children[i];
                    if (filter.accept(f)) {
                        if (f.isDirectory()) {
                            listDir.add(f);
                        } else {
                            listFile.add(f);
                        }
                    }
                }
                Collections.sort(listDir);
                Collections.sort(listFile);
                for (int i = 0; i < listDir.size(); i++) {
                    add(new FileNode((File) listDir.get(i), filter));
                }
                for (int i = 0; i < listFile.size(); i++) {
                    add(new FileNode((File) listFile.get(i), filter));
                }
            }
        }
    }

    /**
     *
     * <p>Title: OpenSwing</p>
     *
     * <p>Description:目录过滤器 </p>
     *
     * <p>Copyright: Copyright (c) 2004</p>
     *
     * <p>Company: </p>
     *
     * @author <a href="mailto:sunkingxie@hotmail.com">SunKing</a>
     * @version 1.0
     */
    public static class DirFilter implements java.io.FileFilter {
        public boolean accept(File pathname) {
            if (pathname.isDirectory()
                    && !pathname.getAbsolutePath().toLowerCase().endsWith(".lnk")) {
                return true;
            }
            return false;
        }
    }

}
