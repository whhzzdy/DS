#include<stdio.h>
#include<stdlib.h>
#include<algorithm>
#define ElemType int

typedef struct btnode {
    ElemType element;
    struct btnode* lChild;
    struct btnode* rChild;
}BTNode;

//先序遍历构建二叉树
BTNode* PreCreateBt(BTNode* t) {
    char ch;
    ch = getchar();
    if (ch == '#') {                           //输入为#表示这里建立空二叉树，即遍历算法的空操作
        t = NULL;
    }
    else {
        t = (BTNode*)malloc(sizeof(BTNode));
        t->element = ch;                        //构造根结点
        t->lChild = PreCreateBt(t->lChild);  //构造左子树
        t->rChild = PreCreateBt(t->rChild);  //构造右子树
    }
    return t;
}


//先序遍历
void PreorderTransverse(BTNode* t) {
    if (t == NULL) {
        return;
    }
    printf("%c", t->element);           //打印输出根结点，此处可以定义其他操作
    PreorderTransverse(t->lChild);  //然后先序遍历左子树
    PreorderTransverse(t->rChild);  //最后先序遍历右子树
}


//中序遍历
void MediumorderTransverse(BTNode* t) {
    if (t == NULL) {
        return;
    }
    MediumorderTransverse(t->lChild);  //中序遍历根结点的左子树
    printf("%c", t->element);          //打印输出根结点，此处可以定义其他操作
    MediumorderTransverse(t->rChild);  //最后中序遍历根结点的右子树
}


//后序遍历
void PostorderTransverse(BTNode* t) {
    if (t == NULL) {
        return;
    }
    PostorderTransverse(t->lChild);  //后序遍历根结点的左子树
    PostorderTransverse(t->rChild);  //然后后序遍历根结点的右子树
    printf("%c", t->element);            //最后打印输出根结点，此处可以定义其他操作
}


//求二叉树结点个数
int GetNodeNum(BTNode* t) {
    if (t == NULL) return 0;
    return GetNodeNum(t->lChild) + GetNodeNum(t->rChild) + 1;
}


//求二叉树叶子结点个数
int GetLeafNum(BTNode* t) {
    if (t == NULL) return 0;
    if ((t->lChild == NULL) && (t->rChild == NULL)) return 1;
    return GetLeafNum(t->lChild) + GetLeafNum(t->rChild);
}


//求二叉树的高度
int GetTreeHeight(BTNode* t) {
    if (t == NULL) return 0;
    else return 1 + std::max(GetTreeHeight(t->lChild), GetTreeHeight(t->rChild));
}


//交换二叉树所有子树
void SwapSubTree(BTNode* t) {
    if (t) {
        BTNode* temp = t->lChild;
        t->lChild = t->rChild;
        t->rChild = temp;
        SwapSubTree(t->lChild);
        SwapSubTree(t->rChild);
    }
}


/*
*     测试的树形
           1
         2   5
        3 4 # 6
       ## ##  ##
*/


// 123##4##5#6##
int main() {
    BTNode* t = NULL;

    // 为二叉树添加元素
    printf("请输入先序遍历的二叉树序列\n");
    t = PreCreateBt(t);

    printf("二叉树的节点数:%d\n", GetNodeNum(t));
    printf("二叉树的叶子数:%d\n", GetLeafNum(t));
    printf("二叉树的树高度:%d\n", GetTreeHeight(t));

    SwapSubTree(t);
    printf("\n交换所有左右子树后:\n\n");
    printf("先序遍历:\n");
    PreorderTransverse(t);
    printf("\n\n中序遍历:\n");
    MediumorderTransverse(t);
    printf("\n\n后序遍历:\n");
    PostorderTransverse(t);
    printf("\n");
    return 0;
}

