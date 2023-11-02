import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.Scanner;
import javax.swing.JScrollPane;
import javax.swing.*;
import javax.swing.border.Border;

class Node_1 {
    int element;   //元素
    Node_1 LChild, RChild;//左右子树
    int height; //该结点的高度
    private Color color; //结点的颜色

    public Node_1(int element) {
        this.element=element;
        height = 1;
        this.color = Color.black; // 默认颜色为黑色
    }
    // 添加getter和setter方法
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}

public class AVLTreeGUI extends JPanel implements ActionListener {
    private static final int NODE_WIDTH = 60; //结点矩形框的宽度
    private static final int NODE_HEIGHT = 40; //结点矩形框的高度
    private static final long serialVersionUID = 1L;//版本控制属性
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static Node_1 root; //根结点
    private Node_1 searchedNode; // 添加成员变量，用于记录当前匹配的节点
    private Node_1 lastSearchedNode; // 添加成员变量，用于记录上一次匹配的节点
    private final JTextField inputField;


    public AVLTreeGUI() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 30, 30)); // 设置布局管理器并设置间距
        inputField = new JTextField(8);

        JButton insertButton = new JButton("Insert");
        insertButton.addActionListener(this);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this);

        JButton SearchButton = new JButton("Search");
        SearchButton.addActionListener(this);

        // 设置文本框的背景色和字体
        inputField.setBackground(Color.WHITE);
        inputField.setFont(new Font("Arial", Font.BOLD, 20));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);
        Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
        inputField.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // 设置输入按钮的前景色、背景色和字体
        insertButton.setForeground(Color.WHITE);
        insertButton.setBackground(Color.BLUE);
        insertButton.setFont(new Font("Arial", Font.BOLD, 20));
        insertButton.setPreferredSize(new Dimension(120, 40));

        // 设置删除按钮的前景色、背景色和字体
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBackground(Color.RED);
        deleteButton.setFont(new Font("Arial", Font.BOLD, 20));
        deleteButton.setPreferredSize(new Dimension(120, 40));

        // 设置查找按钮的前景色、背景色和字体
        SearchButton.setForeground(Color.WHITE);
        SearchButton.setBackground(Color.gray);
        SearchButton.setFont(new Font("Arial", Font.BOLD, 20));
        SearchButton.setPreferredSize(new Dimension(120, 40));

        // 添加按钮和文本框到面板中
        add(SearchButton);
        add(inputField);
        add(insertButton);
        add(deleteButton);

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("AVL");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        AVLTreeGUI panel = new AVLTreeGUI();
        frame.add(panel);
        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (root != null) {
            // 设置线条为 3像素宽度，可以根据需要调整粗细
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(3));

            Font font = new Font("Calibri", Font.BOLD, 18); // 创建一个 Arial 字体对象，字号为 14，字重为粗体
            g.setFont(font); // 设置当前字体为自定义的字体
            paintTree(g, root, getWidth() / 2, getHeight() / 6, WIDTH/8 );//width=800
        }
    }

    //paintTree(x:横轴，y:纵轴，hGap:偏移) 该函数可随实际结点情况修改，防止重叠
    private void paintTree(Graphics g, Node_1 p, int x, int y, int hGap) {

        if (p.LChild != null) {
            /*(x,y)默认矩形中心
            (-30,+20)左下角     (-30,-20)左上角    (+30,+20)右下角    (+30,-20)右上角
            * */
            //连线（10使两个矩形框的距离大于10）
            g.drawLine(x - 2*hGap+NODE_WIDTH/2-NODE_WIDTH-10, y + hGap-NODE_HEIGHT/2+NODE_HEIGHT+10, x-NODE_WIDTH/2, y+NODE_HEIGHT/2);
            //画点偏移设置至少（-60，40），防止重叠
            paintTree(g, p.LChild, x - 2*hGap-NODE_WIDTH-10, y + hGap+NODE_HEIGHT+10, hGap/3 );
        }
        if (p.RChild != null) {
            g.drawLine(x + 2*hGap-NODE_WIDTH/2+NODE_WIDTH+10, y + hGap-NODE_HEIGHT/2+NODE_HEIGHT+10, x+NODE_WIDTH/2, y+NODE_HEIGHT/2);
            paintTree(g, p.RChild, x + 2*hGap+NODE_WIDTH+10, y + hGap+NODE_HEIGHT+10, hGap/3);
        }
        //画结点，矩形框，字居中
        g.setColor(p.getColor()); //查找功能会改变结点颜色
        g.drawRect(x-NODE_WIDTH/2 , y-NODE_HEIGHT/2 , NODE_WIDTH, NODE_HEIGHT);
        g.drawString(Integer.toString(p.element), x - 6, y + 5);
        g.setColor(Color.black); //恢复默认颜色，防止颜色溢出

    }

    // 获取节点的高度
    int height(Node_1 node) {
        if (node == null)
            return 0;
        return node.height;
    }

    // 获取两个数中较大的一个
    int max(int a, int b) {
        return Math.max(a, b);
    }

    // 执行右旋转
    Node_1 RChildRotate(Node_1 y) {
        Node_1 x = y.LChild;
        Node_1 T2 = x.RChild;

        x.RChild = y;
        y.LChild = T2;

        // 更新高度
        y.height = max(height(y.LChild), height(y.RChild)) + 1;
        x.height = max(height(x.LChild), height(x.RChild)) + 1;

        return x;
    }

    // 执行左旋转
    Node_1 LChildRotate(Node_1 x) {
        Node_1 y = x.RChild;
        Node_1 T2 = y.LChild;

        y.LChild = x;
        x.RChild = T2;

        // 更新高度
        x.height = max(height(x.LChild), height(x.RChild)) + 1;
        y.height = max(height(y.LChild), height(y.RChild)) + 1;

        return y;
    }

    // 获取平衡因子BF
    int getBalance(Node_1 node) {
        if (node == null)
            return 0;

        return height(node.LChild) - height(node.RChild);
    }

    // 插入节点
    Node_1 insertNode(Node_1 node, int element) {
        // 执行标准的BST插入
        if (node == null)
            return (new Node_1(element));

        if (element < node.element)
            node.LChild = insertNode(node.LChild, element);
        else if (element > node.element)
            node.RChild = insertNode(node.RChild, element);
        else { // 不允许插入重复值
            System.out.println("Duplicate"); // 重复
            return node;
        }
        // 更新高度
        node.height = 1 + max(height(node.LChild), height(node.RChild));

        // 检查平衡性并进行旋转操作
        int balance = getBalance(node);

        // 左左情况下的旋转
        if (balance > 1 && element < node.LChild.element)
            return RChildRotate(node);

        // 右右情况下的旋转
        if (balance < -1 && element > node.RChild.element)
            return LChildRotate(node);

        // 左右情况下的旋转
        if (balance > 1 && element > node.LChild.element) {
            node.LChild = LChildRotate(node.LChild);
            return RChildRotate(node);
        }

        // 右左情况下的旋转
        if (balance < -1 && element < node.RChild.element) {
            node.RChild = RChildRotate(node.RChild);
            return LChildRotate(node);
        }

        return node;
    }

    // 找到树中最小节点（最左节点）,删除时，从右子树删
    Node_1 minValueNode(Node_1 node) {
        Node_1 current = node;
        while (current.LChild != null)
            current = current.LChild;
        return current;
    }

    // 删除节点
    Node_1 deleteNode(Node_1 root, int element) {
        // 执行标准的BST删除
        if (root == null)
            return null;

        if (element < root.element)
            root.LChild = deleteNode(root.LChild, element);
        else if (element > root.element)
            root.RChild = deleteNode(root.RChild, element);
        else {
            // 节点为叶子节点或只有一个子节点
            if ((root.LChild == null) || (root.RChild == null)) {
                Node_1 temp = null;
                if (temp == root.LChild)
                    temp = root.RChild;
                else
                    temp = root.LChild;

                // 无子节点的情况
                if (temp == null) {
                    temp = root;
                    root = null;
                } else // 一个子节点的情况
                    root = temp;
            } else {
                // 节点有两个子节点，获取后继节点
                Node_1 temp = minValueNode(root.RChild);

                // 将后继节点的值复制到要删除的节点
                root.element = temp.element;

                // 删除后继节点
                root.RChild = deleteNode(root.RChild, temp.element);
            }
        }

        // 如果树为空，则直接返回
        if (root == null)
            return null;

        // 更新高度
        root.height = max(height(root.LChild), height(root.RChild)) + 1;

        // 检查平衡性并进行旋转操作
        int balance = getBalance(root);

        // 左左情况下的旋转
        if (balance > 1 && getBalance(root.LChild) >= 0)
            return RChildRotate(root);

        // 右右情况下的旋转
        if (balance < -1 && getBalance(root.RChild) <= 0)
            return LChildRotate(root);

        // 左右情况下的旋转
        if (balance > 1 && getBalance(root.LChild) < 0) {
            root.LChild = LChildRotate(root.LChild);
            return RChildRotate(root);
        }

        // 右左情况下的旋转
        if (balance < -1 && getBalance(root.RChild) > 0) {
            root.RChild = RChildRotate(root.RChild);
            return LChildRotate(root);
        }

        return root;
    }

    // 在树中搜索指定的键
    Node_1 search(Node_1 root, int element) {
        // 根节点为空或等于目标值，返回根节点
        if (root == null || root.element == element)
            return root;

        // 目标值小于根节点的值，在左子树中继续搜索
        if (root.element > element)
            return search(root.LChild, element);

        // 目标值大于根节点的值，在右子树中继续搜索
        return search(root.RChild, element);
    }

    // 中序遍历二叉树
    void inorder(Node_1 root) {
        if (root != null) {
            inorder(root.LChild);
            System.out.print(root.element + " ");
            inorder(root.RChild);
        }
    }

    //打印树每个结点的高度
    private static void innerTraverse(Node_1 root) {
        if (root == null) {
            return;
        }
        innerTraverse(root.LChild);
        System.out.println(root.element + " height:"+root.height);
        innerTraverse(root.RChild);
    }

    //恢复默认颜色（black）
    public void resetSearchedNodeColor() {
        if (searchedNode != null) {
            searchedNode.setColor(Color.BLACK); // 将记录的节点颜色恢复为默认颜色
            searchedNode = null; // 清除记录的节点
        }
        if (lastSearchedNode != null) {
            lastSearchedNode.setColor(Color.BLACK); // 将记录的上一次节点颜色恢复为默认颜色
            lastSearchedNode = null; // 清除记录的上一次节点
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String input = inputField.getText();
        try {
            int value = Integer.parseInt(input);
            if (e.getActionCommand().equals("Insert")) {
                System.out.println("插入元素 " + value);
                resetSearchedNodeColor();
                if(search(root,value)!=null){
                    JOptionPane.showMessageDialog(this, "待插入元素 " + value + " 已存在");
                }else {
                    root = insertNode(root, value);
                }
            } else if (e.getActionCommand().equals("Delete")) {
                System.out.println("删除元素 " + value);
                resetSearchedNodeColor();
                if(search(root,value)==null){
                    JOptionPane.showMessageDialog(this, "待删除元素 " + value + "不存在 ");
                }else {
                    root = deleteNode(root, value);
                }
            }else if(e.getActionCommand().equals("Search")){
                System.out.println("搜索元素 "+value);
                lastSearchedNode = searchedNode; // 将之前匹配的节点记录为上一次匹配的节点
                searchedNode = search(root, value);
                if(searchedNode==null){
                    JOptionPane.showMessageDialog(this, "待搜索元素 " + value + "不存在 ");
                    resetSearchedNodeColor();
                }else{
                    if (lastSearchedNode != null) {
                        // 将上一次匹配的节点颜色恢复为默认颜色
                        lastSearchedNode.setColor(Color.BLACK);
                    }
                     /*完善部分，待查找结点的字体颜色改为红色*/
                    searchedNode.setColor(Color.RED);
                    JOptionPane.showMessageDialog(this, "待搜索元素 " + value + "存在 ");

                }
            }
            //重新绘制树
            repaint();
            inputField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入整数");
            inputField.setText("");
        }
    }

}
