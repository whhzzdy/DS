#include<iostream>
#include<stdlib.h>
using namespace std;
#define ERROR 0
#define OK 1
#define Overflow 2 //表示上溢
#define Undrflow 3 //表示下溢出
#define NotPesent 4 //表示元素不存在
#define Duplicate 5 //表示有重复元素
typedef int ElemType;
typedef int Status;
typedef struct mGraph {
	ElemType** a;      //邻接矩阵
	int n;             //图的当前顶点数
	int e;             //图的当前边数
	ElemType noEdge;//两顶点间无边时的值
}MGraph;
//循环队列的结构体
typedef struct queue {
	int front;
	int rear;
	int maxSize;
	ElemType* element;
}Queue;
//创建一个能容纳mSize的空队列
void create(Queue* Q, int mSize) {
	Q->maxSize = mSize;
	Q->element = (ElemType*)malloc(sizeof(ElemType) * mSize);
	Q->front = Q->rear=0;
}
bool IsEmpty(Queue* Q) {
	return Q->front == Q->rear;
}
bool IsFULL(Queue* Q) {
	return (Q->rear + 1) % Q->maxSize == Q->front;
}
//获取队头元素,并通过x返回.若操作成功,则返回TRUE,否则返回FALSE
bool Front(Queue* Q, ElemType* x) {
	if (IsEmpty(Q))      //空队列处理
		return false;
	*x = Q->element[(Q->front + 1) % Q->maxSize];
	return true;
}


//入队.在队列Q的队尾插入元素x(入队操作)。操作成功,则返回TRUE,否则返回FALSE
bool EnQueue(Queue* Q, ElemType x) {
	if (IsFULL(Q))      //溢出处理
		return false;
	Q->rear = (Q->rear + 1) % Q->maxSize;
	Q->element[Q->rear] = x;
	return true;
}


//出队.从队列Q中删除队头元素(出队操作)。操作成功,则返回TRUE,否则返回FALSE
bool DeQueue(Queue* Q) {
	if (IsEmpty(Q)) {   //空队列处理
		return false;
	}
	Q->front = (Q->front + 1) % Q->maxSize;
	return true;
}


//邻接矩阵的初始化
Status Init(mGraph* mg, int nSize, ElemType noEdgeValue) {
	int i, j;
	mg->n = nSize;               //初始化顶点数
	mg->e = 0;                   //初始时没有边
	mg->noEdge = noEdgeValue;    //初始化没有边的取值
	mg->a = (ElemType**)malloc(nSize * sizeof(ElemType*));//生成长度为n的一维指针数组
	if (!mg->a)
		return ERROR;
	//动态生成二维数组
	for (i = 0; i < mg->n; i++) {
		mg->a[i] = (ElemType*)malloc(nSize * sizeof(ElemType)); 
		for (j = 0; j < mg->n; j++) mg->a[i][j] = mg->noEdge;
		mg->a[i][i] = 0;
	}
	return OK;
}
//撤销邻接矩阵
void Destroy(mGraph* mg)
{
	int i = 0;
	for (i = 0; i < mg->n; i++)
		free(mg->a[i]);//释放n个一维数组的存储空间
	free(mg->a);    //释放一维指针数组的存储空间
}
//边的搜索,若参数u，v无效或者边不存在，返回ERROR
Status Exist(mGraph* mg, int u, int v) {
	if (u<0 || v<0 || u>mg->n - 1 || v>mg->n - 1 || u == v || mg->a[u][v] == mg->noEdge)
		return ERROR;
	return OK;
}
//边的插入
Status Insert(mGraph* mg, int u, int v, ElemType w) {
	if (u<0 || v<0 || u>mg->n - 1 || v>mg->n - 1 || u == v) {
		return ERROR;
	}
	if (mg->a[u][v] != mg->noEdge) {
		return Duplicate; //待插入边已经存在，返回错误信息
	}
	mg->a[u][v] = w; //插入新边
	mg->e++;
	return OK;
}
//边的删除
Status Remove(mGraph* mg, int u, int v) {
	if (u<0 || v<0 || u>mg->n - 1 || v>mg->n - 1 || u == v)
		return ERROR;
	if (mg->a[u][v] == mg->noEdge) //待删除边不存在
		return NotPesent;
	mg->a[u][v] = mg->noEdge;  //删除边
	mg->e--;
	return OK;
}


//图的深度优先遍历(DFS)
void DFS(int v, int visited[], mGraph mg) {
	int j;
	printf("%d ", v);              //访问顶点v
	visited[v] = 1;               //为顶点v打上访问标记       
	for (j = 0; j < mg.n; j++) {      //遍历v的邻接点
		if (!visited[j] && mg.a[v][j] > 0) {  //当未被访问且有权值
			DFS(j, visited, mg);
		}
	}
}
//邻接矩阵DFS
void DFSGraph(mGraph mg) {
	int i;
	int* visited = (int*)malloc(mg.n * sizeof(int)); //动态生成标记数组visted
	for (i = 0; i < mg.n; i++) {
		visited[i] = 0;          //visted数组初始化
	}                            //visted数组初始化
	for (i = 0; i < mg.n; i++) {     //逐一检查每个顶点,若未被访问,则调用DFS
		if (!visited[i]) {   //当未被访问且有权值
			DFS(i, visited, mg);
		}
	}
	free(visited);                       //释放visted数组
}
//邻接矩阵单一顶点BFS（宽度优先）
void BFS(int v, int visited[], mGraph g) {
	Queue q;
	create(&q, g.n);                        //初始化队列
	visited[v] = 1;                        //为顶点v打上访问标记
	printf("%d ", v);                       //访问顶点v
	EnQueue(&q, v);                         //将顶点v放入队列
	while (!IsEmpty(&q)) {
		Front(&q, &v);
		DeQueue(&q);                       //队首顶点出队列
		for (int i = 0; i < g.n; i++) {       //遍历v的每一项
			if (!visited[i] && g.a[v][i] > 0) {       //若未被访问且有权值,则将其访问并放入队列,注意这里判断的是g.a[v][i]二维数组
				visited[i] = 1;
				printf("%d ", i);
				EnQueue(&q, i);
			}
		}
	}
}
//邻接矩阵的全图BFS
void BFSGraph(mGraph g) {
	int i;
	int* visited = (int*)malloc(g.n * sizeof(int));  //动态生成visited数组
	for (i = 0; i < g.n; i++) {                         //初始化visited数组
		visited[i] = 0;
	}
	for (i = 0; i < g.n; i++) {                        //逐一检查每个顶点,若未被访问,则调用BFS
		if (!visited[i]) {
			BFS(i, visited, g);
		}
	}
	free(visited);
}


int main() {
	mGraph mg;
	int nSize, edge, u, v, i,j;
	ElemType w;
	printf("Please enter the size of the mgraph:");
	scanf_s("%d", &nSize);
	Init(&mg, nSize, -1);
	printf("Please enter the number of the edges:");
	scanf_s("%d", &edge);
	printf("Now init the graph.\n");
	for (i = 0; i < edge; i++) {
		printf("Please enter the edge:");
		scanf_s("%d%d%d", &u, &v, &w);
		Insert(&mg, u, v, w);
	}
	printf("\n邻接矩阵为：\n");
	for (i = 0; i < mg.n; i++) {
		for (j = 0; j < mg.n; j++) {
			printf("%6d", mg.a[i][j]);
		}
		printf("\n");
	}
	printf("当前顶点数：%d\n", mg.n);
	printf("当前边的数目:%d\n", mg.e);
	printf("DFS\n");
	DFSGraph(mg);
	printf("\nBFS\n");
	BFSGraph(mg);
	Destroy(&mg);
}
