1.初始化serviceManager， ProcessState<br>

framework\base\libs\binder\IServiceManager.cpp

````
sp<IServiceManager> defaultServiceManager(){
     gDefaultServiceManager = interface_cast<IServiceManager>(
            ProcessState::self()->getContextObject(NULL));
}
````
2.初始化BpBinder

````
sp<IBinder> ProcessState::getContextObject(const sp<IBinder>& caller) {
     return getStrongProxyForHandle(0);//注意，这里传入0
}

sp<IBinder> ProcessState::getStrongProxyForHandle(int32_t handle) {
	b = new BpBinder(handle); --->看见了吧，创建了一个新的BpBinder
	result = b;
	return result; 返回刚才创建的BpBinder。
}
````

3.初始化 IPCThreadState

````
BpBinder::BpBinder(int32_t handle) 
	: mHandle(handle) //注意，接上述内容，这里调用的时候传入的是0
    ,mAlive(1), mObitsSent(0), mObituaries(NULL) {
   IPCThreadState::self()->incWeakHandle(handle);//FT，竟然到	IPCThreadState::self()
}
````

4.interface_cast

````
gDefaultServiceManager = interface_cast<IServiceManager>(new BpBinder(0));
IInterface.h位于framework/base/include/binder/IInterface.h

template<typename INTERFACE>
inline sp<INTERFACE> interface_cast(const sp<IBinder>& obj) {
    return INTERFACE::asInterface(obj);
}
所以，上面等价于：
inline sp<IServiceManager> interface_cast(const sp<IBinder>& obj) {
    return IServiceManager::asInterface(obj);
}

````
