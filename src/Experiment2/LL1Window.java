package Experiment2;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LL1Window extends JFrame implements ActionListener {

    private JTextArea grammarText;
    private JTextField senntenceText;

    private JLabel senntenceLabel;
    private JLabel grammarLabel;
    private JLabel analyseLabel;
    private JLabel resultLabel;
    private JLabel firstLabel;
    private JLabel followLabel;

    private JTable analysisTable;
    private JTable resultTable;
    private JTable firstTable;
    private JTable followTable;

    private JButton btnSetProgram;
    private JButton btnSetGrammer;
    private JButton btnInputDefaultProgrammar;

    private JPanel topPanel;
    private JPanel bottomTablePanel;
    private JPanel resultTablePanel;
    private JPanel analyzeTablePanel;
    private JPanel centerPanel;
    private JPanel innerInputPanel;
    private JPanel firstTablePanel;
    private JPanel followTablePanel;
    private JPanel followInnerPanel;
    private JPanel firstInnerPanel;
    private JPanel topInputPanel;
    /**
     * 控制器
     */
    private LL1Control control;

    public LL1Window() throws HeadlessException {
        super();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        control = new LL1Control(this);
    }

    @Override
    protected void frameInit() {
        super.frameInit();

        setBounds(100, 0, 800, 720);
        String[] tableHead = {"步骤", "分析表", "剩余输入串", "所用产生式", "过程"};
        String tableData[][] = {};
        setLayout(new BorderLayout(10, 10));

        DefaultTableModel defaultTableModel = new DefaultTableModel(tableData, tableHead);

        grammarText = new JTextArea(30, 20);
        senntenceText = new JTextField(14); //设置宽度为14
        grammarLabel = new JLabel("输入的文法");
        analyseLabel = new JLabel("预测分析表");
        resultLabel = new JLabel("分析结果");
        firstLabel = new JLabel("first集");
        followLabel = new JLabel("follow集");

        senntenceLabel = new JLabel("输入字符串");
        btnSetProgram = new JButton("输入");
        btnSetGrammer = new JButton("文法构建");
        btnInputDefaultProgrammar = new JButton("输入默认文法");
        btnInputDefaultProgrammar.addActionListener(this);
        btnSetGrammer.addActionListener(this);
        btnSetProgram.addActionListener(this);


        centerPanel = new JPanel(new BorderLayout(5, 5));
        bottomTablePanel = new JPanel(new BorderLayout(10, 10));
        analyzeTablePanel = new JPanel(new BorderLayout(5, 5));
        resultTablePanel = new JPanel(new BorderLayout(5, 5));
        innerInputPanel = new JPanel(new GridLayout(0, 2));
        firstTablePanel = new JPanel(new BorderLayout(5, 5));
        followTablePanel = new JPanel(new BorderLayout(5, 5));
        followInnerPanel = new JPanel(new BorderLayout(5, 5));
        firstInnerPanel = new JPanel(new BorderLayout(5, 5));
        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));


        analysisTable = new JTable();
        resultTable = new JTable();
        firstTable = new JTable();
        followTable = new JTable();
        resultTable.setModel(defaultTableModel);

        setTitle("LL（1）分析");
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

        //预测分析表
        analyzeTablePanel.add(analyseLabel, BorderLayout.NORTH);
        JScrollPane jScrollPane2 = new JScrollPane(analysisTable);
        jScrollPane2.setPreferredSize(new Dimension(300, 300));
        analyzeTablePanel.add(jScrollPane2, BorderLayout.CENTER);

        //first集
        firstTablePanel.add(firstLabel, BorderLayout.NORTH);
        JScrollPane jScrollPane1 = new JScrollPane(firstTable);
        jScrollPane1.setPreferredSize(new Dimension(250, 300));
        firstInnerPanel.add(jScrollPane1, BorderLayout.CENTER);
        firstTablePanel.add(firstInnerPanel, BorderLayout.CENTER);

        //follow集
        followTablePanel.add(followLabel, BorderLayout.NORTH);
        JScrollPane jScrollPane3 = new JScrollPane(followTable);
        jScrollPane3.setPreferredSize(new Dimension(250, 300));
        followInnerPanel.add(jScrollPane3, BorderLayout.CENTER);
        followTablePanel.add(followInnerPanel, BorderLayout.CENTER);

        //底部
        bottomTablePanel.add(analyzeTablePanel, BorderLayout.CENTER);
        bottomTablePanel.add(firstTablePanel, BorderLayout.WEST);
        bottomTablePanel.add(followTablePanel, BorderLayout.EAST);
        //添加全体
        add(topPanel, "North");
        add(centerPanel, "Center");
        add(bottomTablePanel, "South");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnInputDefaultProgrammar)) {
            control.setDefaultGrammar();
        } else if (e.getSource().equals(btnSetGrammer)) {
            control.setGrammar(grammarText.getText().trim());
        } else if (e.getSource().equals(btnSetProgram)) {
            control.AnalysisProgram(senntenceText.getText().trim());
        }
    }

    public void setGrammarText(String string) {
        grammarText.setText(string);
    }

    public void setTable(Object[][] tableItems, Object[] tableName) {
        DefaultTableModel model = new DefaultTableModel(tableItems, tableName);
        resultTable.setModel(model);
    }

    public void showError(int type) {
        String tip = null;
        if (type == LL1Control.SEGMENT_ERROR) {
            tip = "输入的程序分析时发现错误";
        } else {
            tip = "输入的文法不是LL(1)文法";
        }
        JOptionPane.showMessageDialog(null,
                tip, " 文法检查提示", JOptionPane.ERROR_MESSAGE);
    }

    public void setAnalysisTable(Object[][] tableItems, Object[] tableName) {
        DefaultTableModel model = new DefaultTableModel(tableItems, tableName);
        analysisTable.setModel(model);
    }

    public void setFirstTable(Object[][] tableItems, Object[] tableName) {
        DefaultTableModel model = new DefaultTableModel(tableItems, tableName);
        firstTable.setModel(model);
    }

    public void setFollowTable(Object[][] tableItems, Object[] tableName) {
        DefaultTableModel model = new DefaultTableModel(tableItems, tableName);
        followTable.setModel(model);
    }
}
