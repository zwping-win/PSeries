package win.zwping.pseries;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main2Activity extends AppCompatActivity {

    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ZXingLibrary.initDisplayOpinion(this);
        setContentView(R.layout.activity_main2);

        img = (ImageView) findViewById(R.id.img);

        saveInvitationImg("http://139.159.242.210/note-web-boss/share/redrshareinfo.dtzn?inviteid=1505382269662&uid=100019");
    }

    private boolean saveInvitationImg(String url) {
        try {
            Bitmap bg = BitmapFactory.decodeResource(getResources(), R.mipmap.bg2);
            Bitmap qr_bg = BitmapFactory.decodeResource(getResources(), R.mipmap.qr_bg);
            Bitmap white_bg = BitmapFactory.decodeResource(getResources(), R.mipmap.white_bg);
            Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
            Bitmap marker = BitmapFactory.decodeResource(getResources(), R.mipmap.marker_01);
            Bitmap adverLang = BitmapFactory.decodeResource(getResources(), R.mipmap.marker_02);

            //二维码
            Bitmap qrBmp = CodeUtils.createImage(url, white_bg.getWidth() - 20, white_bg.getHeight() - 20, null); //比qr_bg小于50
            if (null == qrBmp) {
                //LogUtil.i("生成二维码失败");
                close(bg, qr_bg, white_bg, logo, marker, adverLang);
                return false;
            }
            //白底二维码
            Bitmap whiteBgBmp = composeBitmap(white_bg, qrBmp, 10, 10);
            //加白底的虚边的二维码
            Bitmap qrBgBmp = composeBitmap(qr_bg, whiteBgBmp, (qr_bg.getWidth() - whiteBgBmp.getWidth()) / 2, (qr_bg.getHeight() - whiteBgBmp.getHeight()) / 2);

            //生成整图
            int w = bg.getWidth();
            int h = bg.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bg, 0, 0, null);
            canvas.drawBitmap(logo, (w - logo.getWidth()) / 2, (94f / 700f) * h, paint); //logo
            canvas.drawBitmap(adverLang, (w - adverLang.getWidth()) / 2, (254f / 700f) * h, paint); //advertise language
            canvas.drawBitmap(qrBgBmp, (w - qrBgBmp.getWidth()) / 2, (330f / 700f) * h, paint); //logo
            canvas.drawBitmap(marker, (w - marker.getWidth()) / 2, (630f / 700f) * h, paint); //marker

            img.setImageBitmap(bitmap);
            // TODO: 2017/9/14 0014 UNAME
            if (save(bitmap, "" + System.currentTimeMillis() + ".png")) {
                close(bg, qr_bg, white_bg, logo, marker, adverLang, qrBmp, whiteBgBmp, qrBgBmp, bitmap);
                return true; //保存成功
            } else {
                close(bg, qr_bg, white_bg, logo, marker, adverLang, qrBmp, whiteBgBmp, qrBgBmp, bitmap);
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 合成两张bitmap
     *
     * @param dst
     * @param src
     * @param left
     * @param top
     * @return
     */
    private Bitmap composeBitmap(Bitmap dst, Bitmap src, float left, float top) {
        Bitmap bitmap = Bitmap.createBitmap(dst.getWidth(), dst.getHeight(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); //https://camo.githubusercontent.com/8f4975589d99f7d31ebc8357da6e32dadcbca109/687474703a2f2f7777332e73696e61696d672e636e2f6d773639302f3732353839613039677731656d796c7333666a67796a3230666b3068736e30692e6a7067

        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(dst, 0, 0, null);
        canvas.drawBitmap(src, left, top, paint);
        return bitmap;
    }

    private boolean save(Bitmap bitmap, String fileName) {
        // TODO: 2017/9/14 0014 获取读写sd卡权限
        if (isSDCardEnable()) {
            String dir = getSDCardPath() + "爱分钱/";
            File file = new File(dir + fileName);
            if (!createOrExistsDir(new File(dir))) {
                //LogUtil.i("创建文件夹失败");
                return false;
            }
            if (!createFileByDeleteOldFile(file)) {
                //LogUtil.i("创建文件失败");
                return false;
            }
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
                out.flush();
                close(out);
                noticePicUp(this, file);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        } else {
            //LogUtil.i("SD卡不可用");
            return false;
        }
    }

    /**
     * 通知相册更新
     *
     * @param context
     * @param file    http://www.jianshu.com/p/037a82db102c
     */
    public static void noticePicUp(Context context, File file) {
        // 插入file数据到相册
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        // 通知相册更新
        context.sendBroadcast(new Intent("com.android.camera.NEW_PICTURE", uri));
    }

    /**
     * 判断SD卡是否可用
     *
     * @return true : 可用<br>false : 不可用
     */
    public static boolean isSDCardEnable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取SD卡路径
     * <p>先用shell，shell失败再普通方法获取，一般是/storage/emulated/0/</p>
     *
     * @return SD卡路径
     */
    public static String getSDCardPath() {
        if (!isSDCardEnable()) return null;
        String cmd = "cat/proc/mounts";
        Runtime run = Runtime.getRuntime();
        BufferedReader bufferedReader = null;
        try {
            Process p = run.exec(cmd);
            bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream())));
            String lineStr;
            while ((lineStr = bufferedReader.readLine()) != null) {
                if (lineStr.contains("sdcard") && lineStr.contains(".android_secure")) {
                    String[] strArray = lineStr.split(" ");
                    if (strArray.length >= 5) {
                        return strArray[1].replace("/.android_secure", "") + File.separator;
                    }
                }
                if (p.waitFor() != 0 && p.exitValue() == 1) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(bufferedReader);
        }
        return Environment.getExternalStorageDirectory().getPath() + File.separator;
    }

    public static boolean createOrExistsDir(File file) {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 判断文件是否存在，存在则在创建之前删除
     *
     * @param file 文件
     * @return {@code true}: 创建成功<br>{@code false}: 创建失败
     */
    public static boolean createFileByDeleteOldFile(File file) {
        if (file == null) return false;
        // 文件存在并且删除失败返回false
        if (file.exists() && file.isFile() && !file.delete()) return false;
        // 创建目录失败返回false
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void close(Closeable... closeables) {
        if (closeables == null) return;
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void close(Bitmap... bitmaps) {
        if (bitmaps == null) return;
        for (Bitmap closeable : bitmaps) {
            if (closeable != null && !closeable.isRecycled()) {
                try {
                    closeable.recycle();
                    closeable = null;
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }
    }

}
