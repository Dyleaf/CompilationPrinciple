package Experiment2;


import java.util.*;


class Segment {
    public Character pre; //表达
    public String follow; //
}

class Grammar {
    public static char emptyWord = 'ε';
    public List<Segment> prodeceList;
    public HashSet<Character> TSet = new HashSet<>();
    public HashSet<Character> UTSet = new HashSet<>();
    public HashMap<Character, HashSet<Character>> firstMap = new HashMap<>();
    public HashMap<Character, HashSet<Character>> followMap = new HashMap<>();
    public HashMap<Character, HashMap<Character, Segment>> AnalysisTable = new HashMap<>();

    public Grammar(List<Segment> prodeceList) {
        this.prodeceList = prodeceList;
        //初始化集合
        for (int i = 0; i < prodeceList.size(); i++) {
            UTSet.add(prodeceList.get(i).pre);
        }
        for (int i = 0; i < prodeceList.size(); i++) {
            String follow = prodeceList.get(i).follow;
            for (int j = 0; j < follow.length(); j++) {
                Character word = follow.charAt(j);
                if (UTSet.contains(word)) {
                    continue;
                }
                TSet.add(word);
            }
        }
    }

    /**
     * @param follow 产生式右边
     * @param begin  判断起始位置
     * @return
     */
    public String getWord(String follow, int begin) {
        if (follow.length() > begin + 1 && follow.charAt(begin + 1) == emptyWord)
            return follow.substring(begin, 2 + begin);
        else
            return follow.substring(begin, begin + 1);
    }

    public void setFirstSet() {
        //添加 终止符的first集
        Iterator it = TSet.iterator();
        while (it.hasNext()) {
            Character Tstr = (Character) it.next();
            firstMap.put(Tstr, new HashSet<>());
            firstMap.get(Tstr).add(Tstr);
        }

        Queue<String> UTQueue = new LinkedList<>();
        for (int i = 0; i < prodeceList.size(); i++) {
            String followStr = prodeceList.get(i).follow;
            Character UTStr = followStr.charAt(0);
            if (UTSet.contains(UTStr)) {
                UTQueue.offer(prodeceList.get(i).pre + followStr);
            } else {
                if (firstMap.get(prodeceList.get(i).pre) == null)
                    firstMap.put(prodeceList.get(i).pre, new HashSet<>());
                firstMap.get(prodeceList.get(i).pre).add(UTStr);
            }
        }//处理剩余的字符串
        while (!UTQueue.isEmpty()) {
            String pair = UTQueue.poll();
            Character pre, next;
            pre = pair.charAt(0);
            next = pair.charAt(1);
            //判是否右部具备first集
            boolean getFirst = (firstMap.get(next) == null) ? false : true;
            boolean succcess = false;
            boolean addNUll = false, hasNUll = false;

            if (getFirst) {
                if (firstMap.get(pre) == null)
                    firstMap.put(pre, new HashSet<>());//初始化
                if (firstMap.get(pre).contains(emptyWord))
                    addNUll = true;
                for (int i = 1; i < pair.length(); i++) {
                    next = pair.charAt(i);
                    if (TSet.contains(next)) //判断是否出现了Tset
                        break;
                    if (firstMap.get(next) != null) {
                        if (firstMap.get(next).size() == 1 && firstMap.get(next).contains(emptyWord))
                            hasNUll = true;
                        else {
                            hasNUll = false;
                            firstMap.get(pre).addAll(firstMap.get(next));
                            succcess = true;
                            break;
                        }
                    }
                }
                if (!addNUll && !hasNUll) firstMap.get(pre).remove(emptyWord);
                else {
                    firstMap.put(pre, new HashSet<>());
                    firstMap.get(pre).add(emptyWord);
                }
            }
            if (!succcess) {
                if (!addNUll)
                    firstMap.remove(pre);
                UTQueue.offer(pair); //不满足条件继续循环
            }
        }
    }

    // TODO: 17-10-14 目前处理的时候是认为第一个产生式的左部为开始符号
    public void setFollowSet() {
        HashMap<Character, Queue<Character>> waitSet = new HashMap<>(); //存放待定的集合关系
        followMap.put(prodeceList.get(0).pre, new HashSet<>());
        followMap.get(prodeceList.get(0).pre).add('#');
        Queue<Character> followqueue = new LinkedList<>();
        for (int i = 0; i < prodeceList.size(); i++) {
            Character pre = prodeceList.get(i).pre;
            String follow = prodeceList.get(i).follow;
            if (follow.length() > 1)
                for (int j = 0; j < follow.length(); j++) {
                    Character firstWord = follow.charAt(j);
                    Character nextWord = null;
                    if (UTSet.contains(firstWord)) {
                        //添加树节点
                        //判断是否处终止位置    A->aP  //todo p == word  A=pre
                        if (j + 1 == follow.length()) {
                            //加入等待队列
                            if (!firstWord.equals(pre)) {
                                if (waitSet.get(firstWord) == null)
                                    waitSet.put(firstWord, new LinkedList<>());
                                waitSet.get(firstWord).add(pre);
                                followqueue.offer(firstWord);
                            }
                        } else {
                            // TODO: 17-10-14  A->apb   b==next  判断是否添加A 一定添加first集
                            nextWord = follow.charAt(j + 1);
                            HashSet<Character> nextSet = new HashSet<>(firstMap.get(nextWord));
                            boolean addNULL = nextSet.contains(emptyWord);
                            // 添加first集合
                            if (followMap.get(firstWord) == null)
                                followMap.put(firstWord, new HashSet<>());
                            followMap.get(firstWord).addAll(nextSet);
                            followMap.get(firstWord).remove(emptyWord);
                            //判断b位于串尾且含有‘ ’
                            if (j + 2 == follow.length()
                                    && UTSet.contains(nextWord) && addNULL) {
                                //加入等待队列
                                if (waitSet.get(firstWord) == null)
                                    waitSet.put(firstWord, new LinkedList<>());
                                waitSet.get(firstWord).add(pre);
                                followqueue.offer(firstWord);
                            }
                        }
                    }
                }
        }
        while (!followqueue.isEmpty()) {

            Character curWord = followqueue.poll();
            //判断是否已经处理直到全部处理完成
            while (waitSet.get(curWord).size() == 0) {
                if (followqueue.size() > 0)
                    curWord = followqueue.poll();
                else break;
            }
            Queue waitQueue = waitSet.get(curWord);
            int size = waitQueue.size();

            while (size > 0) {
                Character wait = (Character) waitQueue.poll();
                size--;
                if (waitSet.get(wait) == null || waitSet.get(wait).size() == 0) {
                    HashSet<Character> set = new HashSet<>(followMap.get(wait));
                    if (followMap.get(curWord) == null)
                        followMap.put(curWord, new HashSet<>());
                    followMap.get(curWord).addAll(set);
                } else {
                    waitQueue.offer(wait);
                }
            }
            if (waitQueue.size() > 0) {
                followqueue.offer(curWord);
            }
        }
    }

    public void setSelect() {
        for (int i = 0; i < prodeceList.size(); i++) {
            Segment segment = prodeceList.get(i);
            Character firstWord = segment.follow.charAt(0);
            HashMap<Character, Segment> tableItems;
            boolean hasNULL = false; //判断是否产生式跟着一个空串

            if (AnalysisTable.get(segment.pre) == null)
                tableItems = new HashMap<>();
            else tableItems = AnalysisTable.get(segment.pre);

            Iterator it = firstMap.get(firstWord).iterator();
            while (it.hasNext()) {
                Character first = (Character) it.next();
                if (first.equals(emptyWord)) {
                    hasNULL = true;
                } else {
                    tableItems.put(first, segment);
                }
            }
            if (hasNULL) {
                Segment NULLproduce = new Segment();
                NULLproduce.pre = segment.pre;
                NULLproduce.follow = emptyWord + "";
                it = followMap.get(segment.pre).iterator();
                while (it.hasNext()) {
                    Character first = (Character) it.next();
                    tableItems.put(first, NULLproduce);
                }
            }
            AnalysisTable.put(segment.pre, tableItems);
        }
    }


}


public class LL1model {

    /**
     * 输入的程序
     */
    private String sentence;
    /**
     * 文法结构
     */
    public Grammar grammar = null;
    /**
     * 文法符号
     */
    private Stack<Character> symbolStack = new Stack<>();
    private boolean error = false;

    private int step = 0;

    private String[][] firstTable;
    private String[][] followTable;
    private String[][] stepTable;
    private String[][] analysisTable;
    private Character[] analysisTableHead;
    private ArrayList<ArrayList<String>> printRecorder = new ArrayList<>();

    public LL1model() {

    }

    //设置语法
    public void setGrammar(String programe) {
        List<Segment> prodeces = new ArrayList<>();
        int index = 0, i = 0;
        while (i >= 0 && i < programe.length()) {
            Segment prodece = new Segment();
            index = programe.indexOf("->", i);
            prodece.pre = programe.charAt(i);
            i = programe.indexOf('\n', index + 2);
            int followend = 0;
            if (i == -1) {
                followend = programe.length();
            } else {
                followend = i;
                i++;
            }
            prodece.follow = programe.substring(index + 2, followend);
            prodeces.add(prodece);
        }
        grammar = new Grammar(prodeces);
        grammar.setFirstSet();
        grammar.setFollowSet();
        grammar.setSelect();
        setFirstTable();
        setFollowTable();
        setAnalyzeTable();
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }


    public String[][] getStepTable() {
        return stepTable;
    }

    public String[][] getAnalysisTable() {
        return analysisTable;
    }

    public Character[] getAnalysisTableHead() {
        return analysisTableHead;
    }

    public String[][] getFirstTable() {
        return firstTable;
    }

    public String[][] getFollowTable() {
        return followTable;
    }

    public void setFirstTable() {
        firstTable = new String[grammar.firstMap.size()][2];
        Iterator keyIter = grammar.firstMap.keySet().iterator();
        Iterator entryIter = grammar.firstMap.entrySet().iterator();
        int i = 0;
        while (keyIter.hasNext()) {
            firstTable[i][1] = entryIter.next().toString();
            firstTable[i++][0] = keyIter.next().toString();
        }
    }

    public void setFollowTable() {
        followTable = new String[grammar.followMap.size()][2];
        Iterator keyIter = grammar.followMap.keySet().iterator();
        Iterator entryIter = grammar.followMap.entrySet().iterator();
        int i = 0;
        while (keyIter.hasNext()) {
            followTable[i][1] = entryIter.next().toString();
            followTable[i++][0] = keyIter.next().toString();
        }
    }

    public void setStepTable() {
        if (error) {
            return;
        }
        int row = printRecorder.size();
        int cols = printRecorder.get(0).size();
        stepTable = new String[row][cols];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < cols; j++)
                stepTable[i][j] = printRecorder.get(i).get(j);
        }
    }

    public void setAnalyzeTable() {
        if (grammar.UTSet.size() > grammar.followMap.size()) {
            error = true;
            //return;
        }
        int row = grammar.UTSet.size();
        HashSet<Character> T = grammar.TSet;
        T.remove(Grammar.emptyWord);
        int cols = T.size() + 2;
        Iterator it = T.iterator();
        Character[] Tset = new Character[cols];
        int i = 1;
        Tset[0] = ' ';
        while (it.hasNext()) {
            Tset[i++] = (Character) it.next();
        }
        Tset[i] = '#';
        Character[] UTset = new Character[row];
        i = 0;
        it = grammar.UTSet.iterator();
        while (it.hasNext()) {
            UTset[i++] = (Character) it.next();
        }
        analysisTableHead = Tset;
        analysisTable = new String[row][cols];
        for (i = 0; i < row; i++) {
            analysisTable[i][0] = UTset[i] + "";
            for (int j = 1; j < cols - 1; j++)
                if (grammar.AnalysisTable.get(UTset[i]).get(Tset[j]) != null) {
                    Segment segment = grammar.AnalysisTable.get(UTset[i]).get(Tset[j]);
                    analysisTable[i][j] = segment.pre + "->" + segment.follow;
                } else {
                    analysisTable[i][j] = "";
                }
        }
        for (int j = 1; j < row; j++) {
            if (grammar.firstMap.get(UTset[j]).contains(Grammar.emptyWord))
                analysisTable[j][cols - 1] = UTset[j] + "->" + Grammar.emptyWord;
            else {
                analysisTable[j][cols - 1] = "";
            }
        }
        checkoutAnalyseTable();
    }

    private void checkoutAnalyseTable() {
        int row = analysisTable.length;
        int cols = analysisTable[0].length;
        Set<String> set = new HashSet<>();
        for (int i = 1; i < cols; i++) {
            set.clear();
            for (int j = 0; j < row; j++) {
                try {
                    if (analysisTable[j][i].length() > 3) {
                        if (set.contains(analysisTable[j][i].charAt(3))) {
                            error = true;
                            break;
                        }
                        set.add(analysisTable[j][i].charAt(3) + "");
                    }
                } catch (Exception e) {
                    System.out.print("error in " + i + "  " + j);
                }
            }
        }
    }

    public void analyzeProgram() {
        symbolStack.push('#');
        symbolStack.push(grammar.prodeceList.get(0).pre);
        int cIndex = 0;
        printStepTable(cIndex, grammar.emptyWord, grammar.emptyWord, "初始化", false);
        while (true) {
            if (symbolStack.empty() || cIndex >= sentence.length()) {
                error = true;
                break;
            }
            Character a = sentence.charAt(cIndex);
            Character x = symbolStack.pop();
            // x 属于Vt
            {
                if (x == a) {
                    if (x == '#') {
                        //todo 成功处理
                        System.out.println("success");
                        setStepTable();
                        break;
                    } else {
                        //todo 弹出栈符号指针后移
                        cIndex++;
//                        printStepTable(cIndex, x, a, "POP", false);
                        printStepTable(cIndex, x, a, "GETNEXT(I)", false);
                    }
                } else {
                    if (grammar.UTSet.contains(x))
                        if (grammar.AnalysisTable.get(x).get(a) != null) {
                            // TODO: 17-10-15 替换栈顶符号
                            String replaceStr = grammar.AnalysisTable.get(x).get(a).follow;
                            String reset = "";
                            int length = replaceStr.length();
                            for (int i = 0; i < length; i++) {
                                Character ch = replaceStr.charAt(length - i - 1);
                                if (ch != grammar.emptyWord) {
                                    reset += ch;
                                    symbolStack.push(ch);
                                }
                            }
                            if (reset.length() == 0) {
                                printStepTable(cIndex, x, a, "POP" , true);
                            } else {
                                printStepTable(cIndex, x, a, "POP PUSH(" + reset + ")", true);
                            }
                        } else {
                            // TODO: 17-10-15错误处理
                            error = true;
                            printError(cIndex);
                            break;
                        }
                }

            }
        }
    }

    public boolean isError() {
        return error;
    }

    public void printStep(int index, Character x, Character a, boolean replace) {
        System.out.print(step + "\t\t");
        Iterator it = symbolStack.iterator();
        while (it.hasNext()) {
            System.out.print(it.next());
        }
        System.out.print("\t\t" + sentence.substring(index) + "\t\t");
        if (replace)
            System.out.println(grammar.AnalysisTable.get(x).get(a).pre + "->"
                    + grammar.AnalysisTable.get(x).get(a).follow);
        else
            System.out.print('\n');
        step++;
    }

    public void printStepTable(int index, Character x, Character a, String operation, boolean replace) {
        ArrayList<String> item = new ArrayList<>();
        item.add("" + step);

        StringBuilder stringBuilder = new StringBuilder();
        Iterator it = symbolStack.iterator();
        while (it.hasNext()) {
            stringBuilder.append(it.next());
        }
        String stackStr = stringBuilder.toString();
        item.add(stackStr);
        item.add(sentence.substring(index));
        if (replace)
            item.add(new String(grammar.AnalysisTable.get(x).get(a).pre + "->"
                    + grammar.AnalysisTable.get(x).get(a).follow));
        else
            item.add("");
        step++;
        item.add(operation);
        printRecorder.add(item);
    }

    private void printError(int index) {
        System.out.println("error index " + index);
    }
}