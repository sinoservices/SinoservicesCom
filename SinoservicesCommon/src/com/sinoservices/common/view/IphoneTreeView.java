package com.sinoservices.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

public class IphoneTreeView extends ExpandableListView implements
OnScrollListener, OnGroupClickListener {
public IphoneTreeView(Context context, AttributeSet attrs, int defStyle) {
super(context, attrs, defStyle);
registerListener();
}

public IphoneTreeView(Context context, AttributeSet attrs) {
super(context, attrs);
registerListener();
}

public IphoneTreeView(Context context) {
super(context);
registerListener();
}

/**
* Adapter �ӿ� . �б�����ʵ�ִ˽ӿ� .
*/
public interface IphoneTreeHeaderAdapter {
public static final int PINNED_HEADER_GONE = 0;
public static final int PINNED_HEADER_VISIBLE = 1;
public static final int PINNED_HEADER_PUSHED_UP = 2;

/**
 * ��ȡ Header ��״̬
 * 
 * @param groupPosition
 * @param childPosition
 * @return 
 *         PINNED_HEADER_GONE,PINNED_HEADER_VISIBLE,PINNED_HEADER_PUSHED_UP
 *         ����֮һ
 */
int getTreeHeaderState(int groupPosition, int childPosition);

/**
 * ���� QQHeader, �� QQHeader ֪����ʾ������
 * 
 * @param header
 * @param groupPosition
 * @param childPosition
 * @param alpha
 */
void configureTreeHeader(View header, int groupPosition,
		int childPosition, int alpha);

/**
 * �����鰴�µ�״̬
 * 
 * @param groupPosition
 * @param status
 */
void onHeadViewClick(int groupPosition, int status);

/**
 * ��ȡ�鰴�µ�״̬
 * 
 * @param groupPosition
 * @return
 */
int getHeadViewClickStatus(int groupPosition);

public int getChildCount(int groupPosition);

}

private static final int MAX_ALPHA = 255;

private IphoneTreeHeaderAdapter mAdapter;

/**
* �������б�ͷ��ʾ�� View,mHeaderViewVisible Ϊ true �ſɼ�
*/
private View mHeaderView;

/**
* �б�ͷ�Ƿ�ɼ�
*/
private boolean mHeaderViewVisible;

private int mHeaderViewWidth;

private int mHeaderViewHeight;

public void setHeaderView(View view) {
mHeaderView = view;
AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
		ViewGroup.LayoutParams.MATCH_PARENT,
		ViewGroup.LayoutParams.WRAP_CONTENT);
view.setLayoutParams(lp);

if (mHeaderView != null) {
	setFadingEdgeLength(0);
}

requestLayout();
}

private void registerListener() {
setOnScrollListener(this);
setOnGroupClickListener(this);
}

/**
* ��� HeaderView �������¼�
*/
private void headerViewClick() {
long packedPosition = getExpandableListPosition(this
		.getFirstVisiblePosition());

int groupPosition = ExpandableListView
		.getPackedPositionGroup(packedPosition);

if (mAdapter.getHeadViewClickStatus(groupPosition) == 1) {
	this.collapseGroup(groupPosition);
	mAdapter.onHeadViewClick(groupPosition, 0);
} else {
	this.expandGroup(groupPosition);
	mAdapter.onHeadViewClick(groupPosition, 1);
}

this.setSelectedGroup(groupPosition);
}

private float mDownX;
private float mDownY;

/**
* ��� HeaderView �ǿɼ��� , �˺��������ж��Ƿ����� HeaderView, ��������Ӧ�Ĵ��� , ��Ϊ HeaderView
* �ǻ���ȥ�� , ���������¼���������Ч�� , ֻ�����п��� .
*/
@Override
public boolean onTouchEvent(MotionEvent ev) {
if (mHeaderViewVisible) {
	switch (ev.getAction()) {
	case MotionEvent.ACTION_DOWN:
		mDownX = ev.getX();
		mDownY = ev.getY();
		if (mDownX <= mHeaderViewWidth && mDownY <= mHeaderViewHeight) {
			return true;
		}
		break;
	case MotionEvent.ACTION_UP:
		float x = ev.getX();
		float y = ev.getY();
		float offsetX = Math.abs(x - mDownX);
		float offsetY = Math.abs(y - mDownY);
		// ��� HeaderView �ǿɼ��� , ����� HeaderView �� , ��ô���� headerClick()
		if (x <= mHeaderViewWidth && y <= mHeaderViewHeight
				&& offsetX <= mHeaderViewWidth
				&& offsetY <= mHeaderViewHeight) {
			if (mHeaderView != null) {
				headerViewClick();
			}

			return true;
		}
		break;
	default:
		break;
	}
}

return super.onTouchEvent(ev);

}

@Override
public void setAdapter(ExpandableListAdapter adapter) {
super.setAdapter(adapter);
mAdapter = (IphoneTreeHeaderAdapter) adapter;
}

public static final int COLLAPSED = 0;//����״̬
public static final int EXPANDED = 1;//չ��״̬
/**
* 
* ����� Group �������¼� , Ҫ���ݸ��ݵ�ǰ��� Group ��״̬��
*/
@Override
public boolean onGroupClick(ExpandableListView parent, View v,
	int groupPosition, long id) {
if (mAdapter.getHeadViewClickStatus(groupPosition) == COLLAPSED) {
	mAdapter.onHeadViewClick(groupPosition, EXPANDED);
	parent.expandGroup(groupPosition);
	smoothScrollToPosition(groupPosition);// չ���󣬸ı�ѡ��λ��

} else if (mAdapter.getHeadViewClickStatus(groupPosition) == EXPANDED) {
	mAdapter.onHeadViewClick(groupPosition, COLLAPSED);
	parent.collapseGroup(groupPosition);
}

// ���� true �Ѵ�������¼������������´�,��Ȼ����Ҳ�ᴦ���˵���¼�����������
return true;

}

@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
super.onMeasure(widthMeasureSpec, heightMeasureSpec);
if (mHeaderView != null) {
	measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
	mHeaderViewWidth = mHeaderView.getMeasuredWidth();
	mHeaderViewHeight = mHeaderView.getMeasuredHeight();
}
}

private int mOldState = -1;

@Override
protected void onLayout(boolean changed, int left, int top, int right,
	int bottom) {
super.onLayout(changed, left, top, right, bottom);
final long flatPostion = getExpandableListPosition(getFirstVisiblePosition());
final int groupPos = ExpandableListView
		.getPackedPositionGroup(flatPostion);
final int childPos = ExpandableListView
		.getPackedPositionChild(flatPostion);
int state = mAdapter.getTreeHeaderState(groupPos, childPos);
if (mHeaderView != null && mAdapter != null && state != mOldState) {
	mOldState = state;
	mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
}

configureHeaderView(groupPos, childPos);
}

public void configureHeaderView(int groupPosition, int childPosition) {
if (mHeaderView == null || mAdapter == null
		|| ((ExpandableListAdapter) mAdapter).getGroupCount() == 0) {
	return;
}

int state = mAdapter.getTreeHeaderState(groupPosition, childPosition);

switch (state) {
case IphoneTreeHeaderAdapter.PINNED_HEADER_GONE: {
	mHeaderViewVisible = false;
	break;
}

case IphoneTreeHeaderAdapter.PINNED_HEADER_VISIBLE: {
	mAdapter.configureTreeHeader(mHeaderView, groupPosition,
			childPosition, MAX_ALPHA);

	if (mHeaderView.getTop() != 0) {
		mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
	}

	mHeaderViewVisible = true;

	break;
}

case IphoneTreeHeaderAdapter.PINNED_HEADER_PUSHED_UP: {
	View firstView = getChildAt(0);
	int bottom = firstView.getBottom();

	// intitemHeight = firstView.getHeight();
	int headerHeight = mHeaderView.getHeight();

	int y;

	int alpha;

	if (bottom < headerHeight) {
		y = (bottom - headerHeight);
		alpha = MAX_ALPHA * (headerHeight + y) / headerHeight;
	} else {
		y = 0;
		alpha = MAX_ALPHA;
	}

	mAdapter.configureTreeHeader(mHeaderView, groupPosition,
			childPosition, alpha);

	if (mHeaderView.getTop() != y) {
		mHeaderView.layout(0, y, mHeaderViewWidth, mHeaderViewHeight
				+ y);
	}

	mHeaderViewVisible = true;
	break;
}
}
}

@Override
/**
* �б��������ʱ���ø÷���(�����ʱ)
*/
protected void dispatchDraw(Canvas canvas) {
super.dispatchDraw(canvas);
if (mHeaderViewVisible) {
	// ��������ֱ�ӻ��Ƶ������У������Ǽ��뵽ViewGroup��
	drawChild(canvas, mHeaderView, getDrawingTime());
}
}

@Override
public void onScroll(AbsListView view, int firstVisibleItem,
	int visibleItemCount, int totalItemCount) {
final long flatPos = getExpandableListPosition(firstVisibleItem);
int groupPosition = ExpandableListView.getPackedPositionGroup(flatPos);
int childPosition = ExpandableListView.getPackedPositionChild(flatPos);

configureHeaderView(groupPosition, childPosition);
}

@Override
public void onScrollStateChanged(AbsListView view, int scrollState) {
}
}