##Handler
looper -> 初始化 messagequeue
handler -》 初始化 Looper.myLooper()获取looper， 获取messgequeue
message 
messagequeue 

##AIDL
```
my.aidl
package cyview;
interface my {
    boolean start(in int abc);
}

	public interface my extends android.os.IInterface {
		public static abstract class Stub extends android.os.Binder implements cyview.my {
		
			public static cyview.my asInterface(android.os.IBinder obj) {
				return new cyview.my.Stub.Proxy(obj);
			}
			
			public android.os.IBinder asBinder() {
	      		return this;
	       }
	       
	       @Override
	        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
	        }
	        
	        
	        private static class Proxy implements cyview.my {
		         Proxy(android.os.IBinder remote) {
	                mRemote = remote;
	            }
	
	            @Override
	            public android.os.IBinder asBinder() {
	                return mRemote;
	            }
	
	            public java.lang.String getInterfaceDescriptor() {
	                return DESCRIPTOR;
	            }
	
	            @Override
	            public boolean start(int abc) throws android.os.RemoteException {
	            	
	            }
	        }    
		} //end stub
		
		public boolean start(int abc) throws android.os.RemoteException;
	}


```



```
//生成的java文件
public interface my extends android.os.IInterface {
	public static abstract class Stub extends android.os.Binder implements cyview.my {
		public static cyview.my asInterface(android.os.IBinder obj) {			return new cyview.my.Stub.Proxy(obj);
		}
		
		public android.os.IBinder asBinder() {
      		return this;
       }
       
       @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_start: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0;
                    _arg0 = data.readInt();
                    boolean _result = this.start(_arg0);
                    reply.writeNoException();
                    reply.writeInt(((_result) ? (1) : (0)));
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }
        
        private static class Proxy implements cyview.my {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public java.lang.String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public boolean start(int abc) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                boolean _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(abc);
                    mRemote.transact(Stub.TRANSACTION_start, _data, _reply, 0);
                    _reply.readException();
                    _result = (0 != _reply.readInt());
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }
        }

        static final int TRANSACTION_start = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
	
	} //end stub
	
	public boolean start(int abc) throws android.os.RemoteException;
}
```

###启动程序
                           
system    1592  1275  1407656 87516 SyS_epoll_ b73abff5 S system_server
root      1275  1     1253424 53416 poll_sched b73aa4a0 S zygote

app_main 初始的源文件名称, 生成的二进制文件在/system/bin/app_process
运行后启动zygote进程， 然后孵化出system_server进程

dalvikvm
dvz
app_process

init.rc文件
service zygote /system/bin/app_process -Xzygote /system/bin --zygote --start-system-server
    class main
    socket zygote stream 660 root system
    onrestart write /sys/android_power/request_state wake
    onrestart write /sys/power/state on
    onrestart restart media
    onrestart restart netd


```
app_main.cpp
1.启动appruntime
AppRuntime runtime;
if (zygote) {
    runtime.start("com.android.internal.os.ZygoteInit",
            startSystemServer ? "start-system-server" : "");
} else if (className) {
    // Remainder of args get passed to startup class main()
    runtime.mClassName = className;
    runtime.mArgC = argc - i;
    runtime.mArgV = argv + i;
    runtime.start("com.android.internal.os.RuntimeInit",
            application ? "application" : "tool");
} else {}

2. class AppRuntime : public AndroidRuntime
	void AndroidRuntime::start(const char* className, const char* options) {
      【A】if (startVm(&mJavaVM, &env) != 0) { // 初始化各种虚拟机参数（dalvik.vm.checkjni  -verbose:gc 等等）, 然后调用jni.cpp文件下的JNI_CreateJavaVM方法，生成mJavaVM，env对象
		    return;
		}
		【B】onVmCreated(env);
		【C】调用className类的main方法，同时传入options参数
	}
	
3. 调用com.android.internal.os.ZygoteInit 类的main方法
	    public static void main(String argv[]) {
	    	  【A】registerZygoteSocket(); //注册socket
	    	  【B】preloadClasses();   //  Class.forName() 实例化 frameworks/base/preloaded-classes 文件下的所有类
        	  【C】preloadResources();
        	  【D】if (argv[1].equals("start-system-server")) {
                  startSystemServer();
                 }
            【E】 
		            if (ZYGOTE_FORK_MODE) {
		                runForkMode();
		            } else {
		                runSelectLoopMode();
		            }
		
		            closeServerSocket();
		            
		        	} catch (MethodAndArgsCaller caller) {
		      【F】      caller.run();
		        	}
	    }
	
3.1. startSystemServer()

	String args[] = {
            "--setuid=1000",
            "--setgid=1000",
            "--setgroups=1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1018,1032,3001,3002,3003,3006,3007",
            "--capabilities=" + capabilities + "," + capabilities,
            "--runtime-init",
            "--nice-name=system_server",
            "com.android.server.SystemServer",
        };

	【A】	try {
            parsedArgs = new ZygoteConnection.Arguments(args);
            ZygoteConnection.applyDebuggerSystemProperty(parsedArgs);
            ZygoteConnection.applyInvokeWithSystemProperty(parsedArgs);

            /* Request to fork the system server process */
            pid = Zygote.forkSystemServer(
                    parsedArgs.uid, parsedArgs.gid,
                    parsedArgs.gids,
                    parsedArgs.debugFlags,
                    null,
                    parsedArgs.permittedCapabilities,
                    parsedArgs.effectiveCapabilities);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        }

        /* For child process */
        if (pid == 0) {
            if (hasSecondZygote(abiList)) {
                waitForSecondaryZygote(socketName);
            }

            handleSystemServerProcess(parsedArgs);   // 子进程启动systemserver服务
        }
	【B】private static void handleSystemServerProcess(ZygoteConnection.Arguments parsedArgs)
		 a) closeServerSocket(); //关闭localserversocket
		 b) ClassLoader cl = null;		//初始化classloader
            if (systemServerClasspath != null) {
                cl = new PathClassLoader(systemServerClasspath, ClassLoader.getSystemClassLoader());
                Thread.currentThread().setContextClassLoader(cl);
            }

        c)  RuntimeInit.zygoteInit(parsedArgs.targetSdkVersion, parsedArgs.remainingArgs, cl);

```








###窗口类
WindowImpl WindowManager  WindowManagerGLoable WindowManagerService PhoneWindowManager







 
