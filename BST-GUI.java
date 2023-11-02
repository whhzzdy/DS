import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.border.Border;

class BST_3 {
    int element;
    BST_3 LChild, RChild;

    public BST_3(int element) {
        this.element = element;
        this.LChild = null;
        this.RChild = null;
    }
}

public class Draw_3 extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;//版本控制属性
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static BST_3 root;
    private final JTextField inputField;


    public Draw_3() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 30, 30)); // 设置布局管理器并设置间距
        inputField = new JTextField(8);

        JButton insertButton = new JButton("Insert");
        insertButton.addActionListener(this);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this);

        // 设置文本框的背景色和字体
        inputField.setBackground(Color.WHITE);
        inputField.setFont(new Font("Arial", Font.BOLD, 20));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);
        Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
        inputField.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(5, 5, 5, 5)));


        // 设置按钮的前景色、背景色和字体
        insertButton.setForeground(Color.WHITE);
        insertButton.setBackground(Color.BLUE);
        insertButton.setFont(new Font("Arial", Font.BOLD, 20));
        insertButton.setPreferredSize(new Dimension(120, 40));

        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBackground(Color.RED);
        deleteButton.setFont(new Font("Arial", Font.BOLD, 20));
        deleteButton.setPreferredSize(new Dimension(120, 40));
        // 添加按钮和文本框到面板中
        add(insertButton);
        add(inputField);
        add(deleteButton);

    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("Binary Search Tree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        Draw_3 panel = new Draw_3();
        frame.add(panel);
        frame.setVisible(true);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (root != null) {
            // 设置线条为 3像素宽度，可以根据需要调整粗细
            Graphics2D g2 = (Graphics2D) g;
            Stroke oldStroke = g2.getStroke();
            g2.setStroke(new BasicStroke(3));

            Font font = new Font("Calibri", Font.BOLD, 18); // 创建一个 Arial 字体对象，字号为 14，字重为粗体
            g.setFont(font); // 设置当前字体为自定义的字体
            paintTree(g, root, getWidth() / 2, getHeight() / 6, WIDTH/8 );//width=800
        }
    }
  //paintTree(x:横轴，y:纵轴，hGap:偏移) 该函数可随实际结点情况修改，防止重叠
    private void paintTree(Graphics g, BST_3 p, int x, int y, int hGap) {
        if (p.LChild != null) {
            /*(x,y)默认矩形中心
            (-30,+20)左下角     (-30,-20)左上角    (+30,+20)右下角    (+30,-20)右上角
            * */
            //连线（5使两个矩形框的距离大于5）
            g.drawLine(x - 2*hGap+30-60-5, y + hGap-20+40+5, x-30, y+20);
            //画点偏移设置至少（-60，40），防止重叠
            paintTree(g, p.LChild, x - 2*hGap-60-5, y + hGap+40+5, hGap/2 );
        }
        if (p.RChild != null) {
            g.drawLine(x + 2*hGap-30+60+5, y + hGap-20+40+5, x+30, y+20);
            paintTree(g, p.RChild, x + 2*hGap+60+5, y + hGap+40+5, hGap/2);
        }
        //画结点，矩形框，字居中
        g.drawRect(x-30 , y-20 , 60, 40);
        g.drawString(Integer.toString(p.element), x - 6, y + 5);

    }
    //查找
    public static BST_3 SearchBST(BST_3 T, int k) {
        if (T == null) {
            return null;
        }
        if (T.element == k) {
            return T;
        } else if (k < T.element)
            return SearchBST(T.LChild, k);
        else
            return SearchBST(T.RChild, k);
    }

    // 插入
    public static BST_3 InsertBST(BST_3 T, int k) {
        if (T == null) {
            T = new BST_3(k);
            return T;
        }

        if (k < T.element)
            T.LChild = InsertBST(T.LChild, k);
        else if (k > T.element)
            T.RChild = InsertBST(T.RChild, k);
        else {
            System.out.println("Duplicate"); // 重复

        }

        return T;
    }

    // 删除
    private static BST_3 DeleteBSTNode(BST_3 p, int key) {
        if (p == null)
            return null;

        if (key < p.element)
            p.LChild = DeleteBSTNode(p.LChild, key);
        else if (key > p.element)
            p.RChild = DeleteBSTNode(p.RChild, key);
        else { // 找到待删除节点
            if (p.LChild == null) // 左子树为空
                p = p.RChild;
            else if (p.RChild == null) // 右子树为空
                p = p.LChild;
            else { // 左右子树均不为空
                BST_3 temp = FindMinNode(p.RChild); // 找到右子树的最小节点
                p.element = temp.element; // 将该节点值赋给待删除节点
                p.RChild = DeleteBSTNode(p.RChild, temp.element); // 在右子树中删除该最小节点
            }
        }
        return p;
    }

    // 查找最小节点
    public static BST_3 FindMinNode(BST_3 p) {
        if (p == null)
            return null;
        else if (p.LChild == null)
            return p;
        else
            return FindMinNode(p.LChild);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String input = inputField.getText();
        try {
            int value = Integer.parseInt(input);
            if (e.getActionCommand().equals("Insert")) {
                System.out.println("插入元素 " + value);
                if(SearchBST(root,value)!=null){
                    JOptionPane.showMessageDialog(this, "待插入元素 " + value + " 已存在");
                }else {
                    root = InsertBST(root, value);
                }
            } else if (e.getActionCommand().equals("Delete")) {
                System.out.println("删除元素 " + value);
                if(SearchBST(root,value)==null){
                    JOptionPane.showMessageDialog(this, "待删除元素 " + value + "不存在 ");
                }else {
                    root = DeleteBSTNode(root, value);
                }
            }
            //重新绘制树
            repaint();
            inputField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入整数");
        }
    }

}
//待完善，输入的结点过多，超出屏幕范围，添加滑动窗口
