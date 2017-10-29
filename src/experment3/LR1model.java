package experment3;


import java.util.*;

/**
 * 项目
 */
class Project {
    String production; //项目
    HashSet<Character> expectation;

    public Project(Project project) {
        production = project.production;
        expectation = new HashSet<>(project.expectation);
    }

    public Project() {
        super();
        expectation = new HashSet<>();
        production = "";
    }

    @Override
    public String toString() {
        return "Project{" +
                "production='" + production + '\'' +
                ", expectation=" + expectation +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        Project other = (Project) obj;
        if (!production.equals(other.production) ||
                expectation.size() != other.expectation.size())
            return false;
        Iterator iterator = ((Project) obj).expectation.iterator();
        while (iterator.hasNext()) {
            if (!expectation.contains(iterator.next()))
                return false;
        }
        return true;
    }

}

class ProjectCollecion {
    public static int mark = 0;
    public ArrayList<Project> projects;
    public int collectionId;
    public String pre;
    public HashMap<Character, String> next;

    public ProjectCollecion() {
        super();
        projects = new ArrayList<>();
        next = new HashMap<>();
    }

    public void setProjects(ArrayList<Project> projects) {
        this.projects = projects;
    }

    public ArrayList<Project> getProjects() {
        return projects;
    }


    public void soft() {
        projects.sort(new Comparator<Project>() {
            @Override
            public int compare(Project o1, Project o2) {
                return o1.production.compareTo(o2.production);
            }
        });
    }

    private String printlist() {
        String str = "\n";
        for (int i = 0; i < projects.size(); i++) {
            str += projects.get(i).production + " " + projects.get(i).expectation + "\n";
        }
        return str;
    }

    @Override
    public String toString() {
        return "ProjectCollecion{" +
                "projects=" + printlist() +
                ", collectionId=" + collectionId +
                ", pre=" + pre +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        ProjectCollecion other = (ProjectCollecion) obj;
        if (other.projects.size() != projects.size())
            return false;
        for (int i = 0; i < projects.size(); i++) {
            if (!projects.get(i).equals(other.projects.get(i))) {
                return false;
            }
        }
        return true;
    }
}

class Sententce {
    Character pre;
    String follow;
}


public class LR1model {
    private int step = 0;    //比较步数
    private boolean success; //比较成功标志
    private Character END = 'S'; //终结符
    private Character emptyWord;  //空字符
    private String sentent; //待比较的句子
    private HashSet<Character> TSet;  //终结符
    private HashSet<Character> UTSet; //非终结符

    private ArrayList<Sententce> sententceList; //产生式集合
    private HashMap<Character, ArrayList<String>> sententMap; //产生式查询表
    private HashMap<Character, HashSet<Character>> firstMap; //first集

    private ArrayList<ProjectCollecion> projectCollecionArrayList; //项目集组
    private ArrayList<HashMap<Character, String>> ActionTable; //action表
    private ArrayList<HashMap<Character, String>> GoToTable; //goto表
    private ArrayList<ArrayList<String>> resultTable;
    private Stack<Integer> statusStack; //状态栈
    private Stack<Character> characterstack; //文法符号栈

    public LR1model(String grammar) {
        super();
        init(grammar);
    }

    private void setFirstSet() {
        firstMap = new HashMap<>();
        Iterator it = TSet.iterator();
        while (it.hasNext()) {
            Character Tstr = (Character) it.next();
            firstMap.put(Tstr, new HashSet<>());
            firstMap.get(Tstr).add(Tstr);
        }

        Queue<String> UTQueue = new LinkedList<>();
        for (int i = 0; i < sententceList.size(); i++) {
            String followStr = sententceList.get(i).follow;
            Character UTStr = followStr.charAt(0);
            if (UTSet.contains(UTStr)) {
                UTQueue.offer(sententceList.get(i).pre + followStr);
            } else {
                if (firstMap.get(sententceList.get(i).pre) == null)
                    firstMap.put(sententceList.get(i).pre, new HashSet<>());
                firstMap.get(sententceList.get(i).pre).add(UTStr);
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

    private void setTSet() throws Exception {
        if (UTSet == null) throw new Exception("终止符必须先定义");
        for (int i = 0; i < sententceList.size(); i++) {
            String follow = sententceList.get(i).follow;
            for (int j = 0; j < follow.length(); j++) {
                if (!UTSet.contains(follow.charAt(j))) {
                    TSet.add(follow.charAt(j));
                }
            }
        }
    }

    private void setUTSet() {
        for (int i = 0; i < sententceList.size(); i++) {
            UTSet.add(sententceList.get(i).pre);
        }
    }


    /**
     * 设置文法
     * @param grammar
     */
    private void setGrammarList(String grammar) {
        int index = 0, i = 0;
        while (i >= 0 && i < grammar.length()) {
            Sententce sententce = new Sententce();
            index = grammar.indexOf("->", i);
            sententce.pre = grammar.charAt(i);
            i = grammar.indexOf('\n', index + 2);
            int followend = 0;
            if (i == -1) {
                followend = grammar.length();
            } else {
                followend = i;
                i++;
            }
            sententce.follow = grammar.substring(index + 2, followend);
            sententceList.add(sententce);
            if (sententMap.get(sententce.pre) == null)
                sententMap.put(sententce.pre, new ArrayList<>());
            sententMap.get(sententce.pre).add(sententce.follow);
        }
    }

    /**初始化设置
     * @param grammar
     */
    public void init(String grammar) {
        TSet = new HashSet<>();
        UTSet = new HashSet<>();
        sententMap = new HashMap<>();
        sententceList = new ArrayList<>();
        projectCollecionArrayList  = new ArrayList<>();
        setGrammarList(grammar);
        setUTSet();
        END = sententceList.get(0).follow.charAt(
                sententceList.get(0).follow.length()-1);
        try {
            setTSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setFirstSet();
        TSet.add('#');
        initFirstcollection();
        setCollectionList();
        setLR1Table();
        printDebug();
    }

    private void printDebug(){
        for (int i = 0; i < projectCollecionArrayList.size(); i++) {
            System.out.println("项目集族"+i+"\n"+projectCollecionArrayList.get(i).toString()+"\n");
        }
    }

    /**
     * 设置输入的程序
     * @param sentent
     */
    public void setSentent(String sentent) {
        this.sentent = sentent;
        resultTable = null;
        startMainControl(sentent);
    }

    /**
     * 初始化项目集
     */
    private void initFirstcollection() {
        //添加第一个collection集合
        Project project = new Project();
        project.expectation.add('#');
        project.production = sententceList.get(0).pre + "->." + sententceList.get(0).follow;
        ProjectCollecion projectCollecion = new ProjectCollecion();
        projectCollecion.getProjects().add(project);
        projectCollecion.pre = " ";

        closureCollection(projectCollecion);
        projectCollecionArrayList.add(projectCollecion);

    }

    /**
     * 初始化整个项目集族
     */
    private void setCollectionList() {
        int index = 0;
        HashSet<Character> allCharSet = new HashSet<>(); //全体符号集合
        allCharSet.addAll(TSet);
        allCharSet.addAll(UTSet);
        allCharSet.add('#');

        //循环添加直到项目集族不再增加
        while (index < projectCollecionArrayList.size()) {
            ProjectCollecion collecion = projectCollecionArrayList.get(index);
            ProjectCollecion targetColection = null;
            int pointIndex;

            //开始产生新的项目集
            HashMap<Character, ProjectCollecion> nextProjectList = new HashMap<>();
            for (Project project : collecion.projects) {
                Character chFirst = null; //在点后面的第一个字符
                pointIndex = project.production.indexOf(".");
                if (pointIndex == project.production.length() - 1) {
                    continue;
                }
                chFirst = project.production.charAt(pointIndex + 1);
                if (nextProjectList.get(chFirst) == null)
                    nextProjectList.put(chFirst, new ProjectCollecion());
                Project project1 = new Project(project);
                project1.production = project1.production.replace("." + chFirst, chFirst + ".");
                nextProjectList.get(chFirst).projects.add(project1);
            }
            //对新产生的项目集使用goto进行扩张
            for (Character c : nextProjectList.keySet()) {
                targetColection = GO(nextProjectList.get(c), c); //得到闭包
                if (-1 == projectCollectionContain(targetColection)) {
                    targetColection.pre = index + "";
                    projectCollecionArrayList.add(targetColection);
                }
                //todo 初一移进状态
                if (TSet.contains(c)) {
                    collecion.next.put(c, "s" + projectCollectionContain(targetColection));
                } else if (UTSet.contains(c)) {
                    //todo 填充goto表
                    collecion.next.put(c, "" + projectCollectionContain(targetColection));
                }
            }
            index++;
        }
    }

    /**
     * 主控程序
     * @param sentence
     */
    public void startMainControl(String sentence) {
        resultTable = new ArrayList<>();
        statusStack = new Stack<>();
        characterstack = new Stack<>();
        statusStack.push(0);//初始状态
        characterstack.push('#');
        int i = 0;
        success = false;
        boolean error = false;
        step = 0; //初始化
        while (i < sentence.length()) {
            HashMap<Character, String> row = ActionTable.get(statusStack.peek()); //取得相应状态的一行转换表
            //循环遍历
            for (Character key : row.keySet()) {
                if (key == sentence.charAt(i)) {
                    //判断移进
                    if (row.get(key).charAt(0) == 's') {
                        characterstack.push(sentence.charAt(i));
                        statusStack.push(Integer.valueOf(row.get(key).substring(1)));
                        ArrayList<String> Item = new ArrayList<>();
                        Item.add(step+"");
                        Item.add(statusStack.toString());
                        Item.add(characterstack.toString());
                        Item.add(sentence.substring(i));
                        resultTable.add(Item);
                        step++;
                        System.out.println("移进结果"+characterstack.toString()+"字符 "+key+"   状态 "+statusStack.toString());
                        i++;
                        break;
                    } //判断规约
                    else if (row.get(key).charAt(0) == 'r') {
                        Sententce product = sententceList.get(row.get(key).charAt(1) - '0');
                        for (int j = 0; j < product.follow.length(); j++) {
                            statusStack.pop();
                            characterstack.pop();
                        }
                        characterstack.push(product.pre);
                        HashMap<Character, String> gotorow = GoToTable.get(statusStack.peek());
                        statusStack.push(Integer.valueOf(gotorow.get(product.pre)));
                        ArrayList<String> Item = new ArrayList<>();
                        Item.add(step+"");
                        Item.add(statusStack.toString());
                        Item.add(characterstack.toString());
                        Item.add(sentence.substring(i));
                        resultTable.add(Item);
                        step++;
                        System.out.println("规约结果"+characterstack.toString()+"表达式"+product.follow+"   状态"+statusStack.toString());
                        break;
                    } else if (row.get(key).length() == 3 && characterstack.peek() == END && characterstack.size() == 2
                            && sentence.charAt(i) == '#' && i == sentence.length()-1) {
                        success = true;
                        System.out.println("文法判断成功");
                        break;
                    } else {
                        error= true;
                        System.out.println("文法判断错误");
                        break;
                    }
                }
            }
            if(success||error){
                if (error) success = false;
                break;
            }
        }
    }


    /**
     * Go 函数
     * @param collecion
     * @param x
     * @return
     */
    private ProjectCollecion GO(ProjectCollecion collecion, Character x) {
        //开始扩张项目集
        closureCollection(collecion);
        return collecion;
    }

    /**
     * 项目集判重函数
     * @param projectCollecion
     * @return
     */
    private int projectCollectionContain(ProjectCollecion projectCollecion) {
        for (int i = 0; i < projectCollecionArrayList.size(); i++) {
            if (projectCollecion.equals(projectCollecionArrayList.get(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 项目集闭包函数
     * @param projectCollecion
     */
    private void closureCollection(ProjectCollecion projectCollecion) {

        ArrayList<Project> projects = projectCollecion.getProjects(); //获取项目集
        Queue<Pair<Character, HashSet<Character>>> waitQueue = new LinkedList<>(); //设置待扩张的产生式
        int pointIndex = -1; //点的位置
        Character chFirst; // 。后面的第一个字符

        for (Project project : projectCollecion.projects) {
            pointIndex = project.production.indexOf("."); //获取点的位置

            ////对应于节点在尾的情况，first集由之前的决定，项目集族无法继续增长
            if (pointIndex == project.production.length() - 1) {
//            //获取表达式 设置action表
                String production = project.production.substring(0, project.production.length() - 1);
                for (int j = 0; j < sententceList.size(); j++) {
                    String temp = sententceList.get(j).pre + "->" + sententceList.get(j).follow;
                    if (production.equals(temp)) {
                        for (Character character:project.expectation) {
                            if (production.charAt(production.length() - 1) == END && character.equals('#'))
                                projectCollecion.next.put(character, "acc");
                            else
                                projectCollecion.next.put(character, "r" + j);
                        }
                    }
                }

            } else {
                chFirst = new Character(project.production.charAt(pointIndex + 1));
                //  .后面跟着非终结符，继续扩张
                if (UTSet.contains(chFirst)) {
                    if (pointIndex < project.production.length() - 2) {
                        //产生新的first集
                        HashSet<Character> exepction = firstMap.get(project.production.charAt(pointIndex + 2));
                        waitQueue.add(new Pair<Character, HashSet<Character>>(chFirst, exepction));
                    } else {
                        //first集合由之前的决定
                        waitQueue.add(new Pair<Character, HashSet<Character>>(chFirst, new HashSet<>(project.expectation)));
                    }
                }
            }
        }

        HashSet<String> sentenceRecorder = new HashSet<>(); //记录表达式的出现情况
        HashMap<Character, HashSet<Character>> chRecorder = new HashMap<>();// 记录字符对应的项目是否已经闭合
        //扩展 todo 消除左递归引起的无限循环
        while (!waitQueue.isEmpty()) {

            Pair<Character, HashSet<Character>> pair = waitQueue.poll();
            Character key = pair.getFirst();
            //对应字符的first添加判重
            boolean refinded = true; //代表非终结符对应的项目已经完善
            boolean occured = false; //代表非终结符出现过
            if (chRecorder.get(key) != null) {
                occured = true;
                for (Character ch : pair.getSecond()) {
                    if (!chRecorder.get(key).contains(ch)) {
                        refinded = false;
                        break;
                    }
                }
            } else {
                chRecorder.put(key, new HashSet<>());
            }
            if (refinded && occured) {
                continue;//已经包含的没有必要添加
            }

            ArrayList<String> sententslist = sententMap.get(key);
            Project project = null;
            for (int i = 0; i < sententslist.size(); i++) {
                //产生新的表达式
                chFirst = sententslist.get(i).charAt(0);
                HashSet<Character> expection = null;
                String production = key + "->." + sententslist.get(i);
                //todo 拿到对应的项目
                if (sentenceRecorder.contains(production)) {
                    for (Project p : projectCollecion.projects) {
                        if (p.production.equals(production))
                            project = p;
                    }
                } else {
                    project = new Project();
                    project.production = production;
                    projectCollecion.getProjects().add(project);
                    sentenceRecorder.add(project.production);
                }
                project.expectation.addAll(pair.getSecond());
                chRecorder.get(key).addAll(new HashSet<>(project.expectation));
                //判断是否继续加入等待队列
                if (UTSet.contains(chFirst)) {
                    //对应不是结尾的情况
                    if (project.production.length() > 5) {
                        expection = firstMap.get(sententslist.get(i).charAt(1));
                    } else {
                        expection = new HashSet<>(new HashSet<>(project.expectation));
                    }
                    waitQueue.add(new Pair<Character, HashSet<Character>>(chFirst, expection));
                }

            }
        }
        projectCollecion.soft();
    }

    /**
     * 设置Action Goto表
     */
    private void setLR1Table() {
        ActionTable = new ArrayList<>();
        GoToTable = new ArrayList<>();
        TSet.add('#');
        for (int i = 0; i < projectCollecionArrayList.size(); i++) {
            ProjectCollecion collecion = projectCollecionArrayList.get(i);
            ActionTable.add(new HashMap<>());
            GoToTable.add(new HashMap<>());
            for (Character ch : TSet) {
                if (collecion.next.get(ch) != null) {
                    ActionTable.get(i).put(ch, collecion.next.get(ch));
                } else {
                    ActionTable.get(i).put(ch, " ");
                }
            }
            for (Character ch : UTSet) {
                if (collecion.next.get(ch) == null) {
                    GoToTable.get(i).put(ch, " ");
                } else {
                    GoToTable.get(i).put(ch, collecion.next.get(ch));
                }
            }
        }
        TSet.remove('#');
    }

    public boolean isSuccess() {
        return success;
    }

    public String[][] getResultTable() {
        String[][] table = new String[step][4];
        for (int i = 0; i < resultTable.size(); i++) {
            for (int j = 0; j <resultTable.get(i).size() ; j++) {
                table[i][j] = resultTable.get(i).get(j);
            }
        }
        return table;
    }


    public  String[] getActionTAbleHead(){
        String[] tablehead = new String[TSet.size()+2];
        tablehead[0] = " ";
        int i = 1;
        for (Character ch:ActionTable.get(0).keySet()) {
            tablehead[i++] = ch+"";
        }
        return tablehead;
    }

    public  String[][] getActionTable(){
        String[][] table = new String[ActionTable.size()][TSet.size()+2];
        for (int i = 0; i < ActionTable.size(); i++) {
            table[i][0] = ""+i;
            int j = 1;
            for (Character ch:ActionTable.get(i).keySet()) {
                table[i][j++] = ActionTable.get(i).get(ch);
            }
        }
        return table;
    }

    public String[][] getGotoTable(){
        String[][] table = new String[GoToTable.size()][UTSet.size()+1];
        for (int i = 0; i < ActionTable.size(); i++) {
            table[i][0] = ""+i;
            int j = 1;
            for (Character ch: GoToTable.get(i).keySet()) {
                table[i][j++] = GoToTable.get(i).get(ch);
            }
        }
        return table;
    }

    public  String[] getGotoTAbleHead(){
        String[] tablehead = new String[UTSet.size()+1];
        tablehead[0] = "";
        int i = 1;
        for (Character ch:GoToTable.get(0).keySet()) {
            tablehead[i++] = ch+"";
        }
        return tablehead;
    }

}
