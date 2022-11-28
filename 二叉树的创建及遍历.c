#include<stdio.h>
#include<stdlib.h>
#define ElemType int

typedef struct btnode {                     // 二叉树节点结构体
    ElemType element;
    struct btnode* lChild;
    struct btnode* rChild;
}BTNode;


//先序遍历
void PreOrderTransverse(BTNode* t) {
    if (t == NULL) {
        return;
    }
    printf("%c", t->element);        //打印输出根结点，此处可以定义其他操作
    PreOrderTransverse(t->lChild);  //然后先序遍历左子树
    PreOrderTransverse(t->rChild);  //最后先序遍历右子树
}


//中序遍历 LVR
void InOrderTransverse(BTNode* t) {
    if (t == NULL) {
        return;
    }
    InOrderTransverse(t->lChild);  // 中序遍历根结点的左子树
    printf("%c", t->element);          //打印输出根结点
    InOrderTransverse(t->rChild);  // 中序遍历根结点的右子树
}


//后序遍历
void PostOrderTransverse(BTNode* t) {
    if (t == NULL) {
        return;
    }
    PostOrderTransverse(t->lChild);  //后序遍历根结点的左子树
    PostOrderTransverse(t->rChild);  //然后后序遍历根结点的右子树
    printf("%c", t->element);            //最后打印输出根结点，此处可以定义其他操作
}


//先序构建二叉树
BTNode* PreCreateBt(BTNode* t) {
    char c;
    c = getchar();
    if (c == '#') {                             // 输入#表示此节点没有孩子
        t = NULL;
    }
    else {
        t = (BTNode*)malloc(sizeof(BTNode));
        t->element = c;                         //构造根结点
        t->lChild = PreCreateBt(t->lChild);     //构造左子树
        t->rChild = PreCreateBt(t->rChild);     //构造右子树
    }
    return t;
}


// 123##4##5#6##
int main() {
    BTNode* t = NULL;

    // 为二叉树添加元素
    printf("请输入先序遍历的二叉树序列\n");
    t = PreCreateBt(t);

    printf("\n先序遍历结果:\n");
    PreOrderTransverse(t);

    printf("\n\n中序遍历结果:\n");
    InOrderTransverse(t);

    printf("\n\n后序遍历结果:\n");
    PostOrderTransverse(t);

    printf("\n");

    return 0;
}
