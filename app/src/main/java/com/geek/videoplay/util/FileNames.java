package com.geek.videoplay.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 创建图片、音频、视频的文件名称
 * 
 * @author chenhao
 * 
 */
public class FileNames {
	private String imageName;
	private String voiceName;
	private String videoName;
	private SimpleDateFormat timeStampFormat;
	private String fileName;

	public FileNames() {
		timeStampFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
	}

	public String getImageName() {
		fileName = timeStampFormat.format(new Date());
		imageName = "IMG_" + fileName + ".jpg";
		return imageName;
	}

	public String getVoiceName() {
		fileName = timeStampFormat.format(new Date());
		voiceName = "VOICE_" + fileName + ".mp3";
		return voiceName;
	}

	public String getVideoName() {
		fileName = timeStampFormat.format(new Date());
		videoName = "VIDEO_" + fileName + ".mp4";
		return videoName;
	}

}
