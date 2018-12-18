package com.speaktool.impl.player;

import android.content.Context;
import android.graphics.Point;

import com.google.gson.Gson;
import com.speaktool.bean.ChangeEditData;
import com.speaktool.bean.ChangeImageData;
import com.speaktool.bean.MoveData;
import com.speaktool.bean.ScaleData;
import com.speaktool.bean.ScreenInfoBean;
import com.speaktool.impl.cmd.ICmd;
import com.speaktool.impl.cmd.clear.CmdClearPage;
import com.speaktool.impl.cmd.copy.CmdCopyPage;
import com.speaktool.impl.cmd.create.CmdActivePage;
import com.speaktool.impl.cmd.create.CmdCreateEdit;
import com.speaktool.impl.cmd.create.CmdCreateImage;
import com.speaktool.impl.cmd.create.CmdCreatePage;
import com.speaktool.impl.cmd.create.CmdCreatePen;
import com.speaktool.impl.cmd.delete.CmdDeleteEdit;
import com.speaktool.impl.cmd.delete.CmdDeletePage;
import com.speaktool.impl.cmd.transform.CmdChangeEditNoSeq;
import com.speaktool.impl.cmd.transform.CmdChangeImageNoSeq;
import com.speaktool.impl.cmd.transform.CmdChangePageBackground;
import com.speaktool.impl.cmd.transform.CmdMoveEdit;
import com.speaktool.impl.cmd.transform.CmdMoveImage;
import com.speaktool.impl.cmd.transform.CmdScaleImage;
import com.speaktool.utils.DisplayUtil;
import com.speaktool.utils.FileUtils;
import com.speaktool.utils.ScreenFitUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON 脚本分析器
 *
 * @author shaoshuai
 */
@SuppressWarnings("rawtypes")
public class JsonScriptParser {
    private Context mContext;

    public JsonScriptParser(Context ctx) {
        mContext = ctx;
    }

    public JsonScriptParser(Context ctx, File screenInfoFile) {
        mContext = ctx;
        try {
            initScreenInfo(screenInfoFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initScreenInfo(File f) throws IOException, JSONException {
        String json = FileUtils.readFile(f);
        JSONObject script = new JSONObject(json);

        ScreenInfoBean inputScreenInfo = new ScreenInfoBean(
                script.getInt("inputScreenWidth"),
                script.getInt("inputScreenHeight"),
                script.getInt("density")
        );
        ScreenFitUtil.setInputDeviceInfo(inputScreenInfo);
        //
        float inputRatioHW = ((float) inputScreenInfo.height) / inputScreenInfo.width;
        Point screenSize = DisplayUtil.getScreenSize(mContext);
        Point size = ScreenFitUtil.getKeepRatioScaledSize(inputRatioHW, screenSize.x, screenSize.y);

        ScreenInfoBean currentScreenInfo = new ScreenInfoBean(size.x, size.y, DisplayUtil.getScreenDensity(mContext));
        ScreenFitUtil.setCurrentDeviceInfo(currentScreenInfo);
    }

    /**
     * 解析JSON——>操作命令集合
     *
     * @param json
     * @return
     */
    public List<ICmd> jsonToCmds(String json) {
        try {
            JSONArray jarr = new JSONObject(json).getJSONArray("wbEvents");
            List<ICmd> cmds = new ArrayList<ICmd>();
            //
            JSONObject cmdJson;
            String cmdType;
            for (int i = 0; i < jarr.length(); i++) {
                cmdJson = jarr.getJSONObject(i);
                cmdType = cmdJson.getString("type");
                if (cmdType.equals(ICmd.TYPE_CREATE_SHAPE)) {
                    JSONObject data = cmdJson.getJSONObject("data");
                    String datatype = data.getString("type");
                    float fx = ScreenFitUtil.getFactorX();
                    float fy = ScreenFitUtil.getFactorY();
                    float fd = ScreenFitUtil.getFactorDensity();
                    if (datatype.equals("text")) {
                        CmdCreateEdit cmd = new Gson().fromJson(cmdJson.toString(), CmdCreateEdit.class);
                        cmd.getData().bianHuan(fx, fy);
                        cmds.add(cmd);
                    } else if (datatype.equals("image")) {
                        CmdCreateImage cmd = new Gson().fromJson(cmdJson.toString(), CmdCreateImage.class);
                        cmd.getData().getPosition().bianHuan(fx, fy);
                        cmds.add(cmd);
                    } else if (datatype.equals("pen") || datatype.equals("eraser")) {// create pen.
                        CmdCreatePen cmd = new Gson().fromJson(cmdJson.toString(), CmdCreatePen.class);
                        cmd.getData().bianHuan(fx, fy, fd);
                        cmds.add(cmd);
                    }
                } else if (cmdType.equals(ICmd.TYPE_DELETE_SHAPE)) {
                    CmdDeleteEdit cmd = new Gson().fromJson(cmdJson.toString(), CmdDeleteEdit.class);
                    cmds.add(cmd);
                } else if (cmdType.equals(ICmd.TYPE_TRANSFORM_SHAPE)) {
                    JSONObject data = cmdJson.getJSONObject("data");
                    String datatype = data.getString("type");
                    ICmd cmd;
                    if (datatype.equals("text")) {
                        cmd = textTransform(data, cmdJson);
                        cmds.add(cmd);
                    } else if (datatype.equals("image")) {
                        cmd = imageTransform(data, cmdJson);
                        cmds.add(cmd);
                    } else {// pen.
                        // cmd=penTransform(data,cmdJson);
                        // cmds.add(cmd);
                    }
                } else if (cmdType.equals(ICmd.TYPE_CREATE_PAGE)) {
                    CmdCreatePage cmd = new Gson().fromJson(cmdJson.toString(), CmdCreatePage.class);
                    cmds.add(cmd);
                } else if (cmdType.equals(ICmd.TYPE_SET_ACTIVE_PAGE)) {
                    CmdActivePage cmd = new Gson().fromJson(cmdJson.toString(), CmdActivePage.class);
                    cmds.add(cmd);
                } else if (cmdType.equals(ICmd.TYPE_COPY_PAGE)) {
                    CmdCopyPage cmd = new Gson().fromJson(cmdJson.toString(), CmdCopyPage.class);
                    cmds.add(cmd);
                } else if (cmdType.equals(ICmd.TYPE_CLEAR_PAGE)) {
                    CmdClearPage cmd = new Gson().fromJson(cmdJson.toString(), CmdClearPage.class);
                    cmds.add(cmd);
                } else if (cmdType.equals(ICmd.TYPE_DELETE_PAGE)) {
                    CmdDeletePage cmd = new Gson().fromJson(cmdJson.toString(), CmdDeletePage.class);
                    cmds.add(cmd);
                } else if (cmdType.equals(ICmd.TYPE_CHANGE_PAGE_BACKGROUND)) {
                    CmdChangePageBackground cmd = new Gson().fromJson(cmdJson.toString(), CmdChangePageBackground.class);
                    cmds.add(cmd);
                }
            }
            return cmds;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ICmd imageTransform(JSONObject data, JSONObject cmdJson) {
        float fx = ScreenFitUtil.getFactorX();
        float fy = ScreenFitUtil.getFactorY();
        try {
            if (data.has("sequence")) {
                JSONObject seqFirst = data.getJSONArray("sequence").getJSONObject(0);
                if (!seqFirst.has("s")) {// move.
                    CmdMoveImage cmd = new Gson().fromJson(cmdJson.toString(), CmdMoveImage.class);
                    ChangeImageData<MoveData> cdata = cmd.getData();
                    cdata.getPosition().bianHuan(fx, fy);
                    for (MoveData mv : cdata.getSequence()) {
                        mv.bianHuanXY(fx, fy);
                    }
                    return cmd;

                } else {// scale or rotation.
                    CmdScaleImage cmd = new Gson().fromJson(cmdJson.toString(), CmdScaleImage.class);
                    ChangeImageData<ScaleData> cdata = cmd.getData();
                    cdata.getPosition().bianHuan(fx, fy);
                    for (ScaleData sc : cdata.getSequence()) {
                        sc.bianHuanXY(fx, fy);
                    }
                    return cmd;
                }
            } else {// no seq.
                CmdChangeImageNoSeq cmd = new Gson().fromJson(cmdJson.toString(), CmdChangeImageNoSeq.class);
                cmd.getData().getPosition().bianHuan(fx, fy);
                return cmd;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ICmd textTransform(JSONObject data, JSONObject cmdJson) {
        float fx = ScreenFitUtil.getFactorX();
        float fy = ScreenFitUtil.getFactorY();
        try {
            if (data.has("sequence")) {
                JSONObject seqFirst = data.getJSONArray("sequence").getJSONObject(0);
                if (seqFirst.has("x")) {// move.
                    CmdMoveEdit cmd = new Gson().fromJson(cmdJson.toString(), CmdMoveEdit.class);
                    ChangeEditData<MoveData> cdata = cmd.getData();
                    cdata.bianHuan(fx, fy);
                    for (MoveData mv : cdata.getSequence()) {
                        mv.bianHuanXY(fx, fy);
                    }
                    return cmd;
                } else if (seqFirst.has("r")) {// rotation.

                } else {// s->scale.

                }
            } else {// no seq.
                CmdChangeEditNoSeq cmd = new Gson().fromJson(cmdJson.toString(), CmdChangeEditNoSeq.class);
                cmd.getData().bianHuan(fx, fy);
                return cmd;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ICmd> jsonFileToCmds(String jsonFilePath) {
        try {
            File f = new File(jsonFilePath);
            if (!f.exists())
                return null;
            if (!f.isFile())
                return null;
            List<ICmd> totalCmds = new ArrayList<ICmd>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String line;
            while ((line = reader.readLine()) != null) {
                totalCmds.addAll(jsonToCmds(line));
            }
            reader.close();
            return totalCmds;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    /**
//     * 解析JSON文件
//     *
//     * @param jsonFilePath JSON文件路径
//     * @param startLine    开始行
//     * @param readlines    读取行
//     * @return 操作命令集合
//     */
//    public List<ICmd> jsonFileToCmds(String jsonFilePath, int startLine, int readlines) {
//        try {
//            File f = new File(jsonFilePath);
//            if (!f.exists())
//                return null;
//            if (!f.isFile())
//                return null;
//            List<ICmd> totalCmds = new ArrayList<ICmd>();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
//            for (int i = 0; i < startLine; i++)
//                reader.readLine();
//            String line = null;
//            while ((line = reader.readLine()) != null && readlines > 0) {
//                totalCmds.addAll(jsonToCmds(line));
//                readlines -= 1;
//            }
//            // if (line == null)
//            // readflagRet.isEnd = true;
//            // else
//            // readflagRet.isEnd = false;
//            reader.close();
//            return totalCmds;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

}
