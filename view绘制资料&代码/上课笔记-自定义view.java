


同学们好，欢迎来到享学课堂，我是今天的主讲 Leo老师，

我们正式 上课的时间 20：05，已经进来的同学请耐心等候下其他同学




1. ViewGroup为什么不会执行 onDraw ？
View.draw(canvas) (DecorView)
--> onDraw(canvas);
下面的流程是一个递归动作
--> dispatchDraw(canvas); (ViewGroup.dispatchDraw)
	--> drawChild
		--> View.draw(Canvas canvas, ViewGroup parent, long drawingTime)
			--> renderNode = updateDisplayListIfDirty();
				--> dispatchDraw(canvas);


一.文字变色 -- 自定义View
1.自定义属性，以及xml中使用
2.测量 --- 只需要测量自己  
3.onDraw:绘制自己
4.交互


FontMetricInt.top  负数
FontMetricInt.bottom  正数

裁剪，只显示 裁剪后 canvas.drawXxx 的内容
cavas.clipRect();


二.流式布局 -- 自定义ViewGroup
1.自定义属性，以及xml中使用
2.测量 --- 先测量子View，再根据子View尺寸，计算自己的，保存尺寸给后面用
3.布局 onLayout --- 根据自己的规则确定child 的位置
4.绘制 -- onDraw(正常不会调用) 重写dispatchDraw -- 一般不会用
5.交互


























