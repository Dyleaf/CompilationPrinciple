package experment3;


public class LR1Control {
    public static final int SEGMENT_ERROR = 0;
    public static final int GRAMMAR_ERROR = 1;
    private LR1Window frame;
    private LR1model lr1model;

    public LR1Control(LR1Window frame) {
        this.frame = frame;

    }

    public void setGrammar(String str) {
        lr1model = new LR1model(str);

        String[] actionTableHead = (String[]) lr1model.getActionTAbleHead();
        String[][] actionTableItems = (String[][]) lr1model.getActionTable();
        frame.setActionTable(actionTableItems, actionTableHead);

        String[] gotoTableHead = lr1model.getGotoTAbleHead();
        String[][] gotoItems = lr1model.getGotoTable();
        frame.setGotoTable(gotoItems, gotoTableHead);
    }

    public void setDefaultGrammar() {

        String DefaultGrammar = "S->E\nE->E+T\nE->T\nT->T*F\nT->F\nF->(E)\nF->i";
//        String DefaultGrammar = "T->S\nS->AS\nS->b\nA->SA\nA->a";
//        String DefaultGrammar = "T->S\nS->BB\nB->aB\nB->b";
        frame.setGrammarText(DefaultGrammar);
    }

    public void AnalysisProgram(String str) {
        lr1model.setSentent(str);
        String[] stepTableHead = {"步骤", "状态", "符号", "输入串"};
        String[][] stepTableItems = lr1model.getResultTable();
        frame.setTable(stepTableItems, stepTableHead);
        if(!lr1model.isSuccess()){
            frame.showError();
        }
    }
}
