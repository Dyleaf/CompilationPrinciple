package Experiment2;

public class LL1Control {
    public static  final int SEGMENT_ERROR = 0;
    public static  final int GRAMMAR_ERROR = 1;
    private LL1Window frame;
    private LL1model ll1model;

    public LL1Control(LL1Window frame) {
        this.frame = frame;

    }

    public void setGrammar(String str) {
        ll1model = new LL1model();
        ll1model.setGrammar(str);

        String[] followTableHead = {"word", "follow集"};
        String[][] followTableItems = ll1model.getFollowTable();
        frame.setFollowTable(followTableItems, followTableHead);

        String[] firstTableHead = {"word", "first集"};
        String[][] firstItems = ll1model.getFirstTable();
        frame.setFirstTable(firstItems, firstTableHead);

        Character[] tableHead = ll1model.getAnalysisTableHead();
        String[][] tableItems = ll1model.getAnalysisTable();
        frame.setAnalysisTable(tableItems, tableHead);
        if (ll1model.isError()) {
            frame.showError(GRAMMAR_ERROR);
        }
    }

    public void setDefaultGrammar() {
        String DefaultGrammar2 = "E->TG\nG->+TG\nG->-TG\nG->ε\nT->FS\nS->*FS\nS->/FS\nS->ε\nF->(E)\nF->i";

        String DefaultGrammar = "E->TG\nG->+TG\nG->ε\nT->FU\nU->*FU\nU->ε\nF->(E)\nF->i";
        frame.setGrammarText(DefaultGrammar2);
    }

    public void AnalysisProgram(String str) {
        ll1model.setSentence(str);
        ll1model.analyzeProgram();
        if (ll1model.isError()) {
            frame.showError(SEGMENT_ERROR);
        } else {
            String[] stepTableHead = {"步骤", "分析表", "剩余输入串", "所用产生式", "过程"};
            String[][] stepTableItems = ll1model.getStepTable();
            frame.setTable(stepTableItems, stepTableHead);
        }

    }
}
