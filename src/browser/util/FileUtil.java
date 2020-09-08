package browser.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class FileUtil {
    // 下载文件夹 - 系统下载

    /**
     * 保存为文件
     */
    public static void saveFile(String filePath, String content) {
        try {
            FileWriter fw = new FileWriter(filePath);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 获得文件后缀名 */
    public static String getFileSuffix(URL url) throws IOException {
        String type = HttpURLConnection.guessContentTypeFromName(url.toString());
        // 从url名中猜测
        if (type == null) {
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
            type = HttpURLConnection.guessContentTypeFromStream(urlCon.getInputStream());
            // 从输入流猜测
//            System.out.println("type: " + type);
            if (type == null) {
                urlCon = (HttpURLConnection) url.openConnection();
                urlCon.setRequestMethod("HEAD");
                if (urlCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    urlCon.setRequestMethod("GET");
                }
                type = urlCon.getContentType();
            }
            return MIMEtoSuffix.get(type);
        }
        // 不为空说明已经url中含有后缀，不需要再添加
        return "";
    }

    /* 生成文件路径（当前目录）*/
    public static String getFilePathName(URL url) throws IOException {
        return System.getProperty("user.dir") + "/" + // 当前目录
                url.getPath().replaceAll("[/\\\\]", "-") + // url文件名
                FileUtil.getFileSuffix(url);
    }

    public static String getFilename(URL url) {
        return url.getPath().replaceAll("[/\\\\]", "-");
    }

    public static void setHttpDownloadRange(HttpURLConnection urlCon, long begin) {
        urlCon.setRequestProperty("RANGE", "bytes=" + begin + "-");
    }

    public static void setHttpDownloadRange(HttpURLConnection urlCon, long begin, long end) {
        urlCon.setRequestProperty("RANGE", "bytes=" + begin + "-" + end);
    }

    public static String getFileSize(File file) {
        DecimalFormat df = new DecimalFormat("0.0000");
        return df.format(file.length() / (1024.0 * 1024.0)) + "MB";
    }

    /* MIME类型与文件扩展名Map */
    static final Map<String, String> MIMEtoSuffix = new HashMap<>() {
        {
            put("application/postscript", ".ai");
            put("application/octet-stream", ".exe");
            put("application/vnd.ms-word", ".doc");
            put("application/vnd.ms-excel", ".xls");
            put("application/vnd.ms-powerpoint", ".ppt");
            put("application/pdf", ".pdf");
            put("application/xml", ".xml");
            put("application/vnd.oasis.opendocument.text", ".odt");
            put("application/x-shockwave-flash", ".swf");
            put("application/x-gzip", ".gz");
            put("application/x-bzip2", ".bz");
            put("application/zip", ".zip");
            put("application/x-rar", ".rar");
            put("application/x-tar", ".tar");
            put("application/x-7z-compressed", ".7z");
            put("application/json", ".json");
            put("text/plain", ".txt");
            put("text/x-php", ".php");
            put("text/html", ".html");
            put("text/javascript", ".js");
            put("text/css", ".css");
            put("text/rtf", ".rtf");
            put("text/rtfd", ".rtfd");
            put("text/x-python", ".py");
            put("text/x-java-source", ".java");
            put("text/x-ruby", ".rb");
            put("text/x-shellscript", ".sh");
            put("text/x-perl", ".pl");
            put("text/x-sql", ".sql");
            put("image/x-ms-bmp", ".bmp");
            put("image/jpeg", ".jpeg");
            put("image/gif", ".gif");
            put("image/png", ".png");
            put("image/tiff", ".tif");
            put("image/x-targa", ".tga");
            put("image/vnd.adobe.photoshop", ".psd");
            put("audio/mpeg", ".mp3");
            put("audio/midi", ".mid");
            put("audio/ogg", ".ogg");
            put("audio/mp4", ".mp4a");
            put("audio/wav", ".wav");
            put("audio/x-ms-wma", ".wma");
            put("video/x-msvideo", ".avi");
            put("video/x-dv", ".dv");
            put("video/mp4", ".mp4");
            put("video/mpeg", ".mpeg");
            put("video/quicktime", ".mov");
            put("video/x-ms-wmv", ".wm");
            put("video/x-flv", ".flv");
            put("video/x-matroska", ".mkv");
        }
    };
}
