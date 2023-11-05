import javafx.application.Application;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.zip.CheckedOutputStream;
import java.util.Arrays;
import java.util.zip.DeflaterOutputStream;


public class BTree  {
    BTreeNode root;		// B树的根结点
    static int MAX_ = 3;	// 表示B-树是一棵3叉树，结点中的关键字个数最多是3-1=2个。

    /*
     * 构造函数
     */
    public BTree() {
        root = null;
    }

    /*
     * 初始化B-树
     * 自定义
     */
    public void initBTree () {
        root = new BTreeNode();  //作为树的根结点
        root.data[++root.keyNum] = 35; //50存储在根结点的data数组中，并使这个结点的keyNum加一

        BTreeNode left = new BTreeNode();
        BTreeNode right = new BTreeNode();

        root.childNodes[0] = left;
        root.childNodes[1] = right;

        left.parent = root;
        right.parent = root;

        left.data[++(left.keyNum)] = 18;

        right.data[++(right.keyNum)] = 43;
        right.data[++(right.keyNum)] = 78;

        BTreeNode left_1 = new BTreeNode();
        BTreeNode left_2 = new BTreeNode();

        BTreeNode right_1 = new BTreeNode();
        BTreeNode right_2 = new BTreeNode();
        BTreeNode right_3 =new BTreeNode();
        left.childNodes[0]=left_1;
        left.childNodes[1]=left_2;

        left_1.parent = left;
        left_2.parent = left;

        left_1.data[++(left_1.keyNum)] = 11;

        left_2.data[++(left_2.keyNum)] = 27;


        right.childNodes[0]=right_1;
        right.childNodes[1]=right_2;
        right.childNodes[2]=right_3;
        right_1.parent = right;
        right_2.parent = right;
        right_3.parent =right;

        right_1.data[++(right_1.keyNum)] = 39;
        right_2.data[++(right_2.keyNum)] = 47;
        right_2.data[++(right_2.keyNum)] = 53;
        right_2.data[++(right_2.keyNum)] = 64;
        right_3.data[++(right_3.keyNum)] = 99;
    }

    /*
     * 插入函数
     * 基本思路：需要遍历找到合适的位置，插入之后检查是否需要分裂结点。
     * 		 先行插入，再进行判断当前是否符合B-树的要求
     * 		1、关键字个数没有超过规定的个数，那么就不做任何操作
     * 		2、关键字个数超出规定个数，那么就考虑分裂当前结点，将中间值提升到双亲结点，更新双亲结点与兄弟结点
     * 		3、如果双亲结点因为其孩子结点的分裂提升而超过关键字的个数，从而进行类似上述操作
     * 		4、在这里，由于结点中保存了双亲结点的指针，那么无需在遍历的时候保存这些路径上的结点。
     */
    public void insertBNode (int data) {
        // 判断B-树是否是空树
        if (root == null) {
            root = new BTreeNode();
            root.keyNum++;
            root.data[root.keyNum] = data;
        } else {
            // 遍历找到合适的插入位置
            BTreeNode current = root;
            BTreeNode p = null;
            int i = -1;        // 记录了data应该插入到当前结点的那个位置

            while (current != null) {
                p = current;
                for (i = 1; i <= current.keyNum; i++) {
                    if (current.data[i] > data) {
                        break;
                    }
                }
                current = current.childNodes[i - 1];
            }

            // 循环结束i肯定不为-1
            // 在叶子结点执行插入
            insertGoLeaf(p, i, data);

            // 从当前结点一直判断到根节点
            while (p != null) {
            // 判断是否符合B-树的要求
            if (!isBTNode(p)) {
                // 需要提升的key
                int index;

                if (p.keyNum % 2 == 0) {
                    index = (p.keyNum / 2);
                } else {
                    index = (p.keyNum / 2) + 1;
                }
                int tempData = p.data[index];

//                    // 将结点p进行分割
                Map<BTreeNode, BTreeNode> map = splitNode(p);
                Iterator<Entry<BTreeNode, BTreeNode>> it = map.entrySet().iterator();
                Entry<BTreeNode, BTreeNode> entry = it.next();
//                    // 将tempData插入到双亲结点中
                //entry.getKey()左结点，entry.getValue()右结点
                insertGoNotLeaf(p.parent, entry.getKey(), entry.getValue(), tempData);
            }
            p = p.parent;
        }
        }
    }

    /*
     * 对指定结点进行分割
     * 返回值：Map，保存两个结点
     */
    //Map<BTreeNode, BTreeNode>用于保存结点分裂后产生的两个新结点
    //通过这个返回的Map对象得到分裂后的两个新结点
    private Map<BTreeNode, BTreeNode> splitNode(BTreeNode p) {
        Map<BTreeNode, BTreeNode> map = new HashMap<BTreeNode, BTreeNode>();
        int number = p.keyNum;

        int index;
        // 需要提升的key
        if(number%2==0){
            index=(number/2);
        }else {
            index = (number / 2) + 1;
        }
        // 重新new一个结点，用于保存后半截的数据
        BTreeNode tempNode = new BTreeNode();
        tempNode.keyNum = number - index;
        tempNode.parent = p.parent;
        // 完成数据复制
        for (int n = 1; n <= tempNode.keyNum; n++) {
            tempNode.data[n] = p.data[index+n];
            tempNode.childNodes[n - 1] = p.childNodes[index + n - 1];
        }
        // 最后一个指针需要单独赋值
        tempNode.childNodes[tempNode.keyNum] = p.childNodes[number];

        // 结点p还是结点p，只是key的个数会更新
        p.keyNum = index - 1;
        // 将p所指向的结点的index之后的指针赋值为空，以便于以后的查找
        for (int m = index; m <= p.keyNum; m++) {
            p.childNodes[m] = null;
        }

        // 将tempNode的孩子结点的双亲结点重新设置
        // 需要判断是否是叶子结点，叶子结点的话就不需要设置了
        // 叶子结点分裂的话，不涉及叶子结点的孩子结点问题
        for (int m = 1; m <= tempNode.keyNum; m++) {
            if (tempNode.childNodes[m - 1] != null) {
                tempNode.childNodes[m - 1].parent = tempNode;
            }
        }
        if (tempNode.childNodes[tempNode.keyNum] != null) {
            tempNode.childNodes[tempNode.keyNum].parent = tempNode;
        }

        map.put(p, tempNode);
        return map;
    }

    /*
     * 在双亲结点中插入数据tempData
     * 更新双亲结点中的孩子指针
     */
    private void insertGoNotLeaf(BTreeNode parent, BTreeNode child1,
                                 BTreeNode child2, int tempData) {
        // parent==null说明当前分裂的结点是root结点
        if (parent == null) {
            parent = new BTreeNode();
            parent.data[++parent.keyNum] = tempData;
            parent.childNodes[parent.keyNum - 1] = child1;
            parent.childNodes[parent.keyNum] = child2;
            // 同时更新结点的双亲结点
            child1.parent = parent;
            child2.parent = parent;

            root = parent;
        }
        else{
        parent.keyNum++;
        int j;
        for (j = parent.keyNum; j > 1; j--) {
            if (tempData < parent.data[j - 1]) {
                parent.data[j] = parent.data[j - 1];
                parent.childNodes[j] = parent.childNodes[j - 1];
            } else {
                break;
            }
        }
        // 插入数据
        parent.data[j] = tempData;
        // 更新孩子指针
        parent.childNodes[j - 1] = child1;
        parent.childNodes[j] = child2;
        // 同时更新结点的双亲结点
        child1.parent = parent;
        child2.parent = parent;
    }
    }

    /*
     * 判断当前结点是否符合B-树结点的要求
     * [m/2]-1<=n<=m-1
     */
    private boolean isBTNode(BTreeNode p) {
        if(p.parent==null) {
            return (p.keyNum <= MAX_ - 1) && (p.keyNum >= 1);
        } else {
            return (p.keyNum <= MAX_ - 1) && (p.keyNum >= ((int) Math.ceil(MAX_ / 2.0)) - 1);
        }
    }

    /*
     * 实际执行插入，在结点p（叶子结点）的第i个位置插入数据data
     */
    private void insertGoLeaf (BTreeNode p, int i, int data) {
        p.keyNum++;

        // 整体往后移动
        for (int j = p.keyNum; j > i; j--) {
            p.data[j] = p.data[j-1];
        }
        // 完成数据的插入
        p.data[i] = data;
    }

    /*
     * 查找函数
     * 基本思路：沿指针搜索结点与在结点中搜索key交替进行
     * 返回值：值所在的结点以及在该结点中的位置
     */
    public Map<BTreeNode, Integer> findBNode (int data) {
        if (root == null) {
            return null;
        } else {
            BTreeNode current = root;
            int number;

            while (current != null) {
                // current结点中进行顺序查找
                number = current.keyNum;
                int i;
                for (i = 1; i <= number; i++) {
                    // 由于结点中的key值是按照递增的顺序存储的
                    // 只要找到一个比我们需要的值data大的值就可以确定停止了
                    if (current.data[i] > data) {
                        break;
                    }

                    if (current.data[i] == data) {
                        Map<BTreeNode, Integer> map = new HashMap<BTreeNode, Integer>();
                        map.put(current, i);
                        return map;
                    }
                }
                // 不管是在什么时候退出都会符合这个条件
                current = current.childNodes[i-1];
            }
            return null;
        }
    }

    /*
     * 删除函数
     */
    public void deleteBNode (int data) {
        //先找到需要删除的结点位置
        if (root == null) {
            return;
        } else {
            BTreeNode current = root;
            BTreeNode pNode = null;
            boolean isFind = false;
            int i = 0;

            while (current != null) {  //若非空
                pNode = current;
                for (i = 1; i <= current.keyNum; i++) {
                    if (data == current.data[i]) {
                        isFind = true;  //找到该元素
                        break;
                    }
                    // 在这里会提前结束，减少运行时间
                    if (data < current.data[i]) {
                        break;
                    }
                }

                // 如果找到了需要删除的结点就退出while循环
                if (isFind) {
                    break;
                }
                current = current.childNodes[i-1];
            }

            // 判断退出循环的条件
            if (current == null) {
                return;
            }

            // 如果Current不为空，那么说明找到了需要删除的数据所在的结点
            // 数据存储在结点的第i个位置
            // 判断PNode结点是否是叶子结点
            if (isLeafNode (pNode)) {
                System.out.println("删除叶子");
                deleteGoLeaf (pNode, i);
            } else {
                // 找到Ai子树中最小的key代替当前的keyi
                // 并在Ai中删除key
                System.out.println("删除非叶子");
                deleteGoNotLeaf (pNode, i);
            }

            // 判断完成删除后的结点是否满足m阶B-树的要求
            while (pNode != null) {
                if ((pNode.parent == null) && (pNode.keyNum == 0)) {
                    root = pNode.childNodes[0];
                    break;
                }

                if (!isBTNode(pNode)) {
                    // 先判断左孩子是否有多的关键字
                    // 如果左孩子没有多余的，那么就判断右孩子是否有多余的关键字
                    // 不可借的话，就将该节点与根结点以及它的兄弟合并
                    // 接下来就是更新父结点，判断是否满足条件
                    // 结束条件：(当前结点没有关键字且其双亲结点为空) || (pNode为空)

                    // 对不符合B-树的结点进行处理
                    adjustBNode (pNode);
                }
                pNode = pNode.parent;
            }
        }
    }

    /*
     * 对不符合B-树的结点进行调整
     */
    private void adjustBNode(BTreeNode pNode) {
        System.out.println("调整");
        // 当前结点是其双亲结点的第几个孩子
        int index = orderOfNode (pNode);
        BTreeNode parentNode = pNode.parent;
        boolean leftOk = false;
        boolean rightOk = false;
          //左兄弟结点数要大于最少元素个数
        if (index - 1 >= 0&&parentNode.childNodes[index-1].keyNum>((int)Math.ceil(MAX_/2.0)-1)) {
            // 如果存在左孩子
            leftOk = borrowLeft (pNode, index);
        } else if (index + 1 <= (parentNode.keyNum)&&parentNode.childNodes[index+1].keyNum>((int)Math.ceil(MAX_/2.0)-1)) {
            // 如果存在右孩子

            rightOk = borrowRight (pNode, index);
        }

        if (!(leftOk || rightOk)) {
            // 合并结点
            if ((index - 1 >=0)&&(pNode.keyNum<((int)Math.ceil(MAX_/2.0)-1))) {
                // 与左兄弟合并
                mergeLeft (pNode, index);
            } else if (index + 1 <=(parentNode.keyNum)&&(pNode.keyNum<((int)Math.ceil(MAX_/2.0)-1))) {
                // 与右兄弟合并
              mergeRight (pNode, index);
            }
        }
    }

    /*
     * 与右兄弟以及根结点合并
     */
    private void mergeRight(BTreeNode pNode, int index) {
        System.out.println("右合并" + index);
        BTreeNode parentNode = pNode.parent;
        BTreeNode rightBrother = parentNode.childNodes[index + 1];
        // 先将根结点的关键字合并到右结点上
        int k;
        for (k = rightBrother.keyNum; k>0;k--){
            rightBrother.data[k + 1] = rightBrother.data[k];
    }

        System.out.println("k="+k);
        rightBrother.data[k+1] = parentNode.data[index+1];

        rightBrother.keyNum++;

         //再将Pnode结点的数据合并到右兄弟上
        int i;

       for (i = 1; i <= pNode.keyNum; i++) {
            //rightBrother.childNodes[rightBrother.keyNum] = pNode.childNodes[i-1];
            rightBrother.data[++rightBrother.keyNum]  = pNode.data[i];
        }

        int[] newData = Arrays.copyOf(rightBrother.data, rightBrother.keyNum+1);
        Arrays.sort(newData, 1, newData.length);
        rightBrother.data=Arrays.copyOf(newData,MAX_+1);

       // rightBrother.childNodes[rightBrother.keyNum] = pNode.childNodes[i-1];
       //  更新根结点，整体前移
        int j;
        for ( j = index; j < parentNode.keyNum; j++) {
            parentNode.data[j+1] = parentNode.data[j+2];
            parentNode.childNodes[j] = parentNode.childNodes[j+1]; //删除左子树
        }
        parentNode.childNodes[j]=null;
        parentNode.keyNum--;

        // 需要将pnode结点的孩子的双亲结点重新设置为右兄弟结点
        for (int m = 1; m <= pNode.keyNum; m++) {

            if (pNode.childNodes[m - 1] != null) {
                pNode.childNodes[m - 1].parent = rightBrother;
            }
        }
            if (pNode.childNodes[pNode.keyNum] != null) {
                System.out.println("执行");
                pNode.childNodes[pNode.keyNum].parent = rightBrother;
                for(int nn=pNode.keyNum;nn>=0;nn--){
                int d;

                for( d=numChild(rightBrother);d>0;d--) {
                    rightBrother.childNodes[d] = rightBrother.childNodes[d - 1];
                }

                rightBrother.childNodes[0]=pNode.childNodes[nn];
            }}


        // 释放左兄弟结点
        pNode = null;
    }


    /*
     * 与左兄弟以及根结点合并
     */
    private void mergeLeft(BTreeNode pNode, int index) {
        System.out.println("左合并"+index);

        BTreeNode parentNode = pNode.parent;
        BTreeNode leftBrother = parentNode.childNodes[index -1];

        // 先将根结点的关键字合并到左兄弟上

        leftBrother.data[++leftBrother.keyNum] = parentNode.data[index];
        // 再将pNode结点的数据合并到左兄弟结点上
       int i;
        for (i = 1; i <= pNode.keyNum; i++) {
//            leftBrother.childNodes[leftBrother.keyNum] = pNode.childNodes[i-1];
            leftBrother.data[++leftBrother.keyNum] = pNode.data[i];
       }
//        leftBrother.childNodes[leftBrother.keyNum] = pNode.childNodes[i-1];
        // 更新双亲结点，整体前移
        int j;
        for ( j = index; j < parentNode.keyNum; j++) {
            parentNode.data[j] = parentNode.data[j + 1];
            parentNode.childNodes[j] = parentNode.childNodes[j + 1];
        }
        parentNode.childNodes[j]=null;
       parentNode.keyNum--;

        // 将pNode结点的孩子结点的双亲结点设置为左兄弟结点
        for (int m = 1; m <= pNode.keyNum; m++) {

            if (pNode.childNodes[m-1] != null) {
                pNode.childNodes[m-1].parent = leftBrother;
            }
        }
//空结点的孩子 pNode.keyNum=0
        for(int nn=0;nn<=pNode.keyNum;nn++) {
            if (pNode.childNodes[nn] != null) {

                pNode.childNodes[nn].parent = leftBrother;

                if (numChild(leftBrother) <= MAX_ - 1) {
                    leftBrother.childNodes[numChild(leftBrother)] = pNode.childNodes[pNode.keyNum];
                } else if (numChild(leftBrother) == MAX_) {

               leftBrother.childNodes[numChild(leftBrother) -((int) Math.ceil(MAX_ / 2.0)) + 1+nn] = pNode.childNodes[nn];

                }
            }
        }

        // 释放当前结点
        pNode = null;
    }


    /*
     * 向右兄弟结点借关键字
     */
    private boolean borrowRight(BTreeNode pNode, int index) {
        System.out.println("右借");
        BTreeNode parentPNode = pNode.parent;
        BTreeNode rightBrother = parentPNode.childNodes[index + 1];

        if (isHasMoreKey(rightBrother)) {
            // 先当做是叶子结点处理
            // 向当前结点中插入双亲结点数据
            int data1 = parentPNode.data[index + 1];
            pNode.keyNum++;
            int i=pNode.keyNum;
            for (i = pNode.keyNum; i > 1; i--) {
                if (data1 < pNode.data[i-1]) {
                    pNode.data[i] = pNode.data[i-1];
                } else {
                    break;
                }
            }
            pNode.data[i] = data1;

            // 删除右兄弟结点中最小的数据，然后用其来代替双亲结点的数据
            // 由于删除的是第一个关键字，所以不能简单的设置keyNum
            int data2 = rightBrother.data[1];
            parentPNode.data[index + 1] = data2;

            // 关键字整体前移
            for (int j = 1; j < rightBrother.keyNum; j++) {
                rightBrother.data[j] = rightBrother.data[j + 1];
            }

            rightBrother.keyNum--;

                if (numChild(rightBrother) != (rightBrother.keyNum + 1)) {
                    pNode.childNodes[index+1] = rightBrother.childNodes[0];
                    for(int k=0;k<numChild(rightBrother);k++) {
                    rightBrother.childNodes[k] = rightBrother.childNodes[k+1];
                }
            }
            return true;
        }
        return false;
    }


    /*
     * 向左兄弟结点借关键字
     */
    private boolean borrowLeft(BTreeNode pNode, int index) {
        System.out.println("左借");
        BTreeNode parentPNode = pNode.parent;
        BTreeNode leftBrother = parentPNode.childNodes[index - 1];
        if (isHasMoreKey(leftBrother)) {
            // 先当做是叶子结点处理
            // 向当前结点中插入双亲结点数据
            int data1 = parentPNode.data[index];
            pNode.keyNum ++;
            int i;
            for (i = pNode.keyNum; i > 1; i--) {
                if (data1 < pNode.data[i-1]) {
                    pNode.data[i] = pNode.data[i-1];
                } else {
                    break;
                }
            }
            pNode.data[i] = data1;

            // 删除左兄弟结点中最大的数据，然后用其来代替双亲结点的数据
            int data2 = leftBrother.data[leftBrother.keyNum--];
            parentPNode.data[index] = data2;
            if (numChild(leftBrother) != (leftBrother.keyNum + 1)) {

                for(int k=numChild(pNode);k>0;k--) {
                    pNode.childNodes[k]=pNode.childNodes[k-1];
                }
                pNode.childNodes[0] = leftBrother.childNodes[leftBrother.keyNum+1];
            }
            return true;
        }
        return false;
    }

    //计算结点的孩子个数
    private int numChild(BTreeNode pNode) {
        int count=0;
        for(int i=0;i<MAX_;i++){
                if (pNode.childNodes[i]!=null) {
                    count++;
                }
        }
        return count;
    }

    /*
     * 判断当前结点是其双亲结点的第几个孩子
     */
    private int orderOfNode(BTreeNode pNode) {
        BTreeNode parentNode = pNode.parent;
        int i;
        for (i = 0; i <= parentNode.keyNum; i++) {
            if (parentNode.childNodes[i] == pNode) {
                break;
            }
        }
        return i;
    }

    /*
     * 需要删除的数据所在的结点是非叶子结点
     */
    private void deleteGoNotLeaf(BTreeNode pNode, int index) {
        // 找到最小中序后继并直接用该数据代替需要删除的数据
        findMinSuccNode (pNode, index);
    }

    /*
     * 找到当前结点位置index的最小中序后继
     * 完成删除并更新调整寻找最小中序后继所经过的结点
     */
    private void findMinSuccNode(BTreeNode pNode, int index) {
        BTreeNode current = pNode.childNodes[index];
        BTreeNode parent = null;

        while (current != null) {
            parent = current;
            current = current.childNodes[0];
        }

        // 完成替换，相对于删除数据
        pNode.data[index] = parent.data[1];
        // 在parent结点中删除数据data
        deleteGoLeaf(parent, 1);

        // 对这一右子树中的结点进行调整
        current = parent;
        while (current != pNode) {
            if (!isBTNode(current)) {
                adjustBNode(current);
            }
            current = current.parent;
        }
    }

    /*
     * 判断结点是否可以借出一个关键字
     * 满足的条件是：keyNum-1要大于等于[m/2]-1
     */
    private boolean isHasMoreKey(BTreeNode node) {
        return node.keyNum >= ((int)Math.ceil(MAX_/2.0));
    }

    /*
     * 在叶子结点PNode的第i个位置删除数据data
     */
    private void deleteGoLeaf(BTreeNode pNode, int index) {
        // 从当前位置index开始，将index的后一个位置的数据来填充index的位置的数据，相当于删除数据data
        // 不管是叶子结点还是非叶子结点，都需要同时将指针迁移覆盖替换
        for (int i = index; i < pNode.keyNum; i++) {
            pNode.data[i] = pNode.data[i+1];
            pNode.childNodes[i-1] = pNode.childNodes[i];
        }
        // 最后一个指针的位置需要单独处理
        pNode.childNodes[pNode.keyNum-1] = pNode.childNodes[pNode.keyNum];
        pNode.keyNum--;
    }

    /*
     * 判断PNode结点是否是叶子结点
     */
    private boolean isLeafNode(BTreeNode pNode) {
        return pNode.childNodes[0] == null;
    }

  /*
     * 层次遍历
     * 对每一个结点都要全部遍历一遍其中的关键字
     */
    public void levelTraversal () {
        if (root != null) {
            Queue<BTreeNode> queue = new LinkedList<BTreeNode>();

            BTreeNode current = root;
            queue.add(root);

            while (!queue.isEmpty()) {
                current = queue.poll();

                // 出队列，并访问其中的关键字
                // 在访问的过程中，顺便将非空的孩子结点放入队列中
                int i;
                for (i = 1; i <= current.keyNum; i++) {
                    System.out.print(current.data[i] + " ");
                    if (current.childNodes[i-1] != null) {
                        queue.add(current.childNodes[i-1]);
                    }
                }
                // 当前结点的最后一个孩子指针还没有判断
                if (current.childNodes[i-1] != null) {
                    queue.add(current.childNodes[i-1]);
                }
                System.out.println();
            }
        }
    }


    /*
     * 2019.10.11 15:00
     * 主函数测试B-树
     */
    public static void main(String[] args) {
        // 测试初始化是否成功

		BTree bTree = new BTree();
        // bTree.initBTree();
        bTree.insertBNode(1);
        bTree.insertBNode(2);
        bTree.insertBNode(3);
        bTree.insertBNode(4);
        bTree.insertBNode(5);
        bTree.insertBNode(6);
        bTree.insertBNode(7);
        bTree.deleteBNode(6);

        bTree.levelTraversal();


     // bTree.levelTraversal();

        // 查询结点
//		Map<BTreeNode, Integer> map = bTree.findBNode(40);
//		Iterator<Entry<BTreeNode, Integer>> it = map.entrySet().iterator();
//		Entry<BTreeNode, Integer> entry = it.next();
//		System.out.println(entry.getKey().data[entry.getValue()]);



    }
}

