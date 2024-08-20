package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.Entity.IImageEntity;
import com.exam.hotelmanage1.Entity.RoomImgEntity;
import lombok.extern.java.Log;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Log
public class FTPService {

    public String uploadFile(String originalFileName, byte[] fileData) throws Exception {
        String server = "woori-prn.iptime.org";
        int port = 21;
        String username = "woori";
        String password = "woori6655522";
        String remoteFilepath = "/project/data";
        System.out.println("FTP서버 접속시도");
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            ftpClient.login(username, password);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            UUID uuid = UUID.randomUUID();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String savedFileName = uuid.toString() + extension;

            // 파일을 업로드할 경로로 이동
            ftpClient.changeWorkingDirectory(remoteFilepath);

            // 파일 데이터를 ByteArrayInputStream으로 변환
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData);

            // 파일을 서버로 전송
            boolean uploaded = ftpClient.storeFile(savedFileName, inputStream);
            inputStream.close();

            if (uploaded) {
                System.out.println("업로드 성공");
                return savedFileName; // 파일 이름 반환
            } else {
                System.out.println("업로드 실패");
                throw new IOException("Failed to upload file to FTP server.");
            }
        } finally {
            System.out.println("업로드 실패2");
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public <T extends IImageEntity> void deleteFiles(List<T> imageEntities) throws Exception {
        String server = "woori-prn.iptime.org";
        int port = 21;
        String username = "woori";
        String password = "woori6655522";
        String remoteFilepath = "/project/data";
        System.out.println("FTP서버 접속 시도");
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            ftpClient.login(username, password);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // imageEntities 리스트 안의 각 IImageEntity의 이미지 이름을 사용해 파일 삭제
            for (T imageEntity : imageEntities) {
                String fileName = imageEntity.getImgName();
                boolean deleted = ftpClient.deleteFile(remoteFilepath + "/" + fileName);

                if (deleted) {
                    System.out.println(fileName + " 삭제 성공");
                } else {
                    System.out.println(fileName + " 삭제 실패");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}