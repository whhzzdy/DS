#include<iostream>
using namespace std;
#define ERROR 0
#define OK 1
#define Overflow 2 //表示上溢
#define Undrflow 3 //表示下溢出
#define NotPesent 4 //表示元素不存在
#define Duplicate 5 //表示有重复元素
typedef int ElemType;
typedef int Status;
typedef struct eNode {
	int adjVex;    //与任意顶点u相邻接的顶点
	ElemType w;    //边的权值
	struct eNode* nextArc;   //指向下一个边结点
}ENode;
typedef struct lGraph {
	int n;        //图的当前顶点数
	int e;        //图的当前边数
	ENode** a;    //指向一维指针数组
}LGraph;

//初始化
//构造一个有n个顶点，但不包含边的邻接表
//使用动态分配数组空间方式构造一个长度为n的一维指针数组a
Status Init(LGraph* lg, int nSize) {
	int i;
	lg->n = nSize;
	lg->e = 0;
	lg->a = (ENode**)malloc(nSize * sizeof(ENode*));//动态生成长度为n的一维指针数组
	if (!lg->a)
		return ERROR;
	else {
		for (i = 0; i < lg->n; i++) lg->a[i] = NULL; //将指针数组a置空
		return OK;
	}
}

//撤销
//释放邻接表中每条单链表的所有边结点
//释放一维指针数组a的存储空间
int Destroy(LGraph* lg) {
	int i;
	ENode* p, * q;
	for (i = 0; i < lg->n; i++) {
		p = lg->a[i];//指针p指向顶点i的单列表的第一个边结点
		q = p;
		while (p) {
			p = p->nextArc;
			free(p);
			q = p;
		}
	}
	free(lg->a);
	return 1;
}

//边的搜索
Status Exist(LGraph* lg, int u, int v) {
	ENode* p;
	if (u<0 || v<0 || u>lg->n - 1 || v>lg->n - 1 || u == v) {
		return ERROR;
	}
	p = lg->a[u];   //指针p指向顶点u的单链表的第一个边结点
	while (p && p->adjVex != v) p = p->nextArc;
	if (!p) {
		return ERROR;
	}
	else {
		return OK;
	}
}
//边的插入
Status Insert(LGraph* lg, int u, int v, ElemType w) {
	ENode* p;
	if (u<0 || v<0 || u>lg->n - 1 || v>lg->n - 1 || u == v) {
		return ERROR;
	}
	if (Exist(lg, u, v)) return Duplicate;
	p = (ENode*)malloc(sizeof(ENode));  //为新的边结点分配存储空间
	p->adjVex = v;
	p->w = w;
	p->nextArc = lg->a[u];    //将新的边结点插入单链表的最前面
	lg->a[u] = p;
	lg->e++;
	return OK;
}
//边的删除
Status Remove(LGraph* lg, int u, int v) {
	ENode* p, * q;
	if (u<0 || v<0 || u>lg->n - 1 || v>lg->n - 1 || u == v) {
		return ERROR;
	}
	p = lg->a[u], q = NULL;
	//查找待删除边是否存在
	while (p && p->adjVex != v) {
		q = p;
		p = p->nextArc;
	}
	if (!p) return ERROR; //待删除边不存在
	if (q) q->nextArc = p->nextArc;  //从单链表中删除此边
	else lg->a[u] = p->nextArc;
	free(p);
	lg->e--;
	return OK;
}
//邻接表的DFS
void DFS(int v, int visited[], LGraph g) {
	ENode* w;
	printf("%d", v);        //访问顶点v
	visited[v] = 1;              //为顶点v打上访问标记
	for (w = g.a[v]; w; w = w->nextArc)   //遍历v的邻接点
		if (!visited[w->adjVex])         //若w未被访问，则调用递归DFS
			DFS(w->adjVex, visited, g);

}

void DFSGraph(LGraph g) {
	int i;
	int* visited = (int*)malloc(g.n * sizeof(int));
	for (i = 0; i < g.n; i++) {
		visited[i] = 0;          //初始化visited数组
	}
	for (i = 0; i < g.n; i++)       //检查每一个顶点，若未被访问，则调用DFS
		if (!visited[i]) DFS(i, visited, g);
	free(visited);
}


int main() {
	LGraph g;
	int i, u, v, enode, edge;
	ElemType w;
	printf("Please enter the number of the ENodes:");
	scanf_s("%d", &enode);
	Init(&g, enode);
	printf("Please enter the number of the edges:");
	scanf_s("%d", &edge);
	printf("Now init the graph.\n");
	for (i = 0; i < edge; i++) {
		printf("Please enter the edge:");
		scanf_s("%d%d%d", &u, &v, &w);
		Insert(&g, u, v, w);
	}
	//delete one edge
	printf("Please enter the deleted edge:");
	printf("\nPlease enter the u of the edge:");
	scanf_s("%d", &u);
	printf("Please enter the v of the edge:");
	scanf_s("%d", &v);
	printf("Now search the edge:");
	if (Exist(&g, u, v)) printf("OK");
	else printf("ERROR");
	printf("\nNow delete the edge:");
	//search the deleted edge
	if (Remove(&g, u, v)) printf("OK");
	else printf("ERROR");
	//destroy
	printf("\nNow destroy the graph:");
	if (Destroy(&g)) printf("OK");
	else printf("ERROR");
	return 0;
	
}
