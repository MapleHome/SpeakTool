package com.speaktool.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.speaktool.Const;
import com.speaktool.bean.Html5ImageInfoBean;
import com.speaktool.bean.ScreenInfoBean;
import com.speaktool.impl.bean.TransformShapeData;
import com.speaktool.impl.cmd.ICmd;
import com.speaktool.impl.player.JsonScriptParser;
import com.speaktool.utils.record.RecordFileAnalytic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 记录文件工具类
 *
 * @author shaoshuai
 */
public class RecordFileUtils {
    private static final String tag = RecordFileUtils.class.getSimpleName();

    /**
     * 从本地目录中获取spk上传单元
     *
     * @param dirpath
     * @return
     */
    public static File getSpklUploadBeanFromDir(String dirpath) {
        File dir = new File(dirpath);// 目录文件
        File zip = new File(dir, "record.zip");// 压缩文件
        if (zip.exists()) {
            zip.delete();
        }
        File[] uploadFiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (TextUtils.isEmpty(filename))
                    return false;
                return (filename.equals(Const.RELEASE_JSON_SCRIPT_NAME)
                        || filename.equals(Const.RELEASE_SOUND_NAME)
                        || filename.equals(Const.INFO_FILE_NAME)
                        || filename.endsWith(Const.IMAGE_SUFFIX)
                        || filename.endsWith(Const.GIF_SUFFIX));
            }
        });
        try {
            /** 必须在 listfiles,否则压缩将zipped. */
            zip.createNewFile();
            ZipUtils.zipFiles(Arrays.asList(uploadFiles), zip);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zip;
    }


    /**
     * 将图片拷贝到该记录路径下- 如果BitMap过大，将缩放
     *
     * @param bitmapPath
     * @param dir
     * @return
     */
    public static String copyBitmapToRecordDir(String bitmapPath, String dir) {
        try {
            Bitmap bmpScaled = BitmapScaleUtil.decodeSampledBitmapFromPath(bitmapPath,
                    Const.MAX_MEMORY_BMP_CAN_ALLOCATE);
            if (bmpScaled == null)
                return null;
            return copyBitmapToRecordDir(bmpScaled, dir);

        } catch (Error err) {
            err.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将图片拷贝到该记录路径下
     *
     * @param srcbmpScaled
     * @param dir
     * @return
     */
    public static String copyBitmapToRecordDir(Bitmap srcbmpScaled, String dir) {
        if (srcbmpScaled == null)
            return null;
        try {
            String imgname = Long.toHexString(System.currentTimeMillis()) + ".jpg";
            File img = new File(dir, imgname);
            if (!img.exists())
                img.createNewFile();
            FileOutputStream imgstream = new FileOutputStream(img);
            boolean success = srcbmpScaled.compress(CompressFormat.JPEG, 100, imgstream);
            imgstream.close();
            // srcbmp.recycle();
            if (!success)
                return null;
            return imgname;// img.getAbsolutePath();
        } catch (Error err) {
            err.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取所有CMD文件，并按时间排序
     */
    public static List<File> getAllCmdFilesSortByTime(File dir) {
        // 获取所有cmd文件
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.getName().equals(Const.RELEASE_JSON_SCRIPT_NAME))
                    return false;
                if (pathname.getAbsolutePath().endsWith(Const.CMD_FILE_SUFFIX))
                    return true;
                else
                    return false;
            }
        });
        if (files == null)
            return null;
        List<File> filelist = Arrays.asList(files);
        // 按时间排序
        Collections.sort(filelist, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                int _index = lhs.getName().lastIndexOf("_");
                int dotIndex = lhs.getName().lastIndexOf(".");
                if (_index == -1 || dotIndex == -1)
                    return 0;
                int _index2 = rhs.getName().lastIndexOf("_");
                int dotIndex2 = rhs.getName().lastIndexOf(".");
                if (_index2 == -1 || dotIndex2 == -1)
                    return 0;
                long time1 = Long.valueOf(lhs.getName().substring(_index + 1, dotIndex));
                long time2 = Long.valueOf(rhs.getName().substring(_index2 + 1, dotIndex2));
                return time1 > time2 ? 1 : time1 < time2 ? -1 : 0;
            }
        });
        return filelist;
    }


    /**
     * @param dir
     * @param context
     * @param info
     * @throws Exception
     */
    public static void makeReleaseScript(File dir, Context context, ScreenInfoBean info) throws Exception {
        List<File> jsonFiles = getAllCmdFilesSortByTime(dir);
        if (jsonFiles == null || jsonFiles.isEmpty())
            return;
        // release.txt
        File releaseFile = new File(dir, Const.RELEASE_JSON_SCRIPT_NAME);
        if (releaseFile.exists())
            releaseFile.delete();
        releaseFile.createNewFile();

        FileOutputStream ous = new FileOutputStream(releaseFile, true);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ous), 10240);

        writer.write("{\"wbEvents\":[");
        boolean isFirstCmd = true;
        long delta = 0;
        List<ICmd> filteredCmds = new ArrayList<>();
        Map<Integer, Boolean> transformCmd = new HashMap<>();

        for (int i = 0; i < jsonFiles.size(); i++) {
            File jsonf = jsonFiles.get(i);
            JsonScriptParser parser = new JsonScriptParser(context);
            List<ICmd> pageCmds = parser.jsonFileToCmds(jsonf.getAbsolutePath());
            if (pageCmds == null || pageCmds.isEmpty())
                continue;
            if (!isRecord(jsonf)) {// not record.
                for (int j = pageCmds.size() - 1; j >= 0; j--) {
                    ICmd cmd = pageCmds.get(j);
                    // filter repeat transform cmds.
                    if (cmd.getType().equals(ICmd.TYPE_TRANSFORM_SHAPE)) {
                        TransformShapeData data = (TransformShapeData) cmd.getData();
                        if (!transformCmd.containsKey(data.getShapeID())) {
                            transformCmd.put(data.getShapeID(), true);

                            cmd.setTime(ICmd.TIME_DELETE_FLAG);

                            ICmd copy = cmd.copy();
                            filteredCmds.add(0, copy != null ? copy : cmd);
                        } else
                            Log.e(tag, "repeat cmd");
                    } else {// not transform.
                        cmd.setTime(ICmd.TIME_DELETE_FLAG);
                        ICmd copy = cmd.copy();
                        filteredCmds.add(0, copy != null ? copy : cmd);
                    }
                }// for cmds end.
                delta += getSoundFileDuration(jsonf.getAbsolutePath().replaceAll(Const.CMD_FILE_SUFFIX,
                        Const.SOUND_FILE_SUFFIX));
                //
                String line = new Gson().toJson(filteredCmds);
                line = line.substring(1, line.length() - 1);
                if (!isFirstCmd)
                    writer.write(",");
                else
                    isFirstCmd = false;
                writer.write(line);
                filteredCmds.clear();
                transformCmd.clear();

            } else {// IS RECORD.
                for (ICmd cmd : pageCmds) {
                    cmd.setTime(cmd.getTime() - delta);
                }
                String line = new Gson().toJson(pageCmds);
                line = line.substring(1, line.length() - 1);

                if (!isFirstCmd)
                    writer.write(",");
                else
                    isFirstCmd = false;
                writer.write(line);
            }
        }// for files end.

//        Html5SoundInfoBean sound = getScriptSound(dir, -1);
//        Html5SoundInfoBean sound = new Html5SoundInfoBean(Const.REALEASE_SOUND_FILE_TYPE, Const.RELEASE_SOUND_NAME);
        List<Html5ImageInfoBean> jpgNames = getScriptJpgNames(dir);
        if (jpgNames == null) {
            jpgNames = new LinkedList<>();
        }

        writer.write("]");
        writer.write(",");
        writer.write("\"sound\":" + Const.RELEASE_SOUND_NAME);
        writer.write(",");
        writer.write("\"resources\":" + new Gson().toJson(jpgNames));
        writer.write(",");
        writer.write("\"inputScreenWidth\":" + info.getWidth());
        writer.write(",");
        writer.write("\"inputScreenHeight\":" + info.getHeight());
        writer.write(",");
        writer.write("\"density\":" + info.getDensity());
        writer.write(",");
        writer.write("\"version\":" + Const.SCRIPT_VERSION);
        writer.write(",");
        writer.write("\"inputRate\":" + Const.SCRIPT_INPUT_RATE);
        writer.write("}");
        writer.write("\n");

        writer.close();
        ous.close();

    }

    /**
     * 获取记录总时间
     *
     * @param dirpath
     * @return
     */
    public static long getRecordDuration(String dirpath) {
        File soundfile = new File(dirpath, Const.RELEASE_SOUND_NAME);
        return getSoundFileDuration(soundfile.getAbsolutePath());
    }

    /**
     * 获取音乐文件总时间
     *
     * @param pathf 音乐文件路径
     * @return 总时间
     */
    private static long getSoundFileDuration(String pathf) {
        MediaPlayer mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(pathf);
            mPlayer.prepare();
            long dur = mPlayer.getDuration();
            mPlayer.release();
            return dur;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 是否是讲讲记录文件
     *
     * @param jsonFile
     * @return
     */
    private static boolean isRecord(File jsonFile) {
        return !jsonFile.getName().contains(Const.UN_RECORD_FILE_FLAG);
    }

    private static List<Html5ImageInfoBean> getScriptJpgNames(File recordDir) {
        if (recordDir == null)
            return null;
        File infofile = new File(recordDir, Const.INFO_FILE_NAME);
        Properties p = new Properties();
        String thumbnailName = null;
        try {
            FileInputStream ins = new FileInputStream(infofile);
            p.load(ins);
            thumbnailName = p.getProperty(RecordFileAnalytic.THUMBNAIL_NAME);
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String thumb = thumbnailName;
        String[] jpgNames = recordDir.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(Const.IMAGE_SUFFIX) && !filename.equals(thumb);
            }
        });

        if (jpgNames == null || jpgNames.length <= 0)
            return null;
        Log.e(tag, "jpgNames:" + jpgNames.toString());
        List<Html5ImageInfoBean> jpgBeans = new LinkedList<>();
        for (String jpgName : jpgNames) {
            jpgBeans.add(new Html5ImageInfoBean(jpgName, jpgName));
        }
        return jpgBeans;
    }

    /**
     * 将GIF格式图片拷贝到该记录路径下
     *
     * @param gifpath GIF路径
     * @param dir     目录路径
     * @return
     */
    public static String copyGifToRecordDir(String gifpath, String dir) {
        try {
            FileInputStream fis = new FileInputStream(gifpath);
            String imgname = Long.toHexString(System.currentTimeMillis()) + ".gif";
            File img = new File(dir, imgname);
            if (!img.exists())
                img.createNewFile();
            FileOutputStream fos = new FileOutputStream(img);
            byte[] buf = new byte[10240];
            int len = -1;
            while ((len = fis.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fis.close();
            fos.close();
            return imgname;
        } catch (Error err) {
            err.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


//    public static List<File> getAllSoundFilesSortByTime(File dir) {
//        File[] files = dir.listFiles(new FileFilter() {
//            @Override
//            public boolean accept(File pathname) {
//                if (pathname.getName().equals(Const.RELEASE_SOUND_NAME))
//                    return false;
//                if (pathname.getName().endsWith(Const.SOUND_FILE_SUFFIX)
//                        && !pathname.getName().contains(Const.UN_RECORD_FILE_FLAG))
//                    return true;
//                else
//                    return false;
//            }
//        });
//        if (files == null)
//            return null;
//        //
//        List<File> filelist = Arrays.asList(files);
//        Collections.sort(filelist, new Comparator<File>() {
//            @Override
//            public int compare(File lhs, File rhs) {
//                int _index = lhs.getName().lastIndexOf("_");
//                int dotIndex = lhs.getName().lastIndexOf(".");
//                if (_index == -1 || dotIndex == -1)
//                    return 0;
//                int _index2 = rhs.getName().lastIndexOf("_");
//                int dotIndex2 = rhs.getName().lastIndexOf(".");
//                if (_index2 == -1 || dotIndex2 == -1)
//                    return 0;
//                try {
//                    long time1 = Long.valueOf(lhs.getName().substring(_index + 1, dotIndex));
//                    long time2 = Long.valueOf(rhs.getName().substring(_index2 + 1, dotIndex2));
//                    return time1 > time2 ? 1 : time1 < time2 ? -1 : 0;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return 0;
//                }
//            }
//        });
//        return filelist;
//    }
//    public static List<File> getPageSoundFilesSortByTime(File dir, int pageId, final boolean isIncludeUnRecord) {
//        final String page = pageId + "_";
//        File[] files = dir.listFiles(new FileFilter() {
//            @Override
//            public boolean accept(File pathname) {
//                if (pathname.getName().equals(Const.RELEASE_SOUND_NAME))
//                    return false;
//                if (pathname.getAbsolutePath().endsWith(Const.SOUND_FILE_SUFFIX) // .amr
//                        && pathname.getName().startsWith(page)) {
//                    if (isIncludeUnRecord) {
//                        return true;
//                    } else {
//                        if (!pathname.getName().contains(Const.UN_RECORD_FILE_FLAG)) // #
//                            return true;
//                        else
//                            return false;
//                    }
//                } else
//                    return false;
//            }
//        });
//        if (files == null)
//            return null;
//        //
//        List<File> filelist = Arrays.asList(files);
//        Collections.sort(filelist, new Comparator<File>() {
//            @Override
//            public int compare(File lhs, File rhs) {
//                int _index = lhs.getName().lastIndexOf("_");
//                int dotIndex = lhs.getName().lastIndexOf(".");
//                if (_index == -1 || dotIndex == -1)
//                    return 0;
//                int _index2 = rhs.getName().lastIndexOf("_");
//                int dotIndex2 = rhs.getName().lastIndexOf(".");
//                if (_index2 == -1 || dotIndex2 == -1)
//                    return 0;
//                try {
//                    long time1 = Long.valueOf(lhs.getName().substring(_index + 1, dotIndex));
//                    long time2 = Long.valueOf(rhs.getName().substring(_index2 + 1, dotIndex2));
//                    return time1 > time2 ? 1 : time1 < time2 ? -1 : 0;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return 0;
//                }
//            }
//        });
//        return filelist;
//    }
//    /**
//     * 获取脚本声音
//     *
//     * @param recordDir
//     * @param pageId
//     * @return
//     * @throws Exception
//     */
//    private static Html5SoundInfoBean getScriptSound(File recordDir, int pageId) throws Exception {
//        Log.e("", "获取脚本声音被调用");
//        // 创建 sounlist.txt.
//        File soundlistTxt = new File(recordDir, Const.RECORD_SOUNDLIST_TEXT);
//        if (soundlistTxt.exists())
//            soundlistTxt.delete();
//        soundlistTxt.createNewFile();
//        List<File> soundFiles = null;
//        if (pageId < 0)
//            soundFiles = RecordFileUtils.getAllSoundFilesSortByTime(recordDir);
//        else
//            soundFiles = RecordFileUtils.getPageSoundFilesSortByTime(recordDir, pageId, false);
//
//        if (soundFiles != null && !soundFiles.isEmpty()) {
//            int size = soundFiles.size();
//            FileOutputStream ous = new FileOutputStream(soundlistTxt, true);
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ous), 1024);
//            for (int i = 0; i < size; i++) {
//                File soundfile = soundFiles.get(i);
//                String line = String.format("file '%s'\n", soundfile.getAbsolutePath());
//                writer.write(line);
//            }// for.
//            writer.close();
//            ous.close();
//            /**
//             * concat use ffmpeg.
//             */
//            String releaseSoundPath = String.format("%s/%s", recordDir.getAbsolutePath(), Const.RELEASE_SOUND_NAME);
//
//            File releaseSound = new File(releaseSoundPath);
//            if (releaseSound.exists())// must delete if exist.
//                releaseSound.delete();
//
////			long begin = System.currentTimeMillis();
////			int ret = FFmpegNative.ffmpegMain(getAudioConcatArgs(soundlistTxt.getAbsolutePath(), releaseSoundPath));
////
////			Log.e(tag, "ffmpeg 返回代码:" + ret + ",消耗时间:%d" + (System.currentTimeMillis() - begin));
////			if (ret != FFmpegNative.FFMPEG_SUCCESS)
////				throw new FFmpegException("ffmpeg fail,return code:" + ret);
//
//        }
//        // type:mp3  path:release.mp3
//        return new Html5SoundInfoBean(Const.REALEASE_SOUND_FILE_TYPE, Const.RELEASE_SOUND_NAME);
//    }
//
//
//    private static boolean isRecord(File jsonFile, int pageId) {
//        if (jsonFile.getName().contains(Const.UN_RECORD_FILE_FLAG))// #
//            return false;
//        String page = pageId + "";
//        if (!jsonFile.getName().startsWith(page))
//            return false;
//        return true;
//    }
//
//    /**
//     * 获取单页记录总时间
//     *
//     * @param pageId
//     * @param dirpath
//     * @param isIncludeUnRecord
//     * @return
//     */
//    public static long getPageRecordDuration(int pageId, String dirpath, final boolean isIncludeUnRecord) {
//        File dir = new File(dirpath);
//        List<File> files = getPageSoundFilesSortByTime(dir, pageId, isIncludeUnRecord);
//        if (files == null || files.isEmpty())
//            return 0;
//        long ret = 0;
//        for (File f : files) {
//            ret += getSoundFileDuration(f.getAbsolutePath());
//        }
//        return ret;
//    }
//
//    /**
//     * 获取音频连接参数
//     *
//     * @param soundlistFilePath
//     * @param releaseSoundPath
//     * @return
//     */
//    private static String[] getAudioConcatArgs(String soundlistFilePath, String releaseSoundPath) {
//        String[] arg = new String[]{"ffmpeg", "-f", "concat", "-i", soundlistFilePath, "-acodec", "libmp3lame",
//                "-ar", "22050", "-ab", "64k", "-y", "-threads", "3", "-vol", "1024", "-f", "mp3", releaseSoundPath};
//        return arg;
//    }
//
//    public static List<File> getPageCmdFilesSortByTime(File dir, final int pageId) {
//        final long lastSoundTime = getLastSoundFileTime(dir);
//        File[] files = dir.listFiles(new FileFilter() {
//            @Override
//            public boolean accept(File pathname) {
//                int _index = pathname.getName().indexOf("_");
//                if (_index == -1)
//                    return false;
//                String pagestr = pathname.getName().substring(0, _index);
//                int p = Integer.valueOf(pagestr);
//                if (pathname.getName().equals(Const.RELEASE_JSON_SCRIPT_NAME))
//                    return false;
//                return pathname.getAbsolutePath().endsWith(Const.CMD_FILE_SUFFIX) && p <= pageId
//                        && getRecordFileCreateTime(pathname) <= lastSoundTime;
//
//            }
//        });
//        if (files == null)
//            return null;
//        //
//        List<File> filelist = Arrays.asList(files);
//        Collections.sort(filelist, new Comparator<File>() {
//            @Override
//            public int compare(File lhs, File rhs) {
//                int _index = lhs.getName().lastIndexOf("_");
//                int dotIndex = lhs.getName().lastIndexOf(".");
//                if (_index == -1 || dotIndex == -1)
//                    return 0;
//                int _index2 = rhs.getName().lastIndexOf("_");
//                int dotIndex2 = rhs.getName().lastIndexOf(".");
//                if (_index2 == -1 || dotIndex2 == -1)
//                    return 0;
//                try {
//                    long time1 = Long.valueOf(lhs.getName().substring(_index + 1, dotIndex));
//                    long time2 = Long.valueOf(rhs.getName().substring(_index2 + 1, dotIndex2));
//
//                    return time1 > time2 ? 1 : time1 < time2 ? -1 : 0;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return 0;
//                }
//            }
//        });
//        //
//        return filelist;
//    }

//    private static long getLastSoundFileTime(File dir) {
//        File[] files = dir.listFiles(new FileFilter() {
//            @Override
//            public boolean accept(File pathname) {
//                return pathname.getAbsolutePath().endsWith(Const.SOUND_FILE_SUFFIX);
//            }
//        });
//        if (files == null)
//            return -1;
//        //
//        List<File> filelist = Arrays.asList(files);
//        Collections.sort(filelist, new Comparator<File>() {
//            @Override
//            public int compare(File lhs, File rhs) {
//                int _index = lhs.getName().lastIndexOf("_");
//                int dotIndex = lhs.getName().lastIndexOf(".");
//                if (_index == -1 || dotIndex == -1)
//                    return 0;
//                int _index2 = rhs.getName().lastIndexOf("_");
//                int dotIndex2 = rhs.getName().lastIndexOf(".");
//                if (_index2 == -1 || dotIndex2 == -1)
//                    return 0;
//                try {
//                    long time1 = Long.valueOf(lhs.getName().substring(_index + 1, dotIndex));
//                    long time2 = Long.valueOf(rhs.getName().substring(_index2 + 1, dotIndex2));
//                    return time1 > time2 ? 1 : time1 < time2 ? -1 : 0;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return 0;
//                }
//            }
//        });
//        long lastRecordFileTime = getRecordFileCreateTime(filelist.get(filelist.size() - 1)); // filelist.get(filelist.size()
//        // -
//        // 1).lastModified();
//        return lastRecordFileTime;
//    }
//
//    private static long getRecordFileCreateTime(File f) {
//        int _index = f.getName().lastIndexOf("_");
//        int dotIndex = f.getName().lastIndexOf(".");
//        if (_index == -1 || dotIndex == -1)
//            return 0;
//        try {
//            long time1 = Long.valueOf(f.getName().substring(_index + 1, dotIndex));
//            return time1;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }
//

//    /**
//     * note static var,this is in new process.
//     *
//     * @param dir
//     * @param pageId
//     * @param context
//     * @throws Exception
//     */
//    public static void makeReleaseScript(File dir, int pageId, Context context, ScreenInfoBean info) throws Exception {
//        List<File> jsonFiles = RecordFileUtils.getPageCmdFilesSortByTime(dir, pageId);
//        if (jsonFiles == null || jsonFiles.isEmpty())
//            return;
//        final JsonScriptParser parser = new JsonScriptParser(context);
//
//        // final ScreenInfoBean info = ScreenFitUtil.getCurrentDeviceInfo();
//        List<Html5ImageInfoBean> jpgNames = getScriptJpgNames(dir);
//        if (jpgNames == null) {
//            jpgNames = new LinkedList<>();
//        }
//
//        final Html5SoundInfoBean sound = getScriptSound(dir, pageId);
//
//        File releaseFile = new File(dir, Const.RELEASE_JSON_SCRIPT_NAME);
//        if (releaseFile.exists())
//            releaseFile.delete();
//        releaseFile.createNewFile();
//
//        FileOutputStream ous = new FileOutputStream(releaseFile, true);
//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ous), 10240);
//
//        writer.write("{\"wbEvents\":[");
//        boolean isFirstCmd = true;
//        long delta = 0;
//        List<ICmd> filteredCmds = new ArrayList<>();
//        Map<Integer, Boolean> transformCmd = new HashMap<>();
//
//        for (int i = 0; i < jsonFiles.size(); i++) {
//            File jsonf = jsonFiles.get(i);
//            List<ICmd> pageCmds = parser.jsonFileToCmds(jsonf.getAbsolutePath());
//            if (pageCmds == null || pageCmds.isEmpty())
//                continue;
//            if (!isRecord(jsonf, pageId)) {// not record.
//                for (int j = pageCmds.size() - 1; j >= 0; j--) {
//                    ICmd cmd = pageCmds.get(j);
//                    // filter repeat transform cmds.
//                    if (cmd.getType().equals(ICmd.TYPE_TRANSFORM_SHAPE)) {
//                        TransformShapeData data = (TransformShapeData) cmd.getData();
//                        if (!transformCmd.containsKey(data.getShapeID())) {
//                            transformCmd.put(data.getShapeID(), true);
//
//                            cmd.setTime(ICmd.TIME_DELETE_FLAG);
//
//                            ICmd copy = cmd.copy();
//                            filteredCmds.add(0, copy != null ? copy : cmd);
//                        } else
//                            Log.e(tag, "repeat cmd");
//
//                    } else {// not transform.
//                        cmd.setTime(ICmd.TIME_DELETE_FLAG);
//                        ICmd copy = cmd.copy();
//                        filteredCmds.add(0, copy != null ? copy : cmd);
//                    }
//                }// for cmds end.
//                delta += getSoundFileDuration(jsonf.getAbsolutePath().replaceAll(Const.CMD_FILE_SUFFIX,
//                        Const.SOUND_FILE_SUFFIX));
//                //
//                String line = new Gson().toJson(filteredCmds);
//                line = line.substring(1, line.length() - 1);
//                if (!isFirstCmd)
//                    writer.write(",");
//                else
//                    isFirstCmd = false;
//                writer.write(line);
//                filteredCmds.clear();
//                transformCmd.clear();
//
//            } else {// IS RECORD.
//                for (ICmd cmd : pageCmds) {
//                    cmd.setTime(cmd.getTime() - delta);
//                }
//                String line = new Gson().toJson(pageCmds);
//                line = line.substring(1, line.length() - 1);
//                if (!isFirstCmd)
//                    writer.write(",");
//                else
//                    isFirstCmd = false;
//                writer.write(line);
//
//            }
//        }// for files end.
//        writer.write("]");
//        writer.write(",");
//        writer.write("\"sound\":" + new Gson().toJson(sound));
//        writer.write(",");
//        writer.write("\"resources\":" + new Gson().toJson(jpgNames));
//        writer.write(",");
//        writer.write("\"inputScreenWidth\":" + info.width);
//        writer.write(",");
//        writer.write("\"inputScreenHeight\":" + info.height);
//        writer.write(",");
//        writer.write("\"density\":" + info.density);
//        writer.write(",");
//        writer.write("\"version\":" + Const.SCRIPT_VERSION);
//        writer.write(",");
//        writer.write("\"inputRate\":" + Const.SCRIPT_INPUT_RATE);
//
//        writer.write("}");
//        writer.write("\n");
//
//        writer.close();
//        ous.close();
//    }


//    /**
//     * 将图片拷贝到该记录路径下
//     *
//     * @param bufferScaled
//     * @param dir
//     * @return
//     */
//    public static String copyBitmapToRecordDir(byte[] bufferScaled, String dir) {
//        try {
//            String imgname = Long.toHexString(System.currentTimeMillis()) + ".jpg";
//            File img = new File(dir, imgname);
//            if (!img.exists())
//                img.createNewFile();
//            FileOutputStream fos = new FileOutputStream(img);
//            fos.write(bufferScaled);
//            fos.close();
//            return imgname;
//        } catch (Error err) {
//            err.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


//    /**
//     * 将GIF格式图片拷贝到该记录路径下
//     *
//     * @param bufferScaled
//     * @param dir
//     * @return
//     */
//    public static String copyGifToRecordDir(byte[] bufferScaled, String dir) {
//        try {
//            String imgname = Long.toHexString(System.currentTimeMillis()) + ".gif";
//            File img = new File(dir, imgname);
//            if (!img.exists())
//                img.createNewFile();
//            FileOutputStream fos = new FileOutputStream(img);
//            fos.write(bufferScaled);
//            fos.close();
//            return imgname;
//        } catch (Error err) {
//            err.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

}
