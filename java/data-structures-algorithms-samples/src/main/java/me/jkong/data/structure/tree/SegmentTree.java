package me.jkong.data.structure.tree;

/**
 * @author JKong
 * @version v1.0
 * @description 线段树
 * @date 2019/12/30 3:05 下午.
 * <p>
 * 二维线段树
 * 多维线段树
 * 动态线段树
 * 树状数组（Binary Index Tree）
 * <p>
 * RMQ问题（Range Minimum Query）
 */
public class SegmentTree<E> {
    
    private E[] data;
    private E[] tree;
    private Merger<E> merger;
    
    public SegmentTree(E[] arr, Merger<E> merger) {
        this.merger = merger;
        this.data = (E[]) new Object[arr.length];
        System.arraycopy(arr, 0, data, 0, arr.length);
        
        this.tree = (E[]) new Object[arr.length << 2];
        buildSegmentTree(0, 0, getSize() - 1);
    }
    
    /**
     * 构建线段树
     *
     * @param treeIndex  节点index
     * @param leftIndex  左边界
     * @param rightIndex 右边界
     */
    private void buildSegmentTree(int treeIndex, int leftIndex, int rightIndex) {
        
        if (leftIndex == rightIndex) {
            tree[treeIndex] = data[leftIndex];
            return;
        }
        
        int leftTreeIndex = leftChild(treeIndex);
        int rightTreeIndex = rightChild(treeIndex);
        int mildIndex = leftIndex + ((rightIndex - leftIndex) / 2);
        
        buildSegmentTree(leftTreeIndex, leftIndex, mildIndex);
        buildSegmentTree(rightTreeIndex, mildIndex + 1, rightIndex);
        
        tree[treeIndex] = merger.merge(tree[leftTreeIndex], tree[rightTreeIndex]);
    }
    
    /**
     * tree大小
     *
     * @return 元素数量
     */
    public int getSize() {
        return this.data.length;
    }
    
    /**
     * 当前节点左孩子的下标
     *
     * @param index 当前节点下标
     * @return 左孩子下标
     */
    private int leftChild(int index) {
        return 2 * index + 1;
    }
    
    /**
     * 获取当前节点点右孩子的下标
     *
     * @param index 当前节点下标
     * @return 右孩子节点下标
     */
    private int rightChild(int index) {
        return 2 * index + 2;
    }
    
    /**
     * 根据下标索引获取数据
     *
     * @param index 下标
     * @return 元素值
     */
    public E get(int index) {
        if (index < 0 || index >= data.length) {
            throw new IllegalArgumentException("Index is illegal.");
        }
        
        return this.data[index];
    }
    
    /**
     * 指定范围查询
     *
     * @param queryL 左边界
     * @param queryR 右边界
     * @return 元素值
     */
    public E query(int queryL, int queryR) {
        if (queryL > queryR || queryR > getSize() - 1 || queryL < 0) {
            throw new IllegalArgumentException("Index is illegal.");
        }
        
        return query(0, 0, getSize() - 1, queryL, queryR);
    }
    
    /**
     * 在以treeIndex为根的线段树中[l...r]的范围里，搜索区间[queryL...queryR]的值
     *
     * @param treeIndex 节点下标
     * @param l         左边界
     * @param r         右边界
     * @param queryL    查询左边界
     * @param queryR    查询右边界
     * @return 最终值
     */
    private E query(int treeIndex, int l, int r, int queryL, int queryR) {
        // 终止条件
        if (l == queryL && r == queryR) {
            return tree[treeIndex];
        }
        
        // 如果不是则需要继续递归计算
        int mid = l + (r - l) / 2;
        int leftTreeIndex = leftChild(treeIndex);
        int rightTreeIndex = rightChild(treeIndex);
        
        //判断是否查询的范围属于左右子树的子集
        if (queryL >= mid + 1) {
            return query(rightTreeIndex, mid + 1, r, queryL, queryR);
        } else if (queryR <= mid) {
            return query(leftTreeIndex, l, mid, queryL, queryR);
        }
        
        // 查询的范围左右节点都包含
        E leftQueryResult = query(leftTreeIndex, l, mid, queryL, mid);
        E rightQueryResult = query(rightTreeIndex, mid + 1, r, mid + 1, queryR);
        
        return merger.merge(leftQueryResult, rightQueryResult);
    }
    
    /**
     * 更新
     *
     * @param index 下标
     * @param e     元素
     * @return 原数据值
     */
    public E set(int index, E e) {
        if (index < 0 || index > getSize() - 1) {
            throw new IllegalArgumentException("Index is illegal.");
        }
        E ret = data[index];
        data[index] = e;
        set(0, 0, getSize() - 1, index, e);
        return ret;
    }
    
    private void set(int treeIndex, int l, int r, int index, E e) {
        // 递归终止条件
        if (l == r) {
            tree[treeIndex] = e;
            return;
        }
        
        // 继续递归
        int mid = l + (r - l) / 2;
        int leftTreeIndex = leftChild(index);
        int rightTreeIndex = rightChild(index);
        
        if (index >= mid + 1) {
            set(rightTreeIndex, mid + 1, r, index, e);
        } else {    // index <= mid
            set(leftTreeIndex, l, mid, index, e);
        }
        
        tree[treeIndex] = merger.merge(tree[leftTreeIndex], tree[rightTreeIndex]);
        
    }
    
    /**
     * 区间范围内所有元素更新（采用懒惰更新思维）
     * 借助lazy数组记录未更新的内容，然后下次对元素操作时先判断在lazy中时否有待更新的数据。
     *
     * @param leftIndex  区间的左边界
     * @param rightIndex 区间的右边界
     * @param e          元素
     */
    public void batchSet(int leftIndex, int rightIndex, E e) {
    
    }
    
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append('[');
        for (int i = 0; i < tree.length; i++) {
            if (tree[i] != null) {
                res.append(tree[i]);
            } else {
                res.append("null");
            }
            
            if (i != tree.length - 1) {
                res.append(", ");
            }
        }
        res.append(']');
        return res.toString();
    }
    
    
    public static void main(String[] args) {
        Integer[] nums = new Integer[]{-2, 0, 3, -5, 2, -1};
        SegmentTree<Integer> tree = new SegmentTree<>(nums, Integer::sum);
        System.out.println(tree);
        
        System.out.println(tree.query(0, 2));
        System.out.println(tree.query(2, 5));
        System.out.println(tree.query(0, 5));
    }
}