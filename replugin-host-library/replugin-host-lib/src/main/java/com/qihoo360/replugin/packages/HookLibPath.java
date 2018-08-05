package com.qihoo360.replugin.packages;

import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TimingLogger;
import com.qihoo360.replugin.utils.ReflectUtils;
import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexFile;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 创建时间：2018/07/20/16:28
 * 作者：xiejiangquan <br>
 * 描述：hook libpath的类，将插件要加载的so委托给主工程加载,减少插件体积
 */
public class HookLibPath {

    public static final String TAG = "HackLibPath";

    @NonNull
    public static ClassLoader hookPluginDexClassLoader(ClassLoader pluginDexClassLoader) {
        try {
            Object dexPathListHost = ReflectUtils.readField(pluginDexClassLoader.getClass().getClassLoader(), "pathList");
            Object nativeLibraryDirectoriesObj = ReflectUtils.readField(dexPathListHost, "nativeLibraryDirectories");
            String hostLibPath = null;
            if (nativeLibraryDirectoriesObj instanceof File[]) {
                File[] nativeLibraryDirectoriesHost = (File[]) nativeLibraryDirectoriesObj;
                hostLibPath = nativeLibraryDirectoriesHost[0].getAbsolutePath();
            } else if (nativeLibraryDirectoriesObj instanceof ArrayList) {
                ArrayList<File> nativeLibraryDirectoriesHost = (ArrayList<File>) nativeLibraryDirectoriesObj;
                hostLibPath = nativeLibraryDirectoriesHost.get(0).getAbsolutePath();
            } else {
                Log.e(TAG,"hookPluginDexClassLoader error : nativeLibraryDirectoriesObj = " + nativeLibraryDirectoriesObj);
            }
            HackLibPath(pluginDexClassLoader, hostLibPath);
        } catch (Throwable e) {
            Log.e(TAG, "hookPluginDexClassLoader error :",  e);
            e.printStackTrace();
        }

        //反射添加主工程libs
        return pluginDexClassLoader;
    }

    public static void HackLibPath(ClassLoader classLoader, String pname) {
        TimingLogger timing = new TimingLogger("HackLibProfiler", "HackLibProfiler");

        hackNativeLibraryDirectories(classLoader, pname);
        timing.addSplit("HackNativeLibraryDirectories Profiler");
        hackNativeLibraryPathElements(classLoader, pname);
        timing.addSplit("HackNativeLibraryPathElements Profiler");
        hackRuntimeLibPaths(classLoader, pname);
        timing.addSplit("HackRuntimeLibPaths Profiler");

        timing.dumpToLog();
    }

    //Hook DexPathList.nativeLibraryDirectories
    public static void hackNativeLibraryDirectories(ClassLoader classLoader, String pname) {
        try {
            //获取pathList的字段
            Field fieldSysPath = null;  //获取的pathList字段
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                fieldSysPath = BaseDexClassLoader.class.getDeclaredField("pathList");
            }
            fieldSysPath.setAccessible(true);//设置字段可访问
            //获取pathList的值
            Object paths = (Object) fieldSysPath.get(classLoader);
            Class<? extends Object> c = paths.getClass();
            Field Libpaths =
                c.getDeclaredField("nativeLibraryDirectories");//获取nativeLibraryDirectories字段
            Libpaths.setAccessible(true);//设置字段可访问

            Object tmpNoTypeNativePaths = Libpaths.get(paths);
            File[] nativepaths = null;
            boolean isNativePathIsArray = tmpNoTypeNativePaths.getClass().isArray();
            if (isNativePathIsArray) {
                nativepaths = (File[]) Libpaths.get(paths);
            } else {
                ArrayList<File> tmpNativePaths = (ArrayList<File>) Libpaths.get(paths); //获取本地加载列表
                nativepaths = tmpNativePaths.toArray(new File[tmpNativePaths.size()]);
            }

            File[] tmp = new File[nativepaths.length + 1];
            System.arraycopy(nativepaths, 0, tmp, 1, nativepaths.length);
            tmp[0] = new File(pname);

            if (isNativePathIsArray) {
                Libpaths.set(paths, tmp);//添加自定义路径
            } else {
                Libpaths.set(paths, Arrays.asList(tmp));//添加自定义路径
            }
        } catch (Exception e) {
            Log.i(TAG,"HackNativeLibraryDirectories Exception " + pname);
            e.printStackTrace();
        } catch (Error e)//NoClassDefFoundError
        {
            Log.i(TAG,"HackNativeLibraryDirectories Exception " + pname);
            e.printStackTrace();
        }
    }

    //Hook DexPathList.nativeLibraryPathElements
    public static void hackNativeLibraryPathElements(ClassLoader classLoader, String pname) {
        boolean isAndroidMOrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

        if (!isAndroidMOrHigher) {
            return;
        }

        try {
            //获取pathList的字段
            Field fieldSysPath = null;  //获取的pathList字段
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                fieldSysPath = BaseDexClassLoader.class.getDeclaredField("pathList");
            }
            fieldSysPath.setAccessible(true);//设置字段可访问
            //获取pathList的值
            Object paths = (Object) fieldSysPath.get(classLoader);
            Class<? extends Object> c = paths.getClass();

            Field LibpathElms = c.getDeclaredField("nativeLibraryPathElements");

            if (LibpathElms == null) {
                return;
            }

            File dir = new File(pname);
            LibpathElms.setAccessible(true);//设置字段可访问
            Object[] elmArray = (Object[]) LibpathElms.get(paths);

            if (elmArray == null) {
                return;
            }

            Class<?> compType = LibpathElms.getType().getComponentType();

            Object elmInst = getHackNativeLibraryPathElements(dir, compType);

            if(null == elmInst) {
                return;
            }

            Object newElmArray = Array.newInstance(compType, elmArray.length + 1);

            int index = 0;
            Array.set(newElmArray, index++, compType.cast(elmInst));
            for (Object oldElm : elmArray) {
                Array.set(newElmArray, index++, compType.cast(oldElm));
            }
            LibpathElms.set(paths, newElmArray);

        } catch (Exception castError) {
            Log.e(TAG,"HackNativeLibraryPathElements Exception ", castError);
            castError.printStackTrace();
        }
    }

    private static Object getHackNativeLibraryPathElements(File dir, Class<?> nativeLibraryPathElementCls) {
        Object DexPathListElement = null;

        final String pathElementClsName = nativeLibraryPathElementCls.getName();
        if (pathElementClsName.equals("dalvik.system.DexPathList$Element")) {
            return getNativeLibraryPathElements6x(dir);
        } else if (pathElementClsName.equals("dalvik.system.DexPathList$NativeLibraryElement")) {
            return getNativeLibraryPathElements8x(dir);
        } else {
            Log.e(TAG,"getHackNativeLibraryPathElements error pathElementClsName =  " + pathElementClsName);
        }

        return DexPathListElement;
    }

    private static Object getNativeLibraryPathElements6x(File dir) {
        Object DexPathListElement = null;
        try {
            Class<?> cls = Class.forName("dalvik.system.DexPathList$Element");
            Constructor
                elmCon = cls.getDeclaredConstructor(new Class[]{ File.class, boolean.class, File.class, DexFile.class });
            elmCon.setAccessible(true);
            DexPathListElement = elmCon.newInstance(new Object[]{ dir, true, null, null });
        } catch (Exception e) {
            Log.e(TAG,"HackNativeLibraryPathElements6x Exception ", e);
            e.printStackTrace();
        }
        return DexPathListElement;
    }

    private static Object getNativeLibraryPathElements8x(File dir) {
        Object nativeLibraryElement = null;
        try {
            Class<?> cls = Class.forName("dalvik.system.DexPathList$NativeLibraryElement");
            Constructor elmCon = cls.getDeclaredConstructor(new Class[]{ File.class});
            elmCon.setAccessible(true);
            nativeLibraryElement = elmCon.newInstance(new Object[]{ dir});
        } catch (Exception e) {
            Log.e(TAG,"HackNativeLibraryPathElements8x Exception ", e);
            e.printStackTrace();
        }
        return nativeLibraryElement;
    }

    //Hook RunTime.LibPaths
    public static void hackRuntimeLibPaths(ClassLoader classLoader, String pname) {

        try {
            //获取mLibPaths的字段
            Field libPathsField = Runtime.class.getDeclaredField("mLibPaths");

            if (libPathsField == null) {
                return;
            }

            libPathsField.setAccessible(true);

            //获取mLibPaths的字段的值
            Object[] libPathsValue = (Object[]) libPathsField.get(Runtime.getRuntime());
            if (libPathsValue == null) {
                return;
            }

            Class<?> compType = libPathsField.getType().getComponentType();
            Object newElmArray = Array.newInstance(compType, libPathsValue.length + 1);

            int index = 0;
            Array.set(newElmArray, index++, compType.cast(pname));
            for (Object oldElm : libPathsValue) {
                Array.set(newElmArray, index++, compType.cast(oldElm));
            }
            libPathsField.set(Runtime.getRuntime(), newElmArray);

        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e)//NoClassDefFoundError
        {

            e.printStackTrace();
        }
    }
}
