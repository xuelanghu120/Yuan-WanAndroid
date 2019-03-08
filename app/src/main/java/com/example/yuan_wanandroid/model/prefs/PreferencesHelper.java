package com.example.yuan_wanandroid.model.prefs;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/01/30
 *     desc   : SharedPreferences操作接口
 * </pre>
 */


public interface PreferencesHelper {
    void setNightStyleState(boolean isNight); //保存夜间模式设置
    boolean getNightStyleState(); //得到夜间模式的设置
    void setNoImgState(boolean isNoImg);//保存无图模式设置
    boolean getNoImgStyleState();//得到无图模式的设置
}