#include<stdio.h>
#include<stdlib.h>
#define ERROR 0
#define OK 1
#define Overflow 2
#define Underflow 3
#define Notpresent 4
#define Duplicate 5 //有重复元素
typedef int Status;
typedef int ElemType;
typedef struct node
{
	ElemType element;
	struct node* link;
}Node;
typedef struct headerList {
	Node* head;
	//单链表中元素的个数
	int n;
}HeaderList;
//带表头节点的单链表的初始化
Status  Init(HeaderList* h) {
	//生成表头节点
	h->head = (Node*)malloc(sizeof(Node));
	if (!h->head)
		return ERROR;
	h->head->link = NULL;
	h->n = 0;
	return OK;
}
//插入运算
Status Insert(HeaderList* h, int i, ElemType x) {
	Node* p, * q;
	int j;
	if (i<-1 || i>h->n - 1)
		return ERROR;
	p = h->head;
	for (j = 0; j <= i; j++)
		p = p->link;
	q = (Node*)malloc(sizeof(Node));
	q->element = x;
	q->link = p->link;
	p->link = q;
	h->n++;
	return OK;
}
//删除
Status Delete(HeaderList* h, int i) {
	int j;
	Node* p, * q;
	if (!h->n) {
		return ERROR;
	}
	if (i<0 || i>h->n - 1)
		return ERROR;
	q = h->head;
	for (j = 0; j < i; j++) {
		q = q->link;
	}
	p = q->link;
	//删除p所指结点
	q->link = p->link;
	free(p);
	h->n--;
	return OK;
}

//输出
Status Output(HeaderList* h) {
	Node* p;
	if (!h->n)
		return ERROR;
	p = h->head;
	p = p->link;
	while (p) {
		printf("%d ", p->element);
		p = p->link;
	}
	return OK;
}
//撤销
void Destroy(HeaderList* h) {
	Node* p;
	while (h->head) {
		//保存后继结点地址
		p = h->head->link;
		//释放表头结点存储空间
		free(h->head);
		h->head = p;
	}
}
//排序
Status sort(HeaderList* h) {
	Node* p, * q;
	int t;
	for (p = h->head->link; p != NULL; p = p->link) {
		for (q = p->link; q != NULL; q = q->link) {
			if (p->element > q->element) {
				t = p->element;
				p->element = q->element;
				q->element = t;//交换
			}
		}
	}

}
//简单选择排序
Status SelectSort(HeaderList* h) {
	Node* p, * q,*s;
	int n = h->n;
	int min;
	p = h->head;
	while (p->link) {
		q = p; s = p->link; min = s->element;
		while (s->link) {
			if (min > s->link->element) {
				min = s->link->element;
				q = s;
			}
			s = s->link;
		}
		s = q->link; q->link = s->link;//删除min结点
		q = p->link; p->link = s; s->link = q;//插入min结点
		p = s;
	}

}
//直接插入排序
Status InsertSort(HeaderList* h) {
	//把单链表元素存入数组
	Node* v;
	int i,j;
	int * arr = (int*)malloc(sizeof(int) * h->n);
	v = h->head->link;
	for (j = 0; j < h->n; j++)
	{
		arr[j] = v->element;
		v= v->link;
	}
	Node* t, * p, * q, * s;
	//t为带头结点
	t = h->head;
	//s是存储每一个新加入元素的节点 
	s= (Node*)malloc(sizeof(Node));
	s->element = arr[0];
	s->link = NULL;
	t->link = s;
	//从数组的第二个元素开始执行插入排序，p为前驱，q为后继
	//每次新加入的元素与q中元素进行比较。 
	for (i = 1; i < h->n; i++)
	{
		p = t;
		q = p->link;
		s = (Node*)malloc(sizeof(Node));
		s->element = arr[i];
		s->link = NULL;
		//找到插入位置，如果新来的元素比q中的大，则向后查找，
		//直到q的指针域为空。 
		while ((s->element > q->element) && (q->link != NULL))
		{
			p = q;
			q = q->link;
		}
		//新来的元素大，则插入到q之后 
		if (q->link == NULL && (s->element > q->element))
		{
			s->link = NULL;
			q->link = s;
		}
		else//新来的元素小，则插入到p之后，即q之前 
		{
			p->link = s;
			s->link = q;
		}
	}
	
}
void main() {
	int i;
	int x;

	HeaderList h;
	Init(&h);
	//插入
	for (i = 0; i < 9; i++)
	{
		scanf_s("%d", &x);
		Insert(&h, i - 1, x);
	}
	//打印
	printf("the linklist is:");
	Output(&h);
	InsertSort(&h);
	printf("\n排序后为：");
	Output(&h);
	Destroy(&h);
}
