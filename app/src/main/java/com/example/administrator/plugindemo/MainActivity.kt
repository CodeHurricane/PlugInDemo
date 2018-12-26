package com.example.administrator.plugindemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import dalvik.system.DexClassLoader
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {
    lateinit var dynamic:Dynamic
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tx.setOnClickListener { loadDexClass()}
    }

    private fun loadDexClass() {
        val cacheFile = FileUtils.getCacheDir(applicationContext)
        val internalPath = cacheFile.getAbsolutePath() + File.separator + "hello.jar"
        val desFile = File(internalPath)
        try {
            if (!desFile.exists()) {
                desFile.createNewFile()
                // 从assets目录下 copy 文件到 app/data/cache目录
                FileUtils.copyFiles(this, "hello.jar", desFile)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // 这里由于是加载 jar文件，所以采用DexClassLoader
        //下面开始加载dex class
        val dexClassLoader = DexClassLoader(internalPath, cacheFile.getAbsolutePath(), null, classLoader)
        try {
            // 类加载器负责读取 .class文件，并把它转为 Class实例，这个实例就表示一个java类
            // 加载dex文件中的Class，格式是：包名+类名（全类名）
            val libClazz = dexClassLoader.loadClass("com.example.administrator.plugindemo.IDynamic")
            // 调用Class的 newInstance()方法，创建Class的对象 dynamic
            // Dynamic 是 dex文件中之前的一个接口类
            dynamic = libClazz.newInstance() as Dynamic
            if (dynamic != null)
                Toast.makeText(this, dynamic.sayHelloy(), Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
