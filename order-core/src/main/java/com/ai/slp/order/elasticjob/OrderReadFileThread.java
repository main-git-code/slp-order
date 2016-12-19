package com.ai.slp.order.elasticjob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ai.opt.sdk.util.DateUtil;
import com.ai.opt.sdk.util.StringUtil;
import com.ai.slp.order.util.PropertiesUtil;
import com.ai.slp.order.util.SftpUtil;
import com.ai.slp.order.util.ValidateChkUtil;
import com.alibaba.fastjson.JSON;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

public class OrderReadFileThread extends Thread {

	private static final Log LOG = LogFactory.getLog(OrderReadFileThread.class);

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public BlockingQueue<String[]> ordOrderQueue;

	String ip = PropertiesUtil.getStringByKey("ftp.ip"); // 服务器IP地址
	String userName = PropertiesUtil.getStringByKey("ftp.userName"); // 用户名
	String userPwd = PropertiesUtil.getStringByKey("ftp.userPwd"); // 密码
	int port = Integer.parseInt(PropertiesUtil.getStringByKey("ftp.port")); // 端口号
	String path = PropertiesUtil.getStringByKey("ftp.path"); // 读取文件的存放目录
	String localpath = PropertiesUtil.getStringByKey("ftp.localpath");// 本地存在的文件路径

	public OrderReadFileThread(BlockingQueue<String[]> ordOrderQueue) {
		this.ordOrderQueue = ordOrderQueue;
	}

	public void run() {
		LOG.error("开始获取订单信息ftp文件：" + DateUtil.getSysDate());
		ChannelSftp sftp = SftpUtil.connect(ip, port, userName, userPwd);
		LOG.error("+++++++++++++++++连接订单商品ftp服务器成功");
		List<String> nameList = new ArrayList<>();
		try {
			LOG.error("开始获取订单信息文件列表");
			nameList = getFileName(path, sftp);
			LOG.error("++++++++++++++++++++订单信息文件列表" + JSON.toJSONString(nameList));
		} catch (SftpException e1) {
			e1.printStackTrace();
		}
		for (String fileName : nameList) {
			String chkName = fileName.substring(0, 23) + ".chk";
			try {
				ValidateChkUtil util = new ValidateChkUtil();
				String errCode = util.validateChk(path, localpath, fileName, chkName, sftp);
				if (!StringUtil.isBlank(errCode)) {
					LOG.error("校验订单信息文件失败,校验码:" + errCode.toString());
					String errCodeName = chkName.substring(0, chkName.lastIndexOf(".")) + ".rpt";
					String localPath = localpath + "/rpt";
					File file = new File(localPath);
					if (!file.exists()) {
						file.mkdirs();
					}
					File rptFile = new File(localPath + "/" + errCodeName);
					if (!rptFile.exists()) {
						rptFile.createNewFile();
					}
					FileWriter fw = new FileWriter(rptFile);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(fileName + "\n");
					bw.write(errCode.toString() + "\n");
					bw.flush();
					bw.close();
					fw.close();
					InputStream is = new FileInputStream(rptFile);
					// 移动rpt文件
					SftpUtil.uploadIs(path + "/sapa/rpt", errCodeName, is, sftp);
					if (!errCode.toString().equals("09")) {
						// 移动chk文件
						InputStream chkIs = SftpUtil.download(path, chkName, localPath, sftp);
						SftpUtil.uploadIs(path + "/sapa/err", chkName, chkIs, sftp);
						SftpUtil.delete(path, chkName, sftp);
						deleteFile(localPath+"/"+errCodeName);
						deleteFile(localpath+"/bak/"+chkName);
					}
					continue;
					// 推到ftp上
				} else {
					LOG.error("++++++++++++订单信息校验成功" + chkName);
					InputStream is = SftpUtil.download(path, chkName, localpath+"/bak", sftp);
					SftpUtil.delete(path, chkName, sftp);
					SftpUtil.uploadIs(path + "/sapa/chk", chkName, is, sftp);
					deleteFile(localpath+"/bak/"+chkName);
					readOrderFile(fileName, sftp);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		LOG.error("获取订单信息ftp文件结束：" + DateUtil.getSysDate());
		SftpUtil.disconnect(sftp);
	}

	public void readOrderFile(String fileName, ChannelSftp sftp) throws ParseException {
		InputStream ins = null;
		try {
			// 从服务器上读取指定的文件
			LOG.error("开始读取订单信息文件：" + fileName);
			ins = SftpUtil.download(path, fileName, localpath+"/bak", sftp);
			//ins = sftp.get(path + "/" + fileName);
			if (ins != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(ins, "gbk"));
				String line;
				while ((line = reader.readLine()) != null) {
					try {
						String[] datTemp = line.split("\\t");
						if (datTemp.length != 32)
							continue;
						ordOrderQueue.put(datTemp);
						LOG.error("订单信息订单Id信息：" + datTemp[0]);
					} catch (Exception e) {
						LOG.error("读取订单信息文件失败：" + e.getMessage());
					}

				}
				reader.close();
				if (ins != null) {
					ins.close();
				}
				// SftpUtil.delete(path, fileName, sftp);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			deleteFile(localpath +"/" +fileName);
		}
	}

	public List getFileName(String path, ChannelSftp sftp) throws SftpException {
		List<String> fileList = SftpUtil.listFiles(path, sftp);
		LOG.error("++++++++++获取ftp订单信息文件列表,文件列表如下"+JSON.toJSONString(fileList));
		List<String> nameList = new ArrayList<>();
		for (String string : fileList) {
			String date = sdf.format(DateUtil.getSysDate());
			if (string.length() >= 20) {
				if ((date + "_" + "omsa01001").equals(string.substring(2, 20)) && string.endsWith(".dat")) {
					nameList.add(string);
				}
			}
		}
		LOG.error("++++++++++获取订单信息文件列表成功,文件列表如下"+JSON.toJSONString(nameList));
		return nameList;
	}

	public void deleteFile(String sPath) {
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
		}
	}

}