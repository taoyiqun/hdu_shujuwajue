import java.util.ArrayList;
import java.util.List;


class TreeNode {
    private int num;//节点
    private int support;//频数
    private int layer;//层数
    private int sum;//支持度之和
    private TreeNode parent;
    private List<TreeNode> children;
    private TreeNode nextHomonym;//下一个节点（由表头项维护的那个链表）
    private TreeNode tail;//末节点（由表头项维护的那个链表)
    private static final int ROOT = -99;

    public TreeNode() {
    }
    

    public TreeNode(int ballNumber) {
        this.num = ballNumber;
    }

    @Override
    public String toString() {
        return (num+"频数:"+support);
    }

    public int getNode() {
        return this.num;
    }

    public void setNode(int ballNumber) {
        this.num = ballNumber;
    }

    public int getSupport() {
        return this.support;
    }

    public void setSupport(int support) {
        this.support = support;
    }

    public TreeNode getParent() {
        return this.parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public List<TreeNode> getChildren() {
        return this.children;
    }

    public void addChild(TreeNode child) {
        if (getChildren() == null) {
            List<TreeNode> list = new ArrayList<TreeNode>();
            list.add(child);
            setChildren(list);
        } else {
            getChildren().add(child);
        }
    }

    public TreeNode findChild(int ballNumber) {
        List<TreeNode> children = getChildren();
        if (children != null) {
            for (TreeNode child : children) {
                if (child.getNode()==ballNumber) {
                    return child;
                }
            }
        }
        return null;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public static String printParent(TreeNode treeNode) {
       List<String> str = new ArrayList<>();
       StringBuilder res = new StringBuilder();
       while (treeNode.getNode()!=ROOT){
           str.add("-->{"+treeNode.getNode()+":"+treeNode.getSupport()+"}");
           treeNode = treeNode.getParent();
       }
       str.add("{ROOT}");
       for (int i =str.size()-1;i>=0;i--){
           res.append(str.get(i));
       }

       return res.toString();
    }

    public TreeNode getNextHomonym() {
        return this.nextHomonym;
    }

    public void setNextHomonym(TreeNode nextHomonym) {
        this.nextHomonym = nextHomonym;
    }

    public void SupportIncrement(int n) {
        this.support += n;
    }
    public void SumIncrement(int n) {
        this.sum += n;
    }

    public TreeNode getTail() {
        return tail;
    }

    public void setTail(TreeNode tail) {
        this.tail = tail;
    }
    public void setLayer(int layer) {
        this.layer = layer;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public int getLayer() {
        return layer;
    }

    public int getSum() {
        return sum;
    }
}
