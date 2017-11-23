
[Choreographer1](http://blog.csdn.net/yangwen123/article/details/39518923)<br/>
[Choreographer2](http://www.jianshu.com/p/996bca12eb1d)


MSG_DO_FRAME：开始渲染下一帧的操作

MSG_DO_SCHEDULE_VSYNC：请求Vsync信号

MSG_DO_SCHEDULE_CALLBACK：请求执行callback


####
![关联图片](http://img.blog.csdn.net/20140630191939812?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveWFuZ3dlbjEyMw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast "这是关联图片")

![关联图片](http://upload-images.jianshu.io/upload_images/1688934-a4a5281df5b2bc14.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240 "这是关联图片")

![关联图片](http://img.blog.csdn.net/20140630192141906?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveWFuZ3dlbjEyMw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast
 "这是关联图片")




WindowManager
WindowManagerImpl
WindowManagerGlobal

````
ContextImpl.java

registerService(Context.WINDOW_SERVICE, WindowManager.class,
                new CachedServiceFetcher<WindowManager>() {
        @Override
        public WindowManager createService(ContextImpl ctx) {
            return new WindowManagerImpl(ctx.getDisplay());
        }});
````