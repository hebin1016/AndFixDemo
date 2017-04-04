##AndFix热修复原理及使用说明##

AndFix是一种代替重新发版修复线上bug的解决方案。Andfix是"Android hot-fix"的缩写，它几乎支持所有android版本。AndFix的补丁的压缩文件格式是".apatch"。它可以从服务器发送到客户端修复你的应用程序的bug。

[官方地址](https://github.com/alibaba/AndFix)

###原理###

AndFix的实现原理是方法的替换。android程序和方法是运行在Dalvik虚拟机中的，在android系统中，Dalvik虚拟机实现在libdvm.so中。换言之，android方法执行最终都是被Dalvik虚拟机执行的，方法在执行时会被虚拟机包装成一个c语言结构体，结构体它肯定有个指针。那么如果我们虚拟机中运行的某个方法有bug，我们就可以再另外加载一个修复好的方法，用这个修复好的方法指针代替有bug的方法指针。那么在虚拟机在运行这个方法时就不会有问题了。

![Andfix_Principle](https://github.com/hebin1016/AndFixDemo/blob/master/Andfix_Principle.png)


###修复过程###

* 当我们发现线上包bug并修复后打包（我们假设有bug的包叫src.apk,修复好的包叫dest.apk），使用andfix提供的工具，生成修复插件，命令如下：

		./apkpatch.sh -f /Users/hebin/Downloads/dest.apk -t /Users/hebin/Downloads/res.apk -o ~/andfixdemo -k /Users/hebin/Downloads/hanks.keystore -p 123456 -a 123456 -e 123456
        
	* 参数说明
		1. -f dest.apk路径
		2. -t src.apk路径
		3. -o  生成apatch插件的文件夹路径
		4. -k apk的签名
		5. -p 签名密码	
		6. -a 签名秘钥别名
		7. -e 签名秘钥密码
	
	* 修复文件是以.apatch后缀的文件，修改后缀为rar解压查看
		
			├── META-INF
			│   ├── CERT.RSA
			│   ├── CERT.SF
			│   ├── MANIFEST.MF
			│   └── PATCH.MF
			└── classes.dex
	
* 把.apatch文件加载到线上app中
* 解析META-INF/PATCH.MF文件的Classes节点，读取要替换的类名

    	Patch-Classes: com.example.andfixdemo.MainActivity_CF
    	
* 通过上面读取的类名从classes.dex中获取相应相应的class类文件。并读取该class类文件的注解,获取实际要替换的类名和方法

	    @MethodReplace(
	        clazz = "com.example.andfixdemo.MainActivity",
	        method = "getEditText"
	    )
	    
* 通过反射获取到要替换的方法和有bug的方法，把这两个方法通过调用native函数传到native层

		AndFix.addReplaceMethod(src, method);
	
* 在native层改变方法的指针指向，从而在调用时达成方法的替换

		Method* meth = (Method*) env->FromReflectedMethod(src);
		Method* target = (Method*) env->FromReflectedMethod(dest);
	    meth->clazz = target->clazz;
		meth->accessFlags |= ACC_PUBLIC;
		meth->methodIndex = target->methodIndex;
		meth->jniArgInfo = target->jniArgInfo;
		meth->registersSize = target->registersSize;
		meth->outsSize = target->outsSize;
		meth->insSize = target->insSize;
	
		meth->prototype = target->prototype;
		meth->insns = target->insns;
		meth->nativeFunc = target->nativeFunc;
		

		
###GitHub示例###

[https://github.com/hebin1016/AndFixDemo](https://github.com/hebin1016/AndFixDemo)

![screenshot](https://github.com/hebin1016/AndFixDemo/blob/master/screenshoot.gif)   









