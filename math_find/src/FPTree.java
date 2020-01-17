import java.io.*;
import java.util.*;


public class FPTree {
    private int totalcount;//节点的总数
    private TreeNode root;
    private Map<Integer,Integer> frequency;//支持度表
    private Map<Integer, TreeNode> headers;//表头
    private int max_layer;
    private int max_support;
    private List<TreeNode> path_max_layer_nodes;
    private List<TreeNode> path_max_support_nodes;
    private static final int ROOT = -99;
    private int minSuport;//最小支持度。


    public int getTotalcount() {
        return totalcount;
    }

    public TreeNode getRoot() {
        return root;
    }

    public int getMax_layer() {
        return max_layer;
    }

    public int getMax_support() {
        return max_support;
    }

    public List<TreeNode> getPath_max_layer_nodes() {
        return path_max_layer_nodes;
    }

    public List<TreeNode> getPath_max_support_nodes() {
        return path_max_support_nodes;
    }

    public FPTree(int minSuport,String filename) throws IOException {
        max_layer = -1;
        max_support = -1;
        path_max_support_nodes = new ArrayList<>();
        path_max_layer_nodes = new ArrayList<>();
        setMinSuport(minSuport);
        buildFPTree(filename);
        setSum(root);
    }


    public void setMinSuport(int minSuport) {
        this.minSuport = minSuport;
    }

    private Map<Integer, Integer> getFrequency(String filename) throws IOException {
        Map<Integer, Integer> freq = new HashMap<Integer, Integer>();
        BufferedReader bufferedReader =new BufferedReader(new FileReader(filename));
        String line = null;
        while ((line = bufferedReader.readLine()) != null){
            String[] str = line.split("\t");
            for (int i = 1;i<7;i++){
               Integer item = Integer.parseInt(str[i]);
                Integer cnt = freq.get(item);
                if (cnt == null) {
                    cnt = 0;
                }
                cnt++;
                freq.put(item, cnt);
            }
        }
        bufferedReader.close();
        return freq;
    }
    private Map<Integer, TreeNode> getHeaders(Map<Integer,Integer> frequency,String filename) throws IOException {
        Map<Integer, TreeNode> heads =  new HashMap<Integer, TreeNode>();
        BufferedWriter bufferedWriter =new BufferedWriter(new FileWriter("filterrecord.txt"));
        BufferedReader bufferedReader =new BufferedReader(new FileReader(filename));
        String line = null;
        while ((line = bufferedReader.readLine()) != null){
            String[] str = line.split("\t");
            List<SupportNum> supportNums = new ArrayList<>();
            for (int i = 1;i<7;i++){
                int num = Integer.parseInt(str[i]);
                int cnt = frequency.get(num);
                if(minSuport <=cnt){
                    supportNums.add(new SupportNum(num,cnt));
                }
            }
            Collections.sort(supportNums);
            for (SupportNum supportnum:supportNums) {
                bufferedWriter.write(supportnum.toString()+"\t");
            }
            bufferedWriter.write("\n");
        }
        bufferedWriter.flush();
        for (Map.Entry<Integer, Integer> entry : frequency.entrySet()) {
            int num = entry.getKey();
            int cnt = entry.getValue();
            if (cnt >= minSuport) {
                TreeNode node = new TreeNode(num);
                node.setSupport(cnt);
                heads.put(num, node);

            }
        }
        bufferedReader.close();
        bufferedWriter.close();
        return heads;

    }
    private void buildFPTree(String filename) throws IOException {
        frequency = getFrequency(filename);
        headers = getHeaders(frequency,filename);
        root = new TreeNode(ROOT);
        root.setSum(0);
        root.setLayer(1);
        root.setSupport(0);
        totalcount = 1;
        List<Integer> transaction = null;
        BufferedReader bufferedReader =new BufferedReader(new FileReader("filterrecord.txt"));
        String line = null;
        while ((line = bufferedReader.readLine()) != null){
            String[] str = line.split("\t");
            int count = str.length;
            if (str[0].equals("")){
                count =0;
            }
            int i = 0;
            LinkedList<Integer> record = new LinkedList<>();
            while (i<count){
                int num = Integer.parseInt(str[i]);
                record.add(num);
                i++;
            }
            TreeNode subTreeRoot = root;
            TreeNode tmpRoot = null;
            if (root.getChildren() != null) {
                //延已有的分支，令各节点support加1
                while (!record.isEmpty() //record不为空
                        && (tmpRoot = subTreeRoot.findChild(record.peek())) != null) {//寻找相同子节点
                    tmpRoot.SupportIncrement(1);
                    subTreeRoot = tmpRoot;
                    record.poll();
                }
            }
            //长出新的节点
            addNodes(subTreeRoot, record, headers);
        }
    }
    private void addNodes(TreeNode ancestor, LinkedList<Integer> record,
                          final Map<Integer, TreeNode> headers) {
        while (!record.isEmpty()) {
            Integer item = record.poll();
            if (headers.containsKey(item)) {
                TreeNode leafnode = new TreeNode(item);
                leafnode.setSupport(1);
                leafnode.setParent(ancestor);
                ancestor.addChild(leafnode);
                leafnode.setLayer(ancestor.getLayer()+1);
                TreeNode header = headers.get(item);
                TreeNode tail=header.getTail();
                if(tail!=null){
                    tail.setNextHomonym(leafnode);
                }else{
                    header.setNextHomonym(leafnode);
                }
                header.setTail(leafnode);
                if(leafnode.getLayer()>max_layer){
                    path_max_layer_nodes.clear();
                    path_max_layer_nodes.add(leafnode);
                    max_layer = leafnode.getLayer();
                }else if(leafnode.getLayer()==max_layer){
                    path_max_layer_nodes.add(leafnode);
                }
                totalcount++;
                addNodes(leafnode, record, headers);
            }
        }
    }
    private void setSum(TreeNode Parent){
        if(Parent.getChildren()==null){
            if(Parent.getSum()>max_support){
                path_max_support_nodes.clear();
                path_max_support_nodes.add(Parent);
                max_support = Parent.getSum();
            }else if(Parent.getSum()==max_support){
                path_max_support_nodes.add(Parent);
            }
            return;
        }
        for(TreeNode Child:Parent.getChildren()){
            Child.SumIncrement(Child.getSupport()+Parent.getSum());
            setSum(Child);
        }
    }

    public static void main(String[] args) throws IOException {
        int my_minSupport;
        System.out.println("输入最小支持度阈值");
        Scanner scanner = new Scanner(System.in);
        my_minSupport=scanner.nextInt();
        FPTree fpTree = new FPTree(my_minSupport,"record.txt");
        System.out.println("FPTree节点总数为"+fpTree.getTotalcount());
        System.out.println("最多节点数为"+fpTree.getMax_layer());
        System.out.println("节点数最多的路径为(节点格式为{num:support})");
        for(TreeNode treeNode:fpTree.getPath_max_layer_nodes()){
            System.out.println(TreeNode.printParent(treeNode));
        }
        System.out.println("最大支持度之和为"+fpTree.getMax_support());
        System.out.println("支持度之和最大的路径为(节点格式为{num:support})");
        for(TreeNode treeNode:fpTree.getPath_max_support_nodes()){
            System.out.println(TreeNode.printParent(treeNode));
        }
    }

}
class SupportNum implements Comparable<SupportNum>{
    private int num;
    private int support;
    public SupportNum(int num,int support){
        this.num = num;
        this.support = support;
    }

    public int getNum() {
        return num;
    }

    public int getSupport() {
        return support;
    }
    @Override
    public int compareTo(SupportNum o) {
        return ((o.support)-(this.support));
    }
    @Override
    public String toString() {
        return this.num+"";
    }

}