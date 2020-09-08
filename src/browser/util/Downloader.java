package browser.util;


import javafx.scene.control.ProgressBar;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Downloader extends Thread {
    URL url;
    File downloadFile;
    File tmpFile; // 临时文件，标识未下载完成
    ProgressBar bar;
    long fileLength = 0;

    public Downloader(String url) throws Exception {
        this(new URL(url));
    }

    public Downloader(URL url) throws Exception {
        this.url = url;
        downloadFile = new File(FileUtil.getFilePathName(this.url));
        tmpFile = new File(FileUtil.getFilePathName(this.url) + ".tmp");
    }

    public Downloader(URL url, String savePath, ProgressBar progressBar) throws IOException {
        this.url = url;
        downloadFile = new File(savePath);
        tmpFile = new File(savePath + ".tmp");
        bar = progressBar;

        HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
        fileLength = urlCon.getContentLengthLong();
        urlCon.disconnect();
    }

    /* 断点续传 */
    public void downloadBreakPoint() throws Exception {
        long position = 0; // 断点位置
        if (tmpFile.exists()) {
            System.out.println("检测到临时文件，开始断点续传......");
            position = getPosition();
            System.out.println("开始位置：" + position + "B  " + position / (1024.0 * 1024.0) + "MB");
        }
        HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
        urlCon.setConnectTimeout(6000);
        urlCon.setReadTimeout(6000);
        FileUtil.setHttpDownloadRange(urlCon, position);
        DataInputStream in = new DataInputStream(urlCon.getInputStream());
        RandomAccessFile file = new RandomAccessFile(downloadFile, "rw");
        System.out.println("文件大小: " + fileLength);
        if (fileLength == position) {
            deleteTmpFile();
            return;
        }
        writeFile(in, file, position);
        file.close();
        in.close();
        System.out.println("下载完成");
    }

    /* 多线程断点续传 */
    public void multiDownload(int threadNum) throws Exception {
        bar.setAccessibleHelp("多线程下载中");
        long position = 0; // 断点位置
        if (tmpFile.exists()) {
            System.out.println("检测到临时文件，开始断点续传......");
            position = getPosition();
            System.out.println("开始位置：" + position + "B  " + position / (1024.0 * 1024.0) + "MB");
        }
        System.out.println("文件大小: " + fileLength);
        long remainSize = fileLength - position, // 剩余大小
                blockSize = (fileLength - position) / threadNum; // 每个线程下载的大小，注意最后一个要特殊处理
        for (int i = 0; i < threadNum; i++) {
            long finalPosition = position;
            long begin = finalPosition + blockSize * i, end = 0;
            if (i != threadNum - 1) {
                end = finalPosition + blockSize * (i + 1) - 1;
            } else {
                end = fileLength;
            }
            long finalEnd = end;
            int finalI = i;
            new Thread(() -> {
                try {
                    HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                    urlCon.setConnectTimeout(6000);
                    urlCon.setReadTimeout(6000);
                    FileUtil.setHttpDownloadRange(urlCon, begin, finalEnd);
                    DataInputStream in = new DataInputStream(urlCon.getInputStream());
                    RandomAccessFile file = null;
                    file = new RandomAccessFile(downloadFile, "rw");
                    if (fileLength == finalPosition) {
                        deleteTmpFile();
                        return;
                    }
                    writeFile(in, file, begin);
                    file.close();
                    in.close();
                    System.out.println("线程" + finalI + "下载完成");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    @Override
    public void run() {
        try {
            this.multiDownload(8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 断点续传写入 */
    private void writeFile(DataInputStream in, RandomAccessFile file, long position) throws IOException {
        tmpFile.createNewFile();
        file.seek(position);

        byte[] buffer = new byte[4096];
        int size;
        while ((size = in.read(buffer)) > 0) {
            bar.setProgress(position / (double) fileLength);
            file.write(buffer, 0, size);
            position += size;
//            this.savePosition(position);
        }
        in.close();
        deleteTmpFile();
    }

    /* 获得断点位置 */
    long getPosition() throws Exception {
        return downloadFile.length();
    }


    /* 删除临时文件 */
    void deleteTmpFile() {
        if (tmpFile.exists())
            tmpFile.delete();
    }

    public static void main(String[] args) throws Exception {
        URL url = new URL("https://dldir1.qq.com/weixin/Windows/WeChatSetup.exe");
        Downloader downloader = new Downloader(url);
        downloader.downloadBreakPoint();
//        System.out.println(Utils.getFileFormat(url));
    }
}
