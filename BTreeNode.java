public class BTreeNode {
    public int keyNum;		// 结点中关键字的个数
    public int[] data;		// 存储关键字的数组
    public BTreeNode parent; 		// 双亲结点
    public BTreeNode[] childNodes;		//指向孩子结点（指针数组）

    static int MAX_ = 3;		// 该B树的阶数 即每个结点最多含有多少个子树；结点中关键字的个数最多是3个

    /*
     * 构造函数
     */
    public BTreeNode () {
        keyNum = 0;
        data = new int[MAX_+1];		// 0号位置不使用，正常情况下，还有一个位置不使用，这个位置是为了整体后移准备的
        parent = null;
        childNodes = new BTreeNode[MAX_+1];
    }
}
