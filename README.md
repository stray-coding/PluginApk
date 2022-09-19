最近在研究apk的动态加载，无论是在简书还是CSDN上阅读了很多博客，但是发现很多博主虽然讲的很详细，但是很多文章都是14，15年的，而且有的文章并没有提供demo或者提供的demo根本跑不起来，搞得我一脸懵逼，学习遇到了很多阻力。但是呢，天道酬勤，最终在刻苦钻研几天后，对动态加载算是有了一定的眉目，且听我下文缓缓道来。

首先动态加载apk，一定会有一个宿主apk和一个插件apk，所谓的动态加载，无非是在宿主的apk中，加载插件apk里的activity，类似于支付宝中打开飞猪、淘票票等页面。而要做到上面这两点，就涉及到了class的动态加载以及资源的动态加载。
### 1. 类的动态加载（有兴趣的可以去搜类的加载机制相关博客）
这里我们需要使用到DexClassLoader(String dexPath,String optimizedDirectory,String librarySearchPath,ClassLoader parent)
	* dexPath  填写apk的位置即可（应用内目录）
	* optimizedDirectory 这是存放dex加载后会生存缓存的路径
	* librarySearchPath c、c++库，大部分情况null即可
	* parent 该装载器的父装载器，一般为当前执行类的装载器。
```
pluginDexClassLoader = DexClassLoader(
    apkPath,
    appCtx.getDir("dexOpt", Context.MODE_PRIVATE).absolutePath,
    null,
    appCtx.classLoader
)
```
### 2. res的动态加载
由于我们的pluginApk在动态加载时，并不会走正常的application初始化的那一套流程，并且资源文件也不会加载到宿主apk的resouces里面，所以这里我们需要自己去实现一个resource，在创建resource之前，我们先实例化一个AssetManager对象，然后通过反射调用addAssetPath方法，将我们插件apk的地址设置进去，最后通过Resources(AssetManager assets, DisplayMetrics metrics, Configuration config)方法，新建一个resource（该resource只含有插件apk中的res资源）
```
pluginAssets = AssetManager::class.java.newInstance()
val addAssetPath: Method = AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
addAssetPath.invoke(pluginAssets, apkPath)
val superResources: Resources? = ctx.resources
pluginRes = Resources(
    pluginAssets,
    superResources?.displayMetrics,
    superResources?.configuration
)
```
### 3. ProxyActivity
这时候我们已经掌握了动态加载apk中的class和res资源的方法。但是由于插件apk中的activity并没有声明到我们的宿主apk的Androidmanifest中，所以并不能通过传统的startActivity(intent)方式来跳转到插件activity中。所以这时候我们需要一个ProxyActivity来作为介质，达到控制插件apk中的activity。首先我们需要在ProxyActivity在onCreate(bundle)方法中，反射生成我们想要跳转的插件activity实例，并在该方法中执行pluginActivity的onCreate（）激活该activity，并在ProxyActivity的其他生命周期中，转调PluginActivity的相应生命周期方法，从而使PluginActivity能正确并完整的进入各个生命周期流程。
* ProxyActivity的resource、classLoader、assets应该使用我们自己实例化的对象
* startActivity方法应该覆写，改为由ProxyActivity来跳转相应Activity
* ProxyActivity的生命周期中应该调用PluginActivity的生命周期方法 
```
class ProxyActivity : Activity() {
    companion object {
        private const val TAG = "ProxyActivity"
    }

    private lateinit var pluginInterface: PluginInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        try {
            val clsName = intent.getStringExtra("className")
            Log.d(TAG, "activity name:$clsName")
            val cls = PluginManager.pluginDexClassLoader.loadClass(clsName)
            val newInstance = cls.newInstance()
            if (newInstance is PluginInterface) {
                pluginInterface = newInstance
                pluginInterface.attach(this)
                val bundle = Bundle()
                bundle.putBoolean(PluginManager.TAG_IS_PLUGIN, true)
                pluginInterface.onCreate(bundle)
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    override fun startActivity(intent: Intent) {
        val newIntent = Intent(this, ProxyActivity::class.java)
        newIntent.putExtra("className", intent.component!!.className)
        super.startActivity(newIntent)
    }

    override fun getResources(): Resources? {
        return if (!PluginManager.is_plugin)
            super.getResources()
        else
            PluginManager.pluginRes
    }

    override fun getAssets(): AssetManager? {
        return if (!PluginManager.is_plugin)
            super.getAssets()
        else
            PluginManager.pluginAssets
    }

    override fun getClassLoader(): ClassLoader? {
        return if (!PluginManager.is_plugin)
            super.getClassLoader()
        else
            PluginManager.pluginDexClassLoader
    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        pluginInterface.onStart()
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        pluginInterface.onResume()
        super.onResume()
    }

    override fun onRestart() {
        Log.d(TAG, "onRestart")
        pluginInterface.onRestart()
        super.onRestart()
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        pluginInterface.onPause()
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        pluginInterface.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        pluginInterface.onDestroy()
        super.onDestroy()
    }

    override fun finish() {
        Log.d(TAG, "finish")
        pluginInterface.finish()
        super.finish()
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed")
        pluginInterface.onBackPressed()
        super.onBackPressed()
    }

    override fun onNewIntent(intent: Intent?) {
        Log.d(TAG, "onNewIntent")
        pluginInterface.onNewIntent(intent)
        super.onNewIntent(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult")
        pluginInterface.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.d(TAG, "onRequestPermissionsResult")
        pluginInterface.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d(TAG, "onConfigurationChanged")
        pluginInterface.onConfigurationChanged(newConfig)
        super.onConfigurationChanged(newConfig)
    }
}
```
### 4. PluginActivity（插件中Activity的基类）
该类为插件Activity的基类（插件中的所有Activity均应继承该类）
* 作为插件时：我们的资源和class类均应该使用自定义生成的classLoader和resource去加载，并且获取app信息的方法均应该转调ProxyActivity的实例去获取。
* 不作为插件时（作为独立app）：为确保独立运行该app，能正常使用，我们应该在相应方法中直接使用super调用父方法即可。
```
abstract class PluginActivity : Activity(), PluginInterface {
    private var TAG = this::class.java.simpleName

    /**
     * true  作为插件使用，方法应该转调mActivity
     * false 独立作为APP，用自身资源操作
     * */
    private var isPlugin = false

    /**
     * 代理activity的实例
     * */
    lateinit var mActivity: Activity

    override fun attach(activity: Activity) {
        mActivity = activity
    }

    override fun onCreate(bundle: Bundle?) {
        if (bundle != null) {
            isPlugin = bundle.getBoolean(PluginManager.TAG_IS_PLUGIN, false)
        }
        if (!isPlugin) {
            super.onCreate(bundle)
            mActivity = this
        }
    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        if (!isPlugin) {
            super.onStart()
        }
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        if (!isPlugin) {
            super.onResume()
        }
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        if (!isPlugin) {
            super.onPause()
        }
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        if (!isPlugin) {
            super.onStop()
        }
    }

    override fun onRestart() {
        Log.d(TAG, "onRestart")
        if (!isPlugin) {
            super.onRestart()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        if (!isPlugin) {
            super.onNewIntent(intent)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        if (!isPlugin) {
            super.onDestroy()
        }
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed")
        if (!isPlugin) {
            super.onBackPressed()
        }
    }

    override fun finish() {
        Log.d(TAG, "finish")
        if (!isPlugin) {
            super.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!isPlugin) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.d(TAG, "onRequestPermissionsResult")
        if (!isPlugin) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (!isPlugin) {
            super.onConfigurationChanged(newConfig)
        }
    }

    override fun startActivity(intent: Intent?) {
        if (!isPlugin) {
            super.startActivity(intent)
        } else {
            mActivity.startActivity(intent)
        }
    }

    override fun getLayoutInflater(): LayoutInflater {
        return if (!isPlugin)
            super.getLayoutInflater()
        else
            mActivity.layoutInflater

    }

    override fun getWindowManager(): WindowManager? {
        return if (!isPlugin)
            super.getWindowManager()
        else
            mActivity.windowManager
    }

    override fun getApplicationInfo(): ApplicationInfo? {
        return if (!isPlugin)
            super.getApplicationInfo()
        else
            mActivity.applicationInfo
    }


    override fun getResources(): Resources {
        return if (!isPlugin)
            super.getResources()
        else
            mActivity.resources
    }

    override fun getAssets(): AssetManager {
        return if (!isPlugin)
            super.getAssets()
        else
            mActivity.assets
    }

    override fun getClassLoader(): ClassLoader {
        return if (!isPlugin)
            super.getClassLoader()
        else
            mActivity.classLoader
    }

    override fun setContentView(layoutResID: Int) {
        if (!isPlugin) {
            super.setContentView(layoutResID)
        } else {
            mActivity.setContentView(layoutResID)
        }
    }

    override fun getWindow(): Window {
        return if (!isPlugin) {
            super.getWindow()
        } else {
            mActivity.window
        }
    }

    override fun setContentView(view: View?) {
        if (!isPlugin) {
            super.setContentView(view)
        } else {
            mActivity.setContentView(view)
        }
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        if (!isPlugin) {
            super.setContentView(view, params)
        } else {
            mActivity.setContentView(view, params)
        }
    }

    override fun getPackageName(): String {
        return if(!isPlugin){
            super.getPackageName()
        }else{
            mActivity.packageName
        }
    }

}
```
## demo演示
1.新建两个app module：

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020102419280672.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM2Mzc4ODM2,size_16,color_FFFFFF,t_70#pic_center)

* app：宿主apk     
* other：插件apk
我们在app module中的MainActivity中启动ProxyActivity，并且将other module的Main1Activity的全类名传入，这样我们在宿主apk中就能顺利的启动插件中的activity了。运行效果如下图:

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201024193735731.gif#pic_center)

好了，以上就是本次关于动态加载apk的全部分享内容了，想要详细了解demo工程的小伙伴，我已经将传送门放到后面了[点我，查看demo工程](https://github.com/stray-coding/PluginApk)最后，如果你觉得这篇文章对你有帮助，解决了你的一部分疑惑，请不要吝啬你热情，在github的demo里面点击一下送上你的star哦！

文章参考：
* [https://www.jianshu.com/p/a4ab102fa4ac](https://www.jianshu.com/p/a4ab102fa4ac)
