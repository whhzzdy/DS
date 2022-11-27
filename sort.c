#include<stdio.h>
#include<stdlib.h>
#include<time.h>
typedef int KeyType;
typedef int DataType;
typedef int Status;
#define MaxSize 10000
typedef struct entry { //数据元素
	KeyType key;//排序关键字
	DataType data;
}Entry;
typedef struct list { //顺序表
	int n;   //待排序元素数量
	Entry D[MaxSize];  //静态数组存储数据元素
}List;
typedef struct maxheap {   //定义最大堆结构体
	int n;
	Entry D[MaxSize];

}MaxHeap;
//简单选择排序
int FindMin(List list, int startIndex) { //在startIndex至表尾范围内找到最小关键字元素下标
	int i, minIndex = startIndex;
	for (i = startIndex + 1; i < list.n; i++) {
		if (list.D[i].key < list.D[minIndex].key)
			minIndex = i;
	}
	return minIndex;
}
void Swap(Entry* D, int i, int j) { //交换顺序表中两个元素位置
	Entry temp;
	if (i == j) return;
	temp = *(D + i);
	*(D + i) = *(D + j);
	*(D + j) = temp;

}
void SelectSort(List* list) {
	int minIndex, startIndex = 0;
	while (startIndex < list->n - 1) {
		minIndex = FindMin(*list, startIndex);
		Swap(list->D, startIndex, minIndex);
		startIndex++;
	}
}
//直接插入排序
void InsertSort(List* list) {
	int i, j;//i为待插入元素下标
	Entry insertItem;//每一趟插入元素
	for (i = 1; i < list->n; i++) {
		insertItem = list->D[i];
		for (j = i - 1; j >= 0; j--) {
			//不断将有序序列中元素向后移动，为待插入元素空出一个位置
			if (insertItem.key < list->D[j].key)
				list->D[j + 1] = list->D[j];
			else break;
		}
		list->D[j + 1] = insertItem;//待插入元素有序存放至有序序列中
	}
}
//冒泡排序
void BubbleSort(List* list) {
	int i, j;//i标识每趟排序范围最后一个元素下标，每趟排序元素下标范围是0-i
	for (i = list->n - 1; i > 0; i--) {
		int isSwap = 0;//一趟排序是否发生了交换
		for (j = 0; j < i; j++) {
			if (list->D[j].key > list->D[j + 1].key) {
				Swap(list->D, j, j + 1);
				isSwap = 1;
			}
		}
		if (!isSwap) break;//没有发生元素排序，排序完成
	}
}
//快速排序算法
//序列划分方法
int Partition(List* list, int low, int high) {
	int i = low, j = high + 1;
	Entry pivot = list->D[low];//pivot是分割元素
	do {
		do i++; while (i < high && list->D[i].key < pivot.key);
		do j--; while (list->D[j].key > pivot.key);
		if (i < j) Swap(list->D, i, j);
	} while (i < j);
	Swap(list->D, low, j);
	return j; //j是分割元素下标
}
//快速排序的递归函数
void QuickSort(List* list, int low, int high) {
	int k;
	if (low < high) {
		k = Partition(list, low, high);
		QuickSort(list, low, k - 1);
		QuickSort(list, k + 1, high);
	}
}
//快速排序算法的主调用函数
void QuickSort(List* list) {
	QuickSort(list, 0, list->n - 1);
}
//两路合并方法
//n1,n2是两个子序列长度，low是第一个子序列第一个元素下标
void Merge(List* list, Entry* temp, int low, int n1, int n2) {
	int i = low, j = low + n1;//i,j初始时分别指向两个序列的第一个元素
	while (i <= low + n1 - 1 && j <= low + n1 + n2 - 1) {
		if (list->D[i].key <= list->D[j].key)
			*temp++ = list->D[i++];
		else *temp++ = list->D[j++];

	}
	while (i <= low + n1 - 1)
		*temp++ = list->D[i++];//剩余元素直接复制到temp
	while (j <= low + n1 + n2 - 1)
		*temp++ = list->D[j++];
}
void MergeSort(list* list) {
	Entry temp[MaxSize];
	int low, n1, n2, i, size = 1;
	while (size < list->n) {
		low = 0;//low是一堆待合并序列中第一个序列的第一个元素下标
		while (low + size < list->n) { //说明至少存在两个子序列需要合并
			n1 = size;
			if (low + size * 2 < list->n)
				n2 = size;//计算第二个序列长度
			else
				n2 = list->n - low - size;
			Merge(list, temp + low, low, n1, n2);
			low += n1 + n2; //确定下一对待合并序列中第一个序列的第一个元素下标

		}
		for (i = 0; i < low; i++) {
			list->D[i] = temp[i]; //复制一趟合并排序结果
		}
		size *= 2;//子序列长度翻倍
	}

}
//堆排序算法
void AdjustDown( Entry *heap, int current, int border) {
	int p = current;
	int maxChild;
	Entry temp;
	while (2 * p + 1 <= border) {  //若p不是叶结点，则进行调整
		if ((2 * p + 2 <= border) && (heap[2 * p + 1].key < heap[2 * p + 2].key))
			maxChild = 2 * p + 2;
		else
			maxChild = 2 * p + 1;
		if (heap[p].key >= heap[maxChild].key)
			break;
		else {
			temp = heap[p];
			heap[p] = heap[maxChild];
			heap[maxChild] = temp;
			p = maxChild;
		}
			
	}
}
void HeapSort(MaxHeap* hp) {
	int i; Entry temp;
	for (i = (hp->n - 2) / 2; i >= 0; i--) {
		AdjustDown(hp->D, i, hp->n - 1);
	}
	for (i = hp->n - 1; i > 0; i--) {
		Swap(hp->D, 0, i);//交换堆底与堆顶元素
		AdjustDown(hp->D, 0, i - 1);
	}
}
//顺序表
int main() {
	List list;
	list.n = 500;
	int i;
	srand((unsigned int)time(NULL));
	for (i = 0; i < list.n; i++) {
		list.D[i].key = rand() % 1000 + 1;
	}
	SelectSort(&list);
	int j;
	for (j = 0; j < list.n; j++) {
		printf("%d ", list.D[j].key);
	}
	printf("\n");

}
//堆
int main() {
	MaxHeap hp;
	hp.n = 8;
	int i,j;
	srand((unsigned int)time(NULL));
	for (i = 0; i < hp.n; i++) {
		hp.D[i].key = rand() % 1000 + 1;
	}
	printf("\n");
	HeapSort(&hp);
	for (j = 0; j < hp.n; j++) {
		printf("%d ", hp.D[j].key);
	}
}
