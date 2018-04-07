package com.speaktool;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }


/**
 *
 android studio 华为手机看不到具体的错误日志

 1. 手机的开发人员选项打开了么，其中的 USB 调试打开了么？
 搞定他们并重新运行，是否能找到我们的日志？否，转到 2。
 2. 日志中是否有这样 could not disable core file generation for pid 3963: Operation not permitted 的信息么？
 是，转到 3；否，那我也不知道怎么搞，或者你可以把日志贴到评论中我看看。
 3. 是华为手机么？是，转到 4；否，你可以参考 4 自己 Google Baidu 一下。
 4. 在拨号界面输入：*#*#2846579#*#* 进入测试菜单界面，然后
 Project Menu → 后台设置 → LOG设置
 LOG 开关 → LOG 打开        LOG 级别设置 → VERBOSE
 Dump&Log → 全部选中
 重启手机，重启 Android Studio。
 PS：根据系统版本的不同，可能会有些许差异.

 重启完毕，打开 Android Studio，运行项目，这下应该能找到我们的日志了。
 *
 */

}