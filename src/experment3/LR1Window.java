package experment3;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class LR1Window extends JFrame implements ActionListener {


    private JTextArea grammarText;
    private JTextField senntenceText;

    private JLabel senntenceLabel;
    private JLabel grammarLabel;

    private JLabel resultLabel;
    private JLabel actionLabel;
    private JLabel gotoLabel;

    private JTable resultTable;
    private JTable actionTable;
    private JTable gotoTable;

    private JButton btnSetProgram;
    private JButton btngetFile;
    private JButton btnSetGrammer;
    private JButton btnInputDefaultProgrammar;

    private JPanel topPanel;
    private JPanel bottomTablePanel;
    private JPanel resultTablePanel;
    private JPanel centerPanel;
    private JPanel innerInputPanel;
    private JPanel actionTablePanel;
    private JPanel gotoTablePanel;
    private JPanel gotoInnerPanel;
    private JPanel actionInnerPanel;
    private JPanel topInputPanel;
    /**
     * 控制器
     */
    private LR1Control control;

    public LR1Window() throws HeadlessException {
        super();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        control = new LR1Control(this);
    }

    @Override
    protected void frameInit() {
        super.frameInit();

        setBounds(100, 0, 900, 720);
        setLayout(new BorderLayout(10, 10));

        grammarText = new JTextArea(30, 20);
        senntenceText = new JTextField(14); //设置宽度为14
        grammarLabel = new JLabel("输入的文法");
        resultLabel = new JLabel("分析结果");
        actionLabel = new JLabel("Action表");
        gotoLabel = new JLabel("Goto表");

        senntenceLabel = new JLabel("输入字符串");
        btnSetProgram = new JButton("输入");
        btngetFile = new JButton("打开文件");
        btnSetGrammer = new JButton("文法构建");
        btnInputDefaultProgrammar = new JButton("输入默认文法");
        btnInputDefaultProgrammar.addActionListener(this);
        btnSetGrammer.addActionListener(this);
        btnSetProgram.addActionListener(this);
        btngetFile.addActionListener(this);

        centerPanel = new JPanel(new BorderLayout(5, 5));
        bottomTablePanel = new JPanel(new BorderLayout(10, 10));
        resultTablePanel = new JPanel(new BorderLayout(5, 5));
        innerInputPanel = new JPanel(new GridLayout(0, 2));
        actionTablePanel = new JPanel(new BorderLayout(5, 5));
        gotoTablePanel = new JPanel(new BorderLayout(5, 5));
        gotoInnerPanel = new JPanel(new BorderLayout(5, 5));
        actionInnerPanel = new JPanel(new BorderLayout(5, 5));
        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));


        resultTable = new JTable();
        actionTable = new JTable();
        gotoTable = new JTable();
        initResultTable();

        setTitle("LR（1）分析");
        //顶部输入串区域
        topPanel.add(senntenceLabel);
        topPanel.add(senntenceText);
        topPanel.add(btnSetProgram);
        JLabel authorLabel = new JLabel("作者");
        JLabel authorNameLabel = new JLabel("陈智垚");
        topPanel.add(authorLabel);
        topPanel.add(authorNameLabel);

        //设置输入语法区
        topInputPanel = new JPanel(new BorderLayout()); //默认的布局是flowLayout
        topInputPanel.add(grammarLabel, BorderLayout.NORTH);
        topInputPanel.add(grammarText, BorderLayout.CENTER);
        innerInputPanel.add(btnSetGrammer);
        innerInputPanel.add(btnInputDefaultProgrammar);
        innerInputPanel.add(btngetFile);
        topInputPanel.add(innerInputPanel, BorderLayout.SOUTH);

        //设置结果表
        resultTablePanel.add(resultLabel, BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(resultTable);
        jScrollPane.setPreferredSize(new Dimension(180, 260));
        resultTablePanel.add(jScrollPane, BorderLayout.SOUTH);
        resultTablePanel.setPreferredSize(new Dimension(180, 270));

        //上部
        centerPanel.add(resultTablePanel, BorderLayout.CENTER);
        centerPanel.add(topInputPanel, BorderLayout.WEST);
        centerPanel.setPreferredSize(new Dimension(180, 300));

        //Action表
        actionTablePanel.add(actionLabel, BorderLayout.NORTH);
        JScrollPane jScrollPane1 = new JScrollPane(actionTable);
        jScrollPane1.setPreferredSize(new Dimension(550, 300));
        actionInnerPanel.add(jScrollPane1, BorderLayout.CENTER);
        actionTablePanel.add(actionInnerPanel, BorderLayout.CENTER);

        //Goto集
        gotoTablePanel.add(gotoLabel, BorderLayout.NORTH);
        JScrollPane jScrollPane3 = new JScrollPane(gotoTable);
        jScrollPane3.setPreferredSize(new Dimension(300, 300));
        gotoInnerPanel.add(jScrollPane3, BorderLayout.CENTER);
        gotoTablePanel.add(gotoInnerPanel, BorderLayout.CENTER);

        //底部
        bottomTablePanel.add(actionTablePanel, BorderLayout.WEST);
        bottomTablePanel.add(gotoTablePanel, BorderLayout.EAST);
        //添加全体
        add(topPanel, "North");
        add(centerPanel, "Center");
        add(bottomTablePanel, "South");
    }

    private void initResultTable(){
        String[] tableHead = {"步骤", "状态", "符号", "输入串"};
        String tableData[][] = {};
        DefaultTableModel defaultTableModel = new DefaultTableModel(tableData, tableHead);
        resultTable.setModel(defaultTableModel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnInputDefaultProgrammar)) {
            control.setDefaultGrammar();
        } else if (e.getSource().equals(btnSetGrammer)) {
            control.setGrammar(grammarText.getText().trim());
        } else if (e.getSource().equals(btnSetProgram)) {
            initResultTable();
            control.AnalysisProgram(senntenceText.getText().trim());
        }else if(e.getSource().equals(btngetFile)){
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jFileChooser.showDialog(new JLabel(), "选择");
            File file = jFileChooser.getSelectedFile();
            if(file.exists()){
                String fileContent = readFile(file);
                setGrammarText(fileContent);
                control.setGrammar(fileContent);
            }
        }
    }
    private String readFile(File file){
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(file));
            StringBuilder buf = new StringBuilder();
            String strline;
            while ((strline = buffer.readLine())!=null){
                buf.append(strline+"\n");
            }
            buf.deleteCharAt(buf.length()-1);
            return buf.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setGrammarText(String string) {
        grammarText.setText(string);
    }

    public void setTable(Object[][] tableItems, Object[] tableName) {
        DefaultTableModel model = new DefaultTableModel(tableItems, tableName);
        resultTable.setModel(model);
    }

    public void showError() {
        String tip = "输入的程序分析时发现错误";
        JOptionPane.showMessageDialog(null,
                tip, " 文法检查提示", JOptionPane.ERROR_MESSAGE);
    }

    public void setActionTable(Object[][] tableItems, Object[] tableName) {
        DefaultTableModel model = new DefaultTableModel(tableItems, tableName);
        System.out.print("genx");
        actionTable.setModel(model);
    }

    public void setGotoTable(Object[][] tableItems, Object[] tableName) {
        DefaultTableModel model = new DefaultTableModel(tableItems, tableName);
        gotoTable.setModel(model);
    }
}
