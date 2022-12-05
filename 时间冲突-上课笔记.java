


同学们好，欢迎来到享学课堂，我是今天的主讲 Leo老师，

我们正式 上课的时间 20：05，已经进来的同学请耐心等候下其他同学


少了一行：一定注意边界问题

ScrollView:heightMode MeasureSpec.UNSPECIFIED


case MeasureSpec.UNSPECIFIED:
	else if (childDimension == LayoutParams.MATCH_PARENT) 
		resultSize = View.sUseZeroUnspecifiedMeasureSpec ? 0 : size;
                resultMode = MeasureSpec.UNSPECIFIED;


Padding  Margin


FlowLayout在xml 中设置padding
1.测量时  
2.布局移动


子View中设置margin

自定义View：处理自己的 Padding，有父容器处理设置的 Margin

// mPaddingLeft FlowLayout xml中设置的padding值
// lp.leftMargin 子View 在xml 中设置的margin 值
childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                mPaddingLeft + mPaddingRight + lp.leftMargin + lp.rightMargin
                        + widthUsed, lp.width);

1. 得到父容器允许子View最大为多宽



父容器   子View   --》 包裹关系

parent   super  

View.dispatchTouchEvent --> 只有事件的处理逻辑
ViewGroup.dispatchTouchEvent --> 实现了分发流程的逻辑



事件接收流程
setView@ViewRootImpl.java
	// 接收事件
	--> mInputEventReceiver = new WindowInputEventReceiver(inputChannel,
                            Looper.myLooper());
onInputEvent@ViewRootImpl.java#WindowInputEventReceiver.java
	--> enqueueInputEvent
		--> doProcessInputEvents();
			--> deliverInputEvent(q);
				--> stage.deliver(q); (InputStage stage;)
deliver@ViewPostImeInputStage.java
	--> onProcess(q);
		--> processPointerEvent
			// mView --> DecorView
			--> boolean handled = mView.dispatchPointerEvent(event);
				--> dispatchTouchEvent(event);
// 执行到 cb == Activity
public boolean dispatchTouchEvent(MotionEvent ev) {
    final Window.Callback cb = mWindow.getCallback();
    return cb != null && !mWindow.isDestroyed() && mFeatureId < 0
            ? cb.dispatchTouchEvent(ev) : super.dispatchTouchEvent(ev);
}
dispatchTouchEvent@Activity.java
	--> getWindow().superDispatchTouchEvent(ev)
		--> mDecor.superDispatchTouchEvent(event);
			--> super.dispatchTouchEvent(event);
				--> ViewGroup.dispatchTouchEvent() // 事件分发机制	
					--> onTouchEvent()

View.dispatchTouchEvent() // 事件处理方法


mSyntheticInputStage = new SyntheticInputStage();
InputStage viewPostImeStage = new ViewPostImeInputStage(mSyntheticInputStage);
InputStage nativePostImeStage = new NativePostImeInputStage(viewPostImeStage,
        "aq:native-post-ime:" + counterSuffix);
InputStage earlyPostImeStage = new EarlyPostImeInputStage(nativePostImeStage);
InputStage imeStage = new ImeInputStage(earlyPostImeStage,
        "aq:ime:" + counterSuffix);
InputStage viewPreImeStage = new ViewPreImeInputStage(imeStage);
InputStage nativePreImeStage = new NativePreImeInputStage(viewPreImeStage,
        "aq:native-pre-ime:" + counterSuffix);



dispatchTouchEvent: 父容器
onInterceptTouchEvent: 父容器
dispatchTouchEvent: 子View
onTouch: 0
onTouchEvent: MotionEvent.ACTION_DOWN = 0


dispatchTouchEvent: 父容器
onInterceptTouchEvent: 父容器
dispatchTouchEvent: 子View
onTouch: 1
MotionEvent.ACTION_UP = 1
onClick

每一个事件都要经过：父容器 --》子View
Down，MOVE，


事件处理机制的几个重要方法：
1.dispatchTouchEvent
2.onInterceptTouchEvent
3.onTouchEvent

事件处理 
1.onTouch 和 onClick  执行的位置，关系
2.onTouchEvent 在哪儿执行的  
3.LongClick
4.按下移出View，为什么 onClick 不执行 ---》 cancel 1  不是2

View.dispatchTouchEvent
--> onTouch // 
	--> !result&&onTouchEvent // 短路与，当result 为true ，onTouchEvent不执行
		--> MotionEvent.ACTION_UP
			--> PerformClick
				--> performClickInternal
					--> onClick // 表示改事件 这个View消费了

500ms




掌握百分之六十到七十 --- UI --> 性能优化 专门的讲解



